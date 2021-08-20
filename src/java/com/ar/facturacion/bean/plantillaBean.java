/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;


import com.ar.facturacion.model.Usuario;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;


/**
 *
 * @author leo
 */
@ManagedBean(name = "plantillaBean")
@SessionScoped
public class plantillaBean implements Serializable {
    
//-----------------------------------------------------------------------------    
    public plantillaBean() {
    }
  
  
    public void verificarSession(){
        try{
            FacesContext context = FacesContext.getCurrentInstance();
            Usuario us = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
            if(us == null){
                context.getExternalContext().redirect("./../permisos.xhtml");
            }
        
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }//verificarSession
//-----------------------------------------------------------------------------    
    
    
    
}
