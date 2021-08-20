/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IIvaDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Iva;
import com.ar.facturacion.model.Ivatipo;
import com.ar.facturacion.model.Producto;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Session;

/**
 *
 * @author Leonardo
 */
public class IvaDAOImple implements IIvaDAO, Serializable{

    @Override
    public void createIva(Iva iva, Session session) throws PreexistingEntityException, Exception {
        try{
            Producto producto = iva.getProducto();
            if(producto != null){
                producto = (Producto)session.get(Producto.class, producto.getIdProducto());
                iva.setProducto(producto);
            }
            Ivatipo ivaTipo = iva.getIvatipo();
            if(ivaTipo != null){
                ivaTipo = (Ivatipo)session.get(Ivatipo.class, ivaTipo.getIdIva());
                iva.setIvatipo(ivaTipo);
            }
            session.persist(iva);
            if(producto != null){
                producto.getIvas().add(iva);
                producto = (Producto)session.merge(producto);
            }
            if(ivaTipo != null){
                ivaTipo.getIvas().add(iva);
                ivaTipo = (Ivatipo)session.merge(ivaTipo);
            }
            session.getTransaction().commit();
            
        }catch(Exception e){
            if(this.findById(iva.getIdIva(), session) != null){
                throw new PreexistingEntityException("Iva" + iva +"ya existe.", e);
            }
            throw e;
        }
    
    }

    @Override
    public void delete(Iva iva, Session session) throws IllegalOrphanException, NonexistentEntityException {
        
        try{
            iva = (Iva)session.get(Iva.class, iva.getIdIva());
            iva.getIdIva();
        }catch(EntityNotFoundException e){
            throw new NonexistentEntityException("El iva al que hace referencia ya no existe.", e);
        }
        Ivatipo ivaTipo = iva.getIvatipo();
        if(ivaTipo != null){
            ivaTipo.getIvas().remove(iva);
            ivaTipo = (Ivatipo)session.merge(ivaTipo);
        }
        Producto producto = iva.getProducto();
        if(producto != null){
            producto.getIvas().remove(iva);
            producto = (Producto)session.merge(producto);
        }
        session.delete(iva);
        session.getTransaction().commit();
    }

    @Override
    public void update(Iva iva, Session session) throws IllegalOrphanException, NonexistentEntityException, Exception {
        
        try{
            Iva newIva = iva;
            Iva oldIva = (Iva)session.get(Iva.class, iva.getIdIva());
            Ivatipo newTipo = newIva.getIvatipo();
            Ivatipo oldTipo = oldIva.getIvatipo();
            Producto oldProducto = oldIva.getProducto();
            Producto newProducto = newIva.getProducto();
            if(newTipo != null){
               newTipo = (Ivatipo)session.get(Ivatipo.class, newTipo.getIdIva());
               newIva.setIvatipo(newTipo);
            }
            if(newProducto != null){
                newProducto = (Producto)session.get(Producto.class, newProducto.getIdProducto());
                newIva.setProducto(newProducto);
            }
            iva = (Iva)session.merge(iva);
            
            if(oldTipo != null && !oldTipo.equals(newTipo)){
                oldTipo.getIvas().remove(iva);
                oldTipo = (Ivatipo)session.merge(oldTipo);
            }
            if(newTipo != null && !newTipo.equals(oldTipo)){
                newTipo.getIvas().add(iva);
                newTipo = (Ivatipo)session.merge(newTipo);
            }
            if(oldProducto != null && !oldProducto.equals(newProducto)){
                oldProducto.getIvas().remove(iva);
                oldProducto = (Producto)session.merge(oldProducto);
            }
            if(newProducto != null && !newProducto.equals(oldProducto)){
                newProducto.getIvas().add(iva);
                newProducto = (Producto)session.merge(newProducto);
            }
            session.getTransaction().commit();
        
        }catch(Exception e){
            String msg = e.getLocalizedMessage();
            if(msg == null || msg.length() == 0){
                int id = iva.getIdIva();
                if(this.findById(id, session) == null){
                    throw new NonexistentEntityException("El IVA con el id "+id+" ya no existe.");
                }
            }
        
        }
    
    }

    @Override
    public Iva findById(Integer id, Session session) {
        Iva iva = new Iva();
        iva = (Iva)session.get(Iva.class, iva.getIdIva());
        return iva;
    }

    @Override
    public List<Iva> findByProducto(Producto producto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
