/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.provisorio;

import com.ar.facturacion.model.Cbtesasoc;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ar.facturacion.model.Comprador;
import com.ar.facturacion.model.Concepto;
import com.ar.facturacion.model.Ctetipo;
import com.ar.facturacion.model.Fecaereq;
import com.ar.facturacion.model.Ptoventa;
import com.ar.facturacion.model.Responsabletipo;
import com.ar.facturacion.model.Tipomoneda;
import com.ar.facturacion.model.Usuario;
import com.ar.facturacion.model.Detalle;
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
public class CbtesasocJpaController implements Serializable {

    public CbtesasocJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cbtesasoc cbtesasoc) throws PreexistingEntityException, Exception {
        if (cbtesasoc.getDetalles() == null) {
            cbtesasoc.setDetalles(new ArrayList<Detalle>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Comprador comprador = cbtesasoc.getComprador();
            if (comprador != null) {
                comprador = em.getReference(comprador.getClass(), comprador.getDocNro());
                cbtesasoc.setComprador(comprador);
            }
            Concepto concepto = cbtesasoc.getConcepto();
            if (concepto != null) {
                concepto = em.getReference(concepto.getClass(), concepto.getIdConcepto());
                cbtesasoc.setConcepto(concepto);
            }
            Ctetipo ctetipo = cbtesasoc.getCtetipo();
            if (ctetipo != null) {
                ctetipo = em.getReference(ctetipo.getClass(), ctetipo.getId());
                cbtesasoc.setCtetipo(ctetipo);
            }
            Fecaereq fecaereq = cbtesasoc.getFecaereq();
            if (fecaereq != null) {
                fecaereq = em.getReference(fecaereq.getClass(), fecaereq.getIdFeCaereq());
                cbtesasoc.setFecaereq(fecaereq);
            }
            Ptoventa ptoventa = cbtesasoc.getPtoventa();
            if (ptoventa != null) {
                ptoventa = em.getReference(ptoventa.getClass(), ptoventa.getNumero());
                cbtesasoc.setPtoventa(ptoventa);
            }
            Responsabletipo responsabletipo = cbtesasoc.getResponsabletipo();
            if (responsabletipo != null) {
                responsabletipo = em.getReference(responsabletipo.getClass(), responsabletipo.getIdResponsable());
                cbtesasoc.setResponsabletipo(responsabletipo);
            }
            Tipomoneda tipomoneda = cbtesasoc.getTipomoneda();
            if (tipomoneda != null) {
                tipomoneda = em.getReference(tipomoneda.getClass(), tipomoneda.getIdMoneda());
                cbtesasoc.setTipomoneda(tipomoneda);
            }
            Usuario usuario = cbtesasoc.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdUsuario());
                cbtesasoc.setUsuario(usuario);
            }
            List<Detalle> attachedDetalles = new ArrayList<Detalle>();
            for (Detalle detallesDetalleToAttach : cbtesasoc.getDetalles()) {
                detallesDetalleToAttach = em.getReference(detallesDetalleToAttach.getClass(), detallesDetalleToAttach.getId());
                attachedDetalles.add(detallesDetalleToAttach);
            }
            cbtesasoc.setDetalles(attachedDetalles);
            em.persist(cbtesasoc);
            if (comprador != null) {
                comprador.getCbtesasocs().add(cbtesasoc);
                comprador = em.merge(comprador);
            }
            if (concepto != null) {
                concepto.getCbtesasocs().add(cbtesasoc);
                concepto = em.merge(concepto);
            }
            if (ctetipo != null) {
                ctetipo.getCbtesasocs().add(cbtesasoc);
                ctetipo = em.merge(ctetipo);
            }
            if (fecaereq != null) {
                fecaereq.getCbtesasocs().add(cbtesasoc);
                fecaereq = em.merge(fecaereq);
            }
            if (ptoventa != null) {
                ptoventa.getCbtesasocs().add(cbtesasoc);
                ptoventa = em.merge(ptoventa);
            }
            if (responsabletipo != null) {
                responsabletipo.getCbtesasocs().add(cbtesasoc);
                responsabletipo = em.merge(responsabletipo);
            }
            if (tipomoneda != null) {
                tipomoneda.getCbtesasocs().add(cbtesasoc);
                tipomoneda = em.merge(tipomoneda);
            }
            if (usuario != null) {
                usuario.getCbtesasocs().add(cbtesasoc);
                usuario = em.merge(usuario);
            }
            for (Detalle detallesDetalle : cbtesasoc.getDetalles()) {
                Cbtesasoc oldCbtesasocOfDetallesDetalle = detallesDetalle.getCbtesasoc();
                detallesDetalle.setCbtesasoc(cbtesasoc);
                detallesDetalle = em.merge(detallesDetalle);
                if (oldCbtesasocOfDetallesDetalle != null) {
                    oldCbtesasocOfDetallesDetalle.getDetalles().remove(detallesDetalle);
                    oldCbtesasocOfDetallesDetalle = em.merge(oldCbtesasocOfDetallesDetalle);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCbtesasoc(cbtesasoc.getNumero()) != null) {
                throw new PreexistingEntityException("Cbtesasoc " + cbtesasoc + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cbtesasoc cbtesasoc) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cbtesasoc persistentCbtesasoc = em.find(Cbtesasoc.class, cbtesasoc.getNumero());
            Comprador compradorOld = persistentCbtesasoc.getComprador();
            Comprador compradorNew = cbtesasoc.getComprador();
            Concepto conceptoOld = persistentCbtesasoc.getConcepto();
            Concepto conceptoNew = cbtesasoc.getConcepto();
            Ctetipo ctetipoOld = persistentCbtesasoc.getCtetipo();
            Ctetipo ctetipoNew = cbtesasoc.getCtetipo();
            Fecaereq fecaereqOld = persistentCbtesasoc.getFecaereq();
            Fecaereq fecaereqNew = cbtesasoc.getFecaereq();
            Ptoventa ptoventaOld = persistentCbtesasoc.getPtoventa();
            Ptoventa ptoventaNew = cbtesasoc.getPtoventa();
            Responsabletipo responsabletipoOld = persistentCbtesasoc.getResponsabletipo();
            Responsabletipo responsabletipoNew = cbtesasoc.getResponsabletipo();
            Tipomoneda tipomonedaOld = persistentCbtesasoc.getTipomoneda();
            Tipomoneda tipomonedaNew = cbtesasoc.getTipomoneda();
            Usuario usuarioOld = persistentCbtesasoc.getUsuario();
            Usuario usuarioNew = cbtesasoc.getUsuario();
            List<Detalle> detallesOld = persistentCbtesasoc.getDetalles();
            List<Detalle> detallesNew = cbtesasoc.getDetalles();
            List<String> illegalOrphanMessages = null;
            for (Detalle detallesOldDetalle : detallesOld) {
                if (!detallesNew.contains(detallesOldDetalle)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalle " + detallesOldDetalle + " since its cbtesasoc field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (compradorNew != null) {
                compradorNew = em.getReference(compradorNew.getClass(), compradorNew.getDocNro());
                cbtesasoc.setComprador(compradorNew);
            }
            if (conceptoNew != null) {
                conceptoNew = em.getReference(conceptoNew.getClass(), conceptoNew.getIdConcepto());
                cbtesasoc.setConcepto(conceptoNew);
            }
            if (ctetipoNew != null) {
                ctetipoNew = em.getReference(ctetipoNew.getClass(), ctetipoNew.getId());
                cbtesasoc.setCtetipo(ctetipoNew);
            }
            if (fecaereqNew != null) {
                fecaereqNew = em.getReference(fecaereqNew.getClass(), fecaereqNew.getIdFeCaereq());
                cbtesasoc.setFecaereq(fecaereqNew);
            }
            if (ptoventaNew != null) {
                ptoventaNew = em.getReference(ptoventaNew.getClass(), ptoventaNew.getNumero());
                cbtesasoc.setPtoventa(ptoventaNew);
            }
            if (responsabletipoNew != null) {
                responsabletipoNew = em.getReference(responsabletipoNew.getClass(), responsabletipoNew.getIdResponsable());
                cbtesasoc.setResponsabletipo(responsabletipoNew);
            }
            if (tipomonedaNew != null) {
                tipomonedaNew = em.getReference(tipomonedaNew.getClass(), tipomonedaNew.getIdMoneda());
                cbtesasoc.setTipomoneda(tipomonedaNew);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdUsuario());
                cbtesasoc.setUsuario(usuarioNew);
            }
            List<Detalle> attachedDetallesNew = new ArrayList<Detalle>();
            for (Detalle detallesNewDetalleToAttach : detallesNew) {
                detallesNewDetalleToAttach = em.getReference(detallesNewDetalleToAttach.getClass(), detallesNewDetalleToAttach.getId());
                attachedDetallesNew.add(detallesNewDetalleToAttach);
            }
            detallesNew = attachedDetallesNew;
            cbtesasoc.setDetalles(detallesNew);
            cbtesasoc = em.merge(cbtesasoc);
            if (compradorOld != null && !compradorOld.equals(compradorNew)) {
                compradorOld.getCbtesasocs().remove(cbtesasoc);
                compradorOld = em.merge(compradorOld);
            }
            if (compradorNew != null && !compradorNew.equals(compradorOld)) {
                compradorNew.getCbtesasocs().add(cbtesasoc);
                compradorNew = em.merge(compradorNew);
            }
            if (conceptoOld != null && !conceptoOld.equals(conceptoNew)) {
                conceptoOld.getCbtesasocs().remove(cbtesasoc);
                conceptoOld = em.merge(conceptoOld);
            }
            if (conceptoNew != null && !conceptoNew.equals(conceptoOld)) {
                conceptoNew.getCbtesasocs().add(cbtesasoc);
                conceptoNew = em.merge(conceptoNew);
            }
            if (ctetipoOld != null && !ctetipoOld.equals(ctetipoNew)) {
                ctetipoOld.getCbtesasocs().remove(cbtesasoc);
                ctetipoOld = em.merge(ctetipoOld);
            }
            if (ctetipoNew != null && !ctetipoNew.equals(ctetipoOld)) {
                ctetipoNew.getCbtesasocs().add(cbtesasoc);
                ctetipoNew = em.merge(ctetipoNew);
            }
            if (fecaereqOld != null && !fecaereqOld.equals(fecaereqNew)) {
                fecaereqOld.getCbtesasocs().remove(cbtesasoc);
                fecaereqOld = em.merge(fecaereqOld);
            }
            if (fecaereqNew != null && !fecaereqNew.equals(fecaereqOld)) {
                fecaereqNew.getCbtesasocs().add(cbtesasoc);
                fecaereqNew = em.merge(fecaereqNew);
            }
            if (ptoventaOld != null && !ptoventaOld.equals(ptoventaNew)) {
                ptoventaOld.getCbtesasocs().remove(cbtesasoc);
                ptoventaOld = em.merge(ptoventaOld);
            }
            if (ptoventaNew != null && !ptoventaNew.equals(ptoventaOld)) {
                ptoventaNew.getCbtesasocs().add(cbtesasoc);
                ptoventaNew = em.merge(ptoventaNew);
            }
            if (responsabletipoOld != null && !responsabletipoOld.equals(responsabletipoNew)) {
                responsabletipoOld.getCbtesasocs().remove(cbtesasoc);
                responsabletipoOld = em.merge(responsabletipoOld);
            }
            if (responsabletipoNew != null && !responsabletipoNew.equals(responsabletipoOld)) {
                responsabletipoNew.getCbtesasocs().add(cbtesasoc);
                responsabletipoNew = em.merge(responsabletipoNew);
            }
            if (tipomonedaOld != null && !tipomonedaOld.equals(tipomonedaNew)) {
                tipomonedaOld.getCbtesasocs().remove(cbtesasoc);
                tipomonedaOld = em.merge(tipomonedaOld);
            }
            if (tipomonedaNew != null && !tipomonedaNew.equals(tipomonedaOld)) {
                tipomonedaNew.getCbtesasocs().add(cbtesasoc);
                tipomonedaNew = em.merge(tipomonedaNew);
            }
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.getCbtesasocs().remove(cbtesasoc);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.getCbtesasocs().add(cbtesasoc);
                usuarioNew = em.merge(usuarioNew);
            }
            for (Detalle detallesNewDetalle : detallesNew) {
                if (!detallesOld.contains(detallesNewDetalle)) {
                    Cbtesasoc oldCbtesasocOfDetallesNewDetalle = detallesNewDetalle.getCbtesasoc();
                    detallesNewDetalle.setCbtesasoc(cbtesasoc);
                    detallesNewDetalle = em.merge(detallesNewDetalle);
                    if (oldCbtesasocOfDetallesNewDetalle != null && !oldCbtesasocOfDetallesNewDetalle.equals(cbtesasoc)) {
                        oldCbtesasocOfDetallesNewDetalle.getDetalles().remove(detallesNewDetalle);
                        oldCbtesasocOfDetallesNewDetalle = em.merge(oldCbtesasocOfDetallesNewDetalle);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = cbtesasoc.getNumero();
                if (findCbtesasoc(id) == null) {
                    throw new NonexistentEntityException("The cbtesasoc with id " + id + " no longer exists.");
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
            Cbtesasoc cbtesasoc;
            try {
                cbtesasoc = em.getReference(Cbtesasoc.class, id);
                cbtesasoc.getNumero();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cbtesasoc with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Detalle> detallesOrphanCheck = cbtesasoc.getDetalles();
            for (Detalle detallesOrphanCheckDetalle : detallesOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cbtesasoc (" + cbtesasoc + ") cannot be destroyed since the Detalle " + detallesOrphanCheckDetalle + " in its detalles field has a non-nullable cbtesasoc field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Comprador comprador = cbtesasoc.getComprador();
            if (comprador != null) {
                comprador.getCbtesasocs().remove(cbtesasoc);
                comprador = em.merge(comprador);
            }
            Concepto concepto = cbtesasoc.getConcepto();
            if (concepto != null) {
                concepto.getCbtesasocs().remove(cbtesasoc);
                concepto = em.merge(concepto);
            }
            Ctetipo ctetipo = cbtesasoc.getCtetipo();
            if (ctetipo != null) {
                ctetipo.getCbtesasocs().remove(cbtesasoc);
                ctetipo = em.merge(ctetipo);
            }
            Fecaereq fecaereq = cbtesasoc.getFecaereq();
            if (fecaereq != null) {
                fecaereq.getCbtesasocs().remove(cbtesasoc);
                fecaereq = em.merge(fecaereq);
            }
            Ptoventa ptoventa = cbtesasoc.getPtoventa();
            if (ptoventa != null) {
                ptoventa.getCbtesasocs().remove(cbtesasoc);
                ptoventa = em.merge(ptoventa);
            }
            Responsabletipo responsabletipo = cbtesasoc.getResponsabletipo();
            if (responsabletipo != null) {
                responsabletipo.getCbtesasocs().remove(cbtesasoc);
                responsabletipo = em.merge(responsabletipo);
            }
            Tipomoneda tipomoneda = cbtesasoc.getTipomoneda();
            if (tipomoneda != null) {
                tipomoneda.getCbtesasocs().remove(cbtesasoc);
                tipomoneda = em.merge(tipomoneda);
            }
            Usuario usuario = cbtesasoc.getUsuario();
            if (usuario != null) {
                usuario.getCbtesasocs().remove(cbtesasoc);
                usuario = em.merge(usuario);
            }
            em.remove(cbtesasoc);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cbtesasoc> findCbtesasocEntities() {
        return findCbtesasocEntities(true, -1, -1);
    }

    public List<Cbtesasoc> findCbtesasocEntities(int maxResults, int firstResult) {
        return findCbtesasocEntities(false, maxResults, firstResult);
    }

    private List<Cbtesasoc> findCbtesasocEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cbtesasoc.class));
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

    public Cbtesasoc findCbtesasoc(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cbtesasoc.class, id);
        } finally {
            em.close();
        }
    }

    public int getCbtesasocCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cbtesasoc> rt = cq.from(Cbtesasoc.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
