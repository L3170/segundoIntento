/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Categoria;
import com.ar.facturacion.model.Producto;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public interface IProductoDAO {
    public List<Producto> getProductoList(Session session);
    public void create(Producto producto, Session session);
    public void delete(Producto producto, Session session)throws IllegalOrphanException, NonexistentEntityException;
    public void update(Producto producto, Session session)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Producto findById(Integer id, Session session);
    public List<Producto> findByCategoria(Categoria Categoria, Session session);
}
