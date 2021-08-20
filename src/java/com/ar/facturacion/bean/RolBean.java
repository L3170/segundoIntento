/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;



import com.ar.facturacion.dao.IRolDAO;
import com.ar.facturacion.impl.RolDAOImple;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Rol;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import com.ar.facturacion.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
@ManagedBean(name = "rolBean")
@ApplicationScoped
public class RolBean implements Serializable{
    
     SessionFactory factory = null; 
     Session session = null;
     
    private List<Rol> rolList;
    private Rol rol;
    private boolean skip;
    private String actualizarTabla;
//------------------------------------------------------------------------
       
    public RolBean() {
        this.factory = HibernateUtil.getSessionFactory();
        this.session = factory.openSession();
    }

//------------------------------------------------------------------------    
    public Rol getRol(){
        return this.rol;
    }
//------------------------------------------------------------------------    
    public void setRol(Rol rol){
        this.rol = rol;
    }

//------------------------------------------------------------------------   
   public void setActualizarTabla(String actualizarTabla){
       this.actualizarTabla = actualizarTabla;
   }
   
    //-------------------------------------
   public String getActualizarTabla(){
       return this.actualizarTabla;
   }
    //-------------------------------------
    
    
    public List<Rol> getRolList(){
        IRolDAO rDao = new RolDAOImple();
        rolList = rDao.getRolList(session);
        return rolList;
    }
    
//------------------------------------------------------------------------
    public void setRolList(List<Rol> rolList){
        this.rolList = rolList;
    }

//------------------------------------------------------------------------
    public void prepararNuevoRol(){
        this.rol = new Rol();
    }
//------------------------------------------------------------------------    
     public boolean isSkip() {
        return skip;
    }
 
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
    public String onFlowProcess(FlowEvent event) {
        if(skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }
//------------------------------------------------------------------------
    public void createRol(){
        
        try{
            IRolDAO dao = new RolDAOImple();
            dao.create(rol);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgNuevoRol').hide()");
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.rol = new Rol();
        }catch(Exception e){
           e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
       
    }
    
//------------------------------------------------------------------------
    public void deleteRol()throws NonexistentEntityException{
        
         try{
            IRolDAO dao = new RolDAOImple();
            dao.delete(rol);
            this.rol = new Rol();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se elimino correctamente."));
            
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al eliminar registro."));
        }
       
    }
//------------------------------------------------------------------------
    public void updateRol()throws NonexistentEntityException, Exception{
           try{
            IRolDAO dao = new RolDAOImple();
            dao.update(rol);
             RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgEditarRol').hide()");
            context.getCurrentInstance().update("frmMostrarRol");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            rol = new Rol();
        }catch(Exception e){
             e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
        
        
    }
    
//------------------------------------------------------------------------
    public Rol findRolById(Integer id){
        IRolDAO rDao = new RolDAOImple();
        return rDao.findRolById(id);
        
    }

//------------------------------------------------------------------------
    




    
}
