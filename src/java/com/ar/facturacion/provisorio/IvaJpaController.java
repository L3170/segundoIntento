/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.provisorio;

import com.ar.facturacion.model.Iva;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ar.facturacion.model.Ivatipo;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import com.ar.facturacion.provisorio.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leonardo
 */
public class IvaJpaController implements Serializable {

    public IvaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Iva iva) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ivatipo ivatipo = iva.getIvatipo();
            if (ivatipo != null) {
                ivatipo = em.getReference(ivatipo.getClass(), ivatipo.getIdIva());
                iva.setIvatipo(ivatipo);
            }
            Producto producto = iva.getProducto();
            if (producto != null) {
                producto = em.getReference(producto.getClass(), producto.getIdProducto());
                iva.setProducto(producto);
            }
            em.persist(iva);
            if (ivatipo != null) {
                ivatipo.getIvas().add(iva);
                ivatipo = em.merge(ivatipo);
            }
            if (producto != null) {
                producto.getIvas().add(iva);
                producto = em.merge(producto);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findIva(iva.getIdIva()) != null) {
                throw new PreexistingEntityException("Iva " + iva + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Iva iva) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Iva persistentIva = em.find(Iva.class, iva.getIdIva());
            Ivatipo ivatipoOld = persistentIva.getIvatipo();
            Ivatipo ivatipoNew = iva.getIvatipo();
            Producto productoOld = persistentIva.getProducto();
            Producto productoNew = iva.getProducto();
            if (ivatipoNew != null) {
                ivatipoNew = em.getReference(ivatipoNew.getClass(), ivatipoNew.getIdIva());
                iva.setIvatipo(ivatipoNew);
            }
            if (productoNew != null) {
                productoNew = em.getReference(productoNew.getClass(), productoNew.getIdProducto());
                iva.setProducto(productoNew);
            }
            iva = em.merge(iva);
            if (ivatipoOld != null && !ivatipoOld.equals(ivatipoNew)) {
                ivatipoOld.getIvas().remove(iva);
                ivatipoOld = em.merge(ivatipoOld);
            }
            if (ivatipoNew != null && !ivatipoNew.equals(ivatipoOld)) {
                ivatipoNew.getIvas().add(iva);
                ivatipoNew = em.merge(ivatipoNew);
            }
            if (productoOld != null && !productoOld.equals(productoNew)) {
                productoOld.getIvas().remove(iva);
                productoOld = em.merge(productoOld);
            }
            if (productoNew != null && !productoNew.equals(productoOld)) {
                productoNew.getIvas().add(iva);
                productoNew = em.merge(productoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = iva.getIdIva();
                if (findIva(id) == null) {
                    throw new NonexistentEntityException("The iva with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Iva iva;
            try {
                iva = em.getReference(Iva.class, id);
                iva.getIdIva();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The iva with id " + id + " no longer exists.", enfe);
            }
            Ivatipo ivatipo = iva.getIvatipo();
            if (ivatipo != null) {
                ivatipo.getIvas().remove(iva);
                ivatipo = em.merge(ivatipo);
            }
            Producto producto = iva.getProducto();
            if (producto != null) {
                producto.getIvas().remove(iva);
                producto = em.merge(producto);
            }
            em.remove(iva);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Iva> findIvaEntities() {
        return findIvaEntities(true, -1, -1);
    }

    public List<Iva> findIvaEntities(int maxResults, int firstResult) {
        return findIvaEntities(false, maxResults, firstResult);
    }

    private List<Iva> findIvaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Iva.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Iva findIva(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Iva.class, id);
        } finally {
            em.close();
        }
    }

    public int getIvaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Iva> rt = cq.from(Iva.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
