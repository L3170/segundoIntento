/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.dao.IAfipDAO;
import com.ar.facturacion.impl.AfipDAOImple;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Concepto;
import com.ar.facturacion.model.Ctetipo;
import com.ar.facturacion.model.Doctipo;
import com.ar.facturacion.model.Errors;
import com.ar.facturacion.model.Events;
import com.ar.facturacion.model.Ivatipo;
import com.ar.facturacion.model.Moncotiz;
import com.ar.facturacion.model.Opcional;
import com.ar.facturacion.model.Ptoventa;
import com.ar.facturacion.model.Responsabletipo;
import com.ar.facturacion.model.Tipomoneda;
import com.ar.facturacion.model.Tipotributo;
import com.ar.facturacion.util.HibernateUtil;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Leonardo
 */
@ManagedBean(name = "afipBean")
@SessionScoped
public class AfipBean implements Serializable {
    
    private List<Opcional> opcionalList;
    private List<Opcional> filtrosOpcionalList;
    private List<Responsabletipo> responsableTipoList;
    private List<Responsabletipo> filtrosResponsableTipoList;
    private List<Errors> errorList;
    private List<Errors> filtrosErrorList;
    private List<Events> eventList;
    private List<Events> filtrosEventList;
    private List<Ctetipo> comprobanteTipoList;
    private List<Ctetipo> filtroComprobanteTipoList;
    private List<Cbtesasoc> comprobanteAsociadoList;
    private List<Cbtesasoc> filtroComprobanteAsociadoList;
    private List<Ptoventa> puntoVentaList;
    private List<Ptoventa> filtroPuntoVentaList;
    private List<Doctipo> documentoTipoList;
    private List<Doctipo> filtroDocumentoTipoList;
    private List<Tipomoneda> tipoMonedaList;
    private List<Tipomoneda> filtroTipoMonedaList;
    private List<Moncotiz> cotizMonedaList;
    private List<Moncotiz> filtroCotizMonedaList;
    private List<Ivatipo> ivaTipoList;
    private List<Ivatipo> filtroIvaTipoList;
    private List<Tipotributo> tipoTributoList;
    private List<Tipotributo> filtroTipoTributoList;
    private List<Concepto> conceptoList;
    private List<Concepto> filtroConceptoList;
    IAfipDAO dao;
     //............................
    SessionFactory factory = null;
    Session session = null;
    //............................
//------------------------------------------------------------------------
    
       
  

    
    
//========================================================================
    
    public AfipBean() {
       factory = HibernateUtil.getSessionFactory();
       session = factory.openSession();
       session.beginTransaction();
       dao = new AfipDAOImple();
    }
//-------------------------------------------------------------------------

    public List<Doctipo> getTipoDocumentoList() {
        documentoTipoList = dao.getDoctipoList(session);
        return documentoTipoList;
    }

    public void setTipoDocumentoList(List<Doctipo> tipoDocumentoList) {
        this.documentoTipoList = tipoDocumentoList;
    }
//-------------------------------------------------------------------------

    public List<Opcional> getOpcionalList() {
        opcionalList = dao.getOpcionalList(session);
        return opcionalList;
    }

    public void setOpcionalList(List<Opcional> opcionalList) {
        this.opcionalList = opcionalList;
    }

    public List<Opcional> getFiltrosOpcionalList() {
        return filtrosOpcionalList;
    }

    public void setFiltrosOpcionalList(List<Opcional> filtrosOpcionalList) {
        this.filtrosOpcionalList = filtrosOpcionalList;
    }

    public List<Responsabletipo> getResponsableTipoList() {
        responsableTipoList = dao.getResponsableTipoList(session);
        return responsableTipoList;
    }

    public void setResponsableTipoList(List<Responsabletipo> responsableTipoList) {
        this.responsableTipoList = responsableTipoList;
    }

    public List<Responsabletipo> getFiltrosResponsableTipoList() {
        return filtrosResponsableTipoList;
    }

    public void setFiltrosResponsableTipoList(List<Responsabletipo> filtrosResponsableTipoList) {
        this.filtrosResponsableTipoList = filtrosResponsableTipoList;
    }

    public List<Errors> getErrorList() {
        errorList = dao.getErrorsList(session);
        return errorList;
    }

    public void setErrorList(List<Errors> errorList) {
        this.errorList = errorList;
    }

    public List<Errors> getFiltrosErrorList() {
        return filtrosErrorList;
    }

    public void setFiltrosErrorList(List<Errors> filtrosErrorList) {
        this.filtrosErrorList = filtrosErrorList;
    }

    public List<Events> getEventList() {
        eventList = dao.getEventsList(session);
        return eventList;
    }

    public void setEventList(List<Events> eventList) {
        this.eventList = eventList;
    }

    public List<Events> getFiltrosEventList() {
        return filtrosEventList;
    }

    public void setFiltrosEventList(List<Events> filtrosEventList) {
        this.filtrosEventList = filtrosEventList;
    }

    public List<Ctetipo> getComprobanteTipoList() {
        comprobanteTipoList = dao.getCtetipoList(session);
        return comprobanteTipoList;
    }

