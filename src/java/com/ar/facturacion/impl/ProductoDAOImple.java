/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IProductoDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Categoria;
import com.ar.facturacion.model.Detalle;
import com.ar.facturacion.model.Iva;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.model.Tributos;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public class ProductoDAOImple implements Serializable, IProductoDAO {

    private static final Logger logger = Logger.getLogger(ProductoDAOImple.class.getName());

    
    @Override
    public List<Producto> getProductoList(Session session) {
        List<Producto> productoList = new ArrayList<Producto>();
        try{
            productoList = session.createQuery("From Producto producto").list();
            
        }catch(Exception e){
            logger.info("\n.......Error al hace el getProductoList.......\n");
            e.printStackTrace();
        }
        return productoList;
    }

    @Override
    public void create(Producto producto, Session session) {
        if(producto.getIvas() == null){
            producto.setIvas(new ArrayList<Iva>());
        }
        if(producto.getTributos() == null){
            producto.setTributos(new ArrayList<Tributos>());
        }
        if(producto.getDetalles() == null){
            producto.setDetalles(new ArrayList<Detalle>());
        }
        
        try{
            session.beginTransaction();
            Categoria categoria = producto.getCategoria();
            if(categoria != null){
                categoria = (Categoria)session.get(Categoria.class, categoria.getCodCategoria());
                producto.setCategoria(categoria);
            }
            List<Tributos> almacenarTributos = new ArrayList<Tributos>();
            for(Tributos tributo : producto.getTributos()){
                tributo = (Tributos)session.get(Tributos.class, tributo.getIdTributo());
                almacenarTributos.add(tributo);
            }
            producto.setTributos(almacenarTributos);
            
            List<Iva> almacenarIvas = new ArrayList<Iva>();
            for(Iva iva : producto.getIvas()){
                iva = (Iva)session.get(Iva.class, iva.getIdIva());
                almacenarIvas.add(iva);
            }
            producto.setIvas(almacenarIvas);
            
            List<Detalle> detalleList = new ArrayList<Detalle>();
            for(Detalle det : producto.getDetalles()){
                det = (Detalle)session.get(Detalle.class,det.getId());
                detalleList.add(det);
            }
            producto.setDetalles(detalleList);
            session.persist(producto);
            if(categoria != null){
                categoria.getProductos().add(producto);
                categoria = (Categoria)session.merge(categoria);
            }
            for(Tributos tributo : producto.getTributos()){
                Producto oldProducto = tributo.getProducto();
                tributo.setProducto(producto);
                tributo = (Tributos)session.merge(tributo);
                if(oldProducto != null){
                    oldProducto.getTributos().remove(tributo);
                    oldProducto = (Producto)session.merge(oldProducto);
                }
            }
            //EL TIPO DE IVA PUEDE ESTAR EN VARIOS PRODUCTOS... PERO EL IVA ES EL MONTO APLICADO A SOLO UN PRODUCTO
            // Y EL PRODUCTO PUEDE TENER VARIOS IVAS... ES DECIR VARIOS MONTOS UNICOS...
            //POR LO QUE DOS PRODUCTO NO PUEDE TENER EL MISMO IVA.
            for(Iva iva : producto.getIvas()){
                Producto oldProducto = iva.getProducto();//SI EL IVA ESTABA ASIGNADO A OTRO PRODUCTO (PUEDE SER POR ERROR O POR LO QUE FUERE)
                iva.setProducto(producto);
                iva = (Iva)session.merge(iva);
                if(oldProducto != null){
                    oldProducto.getIvas().remove(iva);
                    oldProducto = (Producto)session.merge(oldProducto);
                }
            }
            
            for(Detalle det : producto.getDetalles()){
                Producto oldProducto = det.getProducto();
                det.setProducto(producto);
                det = (Detalle)session.merge(det);
                if(oldProducto != null){
                    oldProducto.getDetalles().remove(det);
                    oldProducto = (Producto)session.merge(oldProducto);
                }
            }
            
            session.getTransaction().commit();
        }catch(Exception e){
            logger.info("-----------     ERROR   --------------");
            logger.info(producto.getCategoria().getNomCategoria());
            e.printStackTrace();
        }
   
    }
//--------------------------------------------------------------------
    @Override
    public void delete(Producto producto, Session session) throws IllegalOrphanException, NonexistentEntityException {
        
        try{
            session.getTransaction().begin();
            //session.beginTransaction();
            Producto eliminarProducto;
            try{
                eliminarProducto = (Producto)session.get(Producto.class, producto.getIdProducto());
                eliminarProducto.getIdProducto();
            }catch(EntityNotFoundException e){
                throw new NonexistentEntityException("El producto con id"+producto.getIdProducto()+" no existe",e);
            }
            List<String> illegalOrphanMessages = null;
            List<Iva> ivaSinAsignar = eliminarProducto.getIvas();
            for (Iva iva : ivaSinAsignar) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("Este Producto (" + producto.getNomProducto() + ") no puede destruirse ya que el Iva " + iva.getDescripcion() + " no puede estar sin asignar.");
            }
            
            List<Tributos> tributoSinAsignar = eliminarProducto.getTributos();
            for (Tributos tributo : tributoSinAsignar) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("Este Producto (" + producto.getNomProducto() + ") no puede destruirse ya que el tributo " + tributo.getDescripcion() + " no puede estar sin asignar.");
            }
            List<Detalle> detallesSinAsignar = eliminarProducto.getDetalles();
            for (Detalle detalle : detallesSinAsignar) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("Este Producto (" + producto + ") no puede eliminarse ya que " + detalle.getId() + " no puede estar sin asignar.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            
            
            
            Categoria categoria = producto.getCategoria();
            if(categoria != null){
                categoria.getProductos().remove(eliminarProducto);
                categoria = (Categoria)session.merge(categoria);
            }
            session.delete(eliminarProducto);
            session.getTransaction().commit();
            
        
        }catch(Exception e){
            e.printStackTrace();
        }
    
   }
