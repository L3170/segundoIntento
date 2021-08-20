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
import com.ar.facturacion.model.Ptoventa;
import com.ar.facturacion.provisorio.exceptions.IllegalOrphanException;
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
public class PtoventaJpaController implements Serializable {

    public PtoventaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ptoventa ptoventa) throws PreexistingEntityException, Exception {
        if (ptoventa.getCbtesasocs() == null) {
            ptoventa.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cbtesasoc> attachedCbtesasocs = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsCbtesasocToAttach : ptoventa.getCbtesasocs()) {
                cbtesasocsCbtesasocToAttach = em.getReference(cbtesasocsCbtesasocToAttach.getClass(), cbtesasocsCbtesasocToAttach.getNumero());
                attachedCbtesasocs.add(cbtesasocsCbtesasocToAttach);
            }
            ptoventa.setCbtesasocs(attachedCbtesasocs);
            em.persist(ptoventa);
            for (Cbtesasoc cbtesasocsCbtesasoc : ptoventa.getCbtesasocs()) {
                Ptoventa oldPtoventaOfCbtesasocsCbtesasoc = cbtesasocsCbtesasoc.getPtoventa();
                cbtesasocsCbtesasoc.setPtoventa(ptoventa);
                cbtesasocsCbtesasoc = em.merge(cbtesasocsCbtesasoc);
                if (oldPtoventaOfCbtesasocsCbtesasoc != null) {
                    oldPtoventaOfCbtesasocsCbtesasoc.getCbtesasocs().remove(cbtesasocsCbtesasoc);
                    oldPtoventaOfCbtesasocsCbtesasoc = em.merge(oldPtoventaOfCbtesasocsCbtesasoc);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPtoventa(ptoventa.getNumero()) != null) {
                throw new PreexistingEntityException("Ptoventa " + ptoventa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ptoventa ptoventa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ptoventa persistentPtoventa = em.find(Ptoventa.class, ptoventa.getNumero());
            List<Cbtesasoc> cbtesasocsOld = persistentPtoventa.getCbtesasocs();
            List<Cbtesasoc> cbtesasocsNew = ptoventa.getCbtesasocs();
            List<String> illegalOrphanMessages = null;
            for (Cbtesasoc cbtesasocsOldCbtesasoc : cbtesasocsOld) {
                if (!cbtesasocsNew.contains(cbtesasocsOldCbtesasoc)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cbtesasoc " + cbtesasocsOldCbtesasoc + " since its ptoventa field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Cbtesasoc> attachedCbtesasocsNew = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsNewCbtesasocToAttach : cbtesasocsNew) {
                cbtesasocsNewCbtesasocToAttach = em.getReference(cbtesasocsNewCbtesasocToAttach.getClass(), cbtesasocsNewCbtesasocToAttach.getNumero());
                attachedCbtesasocsNew.add(cbtesasocsNewCbtesasocToAttach);
            }
            cbtesasocsNew = attachedCbtesasocsNew;
            ptoventa.setCbtesasocs(cbtesasocsNew);
            ptoventa = em.merge(ptoventa);
            for (Cbtesasoc cbtesasocsNewCbtesasoc : cbtesasocsNew) {
                if (!cbtesasocsOld.contains(cbtesasocsNewCbtesasoc)) {
                    Ptoventa oldPtoventaOfCbtesasocsNewCbtesasoc = cbtesasocsNewCbtesasoc.getPtoventa();
                    cbtesasocsNewCbtesasoc.setPtoventa(ptoventa);
                    cbtesasocsNewCbtesasoc = em.merge(cbtesasocsNewCbtesasoc);
                    if (oldPtoventaOfCbtesasocsNewCbtesasoc != null && !oldPtoventaOfCbtesasocsNewCbtesasoc.equals(ptoventa)) {
                        oldPtoventaOfCbtesasocsNewCbtesasoc.getCbtesasocs().remove(cbtesasocsNewCbtesasoc);
                        oldPtoventaOfCbtesasocsNewCbtesasoc = em.merge(oldPtoventaOfCbtesasocsNewCbtesasoc);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = ptoventa.getNumero();
                if (findPtoventa(id) == null) {
                    throw new NonexistentEntityException("The ptoventa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ptoventa ptoventa;
            try {
                ptoventa = em.getReference(Ptoventa.class, id);
                ptoventa.getNumero();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ptoventa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cbtesasoc> cbtesasocsOrphanCheck = ptoventa.getCbtesasocs();
            for (Cbtesasoc cbtesasocsOrphanCheckCbtesasoc : cbtesasocsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ptoventa (" + ptoventa + ") cannot be destroyed since the Cbtesasoc " + cbtesasocsOrphanCheckCbtesasoc + " in its cbtesasocs field has a non-nullable ptoventa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(ptoventa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ptoventa> findPtoventaEntities() {
        return findPtoventaEntities(true, -1, -1);
    }

    public List<Ptoventa> findPtoventaEntities(int maxResults, int firstResult) {
        return findPtoventaEntities(false, maxResults, firstResult);
    }

    private List<Ptoventa> findPtoventaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ptoventa.class));
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

    public Ptoventa findPtoventa(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ptoventa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPtoventaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ptoventa> rt = cq.from(Ptoventa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
