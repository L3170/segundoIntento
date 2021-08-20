/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.ICbtesAsocDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Comprador;
import com.ar.facturacion.model.Concepto;
import com.ar.facturacion.model.Ctetipo;
import com.ar.facturacion.model.Detalle;
import com.ar.facturacion.model.Fecaereq;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.model.Ptoventa;
import com.ar.facturacion.model.Responsabletipo;
import com.ar.facturacion.model.Tipomoneda;
import com.ar.facturacion.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Session;


/**
 *
 * @author Leonardo
 */
public class CbtesasocDAOImple implements ICbtesAsocDAO{

    private static final Logger logger = Logger.getLogger(CbtesasocDAOImple.class.getName());
    
    @Override
    public List<Cbtesasoc> getCbtesAsoc(Session session) {
        
        List<Cbtesasoc> cbtesAsocList = new ArrayList<Cbtesasoc>();
        try{
            cbtesAsocList = session.createQuery("from Cbtesasoc cbtesasoc").list();
           
        }catch(Exception e){
            logger.info("\n\n --------------Error al hacer el getCbtesasocList en CbesasocDAOImple-----");
            e.printStackTrace();
        }
        return cbtesAsocList;
    }
    @Override
    public void update(Cbtesasoc comprobante, Session session){
        
        try{
            Cbtesasoc almacenarComprobante = (Cbtesasoc)session.get(Cbtesasoc.class, comprobante.getNumero());
            Comprador oldComprador = almacenarComprobante.getComprador();
            Comprador newComprador = comprobante.getComprador();
            Concepto oldConcepto = almacenarComprobante.getConcepto();
            Concepto newConcepto = comprobante.getConcepto();
            Ctetipo oldCtetipo = almacenarComprobante.getCtetipo();
            Ctetipo newCtetipo = comprobante.getCtetipo();
            Fecaereq oldFecaereq = almacenarComprobante.getFecaereq();
            Fecaereq newFecaereq = comprobante.getFecaereq();
            Ptoventa oldPtoVenta = almacenarComprobante.getPtoventa();
            Ptoventa newPtoVenta = comprobante.getPtoventa();
            Responsabletipo oldResponsableTipo = almacenarComprobante.getResponsabletipo();
            Responsabletipo newResponsableTipo = comprobante.getResponsabletipo();
            Tipomoneda oldTipoMoneda = almacenarComprobante.getTipomoneda();
            Tipomoneda newTipoMoneda = comprobante.getTipomoneda();
            Usuario oldUsuario = almacenarComprobante.getUsuario();
            Usuario newUsuario = comprobante.getUsuario();
            List<Detalle> oldDetalles = almacenarComprobante.getDetalles();
            List<Detalle> newDetalles = comprobante.getDetalles();
            List<String> illegalOrphanMessages = null;
            for(Detalle oldDetalle : oldDetalles){
                if(!newDetalles.contains(oldDetalle)){
                    if(illegalOrphanMessages == null){
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("El detalle "+oldDetalle.getId()+"no puede quedar sin un comprobante asociado");
                }
            }//for
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if(newComprador != null){
                newComprador = (Comprador)session.get(Comprador.class, newComprador.getDocNro());
                comprobante.setComprador(newComprador);
            }
            
            if(newConcepto != null){
                newConcepto = (Concepto)session.get(Concepto.class, newConcepto.getIdConcepto());
                comprobante.setConcepto(newConcepto);
            }
            if(newCtetipo != null){
                newCtetipo = (Ctetipo)session.get(Ctetipo.class, newCtetipo.getId());
                comprobante.setCtetipo(newCtetipo);
            }
            if(newFecaereq != null){
                newFecaereq = (Fecaereq)session.get(Fecaereq.class, newFecaereq.getIdFeCaereq());
                comprobante.setFecaereq(newFecaereq);
            }
            if(newPtoVenta != null){
                newPtoVenta = (Ptoventa)session.get(Ptoventa.class, newPtoVenta.getNumero());
                comprobante.setPtoventa(newPtoVenta);
            }
            if(newResponsableTipo != null){
                newResponsableTipo = (Responsabletipo)session.get(Responsabletipo.class, newResponsableTipo.getIdResponsable());
                comprobante.setResponsabletipo(newResponsableTipo);
            }
            
            if(newTipoMoneda != null){
                newTipoMoneda = (Tipomoneda)session.get(Tipomoneda.class, newTipoMoneda.getIdMoneda());
                comprobante.setTipomoneda(newTipoMoneda);
            }
            if(newUsuario != null){
                newUsuario = (Usuario)session.get(Usuario.class, newUsuario.getIdUsuario());
                comprobante.setUsuario(newUsuario);
            }
            
            List<Detalle> almacenarDetalles = new ArrayList<Detalle>();
            for(Detalle detalle : newDetalles){
                detalle = (Detalle)session.get(Detalle.class, detalle.getId());
                almacenarDetalles.add(detalle);
            }
            newDetalles = almacenarDetalles;
            comprobante.setDetalles(newDetalles);
            comprobante = (Cbtesasoc)session.merge(comprobante);
            //------------------
            if(oldComprador != null && !oldComprador.equals(newComprador)){
                oldComprador.getCbtesasocs().remove(comprobante);
                oldComprador = (Comprador)session.merge(oldComprador);
            }
            if(newComprador != null && !newComprador.equals(oldComprador)){
                newComprador.getCbtesasocs().add(comprobante);
                newComprador = (Comprador)session.merge(newComprador);
            }
            
            
            if(oldConcepto != null && !oldConcepto.equals(newConcepto)){
                oldConcepto.getCbtesasocs().remove(comprobante);
                oldConcepto = (Concepto)session.merge(oldConcepto);
            }
            if(newConcepto != null && !newConcepto.equals(oldConcepto)){
                newConcepto.getCbtesasocs().add(comprobante);
                newConcepto = (Concepto)session.merge(newConcepto);
            }
            if(oldCtetipo != null && !oldCtetipo.equals(newCtetipo)){
                oldCtetipo.getCbtesasocs().remove(comprobante);
                oldCtetipo = (Ctetipo)session.merge(oldCtetipo);
            }
            if(newCtetipo != null && !newCtetipo.equals(oldCtetipo)){
                newCtetipo.getCbtesasocs().add(comprobante);
                newCtetipo = (Ctetipo)session.merge(newCtetipo);
            }
            if(oldFecaereq != null && !oldFecaereq.equals(newFecaereq)){
                oldFecaereq.getCbtesasocs().remove(comprobante);
                oldFecaereq = (Fecaereq)session.merge(oldFecaereq);
            }
            if(newFecaereq != null && !newFecaereq.equals(oldFecaereq)){
                newFecaereq.getCbtesasocs().add(comprobante);
                newFecaereq = (Fecaereq)session.merge(newFecaereq);
            }
            if(oldPtoVenta != null && !oldPtoVenta.equals(newPtoVenta)){
                oldPtoVenta.getCbtesasocs().remove(comprobante);
                oldPtoVenta = (Ptoventa)session.merge(oldPtoVenta);
            }
            if(newPtoVenta != null && !newPtoVenta.equals(oldPtoVenta)){
                newPtoVenta.getCbtesasocs().add(comprobante);
                newPtoVenta = (Ptoventa)session.merge(newPtoVenta);
            }
            if(oldResponsableTipo != null && !oldResponsableTipo.equals(newResponsableTipo)){
                oldResponsableTipo.getCbtesasocs().remove(comprobante);
                oldResponsableTipo = (Responsabletipo)session.merge(oldResponsableTipo);
            }
            if(newResponsableTipo != null && !newResponsableTipo.equals(oldResponsableTipo)){
                newResponsableTipo.getCbtesasocs().add(comprobante);
                newResponsableTipo = (Responsabletipo)session.merge(newResponsableTipo);
            }
            
            if(oldTipoMoneda != null && !oldTipoMoneda.equals(newTipoMoneda)){
                oldTipoMoneda.getCbtesasocs().remove(comprobante);
                oldTipoMoneda = (Tipomoneda)session.merge(oldTipoMoneda);
            }
            if(newTipoMoneda != null && !newTipoMoneda.equals(oldTipoMoneda)){
                newTipoMoneda.getCbtesasocs().add(comprobante);
                newTipoMoneda = (Tipomoneda)session.merge(newTipoMoneda);
            }
            if(oldUsuario != null && !oldUsuario.equals(newUsuario)){
                oldUsuario.getCbtesasocs().remove(comprobante);
                oldUsuario = (Usuario)session.merge(oldUsuario);
            }
            if(newUsuario != null && !newUsuario.equals(oldUsuario)){
                newUsuario.getCbtesasocs().add(comprobante);
                newUsuario = (Usuario)session.merge(newUsuario);
            }
           
            for(Detalle detalle : newDetalles){
                if(!oldDetalles.contains(detalle)){
                    Cbtesasoc oldComprobante = detalle.getCbtesasoc();
                    detalle.setCbtesasoc(comprobante);
                    detalle = (Detalle)session.merge(detalle);
                    if(oldComprobante != null && !oldComprobante.equals(comprobante) ){
                        oldComprobante.getDetalles().remove(detalle);
                        oldComprobante = (Cbtesasoc)session.merge(oldComprobante);
                    }
                }
            }
            session.getTransaction().commit();
            
        }catch(Exception e){
            e.printStackTrace();
             logger.info("\n\n --------------Error al hacer el UPDATE en CbesasocDAOImple-----");
           
            
        }
    }

    @Override
    public void create(Cbtesasoc comprobante, Session session) {
       
        if(comprobante.getDetalles()==null){
            comprobante.setDetalles(new ArrayList<Detalle>());
        }
        try{
            Comprador comprador = comprobante.getComprador();
            if(comprador != null){
                comprador = (Comprador)session.get(Comprador.class,comprador.getDocNro());
                comprobante.setComprador(comprador);
            }
            Concepto concepto = comprobante.getConcepto();
            if(concepto != null){
                concepto = (Concepto)session.get(Concepto.class, concepto.getIdConcepto());
                comprobante.setConcepto(concepto);
            }
            Ctetipo tipoComprobante = comprobante.getCtetipo();
            if(tipoComprobante != null){
                tipoComprobante = (Ctetipo)session.get(Ctetipo.class, tipoComprobante.getId());
                comprobante.setCtetipo(tipoComprobante);
            }
            Fecaereq fecaereq = comprobante.getFecaereq();
            if(fecaereq != null){
                fecaereq = (Fecaereq)session.get(Fecaereq.class, fecaereq.getIdFeCaereq());
                comprobante.setFecaereq(fecaereq);
            }
            Ptoventa puntoVenta = comprobante.getPtoventa();
            if(puntoVenta != null){
                puntoVenta = (Ptoventa)session.get(Ptoventa.class, puntoVenta.getNumero());
                comprobante.setPtoventa(puntoVenta);
            }
            Responsabletipo responsable = comprobante.getResponsabletipo();
            if(responsable != null){
                responsable = (Responsabletipo)session.get(Responsabletipo.class, responsable.getIdResponsable());
                comprobante.setResponsabletipo(responsable);
            }
            
            Tipomoneda tipoMoneda = comprobante.getTipomoneda();
            if(tipoMoneda != null){
                tipoMoneda = (Tipomoneda)session.get(Tipomoneda.class, tipoMoneda.getIdMoneda());
                comprobante.setTipomoneda(tipoMoneda);
            }
            Usuario usuario = comprobante.getUsuario();
            if(usuario != null){
                usuario = (Usuario)session.get(Usuario.class, usuario.getIdUsuario());
                comprobante.setUsuario(usuario);
            }
           
            List<Detalle> almacenarDetalles = new ArrayList<Detalle>();
            for(Detalle detalle : comprobante.getDetalles()){
                detalle = (Detalle)session.get(Detalle.class, detalle.getId());
                almacenarDetalles.add(detalle);
            }
            comprobante.setDetalles(almacenarDetalles);;
            session.persist(comprobante);
            
            if(comprador != null){
                comprador.getCbtesasocs().add(comprobante);
                comprador = (Comprador)session.merge(comprador);
            }
            
            if(concepto != null){
                concepto.getCbtesasocs().add(comprobante);
                concepto = (Concepto)session.merge(concepto);
            }
            if(tipoComprobante != null){
                tipoComprobante.getCbtesasocs().add(comprobante);
                tipoComprobante = (Ctetipo)session.merge(tipoComprobante);
            }
            if(fecaereq != null){
                fecaereq.getCbtesasocs().add(comprobante);
                fecaereq = (Fecaereq)session.merge(fecaereq);
            }
            if(puntoVenta != null){
                puntoVenta.getCbtesasocs().add(comprobante);
                puntoVenta = (Ptoventa)session.merge(puntoVenta);
            }
            if(tipoMoneda != null){
                tipoMoneda.getCbtesasocs().add(comprobante);
                tipoMoneda = (Tipomoneda)session.merge(tipoMoneda);
            }
            if(usuario != null){
                usuario.getCbtesasocs().add(comprobante);
                usuario = (Usuario)session.merge(usuario);
            }
           
            //   LOS OBJETOS DETALLE NO SE RECORREN PORQUE EL COMPROBANTE YA ESTA CARGADO AL
            //  SER PARTE DE SU CLAVE PRIMARIA.
            //  TENER EN CUENTA QUE LOS DETALLES SE FORMAN CON EL ID DEL PRODUCTO 
            //  Y EL ID DEL COMPROBANTE, POR LO QUE ESTOS DEBEN EXISTIR PREVIAMENTE
        }catch(Exception e){
            logger.info("\n\n --------------Error al hacer el CREATE en CbesasocDAOImple-----");
           e.printStackTrace();
        }
        
    }

    @Override
    public List<Cbtesasoc> findByComprador(Comprador comprador, Session session) {
    
   
    List<Cbtesasoc> comprobantes = new ArrayList<Cbtesasoc>();
    try{
        comprador = (Comprador)session.get(Comprador.class, comprador.getDocNro());
        comprobantes = comprador.getCbtesasocs();
        
    }catch(Exception e){
        e.printStackTrace();
        System.out.println("Error en el findByProvincia");
    }
    return comprobantes;
   
    }
    
    @Override
    public List<Cbtesasoc> getCbtesNoInformados(Session session){
        List<Cbtesasoc> comprobanteList = new ArrayList<Cbtesasoc>();
        try{
            
            for(Cbtesasoc comprobante : getCbtesAsoc(session)){
                if(comprobante.getFecaereq() == null){
                    comprobanteList.add(comprobante);
                }
            }
            
        }catch(Exception e){
            logger.info("\n\n --------------Error al hacer el getCbtesNoInformados en CbesasocDAOImple-----");
            e.printStackTrace();
        }
        return comprobanteList;
    }

    @Override
    public Cbtesasoc findById(int id, Session session) {
    Cbtesasoc cbtesasoc = null;
    try{
        cbtesasoc = (Cbtesasoc) session.get(Cbtesasoc.class, id);
       
    }catch(Exception e){
        e.printStackTrace();
    }
    return cbtesasoc;
    
    }

    @Override
    public Comprador getClienteByDNI(String dni, Session session) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Producto getProductoById(Integer codigo, Session session) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
