/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.model.Tributos;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author Leonardo
 */
public interface ITributoDAO {
    public void createTributo(Tributos tributos, Session session)throws PreexistingEntityException, Exception;
    public void delete(Tributos tributos, Session session)throws IllegalOrphanException, NonexistentEntityException;
    public void update(Tributos tributos, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Tributos findById(Integer id, Session session);
    public List<Tributos> findByProducto(Producto producto);
}
