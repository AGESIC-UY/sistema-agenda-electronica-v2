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

package uy.gub.imm.sae.web.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputFormat;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.fieldset.Fieldset;
import org.primefaces.component.message.Message;

import uy.gub.imm.sae.common.SAEProfile;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.web.mbean.administracion.SessionMBean;
import uy.gub.imm.sae.web.mbean.reserva.SesionMBean;


/**
 * Clase utilitaria, tiene la logica para generar componentes jsf y richfaces en runtime
 * para desplegar el formulario de ingreso de datos de la reserva en forma dinamica 
 * según el modelo de datos definido.
 * @author im2716295
 *
 */
public class FormularioDinamicoReserva {
	public enum TipoFormulario {LECTURA, EDICION, CONSULTA};
	
	public static final String STYLE_CLASS_CAMPO_CON_ERROR = "formularioCampoConError"; //Se aplica al campo específico
	public static final String STYLE_CLASS_DATO_CON_ERROR = "form-group-con-error"; //Se aplica a todo el dato (label, campo, ayuda)
	
	public static final String STYLE_CLASS_CAMPO_SIN_ERROR = "formularioCampoSinError";
	

	private static final String STYLE_CLASS_FORMULARIO = "form-horizontal";
	private static final String STYLE_CLASS_TEXTO_ETIQUETA = "col-sm-3 control-label label";
	 
	private static final String STYLE_CLASS_CAMPO = "form-control";
	private static final String STYLE_CLASS_REQUERIDO = "formularioCampoRequerido";

	//Bandera que inidca si el formulario debe ser de solo lectura.
	private boolean soloLectura;
	private TipoFormulario tipoFormulario;
	
	//En caso de formulario de edicion
	private String managedBean;
	private String nombreFormulario;
	
	//En caso de formulario de solo lectura
	private Map<String, Object> valores;
	
	private Application app;

	private HtmlPanelGroup formularioGrilla;
	private HtmlPanelGroup mensajesGrilla;
	
	private String formatoFecha;
	private DateFormat formateadorFecha;
	
	/*
	 * Crea un formulario editable de un tipo (consulta o edicion)
	 */	
	public FormularioDinamicoReserva(String managedBeanName, String nombreFormulario, TipoFormulario tipo, Map<String, Object> valores, String formatoFecha) {
		this.managedBean = managedBeanName;
		this.nombreFormulario = nombreFormulario;
		this.app = FacesContext.getCurrentInstance().getApplication();
		this.tipoFormulario = tipo;
		this.soloLectura = (tipo == TipoFormulario.LECTURA);
		if(valores != null) {
		  this.valores = new HashMap<String, Object>(valores);
		}
		configurarFormatoFecha(formatoFecha);
	}
	
	public FormularioDinamicoReserva(Map<String, Object> valores, String formatoFecha) {
		this.valores = new HashMap<String, Object>(valores);
		this.app = FacesContext.getCurrentInstance().getApplication();
		this.tipoFormulario = TipoFormulario.LECTURA;
		this.soloLectura = true;
		configurarFormatoFecha(formatoFecha);
	}

	private void configurarFormatoFecha(String formatoFecha) {
		this.formatoFecha = formatoFecha;
		try {
			if(formatoFecha == null) {
				throw new Exception("Formato de fecha inválido");
			}
			this.formateadorFecha = new SimpleDateFormat(formatoFecha);
		}catch(Exception ex) {
			this.formatoFecha = "dd/MM/yyyy";
			this.formateadorFecha = new SimpleDateFormat(this.formatoFecha);
		}
	}
	
	public static void marcarCamposError(List<String> idComponentes, UIComponent formulario) {
		for (String id : idComponentes) {
			UIComponent comp = formulario.findComponent(id);
			if (comp instanceof HtmlInputText) {
				HtmlInputText input = (HtmlInputText) comp;
				input.setStyleClass(STYLE_CLASS_CAMPO_CON_ERROR);
			}
			if (comp instanceof HtmlSelectOneMenu) {
				HtmlSelectOneMenu input = (HtmlSelectOneMenu) comp;
				input.setStyleClass(STYLE_CLASS_CAMPO_CON_ERROR);
			}
			//Intentar marcar tambien al contenedor
			comp =  comp.getParent();
			if(comp != null) {
				comp = comp.getParent();
			}
			if(comp instanceof HtmlPanelGroup) {
				HtmlPanelGroup panel = (HtmlPanelGroup)comp;
				panel.setStyleClass(panel.getStyleClass()+" "+STYLE_CLASS_DATO_CON_ERROR);
			}
		}
	}

