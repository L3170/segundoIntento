package com.ar.facturacion.model;
// Generated 16/02/2020 19:22:30 by Hibernate Tools 4.3.1


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
 *Compradors generated by hbm2java
 */
@Entity
@Table(name="compradores",catalog="tesis")
public class Comprador  implements java.io.Serializable {
     private String docNro;
     private Doctipo doctipo;
     private Localidad localidad;
     private double porcentaje;
     private String nomComprador;
     private String apellido;
     private String direccion;
     private String piso;
     private String dpto;
     private Date fchNacimiento;
     private String telefono;
     private String celular;
     private String email;
     private Date lastUpdate;
     private int userUpdate;
     private List<Cbtesasoc> cbtesasocList = new ArrayList<Cbtesasoc>();
     private List<Pago> pagos = new ArrayList<Pago>();

    public Comprador() {
    }

	
    public Comprador(String docNro, Doctipo doctipo, Localidad localidad, double porcentaje, String nomComprador, String apellido, String direccion, String piso, String dpto, Date fchNacimiento, String telefono, String celular, String email, Date lastUpdate, int userUpdate) {
        this.docNro = docNro;
        this.doctipo = doctipo;
        this.localidad = localidad;
        this.porcentaje = porcentaje;
        this.nomComprador = nomComprador;
        this.apellido = apellido;
        this.direccion = direccion;
        this.piso = piso;
        this.dpto = dpto;
        this.fchNacimiento = fchNacimiento;
        this.telefono = telefono;
        this.celular = celular;
        this.email = email;
        this.lastUpdate = lastUpdate;
        this.userUpdate = userUpdate;
    }
    public Comprador(String docNro, Doctipo doctipo, Localidad localidad, double porcentaje, String nomComprador, String apellido, String direccion, String piso, String dpto, Date fchNacimiento, String telefono, String celular, String email, Date lastUpdate, int userUpdate, List<Cbtesasoc> cbtesasocs, List<Pago> pagos) {
       this.docNro = docNro;
       this.doctipo = doctipo;
       this.localidad = localidad;
       this.porcentaje = porcentaje;
       this.nomComprador = nomComprador;
       this.apellido = apellido;
       this.direccion = direccion;
       this.piso = piso;
       this.dpto = dpto;
       this.fchNacimiento = fchNacimiento;
       this.telefono = telefono;
       this.celular = celular;
       this.email = email;
       this.lastUpdate = lastUpdate;
       this.userUpdate = userUpdate;
       this.cbtesasocList = cbtesasocList;
       this.pagos = pagos;
    }
   
    @Id 
    @Column(name="docNro", unique=true, nullable=false, length=80)
    public String getDocNro() {
        return this.docNro;
    }
    
    public void setDocNro(String docNro) {
        this.docNro = docNro;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="docTipo", nullable=false)
    public Doctipo getDoctipo() {
        return this.doctipo;
    }
    
    public void setDoctipo(Doctipo doctipo) {
        this.doctipo = doctipo;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="localidad", nullable=false)
    public Localidad getLocalidad() {
        return this.localidad;
    }
    
    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    
    @Column(name="porcentaje", nullable=false, precision=2)
    public double getPorcentaje() {
        return this.porcentaje;
    }
    
    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    
    @Column(name="nomComprador", nullable=false, length=100)
    public String getNomComprador() {
        return this.nomComprador;
    }
    
    public void setNomComprador(String nomComprador) {
        this.nomComprador = nomComprador;
    }

    
    @Column(name="apellido", nullable=false, length=100)
    public String getApellido() {
        return this.apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    
    @Column(name="direccion", nullable=false, length=100)
    public String getDireccion() {
        return this.direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    
    @Column(name="piso", nullable=false, length=10)
    public String getPiso() {
        return this.piso;
    }
    
    public void setPiso(String piso) {
        this.piso = piso;
    }

    
    @Column(name="dpto", nullable=false, length=10)
    public String getDpto() {
        return this.dpto;
    }
    
    public void setDpto(String dpto) {
        this.dpto = dpto;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fchNacimiento", nullable=false, length=19)
    public Date getFchNacimiento() {
        return this.fchNacimiento;
    }
    
    public void setFchNacimiento(Date fchNacimiento) {
        this.fchNacimiento = fchNacimiento;
    }

    
    @Column(name="telefono", nullable=false, length=30)
    public String getTelefono() {
        return this.telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    
    @Column(name="celular", nullable=false, length=30)
    public String getCelular() {
        return this.celular;
    }
    
    public void setCelular(String celular) {
        this.celular = celular;
    }

    
    @Column(name="email", nullable=false, length=50)
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="lastUpdate", nullable=false, length=19)
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

    @OneToMany(fetch=FetchType.LAZY, mappedBy="comprador")
    public List<Cbtesasoc> getCbtesasocs() {
        return this.cbtesasocList;
    }
    
    public void setCbtesasocs(List<Cbtesasoc> cbtesasocList) {
        this.cbtesasocList = cbtesasocList;
    }

    @OneToMany(fetch=FetchType.LAZY, mappedBy="comprador")
    public List<Pago> getPagos() {
        return this.pagos;
    }
    
    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.docNro);
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
        final Comprador other = (Comprador) obj;
        if (!Objects.equals(this.docNro, other.docNro)) {
            return false;
        }
        return true;
    }




}


