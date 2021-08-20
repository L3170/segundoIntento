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
 * Concepto generated by hbm2java
 */
@Entity
@Table(name="concepto",catalog="tesis")
public class Concepto  implements java.io.Serializable {
     private int idConcepto;
     private String descripcion;
     private String fchDesde;
     private String fchHasta;
     private List<Cbtesasoc> cbtesasocs = new ArrayList<Cbtesasoc>();

    public Concepto() {
    }

	
    public Concepto(int idConcepto, String descripcion, String fchDesde, String fchHasta) {
        this.idConcepto = idConcepto;
        this.descripcion = descripcion;
        this.fchDesde = fchDesde;
        this.fchHasta = fchHasta;
    }
    public Concepto(int idConcepto, String descripcion, String fchDesde, String fchHasta, List<Cbtesasoc> cbtesasocs) {
       this.idConcepto = idConcepto;
       this.descripcion = descripcion;
       this.fchDesde = fchDesde;
       this.fchHasta = fchHasta;
       this.cbtesasocs = cbtesasocs;
    }
   
    @Id 
    @Column(name="idConcepto", unique=true, nullable=false)
    public int getIdConcepto() {
        return this.idConcepto;
    }
    
    public void setIdConcepto(int idConcepto) {
        this.idConcepto = idConcepto;
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

    @OneToMany(fetch=FetchType.LAZY, mappedBy="concepto")
    public List<Cbtesasoc> getCbtesasocs() {
        return this.cbtesasocs;
    }
    
    public void setCbtesasocs(List<Cbtesasoc> cbtesasocs) {
        this.cbtesasocs = cbtesasocs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.idConcepto;
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
        final Concepto other = (Concepto) obj;
        if (this.idConcepto != other.idConcepto) {
            return false;
        }
        return true;
    }




}

