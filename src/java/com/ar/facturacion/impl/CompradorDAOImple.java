/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.ICompradorDAO;
import com.ar.facturacion.util.HibernateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Comprador;
import com.ar.facturacion.model.Doctipo;
import com.ar.facturacion.model.Localidad;
import com.ar.facturacion.model.Pago;
import java.util.logging.Logger;
import javax.persistence.EntityNotFoundException;

import org.hibernate.Query;

/**
 *
 * @author LEONARDO
 */
public class CompradorDAOImple implements ICompradorDAO, Serializable{

    private static final Logger logger = Logger.getLogger(CompradorDAOImple.class.getName());

    
    
    @Override
    public List<Comprador> getCompradorList(Session session) {
        List<Comprador> compradorList = new ArrayList<Comprador>();
        try{
            compradorList = session.createQuery("from Comprador comprador").list();
                      
        }catch(Exception e){
            e.printStackTrace();
            logger.info("\n.......Error al hace el getCompradorList.......\n");
        }
        return compradorList;
    }
//-----------------------------------------------------------------------------
    @Override
    public void create(Comprador comprador, Session session) throws PreexistingEntityException {
        if(comprador.getPagos()==null){
            comprador.setPagos(new ArrayList<Pago>());
        }
        if(comprador.getCbtesasocs() == null){
            comprador.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        try{
            session.getTransaction().begin();
            Doctipo doctipo = comprador.getDoctipo();
            if(doctipo != null){
                doctipo = (Doctipo)session.get(Doctipo.class, doctipo.getIdDoc());
                comprador.setDoctipo(doctipo);
            }
            
            Localidad localidad = comprador.getLocalidad();
            if(localidad != null){
                localidad = (Localidad)session.get(Localidad.class, localidad.getIdLocalidad());
                comprador.setLocalidad(localidad);
            }
            List<Cbtesasoc> comprobantes = new ArrayList<Cbtesasoc>();
            for(Cbtesasoc comprobante : comprador.getCbtesasocs()){
                comprobante = (Cbtesasoc)session.get(Cbtesasoc.class, comprobante.getNumero());
                comprobantes.add(comprobante);
            }
            comprador.setCbtesasocs(comprobantes);
            List<Pago> pagos = new ArrayList<Pago>();
            for(Pago pago : comprador.getPagos()){
                pago = (Pago)session.get(Pago.class, pago.getIdPago());
                pagos.add(pago);
            }
            comprador.setPagos(pagos);
            session.persist(comprador);
            
            if(doctipo != null){
                doctipo.getCompradores().add(comprador);
                doctipo = (Doctipo)session.merge(doctipo);
            }
            if(localidad != null){
                localidad.getCompradores().add(comprador);
                localidad = (Localidad)session.merge(localidad);
            }
            for(Cbtesasoc comprobante : comprador.getCbtesasocs()){
                Comprador oldComprador = comprobante.getComprador();
                comprobante.setComprador(comprador);
                comprobante = (Cbtesasoc)session.merge(comprobante);
                if(oldComprador != null){
                    oldComprador.getCbtesasocs().remove(comprobante);
                    oldComprador = (Comprador)session.merge(oldComprador);
                }
            }
            
            for(Pago pago : comprador.getPagos()){
                Comprador oldComprador = pago.getComprador();
                pago.setComprador(comprador);
                pago = (Pago)session.merge(pago);
                if(oldComprador != null){
                    oldComprador.getPagos().remove(pago);
                    oldComprador = (Comprador)session.merge(oldComprador);
                }
            }
        session.getTransaction().commit();

        }catch(Exception e){
            if(findByIdComprador(comprador.getDocNro(), session) != null){
                throw new PreexistingEntityException("Comprador "+comprador.getDocNro()+" ya existe", e);
                 
            }
            throw e;
           
        }
        
    }//create
//-----------------------------------------------------------------------------
    @Override
    public void delete(Comprador comprador, Session session) {
        
        try{
            session.getTransaction().begin();
            try{
                comprador = (Comprador)session.get(Comprador.class, comprador.getDocNro());
                comprador.getDocNro();
                
            }catch(EntityNotFoundException ex){
                throw new NonexistentEntityException("El comprador no existe");
            }
            List<String> illegalOrphanMessages = null;
            
            List<Cbtesasoc> comprobantesSinAsociar = comprador.getCbtesasocs();
            for(Cbtesasoc comprobante : comprobantesSinAsociar){
                if(illegalOrphanMessages == null){
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El comprador "+comprador.getDocNro()+" no puede eliminarse ya que tiene asociado el cbte "+comprobante.getNumero());
            }
            
            List<Pago> pagosSinAsociar = comprador.getPagos();
            for(Pago pago : pagosSinAsociar){
                if(illegalOrphanMessages ==  null){
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El pago"+pago.getIdPago()+"no puede quedar sin un comprador asociado");
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Doctipo doctipo = comprador.getDoctipo();
            if(doctipo != null){
                doctipo.getCompradores().remove(comprador);
                doctipo = (Doctipo)session.merge(doctipo);
            }
            Localidad localidad = comprador.getLocalidad();
            if(localidad != null){
                localidad.getCompradores().remove(comprador);
                localidad = (Localidad)session.merge(localidad);
            }
            session.delete(comprador);
            session.getTransaction().commit();
         }catch(Exception e){
             e.printStackTrace();
        }
        
}
//-----------------------------------------------------------------------------
    @Override
    public void update(Comprador comprador, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception {
        
        try{
            session.getTransaction().begin();
            Comprador persistirComprador = (Comprador) session.get(Comprador.class, comprador.getDocNro());
            Doctipo oldDocTipo = persistirComprador.getDoctipo();
            Doctipo newDocTipo = comprador.getDoctipo();
            Localidad oldLocalidad =  persistirComprador.getLocalidad();
            Localidad newLocalidad = comprador.getLocalidad();
            
            List<Cbtesasoc> oldComprobantes = persistirComprador.getCbtesasocs();
            List<Cbtesasoc> newComprobantes = comprador.getCbtesasocs();
            List<Pago> oldPagos = persistirComprador.getPagos();
            List<Pago> newPagos = comprador.getPagos();
            List<String> illegalOrphanMessages = null;
            
            for(Cbtesasoc oldComprobante : oldComprobantes){
                if(!newComprobantes.contains(oldComprobante)){
                    if(illegalOrphanMessages == null){
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("El comprobante "+oldComprobante.getNumero()+" no puede quedar sin asociar a un comprador.");
                }
            }//for
            for(Pago oldPago : oldPagos){
                if(!newPagos.contains(oldPago)){
                    if(illegalOrphanMessages == null){
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("El pago "+oldPago.getIdPago()+" no puede quedar sin asociar a un comprador.");
                }
            }//for
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            
            if(newDocTipo != null){
                newDocTipo = (Doctipo)session.get(Doctipo.class, newDocTipo.getIdDoc());
                comprador.setDoctipo(newDocTipo);
            }
            if(newLocalidad != null){
                newLocalidad = (Localidad)session.get(Localidad.class, newLocalidad.getIdLocalidad());
                comprador.setLocalidad(newLocalidad);
            }
            List<Cbtesasoc> almacenarComprobantes = new ArrayList<Cbtesasoc>();
            for(Cbtesasoc nuevoCbte : newComprobantes){
                nuevoCbte = (Cbtesasoc)session.get(Cbtesasoc.class, nuevoCbte.getNumero());
                almacenarComprobantes.add(nuevoCbte);
            }
            newComprobantes = almacenarComprobantes;
            comprador.setCbtesasocs(newComprobantes);

            List<Pago> almacenarPagos = new ArrayList<Pago>();
            for(Pago nuevoPago : newPagos){
                nuevoPago = (Pago)session.get(Pago.class, nuevoPago.getIdPago());
                almacenarPagos.add(nuevoPago);
            }
            newPagos = almacenarPagos;
            comprador.setPagos(newPagos);
            comprador = (Comprador)session.merge(comprador);
            
           if(oldDocTipo != null && !oldDocTipo.equals(newDocTipo)){
               oldDocTipo.getCompradores().remove(comprador);
               oldDocTipo = (Doctipo)session.merge(oldDocTipo);
           }
            if(newDocTipo != null && !newDocTipo.equals(oldDocTipo)){
                newDocTipo.getCompradores().add(comprador);
                newDocTipo = (Doctipo)session.merge(newDocTipo);
            }
            
            if(oldLocalidad != null && !oldLocalidad.equals(newLocalidad)){
                oldLocalidad.getCompradores().remove(comprador);
                oldLocalidad = (Localidad)session.merge(oldLocalidad);
            }
            if(newLocalidad != null && !newLocalidad.equals(oldLocalidad)){
                newLocalidad.getCompradores().add(comprador);
                newLocalidad = (Localidad)session.merge(newLocalidad);
            }
           
            for(Cbtesasoc comprobante : newComprobantes){
                Comprador oldComprador = comprobante.getComprador();
                comprobante.setComprador(comprador);
                comprobante = (Cbtesasoc)session.merge(comprobante);
                if(oldComprador != null && !oldComprador.equals(comprador)){
                    oldComprador.getCbtesasocs().remove(comprobante);
                    oldComprador = (Comprador)session.merge(oldComprador);
                }
            }
            for(Pago pago : newPagos){
                if(!oldPagos.contains(pago)){
                    Comprador oldComprador = pago.getComprador();
                    pago.setComprador(comprador);
                    pago = (Pago)session.merge(pago);
                    if(oldComprador != null && !oldComprador.equals(comprador)){
                        oldComprador.getPagos().remove(pago);
                        oldComprador = (Comprador)session.merge(oldComprador);
                    }
                }
            }
            
           
            session.getTransaction().commit();
        }catch(Exception e){
            String msg = e.getLocalizedMessage();
            if(msg == null || msg.length() == 0){
                String id = comprador.getDocNro();
                if(findByIdComprador(comprador.getDocNro(), session)== null){
                    throw new NonexistentEntityException("El comprador con "+comprador.getDocNro()+" no existe.");
                }
            }
        }finally{
            if(session.isOpen()){
                session.close();
            }
        }
    }
//-----------------------------------------------------------------------------
    @Override
    public Comprador findByIdComprador(String id, Session session) {
        Comprador comprador = null;
        try{
            comprador = (Comprador) session.get(Comprador.class, id);
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return comprador;
    }

}//ClienteDaoImp
