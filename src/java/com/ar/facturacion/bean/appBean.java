/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import static com.ar.facturacion.util.MyUtil.baseUrl;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author LEONARDO
 */
@ManagedBean(name = "appBean")
@ApplicationScoped
public class appBean {

    /**
     * Creates a new instance of appBean
     */
    public appBean() {
    }
    
    public String getBaseUrl(){
        return baseUrl();
    }
    
    
    
}
