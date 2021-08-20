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
import com.ar.facturacion.model.Ctetipo;
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
public class CtetipoJpaController implements Serializable {

    public CtetipoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ctetipo ctetipo) throws PreexistingEntityException, Exception {
        if (ctetipo.getCbtesasocs() == null) {
            ctetipo.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cbtesasoc> attachedCbtesasocs = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsCbtesasocToAttach : ctetipo.getCbtesasocs()) {
                cbtesasocsCbtesasocToAttach = em.getReference(cbtesasocsCbtesasocToAttach.getClass(), cbtesasocsCbtesasocToAttach.getNumero());
                attachedCbtesasocs.add(cbtesasocsCbtesasocToAttach);
            }
            ctetipo.setCbtesasocs(attachedCbtesasocs);
            em.persist(ctetipo);
            for (Cbtesasoc cbtesasocsCbtesasoc : ctetipo.getCbtesasocs()) {
                Ctetipo oldCtetipoOfCbtesasocsCbtesasoc = cbtesasocsCbtesasoc.getCtetipo();
                cbtesasocsCbtesasoc.setCtetipo(ctetipo);
                cbtesasocsCbtesasoc = em.merge(cbtesasocsCbtesasoc);
                if (oldCtetipoOfCbtesasocsCbtesasoc != null) {
                    oldCtetipoOfCbtesasocsCbtesasoc.getCbtesasocs().remove(cbtesasocsCbtesasoc);
                    oldCtetipoOfCbtesasocsCbtesasoc = em.merge(oldCtetipoOfCbtesasocsCbtesasoc);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCtetipo(ctetipo.getId()) != null) {
                throw new PreexistingEntityException("Ctetipo " + ctetipo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ctetipo ctetipo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ctetipo persistentCtetipo = em.find(Ctetipo.class, ctetipo.getId());
            List<Cbtesasoc> cbtesasocsOld = persistentCtetipo.getCbtesasocs();
            List<Cbtesasoc> cbtesasocsNew = ctetipo.getCbtesasocs();
            List<String> illegalOrphanMessages = null;
            for (Cbtesasoc cbtesasocsOldCbtesasoc : cbtesasocsOld) {
                if (!cbtesasocsNew.contains(cbtesasocsOldCbtesasoc)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cbtesasoc " + cbtesasocsOldCbtesasoc + " since its ctetipo field is not nullable.");
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
            ctetipo.setCbtesasocs(cbtesasocsNew);
            ctetipo = em.merge(ctetipo);
            for (Cbtesasoc cbtesasocsNewCbtesasoc : cbtesasocsNew) {
                if (!cbtesasocsOld.contains(cbtesasocsNewCbtesasoc)) {
                    Ctetipo oldCtetipoOfCbtesasocsNewCbtesasoc = cbtesasocsNewCbtesasoc.getCtetipo();
                    cbtesasocsNewCbtesasoc.setCtetipo(ctetipo);
                    cbtesasocsNewCbtesasoc = em.merge(cbtesasocsNewCbtesasoc);
                    if (oldCtetipoOfCbtesasocsNewCbtesasoc != null && !oldCtetipoOfCbtesasocsNewCbtesasoc.equals(ctetipo)) {
                        oldCtetipoOfCbtesasocsNewCbtesasoc.getCbtesasocs().remove(cbtesasocsNewCbtesasoc);
                        oldCtetipoOfCbtesasocsNewCbtesasoc = em.merge(oldCtetipoOfCbtesasocsNewCbtesasoc);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = ctetipo.getId();
                if (findCtetipo(id) == null) {
                    throw new NonexistentEntityException("The ctetipo with id " + id + " no longer exists.");
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
            Ctetipo ctetipo;
            try {
                ctetipo = em.getReference(Ctetipo.class, id);
                ctetipo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ctetipo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cbtesasoc> cbtesasocsOrphanCheck = ctetipo.getCbtesasocs();
            for (Cbtesasoc cbtesasocsOrphanCheckCbtesasoc : cbtesasocsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ctetipo (" + ctetipo + ") cannot be destroyed since the Cbtesasoc " + cbtesasocsOrphanCheckCbtesasoc + " in its cbtesasocs field has a non-nullable ctetipo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(ctetipo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ctetipo> findCtetipoEntities() {
        return findCtetipoEntities(true, -1, -1);
    }

    public List<Ctetipo> findCtetipoEntities(int maxResults, int firstResult) {
        return findCtetipoEntities(false, maxResults, firstResult);
    }

    private List<Ctetipo> findCtetipoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ctetipo.class));
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

    public Ctetipo findCtetipo(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ctetipo.class, id);
        } finally {
            em.close();
        }
    }

    public int getCtetipoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ctetipo> rt = cq.from(Ctetipo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
