/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;


import com.ar.facturacion.dao.IIvaDAO;
import com.ar.facturacion.dao.IProductoDAO;
import com.ar.facturacion.impl.IvaDAOImple;
import com.ar.facturacion.impl.ProductoDAOImple;
import com.ar.facturacion.model.Producto;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Iva;
import com.ar.facturacion.model.Ivatipo;
import com.ar.facturacion.model.Tipotributo;
import com.ar.facturacion.model.Tributos;
import com.ar.facturacion.util.HibernateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
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
@ManagedBean(name = "productoBean")
@ViewScoped
public class ProductoBean implements Serializable {
    
    private static final Logger logger = Logger.getLogger(ProductoDAOImple.class.getName());

    private Producto producto;
    private List<Producto> productoList;
    private List<Producto> filteredProducto;
    private List<Ivatipo> ivaTipoList = new ArrayList<Ivatipo>();
    private List<Iva> ivaList = new ArrayList<Iva>();
    private List<Tributos> tributoList = new ArrayList<Tributos>();
    private List<Tipotributo> tributoTipoList = new ArrayList<Tipotributo>();
    private String actualizarTabla;
    
    SessionFactory factory = null;
    Session session = null;
    
    //-------------------------------------------------------------------------
    public ProductoBean() {
        this.factory = HibernateUtil.getSessionFactory();
        this.session = factory.openSession();
        this.session.beginTransaction();
    }
    //-------------------------------------------------------------------------    
    
    //=========================================================================
    //A MODO DE PRUEBA VOY A HACER ESTO...
     public void seleccionDeCategoria(){
         
          
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgCategoria').hide()");
            context.execute("PF('_dlgNuevoProducto').show()");
            //context.getCurrentInstance().update("frmMostrarRoles");
            //context.getCurrentInstance().update(actualizarTabla);
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            //this.producto = new Producto();
       
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
     
         
     }
    
    //=========================================================================
     
    public List<Iva> getIvaList(){
         return this.ivaList;
    }
    public List<Tributos> getTributoList(){
       return this.tributoList; 
    }
    public List<Ivatipo> getIvaTipoList(){
         return this.ivaTipoList;
    }
    public List<Tipotributo> getTributoTipoList(){
         return this.tributoTipoList;
    }
//------------------------------------------------------------------------
    public List<Producto> getFilteredProducto(){
        return this.filteredProducto;
    }
    public void setFilteredProducto(List<Producto> filteredProducto){
        this.filteredProducto = filteredProducto;
    }
//-----------------------------------------------------------------------    
    public void setIvaList(List<Iva> ivaList){
         this.ivaList = ivaList;
    }
    public void setTributoList (List<Tributos> tributoList){
        this.tributoList = tributoList;
    }
    public void setIvaTipoList(List<Ivatipo> ivaTipoList){
         this.ivaTipoList = ivaTipoList;
    }
    public void setTributoTipoList(List<Tipotributo> tributoTipoList){
         this.tributoTipoList = tributoTipoList;
    }
    public void calcularIva(double baseImp){
         
        
     }
// CREA ENTIDADES DE IVA CON SUS RESPECTIVOS IVAS
    public void crearIvas(){
        ivaList.clear();
         for(Ivatipo ivaTipo : ivaTipoList){
             Iva iva = new Iva();
             iva.setProducto(producto);
             iva.setIvatipo(ivaTipo);
             iva.setBaseImp(0);
             iva.setDescripcion(ivaTipo.getDescripcion());
            
             ivaList.add(iva);
         }
    }     
// CREA ENTIDADES DE TRIBUTOS CON SUS RESPECTIVOS TRIBUTOS.    
    public void crearTributos(){
        tributoList.clear();
        for(Tipotributo tributos : tributoTipoList){
             Tributos tributo = new Tributos();
             tributo.setProducto(producto);
             tributo.setAlic(0);
             tributo.setBaseImp(0);
             tributo.setImporte(producto.getPrecioVenta());
             tributo.setTipotributo(tributos);
             tributoList.add(tributo);
             
         }
    }
     
    //-----------------------------------------------------------------------
    public String getActualizarTabla(){
       return this.actualizarTabla;
    }
    //-----------------------------------------------------------------------
    public void setActualizarTabla(String actualizarTabla){
       this.actualizarTabla = actualizarTabla;
   }
   //-------------------------------------
   
    public Producto getProducto(){
        return this.producto;
    }
    //-------------------------------------
    public void setProducto(Producto producto){
        this.producto = producto;
    }
    //-------------------------------------
    public List<Producto> getProductoList(){
        IProductoDAO dao = new ProductoDAOImple();
        productoList = dao.getProductoList(session);
        return this.productoList;
    }
    //-------------------------------------
    public void setProductoList(List<Producto> categoriaList){
        this.productoList = categoriaList;
    }
    //------------------------------------------------------------------------
    public void createProducto(){
        
        try{
            IProductoDAO dao = new ProductoDAOImple();
            dao.create(producto, session);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('_dlgNuevoProducto').hide()");
            //context.getCurrentInstance().update("frmMostrarRoles");
            context.getCurrentInstance().update(actualizarTabla);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.producto = new Producto();
        }catch(Exception e){
           e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
        
        
      
    }
//------------------------------------------------------------------------
    public void deleteProducto() throws IllegalOrphanException, NonexistentEntityException{
        IProductoDAO dao = new ProductoDAOImple();
         try{
            dao.delete(producto, session);
         }catch(Exception e){
            logger.info("-----------     ERROR   --------------");
            e.printStackTrace();
         }finally{
            producto = new Producto();
         }
    }
//------------------------------------------------------------------------
    public void prepararNuevoProducto(){
        this.producto = new Producto();
    }
//------------------------------------------------------------------------    
    public void updateProducto() throws NonexistentEntityException, Exception{
        IProductoDAO dao = new ProductoDAOImple();
        dao.update(producto, session);
        producto = new Producto();
        
    }
    
//---------------------------------------------------------------------
     public double getTotalIVA() {
         double totalIVA = 0;
         for(Iva iva : ivaList){
             totalIVA += iva.getImporte();
         }
        return totalIVA;
    }

    public double getTotalTributos() {
        double totalTributos = 0;
        for(Tributos tributo : tributoList){
            totalTributos += tributo.getImporte();
        }
        return totalTributos;
    }

}
