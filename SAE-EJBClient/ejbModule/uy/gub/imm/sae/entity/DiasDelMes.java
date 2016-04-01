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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table (name = "ae_dias_del_mes")
public class DiasDelMes implements Serializable {
	
	private static final long serialVersionUID = 2252877441642254192L;
	
	private Integer id;
	private Integer diaDelMes;
	
    private Plantilla plantilla;

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_diames")
	@SequenceGenerator (name ="seq_diames", initialValue = 1, sequenceName = "s_ae_dia_mes",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(nullable=false, name="dia_del_mes")
	public Integer getDiaDelMes() {
		return diaDelMes;
	}
	public void setDiaDelMes(Integer diaDelMes) {
		this.diaDelMes = diaDelMes;
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
