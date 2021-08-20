/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IRolDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Permiso;
import com.ar.facturacion.model.Rol;
import com.ar.facturacion.model.Usuario;
import com.ar.facturacion.util.HibernateUtil;
import java.io.Serializable;
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
public class RolDAOImple implements IRolDAO, Serializable{

    private static final Logger logger = Logger.getLogger(RolDAOImple.class.getName());

     @Override
    public List<Rol> getRolList(Session session) {   
        List<Rol> roles = new ArrayList<Rol>();
        try{
            //SessionFactory factory = HibernateUtil.getSessionFactory();
            //Session session = factory.openSession();
            
            Query query = session.createQuery("FROM Rol rol");
            roles = query.list();
            
        }catch(Exception e){
             logger.info("\n.......Error al hace el getRolList.......\n");
             e.printStackTrace();
        }
        return roles;
    }
    
    @Override
    public void create(Rol rol) {
        if(rol.getPermisos()== null){
            rol.setPermisos(new ArrayList<Permiso>());
        }
        
        if(rol.getUsuarios() == null){
            rol.setUsuarios(new ArrayList<Usuario>());
        }
        Session session = null;
        SessionFactory factory = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            List<Permiso> permisoList = new ArrayList<Permiso>();
            List<Usuario> usuariosList = new ArrayList<Usuario>();
            for(Permiso permiso : rol.getPermisos()){
                permiso = (Permiso)session.get(Permiso.class, permiso.getIdPermiso());
               permisoList.add(permiso);
            }
            rol.setPermisos(permisoList);
            for(Usuario usuario : rol.getUsuarios()){
                usuario = (Usuario)session.get(Usuario.class, usuario.getIdUsuario());
                usuariosList.add(usuario);
            }
            rol.setUsuarios(usuariosList);
            session.persist(rol);
            
            for(Permiso permiso : rol.getPermisos()){
                permiso.getRols().add(rol);
                permiso = (Permiso)session.merge(permiso);
            }
            
            for(Usuario usuario : rol.getUsuarios()){
                Rol oldRol = usuario.getRol();
                usuario.setRol(rol);
                usuario = (Usuario)session.merge(usuario);
                if(oldRol != null){
                    oldRol.getUsuarios().remove(usuario);
                    oldRol = (Rol)session.merge(rol);
                }
                
            }    
            session.getTransaction().commit();
            
        }catch(Exception e){
            logger.info("\n\n---------Error en el Create de RolDaoImple---------\n\n");
            e.printStackTrace();
            
        }finally{
            if(session != null){
                session.close();
            }
        }
    
    }
    
   
    

    @Override
    public void delete(Rol rol) throws NonexistentEntityException {
        Session session = null;
        SessionFactory factory = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            Rol eliminarRol;
            try{
                eliminarRol = (Rol)session.get(Rol.class, rol.getIdRol());
                eliminarRol.getIdRol();
            }catch(EntityNotFoundException enfe){
                throw new NonexistentEntityException("el rol con id"+rol.getIdRol()+"no existe", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Usuario> usuariosOrphanCheck = rol.getUsuarios();
            if(!usuariosOrphanCheck.isEmpty()){
                if(illegalOrphanMessages == null){
                    illegalOrphanMessages = new ArrayList<String>();
                } 
                illegalOrphanMessages.add("El Rol "+rol+" posee usuarios.");
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> permisos = rol.getPermisos();
            for(Permiso permiso : permisos){
                permiso.getRols().remove(rol);
                permiso = (Permiso)session.merge(permiso);
            }
            session.delete(rol);
            session.getTransaction().commit();
            
            
        }catch(Exception e){
            logger.info("\n\n---------Error en el Delete de RolDaoImple---------\n\n");
            e.printStackTrace();
        }
    
    }

    @Override
    public void update(Rol rol) throws NonexistentEntityException, Exception {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            Rol oldRol = (Rol)session.get(Rol.class, rol.getIdRol());
            List<Permiso> oldPermisos = oldRol.getPermisos();
            List<Permiso> newPermisos = rol.getPermisos();
            List<Usuario> oldUsuarios = oldRol.getUsuarios();
            List<Usuario> newUsuarios = rol.getUsuarios();
            List<String> illegalOrphanMessages = null;
            for(Usuario viejoUsuario : oldUsuarios){
                if(!newUsuarios.contains(viejoUsuario)){
                    if(illegalOrphanMessages == null){
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("Modifique el rol del usuario "+viejoUsuario.getNomUsuario()+" para eliminar este rol");
                }
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> conservarPermisos = new ArrayList<Permiso>();
            for(Permiso permiso : newPermisos){
                permiso = (Permiso)session.get(Permiso.class, permiso.getIdPermiso());
                conservarPermisos.add(permiso);
            }
            newPermisos = conservarPermisos;
            rol.setPermisos(conservarPermisos);
            List<Usuario> conservarUsuarios = new ArrayList<Usuario>();
            for(Usuario usuario : newUsuarios){
                usuario = (Usuario)session.get(Usuario.class, usuario.getIdUsuario());
                conservarUsuarios.add(usuario);
            }
            newUsuarios = conservarUsuarios;
            rol.setUsuarios(newUsuarios);
            rol = (Rol)session.merge(rol);
            
            for(Permiso oldPermiso : oldPermisos){
                if(!newPermisos.contains(oldPermiso)){
                    oldPermiso.getRols().remove(rol);
                    oldPermiso = (Permiso)session.merge(oldPermiso);
                }
            }
            for(Permiso newPermiso : newPermisos){
                if(!oldPermisos.contains(newPermiso)){
                    newPermiso.getRols().add(rol);
                    newPermiso = (Permiso)session.merge(newPermiso);
                }
            }
            for(Usuario nuevoUsuario : newUsuarios){
                Rol viejoRol = nuevoUsuario.getRol();
                nuevoUsuario.setRol(rol);
                nuevoUsuario = (Usuario)session.merge(nuevoUsuario);
                if(viejoRol != null && !viejoRol.equals(rol)){
                    viejoRol.getUsuarios().remove(nuevoUsuario);
                    viejoRol = (Rol)session.merge(viejoRol);
                }
            }
            session.getTransaction().commit();
          
        }catch(Exception e){
            logger.info("\n\n---------Error en el update de RolDaoImple---------\n\n");
            e.printStackTrace();
        }
    
    
    
    
    }

    @Override
    public Rol findRolById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rol login(Rol rol) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
  /*  
//===========================================================================================
    
    @Override
    public List<Rol> getRolList() {
        Session session = null;
        List<Rol> rolList = null;
        String jpql = "From Rol rol";
        try{
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            rolList = session.createQuery(jpql).list();
            session.getTransaction().commit();
            
        }catch(Exception e){
            session.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            if(session.isOpen()){
                session.close();
            }
        }
    return rolList;
    } //getRolList
//===========================================================================================
    @Override
    public void create(Rol rol) {
        if(rol.getPermisos()== null){
            rol.setPermisos(new ArrayList<Permiso>());
        }
        
        if(rol.getUsuarios() == null){
            rol.setUsuarios(new ArrayList<Usuario>());
        }
        Session session = null;
        SessionFactory factory = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            List<Permiso> permisoList = new ArrayList<Permiso>();
            List<Usuario> usuariosList = new ArrayList<Usuario>();
            for(Permiso permiso : rol.getPermisos()){
                permiso = (Permiso)session.get(Permiso.class, permiso.getIdPermiso());
               permisoList.add(permiso);
            }
            rol.setPermisos(permisoList);
            for(Usuario usuario : rol.getUsuarios()){
                usuario = (Usuario)session.get(Usuario.class, usuario.getIdUsuario());
                usuariosList.add(usuario);
            }
            rol.setUsuarios(usuariosList);
            session.persist(rol);
            for(Usuario usuario : rol.getUsuarios()){
                Rol oldRol = usuario.getRol();
                usuario.setRol(rol);
                if(oldRol != null){
                    oldRol.getUsuarios().remove(rol);
                    oldRol = (Rol)session.merge(rol);
                }
            }
            for(Permiso permiso : rol.getPermisos()){
                permiso.getRols().add(rol);
                permiso = (Permiso)session.merge(permiso);
            }
            session.getTransaction().commit();
            
        }catch(Exception e){
            logger.info("\n\n---------Error en el Create de RolDaoImple---------\n\n");
            e.printStackTrace();
            
        }finally{
            if(session != null){
                session.close();
            }
        }
    
    }
//===========================================================================================

    @Override
    public void delete(Rol rol) throws NonexistentEntityException {
        Session session = null;
        SessionFactory factory = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
           
            try{
                rol = (Rol)session.get(Rol.class, rol.getIdRol());
                rol.getIdRol();
            }catch(EntityNotFoundException e){
                throw new NonexistentEntityException("El rol con id "+rol.getIdRol()+" no existe");
            }
            List<String> illegalOrphanMessages = null;
            List<Usuario> usuarios = rol.getUsuarios();
            for(Usuario usuario : usuarios){
                if(illegalOrphanMessages == null){
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El rol "+rol.getNombre()+" no puede ser eliminado");
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> permisos = rol.getPermisos();
            for(Permiso permiso : permisos){
                permiso.getRols().remove(rol);
                permiso = (Permiso)session.merge(permiso);
            }
            session.delete(rol);
            session.getTransaction().commit();
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(RolDAOImple.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(session != null){
                session.close();
            }
        }
    }
//===========================================================================================

    @Override
    public void update(Rol rol) throws NonexistentEntityException, Exception {
        if(rol.getUsuarios()== null ){
            rol.setUsuarios(new ArrayList<Usuario>());
        }
        if(rol.getPermisos()== null){
            rol.setPermisos(new ArrayList<Permiso>());
        }
        Session session = null;
        SessionFactory factory = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.beginTransaction();
            //no puede guardarse en la variable que le paso rol porque de ahi saco los
            //datos del rol viejo
            Rol oldRol = (Rol)session.get(Rol.class, rol.getIdRol());
            List<Usuario> oldUsuarios = oldRol.getUsuarios();
            List<Usuario> newUsuarios = rol.getUsuarios();
            List<Permiso> oldPermisos = oldRol.getPermisos();
            List<Permiso> newPermisos = rol.getPermisos();
            List<String> illegalOrphanMessages = null;
            for(Usuario viejoUsuario : oldUsuarios ){
                if(! newUsuarios.contains(viejoUsuario)){
                    if(illegalOrphanMessages == null){
                        illegalOrphanMessages = new ArrayList<String>();
                      }
                    illegalOrphanMessages.add("Debe mantener el usuario "+viejoUsuario.getUsuario()+"ya que su campo rol no puede quedar vacio.");
                }
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> almacenarPermisos = new ArrayList<Permiso>();
            for(Permiso permiso : newPermisos ){
                permiso = (Permiso)session.get(Permiso.class, permiso.getIdPermiso());
                almacenarPermisos.add(permiso);
            }
            newPermisos = almacenarPermisos;
            rol.setPermisos(newPermisos);
            List<Usuario> almacenarUsuarios = new ArrayList<Usuario>();
            for(Usuario usuario : newUsuarios){
                usuario = (Usuario)session.get(Usuario.class, usuario.getIdUsuario());
                almacenarUsuarios.add(usuario);
            }
            newUsuarios = almacenarUsuarios;
            rol.setUsuarios(newUsuarios);
            rol = (Rol)session.merge(rol);
            for(Permiso oldPermiso : oldPermisos){
                if(!newPermisos.contains(oldPermiso)){
                    oldPermiso.getRols().remove(rol);
                    oldPermiso = (Permiso)session.merge(oldPermiso);
                }
            }
            for(Permiso newPermiso : newPermisos){
                if(!oldPermisos.contains(newPermiso)){
                    newPermiso.getRols().add(rol);
                    newPermiso = (Permiso)session.merge(newPermiso);
                }
            }
            for(Usuario nuevoUsuario : newUsuarios){
                if(!oldUsuarios.contains(nuevoUsuario)){
                    Rol viejoRol = nuevoUsuario.getRol();
                    nuevoUsuario.setRol(rol);
                    nuevoUsuario=(Usuario)session.merge(nuevoUsuario);
                    if(viejoRol != null && !viejoRol.equals(rol)){
                        viejoRol.getUsuarios().remove(nuevoUsuario);
                        viejoRol = (Rol)session.merge(viejoRol);
                    }
                }
            }
            session.getTransaction().commit();
            
        }catch(Exception e){
            String msg = e.getLocalizedMessage();
            if(msg==null || msg.length() == 0){
                Integer id = rol.getIdRol();
                if(findRolById(id)==null){
                    throw new NonexistentEntityException("El rol con id "+id+" no existe.");
                }
            }
            throw e;
        }finally{
            if(session != null){
                session.close();
            }
        }
    
    }
//===========================================================================================

    @Override
    public Rol findRolById(int id) {
        Rol rol = null;
        try{
            Session session = HibernateUtil.getSessionFactory().openSession();
            rol = (Rol)session.get(Rol.class, id);
            session.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return rol;
    
    }
//===========================================================================================

    @Override
    public Rol login(Rol rol) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
*/    

   
}
