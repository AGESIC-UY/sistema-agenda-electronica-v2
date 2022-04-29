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

package uy.gub.imm.sae.acciones.ejb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.utilidades.MailUtiles;
import uy.gub.imm.sae.common.SAEProfile;
import uy.gub.sae.acciones.business.dto.RecursoDTO;
import uy.gub.sae.acciones.business.ejb.EjecutorAccionLocal;
import uy.gub.sae.acciones.business.ejb.EjecutorAccionRemote;
import uy.gub.sae.acciones.business.ejb.ErrorAccion;
import uy.gub.sae.acciones.business.ejb.ResultadoAccion;
import uy.gub.sae.acciones.business.ejb.exception.InvalidParametersException;
import uy.gub.sae.acciones.business.ejb.exception.UnexpectedAccionException;


/**
 * Ejectua la accion de nombre @param nombreAccion
 *  
 * @param nombreAccion
 * @param params es un hash que tiene en forma de <key,value> los datos ingresados por el usuario que realizó la reserva
 * de forma tal que key = nombre del parametro y value = el valor ingresado. Habrán tantos parámetros como los defindos por la accion, 
 * en este caso es variable par poder reutilizar la accion en diversos formlarios con diferntes campos.
 * Adicionalmente se espera que este hash tenga otra serie de parametros extras: 
 * <"RECURSO", uy.gub.sae.acciones.business.dto.RecursoDTO> Viene dado por programacion
 * <"RESERVA, uy.gub.imm.sae.business.dto.ReservaDTO> Viene dado por programacion
 * <"MAIL", String> El mail que ingresó el usuario (su valor es opcional), este parametro debe configurarse en la asociación de acción
 * <"NOTIFICAR", Boolean> El check que ingresó el usuario (el parametro es opcional), de utilizarse debe configurarse en la asociación de acción
 * 
 * @return
 * @throws UnexpectedAccionException
 * @throws InvalidParametersException
 */

