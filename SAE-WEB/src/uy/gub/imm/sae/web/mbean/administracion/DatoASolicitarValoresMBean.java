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

import java.util.List;

import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.web.common.BaseMBean;

public class DatoASolicitarValoresMBean extends BaseMBean {
	public static final String MSG_ID = "pantalla";

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	private DatoASSessionMBean datoASSessionMBean;

	public SessionMBean sessionMBean;
	
	public DatoASSessionMBean getDatoASSessionMBean() {
		return datoASSessionMBean;
	}
	public void setDatoASSessionMBean(DatoASSessionMBean datoASSessionMBean) {
		this.datoASSessionMBean = datoASSessionMBean;
	}

	public void seleccionarValor(int rowIndex) {
		ValorPosible v = datoASSessionMBean.getValoresDelDato().get(rowIndex);
		if (v != null) {
			datoASSessionMBean.setValorDelDatoSeleccionado(v);
			datoASSessionMBean.setMostrarModifValor(true);
			datoASSessionMBean.setMostrarAgregarValor(false);
		}
		else {
			datoASSessionMBean.setValorDelDatoSeleccionado(null);
			datoASSessionMBean.setMostrarModifValor(false);
			datoASSessionMBean.setMostrarAgregarValor(false);
		}
	}

	
	public void modificarValor(ActionEvent event) {
		
		ValorPosible vp = datoASSessionMBean.getValorDelDatoSeleccionado();
		if (vp != null) {
			boolean hayError = false;
			if(vp.getEtiqueta() == null || vp.getEtiqueta().trim().isEmpty()){
				addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_del_dato_es_obligatoria"), "formModif2:modEtiqueta");
				hayError = true;
			}
			if (vp.getValor() == null || vp.getEtiqueta().trim().isEmpty()){
				addErrorMessage(sessionMBean.getTextos().get("el_valor_del_dato_es_obligatorio"), "formModif2:modValor");
				hayError = true;
			}
			if (vp.getOrden() == null){
				addErrorMessage(sessionMBean.getTextos().get("el_orden_del_dato_es_obligatorio"), "formModif2:VOrden");
				hayError = true;
			} else if (vp.getOrden().intValue() < 1){
				addErrorMessage(sessionMBean.getTextos().get("el_orden_del_dato_debe_ser_mayor_a_cero"), "formModif2:VOrden");
				hayError = true;
			}
			if (vp.getFechaDesde() == null){
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), "formModif2:fechaDesde");
				hayError = true;
			} else if ((vp.getFechaHasta() != null) && (vp.getFechaDesde().compareTo(vp.getFechaHasta()) > 0 )){
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), "formModif2:fechaDesde", "formModif2:fechaHasta");
				hayError = true;
			}
			
			if(!validarCampoLista(vp, "formModif2:modValor")) {
			  //El mensaje lo pone la validación
        hayError = true;
			}
			
			if(hayError) {
				return;
			}
			
			try {
				DatoASolicitar datoSel = datoASSessionMBean.getDatoSeleccionado();
				vp.setDato(datoSel);
				if (recursosEJB.existeValorPosiblePeriodo(vp)) {
					addErrorMessage(sessionMBean.getTextos().get("ya_existe_otro_valor_con_la_misma_etiqueta_y_valor"), "formModif2:modEtiqueta", "formModif2:modValor", "formModif2:fechaDesde", "formModif2:fechaHasta");
				} else {
					try {
						recursosEJB.modificarValorPosible(vp);
						addInfoMessage(sessionMBean.getTextos().get("valor_modificado"), MSG_ID); 				
					} catch (Exception e) {
						addErrorMessage(e, MSG_ID);
					}
					datoASSessionMBean.getDatoSeleccionado().setValoresPosibles(null);
					List<ValorPosible> valoresFromDB = recursosEJB.consultarValoresPosibles(datoASSessionMBean.getDatoSeleccionado());
					datoASSessionMBean.getDatoSeleccionado().setValoresPosibles(valoresFromDB);					
				}
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		}
		else {
			addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_un_valor"), MSG_ID);
		}
	}
	
	
	public void cancelarModificarValor(ActionEvent event) {
		datoASSessionMBean.setValorDelDatoSeleccionado(null);
		datoASSessionMBean.setMostrarModifValor(false);
	}

	public void seleccionarValorParaEliminar(int rowIndex) {
		ValorPosible vp = datoASSessionMBean.getValoresDelDato().get(rowIndex);
		datoASSessionMBean.setValorDelDatoSeleccionado(vp);
		datoASSessionMBean.setMostrarAgregarValor(false);
		datoASSessionMBean.setMostrarModifValor(false);
	}

	
	public void eliminarValor(ActionEvent event) {
		
		ValorPosible v = datoASSessionMBean.getValorDelDatoSeleccionado();
		
		if (v != null && v.getBorrarFlag()) {
			try {
				recursosEJB.eliminarValorPosible(v);

				addInfoMessage(sessionMBean.getTextos().get("valor_eliminado"), MSG_ID);
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		
			try {
				datoASSessionMBean.getDatoSeleccionado().setValoresPosibles(null);
				List<ValorPosible> valoresFromDB = recursosEJB.consultarValoresPosibles(datoASSessionMBean.getDatoSeleccionado());
				datoASSessionMBean.getDatoSeleccionado().setValoresPosibles(valoresFromDB);					
				
			} catch (Exception e1) {
				addErrorMessage(e1, MSG_ID);
			}

		}
		else {
			if(!v.getBorrarFlag())
			{
				addErrorMessage(sessionMBean.getTextos().get("no_se_permite_eliminar_este_valor"), MSG_ID);
			}else
			{
				addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_un_valor"), MSG_ID);
			}
			
		}
	}



	
	
	/*
	 * CREACION DE VALOR POSIBLE
	 * 
	 */
	
	public void mostrarCrearValor(ActionEvent e) {
		datoASSessionMBean.setValorDelDatoSeleccionado(new ValorPosible());
		datoASSessionMBean.setMostrarAgregarValor(true);
		datoASSessionMBean.setMostrarModifValor(false);
	}
	
	public void crearValor(ActionEvent e) {
		limpiarMensajesError();
		ValorPosible vp = datoASSessionMBean.getValorDelDatoSeleccionado();
		boolean hayError = false;
		if(vp.getEtiqueta() == null || vp.getEtiqueta().trim().isEmpty()){
			addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_del_dato_es_obligatoria"), "formModif2:crearEti");
			hayError = true;
		}
		if (vp.getValor() == null || vp.getValor().trim().isEmpty()){
			addErrorMessage(sessionMBean.getTextos().get("el_valor_del_dato_es_obligatorio"), "formModif2:crearValor");
			hayError = true;
		}
		if (vp.getOrden() == null){
			addErrorMessage(sessionMBean.getTextos().get("el_orden_del_dato_es_obligatorio"), "formModif2:VOrdValor");
			hayError = true;
		} else if (vp.getOrden().intValue() < 1){
			addErrorMessage(sessionMBean.getTextos().get("el_orden_del_dato_debe_ser_mayor_a_cero"), "formModif2:VOrdValor");
			hayError = true;
		}
		if (vp.getFechaDesde() == null){
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), "formModif2:CfechaDesde");
			hayError = true;
		} else if ((vp.getFechaHasta() != null) && (vp.getFechaDesde().compareTo(vp.getFechaHasta()) > 0 )){
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), "formModif2:CfechaDesde", "formModif2:CfechaHasta");
			hayError = true;
		}
    if(!validarCampoLista(vp, "formModif2:crearValor")) {
      //El mensaje lo pone la validación
      hayError = true;
    }
		if(hayError) {
			return;
		}
		
		try {
			DatoASolicitar datoSel = datoASSessionMBean.getDatoSeleccionado();
			vp.setDato(datoSel);
			if (recursosEJB.existeValorPosiblePeriodo(vp)) {
				addErrorMessage(sessionMBean.getTextos().get("ya_existe_otro_valor_con_la_misma_etiqueta_y_valor"), "formModif2:crearEti", "formModif2:crearValor", "formModif2:CfechaDesde", "formModif2:CfechaHasta");
			} else {
				recursosEJB.agregarValorPosible(datoASSessionMBean.getDatoSeleccionado(), vp);
				addInfoMessage(sessionMBean.getTextos().get("valor_creado"), MSG_ID);
				//Se blanquea la info en la página
				datoASSessionMBean.setValorDelDatoSeleccionado(new ValorPosible());
				try {
					datoASSessionMBean.getDatoSeleccionado().setValoresPosibles(null);
					List<ValorPosible> valoresFromDB = recursosEJB.consultarValoresPosibles(datoASSessionMBean.getDatoSeleccionado());
					datoASSessionMBean.getDatoSeleccionado().setValoresPosibles(valoresFromDB);					
				} catch (Exception e1) {
					addErrorMessage(e1, MSG_ID);
				}
			}
		} catch (Exception ex) {
			addErrorMessage(ex, MSG_ID);
			ex.printStackTrace();
		}
		
	}

	
	public void cancelarCrearValor(ActionEvent event) {
		limpiarMensajesError();
		datoASSessionMBean.setValorDelDatoSeleccionado(null);
		datoASSessionMBean.setMostrarAgregarValor(false);
	}
	
	public boolean validarCampoLista(ValorPosible vp, String compId)
	{
		boolean ok = true;
		if (!vp.getValor().matches("([a-z]|[A-Z]|[0-9]|\\s)+")) {
			addErrorMessage(sessionMBean.getTextos().get("solo_se_permiten_letras_y_numeros"), compId);
			ok = false;
		}else if (vp.getValor().length()>5) {
			addErrorMessage(sessionMBean.getTextos().get("solo_se_permiten_hasta_5_caracteres"), compId);
			ok = false;
		}
		return ok;
	}
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

}
