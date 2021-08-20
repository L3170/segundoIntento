/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Comprador;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public interface ICompradorDAO {
    public List<Comprador> getCompradorList(Session session);
    public void create(Comprador comprador, Session session)throws PreexistingEntityException, Exception;
    public void delete(Comprador comprador, Session session)throws IllegalOrphanException, NonexistentEntityException;
    public void update(Comprador comprador, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Comprador findByIdComprador(String id, Session session);
    
    
}
