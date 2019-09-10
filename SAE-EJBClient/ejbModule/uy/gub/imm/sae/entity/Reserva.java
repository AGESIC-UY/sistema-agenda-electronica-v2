/*
 * SAE - Sistema de Agenda Electronica
 * Copyright (C) 2009  IMM - Intendencia Municipal de Montevideo
 *
 * This file is part of SAE.

 * SAE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.enumerados.TipoCancelacion;

@Entity
@Table (name = "ae_reservas")
public class Reserva implements Serializable {

	private static final long serialVersionUID = 3500715468120358550L;

	private Integer id;
	private String serie;
	private Integer numero;
	private Estado estado; //Si está cancelada (C) hay que tomar en cuenta el campo fechaLiberacion
	private String observaciones;
	private Date fechaCreacion; //En GMT
	private Date fechaActualizacion; //En GMT
	private Integer version;
	private Llamada llamada;
	private String origen;
	private String ucrea;
	private Date fcancela; //En GMT
	private String ucancela;
	private TipoCancelacion tcancela;
	private Date fechaLiberacion; //Cuándo se libera el cupo, en GMT
	
	private String codigoSeguridad; //Código utilizado para la cencelación
	private String trazabilidadGuid; //Identificador unico asignado por el sistema de Trazabilidad del PEU
	
	private String tramiteCodigo; //Código del trámite en TrámitesUy
	private String tramiteNombre; //Nombre del trámite en TrámitesUy

	private List<Disponibilidad> disponibilidades; //Es una lista pero solo debería haber una
	private Set<DatoReserva> datosReserva;
	private List<Atencion> atenciones;
	
	private TokenReserva token;
	
	private String ipOrigen;
	
	public Reserva () {
		estado = Estado.P;
		fechaCreacion = new Date();
		fechaActualizacion = fechaCreacion;
		disponibilidades = new ArrayList<Disponibilidad>();
		datosReserva = new HashSet<DatoReserva>();
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_reserva")
	@SequenceGenerator (name ="seq_reserva", initialValue = 1, sequenceName = "s_ae_reserva",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column ()
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column (nullable = false, length=1)
	@Enumerated (EnumType.STRING)
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	@Column (length = 100)
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	@OneToMany (mappedBy = "reserva", fetch = FetchType.EAGER)
	public Set<DatoReserva> getDatosReserva() {
		return datosReserva;
	}
	public void setDatosReserva(Set<DatoReserva> datosReserva) {
		this.datosReserva = datosReserva;
	}
	
	// Se agrega la lista de atencion para poder hacer el reporte de vino - No vino
	@XmlTransient
	@OneToMany (mappedBy="reserva")
	public List <Atencion> getAtenciones(){
		return atenciones;
	}
	
	public void setAtenciones(List<Atencion> atenciones){
		this.atenciones = atenciones;
	}
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "ae_reservas_disponibilidades",
			   inverseJoinColumns={@JoinColumn (name = "aedi_id")},
			   joinColumns={@JoinColumn (name = "aers_id")}
	)
	public List<Disponibilidad> getDisponibilidades() {
		return disponibilidades;
	}
	
	public void setDisponibilidades(List<Disponibilidad> disponibilidades) {
		this.disponibilidades = disponibilidades;
	}

	@Column (name = "fcrea", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Column (name = "fact", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	
	@XmlTransient
	@OneToOne(optional=true, mappedBy="reserva")
	public Llamada getLlamada() {
		return llamada;
	}

	public void setLlamada(Llamada llamada) {
		this.llamada = llamada;
	}

	@Version
	@Column(name="version", nullable=false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		String strDisp = "disponibilidades=";
		for (Iterator<Disponibilidad> iterator = disponibilidades.iterator(); iterator.hasNext();) {
			Disponibilidad disp = iterator.next();
			strDisp+= disp.toString()+",";			
		}
		String strDatos = "datos=";
		for (Iterator<DatoReserva> iterator = datosReserva.iterator(); iterator.hasNext();) {
			DatoReserva dato = iterator.next();
			strDatos+=dato.toString()+",";
		}
		return "Reserva [id="+ id + "," + strDisp + "," + strDatos +"]";
	}

	@Column (name = "origen", length = 1)
	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}
	
	@Column (name = "ucrea", length = 30)
	public String getUcrea() {
		return ucrea;
	}

	public void setUcrea(String ucrea) {
		this.ucrea = ucrea;
	}

	public String getUcancela() {
		return ucancela;
	}

	public void setUcancela(String ucancela) {
		this.ucancela = ucancela;
	}

	@Column(name="codigo_seguridad")
	public String getCodigoSeguridad() {
		if(codigoSeguridad == null) {
			return "0000";
		}
		return codigoSeguridad;
	}

	public void setCodigoSeguridad(String codigoSeguridad) {
		this.codigoSeguridad = codigoSeguridad;
	}

  @Column(name="tramite_codigo")
	public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  @Column(name="tramite_nombre")
  public String getTramiteNombre() {
    return tramiteNombre;
  }

  public void setTramiteNombre(String tramiteNombre) {
    this.tramiteNombre = tramiteNombre;
  }

  public String getSerie() {
    return serie!=null?serie:"";
  }

  public void setSerie(String serie) {
    this.serie = serie;
  }

	@Column(name="trazabilidad_guid")
	public String getTrazabilidadGuid() {
		return trazabilidadGuid;
	}

	public void setTrazabilidadGuid(String trazabilidadGuid) {
		this.trazabilidadGuid = trazabilidadGuid;
	}
	
  @Enumerated (EnumType.STRING)
  public TipoCancelacion getTcancela() {
    return tcancela;
  }

  public void setTcancela(TipoCancelacion tcancela) {
    this.tcancela = tcancela;
  }

  public Date getFcancela() {
    return fcancela;
  }

  public void setFcancela(Date fcancela) {
    this.fcancela = fcancela;
  }

  @ManyToOne (optional = true, cascade={CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn (name = "aetr_id", nullable = true)
  public TokenReserva getToken() {
    return token;
  }

  public void setToken(TokenReserva token) {
    this.token = token;
  }

  @Column (name = "ip_origen")
  public String getIpOrigen() {
    return ipOrigen;
  }

  public void setIpOrigen(String ipOrigen) {
    this.ipOrigen = ipOrigen;
  }

  @Column (name = "flibera")
  public Date getFechaLiberacion() {
    return fechaLiberacion;
  }

  public void setFechaLiberacion(Date fechaLiberacion) {
    this.fechaLiberacion = fechaLiberacion;
  }

  @Transient
  public String getEstadoDescripcion(){
    return getEstadoDescripcion(estado);
  }
  
  @Transient
  public String getEstadoDescripcion(Estado estado){
    return (estado != null ? estado.getDescripcion() : "");
  }

  @Transient
  public String getTipoDocumento() {
    String tipoDocumento = "";
    for(DatoReserva dato : getDatosReserva()) {
      DatoASolicitar datoSol = dato.getDatoASolicitar();
      if("TipoDocumento".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
        tipoDocumento = dato.getValor();
      }
    }
    return tipoDocumento;
  }

  @Transient
  public String getNumeroDocumento() {
    String documento = "";
    for(DatoReserva dato : getDatosReserva()) {
      DatoASolicitar datoSol = dato.getDatoASolicitar();
      if("NroDocumento".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
        documento = dato.getValor();
      }
    }
    return documento;
  }

  @Transient
  public String getDocumento() {
    String tipoDocumento = null;
    String numeroDocumento = null;
    for(DatoReserva dato : datosReserva) {
      DatoASolicitar datoSol = dato.getDatoASolicitar();
      if("TipoDocumento".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
        tipoDocumento = dato.getValor();
      }
      if("NroDocumento".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
        numeroDocumento = dato.getValor();
      }
    }
    return (tipoDocumento!=null?tipoDocumento+" ":"")+(numeroDocumento!=null?numeroDocumento:"Sin documento");
  }
  
  @Transient
	public Boolean getPresencial() {
	  return(disponibilidades.isEmpty()? null : disponibilidades.get(0).getPresencial());
	}
  
  @Transient
  public Date getFechaHora() {
    return (disponibilidades.isEmpty()? null : disponibilidades.get(0).getHoraInicio());
  }
  
  
}
