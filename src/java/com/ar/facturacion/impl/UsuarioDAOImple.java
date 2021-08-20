/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.auxiliar.Encriptar;
import com.ar.facturacion.dao.IUsuarioDAO;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Rol;
import com.ar.facturacion.model.Usuario;
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
public class UsuarioDAOImple implements IUsuarioDAO {

    private static final Logger logger = Logger.getLogger(UsuarioDAOImple.class.getName());

    @Override
    public List<Usuario> getUsuarioList() {
        List<Usuario> usuarios = new ArrayList<Usuario>();
        try{
            SessionFactory factory = HibernateUtil.getSessionFactory();
            Session session = factory.openSession();
            Query query = session.createQuery("FROM Usuario usuario");
            usuarios = query.list();
            
        }catch(Exception e){
         logger.info("\n.......Error al hace el getUsuarioList.......\n");
         e.printStackTrace();
        }//catch
    
    return usuarios;
    }
//------------------------------------------------------------------------------

    @Override
    public void create(Usuario usuario, Session session) {
        
        try{
            session.beginTransaction();
            Rol rol = usuario.getRol();
            if(rol != null){
                rol = (Rol) session.get(Rol.class, usuario.getRol().getIdRol());
                usuario.setRol(rol);
            }
            session.persist(usuario);
            if(rol != null){
                rol.getUsuarios().add(usuario);
                rol =(Rol) session.merge(rol);
             }
            session.getTransaction().commit();
        }catch(Exception e){
            logger.info("\n.......Error en el create de Usuario.......\n");
            e.printStackTrace();
      
        }//catch
    }
//------------------------------------------------------------------------------
    @Override
    public void delete(Usuario usuario, Session session) throws NonexistentEntityException {
        
        try{
            session.beginTransaction();
            Usuario eliminarUsuario;
            try{
                eliminarUsuario = (Usuario) session.get(Usuario.class, usuario.getIdUsuario());
                eliminarUsuario.getIdUsuario();
            }catch(EntityNotFoundException e){
                throw new NonexistentEntityException("El usuario que intenta eliminar ya no existe", e);
            }
            Rol rol = eliminarUsuario.getRol();
            if(rol != null){
                rol.getUsuarios().remove(eliminarUsuario);
                rol = (Rol)session.merge(rol);
            }
            session.delete(eliminarUsuario);
            session.getTransaction().commit();
        }finally{
            if(session.isOpen()){
                session.close();
            }
        }
    
    
    }
//------------------------------------------------------------------------------

    @Override
    public void update(Usuario usuario, Session session)throws NonexistentEntityException, Exception {
        
        try{
            session.beginTransaction();
            Usuario persistirUsuario = (Usuario)session.get(Usuario.class, usuario.getIdUsuario());
            Rol oldRol = persistirUsuario.getRol();
            Rol newRol = usuario.getRol();
            if(newRol != null){
                newRol = (Rol)session.get(Rol.class, newRol.getIdRol());
                usuario.setRol(newRol);
            }
            usuario = (Usuario)session.merge(usuario);
            
            if(oldRol != null && !oldRol.equals(newRol)){
                oldRol.getUsuarios().remove(usuario);
                oldRol = (Rol)session.merge(oldRol);
            }
            if(newRol != null && !newRol.equals(oldRol)){
                newRol.getUsuarios().add(usuario);
                newRol = (Rol)session.merge(newRol);
            }
            session.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    
    }//update
//------------------------------------------------------------------------------

    @Override
    public Usuario findById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//------------------------------------------------------------------------------

    @Override
    public Usuario findByUsuario(Usuario usuario) {
        Usuario model = null;
        SessionFactory factory = null;
        Session session = null;
        String sql = "FROM Usuario WHERE nomUsuario = '"+usuario.getNomUsuario()+"'";
        
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
             logger.info("\n........"+ sql +"......\n");
            session.beginTransaction();
            model = (Usuario)session.createQuery(sql).uniqueResult();
            session.getTransaction().commit();
            
        }catch(Exception e){
            logger.info("\n.......Error en el findByUsuario de UsuarioDAOImple.......\n");
            e.printStackTrace();
        } 
        return model;
    }
//------------------------------------------------------------------------------

    @Override
    public Usuario login(Usuario usuario) {
        Usuario model = this.findByUsuario(usuario);
        //logger.info("\n.......         "+model.getNomUsuario()+"      .......\n");
        //logger.info("\n.......         "+model.getClave()+"      .......\n");
        
        if(model != null){
            if(!model.getClave().equals(Encriptar.sha512(usuario.getClave()))){
                model = null;
            }
        }
        return model;
    }
    
}
