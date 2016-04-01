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

package uy.gub.imm.sae.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

 /**
 * Titulo: RichfacesFirefoxFilter.java<br>
 * Descripción: usado para parchear los problemas de re-rendereo con firefox y las imagenes del captcha via ajax
 * Este parche tambien arregla el problema detectado de firefox11 en adelante con richfaces 3.3.3.Final: RF-12062
 * Fecha de creación: 03/05/2012 <br>
 * Ultima actualización: <br>
 * Company: Antel <br>
 *
 * @author Sebastian Negreira<br>
 * @version 1.0
 */

public class RichfacesFirefoxFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */	
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public String getRequestURI() {
                try {
                    return URLDecoder.decode(super.getRequestURI(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Cannot decode request URI.", e);
                }
            }
        }, response);
    }

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