	public static void desmarcarCampos(List<String> idComponentes, UIComponent formulario) {
		for (String id : idComponentes) {
			UIComponent comp = formulario.findComponent(id);
			if(comp != null) {
	      if (comp instanceof HtmlInputText) {
	        HtmlInputText input = (HtmlInputText) comp;
	        input.setStyleClass("");
	      }
	      if (comp instanceof HtmlSelectOneMenu) {
	        HtmlSelectOneMenu input = (HtmlSelectOneMenu) comp;
	        input.setStyleClass("");
	      }
  			//Intentar marcar tambien al contenedor
  			comp =  comp.getParent();
  			if(comp != null) {
  				comp = comp.getParent();
  			}
  			if(comp instanceof HtmlPanelGroup) {
  				HtmlPanelGroup panel = (HtmlPanelGroup)comp;
  				String styleClass = panel.getStyleClass();
  				if(styleClass != null) {
  					styleClass = styleClass.replace(STYLE_CLASS_DATO_CON_ERROR, "");
  					panel.setStyleClass(styleClass);
  				}
  			}
			}
		}
	}
	
	public void armarFormulario(List<AgrupacionDato> agrupaciones, HashMap<Integer,HashMap<Integer,ServicioPorRecurso>> serviciosAutocompletar) {
		if (!(tipoFormulario == TipoFormulario.LECTURA)) {
			//Seccion de mensajes de error
			mensajesGrilla = (HtmlPanelGroup) app.createComponent(HtmlPanelGroup.COMPONENT_TYPE);
			//Mensaje generico
			mensajesGrilla.getChildren().add(armarMensajeValidacion(nombreFormulario));
		}
		formularioGrilla = (HtmlPanelGroup) app.createComponent(HtmlPanelGroup.COMPONENT_TYPE);
		formularioGrilla.setId(nombreFormulario);
		formularioGrilla.setLayout("block");
		for (AgrupacionDato agrupacionDato : agrupaciones) {
			UIComponent agrupacion = armarAgrupacion(agrupacionDato, serviciosAutocompletar);
			formularioGrilla.getChildren().add(agrupacion);
		}
	}
	
	private UIComponent armarAgrupacion(AgrupacionDato agrupacionDato, HashMap<Integer,HashMap<Integer,ServicioPorRecurso>> serviciosAutocompletar) {
		Fieldset panel = new Fieldset();
		panel.setLegend(agrupacionDato.getEtiqueta());
		HtmlPanelGroup grid = (HtmlPanelGroup) app.createComponent(HtmlPanelGroup.COMPONENT_TYPE);
		grid.setLayout("block");
		grid.setStyleClass(STYLE_CLASS_FORMULARIO);
		panel.getChildren().add(grid);
		//Los datos deberían estar ordenados por fila,columna.
		//y la numeracion comienza en (1,1)
		List<DatoASolicitar> datos = new ArrayList<DatoASolicitar>(agrupacionDato.getDatosASolicitar());
		for (DatoASolicitar datoASolicitar : datos) {
			UIComponent campo [] = armarCampo(datoASolicitar);
			HtmlPanelGroup inputs = new HtmlPanelGroup();
			inputs.setLayout("block");
			inputs.setStyleClass("form-group");
			grid.getChildren().add(inputs);   //agrupo las entradas para el caso de campos relacionados ej: dir, apto, bloque.
			inputs.getChildren().add(campo[0]); //Etiqueta
			HtmlPanelGroup inputValor = new HtmlPanelGroup();
			inputValor.setLayout("block");
			inputValor.setStyleClass("col-sm-9");
			inputValor.getChildren().add(campo[1]);
			inputs.getChildren().add(inputValor); //Input
			if (tipoFormulario == TipoFormulario.EDICION){
				inputValor.getChildren().add(campo[2]); //Ayuda
				if (serviciosAutocompletar != null){
					HashMap<Integer,ServicioPorRecurso> servicios = serviciosAutocompletar.get(datoASolicitar.getId());
					if (servicios != null){
						String claves = "";
						for (Integer keyServicio : servicios.keySet()){
							claves += servicios.get(keyServicio).getId().toString() + "|";
						}
						UIParameter parameter = new UIParameter();
						parameter.setName("paramIdsServicio");
						parameter.setValue(claves);
						CommandButton btn = new CommandButton();
						MethodExpression me = app.getExpressionFactory().createMethodExpression(FacesContext.getCurrentInstance().getELContext(),"#{paso3MBean.autocompletarCampo}",String.class, new Class<?>[0]);
						btn.setActionExpression(me);
						btn.setValue("completar");
						btn.setStyleClass("arriba");
						btn.getChildren().add(parameter);
						inputValor.getChildren().add(btn); //Boton
					}
				}
			}
		}
		return panel;
	}
	
