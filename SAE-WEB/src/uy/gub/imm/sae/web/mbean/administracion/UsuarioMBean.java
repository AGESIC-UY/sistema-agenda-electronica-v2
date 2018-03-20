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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.business.dto.UsuarioEmpresaRoles;
import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.common.RolesXRecurso;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.RolesUsuarioRecurso;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Usuario;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

public class UsuarioMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas usuariosEJB;
	
  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
  private AgendaGeneral generalEJB;
	
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
  private Recursos recursosEJB;
  
	private SessionMBean sessionMBean;
	private UsuarioSessionMBean usuarioSessionMBean;
	private RowList<Usuario> usuariosSeleccion;
	private Row<Usuario> rowSelect;
	private DataTable usuariosDataTable;
	
	@PostConstruct
	public void postConstruct() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if(request.getParameter("n")!=null) {
			setUsuarioEditar(new Usuario());
		}
    cargarRecursosYRoles();
	}
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public Usuario getUsuarioEditar() {
		if(usuarioSessionMBean.getUsuarioEditar() == null) {
			usuarioSessionMBean.setUsuarioEditar(new Usuario());
		}
		return usuarioSessionMBean.getUsuarioEditar();
	}
	
	//Siempre deberia invocarse este metodo para setear el usuario a editar ya que
	//tambien se encarga de traer los roles asociados al usuario
	public void setUsuarioEditar(Usuario usuario) {
		if(usuario == null) {
			usuario = new Usuario();
		}
		usuarioSessionMBean.setUsuarioEditar(usuario);
		
		//Es posible que aun no hay aun un id de usuario (si es nuevo), hay que controlarlo al guardar!
		UsuarioEmpresaRoles ueRoles = new UsuarioEmpresaRoles(usuario.getId(), sessionMBean.getEmpresaActual().getId());
		usuarioSessionMBean.setUsuarioEmpresaRolesEditar(ueRoles);
		try {
			List<String> roles = usuariosEJB.obtenerRolesUsuarioEmpresa(usuario.getId(), sessionMBean.getEmpresaActual().getId());
			if(roles != null) {
				for(String rol : roles) {
					ueRoles.marcarRol(rol, true);
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public UsuarioEmpresaRoles getUsuarioEmpresaRolesEditar() {
		if(usuarioSessionMBean.getUsuarioEmpresaRolesEditar() == null) {
			setUsuarioEditar(new Usuario());
		}
		return usuarioSessionMBean.getUsuarioEmpresaRolesEditar();
	}
	
	
	public Row<Usuario> getRowSelect() {
		return rowSelect;
	}
	
	public void setRowSelect(Row<Usuario> rowSelect) {
		this.rowSelect = rowSelect;
	}

	public DataTable getUsuariosDataTable() {
		return usuariosDataTable;
	}

	public void setUsuariosDataTable(DataTable usuariosDataTable) {
		this.usuariosDataTable = usuariosDataTable;
	}

	//Lista de usuarios para seleccionar en la eliminacion/modificacion.
	public RowList<Usuario> getUsuariosSeleccion() {
		try {
			Empresa empresaActual = sessionMBean.getEmpresaActual();
			List<Usuario> entidades = usuariosEJB.consultarUsuariosEmpresa(empresaActual.getId());
			usuariosSeleccion = new RowList<Usuario>(entidades);
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
		return usuariosSeleccion;
	}
	
	public String guardar() {
		
		limpiarMensajesError();
		
		if (getUsuarioEditar() != null) {
			try {
				Empresa empresaActual = sessionMBean.getEmpresaActual();
				Usuario usuario = getUsuarioEditar();
				boolean hayErrores = false;
				if(usuario.getCodigo()==null || usuario.getCodigo().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_codigo_del_usuario_es_obligatorio"), "form:codigoUsuario");
				}
				if(usuario.getNombre()==null || usuario.getNombre().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_usuario_es_obligatorio"), "form:nombreUsuario");
				}
				if(usuario.getCorreoe()==null || usuario.getCorreoe().trim().isEmpty()) {
					hayErrores = true;
					addErrorMessage(sessionMBean.getTextos().get("el_correo_electronico_del_usuario_es_obligatorio"), "form:correoeUsuario");
				}else {
				  Pattern pat = Pattern.compile(Utiles.EMAIL_PATTERN);
					Matcher mat = pat.matcher(usuario.getCorreoe());
					if(!mat.find()){
						addErrorMessage(sessionMBean.getTextos().get("correo_electronico_no_valido"), "form:correoeUsuario");
					  hayErrores = true;
					}
				}
				UsuarioEmpresaRoles ueRoles = getUsuarioEmpresaRolesEditar();
				if(hayErrores) {
					return null;
				}
				
				boolean nuevo = usuario.getId() == null;
				//Si el usuario no esta en la empresa actual, se agrega ahora
				if(usuario.getFechaBaja()!=null)
				{
					usuario.setFechaBaja(null);
				}
				if(usuario.getEmpresas()==null) {
					usuario.setEmpresas(new ArrayList<Empresa>());
				}
				if(!usuario.getEmpresas().contains(empresaActual)) {
					usuario.getEmpresas().add(empresaActual);
				}
				//Guardar el usuario
				usuario = usuariosEJB.guardarUsuario(usuario);
				//Guardar los roles por empresa
				ueRoles.setIdUsuario(usuario.getId()); //Solo es necesario si es un usuario nuevo
				usuariosEJB.guardarRolesUsuarioEmpresa(ueRoles);
				//Guardar los roles por recurso
				recursosEJB.asociarRolesUsuarioRecurso(usuario.getId(), rolesRecurso);
				//Generar y enviar contraseña
				if(usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
					//Si hay CDA no se envia el password
					if(!sessionMBean.getBackendConCda()) {
						usuariosEJB.generarYEnviarPassword(usuario);
					}
				}
				//Recargar los datos del usuario
				setUsuarioEditar(usuario);
				//Recargar la lista de usuarios
				List<Usuario> entidades = usuariosEJB.consultarUsuarios();
				usuariosSeleccion = new RowList<Usuario>(entidades);
				if(nuevo) {
					addInfoMessage(sessionMBean.getTextos().get("usuario_creado"), MSG_ID);
				}else {
					addInfoMessage(sessionMBean.getTextos().get("usuario_modificado"), MSG_ID);
				}
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String modificar() {
		this.rowSelect = (Row<Usuario>)usuariosDataTable.getRowData();
		if (this.rowSelect != null){
			usuariosSeleccion.setSelectedRow(this.rowSelect);
			setUsuarioEditar(usuariosSeleccion.getSelectedRow().getData());
			return "modificar";
		}	else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_usuario_seleccionado"), MSG_ID);
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public void selecUsuarioEliminar(ActionEvent e){
		Usuario usuarioEliminar = ((Row<Usuario>)usuariosDataTable.getRowData()).getData();
		usuarioSessionMBean.setUsuarioEliminar(usuarioEliminar);
	}
	
	/**
	 * Este método no elimina realmente al usuario de la base de datos sino que solo lo desasocia de la empresa.
	 * Lo que sí elimina de la base de datos es la relación con los permisos por recurso.
	 * @return
	 */
	public String eliminar() {
		Usuario usuarioEliminar = usuarioSessionMBean.getUsuarioEliminar(); 
		if (usuarioEliminar != null){
			try {
				Empresa empActual = sessionMBean.getEmpresaActual();
				usuariosEJB.eliminarUsuarioEmpresa(usuarioEliminar, empActual);
				//Recargar la lista de usuarios
				List<Usuario> entidades = usuariosEJB.consultarUsuarios();
				usuariosSeleccion = new RowList<Usuario>(entidades);
				usuarioSessionMBean.setUsuarioEliminar(null); 
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		}	else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_usuario_seleccionado"), MSG_ID);
		}
		return null;
	}
	
	public String enviarPassword() {
		//Si hay CDA no se envia el password
		if(sessionMBean.getBackendConCda()) {
			return null;
		}
		
		Usuario usuario = getUsuarioEditar();
		if (usuario != null) {
			try {
				usuario = usuariosEJB.generarYEnviarPassword(usuario);
				setUsuarioEditar(usuario);
				addInfoMessage(sessionMBean.getTextos().get("la_contrasena_fue_enviada"));
			}catch(ApplicationException aEx) {
				aEx.printStackTrace();
				addErrorMessage(aEx.getMessage(), MSG_ID);
			}
		}
		return null;
	}
	
	
	public void cambioCodigoUsuario(ValueChangeEvent event) {
		String codigo = (String)event.getNewValue();
		try {
			Usuario usuario = usuariosEJB.obtenerUsuarioPorCodigo(codigo);
			setUsuarioEditar(usuario); //El metodo setUsuarioEditar se encarga de manejar el caso de null
		} catch (ApplicationException e) {
			setUsuarioEditar(null); //El metodo setUsuarioEditar se encarga de manejar el caso de null
		}
		
	}
	
	public UsuarioSessionMBean getUsuarioSessionMBean() {
		return usuarioSessionMBean;
	}
	
	public void setUsuarioSessionMBean(UsuarioSessionMBean usuarioSessionMBean) {
		this.usuarioSessionMBean = usuarioSessionMBean;
	}

	//setearlo en <f:view beforePhase de la pagina.
	public void beforePhaseCrearModificar(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			if(request.getParameter("n")!=null) {
				sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("crear_usuario"));
			}else {
				sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("modificar_usuario"));
			}
		}
	}

	public void beforePhaseConsultar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("consultar_usuarios"));
		}
	}
	
	/**
	 * Método invocado cuando se le pone o quita la marca de superadmin a un usuario.
	 * Si determina que no hay otro superadmin entonces se muestra un mensaje de advertencia (pero igual se permite)
	 */
	public void cambioSuperadmin() {
		//Si deja de ser superadmin y es el último mostrar un mensaje de advertencia
		Usuario usuario = getUsuarioEditar();
		if(usuario.getId()!=null && usuario.getSuperadmin()!=null && !usuario.getSuperadmin().booleanValue()) {
  		//Ver si hay al menos otro superadmin
  		try {
  			boolean hayOtroSuperadmin = usuariosEJB.hayOtroSuperadmin(usuario.getId());
  			if(!hayOtroSuperadmin) {
  				addAdvertenciaMessage(sessionMBean.getTextos().get("no_hay_otro_superadmin"), "form:superadministrador");
  			}
  		}catch(ApplicationException ex) {
  			addAdvertenciaMessage(sessionMBean.getTextos().get("no_se_pudo_determinar_si_hay_otro_superadministrador"), "form:superadministrador");
  		}
		}
		//Si se pone la marca de superadmin se blanquean los roles específicos
		if(usuario.getSuperadmin()!=null && usuario.getSuperadmin().booleanValue()) {
			UsuarioEmpresaRoles uer = getUsuarioEmpresaRolesEditar();
			uer.setAdministrador(false);
			uer.setfAtencion(false);
			uer.setfCallCenter(false);
			uer.setLlamador(false);
			uer.setPlanificador(false);
		}
	}
	
// =============================================================================
  private List<Recurso> listaRecursos;
  private List<SelectItem> listaRolesRecurso;
  private Map<Integer, String[]> rolesRecurso;
  
  private void cargarRecursosYRoles() {
    try {
      listaRecursos = new ArrayList<Recurso>();
      rolesRecurso = new HashMap<Integer, String[]>();
      
      List<Agenda> agendas = generalEJB.consultarAgendas();
      if(agendas != null) {
        for(Agenda agenda : agendas) {
          List<Recurso> recursosAgenda = generalEJB.consultarRecursos(agenda);
          if(recursosAgenda != null) {
            for(Recurso recursoAgenda : recursosAgenda) {
              listaRecursos.add(recursoAgenda);
              rolesRecurso.put(recursoAgenda.getId(), new String[0]);
            }
          }
        }
      }
      Collections.sort(listaRecursos, new Comparator<Recurso>() {

        @Override
        public int compare(Recurso o1, Recurso o2) {
          String nombre1 = o1.getAgenda().getNombre();
          String nombre2 = o2.getAgenda().getNombre();
          int compAgendas = nombre1.compareTo(nombre2);
          if(compAgendas != 0) {
            return compAgendas;
          }
          nombre1 = o1.getNombre();
          nombre2 = o2.getNombre();
          return nombre1.compareTo(nombre2);
        }
        
      });
      
      listaRolesRecurso = new ArrayList<SelectItem>();
      for(RolesXRecurso rol : RolesXRecurso.values()) {
        listaRolesRecurso.add(new SelectItem(rol, sessionMBean.getTextos().get(rol.getClave())));
      }
      
      Usuario usuario = getUsuarioEditar();
      if(usuario.getId()!=null) {
        List<RolesUsuarioRecurso> rolesRecursoUsuario = recursosEJB.getRolesUsuarioRecurso(usuario.getId());
        for(RolesUsuarioRecurso rolRecursoUsuario : rolesRecursoUsuario) {
          Integer recursoId = rolRecursoUsuario.getId().getRecursoId();
          if(rolesRecurso.containsKey(recursoId)) {
            String[] roles;
            if(rolRecursoUsuario.getRoles()==null) {
              roles = null;
            }else {
              roles = rolRecursoUsuario.getRoles().split(",");
              for(int i=0; i<roles.length; i++) {
                roles[i] = roles[i].trim();
              }
            }
            rolesRecurso.put(recursoId, roles);
          }
        }
      }
    } catch (ApplicationException e) {
      e.printStackTrace();
    }
  }

  public List<Recurso> getListaRecursos() {
    return listaRecursos;
  }

  public List<SelectItem> getListaRolesRecurso() {
    return listaRolesRecurso;
  }

  public Map<Integer, String[]> getRolesRecurso() {
    return rolesRecurso;
  }
  
  
	
}

