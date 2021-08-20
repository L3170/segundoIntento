/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.impl;

import com.ar.facturacion.dao.IFecaereqDAO;
import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Fecaereq;
import com.ar.facturacion.provisorio.exceptions.IllegalOrphanException;
import com.ar.facturacion.provisorio.exceptions.NonexistentEntityException;
import com.ar.facturacion.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Leonardo
 */
public class FecaereqDAOImple implements IFecaereqDAO{
    private static final Logger logger = Logger.getLogger(FecaereqDAOImple.class.getName());

    @Override
    public List<Fecaereq> getFecaereqList() {
    SessionFactory factory = null;
        Session session = null;
        List<Fecaereq> fecaereqList = new ArrayList<Fecaereq>();
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            fecaereqList = session.createQuery("from Fecaereq fecaereq").list();
            session.close();
            
        }catch(Exception e){
            logger.info("\n\n --------------Error al hacer el getFecaereqList en FecaereqDAOImple-----");
            e.printStackTrace();
        }
        return fecaereqList;
    
    }
    

    @Override
    public void create(Fecaereq fecaereq) throws PreexistingEntityException, Exception {
        if(fecaereq.getCbtesasocs()== null){
            fecaereq.setCbtesasocs(new ArrayList<Cbtesasoc>());
        }
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.getTransaction().begin();
            List<Cbtesasoc> almacenarComprobantes = new ArrayList<Cbtesasoc>();
            for(Cbtesasoc comprobante : fecaereq.getCbtesasocs()){
                comprobante = (Cbtesasoc)session.get(Cbtesasoc.class, comprobante.getNumero());
                almacenarComprobantes.add(comprobante);
            }
            fecaereq.setCbtesasocs(almacenarComprobantes);
            session.persist(fecaereq);
            for(Cbtesasoc comprobante : fecaereq.getCbtesasocs()){
                Fecaereq oldFecaereq = comprobante.getFecaereq();
                comprobante.setFecaereq(fecaereq);
                comprobante = (Cbtesasoc)session.merge(comprobante);
                if(oldFecaereq != null && !oldFecaereq.equals(fecaereq)){
                    oldFecaereq.getCbtesasocs().remove(comprobante);
                    oldFecaereq = (Fecaereq)session.merge(oldFecaereq);
                }
            }
            session.getTransaction().commit();
        }catch(Exception e){
            if(findFecaereqById(fecaereq.getIdFeCaereq())!= null){
                throw new PreexistingEntityException("Fecereq "+fecaereq.getIdFeCaereq()+" ya existe.", e);
            }
            throw e;
          
        }
        
    }

    @Override
    public void delete(Fecaereq fecaereq) throws IllegalOrphanException, NonexistentEntityException {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.getTransaction().begin();
            try{
                fecaereq = (Fecaereq)session.get(Fecaereq.class, fecaereq.getIdFeCaereq());
                fecaereq.getIdFeCaereq();
                
            }catch(EntityNotFoundException enfe){
                throw new NonexistentEntityException("El Fecaereq con id "+fecaereq.getIdFeCaereq()+" no existe.",enfe);
            }
            
            List<Cbtesasoc> cbtesasocList = fecaereq.getCbtesasocs();
            for(Cbtesasoc comprobante : cbtesasocList){
                comprobante.setFecaereq(null);
                comprobante = (Cbtesasoc)session.merge(comprobante);
                
            }
           
            session.delete(fecaereq);
            session.getTransaction().commit();
        }finally{
            if(session != null){
                session.close();
            }
        }
    
    }

    @Override
    public void update(Fecaereq fecaereq) throws IllegalOrphanException, NonexistentEntityException, Exception {
        SessionFactory factory = null;
        Session session = null;
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            session.getTransaction().begin();
            Fecaereq persistirFecaereq = (Fecaereq)session.get(Fecaereq.class, fecaereq.getIdFeCaereq());
            List<Cbtesasoc> newComprobantes = fecaereq.getCbtesasocs();
            List<Cbtesasoc> oldComprobantes = persistirFecaereq.getCbtesasocs();
            List<Cbtesasoc> persistirCbtesasoc = new ArrayList<Cbtesasoc>();
            for(Cbtesasoc  newComprobante : newComprobantes){
                newComprobante = (Cbtesasoc)session.get(Cbtesasoc.class, newComprobante.getNumero());
                persistirCbtesasoc.add(newComprobante);
            }
            newComprobantes = persistirCbtesasoc;
            fecaereq.setCbtesasocs(newComprobantes);
            fecaereq = (Fecaereq)session.merge(fecaereq);
            
            for(Cbtesasoc oldComprobante : oldComprobantes){
                if(!newComprobantes.contains(oldComprobante)){
                    oldComprobante.setFecaereq(null);
                    oldComprobante = (Cbtesasoc)session.merge(oldComprobante);
                }
            }
            for(Cbtesasoc newComprobante : newComprobantes){
                if(!oldComprobantes.contains(newComprobante)){
                    Fecaereq oldFecaereq = newComprobante.getFecaereq();
                    fecaereq.getCbtesasocs().add(newComprobante);
                    newComprobante = (Cbtesasoc)session.merge(newComprobante);
                    if(oldFecaereq != null && !oldFecaereq.equals(fecaereq)){
                        oldFecaereq.getCbtesasocs().remove(newComprobante);
                        oldFecaereq = (Fecaereq)session.merge(oldFecaereq);
                    }
                }
                
            }
            session.getTransaction().commit();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    
    
    }

    @Override
    public Fecaereq findFecaereqById(Integer id) {
        SessionFactory factory = null;
        Session session = null;
        Fecaereq fecaereq = new Fecaereq();
        try{
            factory = HibernateUtil.getSessionFactory();
            session = factory.openSession();
            fecaereq = (Fecaereq)session.get(Fecaereq.class, id);
        }catch(Exception e){
            e.printStackTrace();
        }
        return fecaereq;
    }
    
    
    
}