	public UIComponent getComponenteFormulario() {
		return formularioGrilla;
	}

	public UIComponent getComponenteMensajes() {
		return mensajesGrilla;
	}

	/**
	 * Un campo consiste de una etiqueta y un campo editable, o sea un input.
	 * resultado:
	 *  [0] = etiqueta del campo
	 *  [1] = input
	 *  [2] = {si hay ayuda, ayuda, sino mensaje de error}
	 *  [3] = {si hay ayuda, mensaje de error, sino nada}
	 */
	private UIComponent [] armarCampo(DatoASolicitar dato) {
		HtmlOutputLabel etiqueta = new HtmlOutputLabel();
		etiqueta.setValue(dato.getEtiqueta());
		etiqueta.setStyleClass(STYLE_CLASS_TEXTO_ETIQUETA);
		if (dato.getRequerido() && ! soloLectura && ! (tipoFormulario == TipoFormulario.CONSULTA)) {
			etiqueta.setStyleClass(etiqueta.getStyleClass()+" "+STYLE_CLASS_REQUERIDO);
		}
		UIComponent input;
		switch (dato.getTipo()) {
		case STRING:
			input = armarCampoString(dato);
			break;
		case DATE:
			input = armarCampoDate(dato);
			break;
		case LIST:
			input = armarCampoList(dato);
			break;
		case BOOLEAN:
			input = armarCampoBoolean(dato);
			break;
		default:
			input = armarCampoString(dato);
			break;
		}
		input.setId(dato.getNombre());
		//Para el caso de las fechas, el componente arma un span que tiene el id dado, y deja el input dentro con el mismo id seguido de "_input"
		if(soloLectura || !dato.getTipo().equals(Tipo.DATE)) {
			etiqueta.setFor(input.getId());
		}else {
			etiqueta.setFor(input.getId()+"_input");
		}
		HtmlOutputFormat imgAyuda = new HtmlOutputFormat();
		imgAyuda.setValue("<img alt='información' src='"+SAEProfile.getInstance().getProperties().getProperty(SAEProfile.PROFILE_UI_RESOURCE_BASE_URL_KEY)+"/recursos/images/info.png' TITLE='" + dato.getTextoAyuda() + "'/>");
		imgAyuda.setEscape(false);
		Message mensaje = armarMensajeValidacion(dato.getNombre());
		UIComponent campo [] = new UIComponent[4];
		campo[0] = etiqueta;
		campo[1] = input;
		if (dato.getTextoAyuda() != null && ! dato.getTextoAyuda().equals("")) {
			campo[2] = imgAyuda;
			campo[3] = mensaje;
		}else {
			campo[2] = mensaje;
		}
		return campo;
	}
	
	/**
	 * Un campo de tipo string consiste en un campo editable.
	 */
	private UIComponent armarCampoString (DatoASolicitar dato) {
		HtmlInputText input = (HtmlInputText) app.createComponent(HtmlInputText.COMPONENT_TYPE);
		input.setMaxlength(dato.getLargo());
		input.setSize(dato.getLargo());
		input.setStyleClass(STYLE_CLASS_CAMPO);
		input.setAutocomplete("off");
		if (soloLectura) {
			input.setValue(this.valores.get(dato.getNombre()));
			input.setReadonly(true);
			input.setDisabled(true);
		}else {
			//Le configuro le managed bean donde debe almacenar el valor que ingrese el usuario.
			ValueExpression ve = armarExpresion(dato.getNombre(), String.class);
			input.setValueExpression("value", ve);
	    //Si el campo es de solo lectura y hay un valor para él, entonces se hace readonly
	    if(dato.getSoloLectura()!=null && dato.getSoloLectura().booleanValue()) {
	      if(this.valores!=null && this.valores.get(dato.getNombre())!=null) {
	        input.setReadonly(true);
	        input.setDisabled(true);
	      }
	    }
		}
		return input;
	}
	
