/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Leonardo
 */
@ManagedBean(name = "erroresBean")

public class ErroresBean {
    IllegalOrphanException productosAbandonados;
            NonexistentEntityException objetoNoEncontrado;
    
}
