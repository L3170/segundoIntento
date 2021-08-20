/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Rol;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author LEONARDO
 */
public interface IRolDAO {
public List<Rol> getRolList(Session session);
    public void create(Rol Rol);
    public void delete(Rol rol)throws NonexistentEntityException;
    public void update(Rol rol)throws NonexistentEntityException, Exception;
    public Rol findRolById(int id);
    public Rol login(Rol rol);}
