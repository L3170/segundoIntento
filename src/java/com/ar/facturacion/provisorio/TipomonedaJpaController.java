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
import com.ar.facturacion.model.Moncotiz;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Tipomoneda;
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
public class TipomonedaJpaController implements Serializable {

    public TipomonedaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tipomoneda tipomoneda) throws PreexistingEntityException, Exception {
        if (tipomoneda.getCbtesasocs() == null) {
            tipomoneda.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Moncotiz moncotiz = tipomoneda.getMoncotiz();
            if (moncotiz != null) {
                moncotiz = em.getReference(moncotiz.getClass(), moncotiz.getMonId());
                tipomoneda.setMoncotiz(moncotiz);
            }
            List<Cbtesasoc> attachedCbtesasocs = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsCbtesasocToAttach : tipomoneda.getCbtesasocs()) {
                cbtesasocsCbtesasocToAttach = em.getReference(cbtesasocsCbtesasocToAttach.getClass(), cbtesasocsCbtesasocToAttach.getNumero());
                attachedCbtesasocs.add(cbtesasocsCbtesasocToAttach);
            }
            tipomoneda.setCbtesasocs(attachedCbtesasocs);
            em.persist(tipomoneda);
            if (moncotiz != null) {
                Tipomoneda oldTipomonedaOfMoncotiz = moncotiz.getTipomoneda();
                if (oldTipomonedaOfMoncotiz != null) {
                    oldTipomonedaOfMoncotiz.setMoncotiz(null);
                    oldTipomonedaOfMoncotiz = em.merge(oldTipomonedaOfMoncotiz);
                }
                moncotiz.setTipomoneda(tipomoneda);
                moncotiz = em.merge(moncotiz);
            }
            for (Cbtesasoc cbtesasocsCbtesasoc : tipomoneda.getCbtesasocs()) {
                Tipomoneda oldTipomonedaOfCbtesasocsCbtesasoc = cbtesasocsCbtesasoc.getTipomoneda();
                cbtesasocsCbtesasoc.setTipomoneda(tipomoneda);
                cbtesasocsCbtesasoc = em.merge(cbtesasocsCbtesasoc);
                if (oldTipomonedaOfCbtesasocsCbtesasoc != null) {
                    oldTipomonedaOfCbtesasocsCbtesasoc.getCbtesasocs().remove(cbtesasocsCbtesasoc);
                    oldTipomonedaOfCbtesasocsCbtesasoc = em.merge(oldTipomonedaOfCbtesasocsCbtesasoc);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipomoneda(tipomoneda.getIdMoneda()) != null) {
                throw new PreexistingEntityException("Tipomoneda " + tipomoneda + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tipomoneda tipomoneda) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tipomoneda persistentTipomoneda = em.find(Tipomoneda.class, tipomoneda.getIdMoneda());
            Moncotiz moncotizOld = persistentTipomoneda.getMoncotiz();
            Moncotiz moncotizNew = tipomoneda.getMoncotiz();
            List<Cbtesasoc> cbtesasocsOld = persistentTipomoneda.getCbtesasocs();
            List<Cbtesasoc> cbtesasocsNew = tipomoneda.getCbtesasocs();
            List<String> illegalOrphanMessages = null;
            for (Cbtesasoc cbtesasocsOldCbtesasoc : cbtesasocsOld) {
                if (!cbtesasocsNew.contains(cbtesasocsOldCbtesasoc)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cbtesasoc " + cbtesasocsOldCbtesasoc + " since its tipomoneda field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (moncotizNew != null) {
                moncotizNew = em.getReference(moncotizNew.getClass(), moncotizNew.getMonId());
                tipomoneda.setMoncotiz(moncotizNew);
            }
            List<Cbtesasoc> attachedCbtesasocsNew = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsNewCbtesasocToAttach : cbtesasocsNew) {
                cbtesasocsNewCbtesasocToAttach = em.getReference(cbtesasocsNewCbtesasocToAttach.getClass(), cbtesasocsNewCbtesasocToAttach.getNumero());
                attachedCbtesasocsNew.add(cbtesasocsNewCbtesasocToAttach);
            }
            cbtesasocsNew = attachedCbtesasocsNew;
            tipomoneda.setCbtesasocs(cbtesasocsNew);
            tipomoneda = em.merge(tipomoneda);
            if (moncotizOld != null && !moncotizOld.equals(moncotizNew)) {
                moncotizOld.setTipomoneda(null);
                moncotizOld = em.merge(moncotizOld);
            }
            if (moncotizNew != null && !moncotizNew.equals(moncotizOld)) {
                Tipomoneda oldTipomonedaOfMoncotiz = moncotizNew.getTipomoneda();
                if (oldTipomonedaOfMoncotiz != null) {
                    oldTipomonedaOfMoncotiz.setMoncotiz(null);
                    oldTipomonedaOfMoncotiz = em.merge(oldTipomonedaOfMoncotiz);
                }
                moncotizNew.setTipomoneda(tipomoneda);
                moncotizNew = em.merge(moncotizNew);
            }
            for (Cbtesasoc cbtesasocsNewCbtesasoc : cbtesasocsNew) {
                if (!cbtesasocsOld.contains(cbtesasocsNewCbtesasoc)) {
                    Tipomoneda oldTipomonedaOfCbtesasocsNewCbtesasoc = cbtesasocsNewCbtesasoc.getTipomoneda();
                    cbtesasocsNewCbtesasoc.setTipomoneda(tipomoneda);
                    cbtesasocsNewCbtesasoc = em.merge(cbtesasocsNewCbtesasoc);
                    if (oldTipomonedaOfCbtesasocsNewCbtesasoc != null && !oldTipomonedaOfCbtesasocsNewCbtesasoc.equals(tipomoneda)) {
                        oldTipomonedaOfCbtesasocsNewCbtesasoc.getCbtesasocs().remove(cbtesasocsNewCbtesasoc);
                        oldTipomonedaOfCbtesasocsNewCbtesasoc = em.merge(oldTipomonedaOfCbtesasocsNewCbtesasoc);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = tipomoneda.getIdMoneda();
                if (findTipomoneda(id) == null) {
                    throw new NonexistentEntityException("The tipomoneda with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tipomoneda tipomoneda;
            try {
                tipomoneda = em.getReference(Tipomoneda.class, id);
                tipomoneda.getIdMoneda();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipomoneda with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cbtesasoc> cbtesasocsOrphanCheck = tipomoneda.getCbtesasocs();
            for (Cbtesasoc cbtesasocsOrphanCheckCbtesasoc : cbtesasocsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tipomoneda (" + tipomoneda + ") cannot be destroyed since the Cbtesasoc " + cbtesasocsOrphanCheckCbtesasoc + " in its cbtesasocs field has a non-nullable tipomoneda field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Moncotiz moncotiz = tipomoneda.getMoncotiz();
            if (moncotiz != null) {
                moncotiz.setTipomoneda(null);
                moncotiz = em.merge(moncotiz);
            }
            em.remove(tipomoneda);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tipomoneda> findTipomonedaEntities() {
        return findTipomonedaEntities(true, -1, -1);
    }

    public List<Tipomoneda> findTipomonedaEntities(int maxResults, int firstResult) {
        return findTipomonedaEntities(false, maxResults, firstResult);
    }

    private List<Tipomoneda> findTipomonedaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tipomoneda.class));
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

    public Tipomoneda findTipomoneda(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tipomoneda.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipomonedaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tipomoneda> rt = cq.from(Tipomoneda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