	/**
	 * Un campo de tipo date consiste en un calendario
	 */
	private UIComponent armarCampoDate (DatoASolicitar dato) {
		UIComponent campo = null;
		if (soloLectura) {
			HtmlInputText input = (HtmlInputText) app.createComponent(HtmlInputText.COMPONENT_TYPE);
      String sFecha="";           
      try {
        String fecha = (String)this.valores.get(dato.getNombre());
        if(fecha != null) {
          sFecha = formateadorFecha.format(Utiles.stringToDate(fecha));
        }
      } catch (ParseException ex) {
        sFecha = (String)this.valores.get(dato.getNombre());
      }
      input.setValue(sFecha);
      input.setStyleClass(STYLE_CLASS_CAMPO+" datepicker-dis");
      input.setReadonly(true);
			input.setDisabled(true);
			campo = input;
		} else {
			Calendar calendario = (Calendar) app.createComponent(Calendar.COMPONENT_TYPE);
			calendario.setAutocomplete("off");
			calendario.setStyleClass(STYLE_CLASS_CAMPO_SIN_ERROR);
			calendario.setLocale(getLocale());
			calendario.setLang(getLocale().getLanguage());
			calendario.setNavigator(true);
			calendario.setYearRange("1900:c+10");
			calendario.getAttributes().put("pattern", formatoFecha);
      calendario.getAttributes().put("onError", "zero");
			calendario.setConverter(new DateConverter());
			//Le configuro el managed bean donde debe almacenar el valor que ingrese el usuario.
			ValueExpression ve = armarExpresion(dato.getNombre(), Date.class);
			calendario.setValueExpression("value", ve);
	    calendario.setPattern(formatoFecha);
      //Si el campo es de solo lectura y hay un valor para él, entonces se hace readonly
      if(dato.getSoloLectura()!=null && dato.getSoloLectura().booleanValue()) {
        if(this.valores!=null && this.valores.get(dato.getNombre())!=null) {
          calendario.setReadonly(true);
          calendario.setDisabled(true);
        }
      }
			campo = calendario;
		}
		return campo;
	}
	
	/**
	 * Un campo de tipo List consiste de una etiqueta y una lista de valores desplegable
	 */
	private UIComponent armarCampoList (DatoASolicitar dato) {
		UIComponent campo = null;
		if(soloLectura) {
			HtmlInputText input = (HtmlInputText) app.createComponent(HtmlInputText.COMPONENT_TYPE);
			input.setStyleClass(STYLE_CLASS_CAMPO);
			input.setReadonly(true);
			input.setDisabled(true);
			//Busco la etiqueta del valor posible:
			Iterator<ValorPosible> iter = dato.getValoresPosibles().iterator();
			String etiqueta = null;
			while (iter.hasNext() && etiqueta == null) {
				ValorPosible vp = iter.next();
				if (vp.getValor().equals(this.valores.get(dato.getNombre()))) {
					etiqueta = vp.getEtiqueta();
				}
			}
			if(etiqueta != null) {
				input.setValue(etiqueta);
			}else {
				//Por precaución, aunque siempre debería poder obtener la etiqueta del valor.
				input.setValue(this.valores.get(dato.getNombre()));
			}
			campo = input;
		}else {
			HtmlSelectOneMenu lista = (HtmlSelectOneMenu) app.createComponent(HtmlSelectOneMenu.COMPONENT_TYPE);
			lista.setStyleClass(STYLE_CLASS_CAMPO + " " + STYLE_CLASS_CAMPO_SIN_ERROR);
	
			List<UISelectItem> items = armarListaDeValores(dato);
			for (UISelectItem item: items) {
				lista.getChildren().add(item);
			}
			//Le configuro le managed bean donde debe almacenar el valor que ingrese el usuario.
			ValueExpression ve = armarExpresion(dato.getNombre(), String.class);
			lista.setValueExpression("value", ve);
      //Si el campo es de solo lectura y hay un valor para él, entonces se hace readonly
      if(dato.getSoloLectura()!=null && dato.getSoloLectura().booleanValue()) {
        if(this.valores!=null && this.valores.get(dato.getNombre())!=null) {
          lista.setReadonly(true);
          lista.setDisabled(true);
        }
      }
			campo = lista;
		}
		return campo;
	}
	
