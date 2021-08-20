/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.PreexistingEntityException;
import com.ar.facturacion.model.Fecaereq;
import com.ar.facturacion.impl.exceptions.IllegalOrphanException;
import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import java.util.List;

/**
 *
 * @author Leonardo
 */
public interface IFecaereqDAO {
    public List<Fecaereq> getFecaereqList();
    public void create(Fecaereq fecaereq)throws PreexistingEntityException, Exception;
    public void delete(Fecaereq fecaereq) throws IllegalOrphanException, NonexistentEntityException;
    public void update(Fecaereq fecaereq)throws IllegalOrphanException, NonexistentEntityException, Exception;
    public Fecaereq findFecaereqById(Integer id);
    
    
}
