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
import com.ar.facturacion.model.Tipotributo;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.model.Tributos;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leonardo
 */
public class TributosJpaController implements Serializable {

    public TributosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tributos tributos) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tipotributo tipotributo = tributos.getTipotributo();
            if (tipotributo != null) {
                tipotributo = em.getReference(tipotributo.getClass(), tipotributo.getIdTipoTributo());
                tributos.setTipotributo(tipotributo);
            }
            Producto producto = tributos.getProducto();
            if (producto != null) {
                producto = em.getReference(producto.getClass(), producto.getIdProducto());
                tributos.setProducto(producto);
            }
            em.persist(tributos);
            if (tipotributo != null) {
                tipotributo.getTributos().add(tributos);
                tipotributo = em.merge(tipotributo);
            }
            if (producto != null) {
                Tributos oldTributosOfProducto = producto.getTributos();
                if (oldTributosOfProducto != null) {
                    oldTributosOfProducto.setProducto(null);
                    oldTributosOfProducto = em.merge(oldTributosOfProducto);
                }
                producto.setTributos(tributos);
                producto = em.merge(producto);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tributos tributos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tributos persistentTributos = em.find(Tributos.class, tributos.getIdTributo());
            Tipotributo tipotributoOld = persistentTributos.getTipotributo();
            Tipotributo tipotributoNew = tributos.getTipotributo();
            Producto productoOld = persistentTributos.getProducto();
            Producto productoNew = tributos.getProducto();
            if (tipotributoNew != null) {
                tipotributoNew = em.getReference(tipotributoNew.getClass(), tipotributoNew.getIdTipoTributo());
                tributos.setTipotributo(tipotributoNew);
            }
            if (productoNew != null) {
                productoNew = em.getReference(productoNew.getClass(), productoNew.getIdProducto());
                tributos.setProducto(productoNew);
            }
            tributos = em.merge(tributos);
            if (tipotributoOld != null && !tipotributoOld.equals(tipotributoNew)) {
                tipotributoOld.getTributos().remove(tributos);
                tipotributoOld = em.merge(tipotributoOld);
            }
            if (tipotributoNew != null && !tipotributoNew.equals(tipotributoOld)) {
                tipotributoNew.getTributos().add(tributos);
                tipotributoNew = em.merge(tipotributoNew);
            }
            if (productoOld != null && !productoOld.equals(productoNew)) {
                productoOld.setTributos(null);
                productoOld = em.merge(productoOld);
            }
            if (productoNew != null && !productoNew.equals(productoOld)) {
                Tributos oldTributosOfProducto = productoNew.getTributos();
                if (oldTributosOfProducto != null) {
                    oldTributosOfProducto.setProducto(null);
                    oldTributosOfProducto = em.merge(oldTributosOfProducto);
                }
                productoNew.setTributos(tributos);
                productoNew = em.merge(productoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = tributos.getIdTributo();
                if (findTributos(id) == null) {
                    throw new NonexistentEntityException("The tributos with id " + id + " no longer exists.");
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
            Tributos tributos;
            try {
                tributos = em.getReference(Tributos.class, id);
                tributos.getIdTributo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tributos with id " + id + " no longer exists.", enfe);
            }
            Tipotributo tipotributo = tributos.getTipotributo();
            if (tipotributo != null) {
                tipotributo.getTributos().remove(tributos);
                tipotributo = em.merge(tipotributo);
            }
            Producto producto = tributos.getProducto();
            if (producto != null) {
                producto.setTributos(null);
                producto = em.merge(producto);
            }
            em.remove(tributos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tributos> findTributosEntities() {
        return findTributosEntities(true, -1, -1);
    }

    public List<Tributos> findTributosEntities(int maxResults, int firstResult) {
        return findTributosEntities(false, maxResults, firstResult);
    }

    private List<Tributos> findTributosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tributos.class));
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

    public Tributos findTributos(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tributos.class, id);
        } finally {
            em.close();
        }
    }

    public int getTributosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tributos> rt = cq.from(Tributos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