//--------------------------------------------------------------------

    @Override
    public void update(Producto producto, Session session) throws IllegalOrphanException, NonexistentEntityException, Exception {
        
        try{
            session.beginTransaction();
            Producto persistirProducto = (Producto)session.get(Producto.class, producto.getIdProducto());
            Categoria oldCategoria = persistirProducto.getCategoria();
            Categoria newCategoria = producto.getCategoria();
            List<Tributos> oldTributos = persistirProducto.getTributos();
            List<Tributos> newTributos = producto.getTributos();
            List<Iva> oldIvas = persistirProducto.getIvas();
            List<Iva> newIvas = producto.getIvas();
            
            List<Detalle> oldDetalles = persistirProducto.getDetalles();
            List<Detalle> newDetalles = producto.getDetalles();
            List<String> illegalOrphanMessages = null;
            for(Detalle detalle : oldDetalles){
                    if(!newDetalles.contains(detalle)){
                        if(illegalOrphanMessages == null){
                            illegalOrphanMessages = new ArrayList<String>();
                        }
                        illegalOrphanMessages.add("El detalle "+ detalle +" debe mantenerse ya que el campo producto no acepta nulos");
                    }
            }
            for(Tributos tributo : oldTributos){
                if(!newTributos.contains(tributo)){
                    if(illegalOrphanMessages == null){
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("El tributo "+tributo.getDescripcion()+" debe mantenerse ya que el campo producto no acepta nulos");
                }
            }
            for(Iva iva : oldIvas){
                if(!newIvas.contains(iva)){
                    if(illegalOrphanMessages == null){
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("El iva "+iva.getDescripcion()+" debe mantenerse ya que el campo producto no acepta nulos");
                }
            }
            if(illegalOrphanMessages != null){
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if(newCategoria != null){
                newCategoria = (Categoria)session.get(Categoria.class, newCategoria.getCodCategoria());
                producto.setCategoria(newCategoria);
            }
            List<Iva> almacenarIvas = new ArrayList<Iva>();
            for(Iva iva : newIvas){
                iva = (Iva)session.get(Iva.class, iva.getIdIva());
                almacenarIvas.add(iva);
            }
            newIvas = almacenarIvas;
            producto.setIvas(newIvas);
            List<Tributos> almacenarTributos = new ArrayList<Tributos>();
            for(Tributos tributo : newTributos){
                tributo = (Tributos)session.get(Tributos.class, tributo.getIdTributo());
                almacenarTributos.add(tributo);
            }
            newTributos = almacenarTributos;
            producto.setTributos(newTributos);
            
            List<Detalle> almacenarDetalles = new ArrayList<Detalle>();
            for(Detalle detalle : newDetalles){
                detalle = (Detalle)session.get(Detalle.class, detalle.getId());
                almacenarDetalles.add(detalle);
            }
            newDetalles = almacenarDetalles;
            producto.setDetalles(newDetalles);
            producto = (Producto)session.merge(producto);
            if(oldCategoria != null && ! oldCategoria.equals(newCategoria)){
                oldCategoria.getProductos().remove(producto);
                oldCategoria = (Categoria)session.merge(oldCategoria);
            }
            if(newCategoria != null && ! newCategoria.equals(oldCategoria)){
                newCategoria.getProductos().add(producto);
                newCategoria = (Categoria)session.merge(newCategoria);
            }
            for(Iva iva : newIvas){
                Producto oldProducto = iva.getProducto();
                iva.setProducto(producto);
                iva = (Iva)session.merge(iva);
                if(oldProducto != null && !oldProducto.equals(producto)){
                    oldProducto.getIvas().remove(iva);
                    oldProducto = (Producto)session.merge(oldProducto);
                }
            }
            for(Tributos tributo : newTributos){
                Producto oldProducto = tributo.getProducto();
                tributo.setProducto(producto);
                tributo = (Tributos)session.merge(tributo);
                if(oldProducto != null && !oldProducto.equals(producto)){
                    oldProducto.getTributos().remove(tributo);
                    oldProducto = (Producto)session.merge(oldProducto);
                }
               
            }
            
            for(Detalle detalle : newDetalles){
                if(!oldDetalles.contains(detalle)){
                    Producto productoViejo = detalle.getProducto();
                    detalle.setProducto(producto);
                    detalle = (Detalle)session.merge(detalle);
                    if(productoViejo!= null && !productoViejo.equals(producto)){
                        productoViejo.getDetalles().remove(detalle);
                        productoViejo = (Producto)session.merge(productoViejo);
                    }
                }else{
                    logger.info("-----------  NO ENTRA AL FOR DE DETALLE   --------------");
            
                }
            }
            session.getTransaction().commit();
            
        }catch(Exception e){
            String msg = e.getLocalizedMessage();
            if(msg == null || msg.length() == 0){
                Integer id = producto.getIdProducto();
                if(findById(id, session) == null){
                    throw new NonexistentEntityException("El producto con id"+id+"no existe");
                }
                
            }
            throw e;
            
        
        }
    
    }
//--------------------------------------------------------------------

    @Override
    public Producto findById(Integer id, Session session) {
    
    Producto producto = null;
    try{
        producto = (Producto) session.get(Producto.class, id);
        
    }catch(Exception e){
        e.printStackTrace();
    }
    return producto;
  
    }
//--------------------------------------------------------------------

    @Override
    public List<Producto> findByCategoria(Categoria categoria, Session session) {
        
    List<Producto> productoList = new ArrayList<Producto>();
    try{
        
        Query consulta =session.createQuery("FROM Producto producto "
                                            + "WHERE producto.idCategoria =: id"); 
    consulta.setInteger("id", categoria.getCodCategoria());
    productoList = consulta.list();
    
    }catch(Exception e){
        e.printStackTrace();
        System.out.println("Error en el findByProvincia");
    }
    
    return productoList;
    
    }
//--------------------------------------------------------------------

   
    


}
