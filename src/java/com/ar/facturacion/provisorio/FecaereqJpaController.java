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
import com.ar.facturacion.model.Fecaereq;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import com.ar.facturacion.provisorio.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leonardo
 */
public class FecaereqJpaController implements Serializable {

    public FecaereqJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Fecaereq fecaereq) throws PreexistingEntityException, Exception {
        if (fecaereq.getCbtesasocs() == null) {
            fecaereq.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cbtesasoc> attachedCbtesasocs = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsCbtesasocToAttach : fecaereq.getCbtesasocs()) {
                cbtesasocsCbtesasocToAttach = em.getReference(cbtesasocsCbtesasocToAttach.getClass(), cbtesasocsCbtesasocToAttach.getNumero());
                attachedCbtesasocs.add(cbtesasocsCbtesasocToAttach);
            }
            fecaereq.setCbtesasocs(attachedCbtesasocs);
            em.persist(fecaereq);
            for (Cbtesasoc cbtesasocsCbtesasoc : fecaereq.getCbtesasocs()) {
                Fecaereq oldFecaereqOfCbtesasocsCbtesasoc = cbtesasocsCbtesasoc.getFecaereq();
                cbtesasocsCbtesasoc.setFecaereq(fecaereq);
                cbtesasocsCbtesasoc = em.merge(cbtesasocsCbtesasoc);
                if (oldFecaereqOfCbtesasocsCbtesasoc != null) {
                    oldFecaereqOfCbtesasocsCbtesasoc.getCbtesasocs().remove(cbtesasocsCbtesasoc);
                    oldFecaereqOfCbtesasocsCbtesasoc = em.merge(oldFecaereqOfCbtesasocsCbtesasoc);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFecaereq(fecaereq.getIdFeCaereq()) != null) {
                throw new PreexistingEntityException("Fecaereq " + fecaereq + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Fecaereq fecaereq) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fecaereq persistentFecaereq = em.find(Fecaereq.class, fecaereq.getIdFeCaereq());
            List<Cbtesasoc> cbtesasocsOld = persistentFecaereq.getCbtesasocs();
            List<Cbtesasoc> cbtesasocsNew = fecaereq.getCbtesasocs();
            List<Cbtesasoc> attachedCbtesasocsNew = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsNewCbtesasocToAttach : cbtesasocsNew) {
                cbtesasocsNewCbtesasocToAttach = em.getReference(cbtesasocsNewCbtesasocToAttach.getClass(), cbtesasocsNewCbtesasocToAttach.getNumero());
                attachedCbtesasocsNew.add(cbtesasocsNewCbtesasocToAttach);
            }
            cbtesasocsNew = attachedCbtesasocsNew;
            fecaereq.setCbtesasocs(cbtesasocsNew);
            fecaereq = em.merge(fecaereq);
            for (Cbtesasoc cbtesasocsOldCbtesasoc : cbtesasocsOld) {
                if (!cbtesasocsNew.contains(cbtesasocsOldCbtesasoc)) {
                    cbtesasocsOldCbtesasoc.setFecaereq(null);
                    cbtesasocsOldCbtesasoc = em.merge(cbtesasocsOldCbtesasoc);
                }
            }
            for (Cbtesasoc cbtesasocsNewCbtesasoc : cbtesasocsNew) {
                if (!cbtesasocsOld.contains(cbtesasocsNewCbtesasoc)) {
                    Fecaereq oldFecaereqOfCbtesasocsNewCbtesasoc = cbtesasocsNewCbtesasoc.getFecaereq();
                    cbtesasocsNewCbtesasoc.setFecaereq(fecaereq);
                    cbtesasocsNewCbtesasoc = em.merge(cbtesasocsNewCbtesasoc);
                    if (oldFecaereqOfCbtesasocsNewCbtesasoc != null && !oldFecaereqOfCbtesasocsNewCbtesasoc.equals(fecaereq)) {
                        oldFecaereqOfCbtesasocsNewCbtesasoc.getCbtesasocs().remove(cbtesasocsNewCbtesasoc);
                        oldFecaereqOfCbtesasocsNewCbtesasoc = em.merge(oldFecaereqOfCbtesasocsNewCbtesasoc);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = fecaereq.getIdFeCaereq();
                if (findFecaereq(id) == null) {
                    throw new NonexistentEntityException("The fecaereq with id " + id + " no longer exists.");
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
            Fecaereq fecaereq;
            try {
                fecaereq = em.getReference(Fecaereq.class, id);
                fecaereq.getIdFeCaereq();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The fecaereq with id " + id + " no longer exists.", enfe);
            }
            List<Cbtesasoc> cbtesasocs = fecaereq.getCbtesasocs();
            for (Cbtesasoc cbtesasocsCbtesasoc : cbtesasocs) {
                cbtesasocsCbtesasoc.setFecaereq(null);
                cbtesasocsCbtesasoc = em.merge(cbtesasocsCbtesasoc);
            }
            em.remove(fecaereq);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Fecaereq> findFecaereqEntities() {
        return findFecaereqEntities(true, -1, -1);
    }

    public List<Fecaereq> findFecaereqEntities(int maxResults, int firstResult) {
        return findFecaereqEntities(false, maxResults, firstResult);
    }

    private List<Fecaereq> findFecaereqEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Fecaereq.class));
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

    public Fecaereq findFecaereq(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Fecaereq.class, id);
        } finally {
            em.close();
        }
    }

    public int getFecaereqCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Fecaereq> rt = cq.from(Fecaereq.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
