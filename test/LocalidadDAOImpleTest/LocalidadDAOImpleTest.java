/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocalidadDAOImpleTest;

import com.ar.facturacion.dao.ILocalidadDAO;
import com.ar.facturacion.impl.LocalidadDAOImple;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.ar.facturacion.model.Localidad;
import com.ar.facturacion.model.Provincia;
import com.ar.facturacion.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


/**
 *
 * @author LEONARDO
 */
public class LocalidadDAOImpleTest {
    
    public LocalidadDAOImpleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    //=============================================================================
    @Test
    public void testCreate(){
        try{
        Localidad localidad = new Localidad();
        Provincia provincia = new Provincia();
        provincia.setIdProvincia("AR-S");
        provincia.setNomProvincia("Santa Fe");
        provincia.setUserUpdate(1);
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        
        
        localidad.setIdLocalidad(3001);
        localidad.setNomLocalidad("EVAEVA");
        localidad.setProvincia(provincia);
        localidad.setUserUpdate(1);
        
        ILocalidadDAO lDao = new LocalidadDAOImple();
        lDao.create(localidad,session);
        
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }//testCreateProvincia
    
 //=============================================================================
}
