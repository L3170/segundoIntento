package com.ar.facturacion.model;
// Generated 02/02/2020 19:45:42 by Hibernate Tools 4.3.1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Opcional generated by hbm2java
 */
@Entity
@Table(name="opcional",catalog="tesis")
public class Opcional  implements java.io.Serializable {
     private int id;
     private String valor;

    public Opcional() {
    }

    public Opcional(int id, String valor) {
       this.id = id;
       this.valor = valor;
    }
   
    @Id 
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    
    @Column(name="valor", nullable=false, length=100)
    public String getValor() {
        return this.valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
    }

}


