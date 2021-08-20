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
import com.ar.facturacion.model.Rol;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Usuario;
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
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getCbtesasocs() == null) {
            usuario.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol rol = usuario.getRol();
            if (rol != null) {
                rol = em.getReference(rol.getClass(), rol.getIdRol());
                usuario.setRol(rol);
            }
            List<Cbtesasoc> attachedCbtesasocs = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsCbtesasocToAttach : usuario.getCbtesasocs()) {
                cbtesasocsCbtesasocToAttach = em.getReference(cbtesasocsCbtesasocToAttach.getClass(), cbtesasocsCbtesasocToAttach.getNumero());
                attachedCbtesasocs.add(cbtesasocsCbtesasocToAttach);
            }
            usuario.setCbtesasocs(attachedCbtesasocs);
            em.persist(usuario);
            if (rol != null) {
                rol.getUsuarios().add(usuario);
                rol = em.merge(rol);
            }
            for (Cbtesasoc cbtesasocsCbtesasoc : usuario.getCbtesasocs()) {
                Usuario oldUsuarioOfCbtesasocsCbtesasoc = cbtesasocsCbtesasoc.getUsuario();
                cbtesasocsCbtesasoc.setUsuario(usuario);
                cbtesasocsCbtesasoc = em.merge(cbtesasocsCbtesasoc);
                if (oldUsuarioOfCbtesasocsCbtesasoc != null) {
                    oldUsuarioOfCbtesasocsCbtesasoc.getCbtesasocs().remove(cbtesasocsCbtesasoc);
                    oldUsuarioOfCbtesasocsCbtesasoc = em.merge(oldUsuarioOfCbtesasocsCbtesasoc);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getIdUsuario()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            Rol rolOld = persistentUsuario.getRol();
            Rol rolNew = usuario.getRol();
            List<Cbtesasoc> cbtesasocsOld = persistentUsuario.getCbtesasocs();
            List<Cbtesasoc> cbtesasocsNew = usuario.getCbtesasocs();
            List<String> illegalOrphanMessages = null;
            for (Cbtesasoc cbtesasocsOldCbtesasoc : cbtesasocsOld) {
                if (!cbtesasocsNew.contains(cbtesasocsOldCbtesasoc)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cbtesasoc " + cbtesasocsOldCbtesasoc + " since its usuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (rolNew != null) {
                rolNew = em.getReference(rolNew.getClass(), rolNew.getIdRol());
                usuario.setRol(rolNew);
            }
            List<Cbtesasoc> attachedCbtesasocsNew = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsNewCbtesasocToAttach : cbtesasocsNew) {
                cbtesasocsNewCbtesasocToAttach = em.getReference(cbtesasocsNewCbtesasocToAttach.getClass(), cbtesasocsNewCbtesasocToAttach.getNumero());
                attachedCbtesasocsNew.add(cbtesasocsNewCbtesasocToAttach);
            }
            cbtesasocsNew = attachedCbtesasocsNew;
            usuario.setCbtesasocs(cbtesasocsNew);
            usuario = em.merge(usuario);
            if (rolOld != null && !rolOld.equals(rolNew)) {
                rolOld.getUsuarios().remove(usuario);
                rolOld = em.merge(rolOld);
            }
            if (rolNew != null && !rolNew.equals(rolOld)) {
                rolNew.getUsuarios().add(usuario);
                rolNew = em.merge(rolNew);
            }
            for (Cbtesasoc cbtesasocsNewCbtesasoc : cbtesasocsNew) {
                if (!cbtesasocsOld.contains(cbtesasocsNewCbtesasoc)) {
                    Usuario oldUsuarioOfCbtesasocsNewCbtesasoc = cbtesasocsNewCbtesasoc.getUsuario();
                    cbtesasocsNewCbtesasoc.setUsuario(usuario);
                    cbtesasocsNewCbtesasoc = em.merge(cbtesasocsNewCbtesasoc);
                    if (oldUsuarioOfCbtesasocsNewCbtesasoc != null && !oldUsuarioOfCbtesasocsNewCbtesasoc.equals(usuario)) {
                        oldUsuarioOfCbtesasocsNewCbtesasoc.getCbtesasocs().remove(cbtesasocsNewCbtesasoc);
                        oldUsuarioOfCbtesasocsNewCbtesasoc = em.merge(oldUsuarioOfCbtesasocsNewCbtesasoc);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cbtesasoc> cbtesasocsOrphanCheck = usuario.getCbtesasocs();
            for (Cbtesasoc cbtesasocsOrphanCheckCbtesasoc : cbtesasocsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Cbtesasoc " + cbtesasocsOrphanCheckCbtesasoc + " in its cbtesasocs field has a non-nullable usuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Rol rol = usuario.getRol();
            if (rol != null) {
                rol.getUsuarios().remove(usuario);
                rol = em.merge(rol);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
