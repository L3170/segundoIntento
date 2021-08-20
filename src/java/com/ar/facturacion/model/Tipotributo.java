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
 * Tipotributo generated by hbm2java
 */
@Entity
@Table(name="tipotributo",catalog="tesis")
public class Tipotributo  implements java.io.Serializable {


     private int idTipoTributo;
     private String descripcion;
     private String fchDesde;
     private String fchHasta;
     private List<Tributos> tributos = new ArrayList<Tributos>();

    public Tipotributo() {
    }
    public Tipotributo(int idTipoTributo, String descripcion, String fchDesde, String fchHasta) {
        this.idTipoTributo = idTipoTributo;
        this.descripcion = descripcion;
        this.fchDesde = fchDesde;
        this.fchHasta = fchHasta;
    }
    public Tipotributo(int idTipoTributo, String descripcion, String fchDesde, String fchHasta, List<Tributos> tributos) {
       this.idTipoTributo = idTipoTributo;
       this.descripcion = descripcion;
       this.fchDesde = fchDesde;
       this.fchHasta = fchHasta;
       this.tributos = tributos;
    }
   
    @Id 
    @Column(name="id_Tipo_Tributo", unique=true, nullable=false)
    public int getIdTipoTributo() {
        return this.idTipoTributo;
    }
    
    public void setIdTipoTributo(int idTipoTributo) {
        this.idTipoTributo = idTipoTributo;
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

    @OneToMany(fetch=FetchType.LAZY, mappedBy="tipotributo")
    public List<Tributos> getTributos() {
        return this.tributos;
    }
    
    public void setTributos(List<Tributos> tributos) {
        this.tributos = tributos;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.idTipoTributo;
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
        final Tipotributo other = (Tipotributo) obj;
        if (this.idTipoTributo != other.idTipoTributo) {
            return false;
        }
        return true;
    }




}

