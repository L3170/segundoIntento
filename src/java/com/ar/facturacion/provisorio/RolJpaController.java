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
import java.util.ArrayList;
import java.util.List;
import com.ar.facturacion.model.Usuario;
import com.ar.facturacion.provisorio.exceptions.IllegalOrphanException;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import com.ar.facturacion.provisorio.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leonardo
 */
public class RolJpaController implements Serializable {

    public RolJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Rol rol) throws PreexistingEntityException, Exception {
        if (rol.getPermisos() == null) {
            rol.setPermisos(new ArrayList<Permiso>());
        }
        if (rol.getUsuarios() == null) {
            rol.setUsuarios(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Permiso> attachedPermisos = new ArrayList<Permiso>();
            for (Permiso permisosPermisoToAttach : rol.getPermisos()) {
                permisosPermisoToAttach = em.getReference(permisosPermisoToAttach.getClass(), permisosPermisoToAttach.getIdPermiso());
                attachedPermisos.add(permisosPermisoToAttach);
            }
            rol.setPermisos(attachedPermisos);
            List<Usuario> attachedUsuarios = new ArrayList<Usuario>();
            for (Usuario usuariosUsuarioToAttach : rol.getUsuarios()) {
                usuariosUsuarioToAttach = em.getReference(usuariosUsuarioToAttach.getClass(), usuariosUsuarioToAttach.getIdUsuario());
                attachedUsuarios.add(usuariosUsuarioToAttach);
            }
            rol.setUsuarios(attachedUsuarios);
            em.persist(rol);
            for (Permiso permisosPermiso : rol.getPermisos()) {
                permisosPermiso.getRols().add(rol);
                permisosPermiso = em.merge(permisosPermiso);
            }
            for (Usuario usuariosUsuario : rol.getUsuarios()) {
                Rol oldRolOfUsuariosUsuario = usuariosUsuario.getRol();
                usuariosUsuario.setRol(rol);
                usuariosUsuario = em.merge(usuariosUsuario);
                if (oldRolOfUsuariosUsuario != null) {
                    oldRolOfUsuariosUsuario.getUsuarios().remove(usuariosUsuario);
                    oldRolOfUsuariosUsuario = em.merge(oldRolOfUsuariosUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRol(rol.getIdRol()) != null) {
                throw new PreexistingEntityException("Rol " + rol + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Rol rol) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol persistentRol = em.find(Rol.class, rol.getIdRol());
            List<Permiso> permisosOld = persistentRol.getPermisos();
            List<Permiso> permisosNew = rol.getPermisos();
            List<Usuario> usuariosOld = persistentRol.getUsuarios();
            List<Usuario> usuariosNew = rol.getUsuarios();
            List<String> illegalOrphanMessages = null;
            for (Usuario usuariosOldUsuario : usuariosOld) {
                if (!usuariosNew.contains(usuariosOldUsuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Usuario " + usuariosOldUsuario + " since its rol field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> attachedPermisosNew = new ArrayList<Permiso>();
            for (Permiso permisosNewPermisoToAttach : permisosNew) {
                permisosNewPermisoToAttach = em.getReference(permisosNewPermisoToAttach.getClass(), permisosNewPermisoToAttach.getIdPermiso());
                attachedPermisosNew.add(permisosNewPermisoToAttach);
            }
            permisosNew = attachedPermisosNew;
            rol.setPermisos(permisosNew);
            List<Usuario> attachedUsuariosNew = new ArrayList<Usuario>();
            for (Usuario usuariosNewUsuarioToAttach : usuariosNew) {
                usuariosNewUsuarioToAttach = em.getReference(usuariosNewUsuarioToAttach.getClass(), usuariosNewUsuarioToAttach.getIdUsuario());
                attachedUsuariosNew.add(usuariosNewUsuarioToAttach);
            }
            usuariosNew = attachedUsuariosNew;
            rol.setUsuarios(usuariosNew);
            rol = em.merge(rol);
            for (Permiso permisosOldPermiso : permisosOld) {
                if (!permisosNew.contains(permisosOldPermiso)) {
                    permisosOldPermiso.getRols().remove(rol);
                    permisosOldPermiso = em.merge(permisosOldPermiso);
                }
            }
            for (Permiso permisosNewPermiso : permisosNew) {
                if (!permisosOld.contains(permisosNewPermiso)) {
                    permisosNewPermiso.getRols().add(rol);
                    permisosNewPermiso = em.merge(permisosNewPermiso);
                }
            }
            for (Usuario usuariosNewUsuario : usuariosNew) {
                if (!usuariosOld.contains(usuariosNewUsuario)) {
                    Rol oldRolOfUsuariosNewUsuario = usuariosNewUsuario.getRol();
                    usuariosNewUsuario.setRol(rol);
                    usuariosNewUsuario = em.merge(usuariosNewUsuario);
                    if (oldRolOfUsuariosNewUsuario != null && !oldRolOfUsuariosNewUsuario.equals(rol)) {
                        oldRolOfUsuariosNewUsuario.getUsuarios().remove(usuariosNewUsuario);
                        oldRolOfUsuariosNewUsuario = em.merge(oldRolOfUsuariosNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = rol.getIdRol();
                if (findRol(id) == null) {
                    throw new NonexistentEntityException("The rol with id " + id + " no longer exists.");
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
            Rol rol;
            try {
                rol = em.getReference(Rol.class, id);
                rol.getIdRol();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rol with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Usuario> usuariosOrphanCheck = rol.getUsuarios();
            for (Usuario usuariosOrphanCheckUsuario : usuariosOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Rol (" + rol + ") cannot be destroyed since the Usuario " + usuariosOrphanCheckUsuario + " in its usuarios field has a non-nullable rol field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> permisos = rol.getPermisos();
            for (Permiso permisosPermiso : permisos) {
                permisosPermiso.getRols().remove(rol);
                permisosPermiso = em.merge(permisosPermiso);
            }
            em.remove(rol);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Rol> findRolEntities() {
        return findRolEntities(true, -1, -1);
    }

    public List<Rol> findRolEntities(int maxResults, int firstResult) {
        return findRolEntities(false, maxResults, firstResult);
    }

    private List<Rol> findRolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rol.class));
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

    public Rol findRol(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rol> rt = cq.from(Rol.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
