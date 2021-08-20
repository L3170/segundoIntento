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
import com.ar.facturacion.model.Modopago;
import com.ar.facturacion.model.Pago;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import com.ar.facturacion.provisorio.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leonardo
 */
public class PagoJpaController implements Serializable {

    public PagoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pago pago) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Comprador comprador = pago.getComprador();
            if (comprador != null) {
                comprador = em.getReference(comprador.getClass(), comprador.getDocNro());
                pago.setComprador(comprador);
            }
            Modopago modopago = pago.getModopago();
            if (modopago != null) {
                modopago = em.getReference(modopago.getClass(), modopago.getIdModoPago());
                pago.setModopago(modopago);
            }
            em.persist(pago);
            if (comprador != null) {
                comprador.getPagos().add(pago);
                comprador = em.merge(comprador);
            }
            if (modopago != null) {
                modopago.getPagos().add(pago);
                modopago = em.merge(modopago);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPago(pago.getIdPago()) != null) {
                throw new PreexistingEntityException("Pago " + pago + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pago pago) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pago persistentPago = em.find(Pago.class, pago.getIdPago());
            Comprador compradorOld = persistentPago.getComprador();
            Comprador compradorNew = pago.getComprador();
            Modopago modopagoOld = persistentPago.getModopago();
            Modopago modopagoNew = pago.getModopago();
            if (compradorNew != null) {
                compradorNew = em.getReference(compradorNew.getClass(), compradorNew.getDocNro());
                pago.setComprador(compradorNew);
            }
            if (modopagoNew != null) {
                modopagoNew = em.getReference(modopagoNew.getClass(), modopagoNew.getIdModoPago());
                pago.setModopago(modopagoNew);
            }
            pago = em.merge(pago);
            if (compradorOld != null && !compradorOld.equals(compradorNew)) {
                compradorOld.getPagos().remove(pago);
                compradorOld = em.merge(compradorOld);
            }
            if (compradorNew != null && !compradorNew.equals(compradorOld)) {
                compradorNew.getPagos().add(pago);
                compradorNew = em.merge(compradorNew);
            }
            if (modopagoOld != null && !modopagoOld.equals(modopagoNew)) {
                modopagoOld.getPagos().remove(pago);
                modopagoOld = em.merge(modopagoOld);
            }
            if (modopagoNew != null && !modopagoNew.equals(modopagoOld)) {
                modopagoNew.getPagos().add(pago);
                modopagoNew = em.merge(modopagoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = pago.getIdPago();
                if (findPago(id) == null) {
                    throw new NonexistentEntityException("The pago with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pago pago;
            try {
                pago = em.getReference(Pago.class, id);
                pago.getIdPago();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pago with id " + id + " no longer exists.", enfe);
            }
            Comprador comprador = pago.getComprador();
            if (comprador != null) {
                comprador.getPagos().remove(pago);
                comprador = em.merge(comprador);
            }
            Modopago modopago = pago.getModopago();
            if (modopago != null) {
                modopago.getPagos().remove(pago);
                modopago = em.merge(modopago);
            }
            em.remove(pago);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pago> findPagoEntities() {
        return findPagoEntities(true, -1, -1);
    }

    public List<Pago> findPagoEntities(int maxResults, int firstResult) {
        return findPagoEntities(false, maxResults, firstResult);
    }

    private List<Pago> findPagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pago.class));
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

    public Pago findPago(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pago.class, id);
        } finally {
            em.close();
        }
    }

    public int getPagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pago> rt = cq.from(Pago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
