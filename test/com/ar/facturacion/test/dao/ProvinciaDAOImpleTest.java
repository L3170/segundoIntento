/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.test.dao;

import com.ar.facturacion.dao.IProvinciaDAO;
import com.ar.facturacion.impl.ProvinciaDAOImple;
import com.ar.facturacion.model.Provincia;
import com.ar.facturacion.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author LEONARDO
 */
public class ProvinciaDAOImpleTest {
        IProvinciaDAO dao;
        
        
    public ProvinciaDAOImpleTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        dao = new ProvinciaDAOImple();
        
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    /*
    @Test
    public void testGetProvinciaList(){
        List<Provincia> provinciaList = new ArrayList<Provincia>();
        provinciaList = dao.getProvinciaList();
        for(Provincia prov: provinciaList){
            System.out.println(prov);
        }
    }//testGetProvinciaList
*/
//=============================================================================
    @Test
    public void testCreate(){
        try{
        Provincia provincia = new Provincia();
        provincia.setIdProvincia("AR-PRUEBA");
        provincia.setNomProvincia("PRUEBA");
        Integer i = 1;
        provincia.setUserUpdate(i);
        ////////////////////////////////////////////////
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        ///////////////////////////////////////////////
        dao.create(provincia,session);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
    }//testCreateProvincia
    
 //=============================================================================
    @Test
    public void testDelete(){
    String id = "AR-ARA";
    Provincia provincia= new Provincia();
    /////////////////////////////////////////////////////
    SessionFactory factory = HibernateUtil.getSessionFactory();
    Session session = factory.openSession();
    /////////////////////////////////////////////////////    
    try{
        try{
            provincia = dao.findByIdProvincia(id, session);
        }catch(Exception e){
            System.out.println("Error en el findById");
            e.printStackTrace();
        }
    dao.delete(provincia,session);
    }catch(Exception e){
        e.printStackTrace();
        System.out.println("Error en el delete");
    }
    
    }//testDelete   
    
}//class
