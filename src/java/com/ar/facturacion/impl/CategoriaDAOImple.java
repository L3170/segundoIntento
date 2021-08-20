/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.ICategoriaDAO;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Categoria;
import com.ar.facturacion.model.Producto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public class CategoriaDAOImple implements Serializable, ICategoriaDAO {

    private static final Logger logger = Logger.getLogger(CategoriaDAOImple.class.getName());

    @Override
    public List<Categoria> getCategoriaList(Session session) {

        List<Categoria> categoriaList = new ArrayList<Categoria>();
        try {
            categoriaList = session.createQuery("from Categoria categoria").list();

        } catch (Exception e) {
            logger.info("\n\n --------------Error al hacer el getCategoriaList en CategoriaDAOImple-----");
            e.printStackTrace();
        }
        return categoriaList;
    }

    @Override
    public void create(Categoria categoria, Session session) {
        if (categoria.getProductos() == null) {
            categoria.setProductos(new ArrayList<Producto>());
        }

        try {
            session.beginTransaction();
            List<Producto> almacenarProductos = new ArrayList<Producto>();
            for (Producto p : categoria.getProductos()) {
                p = (Producto) session.get(Producto.class, p.getIdProducto());
                almacenarProductos.add(p);
            }
            categoria.setProductos(almacenarProductos);
            session.persist(categoria);
            for (Producto p : categoria.getProductos()) {
                Categoria oldCategoria = p.getCategoria();
                p.setCategoria(categoria);
                p = (Producto) session.merge(p);
                if (oldCategoria != null) {
                    oldCategoria.getProductos().remove(p);
                    oldCategoria = (Categoria) session.merge(oldCategoria);
                }
            }
            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            if (findByIdCategoria(categoria.getCodCategoria(), session) != null) {
                try {
                    throw new PreexistingEntityException("La Categoria " + categoria.getDescripcion() + " ya existe.", e);
                } catch (PreexistingEntityException ex) {
                    Logger.getLogger(CategoriaDAOImple.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            throw e;
        }

    }
//------------------------------------------------------------------------

    @Override
    public void delete(Categoria categoria, Session session) throws IllegalOrphanException, NonexistentEntityException {
        try {
            session.getTransaction().begin();
            //session.beginTransaction();//NO FUNCIONA DE ESTA FORMA
            try {
                categoria = (Categoria) session.get(Categoria.class, categoria.getCodCategoria());
                categoria.getCodCategoria();
            } catch (EntityNotFoundException e) {
                throw new NonexistentEntityException("La categoria con el id " + categoria.getCodCategoria() + " no existe", e);
            }
            List<String> illegalOrphanMessages = null;
            List<Producto> verificarProductos = categoria.getProductos();
            for (Producto pro : verificarProductos) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("La categoria " + categoria.getNomCategoria()
                        + " no puede eliminarse ya que el producto " + pro.getNomProducto()
                        + "pertenece a dicha categoria.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            session.delete(categoria);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//-----------------------------------------------------------------

    @Override
    public void update(Categoria categoria, Session session) throws IllegalOrphanException,NonexistentEntityException,Exception {
        
        try {
            session.beginTransaction();//AGREGADO RECIENTEMENTE
            Categoria persistirCategoria = (Categoria) session.get(Categoria.class, categoria.getCodCategoria());
            List<Producto> oldProductoList = persistirCategoria.getProductos();
            List<Producto> newProductoList = categoria.getProductos();
            List<String> illegalOrphanMessages = null;
            for (Producto oldProducto : oldProductoList) {
                if (!newProductoList.contains(oldProducto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("La categoria del producto " + oldProducto.getNomProducto() + " no puede ser nula.");
                }
            }
            if (illegalOrphanMessages != null) {
                logger.info("\n\n --------------Error al modificar categoria productos huerfanos-----");
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Producto> almacenarProductos = new ArrayList<Producto>();
            for (Producto producto : newProductoList) {
                producto = (Producto) session.get(Producto.class, producto.getIdProducto());
                almacenarProductos.add(producto);
            }
            newProductoList = almacenarProductos;
            categoria.setProductos(newProductoList);
            categoria = (Categoria) session.merge(categoria);
            logger.info("\n\n --------------LISTA DE LOS PRODUCTOS-----");
                
            for(Producto pr : categoria.getProductos()){
                logger.info("\n\n ------------------"+pr.getIdProducto()+" "+pr.getNomProducto()+" "+pr.getCategoria().getNomCategoria());
                
            }
            
            for (Producto nuevo : newProductoList) {
                   //ESTE ES EL IF QUE ESTA FALLANDO
                if (oldProductoList.contains(nuevo)) {
                
                    Categoria oldCategoria = nuevo.getCategoria();
                    nuevo.setCategoria(categoria);
                    nuevo = (Producto) session.merge(nuevo);
                    if (oldCategoria != null && !oldCategoria.equals(categoria)) {
                        oldCategoria.getProductos().remove(nuevo);
                        oldCategoria = (Categoria) session.merge(oldCategoria);
                    }
                }else{
                logger.info("\n\n ---NO ENTRO---");
                    
                }
            }
             for(Producto pr : categoria.getProductos()){
             logger.info("\n\n ------------------"+pr.getIdProducto()+" "+pr.getNomProducto()+" "+pr.getCategoria().getNomCategoria());
                
            }
           
            session.getTransaction().commit();
            
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categoria.getCodCategoria();
                if (findByIdCategoria(id, session) == null) {
                    throw new NonexistentEntityException("La categoria con id " + id + " no existe.");
                }
            }
            throw e;

        }

    }
//-----------------------------------------------------------------

    @Override
    public Categoria findByIdCategoria(Integer id, Session session) {
        Categoria categoria = null;
        try {
            categoria = (Categoria) session.get(Categoria.class, id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoria;
    }

}
