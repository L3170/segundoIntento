/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.IModopagoDAO;
import com.ar.facturacion.impl.ModopagoDAOImple;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Modopago;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;



/**
 *
 * @author LEONARDO
 */
@ManagedBean(name = "modopagoBean")
@ViewScoped
public class ModopagoBean implements Serializable {

    /**
     * Creates a new instance of ModopagoBean
     */
    public ModopagoBean() {
    }
    
    private List<Modopago> modoPagoList;
    private Modopago modoPago;
    
    
//------------------------------------------------------------------------
    public Modopago getModoPago(){
       return modoPago;
    }
//------------------------------------------------------------------------    
    public void setModoPago(Modopago modoPago){
        this.modoPago = modoPago;
    }

//------------------------------------------------------------------------   
    public List<Modopago> getModoPagoList(){
        IModopagoDAO mDao = new ModopagoDAOImple(); 
        modoPagoList = mDao.getModopagoList();
        return modoPagoList;
    }
 //------------------------------------------------------------------------
    public void setModoPagoList(List<Modopago> modopagoList){
        this.modoPagoList = modopagoList;
    }   
//------------------------------------------------------------------------
    public void createModoPago(){
        
        try{
            IModopagoDAO dao = new ModopagoDAOImple();
            dao.create(modoPago);
            RequestContext context = RequestContext.getCurrentInstance();
            //context.execute("PF('_dlgNuevoRol').hide()");
            //context.getCurrentInstance().update("frmMostrarRoles");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.modoPago = new Modopago();
        }catch(Exception e){
           e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
       
    }
//------------------------------------------------------------------------
    public void deleteModoPago() throws IllegalOrphanException, NonexistentEntityException{
       
         try{
            IModopagoDAO dao = new ModopagoDAOImple();
            dao.delete(modoPago);
            this.modoPago = new Modopago();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se elimino correctamente."));
            
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al eliminar registro."));
        }
       
    }
//------------------------------------------------------------------------
    public void prepararNuevoModoPago(){
        this.modoPago = new Modopago();
    }
//------------------------------------------------------------------------    
    public void updateModopago() throws NonexistentEntityException, Exception{
        try{
            IModopagoDAO dao = new ModopagoDAOImple();
            dao.update(modoPago);
             RequestContext context = RequestContext.getCurrentInstance();
            //context.execute("PF('_dlgEditarRol').hide()");
            //context.getCurrentInstance().update("frmMostrarRol");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            modoPago = new Modopago();
        }catch(Exception e){
             e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
       
    }
    
//------------------------------------------------------------------------
    public Modopago findModoPagoById(int id){
        IModopagoDAO mDao = new ModopagoDAOImple();
        return mDao.findById(id);
        
    }

//------------------------------------------------------------------------
    
}
