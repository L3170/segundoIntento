package com.ar.facturacion.model;
// Generated 02/02/2020 19:45:42 by Hibernate Tools 4.3.1


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Localidad generated by hbm2java
 */
@Entity
@Table(name="localidad",catalog="tesis")
public class Localidad  implements java.io.Serializable {
     private Integer idLocalidad;
     private Provincia provincia;
     private String nomLocalidad;
     private Date lastUpdate;
     private int userUpdate;
    
     private List<Comprador> compradores = new ArrayList<Comprador>();

    public Localidad() {
    }

	
    public Localidad(Integer idLocalidad, Provincia provincia, String nomLocalidad, Date lastUpdate, int userUpdate) {
        this.idLocalidad = idLocalidad;
        this.provincia = provincia;
        this.nomLocalidad = nomLocalidad;
        this.lastUpdate = lastUpdate;
        this.userUpdate = userUpdate;
        
    }
    public Localidad(Integer idLocalidad, Provincia provincia, String nomLocalidad, Date lastUpdate, int userUpdate, List<Comprador> compradores) {
       this.idLocalidad = idLocalidad;
       this.provincia = provincia;
       this.nomLocalidad = nomLocalidad;
       this.lastUpdate = lastUpdate;
       this.userUpdate = userUpdate;
       this.compradores = compradores;
    }
   
    @Id 
    @Column(name="idLocalidad", unique=true, nullable=false)
    public Integer getIdLocalidad() {
        return this.idLocalidad;
    }
    
    public void setIdLocalidad(Integer idLocalidad) {
        this.idLocalidad = idLocalidad;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="idProvincia", nullable=false)
    public Provincia getProvincia() {
        return this.provincia;
    }
    
    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    
    @Column(name="nomLocalidad", nullable=false, length=50)
    public String getNomLocalidad() {
        return this.nomLocalidad;
    }
    
    public void setNomLocalidad(String nomLocalidad) {
        this.nomLocalidad = nomLocalidad;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="lastUpdate", length=19)
    public Date getLastUpdate() {
        return this.lastUpdate;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    
    @Column(name="userUpdate", nullable=false)
    public int getUserUpdate() {
        return this.userUpdate;
    }
    
    public void setUserUpdate(int userUpdate) {
        this.userUpdate = userUpdate;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="localidad")
    public List<Comprador> getCompradores() {
        return this.compradores;
    }
    
    public void setCompradores(List<Comprador> compradores) {
        this.compradores = compradores;
    }




}


