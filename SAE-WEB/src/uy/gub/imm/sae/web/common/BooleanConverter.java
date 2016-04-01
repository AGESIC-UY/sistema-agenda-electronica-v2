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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import uy.gub.imm.sae.web.mbean.administracion.SessionMBean;

public class BooleanConverter implements Converter {

	public BooleanConverter() {
	}
	
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		if(arg2 == null) {
			return null;
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		SessionMBean sessionMBean = context.getApplication().evaluateExpressionGet(context, "#{sessionMBean}", SessionMBean.class);
		String si = sessionMBean!=null?sessionMBean.getTextos().get("si"):"Sí";
		
		if(si.equalsIgnoreCase(arg2)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		
		if(arg2==null || !(arg2 instanceof Boolean)) {
			return null;
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		SessionMBean sessionMBean = context.getApplication().evaluateExpressionGet(context, "#{sessionMBean}", SessionMBean.class);
		
		Boolean b = (Boolean)arg2;
		if(b.booleanValue()) {
			return sessionMBean!=null?sessionMBean.getTextos().get("si"):"Sí";
		}else {
			return sessionMBean!=null?sessionMBean.getTextos().get("no"):"No";
		}
		
		
	}
	
}
