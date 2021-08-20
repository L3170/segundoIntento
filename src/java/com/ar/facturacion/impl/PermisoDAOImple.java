/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IPermisoDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Permiso;
import com.ar.facturacion.model.Rol;

import com.ar.facturacion.util.HibernateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;


 
public class PermisoDAOImple implements Serializable, IPermisoDAO{
    private static final Logger logger = Logger.getLogger(PermisoDAOImple.class.getName());

    @Override
    public List<Permiso> getPermisoList() {
           List<Permiso> permisos = new ArrayList<Permiso>();
        try{
            SessionFactory factory = HibernateUtil.getSessionFactory();
            Session session = factory.openSession();
            Query query = session.createQuery("from Permiso permiso");
            permisos = query.list();
           
       }catch(Exception e){
             logger.info("\n.......Error al hace el getPermisoList.......\n");
             e.printStackTrace();
      
        }
        return permisos;
    }

     @Override
    public void create(Permiso permiso) {
        SessionFactory factory = null;
        Session session= null;
        //NO HACE FALTA
        /*
        if(permiso.getRols()== null){
            permiso.setRols(new ArrayList<Rol>());
        }
        
        if(permiso.getPermisos()== null){
            permiso.setPermisos(new ArrayList<Permiso>());
        }
        */
        
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
           // NO HACE FALTA YA ESTA VALIDADO PORQUE SALE DE LA BASE DE DATOS
            /*
            Permiso permisoPadre = permiso.getPermiso();
           if(permisoPadre != null){
                permisoPadre = (Permiso)session.get(Permiso.class, permisoPadre.getIdPermiso());
                permiso.setPermiso(permisoPadre);
            }
            List<Rol> persistirRoles = new ArrayList<Rol>();
            for(Rol rol : permiso.getRols()){
                rol = (Rol)session.get(Rol.class, rol.getIdRol());
                persistirRoles.add(rol);
            }
            permiso.setRols(persistirRoles);
            List<Permiso> persistirPermisos = new ArrayList<Permiso>();
            for(Permiso per : permiso.getPermisos()){
                per = (Permiso)session.get(Permiso.class, per.getIdPermiso());
                persistirPermisos.add(per);
            }
            permiso.setPermisos(persistirPermisos);
            */
            session.save(permiso);
            /*
            if(permisoPadre != null){
                permisoPadre.getPermisos().add(permiso);
                session.saveOrUpdate(permisoPadre);
                //permisoPadre = (Permiso)session.merge(permisoPadre);
            }
            
            for(Rol rol : permiso.getRols()){
                rol.getPermisos().add(permiso);
                session.saveOrUpdate(rol);
                //rol = (Rol)session.merge(rol);
            }
            for(Permiso permi : permiso.getPermisos()){
                Permiso oldPermiso = permi.getPermiso();
                permi.setPermiso(permiso);
                session.saveOrUpdate(permi);
                //permi = (Permiso)session.merge(permi);
                if(oldPermiso != null){
                    oldPermiso.getPermisos().remove(permi);
                    session.saveOrUpdate(permi);
                    //permi = (Permiso)session.merge(permi);
                }
            }
            */
            session.getTransaction().commit();
            
           
        }catch(Exception e){
             logger.info("\n.......Error al hace el create de PermisoDAOImple.......\n");
             e.printStackTrace();
            session.getTransaction().rollback();
            
        }
    
    }//create

    @Override
    public void delete(Permiso permiso) throws NonexistentEntityException {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            
            try{
                permiso = (Permiso)session.get(Permiso.class, permiso.getIdPermiso());
                permiso.getIdPermiso();
            }catch(EntityNotFoundException e){
                throw new NonexistentEntityException("El siguiente permiso no existe: "+permiso.getNombre());
            }
            List<String>illegalOrphanMessages = null;
            for(Rol rolCheck : permiso.getRols()){
                if(illegalOrphanMessages == null){
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El Permiso (" + permiso.getNombre() + ") no se puede eliminar ya que el Rol" + rolCheck.getIdRol() + "no puede estar vacio");
            }
          
            for(Permiso PermisoCheck : permiso.getPermisos()){
                if(illegalOrphanMessages == null){
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El Permiso (" + permiso.getNombre() + ") no se puede eliminar ya que el rolPermiso" + PermisoCheck.getNombre() + "no puede estar vacio");
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Permiso padre = permiso.getPermiso();
            if(padre!= null){
                padre.getPermisos().remove(permiso);
                padre = (Permiso)session.merge(padre);
            }
            
            session.delete(permiso);
            session.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    
    }

    @Override
    public void update(Permiso permiso) throws NonexistentEntityException, Exception {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.getCurrentSession();
            session.beginTransaction();
            Permiso persistirPermiso = (Permiso)session.get(Permiso.class, permiso.getIdPermiso());
            Permiso oldPadre = persistirPermiso.getPermiso();
            Permiso newPadre = permiso.getPermiso();
            List<Rol> oldRoles = persistirPermiso.getRols();
            List<Rol> newRoles = permiso.getRols();
            List<Permiso> oldPermisos = persistirPermiso.getPermisos();
            List<Permiso> newPermisos = permiso.getPermisos();
            List<String> illegalOrphanMessages = null;
            if( newPadre != null){
                newPadre = (Permiso)session.get(Permiso.class, newPadre.getIdPermiso());
                permiso.setPermiso(newPadre);
            }
            
            List<Rol> almacenarRoles = new ArrayList<Rol>();
            for(Rol almacenarRol : newRoles){
                almacenarRol = (Rol)session.get(Rol.class, almacenarRol.getIdRol());
                almacenarRoles.add(almacenarRol);
            }
            newRoles = almacenarRoles;
            permiso.setRols(newRoles);
            
            List<Permiso> almacenarPermisos = new ArrayList<Permiso>();
            for(Permiso almacenarPermiso : newPermisos){
                almacenarPermiso = (Permiso)session.get(Permiso.class, almacenarPermiso.getIdPermiso());
                almacenarPermisos.add(almacenarPermiso);
            }
            newPermisos = almacenarPermisos;
            permiso.setPermisos(newPermisos);
            permiso = (Permiso)session.merge(permiso);
            
            if(oldPadre != null && !oldPadre.equals(newPadre)){
                oldPadre.getPermisos().remove(permiso);
                oldPadre = (Permiso)session.merge(oldPadre);
            }
            if(newPadre != null && !newPadre.equals(oldPadre)){
                newPadre.getPermisos().add(permiso);
                newPadre = (Permiso)session.merge(newPadre);
            }
            for(Rol oldRol : oldRoles){
                if(!newRoles.contains(oldRol)){
                    oldRol.getPermisos().remove(permiso);
                    oldRol = (Rol)session.merge(oldRol);
                }
            }
            for(Rol newRol : newRoles){
                if(!oldRoles.contains(newRol)){
                    newRol.getPermisos().add(permiso);
                    newRol = (Rol)session.merge(newRol);
                }
            }
            for(Permiso oldPermiso : oldPermisos){
                if(!newPermisos.contains(oldPermiso)){
                    oldPermiso.setPermiso(null);
                    oldPermiso = (Permiso)session.merge(oldPermiso);
                }
            }
            for(Permiso newPermiso : newPermisos){//recorro la nueva listas de permisos a actualizar
                if(!oldPermisos.contains(newPermiso)){//si ese permiso no esta en la lista vieja del permiso a actualizar
                                                    //osea es un permiso nuevo en la lista, almaceno el padre de dicho permiso
                    Permiso viejoPermisoPadre = newPermiso.getPermiso();
                    newPermiso.setPermiso(permiso);
                    newPermiso = (Permiso)session.merge(newPermiso);
                    if(viejoPermisoPadre != null && !viejoPermisoPadre.equals(permiso)){
                        viejoPermisoPadre.getPermisos().remove(newPermiso);
                        viejoPermisoPadre = (Permiso)session.merge(viejoPermisoPadre);
                    }
                }
            }
            session.getTransaction().commit();
        }catch(Exception e){
            String msg = e.getLocalizedMessage();
            if(msg==null || msg.length()==0){
                Integer id=permiso.getIdPermiso();
                if(findById(id)==null){
                    throw new NonexistentEntityException("El permiso con id " + id + " ya no existe.");
                }
            }
            throw e;
        }finally{
            if(session.isOpen()){
                session.close();
            }
        }
    
    
    }

    @Override
    public Permiso findById(int id) {
    Permiso permiso = null;
    try{
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        permiso = (Permiso) session.get(Permiso.class, id);
        session.close();
    }catch(Exception e){
        e.printStackTrace();
    }
    return permiso;
   
    }

    @Override
    public Permiso findByPermiso(Permiso permiso) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
       @Override
    public List<Permiso> findByRol(Rol rol) {
        SessionFactory factory = null;
        Session session = null;
        List<Permiso> permisos = new ArrayList<Permiso>();
            try{
                
                factory = HibernateUtil.getSessionFactory();
                session = factory.openSession();
                rol = (Rol) session.get(Rol.class, rol.getIdRol());
                permisos = rol.getPermisos();
                
            }catch(Exception e){
                e.printStackTrace();
                logger.info("----------------Error al buscar por Rol ----------------------");
            }
    return permisos;
    }

  //  @Override
  //  public List<Permiso> findByRol(Rol rol) {
  //      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  //  }

    
    
    /**
    @Override
    public List<Permiso> getPermisoList() {
    Session session = null;
    SessionFactory factory = null;
    List<Permiso> permisos = null;
    String jpql = "From Permiso permiso";
    try{
        factory = HibernateUtil.getSessionFactory();
        session = factory.openSession();
        session.beginTransaction();
        permisos = session.createQuery(jpql).list();
        session.getTransaction().commit();
        
    }catch(Exception e){
        e.printStackTrace();
            logger.info("----------------Error al listar un permiso ----------------------");
    }finally{
        if(session.isOpen()){
            session.close();
        }
    }
    return permisos;
    }//getPermisoList
//===========================================================================================

    @Override
    public void create(Permiso permiso) {
        
        if(permiso.getRols()== null){
            permiso.setRols(new ArrayList<Rol>());
        }
        if(permiso.getPermisos()==null){
            permiso.setPermisos(new ArrayList<Permiso>());
        }
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session= factory.openSession();
            session.beginTransaction();
            Permiso subpermiso = permiso.getPermiso();
            if(subpermiso != null){
                subpermiso = (Permiso)session.get(Permiso.class, subpermiso.getIdPermiso());
                permiso.setPermiso(subpermiso);
            }
            
            List<Rol> almacenarRoles = new ArrayList<Rol>();
            for(Rol rol : permiso.getRols()){
                rol = (Rol)session.get(Rol.class, rol.getIdRol());
                almacenarRoles.add(rol);
            }
            permiso.setRols(almacenarRoles);
            List<Permiso> almacenarPermisos = new ArrayList<Permiso>();
            for(Permiso miPermiso : permiso.getPermisos()){
                miPermiso = (Permiso)session.get(Permiso.class, miPermiso.getIdPermiso());
                almacenarPermisos.add(miPermiso);
            }
            permiso.setPermisos(almacenarPermisos);
            session.persist(permiso);
            if(subpermiso != null){
                subpermiso.getPermisos().add(permiso);
                subpermiso = (Permiso)session.merge(subpermiso);
            }
            for(Rol rol: permiso.getRols()){
                rol.getPermisos().add(permiso);
                rol = (Rol)session.merge(rol);
            }
            for(Permiso miPermiso : permiso.getPermisos()){
                Permiso oldSubPermiso = miPermiso.getPermiso();
                miPermiso.setPermiso(permiso);
                miPermiso = (Permiso)session.merge(miPermiso);
                if(oldSubPermiso != null){
                    oldSubPermiso.getPermisos().remove(miPermiso);
                    oldSubPermiso = (Permiso)session.merge(oldSubPermiso);
                }
               
            }
            session.getTransaction().commit();
            
        }catch(Exception e){
            logger.info("----------------Error al Crear un permiso ----------------------");
            e.printStackTrace();
            
        }
        
    }
//===========================================================================================

    @Override
    public void delete(Permiso permiso) throws NonexistentEntityException {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            try{
                permiso = (Permiso)session.get(Permiso.class, permiso.getIdPermiso());
                permiso.getIdPermiso();
            }catch(EntityNotFoundException e){
                throw new NonexistentEntityException("El permiso con el id "+permiso.getIdPermiso()+" no existe.");
            }
            Permiso subPermiso = permiso.getPermiso();
            if(subPermiso != null){
                subPermiso.getPermisos().remove(permiso);
                subPermiso = (Permiso)session.merge(subPermiso);
            }
            for(Rol rol : permiso.getRols()){
                rol.getPermisos().remove(permiso);
                rol = (Rol)session.merge(rol);
            }
            for(Permiso permi : permiso.getPermisos()){
                permi.setPermiso(null);
                permi = (Permiso)session.merge(permi);
            }
            
            session.delete(permiso);
            session.getTransaction().commit();
            
        }finally{
            if(session.isOpen()){
                session.close();
            }
        }
    }
//===========================================================================================

    @Override
    public void update(Permiso permiso) throws NonexistentEntityException, Exception {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            
            Permiso updatePermiso = (Permiso)session.get(Permiso.class, permiso.getIdPermiso());
            Permiso oldSubPermiso = updatePermiso.getPermiso();
            Permiso newSubPermiso = permiso.getPermiso();
            
            List<Rol> oldRoles = updatePermiso.getRols();
            List<Rol> newRoles = permiso.getRols();
            List<Permiso> oldPermisos = updatePermiso.getPermisos();
            List<Permiso> newPermisos = permiso.getPermisos();
            
            if(newSubPermiso != null){
                newSubPermiso = (Permiso)session.get(newSubPermiso.getClass(), newSubPermiso.getIdPermiso());
                permiso.setPermiso(newSubPermiso);
            }
            
            List<Rol> persistirRol = new ArrayList<Rol>();
            for(Rol rol : newRoles){
                rol = (Rol)session.get(Rol.class, rol.getIdRol());
                persistirRol.add(rol);
            }
            newRoles = persistirRol;
            permiso.setRols(newRoles);
            
            List<Permiso> persistirPermiso = new ArrayList<Permiso>();
            for(Permiso miPermiso : newPermisos){
                miPermiso = (Permiso)session.get(Permiso.class, miPermiso.getIdPermiso());
                persistirPermiso.add(miPermiso);
            }
            newPermisos = persistirPermiso;
            permiso.setPermisos(newPermisos);
            permiso = (Permiso)session.merge(permiso);
            if(oldSubPermiso != null && !oldSubPermiso.equals(newSubPermiso)){
                oldSubPermiso.getPermisos().remove(permiso);
                oldSubPermiso = (Permiso)session.merge(oldSubPermiso);
            }
            if(newSubPermiso != null && !newSubPermiso.equals(oldSubPermiso)){
                newSubPermiso.getPermisos().add(permiso);
                newSubPermiso = (Permiso)session.merge(newSubPermiso);
            }
            
            for(Rol rol : oldRoles){
                if(!newRoles.contains(rol)){
                    rol.getPermisos().remove(permiso);
                    rol = (Rol)session.merge(rol);
                }
            }
            for(Rol rol : newRoles){
                if(!oldRoles.contains(rol)){
                    rol.getPermisos().add(permiso);
                    rol = (Rol)session.merge(rol);
                }
            }
            for(Permiso auxPermiso : oldPermisos){
                if(!newPermisos.contains(auxPermiso)){
                    auxPermiso.setPermiso(null);
                    auxPermiso = (Permiso)session.merge(auxPermiso);
                }
            }
            for(Permiso auxPermiso : newPermisos){
                if(!oldPermisos.contains(auxPermiso)){
                    Permiso viejoPermiso = auxPermiso.getPermiso();
                    auxPermiso.setPermiso(permiso);
                    auxPermiso = (Permiso)session.merge(auxPermiso);
                    if(viejoPermiso != null && !viejoPermiso.equals(permiso)){
                        viejoPermiso.getPermisos().remove(auxPermiso);
                        viejoPermiso = (Permiso)session.merge(viejoPermiso);
                    }
                    
                }
            
            }
       
        session.getTransaction().commit();
            
        }catch(Exception e){
            String msg = e.getLocalizedMessage();
            if(msg==null || msg.length() == 0){
                if( findById(permiso.getIdPermiso())== null){
                    throw new NonexistentEntityException("El permido con id "+permiso.getIdPermiso()+" no existe");
                } else {
                }
            }
        }
    
    }
//===========================================================================================

    @Override
    public Permiso findById(int id) {
        Session session = null;
        Permiso permiso = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            permiso = (Permiso)session.get(Permiso.class, id);
            session.close();
        }catch(Exception e){
            e.printStackTrace();

        }
        return permiso;
    }
//===========================================================================================

    @Override
    public Permiso findByPermiso(Permiso permiso) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//===========================================================================================
 */ 
 
 //===========================================================================================

    
    
    
  
    
}
