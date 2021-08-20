/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.provisorio;

import com.ar.facturacion.model.Opcional;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import com.ar.facturacion.provisorio.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Leonardo
 */
public class OpcionalJpaController implements Serializable {

    public OpcionalJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Opcional opcional) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(opcional);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOpcional(opcional.getId()) != null) {
                throw new PreexistingEntityException("Opcional " + opcional + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Opcional opcional) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            opcional = em.merge(opcional);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = opcional.getId();
                if (findOpcional(id) == null) {
                    throw new NonexistentEntityException("The opcional with id " + id + " no longer exists.");
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
            Opcional opcional;
            try {
                opcional = em.getReference(Opcional.class, id);
                opcional.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The opcional with id " + id + " no longer exists.", enfe);
            }
            em.remove(opcional);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Opcional> findOpcionalEntities() {
        return findOpcionalEntities(true, -1, -1);
    }

    public List<Opcional> findOpcionalEntities(int maxResults, int firstResult) {
        return findOpcionalEntities(false, maxResults, firstResult);
    }

    private List<Opcional> findOpcionalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Opcional.class));
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

    public Opcional findOpcional(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Opcional.class, id);
        } finally {
            em.close();
        }
    }

    public int getOpcionalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Opcional> rt = cq.from(Opcional.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
