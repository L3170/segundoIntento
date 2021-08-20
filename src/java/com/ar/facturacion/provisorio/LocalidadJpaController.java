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
import com.ar.facturacion.model.Provincia;
import com.ar.facturacion.model.Comprador;
import com.ar.facturacion.model.Localidad;
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
public class LocalidadJpaController implements Serializable {

    public LocalidadJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Localidad localidad) throws PreexistingEntityException, Exception {
        if (localidad.getCompradores() == null) {
            localidad.setCompradores(new ArrayList<Comprador>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Provincia provincia = localidad.getProvincia();
            if (provincia != null) {
                provincia = em.getReference(provincia.getClass(), provincia.getIdProvincia());
                localidad.setProvincia(provincia);
            }
            List<Comprador> attachedCompradores = new ArrayList<Comprador>();
            for (Comprador compradoresCompradorToAttach : localidad.getCompradores()) {
                compradoresCompradorToAttach = em.getReference(compradoresCompradorToAttach.getClass(), compradoresCompradorToAttach.getDocNro());
                attachedCompradores.add(compradoresCompradorToAttach);
            }
            localidad.setCompradores(attachedCompradores);
            em.persist(localidad);
            if (provincia != null) {
                provincia.getLocalidads().add(localidad);
                provincia = em.merge(provincia);
            }
            for (Comprador compradoresComprador : localidad.getCompradores()) {
                Localidad oldLocalidadOfCompradoresComprador = compradoresComprador.getLocalidad();
                compradoresComprador.setLocalidad(localidad);
                compradoresComprador = em.merge(compradoresComprador);
                if (oldLocalidadOfCompradoresComprador != null) {
                    oldLocalidadOfCompradoresComprador.getCompradores().remove(compradoresComprador);
                    oldLocalidadOfCompradoresComprador = em.merge(oldLocalidadOfCompradoresComprador);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLocalidad(localidad.getIdLocalidad()) != null) {
                throw new PreexistingEntityException("Localidad " + localidad + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Localidad localidad) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Localidad persistentLocalidad = em.find(Localidad.class, localidad.getIdLocalidad());
            Provincia provinciaOld = persistentLocalidad.getProvincia();
            Provincia provinciaNew = localidad.getProvincia();
            List<Comprador> compradoresOld = persistentLocalidad.getCompradores();
            List<Comprador> compradoresNew = localidad.getCompradores();
            List<String> illegalOrphanMessages = null;
            for (Comprador compradoresOldComprador : compradoresOld) {
                if (!compradoresNew.contains(compradoresOldComprador)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comprador " + compradoresOldComprador + " since its localidad field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (provinciaNew != null) {
                provinciaNew = em.getReference(provinciaNew.getClass(), provinciaNew.getIdProvincia());
                localidad.setProvincia(provinciaNew);
            }
            List<Comprador> attachedCompradoresNew = new ArrayList<Comprador>();
            for (Comprador compradoresNewCompradorToAttach : compradoresNew) {
                compradoresNewCompradorToAttach = em.getReference(compradoresNewCompradorToAttach.getClass(), compradoresNewCompradorToAttach.getDocNro());
                attachedCompradoresNew.add(compradoresNewCompradorToAttach);
            }
            compradoresNew = attachedCompradoresNew;
            localidad.setCompradores(compradoresNew);
            localidad = em.merge(localidad);
            if (provinciaOld != null && !provinciaOld.equals(provinciaNew)) {
                provinciaOld.getLocalidads().remove(localidad);
                provinciaOld = em.merge(provinciaOld);
            }
            if (provinciaNew != null && !provinciaNew.equals(provinciaOld)) {
                provinciaNew.getLocalidads().add(localidad);
                provinciaNew = em.merge(provinciaNew);
            }
            for (Comprador compradoresNewComprador : compradoresNew) {
                if (!compradoresOld.contains(compradoresNewComprador)) {
                    Localidad oldLocalidadOfCompradoresNewComprador = compradoresNewComprador.getLocalidad();
                    compradoresNewComprador.setLocalidad(localidad);
                    compradoresNewComprador = em.merge(compradoresNewComprador);
                    if (oldLocalidadOfCompradoresNewComprador != null && !oldLocalidadOfCompradoresNewComprador.equals(localidad)) {
                        oldLocalidadOfCompradoresNewComprador.getCompradores().remove(compradoresNewComprador);
                        oldLocalidadOfCompradoresNewComprador = em.merge(oldLocalidadOfCompradoresNewComprador);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = localidad.getIdLocalidad();
                if (findLocalidad(id) == null) {
                    throw new NonexistentEntityException("The localidad with id " + id + " no longer exists.");
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
            Localidad localidad;
            try {
                localidad = em.getReference(Localidad.class, id);
                localidad.getIdLocalidad();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The localidad with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Comprador> compradoresOrphanCheck = localidad.getCompradores();
            for (Comprador compradoresOrphanCheckComprador : compradoresOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Localidad (" + localidad + ") cannot be destroyed since the Comprador " + compradoresOrphanCheckComprador + " in its compradores field has a non-nullable localidad field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Provincia provincia = localidad.getProvincia();
            if (provincia != null) {
                provincia.getLocalidads().remove(localidad);
                provincia = em.merge(provincia);
            }
            em.remove(localidad);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Localidad> findLocalidadEntities() {
        return findLocalidadEntities(true, -1, -1);
    }

    public List<Localidad> findLocalidadEntities(int maxResults, int firstResult) {
        return findLocalidadEntities(false, maxResults, firstResult);
    }

    private List<Localidad> findLocalidadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Localidad.class));
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

    public Localidad findLocalidad(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Localidad.class, id);
        } finally {
            em.close();
        }
    }

    public int getLocalidadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Localidad> rt = cq.from(Localidad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
