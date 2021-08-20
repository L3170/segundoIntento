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
import com.ar.facturacion.model.Comprador;
import com.ar.facturacion.model.Doctipo;
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
public class DoctipoJpaController implements Serializable {

    public DoctipoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Doctipo doctipo) throws PreexistingEntityException, Exception {
        if (doctipo.getCompradores() == null) {
            doctipo.setCompradores(new ArrayList<Comprador>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Comprador> attachedCompradores = new ArrayList<Comprador>();
            for (Comprador compradoresCompradorToAttach : doctipo.getCompradores()) {
                compradoresCompradorToAttach = em.getReference(compradoresCompradorToAttach.getClass(), compradoresCompradorToAttach.getDocNro());
                attachedCompradores.add(compradoresCompradorToAttach);
            }
            doctipo.setCompradores(attachedCompradores);
            em.persist(doctipo);
            for (Comprador compradoresComprador : doctipo.getCompradores()) {
                Doctipo oldDoctipoOfCompradoresComprador = compradoresComprador.getDoctipo();
                compradoresComprador.setDoctipo(doctipo);
                compradoresComprador = em.merge(compradoresComprador);
                if (oldDoctipoOfCompradoresComprador != null) {
                    oldDoctipoOfCompradoresComprador.getCompradores().remove(compradoresComprador);
                    oldDoctipoOfCompradoresComprador = em.merge(oldDoctipoOfCompradoresComprador);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDoctipo(doctipo.getIdDoc()) != null) {
                throw new PreexistingEntityException("Doctipo " + doctipo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Doctipo doctipo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doctipo persistentDoctipo = em.find(Doctipo.class, doctipo.getIdDoc());
            List<Comprador> compradoresOld = persistentDoctipo.getCompradores();
            List<Comprador> compradoresNew = doctipo.getCompradores();
            List<String> illegalOrphanMessages = null;
            for (Comprador compradoresOldComprador : compradoresOld) {
                if (!compradoresNew.contains(compradoresOldComprador)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comprador " + compradoresOldComprador + " since its doctipo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Comprador> attachedCompradoresNew = new ArrayList<Comprador>();
            for (Comprador compradoresNewCompradorToAttach : compradoresNew) {
                compradoresNewCompradorToAttach = em.getReference(compradoresNewCompradorToAttach.getClass(), compradoresNewCompradorToAttach.getDocNro());
                attachedCompradoresNew.add(compradoresNewCompradorToAttach);
            }
            compradoresNew = attachedCompradoresNew;
            doctipo.setCompradores(compradoresNew);
            doctipo = em.merge(doctipo);
            for (Comprador compradoresNewComprador : compradoresNew) {
                if (!compradoresOld.contains(compradoresNewComprador)) {
                    Doctipo oldDoctipoOfCompradoresNewComprador = compradoresNewComprador.getDoctipo();
                    compradoresNewComprador.setDoctipo(doctipo);
                    compradoresNewComprador = em.merge(compradoresNewComprador);
                    if (oldDoctipoOfCompradoresNewComprador != null && !oldDoctipoOfCompradoresNewComprador.equals(doctipo)) {
                        oldDoctipoOfCompradoresNewComprador.getCompradores().remove(compradoresNewComprador);
                        oldDoctipoOfCompradoresNewComprador = em.merge(oldDoctipoOfCompradoresNewComprador);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = doctipo.getIdDoc();
                if (findDoctipo(id) == null) {
                    throw new NonexistentEntityException("The doctipo with id " + id + " no longer exists.");
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
            Doctipo doctipo;
            try {
                doctipo = em.getReference(Doctipo.class, id);
                doctipo.getIdDoc();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The doctipo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Comprador> compradoresOrphanCheck = doctipo.getCompradores();
            for (Comprador compradoresOrphanCheckComprador : compradoresOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Doctipo (" + doctipo + ") cannot be destroyed since the Comprador " + compradoresOrphanCheckComprador + " in its compradores field has a non-nullable doctipo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(doctipo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Doctipo> findDoctipoEntities() {
        return findDoctipoEntities(true, -1, -1);
    }

    public List<Doctipo> findDoctipoEntities(int maxResults, int firstResult) {
        return findDoctipoEntities(false, maxResults, firstResult);
    }

    private List<Doctipo> findDoctipoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Doctipo.class));
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

    public Doctipo findDoctipo(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doctipo.class, id);
        } finally {
            em.close();
        }
    }

    public int getDoctipoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Doctipo> rt = cq.from(Doctipo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
