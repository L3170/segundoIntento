/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.test.bean;

import com.ar.facturacion.bean.ProvinciaLocalidadBean;
import com.ar.facturacion.model.Provincia;
import java.util.List;
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
public class ProvinciaLocalidadBeanTest {
    
    ProvinciaLocalidadBean bean = new ProvinciaLocalidadBean();
    
    public ProvinciaLocalidadBeanTest() {
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
    @Test
    public void testGetProvinciaList(){
        List<Provincia> provincias = bean.getProvinciaList();
        for(Provincia prov : provincias){
            System.out.println(prov);
        }
   
    }
    @Test
    public void testUpdateProvincia(){
        
        
        
    }
    
    
    
    
}
