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

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

public class ErrorPageRedirectPhaseListener implements javax.faces.event.PhaseListener {

	private static final long serialVersionUID = 1L;

	public ErrorPageRedirectPhaseListener() {
	}

	public void afterPhase(PhaseEvent event) {

		event.getFacesContext().getApplication().getNavigationHandler().handleNavigation(event.getFacesContext(), null, "/");
		event.getFacesContext().responseComplete();
	}

	public void beforePhase(PhaseEvent event) {

	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
