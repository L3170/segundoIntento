
import com.ar.facturacion.dao.IPermisoDAO;
import com.ar.facturacion.dao.IProvinciaDAO;
import com.ar.facturacion.dao.IRolDAO;
import com.ar.facturacion.impl.PermisoDAOImple;
import com.ar.facturacion.impl.RolDAOImple;
import com.ar.facturacion.model.Permiso;
import com.ar.facturacion.model.Provincia;
import com.ar.facturacion.model.Rol;
import com.ar.facturacion.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LEONARDO
 */
public class Principal {
    
    
    
    public static void main(String[] args){
        
        
        SessionFactory factory;
        Session session = null;
        
        try{
         factory = HibernateUtil.getSessionFactory();
         session = factory.openSession();
         session.beginTransaction();
         
         Rol rol = (Rol)session.get(Rol.class, 2);
         IPermisoDAO dao = new PermisoDAOImple();
         List<Permiso> permisos = dao.getPermisoList();
         //System.out.println("LISTA DE TODOS LOS PERMISOS");
         //for(Permiso permiso : permisos){
         //    System.out.println(permiso.getNombre());
        // }

        // List<Permiso> permisosOfRol = new ArrayList<Permiso>();
        // permisosOfRol = rol.getPermisos();
        //-----------------------------------------------------------------------  
         // System.out.println("PERMISOS QUE ESTÁN DENTRO DEL ROL 1:");
         // for(Permiso permiso : permisosOfRol){
         //    System.out.println(permiso.getNombre());
         //}
         //---------------------------------------------------------------------------
         System.out.println("PERMISOS QUE ESTÁN DENTRO DEL ROL:");
          List<Permiso> permisosOfRol = rol.getPermisos();
         for(Permiso permiso : permisos){
             if(permisosOfRol.indexOf(permiso) != -1){
              System.out.println("------------------------------------------------");
             System.out.println(permiso.getNombre());
             }
        }
         
              
        }catch(Exception e){
            e.printStackTrace();
        }
         
        
        
        
        
        
        
        
        
        
        /*
        SessionFactory factory;
        Session session = null;
        IPermisoDAO dao= new PermisoDAOImple();
        try{
         factory = HibernateUtil.getSessionFactory();
         session = factory.openSession();
         session.beginTransaction();
         List<Permiso> permisos = dao.getPermisoList();
         for(Permiso permiso : permisos){
             System.out.println(permiso.getNombre());
         }
       
        
        }catch(Exception e){
            e.printStackTrace();
        }
         */
       
/*
          List<Permiso> permisos = new ArrayList<Permiso>();
         
          SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
          Session session = sessionFactory.openSession();
          Rol rol = (Rol) session.get(Rol.class, 2);
          permisos = rol.getPermisos();
          for(Permiso permiso : permisos){
                System.out.println(permiso.getNombre());
            }
*/
/*
        SessionFactory factory;
        Session session = null;
        IProvinciaDAO dao= new ProvinciaDAOImple();
        try{
         factory = HibernateUtil.getSessionFactory();
         session = factory.openSession();
         session.beginTransaction();
         Provincia provincia = new Provincia();
         provincia.setIdProvincia("AR-PRUEBA");
        provincia.setNomProvincia("PRUEBA");
        Integer i = 1;
        provincia.setUserUpdate(i);
        session.save(provincia);
        session.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
        }
      
  */  
    }
    
}
