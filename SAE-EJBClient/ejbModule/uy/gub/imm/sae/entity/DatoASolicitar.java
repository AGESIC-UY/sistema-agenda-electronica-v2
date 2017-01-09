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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import uy.gub.imm.sae.common.enumerados.Tipo;

@Entity
@Table (name = "ae_datos_a_solicitar")
public class DatoASolicitar implements Serializable{

	private static final long serialVersionUID = 2571548327892870771L;
	private static final int ANCHO_DESPLIEGUE_DEFECTO = 20;

	
	
	private Integer id;
	private String nombre;
	private String etiqueta;
	private String textoAyuda;
	private Tipo tipo;
	private Integer largo;	
	private Boolean requerido;
	private Boolean esClave;
	private Boolean soloLectura; //Si se especifica el valor en la URL (reserva pública) entonces el campo no es modificable
	private Integer fila;
	private Integer columna = 1; //Solo se admite 1 columna por fila
	private Date fechaBaja;
	private Boolean incluirEnReporte;
	private Integer anchoDespliegue;
	
	
	private Boolean incluirEnLlamador;
	private Integer largoEnLlamador;
	private Integer ordenEnLlamador;
	
  //Indica si se puede eliminar o no (solo los datos a solicitar por defecto deberían tener esta bandera en false ya que es utilizada
  //para determinar justamente eso, si es un dato a solicitar por defecto)
	private Boolean borrarFlag;
	
	private List<DatoReserva> datosReserva;
	private List<ValidacionPorDato> validacionesPorDato;
	private List<AccionPorDato> accionesPorDato;
	private List<ValorPosible> valoresPosibles;
	
	private AgrupacionDato agrupacionDato;
	private Recurso recurso;
	
