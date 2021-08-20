/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.IPermisoDAO;
import com.ar.facturacion.impl.PermisoDAOImple;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Permiso;
import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;


/**
 *
 * @author leo
 */
@ManagedBean(name = "permisoBean")
@SessionScoped
public class permisoBean implements Serializable{

    private Permiso permiso;//permiso padre
    private List<Permiso> permisos;//permisos hijos
    FacesMessage message;
            
//------------------------------------------------------------------------
   
    public permisoBean() {
        
    }
//------------------------------------------------------------------------    
    public Permiso getPermiso(){
        return this.permiso;
    }
//------------------------------------------------------------------------    
    public void setPermiso(Permiso permiso){
        this.permiso = permiso;
    }

//------------------------------------------------------------------------ 
    
    public List<Permiso> getPermisos(){
        IPermisoDAO dao = new PermisoDAOImple();
        permisos = dao.getPermisoList();
        return permisos;
    }
//------------------------------------------------------------------------
    public void setPermisos(List<Permiso> permisos){
        this.permisos = permisos;
    }

//------------------------------------------------------------------------
    public void prepararNuevoPermiso(){
        this.permiso = new Permiso();
    }
//------------------------------------------------------------------------
    public void create(){
            
        try{
            IPermisoDAO dao = new PermisoDAOImple();
            dao.create(permiso);   
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgNuevoPermiso').hide();");
            context.getCurrentInstance().update("frmMostrarPermiso");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.permiso = new Permiso();
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
            
        }
    }
    
//------------------------------------------------------------------------
    public void delete()throws NonexistentEntityException{
        
        try{
            IPermisoDAO dao = new PermisoDAOImple();
            dao.delete(permiso);
            this.permiso = new Permiso();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se eliminó correctamente."));
            
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al eliminar registro."));
            
        }
        
       
    }
//------------------------------------------------------------------------
    public void update()throws NonexistentEntityException, Exception{
        try{
            IPermisoDAO dao = new PermisoDAOImple();
            dao.update(permiso);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgModificarPermiso').hide()");
            context.getCurrentInstance().update("frmMostrarPermiso");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.permiso = new Permiso();
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
    }
    
//------------------------------------------------------------------------
    public Permiso findById(Integer id){
        IPermisoDAO uDao = new PermisoDAOImple();
        return uDao.findById(id);
        
    }

//------------------------------------------------------------------------
    
    
    
}
