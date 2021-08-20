/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.provisorio;

import com.ar.facturacion.model.Moncotiz;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ar.facturacion.model.Tipomoneda;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leonardo
 */
public class MoncotizJpaController implements Serializable {

    public MoncotizJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Moncotiz moncotiz) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tipomoneda tipomoneda = moncotiz.getTipomoneda();
            if (tipomoneda != null) {
                tipomoneda = em.getReference(tipomoneda.getClass(), tipomoneda.getIdMoneda());
                moncotiz.setTipomoneda(tipomoneda);
            }
            em.persist(moncotiz);
            if (tipomoneda != null) {
                Moncotiz oldMoncotizOfTipomoneda = tipomoneda.getMoncotiz();
                if (oldMoncotizOfTipomoneda != null) {
                    oldMoncotizOfTipomoneda.setTipomoneda(null);
                    oldMoncotizOfTipomoneda = em.merge(oldMoncotizOfTipomoneda);
                }
                tipomoneda.setMoncotiz(moncotiz);
                tipomoneda = em.merge(tipomoneda);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Moncotiz moncotiz) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Moncotiz persistentMoncotiz = em.find(Moncotiz.class, moncotiz.getMonId());
            Tipomoneda tipomonedaOld = persistentMoncotiz.getTipomoneda();
            Tipomoneda tipomonedaNew = moncotiz.getTipomoneda();
            if (tipomonedaNew != null) {
                tipomonedaNew = em.getReference(tipomonedaNew.getClass(), tipomonedaNew.getIdMoneda());
                moncotiz.setTipomoneda(tipomonedaNew);
            }
            moncotiz = em.merge(moncotiz);
            if (tipomonedaOld != null && !tipomonedaOld.equals(tipomonedaNew)) {
                tipomonedaOld.setMoncotiz(null);
                tipomonedaOld = em.merge(tipomonedaOld);
            }
            if (tipomonedaNew != null && !tipomonedaNew.equals(tipomonedaOld)) {
                Moncotiz oldMoncotizOfTipomoneda = tipomonedaNew.getMoncotiz();
                if (oldMoncotizOfTipomoneda != null) {
                    oldMoncotizOfTipomoneda.setTipomoneda(null);
                    oldMoncotizOfTipomoneda = em.merge(oldMoncotizOfTipomoneda);
                }
                tipomonedaNew.setMoncotiz(moncotiz);
                tipomonedaNew = em.merge(tipomonedaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = moncotiz.getMonId();
                if (findMoncotiz(id) == null) {
                    throw new NonexistentEntityException("The moncotiz with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Moncotiz moncotiz;
            try {
                moncotiz = em.getReference(Moncotiz.class, id);
                moncotiz.getMonId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The moncotiz with id " + id + " no longer exists.", enfe);
            }
            Tipomoneda tipomoneda = moncotiz.getTipomoneda();
            if (tipomoneda != null) {
                tipomoneda.setMoncotiz(null);
                tipomoneda = em.merge(tipomoneda);
            }
            em.remove(moncotiz);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Moncotiz> findMoncotizEntities() {
        return findMoncotizEntities(true, -1, -1);
    }

    public List<Moncotiz> findMoncotizEntities(int maxResults, int firstResult) {
        return findMoncotizEntities(false, maxResults, firstResult);
    }

    private List<Moncotiz> findMoncotizEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Moncotiz.class));
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

    public Moncotiz findMoncotiz(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Moncotiz.class, id);
        } finally {
            em.close();
        }
    }

    public int getMoncotizCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Moncotiz> rt = cq.from(Moncotiz.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
