/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.impl.exceptions.NonexistentEntityException;
import com.ar.facturacion.model.Usuario;
import java.util.List;
import org.hibernate.Session;

public interface IUsuarioDAO {
     public List<Usuario> getUsuarioList();
    public void create(Usuario usuario, Session session);
    public void delete(Usuario usuario, Session session)throws NonexistentEntityException;
    public void update(Usuario usuario, Session session)throws NonexistentEntityException, Exception;
    public Usuario findById(int id);
    public Usuario findByUsuario(Usuario usuario);
    public Usuario login(Usuario usuario);
}
