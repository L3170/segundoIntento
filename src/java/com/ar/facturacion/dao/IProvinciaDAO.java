/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Provincia;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public interface IProvinciaDAO {
    public List<Provincia> getProvinciaList(Session session);
    public void create(Provincia provincia, Session session)throws PreexistingEntityException, Exception;
    public void delete(Provincia provincia, Session session)throws IllegalOrphanException, NonexistentEntityException;
    public void update(Provincia provincia, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Provincia findByIdProvincia(String id, Session session);
}
