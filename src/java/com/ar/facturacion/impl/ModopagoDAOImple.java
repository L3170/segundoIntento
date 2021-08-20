/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IModopagoDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Modopago;
import com.ar.facturacion.model.Pago;
import com.ar.facturacion.util.HibernateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author LEONARDO
 */
public class ModopagoDAOImple implements Serializable, IModopagoDAO{

    private static final Logger logger = Logger.getLogger(ModopagoDAOImple.class.getName());

    
    @Override
    public List<Modopago> getModopagoList() {
        SessionFactory factory = null;
        Session session = null;
        List<Modopago> modoPagos = new ArrayList<Modopago>();
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            modoPagos = session.createQuery("FROM Modopago modopago").list();
           
        }catch(Exception e){
            logger.info("\n.......Error al hace el getModopagoList.......\n");
            e.printStackTrace();
        }
        return modoPagos;
    }

    @Override
    public void create(Modopago modopago) {
        if(modopago.getPagos()==null){
            modopago.setPagos(new ArrayList<Pago>());
        }
        
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.getTransaction().begin();
            List<Pago> almacenarPagos = new ArrayList<Pago>();
            for(Pago pago : modopago.getPagos()){
                pago = (Pago)session.get(Pago.class, pago.getIdPago());
                almacenarPagos.add(pago);
            }
            modopago.setPagos(almacenarPagos);
            session.persist(modopago);
            for(Pago pago : modopago.getPagos()){
                Modopago oldModoPago = pago.getModopago();
                pago.setModopago(modopago);
                pago = (Pago)session.merge(pago);
                if(oldModoPago != null){
                    oldModoPago.getPagos().remove(pago);
                    oldModoPago = (Modopago)session.merge(oldModoPago);
                }
                
            }
            session.getTransaction().commit();
              
        }catch(Exception e){
            logger.info("\n.......Error al hace el Create ModoPago.......\n");
            e.printStackTrace();
            
        }
    }//create

    @Override
    public void delete(Modopago modopago) throws IllegalOrphanException, NonexistentEntityException {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            try{
                modopago = (Modopago)session.get(Modopago.class, modopago.getIdModoPago());
                modopago.getIdModoPago();
            }catch(EntityNotFoundException e){
                throw new NonexistentEntityException("El modo de pago "+modopago.getNombreModoPago()+" no existe");
            }
            List<String> illegalOrphanMessages = null;
            List<Pago> pagos = modopago.getPagos();
            for(Pago pagoSinAsociar : pagos){
                if(illegalOrphanMessages == null){
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El pago "+pagoSinAsociar.getCodCupon()+" no puede quedar sin un modo de pago.");
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            session.delete(modopago);
            session.getTransaction().commit();
        }finally{
            if(session != null){
                session.close();
            }
        }
    
    }

    @Override
    public void update(Modopago modopago) throws IllegalOrphanException, NonexistentEntityException, Exception {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            Modopago persistirModoPago = (Modopago)session.get(Modopago.class, modopago.getIdModoPago());
            List<Pago> oldPagos = persistirModoPago.getPagos();
            List<Pago> newPagos = modopago.getPagos();
            List<String> illegalOrphanMessages = null;
            for(Pago pagoSinAsociar : oldPagos){
                if(!newPagos.contains(pagoSinAsociar)){
                    if(illegalOrphanMessages == null){
                    illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("El pago "+pagoSinAsociar.getCodCupon()+" no puede quedar sin un modo de pago.");
                
                }
                
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            
            List<Pago> almacenarPagos = new ArrayList<Pago>();
            for(Pago pago: newPagos){
                pago = (Pago)session.get(Pago.class, pago.getIdPago());
                almacenarPagos.add(pago);
            }
            newPagos = almacenarPagos;
            modopago.setPagos(newPagos);
            //se hace un merge porque es un update y el objeto ya existe en persistencia
            modopago = (Modopago)session.merge(modopago);
            for(Pago newPago : newPagos){
                if(!oldPagos.contains(newPago)){
                    Modopago oldModoPago = newPago.getModopago();
                    newPago.setModopago(modopago);
                    newPago = (Pago)session.merge(newPago);
                    if(oldModoPago != null && !oldModoPago.equals(modopago)){
                         oldModoPago.getPagos().remove(newPago);
                        oldModoPago = (Modopago)session.merge(oldModoPago);
                    }
                }
                
            }
           
            session.getTransaction().commit();
        }catch(Exception e){
            String msg = e.getLocalizedMessage();
            if(msg == null || msg.length()==0){
                Integer id = modopago.getIdModoPago();
                if(findById(id)==null){
                    throw new NonexistentEntityException("El modo de pago con id "+id+"no existe");
                }
            }
        throw e;
        }finally{
            if(session.isOpen()){
                session.close();
            }
        }
    
    }

    @Override
    public Modopago findById(Integer id) {
    
    Modopago modopago = null;
    try{
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        modopago = (Modopago) session.get(Modopago.class, id);
        session.close();
    }catch(Exception e){
        e.printStackTrace();
    }
    return modopago;
    
    }
  

}//clase MOdopagoDAOImple
