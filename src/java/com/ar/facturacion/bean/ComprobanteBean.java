/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.ICbtesAsocDAO;
import com.ar.facturacion.dao.IProductoDAO;
import com.ar.facturacion.impl.CbtesasocDAOImple;
import com.ar.facturacion.impl.ProductoDAOImple;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Detalle;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.util.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.primefaces.context.RequestContext;


/**
 *
 * @author Leonardo
 */
@ManagedBean(name = "comprobanteBean")
@RequestScoped
public class ComprobanteBean implements Serializable{
    
    private static final Logger logger = Logger.getLogger(CbtesasocDAOImple.class.getName());
    
    private Cbtesasoc comprobante;
    private List<Cbtesasoc> comprobantes;
    private List<Cbtesasoc> cbtesNoInformados;
    private List<Cbtesasoc> filtroComprobantes;
    private List<Detalle> detalles;
    private boolean enable;
    private String docNroComprador;
    private Integer codProducto;
    
    //............................
    SessionFactory factory = null;
    Session session = null;
    //............................
//------------------------------------------------------------------------
    public ComprobanteBean() {
       factory = HibernateUtil.getSessionFactory();
       session = factory.openSession();
       session.beginTransaction();
    } 

    
//------------------------------------------------------------------------
    public List<Cbtesasoc> getComprobantes(){
        ICbtesAsocDAO dao = new CbtesasocDAOImple();
        comprobantes = dao.getCbtesAsoc(session);
        return comprobantes;
    }
//------------------------------------------------------------------------
    public List<Cbtesasoc> getFiltroComprobantes(){
        return this.filtroComprobantes;
    }
//------------------------------------------------------------------------
    public void setFiltroComprobantes(List<Cbtesasoc> filtroComprobantes){
        this.filtroComprobantes = filtroComprobantes;
    }
    
//------------------------------------------------------------------------
    public void setComprobantes(List<Cbtesasoc> comprobantes){
        this.comprobantes = comprobantes;
    }
//------------------------------------------------------------------------    
    public List<Cbtesasoc> getCbtesNoInformados(){
        ICbtesAsocDAO dao = new CbtesasocDAOImple();
        this.cbtesNoInformados = dao.getCbtesNoInformados(session);
        return this.cbtesNoInformados;
    }
    
//------------------------------------------------------------------------
    public void setCbtesNoInformados(List<Cbtesasoc> cbtesNoInformados){
        this.cbtesNoInformados = cbtesNoInformados;
    }
//------------------------------------------------------------------------    

    
    
    
    
    public Cbtesasoc getComprobante() {
        return comprobante;
    }
//------------------------------------------------------------------------
    public Integer getCodProducto(){
        return this.codProducto;
    }
    public void setCodProducto(Integer cod){
        this.codProducto = cod;
    }
//------------------------------------------------------------------------    
    public void nuevoCliente(){
        
    }
    
//------------------------------------------------------------------------        
    public String getDocNroComprador(){
        return this.docNroComprador;
    }
    public void setDocNroComprador(String docNroComprador){
        this.docNroComprador = docNroComprador;
    }
//------------------------------------------------------------------------    

    public void setComprobante(Cbtesasoc comprobante) {
        this.comprobante = comprobante;
    }
//------------------------------------------------------------------------

    public List<Detalle> getDetalles() {
        return detalles;
    }
//------------------------------------------------------------------------

    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }
//------------------------------------------------------------------------

    public boolean isEnable() {
        return enable;
    }
//------------------------------------------------------------------------
    public void enableButton(){
        this.enable = true;
    }
//------------------------------------------------------------------------   
    public void disableButton(){
        this.enable = false;
    }

//------------------------------------------------------------------------
    public void prepararNuevaFactura(){
        this.comprobante = new Cbtesasoc();
        this.enableButton();
    }
//------------------------------------------------------------------------
  public void createRol(){
        
        try{
            ICbtesAsocDAO dao = new CbtesasocDAOImple();
            dao.create(comprobante, session);
            RequestContext context = RequestContext.getCurrentInstance();
            //context.execute("PF('_dlgNuevoRol').hide()");
            //context.getCurrentInstance().update("frmMostrarRoles");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Se registro correctamente."));
            this.comprobante = new Cbtesasoc();
        }catch(Exception e){
           e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso: ", "Error al registrarlo."));
        }
       
    }

//------------------------------------------------------------------------
// public Rol findRolById(Integer id){
  //      IRolDAO rDao = new RolDAOImple();
    //    return rDao.findRolById(id);
        
    //}
    
//------------------------------------------------------------------------

//------------------------------------------------------------------------
  public void obtenerCliente(){
        try{
            ICbtesAsocDAO dao = new CbtesasocDAOImple();
            RequestContext context = RequestContext.getCurrentInstance();
            if(docNroComprador != null){
                //comprobante.set;
                comprobante.setComprador(dao.getClienteByDNI(docNroComprador, session));
               //factura.setCliente(dao.getClienteByDNI(duiCliente));
               if(comprobante.getComprador() == null){
                    docNroComprador = null;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "No se encontro el Cliente."));
                     context.getCurrentInstance().update("growlFactura");
                  //  this.factura = new Factura();
               }
               context.getCurrentInstance().update("formFactura");
               
            }
        }catch(Exception e){
           e.printStackTrace();
        }
       
    }

//------------------------------------------------------------------------
    public void obtenerProducto(){
        try{
            if(this.codProducto == null){
                return;
            }    
            RequestContext context = RequestContext.getCurrentInstance();
            
            System.out.println("OOOOOOOOOOOOOO"+codProducto+"OOOOOOOOOOOOOOOOO");
            
            IProductoDAO daoProducto = new ProductoDAOImple();
            Producto producto = null;
            producto = daoProducto.findById(codProducto, session);
            logger.info("\n......."+producto.getNomProducto()+".......\n");
            if(producto!= null){
                Detalle newDetalle = new Detalle();//un detalle va a tener un unico producto
                newDetalle.setProducto(producto);
                detalles.add(newDetalle);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "Producto agregado al detalle."));
                context.getCurrentInstance().update("growlFactura");
                }
                else{
                     FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso: ", "No se encontro el Producto."));
                     context.getCurrentInstance().update("growlFactura");
                    
                    //queda agregado el detalle en la lista de detalles, pero 
                    //tengo hay que vaciar el txt de codigo de producto para 
                    //volver a cargar otro producto a la lista por codigo...
                }
               context.getCurrentInstance().update("formFactura");
               
            
        }catch(Exception e){
           e.printStackTrace();
        }
       
    }
//------------------------------------------------------------------------
    
}
