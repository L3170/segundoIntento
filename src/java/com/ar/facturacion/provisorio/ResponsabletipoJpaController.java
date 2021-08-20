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
import com.ar.facturacion.model.Responsabletipo;
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
public class ResponsabletipoJpaController implements Serializable {

    public ResponsabletipoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Responsabletipo responsabletipo) throws PreexistingEntityException, Exception {
        if (responsabletipo.getCbtesasocs() == null) {
            responsabletipo.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cbtesasoc> attachedCbtesasocs = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsCbtesasocToAttach : responsabletipo.getCbtesasocs()) {
                cbtesasocsCbtesasocToAttach = em.getReference(cbtesasocsCbtesasocToAttach.getClass(), cbtesasocsCbtesasocToAttach.getNumero());
                attachedCbtesasocs.add(cbtesasocsCbtesasocToAttach);
            }
            responsabletipo.setCbtesasocs(attachedCbtesasocs);
            em.persist(responsabletipo);
            for (Cbtesasoc cbtesasocsCbtesasoc : responsabletipo.getCbtesasocs()) {
                Responsabletipo oldResponsabletipoOfCbtesasocsCbtesasoc = cbtesasocsCbtesasoc.getResponsabletipo();
                cbtesasocsCbtesasoc.setResponsabletipo(responsabletipo);
                cbtesasocsCbtesasoc = em.merge(cbtesasocsCbtesasoc);
                if (oldResponsabletipoOfCbtesasocsCbtesasoc != null) {
                    oldResponsabletipoOfCbtesasocsCbtesasoc.getCbtesasocs().remove(cbtesasocsCbtesasoc);
                    oldResponsabletipoOfCbtesasocsCbtesasoc = em.merge(oldResponsabletipoOfCbtesasocsCbtesasoc);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findResponsabletipo(responsabletipo.getIdResponsable()) != null) {
                throw new PreexistingEntityException("Responsabletipo " + responsabletipo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Responsabletipo responsabletipo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Responsabletipo persistentResponsabletipo = em.find(Responsabletipo.class, responsabletipo.getIdResponsable());
            List<Cbtesasoc> cbtesasocsOld = persistentResponsabletipo.getCbtesasocs();
            List<Cbtesasoc> cbtesasocsNew = responsabletipo.getCbtesasocs();
            List<String> illegalOrphanMessages = null;
            for (Cbtesasoc cbtesasocsOldCbtesasoc : cbtesasocsOld) {
                if (!cbtesasocsNew.contains(cbtesasocsOldCbtesasoc)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cbtesasoc " + cbtesasocsOldCbtesasoc + " since its responsabletipo field is not nullable.");
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
            responsabletipo.setCbtesasocs(cbtesasocsNew);
            responsabletipo = em.merge(responsabletipo);
            for (Cbtesasoc cbtesasocsNewCbtesasoc : cbtesasocsNew) {
                if (!cbtesasocsOld.contains(cbtesasocsNewCbtesasoc)) {
                    Responsabletipo oldResponsabletipoOfCbtesasocsNewCbtesasoc = cbtesasocsNewCbtesasoc.getResponsabletipo();
                    cbtesasocsNewCbtesasoc.setResponsabletipo(responsabletipo);
                    cbtesasocsNewCbtesasoc = em.merge(cbtesasocsNewCbtesasoc);
                    if (oldResponsabletipoOfCbtesasocsNewCbtesasoc != null && !oldResponsabletipoOfCbtesasocsNewCbtesasoc.equals(responsabletipo)) {
                        oldResponsabletipoOfCbtesasocsNewCbtesasoc.getCbtesasocs().remove(cbtesasocsNewCbtesasoc);
                        oldResponsabletipoOfCbtesasocsNewCbtesasoc = em.merge(oldResponsabletipoOfCbtesasocsNewCbtesasoc);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = responsabletipo.getIdResponsable();
                if (findResponsabletipo(id) == null) {
                    throw new NonexistentEntityException("The responsabletipo with id " + id + " no longer exists.");
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
            Responsabletipo responsabletipo;
            try {
                responsabletipo = em.getReference(Responsabletipo.class, id);
                responsabletipo.getIdResponsable();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The responsabletipo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cbtesasoc> cbtesasocsOrphanCheck = responsabletipo.getCbtesasocs();
            for (Cbtesasoc cbtesasocsOrphanCheckCbtesasoc : cbtesasocsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Responsabletipo (" + responsabletipo + ") cannot be destroyed since the Cbtesasoc " + cbtesasocsOrphanCheckCbtesasoc + " in its cbtesasocs field has a non-nullable responsabletipo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(responsabletipo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Responsabletipo> findResponsabletipoEntities() {
        return findResponsabletipoEntities(true, -1, -1);
    }

    public List<Responsabletipo> findResponsabletipoEntities(int maxResults, int firstResult) {
        return findResponsabletipoEntities(false, maxResults, firstResult);
    }

    private List<Responsabletipo> findResponsabletipoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Responsabletipo.class));
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

    public Responsabletipo findResponsabletipo(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Responsabletipo.class, id);
        } finally {
            em.close();
        }
    }

    public int getResponsabletipoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Responsabletipo> rt = cq.from(Responsabletipo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
