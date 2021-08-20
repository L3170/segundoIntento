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
import com.ar.facturacion.model.Localidad;
import com.ar.facturacion.model.Provincia;
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
public class ProvinciaJpaController implements Serializable {

    public ProvinciaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Provincia provincia) throws PreexistingEntityException, Exception {
        if (provincia.getLocalidads() == null) {
            provincia.setLocalidads(new ArrayList<Localidad>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Localidad> attachedLocalidads = new ArrayList<Localidad>();
            for (Localidad localidadsLocalidadToAttach : provincia.getLocalidads()) {
                localidadsLocalidadToAttach = em.getReference(localidadsLocalidadToAttach.getClass(), localidadsLocalidadToAttach.getIdLocalidad());
                attachedLocalidads.add(localidadsLocalidadToAttach);
            }
            provincia.setLocalidads(attachedLocalidads);
            em.persist(provincia);
            for (Localidad localidadsLocalidad : provincia.getLocalidads()) {
                Provincia oldProvinciaOfLocalidadsLocalidad = localidadsLocalidad.getProvincia();
                localidadsLocalidad.setProvincia(provincia);
                localidadsLocalidad = em.merge(localidadsLocalidad);
                if (oldProvinciaOfLocalidadsLocalidad != null) {
                    oldProvinciaOfLocalidadsLocalidad.getLocalidads().remove(localidadsLocalidad);
                    oldProvinciaOfLocalidadsLocalidad = em.merge(oldProvinciaOfLocalidadsLocalidad);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProvincia(provincia.getIdProvincia()) != null) {
                throw new PreexistingEntityException("Provincia " + provincia + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Provincia provincia) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Provincia persistentProvincia = em.find(Provincia.class, provincia.getIdProvincia());
            List<Localidad> localidadsOld = persistentProvincia.getLocalidads();
            List<Localidad> localidadsNew = provincia.getLocalidads();
            List<String> illegalOrphanMessages = null;
            for (Localidad localidadsOldLocalidad : localidadsOld) {
                if (!localidadsNew.contains(localidadsOldLocalidad)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Localidad " + localidadsOldLocalidad + " since its provincia field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Localidad> attachedLocalidadsNew = new ArrayList<Localidad>();
            for (Localidad localidadsNewLocalidadToAttach : localidadsNew) {
                localidadsNewLocalidadToAttach = em.getReference(localidadsNewLocalidadToAttach.getClass(), localidadsNewLocalidadToAttach.getIdLocalidad());
                attachedLocalidadsNew.add(localidadsNewLocalidadToAttach);
            }
            localidadsNew = attachedLocalidadsNew;
            provincia.setLocalidads(localidadsNew);
            provincia = em.merge(provincia);
            for (Localidad localidadsNewLocalidad : localidadsNew) {
                if (!localidadsOld.contains(localidadsNewLocalidad)) {
                    Provincia oldProvinciaOfLocalidadsNewLocalidad = localidadsNewLocalidad.getProvincia();
                    localidadsNewLocalidad.setProvincia(provincia);
                    localidadsNewLocalidad = em.merge(localidadsNewLocalidad);
                    if (oldProvinciaOfLocalidadsNewLocalidad != null && !oldProvinciaOfLocalidadsNewLocalidad.equals(provincia)) {
                        oldProvinciaOfLocalidadsNewLocalidad.getLocalidads().remove(localidadsNewLocalidad);
                        oldProvinciaOfLocalidadsNewLocalidad = em.merge(oldProvinciaOfLocalidadsNewLocalidad);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = provincia.getIdProvincia();
                if (findProvincia(id) == null) {
                    throw new NonexistentEntityException("The provincia with id " + id + " no longer exists.");
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
            Provincia provincia;
            try {
                provincia = em.getReference(Provincia.class, id);
                provincia.getIdProvincia();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The provincia with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Localidad> localidadsOrphanCheck = provincia.getLocalidads();
            for (Localidad localidadsOrphanCheckLocalidad : localidadsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Provincia (" + provincia + ") cannot be destroyed since the Localidad " + localidadsOrphanCheckLocalidad + " in its localidads field has a non-nullable provincia field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(provincia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Provincia> findProvinciaEntities() {
        return findProvinciaEntities(true, -1, -1);
    }

    public List<Provincia> findProvinciaEntities(int maxResults, int firstResult) {
        return findProvinciaEntities(false, maxResults, firstResult);
    }

    private List<Provincia> findProvinciaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Provincia.class));
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

    public Provincia findProvincia(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Provincia.class, id);
        } finally {
            em.close();
        }
    }

    public int getProvinciaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Provincia> rt = cq.from(Provincia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
