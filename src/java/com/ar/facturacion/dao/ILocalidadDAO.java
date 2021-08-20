/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Localidad;
import com.ar.facturacion.model.Provincia;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public interface ILocalidadDAO {
    public List<Localidad> getLocalidadList(Session session);
    public void create(Localidad localidad, Session session)throws PreexistingEntityException, Exception;
    public void delete(Localidad localidad, Session session)throws IllegalOrphanException, NonexistentEntityException;
    public void update(Localidad localidad, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Localidad findById(Integer id, Session session);
    public List<Localidad> findByProvincia(Provincia provincia);
    
}