@Stateless
public class AccionNotificarBean implements
		EjecutorAccionLocal, EjecutorAccionRemote {

	
	//Acciones que implementa
	private final String ACCION_NOTIFICAR_CONFIRMACION_DE_RESERVA =  "NOTIFICAR_CONFIRMACION_DE_RESERVA";
	private final String ACCION_NOTIFICAR_CANCELACION_DE_RESERVA  =  "NOTIFICAR_CANCELACION_DE_RESERVA";
	
	
	
	//Parametros nombrados
	private final String PARAM_RECURSO = "RECURSO";
	private final String PARAM_RESERVA = "RESERVA";
	private final String PARAM_MAIL = "MAIL";
	private final String PARAM_NOTIFICAR = "NOTIFICAR";
	
	
	// errores
	private final String CODIGO_ERROR_NOTIFICACION = "-1";
	
	
	private final String PROP_SUBJECT_CANCELACION   = "uy.gub.imm.sae.acciones.notificar.mail.subject.cancelacion";
	private final String PROP_SUBJECT_CONFIRMACION  = "uy.gub.imm.sae.acciones.notificar.mail.subject.confirmacion";
	private final String PROP_BODY_CANCELACION_URL  = "uy.gub.imm.sae.acciones.notificar.mail.body.cancelacion.templates.url";
	private final String PROP_BODY_CONFIRMACION_URL = "uy.gub.imm.sae.acciones.notificar.mail.body.confirmacion.templates.url";

//	@Resource(mappedName="java:/sae/mail")
//    private Session mailSession;

	private Logger log = Logger.getLogger(AccionNotificarBean.class);
	
	
	@Override
	public ResultadoAccion ejecutar(String nombreAccion, Map<String, Object> params)
			throws UnexpectedAccionException, InvalidParametersException {

		ResultadoAccion res = new ResultadoAccion();
		
		try {
			
			if(params == null){
				throw new InvalidParametersException("Falta parametro <params>");
			} else if (nombreAccion == null){
				throw new InvalidParametersException("Falta parametro <nombreAccion");
			} else {
				
				Boolean confirmacion;
				
				if (nombreAccion.equals(ACCION_NOTIFICAR_CONFIRMACION_DE_RESERVA)){
					confirmacion = true;
				} else if (nombreAccion.equals(ACCION_NOTIFICAR_CANCELACION_DE_RESERVA)) {
					confirmacion = false;
				}
				else {
					throw new InvalidParametersException("No existe una acción con nombreAccion = " + nombreAccion);
				}
				
				RecursoDTO recurso = (RecursoDTO)params.get(PARAM_RECURSO);
				ReservaDTO reservaDTO = (ReservaDTO)params.get(PARAM_RESERVA);

				//Si el mail esta vacio, es xq se configuró opcional, por lo tanto no voy a notifiar
				String mail = ("".equals(params.get(PARAM_MAIL)) ? null : (String)params.get(PARAM_MAIL));
				
				//Si no me pasan el parametro de notifiar es xq siempre quiero notificar
				Boolean notificar = (params.get(PARAM_NOTIFICAR) != null ? (Boolean)params.get(PARAM_NOTIFICAR) : true);
				
				Map<String, Object> datos = new HashMap<String, Object>();
				for (String key : params.keySet()) {
					if (!(key.equals(PARAM_MAIL) || key.equals(PARAM_NOTIFICAR) || key.equals(PARAM_RECURSO) || key.equals(PARAM_RESERVA))) {
						datos.put(key, params.get(key));
					}
				}
				
				if (mail != null && notificar) {
					//Logica de la accion de notifacion
					res = notificar(reservaDTO, recurso, mail, datos, confirmacion);
				}
				else if (mail == null && notificar) {
					res.addError(CODIGO_ERROR_NOTIFICACION, "NOTIFICAR == true y MAIL = null. No es posible notificar sin proporcionar un mail");
				}
				
				
			}
			
		} catch(UnexpectedAccionException e) {
			throw e;
		} catch(InvalidParametersException e) {
			throw e;
		} catch (Exception e) {
			throw new UnexpectedAccionException("Error inesperado catcheado como Exception", e);
		}

		if (res.hayErrores()) {
			for (ErrorAccion err : res.getErrores()) {
				log.error(err.getMensaje());
			}
			res = new ResultadoAccion();
		}
		return res;
	}
	

	
	private ResultadoAccion notificar(ReservaDTO reserva, RecursoDTO recurso, String mail, Map<String, Object> datos, Boolean confirmacion) 
		throws UnexpectedAccionException, InvalidParametersException {

		ResultadoAccion res = new ResultadoAccion();
		
		
        try    {
        	
    		Properties saeProperties = SAEProfile.getInstance().getProperties();
    		
    		String subject = (confirmacion ? saeProperties.getProperty(PROP_SUBJECT_CONFIRMACION): saeProperties.getProperty(PROP_SUBJECT_CANCELACION));
        	String contentTemplateURL = (confirmacion ? saeProperties.getProperty(PROP_BODY_CONFIRMACION_URL): saeProperties.getProperty(PROP_BODY_CANCELACION_URL));
    		contentTemplateURL = String.format(contentTemplateURL, saeProperties.getProperty(SAEProfile.ENVIRONMENT_PROFILE_HOST_PORT_KEY));
        	
        	StringBuffer content = buildContent(contentTemplateURL, reserva, recurso, datos);
        	//MailUtiles.enviarMail(mailSession, mail, subject, content.toString(), MailUtiles.CONTENT_TYPE_HTML);
        	MailUtiles.enviarMail(mail, subject, content.toString(), MailUtiles.CONTENT_TYPE_HTML);
        }
        catch (javax.mail.MessagingException e)
        {
    		res.addError(CODIGO_ERROR_NOTIFICACION, e.getMessage());
        	e.printStackTrace();
        } catch (MalformedURLException e) {
    		res.addError(CODIGO_ERROR_NOTIFICACION, e.getMessage());
        	e.printStackTrace();
		} catch (IOException e) {
    		res.addError(CODIGO_ERROR_NOTIFICACION, e.getMessage());
        	e.printStackTrace();
		}


		return res;
	}

	

	private StringBuffer buildContent(String contentTemplate,
			ReservaDTO reserva, RecursoDTO recurso, Map<String, Object> datos)
			throws MalformedURLException, IOException {

		String content = readContent(contentTemplate).toString();
		
		DateFormat df = DateFormat.getDateInstance();
		DateFormat tf = DateFormat.getTimeInstance();
		
		content = content.replaceAll("\\{AGENDA_DESCRIPCION\\}", Matcher.quoteReplacement(recurso.getAgendaDescripcion()));
		content = content.replaceAll("\\{RECURSO_DESCRIPCION\\}", Matcher.quoteReplacement(recurso.getDescripcion()));
		content = content.replaceAll("\\{RESERVA_FECHA\\}", Matcher.quoteReplacement(df.format(reserva.getFecha())));
		content = content.replaceAll("\\{RESERVA_HORA\\}", Matcher.quoteReplacement(tf.format(reserva.getHoraInicio())));
		content = content.replaceAll("\\{RESERVA_NUMERO\\}", Matcher.quoteReplacement((recurso.getSerie()!=null ? recurso.getSerie()+" ": "") + reserva.getNumero()));

		for (String key : datos.keySet()) {
			String valor = ( datos.get(key) == null ? "" : Matcher.quoteReplacement((String)datos.get(key)));
			content = content.replaceAll("\\{"+key+"\\}", valor);
		}
		
		return new StringBuffer(content);
	}

	
	private StringBuffer readContent(String contentTemplate)
			throws MalformedURLException, IOException {

		StringBuffer content = new StringBuffer();

		URL templateUrl = new URL(contentTemplate);
		URLConnection cxn = templateUrl.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				cxn.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();

		return content;
	}

	
}
