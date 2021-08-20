/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.ILocalidadDAO;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Comprador;
import com.ar.facturacion.model.Localidad;
import com.ar.facturacion.model.Provincia;
import com.ar.facturacion.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author LEONARDO
 */
public class LocalidadDAOImple implements ILocalidadDAO{

    private static final Logger logger = Logger.getLogger(LocalidadDAOImple.class.getName());

   
//------------------------------------------------------------------------
    @Override
    public List<Localidad> getLocalidadList(Session session) {
        List<Localidad> localidadList = new ArrayList<Localidad>();
        try{
            localidadList = session.createQuery("FROM Localidad localidad").list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getLocalidadList.......\n");
            logger.info("\n......"+e.getMessage()+".......\n");
      
        }
        return localidadList;
    
    
    }
//------------------------------------------------------------------------    
    @Override
    public void create(Localidad localidad, Session session){
        
        if(localidad.getCompradores()==null){
            localidad.setCompradores(new ArrayList<Comprador>());
        }
        try{
            
            Provincia provincia = localidad.getProvincia();
            if(provincia!=null){
                provincia = (Provincia)session.get(Provincia.class, provincia.getIdProvincia());
                localidad.setProvincia(provincia);
            }
            List<Comprador> almacenarCompradorList = new ArrayList<Comprador>();
            for(Comprador comprador : localidad.getCompradores()){
                    comprador = (Comprador) session.get(Comprador.class, comprador.getDocNro());
                    almacenarCompradorList.add(comprador);
            }
            localidad.setCompradores(almacenarCompradorList);
            session.persist(localidad);
            if(provincia != null){
                provincia.getLocalidads().add(localidad);
                provincia = (Provincia)session.merge(provincia);
            }
            for(Comprador comprador : localidad.getCompradores()){
                Localidad oldLocalidad = comprador.getLocalidad();
                comprador.setLocalidad(localidad);
                if(oldLocalidad != null){
                    oldLocalidad.getCompradores().remove(comprador);
                    oldLocalidad = (Localidad)session.merge(oldLocalidad);
                }
            }
            session.getTransaction().commit();
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
    
    
    
    }//create
//------------------------------------------------------------------------    
    @Override
    public void delete(Localidad localidad, Session session)throws NonexistentEntityException{
       
        try{
            session.beginTransaction();
            Localidad eliminarLocalidad;
            try{
                eliminarLocalidad = (Localidad)session.get(Localidad.class, localidad.getIdLocalidad());
                eliminarLocalidad.getIdLocalidad();
            }catch(EntityNotFoundException enfe){
                throw new NonexistentEntityException("La localidad con id"+localidad.getIdLocalidad()+"no existe", enfe);
            }
            Provincia provincia = eliminarLocalidad.getProvincia();
            if(provincia != null){
                provincia.getLocalidads().remove(eliminarLocalidad);
                provincia = (Provincia)session.merge(provincia);
            }
            List<Comprador> compradorList = eliminarLocalidad.getCompradores();
            for(Comprador comprador : compradorList){
                comprador.setLocalidad(null);
                comprador = (Comprador)session.merge(comprador);
            }
            session.delete(eliminarLocalidad);
            eliminarLocalidad = (Localidad)session.merge(eliminarLocalidad);
            session.getTransaction().commit();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
    }//deleteLocalidad
    
//------------------------------------------------------------------------------ 
@Override
public void update(Localidad localidad, Session session) throws NonexistentEntityException, Exception{
   
    try{
        session.beginTransaction();
        Localidad persistirLocalidad = (Localidad)session.get(Localidad.class, localidad.getIdLocalidad());
        Provincia oldProvincia = persistirLocalidad.getProvincia();
        Provincia newProvincia = localidad.getProvincia();
        List<Comprador> oldCompradorList = persistirLocalidad.getCompradores();
        List<Comprador> newCompradorList = localidad.getCompradores();
        if(newProvincia != null){
           newProvincia = (Provincia) session.get(Provincia.class, newProvincia.getIdProvincia());
           localidad.setProvincia(newProvincia);
        }
        List<Comprador> almacenarCompradores = new ArrayList<Comprador>();
        for(Comprador cli : newCompradorList){
            cli = (Comprador) session.get(Comprador.class, cli.getDocNro());
            almacenarCompradores.add(cli);
        }
        newCompradorList = almacenarCompradores;
        localidad.setCompradores(newCompradorList);
        localidad = (Localidad)session.merge(localidad);
        if(oldProvincia != null && !oldProvincia.equals(newProvincia)){
            oldProvincia.getLocalidads().remove(localidad);
            oldProvincia = (Provincia)session.merge(oldProvincia);
        }
        if(newProvincia != null && !newProvincia.equals(oldProvincia)){
            newProvincia.getLocalidads().add(localidad);
            newProvincia = (Provincia)session.merge(newProvincia);
        }
        for(Comprador old : oldCompradorList){
            if(!newCompradorList.contains(old)){
                old.setLocalidad(null);
                old = (Comprador)session.merge(old);
            }
        }
        for(Comprador nuevo : newCompradorList){
            if(!oldCompradorList.contains(nuevo)){
               Localidad viejaLocalidad = nuevo.getLocalidad();
               nuevo.setLocalidad(localidad);
               nuevo = (Comprador)session.merge(nuevo);
               if(viejaLocalidad != null && !viejaLocalidad.equals(localidad)){
                   viejaLocalidad.getCompradores().remove(nuevo);
                   viejaLocalidad = (Localidad)session.merge(viejaLocalidad);
                }
            }
        
        }
        session.getTransaction().commit();
        
    }catch(Exception ex){
        
        String msg = ex.getLocalizedMessage();
        if(msg == null || msg.length()== 0){
            int id = localidad.getIdLocalidad();
            if(findById(id, session)==null){
                throw new NonexistentEntityException("la localdiad con el id"+ id +"no existe.");
            }
        }
        throw ex;
        
    }
    

}

//------------------------------------------------------------------------
    @Override
    public Localidad findById(Integer id, Session session) {
    Localidad localidad = null;
    try{
        localidad = (Localidad) session.get(Localidad.class, id);
       
    }catch(Exception e){
        e.printStackTrace();
    }
    return localidad;
   
    }
//------------------------------------------------------------------------
    @Override
    public List<Localidad> findByProvincia(Provincia provincia) {
    SessionFactory factory = HibernateUtil.getSessionFactory();
    Session session = factory.openSession();
    List<Localidad> localidadList = new ArrayList<Localidad>();
    try{
        Query consulta =session.createQuery("FROM Localidad localidad "
                                            + "WHERE localidad.idProvincia =: id"); 
    consulta.setString("id", provincia.getIdProvincia());
    localidadList = consulta.list();
    
    }catch(Exception e){
        e.printStackTrace();
        System.out.println("Error en el findByProvincia");
    }finally{
    session.close();
    }
    
    return localidadList;
    
    }
    
}//LocalidadDAOImple
