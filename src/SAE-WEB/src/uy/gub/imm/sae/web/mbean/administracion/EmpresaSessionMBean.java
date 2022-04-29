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

package uy.gub.imm.sae.web.mbean.administracion;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.faces.model.SelectItem;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Organismo;
import uy.gub.imm.sae.entity.global.UnidadEjecutora;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;

public class EmpresaSessionMBean extends SessionCleanerMBean implements RemovableFromSession {
	
	public static final String MSG_ID = "pantalla";
	
	private Empresa empresaEditar;
	private Empresa empresaEliminar;
	private boolean ultimaEmpresaEliminar; //Indica si la empresa seleccionada para eliminar es la última
	//Datos de los organismos 
	private Map<Integer, Organismo> mapOrganismos = new HashMap<Integer, Organismo>();
	private List<SelectItem> organismos = new ArrayList<SelectItem>(0);

	//Datos de las unidades ejecutoras 
	private Map<Integer, UnidadEjecutora> mapUnidadesEjecutoras = new HashMap<Integer, UnidadEjecutora>();
	private List<SelectItem> unidadesEjecutoras = new ArrayList<SelectItem>(0);

	public Empresa getEmpresaEditar() {
		return empresaEditar;
	}

	public void setEmpresaEditar(Empresa empresaEditar) {
		this.empresaEditar = empresaEditar;
		if(this.empresaEditar==null) {
		}
	}
	
	public void setOrganismos(List<Organismo> orgs) {
		mapOrganismos = new HashMap<Integer, Organismo>();
		organismos = new ArrayList<SelectItem>(0);
		organismos.add(new SelectItem(0, "Sin especificar"));
		if(orgs == null) {
			return;
		}
		organismos = new ArrayList<SelectItem>(orgs.size());
		organismos.add(new SelectItem(0, "Sin especificar"));
		for(Organismo org : orgs) {
			organismos.add(new SelectItem(org.getId(), org.getNombre()));
			mapOrganismos.put(org.getId(), org);
		}
	}
	
	public List<SelectItem> getOrganismos() {
		return organismos;
	}
	
	public void actualizarOrganismoEmpresa(Integer orgId) {
		if(orgId == null || !mapOrganismos.containsKey(orgId)) {
			this.empresaEditar.setOrganismoId(null);
			this.empresaEditar.setOrganismoCodigo("");
			this.empresaEditar.setOrganismoNombre("");
		}else {
			Organismo organismo = mapOrganismos.get(orgId);
			this.empresaEditar.setOrganismoId(orgId);
			this.empresaEditar.setOrganismoCodigo(organismo.getCodigo());
			this.empresaEditar.setOrganismoNombre(organismo.getNombre());
		}
	}
	
	public void setUnidadesEjecutoras(List<UnidadEjecutora> ues) {
		mapUnidadesEjecutoras = new HashMap<Integer, UnidadEjecutora>();
		unidadesEjecutoras = new ArrayList<SelectItem>(0);
		unidadesEjecutoras.add(new SelectItem(0, "Sin especificar"));
		if(ues == null) {
			return;
		}
		unidadesEjecutoras = new ArrayList<SelectItem>(ues.size());
		unidadesEjecutoras.add(new SelectItem(0, "Sin especificar"));
		for(UnidadEjecutora ue : ues) {
			unidadesEjecutoras.add(new SelectItem(ue.getId(), ue.getNombre()));
			mapUnidadesEjecutoras.put(ue.getId(), ue);
		}
	}
	
	public List<SelectItem> getUnidadesEjecutoras() {
		return unidadesEjecutoras;
	}
	
	public void actualizarUnidadEjecutoraEmpresa(Integer ueId) {
		if(ueId == null || !mapUnidadesEjecutoras.containsKey(ueId)) {
			this.empresaEditar.setUnidadEjecutoraId(null);
			this.empresaEditar.setUnidadEjecutoraCodigo("");
			this.empresaEditar.setUnidadEjecutoraNombre("");
		}else {
			UnidadEjecutora uEjecutora = mapUnidadesEjecutoras.get(ueId);
			this.empresaEditar.setUnidadEjecutoraId(ueId);
			this.empresaEditar.setUnidadEjecutoraCodigo(uEjecutora.getCodigo());
			this.empresaEditar.setUnidadEjecutoraNombre(uEjecutora.getNombre());
		}
	}

	public StreamedContent getEmpresaLogo() {
		if(this.empresaEditar.getLogo() != null) {
			ByteArrayInputStream input = new ByteArrayInputStream(this.empresaEditar.getLogo());
			return new DefaultStreamedContent(input);
		}
		return null;
	}

	public Empresa getEmpresaEliminar() {
		return empresaEliminar;
	}

	public void setEmpresaEliminar(Empresa empresaEliminar) {
		this.empresaEliminar = empresaEliminar;
	}

	public List<SelectItem> getTimezones() {
		String[] ids = TimeZone.getAvailableIDs();
		List<SelectItem> timezones = new ArrayList<SelectItem>();
		for (String id : ids) {
			TimeZone timezone = TimeZone.getTimeZone(id);
			long hours = TimeUnit.MILLISECONDS.toHours(timezone.getRawOffset());
			long minutes = TimeUnit.MILLISECONDS.toMinutes(timezone.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
			timezones.add(new SelectItem(id, id+" (GMT"+(hours > 0?"+":"")+hours+":"+minutes+")"));
		}
		return timezones;		
	}
	
	public List<SelectItem> getFormatosFecha() {
		List<SelectItem> formatos = new ArrayList<SelectItem>();
		formatos.add(new SelectItem("dd/MM/yyyy", "Día/Mes/Año"));
		formatos.add(new SelectItem("MM/dd/yyyy", "Mes/Día/Año"));
		formatos.add(new SelectItem("yyyy/MM/dd", "Año/Mes/Día"));
		formatos.add(new SelectItem("yyyy/dd/MM", "Año/Día/Mes"));
		return formatos;
	}
	
	public List<SelectItem> getFormatosHora() {
		List<SelectItem> formatos = new ArrayList<SelectItem>();
		formatos.add(new SelectItem("HH:mm", "24 horas"));
		formatos.add(new SelectItem("hh:mm a", "12 horas"));
		return formatos;
	}

  public boolean isUltimaEmpresaEliminar() {
    return ultimaEmpresaEliminar;
  }

  public void setUltimaEmpresaEliminar(boolean ultimaEmpresaEliminar) {
    this.ultimaEmpresaEliminar = ultimaEmpresaEliminar;
  }
	
	
}


