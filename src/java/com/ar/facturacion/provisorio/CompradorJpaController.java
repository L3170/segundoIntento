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
import com.ar.facturacion.model.Doctipo;
import com.ar.facturacion.model.Localidad;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Comprador;
import java.util.ArrayList;
import java.util.List;
import com.ar.facturacion.model.Pago;
import com.ar.facturacion.provisorio.exceptions.IllegalOrphanException;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import com.ar.facturacion.provisorio.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leonardo
 */
public class CompradorJpaController implements Serializable {

    public CompradorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Comprador comprador) throws PreexistingEntityException, Exception {
        if (comprador.getCbtesasocs() == null) {
            comprador.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        if (comprador.getPagos() == null) {
            comprador.setPagos(new ArrayList<Pago>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doctipo doctipo = comprador.getDoctipo();
            if (doctipo != null) {
                doctipo = em.getReference(doctipo.getClass(), doctipo.getIdDoc());
                comprador.setDoctipo(doctipo);
            }
            Localidad localidad = comprador.getLocalidad();
            if (localidad != null) {
                localidad = em.getReference(localidad.getClass(), localidad.getIdLocalidad());
                comprador.setLocalidad(localidad);
            }
            List<Cbtesasoc> attachedCbtesasocs = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsCbtesasocToAttach : comprador.getCbtesasocs()) {
                cbtesasocsCbtesasocToAttach = em.getReference(cbtesasocsCbtesasocToAttach.getClass(), cbtesasocsCbtesasocToAttach.getNumero());
                attachedCbtesasocs.add(cbtesasocsCbtesasocToAttach);
            }
            comprador.setCbtesasocs(attachedCbtesasocs);
            List<Pago> attachedPagos = new ArrayList<Pago>();
            for (Pago pagosPagoToAttach : comprador.getPagos()) {
                pagosPagoToAttach = em.getReference(pagosPagoToAttach.getClass(), pagosPagoToAttach.getIdPago());
                attachedPagos.add(pagosPagoToAttach);
            }
            comprador.setPagos(attachedPagos);
            em.persist(comprador);
            if (doctipo != null) {
                doctipo.getCompradores().add(comprador);
                doctipo = em.merge(doctipo);
            }
            if (localidad != null) {
                localidad.getCompradores().add(comprador);
                localidad = em.merge(localidad);
            }
            for (Cbtesasoc cbtesasocsCbtesasoc : comprador.getCbtesasocs()) {
                Comprador oldCompradorOfCbtesasocsCbtesasoc = cbtesasocsCbtesasoc.getComprador();
                cbtesasocsCbtesasoc.setComprador(comprador);
                cbtesasocsCbtesasoc = em.merge(cbtesasocsCbtesasoc);
                if (oldCompradorOfCbtesasocsCbtesasoc != null) {
                    oldCompradorOfCbtesasocsCbtesasoc.getCbtesasocs().remove(cbtesasocsCbtesasoc);
                    oldCompradorOfCbtesasocsCbtesasoc = em.merge(oldCompradorOfCbtesasocsCbtesasoc);
                }
            }
            for (Pago pagosPago : comprador.getPagos()) {
                Comprador oldCompradorOfPagosPago = pagosPago.getComprador();
                pagosPago.setComprador(comprador);
                pagosPago = em.merge(pagosPago);
                if (oldCompradorOfPagosPago != null) {
                    oldCompradorOfPagosPago.getPagos().remove(pagosPago);
                    oldCompradorOfPagosPago = em.merge(oldCompradorOfPagosPago);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findComprador(comprador.getDocNro()) != null) {
                throw new PreexistingEntityException("Comprador " + comprador + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Comprador comprador) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Comprador persistentComprador = em.find(Comprador.class, comprador.getDocNro());
            Doctipo doctipoOld = persistentComprador.getDoctipo();
            Doctipo doctipoNew = comprador.getDoctipo();
            Localidad localidadOld = persistentComprador.getLocalidad();
            Localidad localidadNew = comprador.getLocalidad();
            List<Cbtesasoc> cbtesasocsOld = persistentComprador.getCbtesasocs();
            List<Cbtesasoc> cbtesasocsNew = comprador.getCbtesasocs();
            List<Pago> pagosOld = persistentComprador.getPagos();
            List<Pago> pagosNew = comprador.getPagos();
            List<String> illegalOrphanMessages = null;
            for (Cbtesasoc cbtesasocsOldCbtesasoc : cbtesasocsOld) {
                if (!cbtesasocsNew.contains(cbtesasocsOldCbtesasoc)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cbtesasoc " + cbtesasocsOldCbtesasoc + " since its comprador field is not nullable.");
                }
            }
            for (Pago pagosOldPago : pagosOld) {
                if (!pagosNew.contains(pagosOldPago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pago " + pagosOldPago + " since its comprador field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (doctipoNew != null) {
                doctipoNew = em.getReference(doctipoNew.getClass(), doctipoNew.getIdDoc());
                comprador.setDoctipo(doctipoNew);
            }
            if (localidadNew != null) {
                localidadNew = em.getReference(localidadNew.getClass(), localidadNew.getIdLocalidad());
                comprador.setLocalidad(localidadNew);
            }
            List<Cbtesasoc> attachedCbtesasocsNew = new ArrayList<Cbtesasoc>();
            for (Cbtesasoc cbtesasocsNewCbtesasocToAttach : cbtesasocsNew) {
                cbtesasocsNewCbtesasocToAttach = em.getReference(cbtesasocsNewCbtesasocToAttach.getClass(), cbtesasocsNewCbtesasocToAttach.getNumero());
                attachedCbtesasocsNew.add(cbtesasocsNewCbtesasocToAttach);
            }
            cbtesasocsNew = attachedCbtesasocsNew;
            comprador.setCbtesasocs(cbtesasocsNew);
            List<Pago> attachedPagosNew = new ArrayList<Pago>();
            for (Pago pagosNewPagoToAttach : pagosNew) {
                pagosNewPagoToAttach = em.getReference(pagosNewPagoToAttach.getClass(), pagosNewPagoToAttach.getIdPago());
                attachedPagosNew.add(pagosNewPagoToAttach);
            }
            pagosNew = attachedPagosNew;
            comprador.setPagos(pagosNew);
            comprador = em.merge(comprador);
            if (doctipoOld != null && !doctipoOld.equals(doctipoNew)) {
                doctipoOld.getCompradores().remove(comprador);
                doctipoOld = em.merge(doctipoOld);
            }
            if (doctipoNew != null && !doctipoNew.equals(doctipoOld)) {
                doctipoNew.getCompradores().add(comprador);
                doctipoNew = em.merge(doctipoNew);
            }
            if (localidadOld != null && !localidadOld.equals(localidadNew)) {
                localidadOld.getCompradores().remove(comprador);
                localidadOld = em.merge(localidadOld);
            }
            if (localidadNew != null && !localidadNew.equals(localidadOld)) {
                localidadNew.getCompradores().add(comprador);
                localidadNew = em.merge(localidadNew);
            }
            for (Cbtesasoc cbtesasocsNewCbtesasoc : cbtesasocsNew) {
                if (!cbtesasocsOld.contains(cbtesasocsNewCbtesasoc)) {
                    Comprador oldCompradorOfCbtesasocsNewCbtesasoc = cbtesasocsNewCbtesasoc.getComprador();
                    cbtesasocsNewCbtesasoc.setComprador(comprador);
                    cbtesasocsNewCbtesasoc = em.merge(cbtesasocsNewCbtesasoc);
                    if (oldCompradorOfCbtesasocsNewCbtesasoc != null && !oldCompradorOfCbtesasocsNewCbtesasoc.equals(comprador)) {
                        oldCompradorOfCbtesasocsNewCbtesasoc.getCbtesasocs().remove(cbtesasocsNewCbtesasoc);
                        oldCompradorOfCbtesasocsNewCbtesasoc = em.merge(oldCompradorOfCbtesasocsNewCbtesasoc);
                    }
                }
            }
            for (Pago pagosNewPago : pagosNew) {
                if (!pagosOld.contains(pagosNewPago)) {
                    Comprador oldCompradorOfPagosNewPago = pagosNewPago.getComprador();
                    pagosNewPago.setComprador(comprador);
                    pagosNewPago = em.merge(pagosNewPago);
                    if (oldCompradorOfPagosNewPago != null && !oldCompradorOfPagosNewPago.equals(comprador)) {
                        oldCompradorOfPagosNewPago.getPagos().remove(pagosNewPago);
                        oldCompradorOfPagosNewPago = em.merge(oldCompradorOfPagosNewPago);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = comprador.getDocNro();
                if (findComprador(id) == null) {
                    throw new NonexistentEntityException("The comprador with id " + id + " no longer exists.");
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
            Comprador comprador;
            try {
                comprador = em.getReference(Comprador.class, id);
                comprador.getDocNro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The comprador with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cbtesasoc> cbtesasocsOrphanCheck = comprador.getCbtesasocs();
            for (Cbtesasoc cbtesasocsOrphanCheckCbtesasoc : cbtesasocsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Comprador (" + comprador + ") cannot be destroyed since the Cbtesasoc " + cbtesasocsOrphanCheckCbtesasoc + " in its cbtesasocs field has a non-nullable comprador field.");
            }
            List<Pago> pagosOrphanCheck = comprador.getPagos();
            for (Pago pagosOrphanCheckPago : pagosOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Comprador (" + comprador + ") cannot be destroyed since the Pago " + pagosOrphanCheckPago + " in its pagos field has a non-nullable comprador field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Doctipo doctipo = comprador.getDoctipo();
            if (doctipo != null) {
                doctipo.getCompradores().remove(comprador);
                doctipo = em.merge(doctipo);
            }
            Localidad localidad = comprador.getLocalidad();
            if (localidad != null) {
                localidad.getCompradores().remove(comprador);
                localidad = em.merge(localidad);
            }
            em.remove(comprador);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Comprador> findCompradorEntities() {
        return findCompradorEntities(true, -1, -1);
    }

    public List<Comprador> findCompradorEntities(int maxResults, int firstResult) {
        return findCompradorEntities(false, maxResults, firstResult);
    }

    private List<Comprador> findCompradorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comprador.class));
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

    public Comprador findComprador(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comprador.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompradorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Comprador> rt = cq.from(Comprador.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
