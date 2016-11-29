package uy.gub.imm.sae.exportar;

import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.ValorPosible;

public class ExportarHelper {

	public static RecursoExportar exportarRecurso(Recurso r) {

		if (r == null) {
			return null;
		}

		RecursoExportar re = new RecursoExportar();

		re.setNombre(r.getNombre());
		re.setDescripcion(r.getDescripcion());
		re.setFechaInicio(r.getFechaInicio());
		re.setFechaFin(r.getFechaFin());
		re.setFechaInicioDisp(r.getFechaInicioDisp());
		re.setFechaFinDisp(r.getFechaFinDisp());
		re.setDiasInicioVentanaIntranet(r.getDiasInicioVentanaIntranet());
		re.setDiasVentanaIntranet(r.getDiasVentanaIntranet());
		re.setDiasInicioVentanaInternet(r.getDiasInicioVentanaInternet());
		re.setDiasVentanaInternet(r.getDiasVentanaInternet());
		re.setVentanaCuposMinimos(r.getVentanaCuposMinimos());
		re.setCantDiasAGenerar(r.getCantDiasAGenerar());
		re.setLargoListaEspera(r.getLargoListaEspera());
		re.setVersion(r.getVersion());
		re.setFechaBaja(r.getFechaBaja());
		re.setMostrarNumeroEnLlamador(r.getMostrarNumeroEnLlamador());
		re.setVisibleInternet(r.getVisibleInternet());
		re.setMostrarNumeroEnTicket(r.getMostrarNumeroEnTicket());
		re.setMostrarIdEnTicket(r.getMostrarIdEnTicket());
		re.setUsarLlamador(r.getUsarLlamador());
		re.setSerie(r.getSerie());
		re.setSabadoEsHabil(r.getSabadoEsHabil());
    re.setDomingoEsHabil(r.getDomingoEsHabil());
		re.setOficinaId(r.getOficinaId());
		re.setDireccion(r.getDireccion());
		re.setLocalidad(r.getLocalidad());
		re.setDepartamento(r.getDepartamento());
		re.setTelefonos(r.getTelefonos());
		re.setHorarios(r.getHorarios());
		re.setLatitud(r.getLatitud());
		re.setLongitud(r.getLongitud());

		for (AgrupacionDato agrupacionDato : r.getAgrupacionDatos()) {
			if (agrupacionDato.getFechaBaja() == null) {
				AgrupacionDatoExport age = exportarAgrupacionDato(agrupacionDato);
				re.getAgrupaciones().add(age);
			}
		}
		
		for (String idioma : r.getTextosRecurso().keySet()) {
			TextoRecursoExportar tre = exportarTextoRecurso(r.getTextosRecurso().get(idioma));
			re.getTextosRecurso().put(idioma, tre);
		}

		return re;
	}

	public static AgrupacionDatoExport exportarAgrupacionDato(AgrupacionDato ag) {

		if (ag == null) {
			return null;
		}

		AgrupacionDatoExport age = new AgrupacionDatoExport();

		age.setNombre(ag.getNombre());
		age.setOrden(ag.getOrden());
		age.setFechaBaja(ag.getFechaBaja());
		age.setBorrarFlag(ag.getBorrarFlag());
		for (DatoASolicitar datoASolicitar : ag.getDatosASolicitar()) {
			if (datoASolicitar.getFechaBaja() == null) {
				DatoASolicitarExportar dsexp = new DatoASolicitarExportar();
				dsexp.setAnchoDespliegue(datoASolicitar.getAnchoDespliegue());
				dsexp.setColumna(datoASolicitar.getColumna());
				dsexp.setEsClave(datoASolicitar.getEsClave());
				dsexp.setEtiqueta(datoASolicitar.getEtiqueta());
				dsexp.setFechaBaja(datoASolicitar.getFechaBaja());
				dsexp.setFila(datoASolicitar.getFila());
				dsexp.setIncluirEnReporte(datoASolicitar.getIncluirEnReporte());
				dsexp.setLargo(datoASolicitar.getLargo());
				dsexp.setNombre(datoASolicitar.getNombre());
				dsexp.setRequerido(datoASolicitar.getRequerido());
				dsexp.setTextoAyuda(datoASolicitar.getTextoAyuda());
				dsexp.setTipo(datoASolicitar.getTipo());
				dsexp.setBorrarFlag(datoASolicitar.getBorrarFlag());
				dsexp.setIncluirEnLlamador(datoASolicitar.getIncluirEnLlamador());
				dsexp.setOrdenEnLlamador(datoASolicitar.getOrdenEnLlamador());
				dsexp.setLargoEnLlamador(datoASolicitar.getLargoEnLlamador());
				for (ValorPosible vp : datoASolicitar.getValoresPosibles()) {

					ValorPosibleExport vpexp = exportarValorPosible(vp);
					dsexp.getValoresPosibles().add(vpexp);
				}

				age.getDatosAsolicitar().add(dsexp);

			}
		}

		return age;
	}

	public static ValorPosibleExport exportarValorPosible(ValorPosible vp) {

		ValorPosibleExport vpexp = new ValorPosibleExport();
		vpexp.setBorrarFlag(vp.getBorrarFlag());
		vpexp.setEtiqueta(vp.getEtiqueta());
		vpexp.setFechaDesde(vp.getFechaDesde());
		vpexp.setFechaHasta(vp.getFechaHasta());
		vpexp.setOrden(vp.getOrden());
		vpexp.setValor(vp.getValor());

		return vpexp;
	}

