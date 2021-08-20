/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IPagoDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Comprador;
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
public class PagoDAOImple implements IPagoDAO, Serializable{

    private static final Logger logger = Logger.getLogger(PagoDAOImple.class.getName());

    @Override
    public List<Pago> getPagos(Session session) {
        List<Pago> pagos = new ArrayList<Pago>();
        
        try{
           
            pagos = session.createQuery("FROM Pago pago").list();
            
        }catch(Exception e){
            
             logger.info("\n\n---------Error al Listar los Pagos---------\n\n");
            e.printStackTrace();
        }
    return pagos;
    }

    @Override
    public void create(Pago pago, Session session)throws PreexistingEntityException, Exception {
        
        
        try{
        
        
        session.getTransaction().begin();

        Comprador comprador = pago.getComprador();
        if(comprador != null){
            comprador = (Comprador)session.get(Comprador.class, comprador.getDocNro());
            pago.setComprador(comprador);
        }
        Modopago modoPago = pago.getModopago();
        if(modoPago != null){
            modoPago =(Modopago) session.get(modoPago.getClass(), modoPago.getIdModoPago());
            pago.setModopago(modoPago);
        }
        session.persist(pago);
        
        if(comprador != null){
            comprador.getPagos().add(pago);
            comprador = (Comprador)session.merge(comprador);
        }
        if(modoPago != null){
            modoPago.getPagos().add(pago);
            modoPago = (Modopago) session.merge(modoPago);
        }
        session.getTransaction().commit();
        }catch(Exception e){
         logger.info("\n\n---------Error en el Create de PagoDAOImple---------\n\n");
         e.printStackTrace();
        }


    }//create

    @Override
    public void delete(Pago pago, Session session) throws IllegalOrphanException, NonexistentEntityException {
       
        try{
            session.getTransaction().begin();
                try{
                    pago =(Pago) session.get(Pago.class, pago.getIdPago());
                    pago.getIdPago();
                }catch(EntityNotFoundException enfe){
                    throw new NonexistentEntityException("the pago with id "+ pago.getIdPago() +"no longer exists.", enfe);
                }
            Comprador comprador = pago.getComprador();
            if(comprador != null){
                comprador.getPagos().remove(pago);
                comprador = (Comprador) session.merge(comprador);
            }
            Modopago modoPago = pago.getModopago();
            if(modoPago != null){
                modoPago.getPagos().remove(pago);
                modoPago = (Modopago) session.merge(modoPago);
            }
            session.delete(pago);
            session.getTransaction().commit();
        }catch(Exception e){
        logger.info("\n\n---------Error en el Delete de PagoDAOImple---------\n\n");
         e.printStackTrace();
        }
    
    }

    @Override
    public void update(Pago pago, Session session) throws IllegalOrphanException, NonexistentEntityException, Exception {
       
        try{
            session.getTransaction().begin();
            Pago oldPago =(Pago) session.get(Pago.class, pago.getIdPago());
            Comprador oldComprador = oldPago.getComprador();
            Comprador newComprador = pago.getComprador();
            Modopago oldModoPago = oldPago.getModopago();
            Modopago newModoPago = pago.getModopago();
            if(newComprador != null){
                newComprador = (Comprador)session.get(Comprador.class, newComprador.getDocNro());
                pago.setComprador(newComprador);
            }
            if(newModoPago != null){
                newModoPago = (Modopago)session.get(Modopago.class, newModoPago.getIdModoPago());
                pago.setModopago(newModoPago);
            }
            pago = (Pago)session.merge(pago);
            if(oldComprador != null && !oldComprador.equals(newComprador)){
                oldComprador.getPagos().remove(pago);
                oldComprador = (Comprador) session.merge(oldComprador);
            }
            if(newComprador != null && !newComprador.equals(oldComprador)){
                newComprador.getPagos().add(pago);
                newComprador = (Comprador)session.merge(newComprador);
            }
            
            if(oldModoPago != null && !oldModoPago.equals(newModoPago)){
                oldModoPago.getPagos().remove(pago);
                oldModoPago = (Modopago)session.merge(oldModoPago);
            }
            if(newModoPago != null && !newModoPago.equals(oldModoPago)){
                newModoPago.getPagos().add(pago);
                newModoPago = (Modopago)session.merge(newModoPago);
            }
            session.getTransaction().commit();
            
        }catch(Exception e){
            logger.info("\n\n---------Error en el Update de PagoDAOImple---------\n\n");
            e.printStackTrace();
        }
    
    }//update

    @Override
    public Pago findById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Pago> findByCliente(Comprador comprador) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
}
