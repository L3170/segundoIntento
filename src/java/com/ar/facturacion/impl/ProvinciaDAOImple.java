/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IProvinciaDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Localidad;
import com.ar.facturacion.model.Provincia;
import com.ar.facturacion.util.HibernateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author LEONARDO
 */
public class ProvinciaDAOImple implements IProvinciaDAO, Serializable{
    private static final Logger logger = Logger.getLogger(ProvinciaDAOImple.class.getName());

    @Override
    public List<Provincia> getProvinciaList(Session session) {
         List<Provincia> provinciaList = new ArrayList<Provincia>();
        try{
            provinciaList = session.createQuery("FROM Provincia provincia").list();
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error al hacer el getProvinciaList");
        }
        return provinciaList;
    
    }
    
    @Override
    public void create(Provincia provincia, Session session)throws PreexistingEntityException, Exception{
        if(provincia.getLocalidads()==null){
            provincia.setLocalidads(new ArrayList<Localidad>());
        }
        
        try{
            session.beginTransaction();
            List<Localidad> localidadList = new ArrayList<Localidad>();
            for( Localidad localidad : provincia.getLocalidads()){
                localidad = (Localidad)session.get(Localidad.class, localidad.getIdLocalidad());
                localidadList.add(localidad);
            }
            provincia.setLocalidads(localidadList);
            session.persist(provincia);
            for(Localidad localidad : provincia.getLocalidads()){
                Provincia oldProvincia = localidad.getProvincia();
                localidad.setProvincia(provincia);
                localidad = (Localidad)session.merge(localidad);
                if(oldProvincia != null){
                    oldProvincia.getLocalidads().remove(localidad);
                    oldProvincia=(Provincia)session.merge(oldProvincia);
                }
            }
            session.getTransaction().commit();
        }catch(Exception e){
            if(findByIdProvincia(provincia.getIdProvincia(), session) != null){
                throw new PreexistingEntityException("Provincia " +provincia.getNomProvincia()+" ya existe", e);
            }
            logger.info("\n.......Error al hace el create(Provincia).......\n");
            throw e;
        }
    }
    //------------------------------------------------------------------------

    public void delete(Provincia provincia, Session session)throws IllegalOrphanException, NonexistentEntityException{
    
    try{
        session.beginTransaction();
        Provincia provinciaToDelete = (Provincia)session.get(Provincia.class, provincia.getIdProvincia());
        // si no existe no puede recuperar el id y arroja un error
        provinciaToDelete.getIdProvincia();
        List<String> illegalOrphanMessages = null;
        List<Localidad> localidadListOrphan = provincia.getLocalidads();
        for(Localidad localidadOrphan : localidadListOrphan){
            if(illegalOrphanMessages == null){
                illegalOrphanMessages = new ArrayList<String>();
            }
            illegalOrphanMessages.add("La localidad "+localidadOrphan.getNomLocalidad()+" esta asociada a esta provincia");
            
        }
        if(illegalOrphanMessages != null){
            throw new IllegalOrphanException(illegalOrphanMessages);
            
        }
        session.delete(provincia);
        
        provincia = (Provincia)session.merge(provincia);
        session.getTransaction().commit();
        
        
    }catch(Exception e){
        e.printStackTrace();
            
    }
}
    
//------------------------------------------------------------------------
    @Override
    public void update(Provincia provincia, Session session) throws IllegalOrphanException, NonexistentEntityException, Exception{
      
      try{
            session.beginTransaction();
            Provincia persistirProvincia = (Provincia)session.get(Provincia.class, provincia.getIdProvincia());
            List<Localidad> oldLocalidades = persistirProvincia.getLocalidads();
            List<Localidad> newLocalidades = provincia.getLocalidads();
            List<String> illegalOrphanMessages = null;
            for(Localidad localidad : oldLocalidades){
                if(!newLocalidades.contains(localidad)){
                    if(illegalOrphanMessages == null){
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("Debe asignar una provincia a la localidad "+localidad.getIdLocalidad()+". ");

                }
            }
            List<Localidad> persistirLocalidades = new ArrayList<Localidad>();
            for (Localidad localidad : newLocalidades){
                localidad = (Localidad)session.get(Localidad.class, localidad.getIdLocalidad());
                persistirLocalidades.add(localidad);
            }
            newLocalidades = persistirLocalidades;
            provincia.setLocalidads(newLocalidades);
            provincia = (Provincia)session.merge(provincia);

            for(Localidad localidad : newLocalidades){
                if(!oldLocalidades.contains(localidad)){
                    Provincia oldProvincia = localidad.getProvincia();
                    localidad.setProvincia(provincia);
                    localidad = (Localidad)session.merge(localidad);
                    if(oldProvincia != null && !oldProvincia.equals(provincia)){
                        oldProvincia.getLocalidads().remove(localidad);
                        oldProvincia = (Provincia)session.merge(oldProvincia);
                    }
                }
            }
            session.getTransaction().commit();

        }catch(Exception e){
            session.getTransaction().rollback();
            e.getStackTrace();
        }
    }

    @Override
    public Provincia findByIdProvincia(String id, Session session){
    Provincia provincia = null;
    try{
        provincia = (Provincia) session.get(Provincia.class, id);
        
    }catch(Exception e){
        e.printStackTrace();
    }
    return provincia;
    
    }
    

}
