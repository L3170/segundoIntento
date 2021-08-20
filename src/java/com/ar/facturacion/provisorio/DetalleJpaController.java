/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.provisorio;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Detalle;
import com.ar.facturacion.model.DetalleId;
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
public class DetalleJpaController implements Serializable {

    public DetalleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detalle detalle) throws PreexistingEntityException, Exception {
        if (detalle.getId() == null) {
            detalle.setId(new DetalleId());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cbtesasoc cbtesasoc = detalle.getCbtesasoc();
            if (cbtesasoc != null) {
                cbtesasoc = em.getReference(cbtesasoc.getClass(), cbtesasoc.getNumero());
                detalle.setCbtesasoc(cbtesasoc);
            }
            Producto producto = detalle.getProducto();
            if (producto != null) {
                producto = em.getReference(producto.getClass(), producto.getIdProducto());
                detalle.setProducto(producto);
            }
            em.persist(detalle);
            if (cbtesasoc != null) {
                cbtesasoc.getDetalles().add(detalle);
                cbtesasoc = em.merge(cbtesasoc);
            }
            if (producto != null) {
                producto.getDetalles().add(detalle);
                producto = em.merge(producto);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDetalle(detalle.getId()) != null) {
                throw new PreexistingEntityException("Detalle " + detalle + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detalle detalle) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalle persistentDetalle = em.find(Detalle.class, detalle.getId());
            Cbtesasoc cbtesasocOld = persistentDetalle.getCbtesasoc();
            Cbtesasoc cbtesasocNew = detalle.getCbtesasoc();
            Producto productoOld = persistentDetalle.getProducto();
            Producto productoNew = detalle.getProducto();
            if (cbtesasocNew != null) {
                cbtesasocNew = em.getReference(cbtesasocNew.getClass(), cbtesasocNew.getNumero());
                detalle.setCbtesasoc(cbtesasocNew);
            }
            if (productoNew != null) {
                productoNew = em.getReference(productoNew.getClass(), productoNew.getIdProducto());
                detalle.setProducto(productoNew);
            }
            detalle = em.merge(detalle);
            if (cbtesasocOld != null && !cbtesasocOld.equals(cbtesasocNew)) {
                cbtesasocOld.getDetalles().remove(detalle);
                cbtesasocOld = em.merge(cbtesasocOld);
            }
            if (cbtesasocNew != null && !cbtesasocNew.equals(cbtesasocOld)) {
                cbtesasocNew.getDetalles().add(detalle);
                cbtesasocNew = em.merge(cbtesasocNew);
            }
            if (productoOld != null && !productoOld.equals(productoNew)) {
                productoOld.getDetalles().remove(detalle);
                productoOld = em.merge(productoOld);
            }
            if (productoNew != null && !productoNew.equals(productoOld)) {
                productoNew.getDetalles().add(detalle);
                productoNew = em.merge(productoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DetalleId id = detalle.getId();
                if (findDetalle(id) == null) {
                    throw new NonexistentEntityException("The detalle with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DetalleId id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalle detalle;
            try {
                detalle = em.getReference(Detalle.class, id);
                detalle.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalle with id " + id + " no longer exists.", enfe);
            }
            Cbtesasoc cbtesasoc = detalle.getCbtesasoc();
            if (cbtesasoc != null) {
                cbtesasoc.getDetalles().remove(detalle);
                cbtesasoc = em.merge(cbtesasoc);
            }
            Producto producto = detalle.getProducto();
            if (producto != null) {
                producto.getDetalles().remove(detalle);
                producto = em.merge(producto);
            }
            em.remove(detalle);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detalle> findDetalleEntities() {
        return findDetalleEntities(true, -1, -1);
    }

    public List<Detalle> findDetalleEntities(int maxResults, int firstResult) {
        return findDetalleEntities(false, maxResults, firstResult);
    }

    private List<Detalle> findDetalleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalle.class));
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

    public Detalle findDetalle(DetalleId id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalle.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalle> rt = cq.from(Detalle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
