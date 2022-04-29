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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Table( name = "ae_agrupaciones_datos" )
public class AgrupacionDato implements Serializable{

	private static final long serialVersionUID = 5622518450165783147L;
	
	private Integer id;
	private String nombre;
  private String etiqueta;
	private Integer orden;
	private Date fechaBaja;
	//Indica si se puede eliminar o no (solo las agrupaciones por defecto deberían tener esta bandera en false ya que es utilizada
	//para determinar justamente eso, si es la agrupación por defecto)
	private boolean borrarFlag; 
	private Recurso recurso;
	private List<DatoASolicitar> datosASolicitar;
	
	
	public AgrupacionDato () {
		datosASolicitar = new ArrayList<DatoASolicitar>();
		borrarFlag = true;
	}
	
	/**
	 * Constructor por copia no en profundidad.
	 */
	public AgrupacionDato (AgrupacionDato a) {
		id = a.getId();
		nombre = a.getNombre();
		etiqueta = a.getEtiqueta();
		orden = a.getOrden();
		fechaBaja = a.getFechaBaja();
		borrarFlag = a.getBorrarFlag(); 
		recurso = null;
		datosASolicitar = new ArrayList<DatoASolicitar>();
	}

	AgrupacionDato(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_agrupacionDato")
	@SequenceGenerator (name ="seq_agrupacionDato", initialValue = 1, sequenceName = "s_ae_agrupacion_dato",allocationSize=1)
    public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (nullable=false, length=50)	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
  @Column (nullable=false, length=50) 
  public String getEtiqueta() {
    return etiqueta;
  }
  
  public void setEtiqueta(String etiqueta) {
    this.etiqueta = etiqueta;
  }
  
	@Column (nullable=false)
	public Integer getOrden() {
		return orden;
	}
	
	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	
	public void setFechaBaja(Date fin) {
		this.fechaBaja = fin;
	}

	@Column (nullable=false ,name = "borrar_flag")
	public boolean getBorrarFlag() {
		return borrarFlag;
	}

	public void setBorrarFlag(boolean borrarFlag) {
		this.borrarFlag = borrarFlag;
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

	@OneToMany (mappedBy = "agrupacionDato")
	public List<DatoASolicitar> getDatosASolicitar() {
		return datosASolicitar;
	}
	
	public void setDatosASolicitar(List<DatoASolicitar> datosASolicitar) {
		this.datosASolicitar = datosASolicitar;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AgrupacionDato that = (AgrupacionDato) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
