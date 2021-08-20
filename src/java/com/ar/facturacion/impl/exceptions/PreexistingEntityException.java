/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl.exceptions;

/**
 *
 * @author LEONARDO
 */
public class PreexistingEntityException extends Exception{
    
    public PreexistingEntityException(String message, Throwable cause){
        super(message, cause);
    }
    
    public PreexistingEntityException(String message){
        super(message);
    }
    
    
}
