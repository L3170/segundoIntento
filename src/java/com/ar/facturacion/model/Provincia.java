package com.ar.facturacion.model;
// Generated 02/02/2020 19:45:42 by Hibernate Tools 4.3.1


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Provincia generated by hbm2java
 */
@Entity
@Table(name="provincia",catalog="tesis")
public class Provincia  implements java.io.Serializable {
     private String idProvincia;
     private String nomProvincia;
     private Date lastUpdate;
     private int userUpdate;
     private List<Localidad> localidads = new ArrayList<Localidad>();

    public Provincia() {
    }
    public Provincia(String idProvincia, String nomProvincia, Date lastUpdate, int userUpdate) {
        this.idProvincia = idProvincia;
        this.nomProvincia = nomProvincia;
        this.lastUpdate = lastUpdate;
        this.userUpdate = userUpdate;
       
    }
    public Provincia(String idProvincia, String nomProvincia, Date lastUpdate, int userUpdate, List<Localidad> localidads) {
       this.idProvincia = idProvincia;
       this.nomProvincia = nomProvincia;
       this.lastUpdate = lastUpdate;
       this.userUpdate = userUpdate;
      this.localidads = localidads;
    }
   
    @Id 
    @Column(name="idProvincia", unique=true, nullable=false, length=10)
    public String getIdProvincia() {
        return this.idProvincia;
    }
    
    public void setIdProvincia(String idProvincia) {
        this.idProvincia = idProvincia;
    }

    
    @Column(name="nomProvincia", nullable=false, length=50)
    public String getNomProvincia() {
        return this.nomProvincia;
    }
    
    public void setNomProvincia(String nomProvincia) {
        this.nomProvincia = nomProvincia;
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

    @OneToMany(fetch=FetchType.LAZY, mappedBy="provincia")
    public List<Localidad> getLocalidads() {
        return this.localidads;
    }
    
    public void setLocalidads(List<Localidad> localidads) {
        this.localidads = localidads;
    }




}

