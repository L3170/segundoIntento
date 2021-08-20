/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Iva;
import com.ar.facturacion.model.Producto;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author Leonardo
 */
public interface IIvaDAO {
    
    public void createIva(Iva iva, Session session)throws PreexistingEntityException, Exception;
    public void delete(Iva iva, Session session)throws IllegalOrphanException, NonexistentEntityException;
    public void update(Iva iva, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Iva findById(Integer id, Session session);
    public List<Iva> findByProducto(Producto producto);
}
