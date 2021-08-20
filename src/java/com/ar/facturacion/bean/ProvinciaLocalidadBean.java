/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;


import com.ar.facturacion.dao.ILocalidadDAO;
import com.ar.facturacion.impl.LocalidadDAOImple;
import com.ar.facturacion.model.Localidad;

import com.ar.facturacion.dao.IProvinciaDAO;
import com.ar.facturacion.impl.ProvinciaDAOImple;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Provincia;
import com.ar.facturacion.model.Usuario;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.util.HibernateUtil;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.primefaces.context.RequestContext;



@ManagedBean(name= "provinciaLocalidadBean")
@SessionScoped
public class ProvinciaLocalidadBean implements Serializable {
    private static final Logger log = Logger.getLogger(ProvinciaLocalidadBean.class.getName());
    
    private List<Provincia> provinciaList;
    private List<Localidad> localidadList;
    private List<Provincia> filtroProvincias;
    private List<Localidad> filtroLocalidades;
    private String actualizarTabla;
    
    private Provincia provincia;
    private Localidad localidad;

    SessionFactory factory = null; 
    Session session = null;
    
//------------------------------------------------------------------------
    
    public ProvinciaLocalidadBean() {
        this.factory = HibernateUtil.getSessionFactory();
        this.session = factory.openSession();
        this.session.beginTransaction();
    
    }
//------------------------------------------------------------------------    
    public Provincia getProvincia(){
       return provincia;
    }
//------------------------------------------------------------------------    
    public void setProvincia(Provincia provincia){
        this.provincia = provincia;
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
    public List<Provincia> getProvinciaList(){
        IProvinciaDAO pDao = new ProvinciaDAOImple(); 
        provinciaList = pDao.getProvinciaList(session);
        return provinciaList;
    }
 //------------------------------------------------------------------------
    public void setProvinciaList(List<Provincia> provinciaList){
        this.provinciaList = provinciaList;
    }   
//------------------------------------------------------------------------
    public List<Provincia> getFiltroProvincias() {
        return filtroProvincias;
    }
//------------------------------------------------------------------------
    public void setFiltroProvincias(List<Provincia> filtroProvincias) {
        this.filtroProvincias = filtroProvincias;
    }
//------------------------------------------------------------------------
    public List<Localidad> getFiltroLocalidades() {
        return filtroLocalidades;
    }
//------------------------------------------------------------------------
    public void setFiltroLocalidades(List<Localidad> filtroLocalidades) {
        this.filtroLocalidades = filtroLocalidades;
    }
//------------------------------------------------------------------------
    public void createProvincia(){
        try{
        IProvinciaDAO dao = new ProvinciaDAOImple();
        dao.create(provincia, session);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('_dlgNuevaProvincia').hide()");
        context.update("frmMostrarProvincia");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
        this.provincia = new Provincia();
        
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
        
        }
//------------------------------------------------------------------------
    public void deleteProvincia(){
        try{
        IProvinciaDAO dao = new ProvinciaDAOImple();
        dao.delete(provincia, session);
        provincia = new Provincia();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ","Se elimino correctamente."));
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al eliminar registro."));
        }
    }
//------------------------------------------------------------------------
    public void prepararNuevaProvincia(){
        this.provincia = new Provincia();
    }
//------------------------------------------------------------------------    
    public void updateProvincia(){
        try{
        IProvinciaDAO dao = new ProvinciaDAOImple();
        dao.update(provincia, session);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('_dlgEditarProvincia').hide()");
        context.update("frmMostrarProvincia");
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ","Se actualizo correctamente."));
         provincia = new Provincia();
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al actualizar registro."));
        }
    }
    
//------------------------------------------------------------------------
    public Provincia findProvinciaById(String id){
        IProvinciaDAO cDao = new ProvinciaDAOImple();
        return cDao.findByIdProvincia(id, session);
        
    }

//------------------------------------------------------------------------
    public List<Localidad> getLocalidadOfProvincia(){
            ILocalidadDAO lDao = new LocalidadDAOImple();
            return lDao.findByProvincia(provincia);
    }

//------------------------------------------------------------------------
//------------------------------------------------------------------------
    public void prepararNuevaLocalidad(){
        this.localidad = new Localidad();
        this.prepararNuevaProvincia();
    }
//------------------------------------------------------------------------

    public Localidad getLocalidad(){
        return this.localidad;
    }
    
//------------------------------------------------------------------------
    public void setLocalidad(Localidad localidad){
        this.localidad = localidad;
    }

//------------------------------------------------------------------------
    public List<Localidad> getLocalidadList(){
        ILocalidadDAO lDao = new LocalidadDAOImple(); 
        localidadList = lDao.getLocalidadList(session);
        return localidadList;
    }
//------------------------------------------------------------------------
    public void setLocalidadList(List<Localidad> localidadList){
        this.localidadList = localidadList;
    }
//------------------------------------------------------------------------
    public void createLocalidad() throws Exception{
        ILocalidadDAO dao = new LocalidadDAOImple();
        try{
        Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
        localidad.setUserUpdate(us.getIdUsuario());
        
        dao.create(localidad, session);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('_dlgNuevaLocalidad').hide()");
        context.update(actualizarTabla);        
        //context.update("formMostrarLocalidad");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
        
        localidad = new Localidad();
        }catch(Exception e){
            e.printStackTrace();
             log.info("\n.......Error al hace el createLocalidad.......\n");
             
        }
        
        
    }
//------------------------------------------------------------------------
    public void deleteLocalidad() throws NonexistentEntityException, IllegalOrphanException{
        ILocalidadDAO dao = new LocalidadDAOImple();
        Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
        localidad.setUserUpdate(us.getIdUsuario());
        dao.delete(localidad, session);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('_dlgEliminarLocalidad').hide()");
        context.update(actualizarTabla);        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se elimino correctamente."));
        
        localidad = new Localidad();
    }
//------------------------------------------------------------------------    
    public void updateLocalidad() throws Exception{
        ILocalidadDAO dao = new LocalidadDAOImple();
        Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
        localidad.setUserUpdate(us.getIdUsuario());
        dao.update(localidad, session);
       
        RequestContext context = RequestContext.getCurrentInstance();
         context.execute("PF('_dlgEditarLocalidad').hide()");
        context.update(actualizarTabla);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se modifico correctamente."));
        localidad = new Localidad();
        
    }
//------------------------------------------------------------------------
    
}//ProvinciaLocalidadBean
