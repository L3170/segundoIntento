/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.provisorio;

import com.ar.facturacion.model.Tipotributo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ar.facturacion.model.Tributos;
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
public class TipotributoJpaController implements Serializable {

    public TipotributoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tipotributo tipotributo) throws PreexistingEntityException, Exception {
        if (tipotributo.getTributos() == null) {
            tipotributo.setTributos(new ArrayList<Tributos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Tributos> attachedTributos = new ArrayList<Tributos>();
            for (Tributos tributosTributosToAttach : tipotributo.getTributos()) {
                tributosTributosToAttach = em.getReference(tributosTributosToAttach.getClass(), tributosTributosToAttach.getIdTributo());
                attachedTributos.add(tributosTributosToAttach);
            }
            tipotributo.setTributos(attachedTributos);
            em.persist(tipotributo);
            for (Tributos tributosTributos : tipotributo.getTributos()) {
                Tipotributo oldTipotributoOfTributosTributos = tributosTributos.getTipotributo();
                tributosTributos.setTipotributo(tipotributo);
                tributosTributos = em.merge(tributosTributos);
                if (oldTipotributoOfTributosTributos != null) {
                    oldTipotributoOfTributosTributos.getTributos().remove(tributosTributos);
                    oldTipotributoOfTributosTributos = em.merge(oldTipotributoOfTributosTributos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipotributo(tipotributo.getIdTipoTributo()) != null) {
                throw new PreexistingEntityException("Tipotributo " + tipotributo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tipotributo tipotributo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tipotributo persistentTipotributo = em.find(Tipotributo.class, tipotributo.getIdTipoTributo());
            List<Tributos> tributosOld = persistentTipotributo.getTributos();
            List<Tributos> tributosNew = tipotributo.getTributos();
            List<String> illegalOrphanMessages = null;
            for (Tributos tributosOldTributos : tributosOld) {
                if (!tributosNew.contains(tributosOldTributos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tributos " + tributosOldTributos + " since its tipotributo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Tributos> attachedTributosNew = new ArrayList<Tributos>();
            for (Tributos tributosNewTributosToAttach : tributosNew) {
                tributosNewTributosToAttach = em.getReference(tributosNewTributosToAttach.getClass(), tributosNewTributosToAttach.getIdTributo());
                attachedTributosNew.add(tributosNewTributosToAttach);
            }
            tributosNew = attachedTributosNew;
            tipotributo.setTributos(tributosNew);
            tipotributo = em.merge(tipotributo);
            for (Tributos tributosNewTributos : tributosNew) {
                if (!tributosOld.contains(tributosNewTributos)) {
                    Tipotributo oldTipotributoOfTributosNewTributos = tributosNewTributos.getTipotributo();
                    tributosNewTributos.setTipotributo(tipotributo);
                    tributosNewTributos = em.merge(tributosNewTributos);
                    if (oldTipotributoOfTributosNewTributos != null && !oldTipotributoOfTributosNewTributos.equals(tipotributo)) {
                        oldTipotributoOfTributosNewTributos.getTributos().remove(tributosNewTributos);
                        oldTipotributoOfTributosNewTributos = em.merge(oldTipotributoOfTributosNewTributos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = tipotributo.getIdTipoTributo();
                if (findTipotributo(id) == null) {
                    throw new NonexistentEntityException("The tipotributo with id " + id + " no longer exists.");
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
            Tipotributo tipotributo;
            try {
                tipotributo = em.getReference(Tipotributo.class, id);
                tipotributo.getIdTipoTributo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipotributo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Tributos> tributosOrphanCheck = tipotributo.getTributos();
            for (Tributos tributosOrphanCheckTributos : tributosOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tipotributo (" + tipotributo + ") cannot be destroyed since the Tributos " + tributosOrphanCheckTributos + " in its tributos field has a non-nullable tipotributo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipotributo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tipotributo> findTipotributoEntities() {
        return findTipotributoEntities(true, -1, -1);
    }

    public List<Tipotributo> findTipotributoEntities(int maxResults, int firstResult) {
        return findTipotributoEntities(false, maxResults, firstResult);
    }

    private List<Tipotributo> findTipotributoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tipotributo.class));
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

    public Tipotributo findTipotributo(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tipotributo.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipotributoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tipotributo> rt = cq.from(Tipotributo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
