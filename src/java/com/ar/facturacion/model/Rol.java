package com.ar.facturacion.model;
// Generated 02/02/2020 19:45:42 by Hibernate Tools 4.3.1


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Rol generated by hbm2java
 */
@Entity
@Table(name="rol",catalog="tesis")
public class Rol  implements java.io.Serializable {
     private int idRol;
     private String nomRol;
     private String descripcion;
     private int userAdmin;
     private Date fchModificacion;
     private List<Permiso> permisos = new ArrayList<Permiso>();
     private List<Usuario> usuarios = new ArrayList<Usuario>();

    public Rol() {
    }

	
    public Rol(int idRol, String nomRol, String descripcion, int userAdmin, Date fchModificacion) {
        this.idRol = idRol;
        this.nomRol = nomRol;
        this.descripcion = descripcion;
        this.userAdmin = userAdmin;
        this.fchModificacion = fchModificacion;
    }
    public Rol(int idRol, String nomRol, String descripcion, int userAdmin, Date fchModificacion, List<Permiso> permisos, List<Usuario> usuarios) {
       this.idRol = idRol;
       this.nomRol = nomRol;
       this.descripcion = descripcion;
       this.userAdmin = userAdmin;
       this.fchModificacion = fchModificacion;
       this.permisos = permisos;
       this.usuarios = usuarios;
    }
   
    @Id 
    @Column(name="idRol", unique=true, nullable=false)
    public int getIdRol() {
        return this.idRol;
    }
    
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    
    @Column(name="nomRol", nullable=false, length=50)
    public String getNomRol() {
        return this.nomRol;
    }
    
    public void setNomRol(String nomRol) {
        this.nomRol = nomRol;
    }

    
    @Column(name="descripcion", nullable=false, length=65535)
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    
    @Column(name="userAdmin", nullable=false)
    public int getUserAdmin() {
        return this.userAdmin;
    }
    
    public void setUserAdmin(int userAdmin) {
        this.userAdmin = userAdmin;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fchModificacion")
    public Date getFchModificacion() {
        return this.fchModificacion;
    }
    
    public void setFchModificacion(Date fchModificacion) {
        this.fchModificacion = fchModificacion;
    }

    
   
/*
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="rol_permiso", catalog="tesis", joinColumns = { 
        @JoinColumn(name="idRol", nullable=false, updatable=false) }, inverseJoinColumns = { 
        @JoinColumn(name="idPermiso", nullable=false, updatable=false) })
*/
    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="rol_permiso",
            joinColumns = @JoinColumn(name="idRol"),
            inverseJoinColumns = @JoinColumn(name="idPermiso")
    )
    public List<Permiso> getPermisos() {
        return this.permisos;
    }
    
    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="rol")
    public List<Usuario> getUsuarios() {
        return this.usuarios;
    }
    
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }




}


