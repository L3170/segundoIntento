package com.ar.facturacion.model;
// Generated 16/02/2020 19:22:30 by Hibernate Tools 4.3.1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Iva generated by hbm2java
 */
@Entity
@Table(name="iva",catalog="tesis")
public class Iva  implements java.io.Serializable {
     private int idIva;
     private Ivatipo ivatipo;
     private Producto producto;
     private String descripcion;
     private double baseImp;
     private double importe;

    public Iva() {
    }

    public Iva(int idIva, Ivatipo ivatipo, Producto producto, String descripcion, double baseImp, double importe) {
       this.idIva = idIva;
       this.ivatipo = ivatipo;
       this.producto = producto;
       this.descripcion = descripcion;
       this.baseImp = baseImp;
       this.importe = importe;
    }
   
    @Id 
    @Column(name="idIva", unique=true, nullable=false)
    public int getIdIva() {
        return this.idIva;
    }
    
    public void setIdIva(int idIva) {
        this.idIva = idIva;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="tipoIva", nullable=false)
    public Ivatipo getIvatipo() {
        return this.ivatipo;
    }
    
    public void setIvatipo(Ivatipo ivatipo) {
        this.ivatipo = ivatipo;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="idProducto", nullable=false)
    public Producto getProducto() {
        return this.producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    
    @Column(name="Descripcion", nullable=false, length=250)
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    
    @Column(name="BaseImp", nullable=false, precision=13)
    public double getBaseImp() {
        return this.baseImp;
    }
    
    public void setBaseImp(double baseImp) {
        this.baseImp = baseImp;
    }

    
    @Column(name="Importe", nullable=false, precision=13)
    public double getImporte() {
        return this.importe;
    }
    
    public void setImporte(double importe) {
        this.importe = importe;
    }

}

