/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.ICompradorDAO;
import com.ar.facturacion.impl.CompradorDAOImple;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Comprador;
import com.ar.facturacion.model.Doctipo;
import com.ar.facturacion.util.HibernateUtil;
import java.util.List;
import java.io.Serializable;
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
@ManagedBean(name = "compradorBean")
@ViewScoped
public class CompradorBean implements Serializable{
    private List<Comprador> compradorList;
    private List<Comprador> filteredCompradores;
    private List<Doctipo> tipoDocumentoList;
    private Comprador comprador;
    private String actualizarTabla;
    SessionFactory factory = null;
    Session session = null;
    //CON MANAGEDPROPERTY puedo usar las variables de un bean en otro
    
//------------------------------------------------------------------------
    //SIEMPRE INICIALIZA LAS VARIABLES QUE VAS A USAR... TODA UNA VIDA BUSCANDO EL ERROR
    // Y ERA ESO (... DEVOLVIO NULO) ADEMAS TIENEN QUE SER SERIALIZABLES Y GETTER Y SETTER
    public CompradorBean() {
        factory = HibernateUtil.getSessionFactory();
        session = factory.openSession();
       this.comprador = new Comprador();
    }
 //=========================================================================
 //=========================================================================
    
    
    
 //=========================================================================
 //=========================================================================
//------------------------------------------------------------------------    
    
//------------------------------------------------------------------------    
    public Comprador getComprador(){
        return this.comprador;
    }
//------------------------------------------------------------------------   
    public void setComprador(Comprador comprador){
        this.comprador = comprador;
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
    public List<Comprador> getCompradorList(){
        ICompradorDAO cDao = new CompradorDAOImple();
        compradorList = cDao.getCompradorList(session);
        return compradorList;
    }
//------------------------------------------------------------------------
    public void setCompradorList(List<Comprador> compradorList){
        this.compradorList = compradorList;
    }
//------------------------------------------------------------------------   
    public List<Comprador> getFilteredCompradores(){
        return this.filteredCompradores;
    }
//------------------------------------------------------------------------
    public void setFilteredCompradores(List<Comprador> filteredCompradores){
        this.filteredCompradores = filteredCompradores;
    }

//------------------------------------------------------------------------
    public void prepararNuevoComprador(){
        this.comprador = new Comprador();
    }
//------------------------------------------------------------------------
    public void create()throws PreexistingEntityException, Exception{
        
        try{
            ICompradorDAO dao = new CompradorDAOImple();
            dao.create(this.comprador, session);
            //RequestContext context = RequestContext.getCurrentInstance();
            //context.execute("PF('_dlgNuevoRol').hide()");
            //context.getCurrentInstance().update("frmMostrarRoles");
            //context.getCurrentInstance().update(actualizarTabla);
          /* 
            if(actualizarTabla == "formFactura"){
                 FacesContext contexto = FacesContext.getCurrentInstance();
                 FacturaBean factura = contexto.getApplication().evaluateExpressionGet(contexto,"#{facturaBean}", FacturaBean.class);
                 factura.getFactura().setCliente(cliente);
            
            }else{
                cliente = new Cliente();
            }
          */        
            RequestContext context = RequestContext.getCurrentInstance();
            context.update(actualizarTabla);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            
        }catch(Exception e){
           e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
        
        
        }
    
//------------------------------------------------------------------------
    public void delete() throws IllegalOrphanException, NonexistentEntityException{
        ICompradorDAO cDao = new CompradorDAOImple();
        cDao.delete(comprador,session);
        comprador = new Comprador();
    }
//------------------------------------------------------------------------
    public void update(){
        
         try{
           ICompradorDAO dao = new CompradorDAOImple();
            dao.update(comprador,session);
            RequestContext context = RequestContext.getCurrentInstance();
            //context.execute("PF('_dlgNuevoRol').hide()");
            //context.getCurrentInstance().update("frmMostrarRoles");
            context.getCurrentInstance().update(actualizarTabla);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            comprador = new Comprador();
        }catch(Exception e){
           e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
        
       // ICompradorDAO cDao = new CompradorDAOImple();
       // Dao.update(comprador,session);
       // comprador = new Comprador();
    }
    
//------------------------------------------------------------------------
    public Comprador findById(String id){
        ICompradorDAO cDao = new CompradorDAOImple();
        return cDao.findByIdComprador(id,session);
        
    }

//------------------------------------------------------------------------
    
    
//------------------------------------------------------------------------


}//CompradorBean
