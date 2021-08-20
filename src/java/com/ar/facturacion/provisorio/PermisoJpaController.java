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
import com.ar.facturacion.model.Permiso;
import com.ar.facturacion.model.Rol;
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
public class PermisoJpaController implements Serializable {

    public PermisoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Permiso permiso) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (permiso.getRols() == null) {
            permiso.setRols(new ArrayList<Rol>());
        }
        if (permiso.getPermisos() == null) {
            permiso.setPermisos(new ArrayList<Permiso>());
        }
        List<String> illegalOrphanMessages = null;
        Permiso permisoRelOrphanCheck = permiso.getPermiso();
        if (permisoRelOrphanCheck != null) {
            Permiso oldPermisoOfPermisoRel = permisoRelOrphanCheck.getPermiso();
            if (oldPermisoOfPermisoRel != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Permiso " + permisoRelOrphanCheck + " already has an item of type Permiso whose permisoRel column cannot be null. Please make another selection for the permisoRel field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Permiso permisoRel = permiso.getPermiso();
            if (permisoRel != null) {
                permisoRel = em.getReference(permisoRel.getClass(), permisoRel.getIdPermiso());
                permiso.setPermiso(permisoRel);
            }
            List<Rol> attachedRols = new ArrayList<Rol>();
            for (Rol rolsRolToAttach : permiso.getRols()) {
                rolsRolToAttach = em.getReference(rolsRolToAttach.getClass(), rolsRolToAttach.getIdRol());
                attachedRols.add(rolsRolToAttach);
            }
            permiso.setRols(attachedRols);
            List<Permiso> attachedPermisos = new ArrayList<Permiso>();
            for (Permiso permisosPermisoToAttach : permiso.getPermisos()) {
                permisosPermisoToAttach = em.getReference(permisosPermisoToAttach.getClass(), permisosPermisoToAttach.getIdPermiso());
                attachedPermisos.add(permisosPermisoToAttach);
            }
            permiso.setPermisos(attachedPermisos);
            em.persist(permiso);
            if (permisoRel != null) {
                permisoRel.setPermiso(permiso);
                permisoRel = em.merge(permisoRel);
            }
            for (Rol rolsRol : permiso.getRols()) {
                rolsRol.getPermisos().add(permiso);
                rolsRol = em.merge(rolsRol);
            }
            for (Permiso permisosPermiso : permiso.getPermisos()) {
                Permiso oldPermisoOfPermisosPermiso = permisosPermiso.getPermiso();
                permisosPermiso.setPermiso(permiso);
                permisosPermiso = em.merge(permisosPermiso);
                if (oldPermisoOfPermisosPermiso != null) {
                    oldPermisoOfPermisosPermiso.getPermisos().remove(permisosPermiso);
                    oldPermisoOfPermisosPermiso = em.merge(oldPermisoOfPermisosPermiso);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPermiso(permiso.getIdPermiso()) != null) {
                throw new PreexistingEntityException("Permiso " + permiso + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Permiso permiso) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Permiso persistentPermiso = em.find(Permiso.class, permiso.getIdPermiso());
            Permiso permisoRelOld = persistentPermiso.getPermiso();
            Permiso permisoRelNew = permiso.getPermiso();
            List<Rol> rolsOld = persistentPermiso.getRols();
            List<Rol> rolsNew = permiso.getRols();
            List<Permiso> permisosOld = persistentPermiso.getPermisos();
            List<Permiso> permisosNew = permiso.getPermisos();
            List<String> illegalOrphanMessages = null;
            if (permisoRelOld != null && !permisoRelOld.equals(permisoRelNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Permiso " + permisoRelOld + " since its permiso field is not nullable.");
            }
            if (permisoRelNew != null && !permisoRelNew.equals(permisoRelOld)) {
                Permiso oldPermisoOfPermisoRel = permisoRelNew.getPermiso();
                if (oldPermisoOfPermisoRel != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Permiso " + permisoRelNew + " already has an item of type Permiso whose permisoRel column cannot be null. Please make another selection for the permisoRel field.");
                }
            }
            for (Permiso permisosOldPermiso : permisosOld) {
                if (!permisosNew.contains(permisosOldPermiso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Permiso " + permisosOldPermiso + " since its permiso field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (permisoRelNew != null) {
                permisoRelNew = em.getReference(permisoRelNew.getClass(), permisoRelNew.getIdPermiso());
                permiso.setPermiso(permisoRelNew);
            }
            List<Rol> attachedRolsNew = new ArrayList<Rol>();
            for (Rol rolsNewRolToAttach : rolsNew) {
                rolsNewRolToAttach = em.getReference(rolsNewRolToAttach.getClass(), rolsNewRolToAttach.getIdRol());
                attachedRolsNew.add(rolsNewRolToAttach);
            }
            rolsNew = attachedRolsNew;
            permiso.setRols(rolsNew);
            List<Permiso> attachedPermisosNew = new ArrayList<Permiso>();
            for (Permiso permisosNewPermisoToAttach : permisosNew) {
                permisosNewPermisoToAttach = em.getReference(permisosNewPermisoToAttach.getClass(), permisosNewPermisoToAttach.getIdPermiso());
                attachedPermisosNew.add(permisosNewPermisoToAttach);
            }
            permisosNew = attachedPermisosNew;
            permiso.setPermisos(permisosNew);
            permiso = em.merge(permiso);
            if (permisoRelNew != null && !permisoRelNew.equals(permisoRelOld)) {
                permisoRelNew.setPermiso(permiso);
                permisoRelNew = em.merge(permisoRelNew);
            }
            for (Rol rolsOldRol : rolsOld) {
                if (!rolsNew.contains(rolsOldRol)) {
                    rolsOldRol.getPermisos().remove(permiso);
                    rolsOldRol = em.merge(rolsOldRol);
                }
            }
            for (Rol rolsNewRol : rolsNew) {
                if (!rolsOld.contains(rolsNewRol)) {
                    rolsNewRol.getPermisos().add(permiso);
                    rolsNewRol = em.merge(rolsNewRol);
                }
            }
            for (Permiso permisosNewPermiso : permisosNew) {
                if (!permisosOld.contains(permisosNewPermiso)) {
                    Permiso oldPermisoOfPermisosNewPermiso = permisosNewPermiso.getPermiso();
                    permisosNewPermiso.setPermiso(permiso);
                    permisosNewPermiso = em.merge(permisosNewPermiso);
                    if (oldPermisoOfPermisosNewPermiso != null && !oldPermisoOfPermisosNewPermiso.equals(permiso)) {
                        oldPermisoOfPermisosNewPermiso.getPermisos().remove(permisosNewPermiso);
                        oldPermisoOfPermisosNewPermiso = em.merge(oldPermisoOfPermisosNewPermiso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = permiso.getIdPermiso();
                if (findPermiso(id) == null) {
                    throw new NonexistentEntityException("The permiso with id " + id + " no longer exists.");
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
            Permiso permiso;
            try {
                permiso = em.getReference(Permiso.class, id);
                permiso.getIdPermiso();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The permiso with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Permiso permisoOrphanCheck = permiso.getPermiso();
            if (permisoOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Permiso (" + permiso + ") cannot be destroyed since the Permiso " + permisoOrphanCheck + " in its permiso field has a non-nullable permiso field.");
            }
            List<Permiso> permisosOrphanCheck = permiso.getPermisos();
            for (Permiso permisosOrphanCheckPermiso : permisosOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Permiso (" + permiso + ") cannot be destroyed since the Permiso " + permisosOrphanCheckPermiso + " in its permisos field has a non-nullable permiso field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Rol> rols = permiso.getRols();
            for (Rol rolsRol : rols) {
                rolsRol.getPermisos().remove(permiso);
                rolsRol = em.merge(rolsRol);
            }
            em.remove(permiso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Permiso> findPermisoEntities() {
        return findPermisoEntities(true, -1, -1);
    }

    public List<Permiso> findPermisoEntities(int maxResults, int firstResult) {
        return findPermisoEntities(false, maxResults, firstResult);
    }

    private List<Permiso> findPermisoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Permiso.class));
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

    public Permiso findPermiso(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Permiso.class, id);
        } finally {
            em.close();
        }
    }

    public int getPermisoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Permiso> rt = cq.from(Permiso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
