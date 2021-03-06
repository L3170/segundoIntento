package com.ar.facturacion.model;
// Generated 02/02/2020 19:45:42 by Hibernate Tools 4.3.1


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Ivatipo generated by hbm2java
 */
@Entity
@Table(name="ivatipo",catalog="tesis")
public class Ivatipo  implements java.io.Serializable {
     private int idIva;
     private String descripcion;
     private String fchDesde;
     private String fchHasta;
     private List<Iva> ivas = new ArrayList<Iva>();

    public Ivatipo() {
    }
    public Ivatipo(int idIva, String descripcion, String fchDesde, String fchHasta) {
        this.idIva = idIva;
        this.descripcion = descripcion;
        this.fchDesde = fchDesde;
        this.fchHasta = fchHasta;
    }
    public Ivatipo(int idIva, String descripcion, String fchDesde, String fchHasta, List<Iva> ivas) {
       this.idIva = idIva;
       this.descripcion = descripcion;
       this.fchDesde = fchDesde;
       this.fchHasta = fchHasta;
       this.ivas = ivas;
    }
   
    @Id 
    @Column(name="idIva", unique=true, nullable=false)
    public int getIdIva() {
        return this.idIva;
    }
    
    public void setIdIva(int idIva) {
        this.idIva = idIva;
    }

    
    @Column(name="descripcion", nullable=false, length=250)
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    
    @Column(name="fchDesde", nullable=false, length=8)
    public String getFchDesde() {
        return this.fchDesde;
    }
    
    public void setFchDesde(String fchDesde) {
        this.fchDesde = fchDesde;
    }

    
    @Column(name="fchHasta", nullable=false, length=8)
    public String getFchHasta() {
        return this.fchHasta;
    }
    
    public void setFchHasta(String fchHasta) {
        this.fchHasta = fchHasta;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="ivatipo")
    public List<Iva> getIvas() {
        return this.ivas;
    }
    
    public void setIvas(List<Iva> ivas) {
        this.ivas = ivas;
    }




}