    public void setComprobanteTipoList(List<Ctetipo> comprobanteTipoList) {
        this.comprobanteTipoList = comprobanteTipoList;
    }

    public List<Ctetipo> getFiltroComprobanteTipoList() {
        return filtroComprobanteTipoList;
    }

    public void setFiltroComprobanteTipoList(List<Ctetipo> filtroComprobanteTipoList) {
        this.filtroComprobanteTipoList = filtroComprobanteTipoList;
    }

    public List<Cbtesasoc> getComprobanteAsociadoList() {
        comprobanteAsociadoList = dao.getCbtesasocList(session);
        return comprobanteAsociadoList;
    }

    public void setComprobanteAsociadoList(List<Cbtesasoc> comprobanteAsociadoList) {
        this.comprobanteAsociadoList = comprobanteAsociadoList;
    }

    public List<Cbtesasoc> getFiltroComprobanteAsociadoList() {
        return filtroComprobanteAsociadoList;
    }

    public void setFiltroComprobanteAsociadoList(List<Cbtesasoc> filtroComprobanteAsociadoList) {
        this.filtroComprobanteAsociadoList = filtroComprobanteAsociadoList;
    }

    public List<Ptoventa> getPuntoVentaList() {
        puntoVentaList = dao.getPtoVentaList(session);
        return puntoVentaList;
    }

    public void setPuntoVentaList(List<Ptoventa> puntoVentaList) {
        this.puntoVentaList = puntoVentaList;
    }

    public List<Ptoventa> getFiltroPuntoVentaList() {
        return filtroPuntoVentaList;
    }

    public void setFiltroPuntoVentaList(List<Ptoventa> filtroPuntoVentaList) {
        this.filtroPuntoVentaList = filtroPuntoVentaList;
    }

    public List<Doctipo> getDocumentoTipoList() {
        documentoTipoList = dao.getDoctipoList(session);
        return documentoTipoList;
    }

    public void setDocumentoTipoList(List<Doctipo> documentoTipoList) {
        this.documentoTipoList = documentoTipoList;
    }

    public List<Doctipo> getFiltroDocumentoTipoList() {
        return filtroDocumentoTipoList;
    }

    public void setFiltroDocumentoTipoList(List<Doctipo> filtroDocumentoTipoList) {
        this.filtroDocumentoTipoList = filtroDocumentoTipoList;
    }

    public List<Tipomoneda> getTipoMonedaList() {
        tipoMonedaList = dao.getTipomonedaList(session);
        return tipoMonedaList;
    }

    public void setTipoMonedaList(List<Tipomoneda> tipoMonedaList) {
        this.tipoMonedaList = tipoMonedaList;
    }

    public List<Tipomoneda> getFiltroTipoMonedaList() {
        return filtroTipoMonedaList;
    }

    public void setFiltroTipoMonedaList(List<Tipomoneda> filtroTipoMonedaList) {
        this.filtroTipoMonedaList = filtroTipoMonedaList;
    }

    public List<Moncotiz> getCotizMonedaList() {
        cotizMonedaList = dao.getMonCotizList(session);
        return cotizMonedaList;
    }

    public void setCotizMonedaList(List<Moncotiz> cotizMonedaList) {
        this.cotizMonedaList = cotizMonedaList;
    }

    public List<Moncotiz> getFiltroCotizMonedaList() {
        return filtroCotizMonedaList;
    }

    public void setFiltroCotizMonedaList(List<Moncotiz> filtroCotizMonedaList) {
        this.filtroCotizMonedaList = filtroCotizMonedaList;
    }

    public List<Ivatipo> getIvaTipoList() {
        ivaTipoList = dao.getIvaTipoList(session);
        return ivaTipoList;
    }

    public void setIvaTipoList(List<Ivatipo> ivaTipoList) {
        this.ivaTipoList = ivaTipoList;
    }

    public List<Ivatipo> getFiltroIvaTipoList() {
        return filtroIvaTipoList;
    }

    public void setFiltroIvaTipoList(List<Ivatipo> filtroIvaTipoList) {
        this.filtroIvaTipoList = filtroIvaTipoList;
    }

    public List<Tipotributo> getTipoTributoList() {
        tipoTributoList = dao.getTipoTributoList(session);
        return tipoTributoList;
    }

    public void setTipoTributoList(List<Tipotributo> tipoTributoList) {
        this.tipoTributoList = tipoTributoList;
    }

    public List<Tipotributo> getFiltroTipoTributoList() {
        return filtroTipoTributoList;
    }

    public void setFiltroTipoTributoList(List<Tipotributo> filtroTipoTributoList) {
        this.filtroTipoTributoList = filtroTipoTributoList;
    }

    public List<Concepto> getConceptoList() {
        conceptoList = dao.getConceptoList(session);
        return conceptoList;
    }

    public void setConceptoList(List<Concepto> conceptoList) {
        this.conceptoList = conceptoList;
    }

    public List<Concepto> getFiltroConceptoList() {
        return filtroConceptoList;
    }

    public void setFiltroConceptoList(List<Concepto> filtroConceptoList) {
        this.filtroConceptoList = filtroConceptoList;
    }
    
    
    
    
    
}
