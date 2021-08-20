/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IAfipDAO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Leonardo
 */
public class AfipDAOImple implements IAfipDAO{
    private static final Logger logger = Logger.getLogger(AfipDAOImple.class.getName());

    @Override
    public List<Opcional> getOpcionalList(Session session) {
     List<Opcional> opcionales = new ArrayList<Opcional>();
        try{
           Query query = session.createQuery("FROM Opcional opcional");
            opcionales = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getOpcionalList.......\n");
             e.printStackTrace();
        }
        return opcionales;
    
    }

    @Override
    public void dropOpcionalList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Errors> getErrorsList(Session session) {
        List<Errors> errores = new ArrayList<Errors>();
        try{
            Query query = session.createQuery("FROM Errors errors");
            errores = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getErrorsList.......\n");
             e.printStackTrace();
        }
        return errores;
    
    }

    @Override
    public void dropErrorsList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Events> getEventsList(Session session) {
        List<Events> eventos = new ArrayList<Events>();
        try{
            Query query = session.createQuery("FROM Events events");
            eventos = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getOpcionalList.......\n");
             e.printStackTrace();
        }
        return eventos;
    
    }

    @Override
    public void dropEventsList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Ctetipo> getCtetipoList(Session session) {
        List<Ctetipo> tiposComprobantes = new ArrayList<Ctetipo>();
        try{
            Query query = session.createQuery("FROM Ctetipo ctetipo");
            tiposComprobantes = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getCtetipoList.......\n");
             e.printStackTrace();
        }
        return tiposComprobantes;
    
    }

    @Override
    public void dropCteTipoList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Cbtesasoc> getCbtesasocList(Session session) {
        List<Cbtesasoc> comprobantes = new ArrayList<Cbtesasoc>();
        try{
            Query query = session.createQuery("FROM Cbtesasoc cbtesasoc");
            comprobantes = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getCbtesasocList.......\n");
             e.printStackTrace();
        }
        return comprobantes;
    }

    @Override
    public void dropCbtesasocList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Ptoventa> getPtoVentaList(Session session) {
        List<Ptoventa> puntoVentas = new ArrayList<Ptoventa>();
        try{
            Query query = session.createQuery("FROM Ptoventa ptoventa");
            puntoVentas = query.list();
           
        }catch(Exception e){
             logger.info("\n.......Error al hace el getPtoventaList.......\n");
             e.printStackTrace();
        }
        return puntoVentas;
    
    }

    @Override
    public void setPtoVentaList(List<Ptoventa> puntoVenta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Doctipo> getDoctipoList(Session session) {
        List<Doctipo> tiposDocumentos = new ArrayList<Doctipo>();
        try{
            Query query = session.createQuery("FROM Doctipo doctipo");
            tiposDocumentos = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getDoctipoList.......\n");
             e.printStackTrace();
        }
        return tiposDocumentos;
    
    }

    @Override
    public void dropDocTipoList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Tipomoneda> getTipomonedaList(Session session) {
        List<Tipomoneda> tipoMonedas = new ArrayList<Tipomoneda>();
        try{
            Query query = session.createQuery("FROM Tipomoneda tipomoneda");
            tipoMonedas = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getTipoMonedaList.......\n");
             e.printStackTrace();
        }
        return tipoMonedas;
    
    
    }

    @Override
    public void dropTipoMonedaList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Moncotiz> getMonCotizList(Session session) {
        List<Moncotiz> cotizMonedas = new ArrayList<Moncotiz>();
        try{
            Query query = session.createQuery("FROM Moncotiz moncotiz");
            cotizMonedas = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getCotizList.......\n");
             e.printStackTrace();
        }
        return cotizMonedas;
    
    }

    @Override
    public void dropMonCotizList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Ivatipo> getIvaTipoList(Session session) {
        List<Ivatipo> tiposIva = new ArrayList<Ivatipo>();
        try{
            Query query = session.createQuery("FROM Ivatipo ivatipo");
            tiposIva = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getTiposIvaList.......\n");
             e.printStackTrace();
        }
        return tiposIva;
    
    }

    @Override
    public void dropIvaTipoList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Tipotributo> getTipoTributoList(Session session) {
         List<Tipotributo> tiposTributos = new ArrayList<Tipotributo>();
        try{
            Query query = session.createQuery("FROM Tipotributo tipotributo");
            tiposTributos = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getTiposTributosList.......\n");
             e.printStackTrace();
        }
        return tiposTributos;
    }

    @Override
    public void dropTipoTributoList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Concepto> getConceptoList(Session session) {
         List<Concepto> conceptos = new ArrayList<Concepto>();
        try{
            
            Query query = session.createQuery("FROM Concepto concepto");
            conceptos = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getConceptosList.......\n");
             e.printStackTrace();
        }
        return conceptos;
    
    }

    @Override
    public void dropConceptoList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOpcionalList(List<Opcional> opcional) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setErrorsList(List<Errors> errors) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEventsList(List<Events> events) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCtetipoList(List<Ctetipo> ctetipoList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCbtesasocList(List<Cbtesasoc> cbtesAsocList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dropPtoVenta(Ptoventa puntoVenta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDoctipoList(List<Doctipo> docTipoList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTipomonedaList(List<Tipomoneda> tipoMonedaList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMonCotizList(List<Moncotiz> monCotizList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIvaTipoList(List<Ivatipo> ivaTipoList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTipoTributoList(List<Tipotributo> tipoTributoList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setConceptoList(List<Concepto> conceptoList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Responsabletipo> getResponsableTipoList(Session session) {
        List<Responsabletipo> responsableTipo = new ArrayList<Responsabletipo>();
        try{
            Query query = session.createQuery("FROM Responsabletipo responsabletipo");
            responsableTipo = query.list();
           
        }catch(Exception e){
             logger.info("\n.......Error al hace el getPtoventaList.......\n");
             e.printStackTrace();
        }
        return responsableTipo;
    
    
    }

    @Override
    public void setResponsableTipoList(List<Responsabletipo> ResponsableTipoList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dropResponsableTipoList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
