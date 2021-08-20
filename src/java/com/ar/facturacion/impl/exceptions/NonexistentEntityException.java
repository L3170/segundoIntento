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
public class NonexistentEntityException extends Exception{
    public NonexistentEntityException(String message, Throwable cause){
            super(message, cause);
    }
    public NonexistentEntityException(String message){
        super(message);
    }
    
    
    
    
}
