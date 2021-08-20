/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Modopago;
import java.util.List;

/**
 *
 * @author LEONARDO
 */
public interface IModopagoDAO {
     public List<Modopago> getModopagoList();
    public void create(Modopago modopago);
    public void delete(Modopago modopago)throws IllegalOrphanException, NonexistentEntityException;
    public void update(Modopago modopago)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Modopago findById(Integer id);
    
}