	/**
	 * Una lista de SelectItem done cada elemento tiene la etiqueta y el valor que se mostrara en la lista desplegable.
	 */
	private List<UISelectItem> armarListaDeValores (DatoASolicitar dato) {
		List<UISelectItem> items = new ArrayList<UISelectItem>();
		// Agrego el resto de las opciones
		for (ValorPosible valor: dato.getValoresPosibles()) {
			UISelectItem item = new UISelectItem();
			item.setItemLabel(valor.getEtiqueta());
			item.setItemValue(valor.getValor());
			items.add(item);
		}
		return items;
	}
	
	/**
	 * Un campo de tipo boolean consiste en un campo editable.
	 */
	private UIComponent armarCampoBoolean (DatoASolicitar dato) {
		UIComponent campo = null;
		if(soloLectura) {
			HtmlSelectBooleanCheckbox input = (HtmlSelectBooleanCheckbox) app.createComponent(HtmlSelectBooleanCheckbox.COMPONENT_TYPE);
      if(this.valores.get(dato.getNombre())!=null) {
        input.setValue(Boolean.valueOf(this.valores.get(dato.getNombre()).toString()));
      }else {
        input.setValue(null);
      }
			input.setDisabled(true);
			campo = input;
		}else {
			HtmlSelectBooleanCheckbox checkbox = (HtmlSelectBooleanCheckbox) app.createComponent(HtmlSelectBooleanCheckbox.COMPONENT_TYPE);			
			//Le configuro le managed bean donde debe almacenar el valor que ingrese el usuario.
			ValueExpression ve = armarExpresion(dato.getNombre(), Boolean.class);
			checkbox.setValueExpression("value", ve);
      //Si el campo es de solo lectura y hay un valor para él, entonces se hace readonly
      if(this.valores!=null && dato.getSoloLectura()!=null && dato.getSoloLectura().booleanValue()) {
        if(this.valores.get(dato.getNombre())!=null) {
          checkbox.setReadonly(true);
          checkbox.setDisabled(true);
        }
      }
			campo = checkbox;
		}
		return campo;
	}
	
	private ValueExpression armarExpresion(String nombre, Class<?> clazz) { 
		//Armar la EL que liga el valor del campo editable a un managed bean generico (Map) que recolectará los datos del formulario
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		ExpressionFactory expFactory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
		String el = "#{"+managedBean+"."+nombre+"}"; 
		ValueExpression ve = expFactory.createValueExpression(elContext, el, clazz);
		return ve;
	}

	private Message armarMensajeValidacion(String para) {
		Message mensaje = (Message) app.createComponent(Message.COMPONENT_TYPE);
		mensaje.setFor(para);
		mensaje.setId("mensaje"+para);
		return mensaje;
	}
	
	public static Map<String, DatoASolicitar> obtenerCampos(List<AgrupacionDato> agrupaciones) {
		Map<String, DatoASolicitar> camposXnombre = new HashMap<String, DatoASolicitar>();
		for (AgrupacionDato agrupacion : agrupaciones) {
			for (DatoASolicitar dato : agrupacion.getDatosASolicitar()) {
				camposXnombre.put(dato.getNombre(), dato);
			}
		}
		return camposXnombre;
	}
	
	private Locale getLocale() {
		Locale locale = null;
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			SessionMBean sessionMBean = context.getApplication().evaluateExpressionGet(context, "#{sessionMBean}", SessionMBean.class);
			locale = new Locale(sessionMBean.getLocale());
		}catch(Throwable ex) {
			locale = null;
		}
		if(locale == null) {
			try {
				SesionMBean sesionMBean = context.getApplication().evaluateExpressionGet(context, "#{sesionMBean}", SesionMBean.class);
				locale = new Locale(sesionMBean.getLocale());
			}catch(Throwable ex) {
				locale = null;
			}
		}
		if(locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}

}
