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
import com.ar.facturacion.model.Iva;
import com.ar.facturacion.model.Ivatipo;
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
public class IvatipoJpaController implements Serializable {

    public IvatipoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ivatipo ivatipo) throws PreexistingEntityException, Exception {
        if (ivatipo.getIvas() == null) {
            ivatipo.setIvas(new ArrayList<Iva>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Iva> attachedIvas = new ArrayList<Iva>();
            for (Iva ivasIvaToAttach : ivatipo.getIvas()) {
                ivasIvaToAttach = em.getReference(ivasIvaToAttach.getClass(), ivasIvaToAttach.getIdIva());
                attachedIvas.add(ivasIvaToAttach);
            }
            ivatipo.setIvas(attachedIvas);
            em.persist(ivatipo);
            for (Iva ivasIva : ivatipo.getIvas()) {
                Ivatipo oldIvatipoOfIvasIva = ivasIva.getIvatipo();
                ivasIva.setIvatipo(ivatipo);
                ivasIva = em.merge(ivasIva);
                if (oldIvatipoOfIvasIva != null) {
                    oldIvatipoOfIvasIva.getIvas().remove(ivasIva);
                    oldIvatipoOfIvasIva = em.merge(oldIvatipoOfIvasIva);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findIvatipo(ivatipo.getIdIva()) != null) {
                throw new PreexistingEntityException("Ivatipo " + ivatipo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ivatipo ivatipo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ivatipo persistentIvatipo = em.find(Ivatipo.class, ivatipo.getIdIva());
            List<Iva> ivasOld = persistentIvatipo.getIvas();
            List<Iva> ivasNew = ivatipo.getIvas();
            List<String> illegalOrphanMessages = null;
            for (Iva ivasOldIva : ivasOld) {
                if (!ivasNew.contains(ivasOldIva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Iva " + ivasOldIva + " since its ivatipo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Iva> attachedIvasNew = new ArrayList<Iva>();
            for (Iva ivasNewIvaToAttach : ivasNew) {
                ivasNewIvaToAttach = em.getReference(ivasNewIvaToAttach.getClass(), ivasNewIvaToAttach.getIdIva());
                attachedIvasNew.add(ivasNewIvaToAttach);
            }
            ivasNew = attachedIvasNew;
            ivatipo.setIvas(ivasNew);
            ivatipo = em.merge(ivatipo);
            for (Iva ivasNewIva : ivasNew) {
                if (!ivasOld.contains(ivasNewIva)) {
                    Ivatipo oldIvatipoOfIvasNewIva = ivasNewIva.getIvatipo();
                    ivasNewIva.setIvatipo(ivatipo);
                    ivasNewIva = em.merge(ivasNewIva);
                    if (oldIvatipoOfIvasNewIva != null && !oldIvatipoOfIvasNewIva.equals(ivatipo)) {
                        oldIvatipoOfIvasNewIva.getIvas().remove(ivasNewIva);
                        oldIvatipoOfIvasNewIva = em.merge(oldIvatipoOfIvasNewIva);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = ivatipo.getIdIva();
                if (findIvatipo(id) == null) {
                    throw new NonexistentEntityException("The ivatipo with id " + id + " no longer exists.");
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
            Ivatipo ivatipo;
            try {
                ivatipo = em.getReference(Ivatipo.class, id);
                ivatipo.getIdIva();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ivatipo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Iva> ivasOrphanCheck = ivatipo.getIvas();
            for (Iva ivasOrphanCheckIva : ivasOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ivatipo (" + ivatipo + ") cannot be destroyed since the Iva " + ivasOrphanCheckIva + " in its ivas field has a non-nullable ivatipo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(ivatipo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ivatipo> findIvatipoEntities() {
        return findIvatipoEntities(true, -1, -1);
    }

    public List<Ivatipo> findIvatipoEntities(int maxResults, int firstResult) {
        return findIvatipoEntities(false, maxResults, firstResult);
    }

    private List<Ivatipo> findIvatipoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ivatipo.class));
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

    public Ivatipo findIvatipo(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ivatipo.class, id);
        } finally {
            em.close();
        }
    }

    public int getIvatipoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ivatipo> rt = cq.from(Ivatipo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
