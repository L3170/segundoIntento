/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;


import com.ar.facturacion.dao.ICategoriaDAO;
import com.ar.facturacion.impl.CategoriaDAOImple;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Categoria;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.model.Usuario;
import com.ar.facturacion.util.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;


/**
 *
 * @author LEONARDO
 */
@ManagedBean(name = "categoriaBean")
@ApplicationScoped
public class CategoriaBean implements Serializable{

    private Categoria categoria;
    private List<Categoria> categoriaList;
    
    private String actualizarTabla;
    SessionFactory factory = null;
    Session session = null;
    private boolean skip;
    
    public CategoriaBean() {
        factory = HibernateUtil.getSessionFactory();
        session = factory.openSession();
      
    }
    //-------------------------------------
    public Categoria getCategoria(){
        return this.categoria;
    }
    //-------------------------------------
    public void setCategoria(Categoria categoria){
        this.categoria = categoria;
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
   
    //-------------------------------------
    public List<Categoria> getCategoriaList(){
        ICategoriaDAO categoriaDAO = new CategoriaDAOImple();
        categoriaList = categoriaDAO.getCategoriaList(session);
        return this.categoriaList;
    }
    //-------------------------------------
    public void setCategoriaList(List<Categoria> categoriaList){
        this.categoriaList = categoriaList;
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
    public void createCategoria() {

        try {
            ICategoriaDAO cDao = new CategoriaDAOImple();
            //obtengo el id del usuario en session y lo asigno como el creador de la categoria
            Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            categoria.setUserUpdate(us.getIdUsuario());
            
            cDao.create(categoria, session);
            RequestContext context = RequestContext.getCurrentInstance();
            categoria = new Categoria();
            //ACA DEBE SER actualizarTabla y no FRMNUEVACATEGORIA
            context.update("frmNuevaCategoria");
            context.execute("PF('_dlgNuevaCategoria').hide()");
            context.update(actualizarTabla);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            context.update("growl");
             //EL ERROR DE NO PODER HACER DOS ADD UNO DESPUES DE OTRO ES PORQUE ES LA MISMA SESION
             //TENGO QUE DESTRUIR LA SESION Y CREAR OTRA EN EL CREATE
             //PARA ELLO SACAR EL SESSION DEL CONSTRUCTOR Y ABRIRLO EN CADA FUNCIÓN QUE LO NECESITE
             //CREATE, DELETE Y UPDATE Y PARA CONSULTAR
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
 
    }
//------------------------------------------------------------------------
    public void deleteCategoria() throws IllegalOrphanException, NonexistentEntityException{
        try{
            //PUEDO MANEJAR DE NO ELIMINAR CATEGORIA SI TIENE PRODUCTOS ASOCIADOS
            // CON A TRAVES DE LAS CLASES CREADAS Y throws. LUEGO LOS MENSAJES
            //MOSTRARLOS EN EL FACESCONTEXT
        ICategoriaDAO cDao = new CategoriaDAOImple();
        cDao.delete(categoria, session);
        categoria = new Categoria();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se Elimino correctamente."));
        RequestContext.getCurrentInstance().update("growl");
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error: No se pudo Eliminar."));
        }
    }
//------------------------------------------------------------------------
    //TIENE QUE ESTAR EL HASH CODE Y EL EQUALS CREADOS EN LOS POJOS
    public boolean enabledRow(Producto p){
        if(categoria.getProductos().contains(p)){
            
            return true;
        }else{
            return false;
        }
    }
    
//------------------------------------------------------------------------
    public void prepararNuevaCategoria(){
        this.categoria = new Categoria();
    }
//------------------------------------------------------------------------    
    public void updateCategoria() throws NonexistentEntityException, Exception{
            
        try{
            ICategoriaDAO cDao = new CategoriaDAOImple();
            //obtengo el id del usuario en session y lo asigno como el creador de la categoria
            Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            categoria.setUserUpdate(us.getIdUsuario());
            
            cDao.update(categoria, session);
            RequestContext context = RequestContext.getCurrentInstance();
            categoria = new Categoria();
            
            context.update("frmEditarCategoria");
            context.execute("PF('_dlgEditarCategoria').hide()");
            context.update(actualizarTabla);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se modifico correctamente."));
            context.update("growl");
        }catch(Exception e){
         e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al modificarlo."));
             RequestContext context = RequestContext.getCurrentInstance();
            context.update("growl");
        }
    }
    
 
        
    }    
//---------------------------------------------------------------------
    
    

