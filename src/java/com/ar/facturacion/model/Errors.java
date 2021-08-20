package com.ar.facturacion.model;
// Generated 02/02/2020 19:45:42 by Hibernate Tools 4.3.1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Errors generated by hbm2java
 */
@Entity
@Table(name="errors",catalog="tesis")
public class Errors  implements java.io.Serializable {
     private int id;
     private String msg;

    public Errors() {
    }

    public Errors(int id, String msg) {
       this.id = id;
       this.msg = msg;
    }
   
    @Id 
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    
    @Column(name="Msg", nullable=false, length=250)
    public String getMsg() {
        return this.msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }




}


