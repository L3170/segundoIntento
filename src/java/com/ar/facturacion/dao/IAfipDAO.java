/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.model.*;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author Leonardo
 */
public interface IAfipDAO {
    
    
    public List<Opcional> getOpcionalList(Session session);
    public void setOpcionalList(List<Opcional> opcional);
    public void dropOpcionalList();
    
    public List<Responsabletipo> getResponsableTipoList(Session session);
    public void setResponsableTipoList(List<Responsabletipo> ResponsableTipoList);
    public void dropResponsableTipoList();
    
    public List<Errors> getErrorsList(Session session);
    public void setErrorsList(List<Errors> errors);
    public void dropErrorsList();
    
    public List<Events> getEventsList(Session session);
    public void setEventsList(List<Events> events);
    public void dropEventsList();
    
    public List<Ctetipo> getCtetipoList(Session session);
    public void setCtetipoList(List<Ctetipo> ctetipoList);
    public void dropCteTipoList();
    
    public List<Cbtesasoc> getCbtesasocList(Session session);
    public void setCbtesasocList(List<Cbtesasoc> cbtesAsocList);
    public void dropCbtesasocList();
    
    public List<Ptoventa> getPtoVentaList(Session session);
    public void setPtoVentaList(List<Ptoventa> ptoVentaList);
    public void dropPtoVenta(Ptoventa puntoVenta);
    
    public List<Doctipo> getDoctipoList(Session session);
    public void setDoctipoList(List<Doctipo> docTipoList);
    public void dropDocTipoList();
    
    public List<Tipomoneda> getTipomonedaList(Session session);
     public void setTipomonedaList(List<Tipomoneda> tipoMonedaList);
    public void dropTipoMonedaList();
    
    public List<Moncotiz> getMonCotizList(Session session);
    public void setMonCotizList(List<Moncotiz> monCotizList);
    public void dropMonCotizList();
    
    public List<Ivatipo> getIvaTipoList(Session session);
    public void setIvaTipoList(List<Ivatipo> ivaTipoList);
    public void dropIvaTipoList();
    
    public List<Tipotributo> getTipoTributoList(Session session);
    public void setTipoTributoList(List<Tipotributo> tipoTributoList);
    public void dropTipoTributoList();
    
    public List<Concepto> getConceptoList(Session session);
    public void setConceptoList(List<Concepto> conceptoList);
    public void dropConceptoList();
    
    
}
