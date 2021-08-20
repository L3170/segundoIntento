/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.auxiliar.Encriptar;
import com.ar.facturacion.dao.IUsuarioDAO;
import com.ar.facturacion.impl.UsuarioDAOImple;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
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
 * @author LEONARDO
 */
@ManagedBean(name = "usuarioBean")
@ViewScoped
public class UsuarioBean implements Serializable {

    private List<Usuario> usuarioList;
    private Usuario usuario;
    private String actualizarTabla;
   
    SessionFactory factory = null;
    Session session = null;
    private FacesMessage message;
  
//------------------------------------------------------------------------
    
    public UsuarioBean() {
         factory = HibernateUtil.getSessionFactory();
        session = factory.openSession();
    }
//------------------------------------------------------------------------    
    public Usuario getUsuario(){
        return this.usuario;
    }
//------------------------------------------------------------------------    
    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }

//------------------------------------------------------------------------   
    public List<Usuario> getUsuarioList(){
        IUsuarioDAO uDao = new UsuarioDAOImple();
        usuarioList = uDao.getUsuarioList();
        return usuarioList;
    }
    

//------------------------------------------------------------------------
    public void setUsuarioList(List<Usuario> usuarioList){
        this.usuarioList = usuarioList;
    }
//------------------------------------------------------------------------

    public String getActualizarTabla() {
        return this.actualizarTabla;
    }

//------------------------------------------------------------------------  
    public void setActualizarTabla(String actualizarTabla) {
        this.actualizarTabla = actualizarTabla;
    }
//------------------------------------------------------------------------
    public void prepararNuevoUsuario(){
        this.usuario = new Usuario();
    }
//------------------------------------------------------------------------
    public void create(){
            
        try{
            IUsuarioDAO uDao = new UsuarioDAOImple();
            usuario.setClave(Encriptar.sha512(usuario.getClave()));
             //obtengo el id del usuario en session y lo asigno como el creador de la categoria
            Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            usuario.setUsuarioAdmin(us.getIdUsuario());
            uDao.create(usuario, session);   
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgNuevoUsuario').hide();");
            context.getCurrentInstance().update("frmMostrarUsuario");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.usuario = new Usuario();
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
            
        }
    }
    

//------------------------------------------------------------------------
    public void update()throws NonexistentEntityException, Exception{
        try{
            IUsuarioDAO uDao = new UsuarioDAOImple();
            usuario.setClave(Encriptar.sha512(usuario.getClave()));
             //obtengo el id del usuario en session y lo asigno como el creador del nuevo usuario
            Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            usuario.setUsuarioAdmin(us.getIdUsuario());
            uDao.update(usuario, session);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgEditarUsuario').hide()");
            context.getCurrentInstance().update("frmMostrarUsuario");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.usuario = new Usuario();
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
    }
//------------------------------------------------------------------------
    public void delete()throws NonexistentEntityException{
        
        try{
            IUsuarioDAO uDao = new UsuarioDAOImple();
            uDao.delete(usuario, session);
            this.usuario = new Usuario();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se eliminó correctamente."));
            
        }catch(Exception e){
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al eliminar registro."));
            
        }
        
       
    }    
//------------------------------------------------------------------------
    public Usuario findById(Integer id){
        IUsuarioDAO uDao = new UsuarioDAOImple();
        return uDao.findById(id);
        
    }

//------------------------------------------------------------------------
    
    
//------------------------------------------------------------------------

    
    
    
    
    
    
    
}