	public static TextoRecursoExportar exportarTextoRecurso(TextoRecurso tr) {
		if(tr==null) {
			return null;
		}
		TextoRecursoExportar textoRecurso = new TextoRecursoExportar();
		textoRecurso.setIdioma(tr.getIdioma());
		textoRecurso.setTextoPaso2(tr.getTextoPaso2());
		textoRecurso.setTextoPaso3(tr.getTextoPaso3());
		return textoRecurso;
	}
	
	public static Recurso importarRecurso(RecursoExportar re) {

		if (re == null) {
			return null;
		}

		Recurso r = new Recurso();

		r.setNombre(re.getNombre());
		r.setDescripcion(re.getDescripcion());
		r.setFechaInicio(re.getFechaInicio());
		r.setFechaFin(re.getFechaFin());
		r.setFechaInicioDisp(re.getFechaInicioDisp());
		r.setFechaFinDisp(re.getFechaFinDisp());
		r.setDiasInicioVentanaIntranet(re.getDiasInicioVentanaIntranet());
		r.setDiasVentanaIntranet(re.getDiasVentanaIntranet());
		r.setDiasInicioVentanaInternet(re.getDiasInicioVentanaInternet());
		r.setDiasVentanaInternet(re.getDiasVentanaInternet());
		r.setVentanaCuposMinimos(re.getVentanaCuposMinimos());
		r.setCantDiasAGenerar(re.getCantDiasAGenerar());
		r.setLargoListaEspera(re.getLargoListaEspera());
		r.setFechaBaja(re.getFechaBaja());
		r.setMostrarNumeroEnLlamador(re.getMostrarNumeroEnLlamador());
		r.setVisibleInternet(re.getVisibleInternet());
		r.setMostrarNumeroEnTicket(re.getMostrarNumeroEnTicket());
		r.setMostrarIdEnTicket(re.getMostrarIdEnTicket());
		r.setUsarLlamador(re.getUsarLlamador());
		r.setSerie(re.getSerie());
		r.setSabadoEsHabil(re.getSabadoEsHabil());
    r.setDomingoEsHabil(re.getDomingoEsHabil());
		r.setOficinaId(re.getOficinaId());
		r.setDireccion(re.getDireccion());
		r.setLocalidad(re.getLocalidad());
		r.setDepartamento(re.getDepartamento());
		r.setTelefonos(re.getTelefonos());
		r.setHorarios(re.getHorarios());
		r.setLatitud(re.getLatitud());
		r.setLongitud(re.getLongitud());

		return r;
	}

	public static AgrupacionDato importarAgrupacionDato(
			AgrupacionDatoExport agexp) {

		if (agexp == null) {
			return null;
		}

		AgrupacionDato agd = new AgrupacionDato();

		agd.setBorrarFlag(agexp.getBorrarFlag());
		agd.setFechaBaja(agexp.getFechaBaja());
		agd.setNombre(agexp.getNombre());
		agd.setOrden(agexp.getOrden());

		return agd;
	}

	public static ValorPosible importarValorPosible(ValorPosibleExport vpexp) {

		if (vpexp == null) {
			return null;
		}

		ValorPosible vp = new ValorPosible();

		vp.setBorrarFlag(vpexp.getBorrarFlag());
		vp.setEtiqueta(vpexp.getEtiqueta());
		vp.setFechaDesde(vpexp.getFechaDesde());
		vp.setFechaHasta(vpexp.getFechaHasta());
		vp.setOrden(vpexp.getOrden());
		vp.setValor(vpexp.getValor());

		return vp;
	}

	public static DatoASolicitar importarDatoASolicitar(
			DatoASolicitarExportar dasExp) {

		if (dasExp == null) {
			return null;
		}
		DatoASolicitar das = new DatoASolicitar();
		das.setAnchoDespliegue(dasExp.getAnchoDespliegue());
		das.setBorrarFlag(dasExp.getBorrarFlag());
		das.setColumna(dasExp.getColumna());
		das.setEsClave(dasExp.getEsClave());
		das.setEtiqueta(dasExp.getEtiqueta());
		das.setFechaBaja(dasExp.getFechaBaja());
		das.setFila(dasExp.getFila());
		das.setIncluirEnLlamador(dasExp.getIncluirEnLlamador());
		das.setIncluirEnReporte(dasExp.getIncluirEnReporte());
		das.setLargo(dasExp.getLargo());
		das.setLargoEnLlamador(dasExp.getLargoEnLlamador());
		das.setNombre(dasExp.getNombre());
		das.setOrdenEnLlamador(dasExp.getOrdenEnLlamador());
		das.setRequerido(dasExp.getRequerido());
		das.setTextoAyuda(dasExp.getTextoAyuda());
		das.setTipo(dasExp.getTipo());

		return das;
	}
	
	public static TextoRecurso importarTextoRecurso(TextoRecursoExportar tre) {
		if(tre==null) {
			return null;
		}
		TextoRecurso textoRecurso = new TextoRecurso();
		textoRecurso.setIdioma(tre.getIdioma());
		textoRecurso.setTextoPaso2(tre.getTextoPaso2());
		textoRecurso.setTextoPaso3(tre.getTextoPaso3());
		return textoRecurso;
	}
	
	
}
