/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.IPermisoDAO;
import com.ar.facturacion.impl.PermisoDAOImple;
import com.ar.facturacion.model.Permiso;
import com.ar.facturacion.model.Rol;
import com.ar.facturacion.model.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author leo
 */
@ManagedBean( name = "menuBean")
@SessionScoped
public class MenuBean implements Serializable{

    private static final Logger logger = Logger.getLogger(plantillaBean.class.getName());
  
    private Rol rol; 
    private Permiso menu;
    private List<Permiso> menus;
    private MenuModel model;
    
    
    //-----------------------------------------------------------------------------    
   @PostConstruct
    public void init(){
        this.getMenus();
        model = new DefaultMenuModel();
        this.establecerMenu();
    }
    //-----------------------------------------------------------------------------    

    public MenuBean() {
    }

    public Permiso getMenu() {
        return menu;
    }

    public void setMenu(Permiso menu) {
        this.menu = menu;
    }
    public void getMenus(){
        try{
            Usuario us = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            rol = us.getRol();
            //IPermisoDAO dao = new PermisoDAOImple();
            //menus = dao.findByRol(rol);
            menus = us.getRol().getPermisos();
           
        }catch(Exception e){
            logger.info("\n.......Error en el getMenus().......\n");
            e.printStackTrace();
        }
    }

    public void setMenus(List<Permiso> menus) {
        this.menus = menus;
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }
    
    //-----------------------------------------------------------------------------    
    public void establecerMenu(){
        try{
             for(Permiso p : menus){
                if(p.getPermiso() == null){//si no tiene permiso padre
                    if( p.getTipo().equals(Permiso.TipoEnum.submenu)){
                        //si no tiene padre y es un submenu
                        //creo el menu y recorro todos sus sub menus
                        model.addElement(menuAnidados(p));
                        //agregado && rol.getPermisos().contains(p)
                    }else if( p.getTipo().equals(Permiso.TipoEnum.menuitem) 
                            && (rol.getPermisos().indexOf(p) != -1) ){
                          DefaultMenuItem firstItem = new DefaultMenuItem(p.getNombre());
                          firstItem.setId(Integer.toString(p.getIdPermiso()));
                          firstItem.setIcon(p.getIcono());
                          firstItem.setUrl(p.getUrl());
                          model.addElement(firstItem);
                          //creo la hoja individual... es decir no tienes hijos pero tampoco padre y esta solo en la barra
                    }

                }//si es != null tiene un padre pero no se hace nada en este if eso se maneja en el segundo for
            }//primer for
        }catch(Exception e){
            logger.info("\n.......Error en el establecerMenu().......\n");
            e.printStackTrace();
        }

    }


//-----------------------------------------------------------------------------    
//-----------------------------------------------------------------------------    
public DefaultSubMenu menuAnidados(Permiso p){//y Rol rol
    DefaultSubMenu subMenu = new DefaultSubMenu(p.getNombre());
    subMenu.setId(Integer.toString(p.getIdPermiso()));
    subMenu.setIcon(p.getIcono());
    for(Permiso per: p.getPermisos()){
                
        //if(per.getRols().get(rol.getIdRol())== null){
            if(per.getTipo().equals(Permiso.TipoEnum.menuitem) && rol.getPermisos().contains(per)){// y rol.getPermisos.contains(per);
                DefaultMenuItem menuItem = new DefaultMenuItem(per.getNombre());
                menuItem.setUrl(per.getUrl());
                subMenu.addElement(menuItem);
            }else if(per.getTipo().equals(Permiso.TipoEnum.submenu) &&  rol.getPermisos().contains(per)){// y rol.getPermisos.contains(per);
                subMenu.addElement(menuAnidados(per));//y tambien le paso el rol como parametro
            }
      
    }

return subMenu;
}  
    
    
    
    
    
    
}
 