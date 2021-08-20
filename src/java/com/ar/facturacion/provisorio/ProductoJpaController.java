/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.provisorio;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ar.facturacion.model.Categoria;
import com.ar.facturacion.model.Tributos;
import com.ar.facturacion.model.Iva;
import java.util.ArrayList;
import java.util.List;
import com.ar.facturacion.model.Detalle;
import com.ar.facturacion.model.Producto;
import com.ar.facturacion.provisorio.exceptions.IllegalOrphanException;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leonardo
 */
public class ProductoJpaController implements Serializable {

    public ProductoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producto producto) {
        if (producto.getIvas() == null) {
            producto.setIvas(new ArrayList<Iva>());
        }
        if (producto.getDetalles() == null) {
            producto.setDetalles(new ArrayList<Detalle>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria categoria = producto.getCategoria();
            if (categoria != null) {
                categoria = em.getReference(categoria.getClass(), categoria.getCodCategoria());
                producto.setCategoria(categoria);
            }
            Tributos tributos = producto.getTributos();
            if (tributos != null) {
                tributos = em.getReference(tributos.getClass(), tributos.getIdTributo());
                producto.setTributos(tributos);
            }
            List<Iva> attachedIvas = new ArrayList<Iva>();
            for (Iva ivasIvaToAttach : producto.getIvas()) {
                ivasIvaToAttach = em.getReference(ivasIvaToAttach.getClass(), ivasIvaToAttach.getIdIva());
                attachedIvas.add(ivasIvaToAttach);
            }
            producto.setIvas(attachedIvas);
            List<Detalle> attachedDetalles = new ArrayList<Detalle>();
            for (Detalle detallesDetalleToAttach : producto.getDetalles()) {
                detallesDetalleToAttach = em.getReference(detallesDetalleToAttach.getClass(), detallesDetalleToAttach.getId());
                attachedDetalles.add(detallesDetalleToAttach);
            }
            producto.setDetalles(attachedDetalles);
            em.persist(producto);
            if (categoria != null) {
                categoria.getProductos().add(producto);
                categoria = em.merge(categoria);
            }
            if (tributos != null) {
                Producto oldProductoOfTributos = tributos.getProducto();
                if (oldProductoOfTributos != null) {
                    oldProductoOfTributos.setTributos(null);
                    oldProductoOfTributos = em.merge(oldProductoOfTributos);
                }
                tributos.setProducto(producto);
                tributos = em.merge(tributos);
            }
            for (Iva ivasIva : producto.getIvas()) {
                Producto oldProductoOfIvasIva = ivasIva.getProducto();
                ivasIva.setProducto(producto);
                ivasIva = em.merge(ivasIva);
                if (oldProductoOfIvasIva != null) {
                    oldProductoOfIvasIva.getIvas().remove(ivasIva);
                    oldProductoOfIvasIva = em.merge(oldProductoOfIvasIva);
                }
            }
            for (Detalle detallesDetalle : producto.getDetalles()) {
                Producto oldProductoOfDetallesDetalle = detallesDetalle.getProducto();
                detallesDetalle.setProducto(producto);
                detallesDetalle = em.merge(detallesDetalle);
                if (oldProductoOfDetallesDetalle != null) {
                    oldProductoOfDetallesDetalle.getDetalles().remove(detallesDetalle);
                    oldProductoOfDetallesDetalle = em.merge(oldProductoOfDetallesDetalle);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Producto producto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto persistentProducto = em.find(Producto.class, producto.getIdProducto());
            Categoria categoriaOld = persistentProducto.getCategoria();
            Categoria categoriaNew = producto.getCategoria();
            Tributos tributosOld = persistentProducto.getTributos();
            Tributos tributosNew = producto.getTributos();
            List<Iva> ivasOld = persistentProducto.getIvas();
            List<Iva> ivasNew = producto.getIvas();
            List<Detalle> detallesOld = persistentProducto.getDetalles();
            List<Detalle> detallesNew = producto.getDetalles();
            List<String> illegalOrphanMessages = null;
            for (Iva ivasOldIva : ivasOld) {
                if (!ivasNew.contains(ivasOldIva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Iva " + ivasOldIva + " since its producto field is not nullable.");
                }
            }
            for (Detalle detallesOldDetalle : detallesOld) {
                if (!detallesNew.contains(detallesOldDetalle)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalle " + detallesOldDetalle + " since its producto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (categoriaNew != null) {
                categoriaNew = em.getReference(categoriaNew.getClass(), categoriaNew.getCodCategoria());
                producto.setCategoria(categoriaNew);
            }
            if (tributosNew != null) {
                tributosNew = em.getReference(tributosNew.getClass(), tributosNew.getIdTributo());
                producto.setTributos(tributosNew);
            }
            List<Iva> attachedIvasNew = new ArrayList<Iva>();
            for (Iva ivasNewIvaToAttach : ivasNew) {
                ivasNewIvaToAttach = em.getReference(ivasNewIvaToAttach.getClass(), ivasNewIvaToAttach.getIdIva());
                attachedIvasNew.add(ivasNewIvaToAttach);
            }
            ivasNew = attachedIvasNew;
            producto.setIvas(ivasNew);
            List<Detalle> attachedDetallesNew = new ArrayList<Detalle>();
            for (Detalle detallesNewDetalleToAttach : detallesNew) {
                detallesNewDetalleToAttach = em.getReference(detallesNewDetalleToAttach.getClass(), detallesNewDetalleToAttach.getId());
                attachedDetallesNew.add(detallesNewDetalleToAttach);
            }
            detallesNew = attachedDetallesNew;
            producto.setDetalles(detallesNew);
            producto = em.merge(producto);
            if (categoriaOld != null && !categoriaOld.equals(categoriaNew)) {
                categoriaOld.getProductos().remove(producto);
                categoriaOld = em.merge(categoriaOld);
            }
            if (categoriaNew != null && !categoriaNew.equals(categoriaOld)) {
                categoriaNew.getProductos().add(producto);
                categoriaNew = em.merge(categoriaNew);
            }
            if (tributosOld != null && !tributosOld.equals(tributosNew)) {
                tributosOld.setProducto(null);
                tributosOld = em.merge(tributosOld);
            }
            if (tributosNew != null && !tributosNew.equals(tributosOld)) {
                Producto oldProductoOfTributos = tributosNew.getProducto();
                if (oldProductoOfTributos != null) {
                    oldProductoOfTributos.setTributos(null);
                    oldProductoOfTributos = em.merge(oldProductoOfTributos);
                }
                tributosNew.setProducto(producto);
                tributosNew = em.merge(tributosNew);
            }
            for (Iva ivasNewIva : ivasNew) {
                if (!ivasOld.contains(ivasNewIva)) {
                    Producto oldProductoOfIvasNewIva = ivasNewIva.getProducto();
                    ivasNewIva.setProducto(producto);
                    ivasNewIva = em.merge(ivasNewIva);
                    if (oldProductoOfIvasNewIva != null && !oldProductoOfIvasNewIva.equals(producto)) {
                        oldProductoOfIvasNewIva.getIvas().remove(ivasNewIva);
                        oldProductoOfIvasNewIva = em.merge(oldProductoOfIvasNewIva);
                    }
                }
            }
            for (Detalle detallesNewDetalle : detallesNew) {
                if (!detallesOld.contains(detallesNewDetalle)) {
                    Producto oldProductoOfDetallesNewDetalle = detallesNewDetalle.getProducto();
                    detallesNewDetalle.setProducto(producto);
                    detallesNewDetalle = em.merge(detallesNewDetalle);
                    if (oldProductoOfDetallesNewDetalle != null && !oldProductoOfDetallesNewDetalle.equals(producto)) {
                        oldProductoOfDetallesNewDetalle.getDetalles().remove(detallesNewDetalle);
                        oldProductoOfDetallesNewDetalle = em.merge(oldProductoOfDetallesNewDetalle);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = producto.getIdProducto();
                if (findProducto(id) == null) {
                    throw new NonexistentEntityException("The producto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getIdProducto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Iva> ivasOrphanCheck = producto.getIvas();
            for (Iva ivasOrphanCheckIva : ivasOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the Iva " + ivasOrphanCheckIva + " in its ivas field has a non-nullable producto field.");
            }
            List<Detalle> detallesOrphanCheck = producto.getDetalles();
            for (Detalle detallesOrphanCheckDetalle : detallesOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the Detalle " + detallesOrphanCheckDetalle + " in its detalles field has a non-nullable producto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Categoria categoria = producto.getCategoria();
            if (categoria != null) {
                categoria.getProductos().remove(producto);
                categoria = em.merge(categoria);
            }
            
            em.remove(producto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }

    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }

    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producto.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Producto findProducto(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producto> rt = cq.from(Producto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