	public DatoASolicitar () {
		datosReserva = new ArrayList<DatoReserva>();
		validacionesPorDato = new ArrayList<ValidacionPorDato>();
		accionesPorDato = new ArrayList<AccionPorDato>();
		valoresPosibles = new ArrayList<ValorPosible>();

		incluirEnReporte = true;
		anchoDespliegue = ANCHO_DESPLIEGUE_DEFECTO;
		
		incluirEnLlamador = false;
		borrarFlag = true;
		soloLectura = false;
	}
	
	
	/**
	 * Constructor por copia no en profundidad.
	 */
	public DatoASolicitar (DatoASolicitar d) {
		datosReserva = new ArrayList<DatoReserva>();
		validacionesPorDato = new ArrayList<ValidacionPorDato>();
		accionesPorDato = new ArrayList<AccionPorDato>();
		valoresPosibles = new ArrayList<ValorPosible>();
		
		id = d.getId();
		nombre = d.getNombre();
		etiqueta = d.getEtiqueta();
		textoAyuda = d.getTextoAyuda();
		tipo = d.getTipo();
		largo = d.getLargo();
		requerido = d.getRequerido();
		esClave = d.getEsClave();
		soloLectura = d.getSoloLectura();
		fila = d.getFila();
		columna = d.getColumna();
		fechaBaja = d.getFechaBaja();
		incluirEnReporte = d.getIncluirEnReporte();
		anchoDespliegue = d.getAnchoDespliegue();
		incluirEnLlamador = d.getIncluirEnLlamador();
		largoEnLlamador = d.getLargoEnLlamador();
		ordenEnLlamador = d.getOrdenEnLlamador();
		
		if (d.getBorrarFlag()!=null) {
			borrarFlag = d.getBorrarFlag();
		}else {
			borrarFlag = true;
		}
		
		agrupacionDato = new AgrupacionDato(d.getAgrupacionDato());
		recurso = new Recurso(d.getRecurso());
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_datoASolicitar")
	@SequenceGenerator (name ="seq_datoASolicitar", initialValue = 1, sequenceName = "s_ae_datoasolicitar",allocationSize=1)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (nullable = false, length=50)
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column (nullable = false, length=50)
	public String getEtiqueta() {
		return etiqueta;
	}
	
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	@Column (name="texto_ayuda", length=100)
	public String getTextoAyuda() {
		return textoAyuda;
	}
	
	public void setTextoAyuda(String textoAyuda) {
		this.textoAyuda = textoAyuda;
	}
	
	@Column (nullable = false, length=30)
	@Enumerated(EnumType.STRING)
	public Tipo getTipo() {
		return tipo;
	}
	
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	
	public Integer getLargo() {
		return largo;
	}
	
	public void setLargo(Integer largo) {
		this.largo = largo;
	}
	
	@Column (nullable = false)
	public Boolean getRequerido() {
		return requerido;
	}
	
	public void setRequerido(Boolean requerido) {
		this.requerido = requerido;
	}
	
	@Column (name = "es_clave", nullable = false)
	public Boolean getEsClave() {
		return esClave;
	}
	
	public void setEsClave(Boolean esClave) {
		this.esClave = esClave;
	}
	
  @Column (name="solo_lectura", length=100)
	public Boolean getSoloLectura() {
    return soloLectura;
  }

  public void setSoloLectura(Boolean soloLectura) {
    this.soloLectura = soloLectura;
  }

  @Column (nullable = false)
	public Integer getFila() {
		return fila;
	}
  
	public void setFila(Integer fila) {
		this.fila = fila;
	}
	
	@Column (nullable = false)
	public Integer getColumna() {
		return columna;
	}
	
	public void setColumna(Integer columna) {
		this.columna = columna;
	}
	
	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	@Column (name = "incluir_en_reporte", nullable = false)
	public Boolean getIncluirEnReporte() {
		return incluirEnReporte;
	}
	
	public void setIncluirEnReporte(Boolean incluirEnReporte) {
		this.incluirEnReporte = incluirEnReporte;
	}

	@Column (name = "ancho_despliegue", nullable = false)
	public Integer getAnchoDespliegue() {
		return anchoDespliegue;
	}
	
	public void setAnchoDespliegue(Integer anchoDespliegue) {
		this.anchoDespliegue = anchoDespliegue;
	}
	
	@Column (name = "incluir_en_llamador", nullable = false)
	public Boolean getIncluirEnLlamador() {
		if(incluirEnLlamador==null) {
			return false;
		}
		return incluirEnLlamador;
	}
	
	public void setIncluirEnLlamador(Boolean incluirEnLlamador) {
		this.incluirEnLlamador = incluirEnLlamador;
	}

	@Column (name = "largo_en_llamador", nullable = false)
	public Integer getLargoEnLlamador() {
		return largoEnLlamador;
	}
	
	public void setLargoEnLlamador(Integer largoEnLlamador) {
		this.largoEnLlamador = largoEnLlamador;
	}

	@Column (name = "orden_en_llamador", nullable = false)
	public Integer getOrdenEnLlamador() {
		return ordenEnLlamador;
	}
	
	public void setOrdenEnLlamador(Integer ordenEnLlamador) {
		this.ordenEnLlamador = ordenEnLlamador;
	}
	
	@Column (name = "borrar_flag", nullable = false)
	public Boolean getBorrarFlag() {
		return borrarFlag;
	}

	public void setBorrarFlag(Boolean borrarFlag) {
		this.borrarFlag = borrarFlag;
	}
	
	@XmlTransient
	@OneToMany(mappedBy = "datoASolicitar")
	public List<DatoReserva> getDatosReserva() {
		return datosReserva;
	}
	
	public void setDatosReserva(List<DatoReserva> datosReserva) {
		this.datosReserva = datosReserva;
	}
	
	@XmlTransient
	@OneToMany(mappedBy = "datoASolicitar")
	public List<ValidacionPorDato> getValidacionesPorDato() {
		return validacionesPorDato;
	}
	
	public void setValidacionesPorDato(List<ValidacionPorDato> validacionesPorDato) {
		this.validacionesPorDato = validacionesPorDato;
	}

	@XmlTransient
	@OneToMany(mappedBy = "datoASolicitar")
	public List<AccionPorDato> getAccionesPorDato() {
		return accionesPorDato;
	}
	
	public void setAccionesPorDato(List<AccionPorDato> accionesPorDato) {
		this.accionesPorDato = accionesPorDato;
	}
	
	//TODO cambiar a "datoASolicitar"
	@OneToMany(mappedBy = "dato")
	public List<ValorPosible> getValoresPosibles() {
		return valoresPosibles;
	}
	
	public void setValoresPosibles(List<ValorPosible> valoresPosibles) {
		this.valoresPosibles = valoresPosibles;
	}
	
	@XmlTransient
	@ManyToOne (optional = false)
	@JoinColumn (name = "aead_id", nullable = false)
	public AgrupacionDato getAgrupacionDato() {
		return agrupacionDato;
	}
	
	public void setAgrupacionDato(AgrupacionDato agrupacionDato) {
		this.agrupacionDato = agrupacionDato;
	}

	@XmlTransient
	@ManyToOne (optional = false)
	@JoinColumn (name = "aere_id", nullable = false)
	public Recurso getRecurso() {
		return recurso;
	}
	
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DatoASolicitar) {
			DatoASolicitar val = (DatoASolicitar) obj;
			return (val.getId().equals(this.getId()));
		} else {
			return false;
		}
	}

}
