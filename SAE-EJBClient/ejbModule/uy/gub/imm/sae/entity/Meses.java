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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
  
@Entity
@Table (name = "ae_meses")
public class Meses implements Serializable {
    
	private static final long serialVersionUID = -8821849842938576521L;
	
	private Integer id;
    private MesesType mes;
    
    private Plantilla plantilla;
    
	public enum MesesType {ENERO, FEBRERO, MARZO, ABRIL, MAYO, JUNIO, JULIO, AGOSTO, SETIEMBRE, OCTUBRE, NOVIEMBRE, DICIEMBRE}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_mes")
	@SequenceGenerator (name ="seq_mes", initialValue = 1, sequenceName = "s_ae_mes",allocationSize=1)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column (nullable = false)
	@Enumerated (EnumType.ORDINAL)
	public MesesType getMes() {
		return mes;
	}

	public void setMes(MesesType mes) {
		this.mes = mes;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aepl_id", nullable = false)
	public Plantilla getPlantilla() {
		return plantilla;
	}

	public void setPlantilla(Plantilla plantilla) {
		this.plantilla = plantilla;
	}
}
