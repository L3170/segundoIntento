/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Categoria;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public interface ICategoriaDAO {
    
    public List<Categoria> getCategoriaList(Session session);
    public void create(Categoria categoria, Session session);
    public void delete(Categoria categoria, Session session) throws IllegalOrphanException, NonexistentEntityException;
    public void update(Categoria categoria, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Categoria findByIdCategoria(Integer id, Session session);
    
}
