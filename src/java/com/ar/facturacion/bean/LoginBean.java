/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.IUsuarioDAO;
import com.ar.facturacion.impl.UsuarioDAOImple;
import com.ar.facturacion.model.Usuario;
import com.ar.facturacion.util.MyUtil;
import java.awt.event.ActionEvent;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;

/**
 *
 * @author LEONARDO
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    
    private static final Logger logger = Logger.getLogger(LoginBean.class.getName());


    private Usuario usuario;
    private IUsuarioDAO dao;
    
//----------------------------------------------------------------------------    
    public LoginBean() {
        dao = new UsuarioDAOImple();
        if(usuario == null){
            usuario = new Usuario();
        }
        
    }
    
//----------------------------------------------------------------------------    
   public Usuario getUsuario(){
       return this.usuario;
   }
   public void setUsuario(Usuario usuario){
       this.usuario = usuario;
   }
    
    
    public void login(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message;
        boolean loggedIn;
        String ruta = "";
        this.usuario = this.dao.login(this.usuario);
         
        if(usuario != null) {
            loggedIn = true;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", this.usuario);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido",usuario.getNomUsuario());
            ruta = MyUtil.basePathLogin()+"/Views/Bienvenido.xhtml";
        } else {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Usuario y / o clave incorrectos.");
             if(usuario == null){
            usuario = new Usuario();
            }
        }
         
        FacesContext.getCurrentInstance().addMessage(null, message); 
        context.addCallbackParam("loggedIn", loggedIn);
        //para la funcion javaScript del template login
        context.addCallbackParam("ruta", ruta);
    }   
    
    
//----------------------------------------------------------------------------    
     public String cerrarSession(){
        this.usuario = null;
        HttpSession httpSession = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        httpSession.invalidate();
        return "/index";
    
    }
//----------------------------------------------------------------------------    
}
