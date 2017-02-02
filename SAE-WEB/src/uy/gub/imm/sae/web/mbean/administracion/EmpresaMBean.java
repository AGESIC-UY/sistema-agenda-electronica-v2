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

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Organismo;
import uy.gub.imm.sae.entity.global.UnidadEjecutora;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

public class EmpresaMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas empresasEJB;
	
	private SessionMBean sessionMBean;
	private EmpresaSessionMBean empresaSessionMBean;
	private RowList<Empresa> empresasSeleccion;
	private Row<Empresa> rowSelect;
	private DataTable empresasDataTable;

	@PostConstruct
	public void postConstruct() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if(request.getParameter("n")!=null) {
			setEmpresaEditar(new Empresa());
		}
		//Cargar los organismos
		try {
			List<Organismo> orgs = empresasEJB.obtenerOrganismos(false);
			empresaSessionMBean.setOrganismos(orgs);
		} catch (ApplicationException e) {
			empresaSessionMBean.setOrganismos(null);
			e.printStackTrace();
		}
		//Cargar las unidades ejecutoras
		try {
			List<UnidadEjecutora> ues = empresasEJB.obtenerUnidadesEjecutoras(false);
			empresaSessionMBean.setUnidadesEjecutoras(ues);
		} catch (ApplicationException e) {
			empresaSessionMBean.setUnidadesEjecutoras(null);
			e.printStackTrace();
		}
	}
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public Empresa getEmpresaEditar() {
		if(empresaSessionMBean.getEmpresaEditar() == null) {
			empresaSessionMBean.setEmpresaEditar(new Empresa());
		}
		return empresaSessionMBean.getEmpresaEditar();
	}
	
	public void setEmpresaEditar(Empresa empresa) {
		empresaSessionMBean.setEmpresaEditar(empresa);
	}
	
	public StreamedContent getEmpresaLogo() {
		return empresaSessionMBean.getEmpresaLogo();
	} 
	
	
	
	public Row<Empresa> getRowSelect() {
		return rowSelect;
	}
	
	public void setRowSelect(Row<Empresa> rowSelect) {
		this.rowSelect = rowSelect;
	}

	public DataTable getEmpresasDataTable() {
		return empresasDataTable;
	}

	public void setEmpresasDataTable(DataTable empresasDataTable) {
		this.empresasDataTable = empresasDataTable;
	}

	//Lista de empresas para seleccionar en la eliminacion/modificacion.
	public RowList<Empresa> getEmpresasSeleccion() {
		try {
			List<Empresa> entidades = empresasEJB.consultarTodasEmpresas();
			empresasSeleccion = new RowList<Empresa>(entidades);
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
		return empresasSeleccion;
	}
	
	public String guardar() {
		
		limpiarMensajesError();
		
		if (getEmpresaEditar() != null) {
			try {
				Empresa empresa = getEmpresaEditar();
				
				boolean hayErrores = false;
				
				if(empresa.getOrganismoCodigo()==null || empresa.getOrganismoCodigo().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_codigo_del_organismo_es_obligatorio"), "form:codigoOrganismo");
				}
				if(empresa.getUnidadEjecutoraCodigo()==null || empresa.getUnidadEjecutoraCodigo().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_codigo_de_la_unidad_ejecutora_es_obligatorio"), "form:codigoUnidadEjecutora");
				}
				
				if(empresa.getNombre()==null || empresa.getNombre().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_empresa_es_obligatorio"), "form:nombreEmpresa");
				}
				if(empresa.getOid()==null || empresa.getOid().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_oid_de_la_empresa_es_obligatorio"), "form:oidEmpresa");
				}
				if(empresa.getDatasource()==null || empresa.getDatasource().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_origen_de_datos_de_la_empresa_es_obligatorio"), "form:datasourceEmpresa");
				}else {
					//Ver si existe el datasource
					boolean existeDatasource = empresasEJB.existeEsquema(empresa.getDatasource());
					if(!existeDatasource) {
						hayErrores = true;
						addErrorMessage(sessionMBean.getTextos().get("no_existe_el_origen_de_datos"), "form:datasourceEmpresa");
					}else {
						List<Empresa> otrasEmpresas = empresasEJB.obtenerEmpresasPorDatasource(empresa.getDatasource());
						for(Empresa otraEmpresa : otrasEmpresas) {
							//Si esta eliminada se ignora
							if(otraEmpresa.getFechaBaja()==null) {
								//No esta eliminada
								if(empresa.getId()==null || !empresa.getId().equals(otraEmpresa.getId())) {
									hayErrores = true;
									addErrorMessage(sessionMBean.getTextos().get("ya_existe_una_empresa_con_el_mismo_valor_para_origen_de_datos"), "form:datasourceEmpresa");
								}
							}
						}
					}					
				}
				if(empresa.getCcFinalidad()==null || empresa.getCcFinalidad().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("la_finalidad_para_la_clausula_de_consentimiento_informado_es_obligatoria"), "form:ccFinalidad");
				}
				if(empresa.getCcResponsable()==null || empresa.getCcResponsable().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_responsable_para_la_clausula_de_consentimiento_informado_es_obligatorio"), "form:ccResponsable");
				}
				if(empresa.getCcDireccion()==null || empresa.getCcDireccion().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("la_direccion_para_la_clausula_de_consentimiento_informado_es_obligatoria"), "form:ccDireccion");
				}
				if(empresa.getLogoTexto()==null ||empresa.getLogoTexto().trim().isEmpty())
				{
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_texto_alternativo_del_logo_no_puede_estar_vacio"), "form:textoLogoEmpresa");
				}
				
				if(hayErrores) {
					return null;
				}

				boolean nueva = (empresa.getId() == null);
				//Verificar que no existe otra empresa con el mismo nombre
				if(nueva) {
					try {
						Empresa empresa0 = empresasEJB.obtenerEmpresaPorNombre(empresa.getNombre());
						if(empresa0 != null) {
							addErrorMessage(sessionMBean.getTextos().get("ya_existe_una_empresa_con_el_nombre_especificado"), "nombreEmpresa");
							return null;
						}
					}catch(ApplicationException aEx) {
						//Nada para hacer
					}
				}
				empresa = empresasEJB.guardarEmpresa(empresa);
				setEmpresaEditar(empresa);
				List<Empresa> entidades = empresasEJB.consultarEmpresas();
				empresasSeleccion = new RowList<Empresa>(entidades);
				if(nueva) {
					addInfoMessage(sessionMBean.getTextos().get("empresa_creada"), MSG_ID);
				}else {
					addInfoMessage(sessionMBean.getTextos().get("empresa_modificada"), MSG_ID);
				}
				//se carga los datos del usuario para que cargue la nueva empresa creada
				sessionMBean.cargarEmpresasUsuario();
				//si es la primer empresa y es nueva, loguear al usuario en esa empresa
				//nota: habría que hacer lo mismo si es una edición, es la empresa actualmente seleccionada y cambió el datasource
				if(nueva) {
					List<SelectItem> emps = sessionMBean.getEmpresasUsuario();
					if(emps!=null && emps.size()==1) {
						SelectItem emp = emps.get(0);
						sessionMBean.cambioEmpresa((Integer)emp.getValue());
					}
				}
			} catch(Exception ex) {
        addErrorMessage(ex, MSG_ID);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void selecEmpresaEliminar(ActionEvent e){
		Empresa empresaEliminar = ((Row<Empresa>)empresasDataTable.getRowData()).getData();
		empresaSessionMBean.setEmpresaEliminar(empresaEliminar);
	}
	
	@SuppressWarnings("unchecked")
	public String modificar() {
		this.rowSelect = (Row<Empresa>)empresasDataTable.getRowData();
		if (this.rowSelect != null){
			empresasSeleccion.setSelectedRow(this.rowSelect);
			setEmpresaEditar(empresasSeleccion.getSelectedRow().getData());
			return "modificar";
		}
		return null;
	}
	
	public String eliminar() {
		Empresa empresaEliminar = empresaSessionMBean.getEmpresaEliminar();
		if (empresaEliminar != null){
			Empresa empresaActual = sessionMBean.getEmpresaActual();
			boolean seleccionEmpliminar = false;
			try {
				
				if (empresaEliminar.getId()!=empresaActual.getId() && empresasEJB.empresaEsquemaValido(empresaEliminar.getId())) {
					seleccionEmpliminar = true;
					sessionMBean.seleccionarEmpresa(empresaEliminar.getId());
				}
				empresasEJB.eliminarEmpresa(empresaEliminar, sessionMBean.getTimeZone());
				//Recargar la lista de empresas
				List<Empresa> entidades = empresasEJB.consultarEmpresas();
				empresasSeleccion = new RowList<Empresa>(entidades);
				empresaSessionMBean.setEmpresaEliminar(null);
				addInfoMessage(sessionMBean.getTextos().get("empresa_eliminada"), MSG_ID);
			} catch (ApplicationException aEx) {
				addErrorMessage(aEx, MSG_ID);
			} catch (UserException e) {
				addErrorMessage(e, MSG_ID);
			}finally
			{
				if (seleccionEmpliminar)
				{
					sessionMBean.seleccionarEmpresa(empresaActual.getId());
					sessionMBean.cargarEmpresasUsuario();
				}else if(empresaEliminar.getId()==empresaActual.getId())
				{
					sessionMBean.setEmpresaActual(null);
					sessionMBean.cargarDatosUsuario();
				}
					
			}
		}
		return null;
	}
	
	public EmpresaSessionMBean getEmpresaSessionMBean() {
		return empresaSessionMBean;
	}
	
	public void setEmpresaSessionMBean(EmpresaSessionMBean empresaSessionMBean) {
		this.empresaSessionMBean = empresaSessionMBean;
	}

	public void beforePhaseCrearModificar(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			if(request.getParameter("n")!=null) {
				sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("crear_empresa"));
			}else {
				sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("modificar_empresa"));
			}
		}
	}

	public void beforePhaseConsultar(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("consultar_empresas"));
		}
	}
	
	public void cambioOrganismo(ValueChangeEvent event) {
		Integer orgId = (Integer) event.getNewValue();
		empresaSessionMBean.actualizarOrganismoEmpresa(orgId);
	}
	
	public String recargarOrganismos(ActionEvent event) {
		//Cargar los organismos
		try {
			List<Organismo> orgs = empresasEJB.obtenerOrganismos(true);
			if(orgs == null) {
				addAdvertenciaMessage(sessionMBean.getTextos().get("no_se_pudo_actualizar_lista_de_organismos"));
				orgs = empresasEJB.obtenerOrganismos(false);
			}else {
				addInfoMessage(sessionMBean.getTextos().get("lista_de_organismos_actualizada"));
			}
			empresaSessionMBean.setOrganismos(orgs);
		} catch (ApplicationException e) {
			empresaSessionMBean.setOrganismos(null);
			e.printStackTrace();
		}
		return null;
	}
	
	public void cambioUnidadEjecutora(ValueChangeEvent event) {
		Integer ueId = (Integer) event.getNewValue();
		empresaSessionMBean.actualizarUnidadEjecutoraEmpresa(ueId);
	}
	
	public String recargarUnidadesEjecutoras(ActionEvent event) {
		//Cargar las unidades ejecutoras
		try {
			List<UnidadEjecutora> ues = empresasEJB.obtenerUnidadesEjecutoras(true);
			if(ues == null) {
				addAdvertenciaMessage(sessionMBean.getTextos().get("no_se_pudo_actualizar_lista_de_unidades_ejecutoras"));
				ues = empresasEJB.obtenerUnidadesEjecutoras(false);
			}else {
				addInfoMessage(sessionMBean.getTextos().get("lista_de_unidades_ejecutas_actualizada"));
			}
			empresaSessionMBean.setUnidadesEjecutoras(ues);
		} catch (ApplicationException e) {
			empresaSessionMBean.setUnidadesEjecutoras(null);
			e.printStackTrace();
		}
		return null;
	}
	
	public void cambioLogo(FileUploadEvent event) {
		UploadedFile archivo = event.getFile();
		try {
			byte[] bytes = IOUtils.toByteArray(archivo.getInputstream());
			empresaSessionMBean.getEmpresaEditar().setLogo(bytes);
			boolean actualizarLogo = false;
			if(sessionMBean.getEmpresaActualId()!=null && empresaSessionMBean.getEmpresaEditar()!=null && empresaSessionMBean.getEmpresaEditar().getId()!=null) {
				try {
					Integer empresaActualId = Integer.valueOf(sessionMBean.getEmpresaActualId());
					Integer empresaEditarId  = empresaSessionMBean.getEmpresaEditar().getId();
					if(empresaActualId.intValue() == empresaEditarId.intValue()) {
						actualizarLogo = true;
					}
				}catch(Exception ex) {
					//
				}
			}
			if(actualizarLogo) {
				//si la empresa que edito es la actual tengo que modificar el logo en el cabezal
				sessionMBean.setEmpresaActualLogoBytes(bytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void quitarLogo() {
    empresaSessionMBean.getEmpresaEditar().setLogo(null);
    if(sessionMBean.getEmpresaActualId()!=null && empresaSessionMBean.getEmpresaEditar()!=null && empresaSessionMBean.getEmpresaEditar().getId()!=null) {
      Integer empresaActualId = Integer.valueOf(sessionMBean.getEmpresaActualId());
      Integer empresaEditarId  = empresaSessionMBean.getEmpresaEditar().getId();
      if(empresaActualId.intValue() == empresaEditarId.intValue()) {
        //si la empresa que edito es la actual tengo que modificar el logo en el cabezal
        sessionMBean.setEmpresaActualLogoBytes(null);
      }
    }
	}
	
}