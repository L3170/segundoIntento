/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.IPagoDAO;
import com.ar.facturacion.impl.PagoDAOImple;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Pago;
import com.ar.facturacion.model.Usuario;
import com.ar.facturacion.util.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.primefaces.context.RequestContext;


/**
 *
 * @author leo
 */
@ManagedBean(name = "pagoBean")
@ViewScoped
public class pagoBean implements Serializable{

    private Pago pago;
    private List<Pago> pagos;
    FacesMessage message;
    //actualiza la tabla que llamo al dialog
    private String actualizarTabla;
    SessionFactory factory = null;
    Session session = null;
   
//------------------------------------------------------------------------
   
    public pagoBean() {
        factory = HibernateUtil.getSessionFactory();
        session = factory.openSession();
    }
//------------------------------------------------------------------------    
    public Pago getPago(){
        return this.pago;
    }
//------------------------------------------------------------------------    
    public void setPago(Pago pago){
        this.pago = pago;
    }

//------------------------------------------------------------------------ 
    
    public List<Pago> getPagos(){
        IPagoDAO dao = new PagoDAOImple();
        pagos = dao.getPagos(session);
        return pagos;
    }
//------------------------------------------------------------------------
    public void setPagos(List<Pago> pagos){
        this.pagos = pagos;
    }

//------------------------------------------------------------------------
    public String getActualizarTabla(){
        return this.actualizarTabla;
    }
//------------------------------------------------------------------------
    public void setActualizarTabla(String actualizarTabla){
         this.actualizarTabla = actualizarTabla;
    }
//------------------------------------------------------------------------
    public void prepararNuevoPago(){
        this.pago = new Pago();
    }
//------------------------------------------------------------------------
    public void createPago(){
            
        try{
            IPagoDAO dao = new PagoDAOImple();
            //obtengo el id del usuario en session y lo asigno como el creador de la categoria
            Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            pago.setUserUpdate(us.getIdUsuario());
                        
            dao.create(pago, session);   
            RequestContext context = RequestContext.getCurrentInstance();
            
            context.execute("PF('_dlgNuevoPago').hide()");
            context.update(actualizarTabla);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
           context.update("growl");
            this.pago = new Pago();
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
            
        }
    }
    
//------------------------------------------------------------------------
    public void deletePago()throws NonexistentEntityException{
        
        try{
            IPagoDAO dao = new PagoDAOImple();
            dao.delete(pago, session);
            this.pago = new Pago();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se eliminó correctamente."));
            RequestContext.getCurrentInstance().update("growl");
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al eliminar registro."));
            
        }
        
       
    }
//------------------------------------------------------------------------
    public void updatePago()throws NonexistentEntityException, Exception{
        try{
            IPagoDAO dao = new PagoDAOImple();
            //obtengo el id del usuario en session y lo asigno como el creador de la categoria
            Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            
            pago.setUserUpdate(us.getIdUsuario());
            dao.update(pago, session);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgEditarPago').hide()");
            context.update(actualizarTabla);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.pago = new Pago();
            context.update("growl");
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
             RequestContext context = RequestContext.getCurrentInstance();
            context.update("growl");
        }
    }
    
//------------------------------------------------------------------------
    public Pago findPagoById(Integer id){
        IPagoDAO uDao = new PagoDAOImple();
        return uDao.findById(id);
        
    }

//------------------------------------------------------------------------
    
    
    
}
