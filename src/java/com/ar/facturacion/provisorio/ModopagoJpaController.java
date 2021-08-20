/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.provisorio;

import com.ar.facturacion.model.Modopago;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ar.facturacion.model.Pago;
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
public class ModopagoJpaController implements Serializable {

    public ModopagoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Modopago modopago) throws PreexistingEntityException, Exception {
        if (modopago.getPagos() == null) {
            modopago.setPagos(new ArrayList<Pago>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Pago> attachedPagos = new ArrayList<Pago>();
            for (Pago pagosPagoToAttach : modopago.getPagos()) {
                pagosPagoToAttach = em.getReference(pagosPagoToAttach.getClass(), pagosPagoToAttach.getIdPago());
                attachedPagos.add(pagosPagoToAttach);
            }
            modopago.setPagos(attachedPagos);
            em.persist(modopago);
            for (Pago pagosPago : modopago.getPagos()) {
                Modopago oldModopagoOfPagosPago = pagosPago.getModopago();
                pagosPago.setModopago(modopago);
                pagosPago = em.merge(pagosPago);
                if (oldModopagoOfPagosPago != null) {
                    oldModopagoOfPagosPago.getPagos().remove(pagosPago);
                    oldModopagoOfPagosPago = em.merge(oldModopagoOfPagosPago);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findModopago(modopago.getIdModoPago()) != null) {
                throw new PreexistingEntityException("Modopago " + modopago + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Modopago modopago) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Modopago persistentModopago = em.find(Modopago.class, modopago.getIdModoPago());
            List<Pago> pagosOld = persistentModopago.getPagos();
            List<Pago> pagosNew = modopago.getPagos();
            List<String> illegalOrphanMessages = null;
            for (Pago pagosOldPago : pagosOld) {
                if (!pagosNew.contains(pagosOldPago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pago " + pagosOldPago + " since its modopago field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Pago> attachedPagosNew = new ArrayList<Pago>();
            for (Pago pagosNewPagoToAttach : pagosNew) {
                pagosNewPagoToAttach = em.getReference(pagosNewPagoToAttach.getClass(), pagosNewPagoToAttach.getIdPago());
                attachedPagosNew.add(pagosNewPagoToAttach);
            }
            pagosNew = attachedPagosNew;
            modopago.setPagos(pagosNew);
            modopago = em.merge(modopago);
            for (Pago pagosNewPago : pagosNew) {
                if (!pagosOld.contains(pagosNewPago)) {
                    Modopago oldModopagoOfPagosNewPago = pagosNewPago.getModopago();
                    pagosNewPago.setModopago(modopago);
                    pagosNewPago = em.merge(pagosNewPago);
                    if (oldModopagoOfPagosNewPago != null && !oldModopagoOfPagosNewPago.equals(modopago)) {
                        oldModopagoOfPagosNewPago.getPagos().remove(pagosNewPago);
                        oldModopagoOfPagosNewPago = em.merge(oldModopagoOfPagosNewPago);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = modopago.getIdModoPago();
                if (findModopago(id) == null) {
                    throw new NonexistentEntityException("The modopago with id " + id + " no longer exists.");
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
            Modopago modopago;
            try {
                modopago = em.getReference(Modopago.class, id);
                modopago.getIdModoPago();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The modopago with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Pago> pagosOrphanCheck = modopago.getPagos();
            for (Pago pagosOrphanCheckPago : pagosOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Modopago (" + modopago + ") cannot be destroyed since the Pago " + pagosOrphanCheckPago + " in its pagos field has a non-nullable modopago field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(modopago);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Modopago> findModopagoEntities() {
        return findModopagoEntities(true, -1, -1);
    }

    public List<Modopago> findModopagoEntities(int maxResults, int firstResult) {
        return findModopagoEntities(false, maxResults, firstResult);
    }

    private List<Modopago> findModopagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Modopago.class));
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

    public Modopago findModopago(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Modopago.class, id);
        } finally {
            em.close();
        }
    }

    public int getModopagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Modopago> rt = cq.from(Modopago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
