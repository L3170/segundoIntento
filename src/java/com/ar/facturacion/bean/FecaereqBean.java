/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.ICbtesAsocDAO;
import com.ar.facturacion.dao.IFecaereqDAO;
import com.ar.facturacion.impl.CbtesasocDAOImple;
import com.ar.facturacion.impl.FecaereqDAOImple;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Fecaereq;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.FlowEvent;

/**
 *
 * @author Leonardo
 */
@ManagedBean(name = "fecaereqBean")
@SessionScoped
public class FecaereqBean implements Serializable {
    private static final Logger log = Logger.getLogger(FecaereqBean.class.getName());

    private Fecaereq fecaereq;
    private boolean enable;
    private String actualizarTabla;
    private List<Fecaereq> fecaereqList;
    private List<Fecaereq> filteredFecaereq;
    boolean skip;
    
    //GENERAR DOS FUNCIONES UNA CUANDO SE GENERA UN FECAEREQ CON UN SOLO COMPROBANTE ASOCIADO
    // Y OTRA CUANDO SE GENERA UN FECAEREQ CON VARIOS COMPROBANTES ASOCIADOS QUE PERMITAN
    // OBTENER LOS DATOS DEL COMPROBANTE Y ASIGNARLE LOS VALORES CALCULABLES O NO EN EL 
    // NUEVO FECAEREQ QUE VAMOS A GENERAR.
    
//------------------------------------------------------------------------
    
    public FecaereqBean() {
    }
//------------------------------------------------------------------------

    public Fecaereq getFecaereq(){
        return this.fecaereq;
    }
    public void setFecaereq(Fecaereq fecaereq){
        this.fecaereq = fecaereq;
    }
//------------------------------------------------------------------------
    public List<Fecaereq> getFilteredFecaereq(){
        return this.filteredFecaereq;
    }
//------------------------------------------------------------------------
    public void setFilteredFecaereq(List<Fecaereq> filteredFecaereq){
        this.filteredFecaereq = filteredFecaereq;
    }
    //------------------------------------------------------------------------
    public List<Fecaereq> getFecaereqList(){
        return this.fecaereqList;
    }
//------------------------------------------------------------------------
    public void setFecaereqList(List<Fecaereq> fecaereqList){
        this.fecaereqList = fecaereqList;
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
    public String getActualizarTabla(){
        return this.actualizarTabla;
    }
//------------------------------------------------------------------------   
    public void setActualizarTabla(String actualizarTabla){
        this.actualizarTabla = actualizarTabla;
    }
//------------------------------------------------------------------------   
    public void disableButton(){
        this.enable = false;
    }

//------------------------------------------------------------------------
    public void prepararNuevoFecaereq(){
        this.fecaereq = new Fecaereq();
        this.enableButton();
    }
//------------------------------------------------------------------------

    
//------------------------------------------------------------------------    
     public boolean isSkip() {
        return skip;
    }
 
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
    
    public String onFlowProcess(FlowEvent event) {
    //    if(skip) {
    //        skip = false;   //reset in case user goes back
    //        return "Respuesta";
    //    }
    //    else {
            return event.getNewStep();
    //    }
    }
    
//------------------------------------------------------------------------
    
    
}
