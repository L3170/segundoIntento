/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.dao;

import com.ar.facturacion.model.Comprador;
import com.ar.facturacion.model.Cbtesasoc;
import com.ar.facturacion.model.Producto;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author Leonardo
 */
public interface ICbtesAsocDAO {
    public List<Cbtesasoc> getCbtesAsoc(Session session);
    public List<Cbtesasoc> getCbtesNoInformados(Session session);
    public void create(Cbtesasoc comprobante, Session session);
    public void update(Cbtesasoc comprobante, Session session);
    public List<Cbtesasoc> findByComprador(Comprador comprador, Session session);
    public Cbtesasoc findById(int id, Session session);
    public Comprador getClienteByDNI(String dni, Session session);
    public Producto getProductoById(Integer codigo, Session session);
}
