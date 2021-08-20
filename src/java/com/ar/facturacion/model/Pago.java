package com.ar.facturacion.model;
// Generated 02/02/2020 19:45:42 by Hibernate Tools 4.3.1


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Pago generated by hbm2java
 */
@Entity
@Table(name="pago",catalog="tesis")
public class Pago  implements java.io.Serializable {
     private int idPago;
     private Comprador comprador;
     private Modopago modopago;
     private String codCupon;
     private double monto;
     private String detalle;
     private Date fechaPago;
     private int userUpdate;
     

    public Pago() {
    }

    public Pago(int idPago, Comprador comprador, Modopago modopago, String codCupon, double monto, String detalle, Date fechaPago, int userUpdate, String estado) {
       this.idPago = idPago;
       this.comprador = comprador;
       this.modopago = modopago;
       this.codCupon = codCupon;
       this.monto = monto;
       this.detalle = detalle;
       this.fechaPago = fechaPago;
       this.userUpdate = userUpdate;
    
    }
   
    @Id 
    @Column(name="idPago", unique=true, nullable=false)
    public int getIdPago() {
        return this.idPago;
    }
    
    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Cliente", nullable=false)
    public Comprador getComprador() {
        return this.comprador;
    }
    
    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="modoPago", nullable=false)
    public Modopago getModopago() {
        return this.modopago;
    }
    
    public void setModopago(Modopago modopago) {
        this.modopago = modopago;
    }

    
    @Column(name="codCupon", nullable=false, length=250)
    public String getCodCupon() {
        return this.codCupon;
    }
    
    public void setCodCupon(String codCupon) {
        this.codCupon = codCupon;
    }

    
    @Column(name="monto", nullable=false, precision=13)
    public double getMonto() {
        return this.monto;
    }
    
    public void setMonto(double monto) {
        this.monto = monto;
    }

    
    @Column(name="detalle", nullable=false, length=65535)
    public String getDetalle() {
        return this.detalle;
    }
    
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fechaPago", nullable=false, length=19)
    public Date getFechaPago() {
        return this.fechaPago;
    }
    
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    
    @Column(name="userUpdate", nullable=false)
    public int getUserUpdate() {
        return this.userUpdate;
    }
    
    public void setUserUpdate(int userUpdate) {
        this.userUpdate = userUpdate;
    }


}

