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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.exception.ApplicationException;

public class DatoASolicitarMBean extends BaseMBean {
	public static final String MSG_ID = "pantalla";

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	public SessionMBean sessionMBean;
	private DatoASSessionMBean datoASSessionMBean;

	private List<SelectItem> listaAgrupaciones = new ArrayList<SelectItem>();
	private List<SelectItem> listaTipos = new ArrayList<SelectItem>();
	private boolean visualizarLargoMax;
	private boolean visualizarValoresPosibles;
	
	private DatoASolicitar datoASolicitarNuevo;

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public DatoASSessionMBean getDatoASSessionMBean() {
		return datoASSessionMBean;
	}

	public void setDatoASSessionMBean(DatoASSessionMBean datoASSessionMBean) {
		this.datoASSessionMBean = datoASSessionMBean;
	}

	public void beforePhaseCrear(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("crear_dato"));
		}
	}

	public void beforePhaseModificarConsultar(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("consultar_datos"));
		}
	}

	public void beforePhaseModificar(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("modificar_dato"));
		}
	}

	@PostConstruct
	public void init() {
		if (sessionMBean.getAgendaMarcada() == null) {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if (sessionMBean.getRecursoMarcado() != null) {
			this.cargarListaTipos();
			this.cargarListaAgrupaciones();
		} else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		datoASolicitarNuevo = new DatoASolicitar();
		visualizarLargoMax = true;
		visualizarValoresPosibles = false;
		if (datoASSessionMBean.getDatoSeleccionado() != null) {
			Tipo tipo = datoASSessionMBean.getDatoSeleccionado().getTipo();
			if (tipo==Tipo.BOOLEAN || tipo==Tipo.DATE || tipo==Tipo.LIST) {
				visualizarLargoMax = false;
			}
			if(tipo==Tipo.LIST) {
				visualizarValoresPosibles = true;
			}
		}
	}

	public void seleccionarDato(int rowIndex) {
		DatoASolicitar d = datoASSessionMBean.getDatosASolicitar().get(rowIndex);
		if (d != null) {
			datoASSessionMBean.setDatoSeleccionado(d);
			datoASSessionMBean.setAgrupacionDatoId(d.getAgrupacionDato().getId());
			datoASSessionMBean.setMostrarModifValor(false);
			datoASSessionMBean.setMostrarAgregarValor(false);
		} else {
			datoASSessionMBean.setDatoSeleccionado(null);
			datoASSessionMBean.setAgrupacionDatoId(null);
		}
	}

	public void modificarDato(ActionEvent event) {
		
		limpiarMensajesError();

		if (datoASSessionMBean.getDatoSeleccionado() != null) {
			for (AgrupacionDato a : datoASSessionMBean.getAgrupaciones()) {
				if (a.getId().equals(datoASSessionMBean.getAgrupacionDatoId())) {
					datoASSessionMBean.getDatoSeleccionado().setAgrupacionDato(a);
				}
			}

			// Se intenta modificar el dato en la base.
			try {
				boolean hayError = false;
				DatoASolicitar datoModificar = datoASSessionMBean.getDatoSeleccionado();

				if (datoModificar.getNombre() == null || datoModificar.getNombre().trim().isEmpty()) {
					addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_dato_es_obligatorio"), "form:VName");
					hayError = true;
				}else {
					boolean nombreValido = validarNombre(datoModificar.getNombre(),"form:VName");
					if (!nombreValido) {
						hayError = true;
					}
				}

				if (recursosEJB.existeDatoASolicPorNombre(datoModificar.getNombre(), sessionMBean.getRecursoMarcado().getId(),	datoModificar.getId())) {
					addErrorMessage(sessionMBean.getTextos().get("ya_existe_un_dato_con_el_nombre_especificado"),	"form:VName");
					hayError = true;
				}

				if (datoModificar.getEtiqueta() == null || datoModificar.getEtiqueta().trim().isEmpty()) {
					addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_del_dato_es_obligatoria"), "form:etiqueta");
					hayError = true;
				}

				if (datoModificar.getTipo() == null) {
					addErrorMessage(sessionMBean.getTextos().get("el_tipo_del_dato_es_obligatorio"), "form:tipo");
					hayError = true;
				}

				if (datoModificar.getFila() == null) {
					addErrorMessage(sessionMBean.getTextos().get("la_fila_del_dato_es_obligatoria"), "form:vFilaDS");
					hayError = true;
				} else if (datoModificar.getFila().intValue() < 1) {
					addErrorMessage(sessionMBean.getTextos().get("la_fila_del_dato_debe_ser_mayor_a_cero"), "form:vFilaDS");
					hayError = true;
				}

				if (this.visualizarLargoMax == true && datoModificar.getLargo() == null) {
					addErrorMessage(sessionMBean.getTextos().get("el_largo_del_dato_es_obligatorio"),	"form:vLargoDS");
					hayError = true;
				} else if (this.visualizarLargoMax == true && datoModificar.getLargo().intValue() < 1) {
					addErrorMessage(sessionMBean.getTextos().get("el_largo_del_dato_debe_ser_mayor_a_cero"),"form:vLargoDS");
					hayError = true;
				}
				
				if (datoModificar.getIncluirEnReporte() && datoModificar.getAnchoDespliegue() == null) {
					addErrorMessage(sessionMBean.getTextos().get("el_ancho_de_despliegue_es_obligatorio"), "form:vAnchoDS");
					hayError = true;
				} else if (datoModificar.getIncluirEnReporte() && datoModificar.getAnchoDespliegue().intValue() < 1) {
					addErrorMessage(sessionMBean.getTextos().get("el_ancho_de_despliegue_debe_ser_mayor_a_cero"), "form:vAnchoDS");
					hayError = true;
				}
				
				if (datoModificar.getIncluirEnLlamador() && datoModificar.getLargoEnLlamador() == null) {
					addErrorMessage(sessionMBean.getTextos().get("el_largo_del_dato_es_obligatorio"),"form:largoEnLlamador");
					hayError = true;
				} else if (datoModificar.getIncluirEnLlamador() && datoModificar.getOrdenEnLlamador().intValue() < 1) {
					addErrorMessage(sessionMBean.getTextos().get("el_orden_en_el_llamador_debe_ser_mayor_a_cero"), "form:ordenEnLlamador");
					hayError = true;
				}

				if (datoModificar.getIncluirEnLlamador() && datoModificar.getOrdenEnLlamador() == null) {
					addErrorMessage(sessionMBean.getTextos().get("el_orden_en_el_llamador_es_obligatorio"), "form:ordenEnLlamador");
					hayError = true;
				} else if (datoModificar.getIncluirEnLlamador() && datoModificar.getLargoEnLlamador().intValue() < 1) {
					addErrorMessage(sessionMBean.getTextos().get("el_largo_del_llamador_debe_ser_mayor_a_cero"), "form:largoEnLlamador");
					hayError = true;
				}

				if (hayError) {
					return;
				}
				
				if(this.visualizarLargoMax == false) {
					datoModificar.setLargo(0);
				}
				recursosEJB.modificarDatoASolicitar(datoASSessionMBean.getDatoSeleccionado());
				datoASSessionMBean.clearDatosASolicitar();

				// Hay que traer los valores posibles en caso que halla cambiado
				// el tipo a LIST
				List<ValorPosible> valoresP = recursosEJB.consultarValoresPosibles(datoASSessionMBean.getDatoSeleccionado());
				datoASSessionMBean.getDatoSeleccionado().setValoresPosibles(valoresP);

				addInfoMessage(sessionMBean.getTextos().get("campo_modificado"), MSG_ID);
				
				datoASSessionMBean.cargarDatosSolicitar();
				
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		} else {
			addErrorMessage(sessionMBean.getTextos().get( "debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
	}

	public String cancelarModificarDato() {
		datoASSessionMBean.cargarDatosSolicitar();
		return "volver";
	}
	
	public List<SelectItem> getListaTipos() {
		return listaTipos;
	}

	public void setListaTipos(List<SelectItem> listaTipos) {
		this.listaTipos = listaTipos;
	}

	public List<SelectItem> getListaAgrupaciones() {
		if (this.listaAgrupaciones.isEmpty()) {
			addErrorMessage(
					sessionMBean.getTextos().get(
							"debe_crear_al_menos_una_agrupacion"), MSG_ID);
		}
		return listaAgrupaciones;
	}

	public void setListaAgrupaciones(List<SelectItem> listaAgrupaciones) {
		this.listaAgrupaciones = listaAgrupaciones;
	}

	private void cargarListaTipos() {

		this.listaTipos = new ArrayList<SelectItem>();

		for (Tipo t : Tipo.values()) {
			SelectItem s = new SelectItem();
			s.setValue(t);
			s.setLabel(sessionMBean.getTextos().get(t.getDescripcion()));
			this.listaTipos.add(s);
		}
	}

	private void cargarListaAgrupaciones() {

		// Si hay recurso selecciondada, se cargan las agrupaciones.
		// En caso contrario se vacía la lista de agrupaciones
		if (sessionMBean.getRecursoMarcado() != null) {
			try {
				List<AgrupacionDato> entidades;
				entidades = recursosEJB.consultarAgrupacionesDatos(sessionMBean
						.getRecursoMarcado());

				listaAgrupaciones = new ArrayList<SelectItem>();
				for (AgrupacionDato a : entidades) {
					SelectItem s = new SelectItem();
					s.setValue(a.getId());
					s.setLabel(a.getNombre());
					listaAgrupaciones.add(s);
				}
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		}
	}

	/*
	 * ELIMINACION
	 */

	public void seleccionarDatoParaEliminar(int rowIndex) {
		DatoASolicitar d = datoASSessionMBean.getDatosASolicitar().get(rowIndex);
		datoASSessionMBean.setDatoSeleccionado(d);
	}

	public void eliminarDato() {

		DatoASolicitar d = datoASSessionMBean.getDatoSeleccionado();
		if (d != null && d.getBorrarFlag()) {

			try {
				recursosEJB.eliminarDatoASolicitar(d);
				addInfoMessage(sessionMBean.getTextos().get("dato_eliminado"), MSG_ID);
				datoASSessionMBean.setDatoSeleccionado(null);
				datoASSessionMBean.clearDatosASolicitar();

			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			} finally {
				datoASSessionMBean.setDatoSeleccionado(null);
			}
		} else {
			if (!d.getBorrarFlag()) {
				addErrorMessage(
						sessionMBean.getTextos().get(
								"no_se_permite_eliminar_este_dato"), MSG_ID);
			} else {
				addErrorMessage(
						sessionMBean.getTextos().get(
								"debe_haber_un_recurso_seleccionado"), MSG_ID);
			}

		}
	}

	/*
	 * CREACION
	 */

	public void crearDato(ActionEvent e) {

		limpiarMensajesError();
		
		try {
			boolean hayError = false;
			DatoASolicitar datoNuevo = getDatoASolicitarNuevo();
			
			if (datoNuevo.getNombre() == null || datoNuevo.getNombre().trim().isEmpty()) {
				addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_dato_es_obligatorio"), "formCrear:VName");
				hayError = true;
			}else {
				boolean nombreValido = validarNombre(datoNuevo.getNombre(),"formCrear:VName");
				if (!nombreValido) {
					hayError = true;
				}
			}

			if (recursosEJB.existeDatoASolicPorNombre(datoNuevo.getNombre(),
					sessionMBean.getRecursoMarcado().getId(), null)) {
				addErrorMessage(sessionMBean.getTextos().get("ya_existe_un_dato_con_el_nombre_especificado"), "formCrear:VName");
				hayError = true;
			}

			if (datoNuevo.getEtiqueta() == null || datoNuevo.getEtiqueta().trim().isEmpty()) {
				addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_del_dato_es_obligatoria"), "formCrear:etiqueta");
				hayError = true;
			}

			if (datoNuevo.getTipo() == null) {
				addErrorMessage(sessionMBean.getTextos().get("el_tipo_del_dato_es_obligatorio"), "formCrear:tipoDato");
				hayError = true;
			}

			if (datoNuevo.getFila() == null) {addErrorMessage(sessionMBean.getTextos().get("la_fila_del_dato_es_obligatoria"), "formCrear:VFila");
				hayError = true;
			} else if (datoNuevo.getFila().intValue() < 1) {
				addErrorMessage(sessionMBean.getTextos().get("la_fila_del_dato_debe_ser_mayor_a_cero"), "formCrear:VFila");
				hayError = true;
			}

			if (datoNuevo.getLargo() == null && this.visualizarLargoMax == true) {
				addErrorMessage(sessionMBean.getTextos().get("el_largo_del_dato_es_obligatorio"), "formCrear:VLargo");
				hayError = true;
			} else if (this.visualizarLargoMax == true && datoNuevo.getLargo().intValue() < 1) {
				addErrorMessage(sessionMBean.getTextos().get("el_largo_del_dato_debe_ser_mayor_a_cero"), "formCrear:VLargo");
				hayError = true;
			}

			if (datoNuevo.getIncluirEnReporte()
					&& datoNuevo.getAnchoDespliegue() == null) {
				addErrorMessage(sessionMBean.getTextos().get("el_ancho_de_despliegue_es_obligatorio"), "formCrear:VAncho");
				hayError = true;
			} else if (datoNuevo.getIncluirEnReporte() && datoNuevo.getAnchoDespliegue().intValue() < 1) {
				addErrorMessage(sessionMBean.getTextos().get("el_ancho_de_despliegue_debe_ser_mayor_a_cero"), "formCrear:VAncho");
				hayError = true;
			}
			
			if (datoNuevo.getIncluirEnLlamador() && datoNuevo.getLargoEnLlamador() == null) {
				addErrorMessage(sessionMBean.getTextos().get("el_largo_del_dato_es_obligatorio"),"formCrear:largoEnLlamador");
				hayError = true;
			} else if (datoNuevo.getIncluirEnLlamador() && datoNuevo.getOrdenEnLlamador().intValue() < 1) {
				addErrorMessage(sessionMBean.getTextos().get("el_orden_en_el_llamador_debe_ser_mayor_a_cero"), "formCrear:ordenEnLlamador");
				hayError = true;
			}

			if (datoNuevo.getIncluirEnLlamador() && datoNuevo.getOrdenEnLlamador() == null) {
				addErrorMessage(sessionMBean.getTextos().get("el_orden_en_el_llamador_es_obligatorio"), "formCrear:ordenEnLlamador");
				hayError = true;
			} else if (datoNuevo.getIncluirEnLlamador() && datoNuevo.getLargoEnLlamador().intValue() < 1) {
				addErrorMessage(sessionMBean.getTextos().get("el_largo_del_llamador_debe_ser_mayor_a_cero"), "formCrear:largoEnLlamador");
				hayError = true;
			}

			if (hayError) {
				return;
			}
			
			if(this.visualizarLargoMax == false) {
				datoNuevo.setLargo(0);
			}

			List<AgrupacionDato> agrupaciones = recursosEJB.consultarAgrupacionesDatos(sessionMBean.getRecursoMarcado());
			for (AgrupacionDato agrupacion : agrupaciones) {
				if (agrupacion.getId().equals(datoASSessionMBean.getAgrupacionDatoId())) {
					this.datoASolicitarNuevo.setAgrupacionDato(agrupacion);
				}
			}
			if (getDatoASolicitarNuevo().getLargoEnLlamador() == null) {
				getDatoASolicitarNuevo().setLargoEnLlamador(0);
			}
			if (getDatoASolicitarNuevo().getOrdenEnLlamador() == null) {
				getDatoASolicitarNuevo().setOrdenEnLlamador(0);
			}

			recursosEJB.agregarDatoASolicitar(sessionMBean.getRecursoMarcado(),
					getDatoASolicitarNuevo().getAgrupacionDato(),
					getDatoASolicitarNuevo());

			datoASSessionMBean.clearDatosASolicitar();

			// Se blanquea la info en la página
			datoASolicitarNuevo = new DatoASolicitar();

			addInfoMessage(sessionMBean.getTextos().get("dato_creado"), MSG_ID);
		} catch (Exception ex) {
			addErrorMessage(ex, MSG_ID);
		}
	}

	public DatoASolicitar getDatoASolicitarNuevo() {
		return datoASolicitarNuevo;
	}

	public void setDatoASolicitarNuevo(DatoASolicitar datoASolicitarNuevo) {
		this.datoASolicitarNuevo = datoASolicitarNuevo;
	}

	public void limpiarAnchoDespliegueReporteNuevo() {
		if (this.datoASolicitarNuevo.getIncluirEnReporte() == false) {
			this.datoASolicitarNuevo.setAnchoDespliegue(0);
		}

	}
	
	public void limpiarAnchoDespliegueReporteSeleccionado() {
		if (this.datoASSessionMBean.getDatoSeleccionado().getIncluirEnReporte() == false) {
			this.datoASSessionMBean.getDatoSeleccionado().setAnchoDespliegue(0);
		}

	}

	public void limpiarLargoOrdenLlamadorNuevo() {
		if (this.datoASolicitarNuevo.getIncluirEnLlamador() == false) {
			this.datoASolicitarNuevo.setLargoEnLlamador(0);
			this.datoASolicitarNuevo.setOrdenEnLlamador(0);
		}

	}
	
	public void limpiarLargoOrdenLlamadorSeleccionado() {
		if (this.datoASSessionMBean.getDatoSeleccionado().getIncluirEnLlamador() == false) {
			this.datoASSessionMBean.getDatoSeleccionado().setLargoEnLlamador(0);
			this.datoASSessionMBean.getDatoSeleccionado().setOrdenEnLlamador(0);
		}

	}

	/*
	 * CONSULTAR
	 */

	public String consultarDato(int rowIndex) throws ApplicationException {
		DatoASolicitar d = datoASSessionMBean.getDatosASolicitar().get(rowIndex);
		if (d != null) {
			datoASSessionMBean.setDatoSeleccionado(d);
			if (datoASSessionMBean.getDatoSeleccionado().getTipo() == Tipo.LIST) {
				datoASSessionMBean.setMostrarConsultarValor(true);
			} else {
				datoASSessionMBean.setMostrarConsultarValor(false);
			}
		} else {
			datoASSessionMBean.setDatoSeleccionado(null);
			datoASSessionMBean.setMostrarConsultarValor(false);
		}
		return "consultarDato";
	}

	public boolean validarNombre(String nombre,String componenteId) {

		nombre = nombre.toUpperCase();
		String caracValidos = "ABCDEFGHIJKLMNOPQRSTVUWXYZ0123456789_";
		boolean nombreValido = true;
		String NUMEROS="1234567890";
		
		for (int i = 0; (i < nombre.length() && nombreValido); i++) {
			char caracter = nombre.charAt(i);

			// Se chequea que el primer caracter no sea un numero
			if (i == 0 && NUMEROS.indexOf(caracter) != -1) {
				nombreValido = false;
				addErrorMessage(sessionMBean.getTextos().get("el_campo_nombre_no_puede_comenzar_con_un_numero"),componenteId);
				
			}

			// Se chequea si los caracterss son validos
			if (caracValidos.indexOf(caracter) == -1) {
				nombreValido = false;
				addErrorMessage(sessionMBean.getTextos().get("el_campo_nombre_solo_puede_contener_letras_sin_tildes,_numeros_y__"), componenteId);
				
			}
		}

		return nombreValido;

	}
	
	public void selectOneTipoDatoNuevo(AjaxBehaviorEvent e)
	{
		Tipo tipo = datoASolicitarNuevo.getTipo();
		if(tipo == null) {
			visualizarLargoMax = false;
			visualizarValoresPosibles = false; 
		}else {
			visualizarLargoMax = !(tipo.equals(Tipo.BOOLEAN) || tipo.equals(Tipo.DATE) || tipo.equals(Tipo.LIST));
			visualizarValoresPosibles = tipo.equals(Tipo.LIST);
		}
	}
	
	public void selectOneTipoDatoSeleccionado(AjaxBehaviorEvent e)
	{
		Tipo tipo = datoASSessionMBean.getDatoSeleccionado().getTipo();
		if(tipo == null) {
			visualizarLargoMax = false;
			visualizarValoresPosibles = false; 
		}else {
			visualizarLargoMax = !(tipo.equals(Tipo.BOOLEAN) || tipo.equals(Tipo.DATE) || tipo.equals(Tipo.LIST));
			visualizarValoresPosibles = tipo.equals(Tipo.LIST);
		}
	}

	public boolean isVisualizarLargoMax() {
		
		return visualizarLargoMax;
	}

	public boolean getVisualizarValoresPosibles() {
		return visualizarValoresPosibles;
	}
	

}
