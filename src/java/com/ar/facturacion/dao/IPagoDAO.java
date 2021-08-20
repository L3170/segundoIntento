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
import com.ar.facturacion.model.Pago;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public interface IPagoDAO {
    public List<Pago> getPagos(Session session);
    public void create(Pago pago, Session session)throws PreexistingEntityException, Exception;
    public void delete(Pago pago,Session session)throws IllegalOrphanException, NonexistentEntityException;
    public void update(Pago pago, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Pago findById(Integer id);
    public List<Pago> findByCliente(Comprador comprador);
    
}
