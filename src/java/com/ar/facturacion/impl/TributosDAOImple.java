/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.ITributoDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.model.Tipotributo;
import com.ar.facturacion.model.Tributos;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Session;

/**
 *
 * @author Leonardo
 */
public class TributosDAOImple implements ITributoDAO, Serializable{

    @Override
    public void createTributo(Tributos tributos, Session session) throws PreexistingEntityException, Exception {
    try{
        Producto producto = tributos.getProducto();
        if(producto != null){
            producto = (Producto)session.get(Producto.class, producto.getIdProducto());
            tributos.setProducto(producto);
        }
        Tipotributo tipo = tributos.getTipotributo();
        if(tipo != null){
            tipo = (Tipotributo)session.get(Tipotributo.class, tipo.getIdTipoTributo());
            tributos.setTipotributo(tipo);
        }
        session.persist(tributos);
        if(producto != null){
            producto.getTributos().add(tributos);
            producto = (Producto)session.merge(producto);
        }
        if(tipo != null){
            tipo.getTributos().add(tributos);
            tipo = (Tipotributo)session.merge(tipo);
        }
        session.getTransaction().commit();
        
    }catch(Exception e){
        e.printStackTrace();
    
    }
    
    }

    @Override
    public void delete(Tributos tributos, Session session) throws IllegalOrphanException, NonexistentEntityException {
        try{
            tributos = (Tributos)session.get(Tributos.class, tributos.getIdTributo());
            tributos.getIdTributo();
        }catch(EntityNotFoundException enfe){
            throw new NonexistentEntityException("El tributo con el id "+tributos.getIdTributo()+" ya no existe.", enfe);
        }
        Tipotributo tipoTributo = tributos.getTipotributo();
        if(tipoTributo != null){
            tipoTributo.getTributos().remove(tributos);
            tipoTributo = (Tipotributo)session.merge(tipoTributo);
        }
        Producto producto = tributos.getProducto();
        if(producto != null){
            producto.getTributos().remove(tributos);
            producto = (Producto)session.merge(producto);
        }
        session.delete(tributos);
        session.getTransaction().commit();
    }

    @Override
    public void update(Tributos tributos, Session session) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try{
            Tributos oldTributo = (Tributos)session.get(Tributos.class, tributos.getIdTributo());
            Producto oldProducto = oldTributo.getProducto();
            Producto newProducto = tributos.getProducto();
            Tipotributo oldTipo = oldTributo.getTipotributo();
            Tipotributo newTipo = tributos.getTipotributo();
            
            if(newTipo != null){
                newTipo = (Tipotributo)session.get(Tipotributo.class, newTipo.getIdTipoTributo());
                tributos.setTipotributo(newTipo);
            }
            if(newProducto != null){
                newProducto = (Producto)session.get(Producto.class, newProducto.getIdProducto());
                tributos.setProducto(newProducto);
            }
            tributos = (Tributos)session.merge(tributos);
            if(oldTipo != null && !oldTipo.equals(newTipo)){
                oldTipo.getTributos().remove(tributos);
                oldTipo = (Tipotributo)session.merge(oldTipo);
            }
            if(newTipo != null && !newTipo.equals(oldTipo)){
                newTipo.getTributos().add(tributos);
                newTipo = (Tipotributo)session.merge(newTipo);
            }
            if(oldProducto != null && !oldProducto.equals(newProducto)){
                oldProducto.getTributos().remove(tributos);
                oldProducto = (Producto)session.merge(oldProducto);
            }
            if(newProducto != null && !newProducto.equals(oldProducto)){
                newProducto.getTributos().add(tributos);
                newProducto = (Producto)session.merge(newProducto);
            }
            session.getTransaction().commit();
            }catch(Exception e){
                String msg = e.getLocalizedMessage();
                if(msg == null || msg.length() == 0){
                    if(findById(tributos.getIdTributo(),session) == null){
                        throw new NonexistentEntityException ("El tributo con id "+tributos.getIdTributo()+" ya no existe.");
                    }
                }
             throw e;
            }
    
    }

    @Override
    public Tributos findById(Integer id, Session session) {
        Tributos tributos = new Tributos();
        try{
            tributos = (Tributos)session.get(Tributos.class, tributos.getIdTributo());
        }catch(Exception e){
            e.printStackTrace();
        }
    return tributos;
    }

    @Override
    public List<Tributos> findByProducto(Producto producto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
