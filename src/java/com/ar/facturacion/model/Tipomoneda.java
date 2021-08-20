package com.ar.facturacion.model;
// Generated 02/02/2020 19:45:42 by Hibernate Tools 4.3.1


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Tipomoneda generated by hbm2java
 */
@Entity
@Table(name="tipomoneda",catalog="tesis")
public class Tipomoneda  implements java.io.Serializable {
     private String idMoneda;
     private String descripcion;
     private String fchDesde;
     private String fchHasta;
     private List<Cbtesasoc> cbtesasocs = new ArrayList<Cbtesasoc>();
     private Moncotiz moncotiz;

    public Tipomoneda() {
    }

	
    public Tipomoneda(String idMoneda, String descripcion, String fchDesde, String fchHasta) {
        this.idMoneda = idMoneda;
        this.descripcion = descripcion;
        this.fchDesde = fchDesde;
        this.fchHasta = fchHasta;
    }
    public Tipomoneda(String idMoneda, String descripcion, String fchDesde, String fchHasta, List<Cbtesasoc> cbtesasocs, Moncotiz moncotiz) {
       this.idMoneda = idMoneda;
       this.descripcion = descripcion;
       this.fchDesde = fchDesde;
       this.fchHasta = fchHasta;
       this.cbtesasocs = cbtesasocs;
       this.moncotiz = moncotiz;
    }
   
    @Id 
    @Column(name="idMoneda", unique=true, nullable=false, length=3)
    public String getIdMoneda() {
        return this.idMoneda;
    }
    
    public void setIdMoneda(String idMoneda) {
        this.idMoneda = idMoneda;
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

    @OneToMany(fetch=FetchType.LAZY, mappedBy="tipomoneda")
    public List<Cbtesasoc> getCbtesasocs() {
        return this.cbtesasocs;
    }
    
    public void setCbtesasocs(List<Cbtesasoc> cbtesasocs) {
        this.cbtesasocs = cbtesasocs;
    }

    @OneToOne(fetch=FetchType.LAZY, mappedBy="tipomoneda")
    public Moncotiz getMoncotiz() {
        return this.moncotiz;
    }
    
    public void setMoncotiz(Moncotiz moncotiz) {
        this.moncotiz = moncotiz;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.idMoneda);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tipomoneda other = (Tipomoneda) obj;
        if (!Objects.equals(this.idMoneda, other.idMoneda)) {
            return false;
        }
        return true;
    }




}

