/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Permiso;
import com.ar.facturacion.model.Rol;
import java.util.List;

/**
 *
 * @author leo
 */
public interface IPermisoDAO {
     public List<Permiso> getPermisoList();
    public void create(Permiso permiso);
    public void delete(Permiso permiso)throws NonexistentEntityException;
    public void update(Permiso permiso)throws NonexistentEntityException, Exception;
    public Permiso findById(int id);
    public Permiso findByPermiso(Permiso permiso);
    public List<Permiso> findByRol(Rol rol);
    
}
