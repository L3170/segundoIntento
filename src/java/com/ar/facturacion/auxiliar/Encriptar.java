/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.auxiliar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author LEONARDO
 */
public class Encriptar {
    
    public static String sha512(String str){
    StringBuilder sb = new StringBuilder();
    
    try{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(str.getBytes());
        byte[] mb = md.digest();
        for(int i = 0; i < mb.length; i++){
         sb.append(Integer.toString((mb[i] & 0xff)+ 0x100, 16).substring(1));
        }
    
    }catch(NoSuchAlgorithmException ex){
        ex.printStackTrace();
    }
    return sb.toString();
    }
    
    
    
    
    
}
