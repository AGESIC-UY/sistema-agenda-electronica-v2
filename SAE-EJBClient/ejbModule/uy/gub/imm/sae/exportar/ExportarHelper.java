package uy.gub.imm.sae.exportar;

import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.ValorPosible;

public class ExportarHelper {

	public static RecursoExportar exportarRecurso(Recurso recurso, String versionSAE) {

		if (recurso == null) {
			return null;
		}

		RecursoExportar recursoExportar = new RecursoExportar();

		recursoExportar.setVersionSAE(versionSAE);
		
		recursoExportar.setNombre(recurso.getNombre());
		recursoExportar.setDescripcion(recurso.getDescripcion());
		recursoExportar.setFechaInicio(recurso.getFechaInicio());
		recursoExportar.setFechaFin(recurso.getFechaFin());
		recursoExportar.setFechaInicioDisp(recurso.getFechaInicioDisp());
		recursoExportar.setFechaFinDisp(recurso.getFechaFinDisp());
		recursoExportar.setDiasInicioVentanaIntranet(recurso.getDiasInicioVentanaIntranet());
		recursoExportar.setDiasVentanaIntranet(recurso.getDiasVentanaIntranet());
		recursoExportar.setDiasInicioVentanaInternet(recurso.getDiasInicioVentanaInternet());
		recursoExportar.setDiasVentanaInternet(recurso.getDiasVentanaInternet());
		recursoExportar.setVentanaCuposMinimos(recurso.getVentanaCuposMinimos());
		recursoExportar.setCantDiasAGenerar(recurso.getCantDiasAGenerar());
		recursoExportar.setLargoListaEspera(recurso.getLargoListaEspera());
		recursoExportar.setVersion(recurso.getVersion());
		recursoExportar.setFechaBaja(recurso.getFechaBaja());
		recursoExportar.setMostrarNumeroEnLlamador(recurso.getMostrarNumeroEnLlamador());
		recursoExportar.setVisibleInternet(recurso.getVisibleInternet());
    recursoExportar.setMostrarIdEnTicket(recurso.getMostrarIdEnTicket());
		recursoExportar.setMostrarNumeroEnTicket(recurso.getMostrarNumeroEnTicket());
		recursoExportar.setFuenteTicket(recurso.getFuenteTicket());
		recursoExportar.setTamanioFuenteChica(recurso.getTamanioFuenteChica());
		recursoExportar.setTamanioFuenteNormal(recurso.getTamanioFuenteNormal());
		recursoExportar.setTamanioFuenteGrande(recurso.getTamanioFuenteGrande());
		recursoExportar.setUsarLlamador(recurso.getUsarLlamador());
		recursoExportar.setSerie(recurso.getSerie());
		recursoExportar.setSabadoEsHabil(recurso.getSabadoEsHabil());
    recursoExportar.setDomingoEsHabil(recurso.getDomingoEsHabil());
		recursoExportar.setOficinaId(recurso.getOficinaId());
		recursoExportar.setDireccion(recurso.getDireccion());
		recursoExportar.setLocalidad(recurso.getLocalidad());
		recursoExportar.setDepartamento(recurso.getDepartamento());
		recursoExportar.setTelefonos(recurso.getTelefonos());
		recursoExportar.setHorarios(recurso.getHorarios());
		recursoExportar.setLatitud(recurso.getLatitud());
		recursoExportar.setLongitud(recurso.getLongitud());
		
		recursoExportar.setPresencialAdmite(recurso.getPresencialAdmite());
    recursoExportar.setPresencialCupos(recurso.getPresencialCupos());
    recursoExportar.setPresencialLunes(recurso.getPresencialLunes());
    recursoExportar.setPresencialMartes(recurso.getPresencialMartes());
    recursoExportar.setPresencialMiercoles(recurso.getPresencialMiercoles());
    recursoExportar.setPresencialJueves(recurso.getPresencialJueves());
    recursoExportar.setPresencialViernes(recurso.getPresencialViernes());
    recursoExportar.setPresencialSabado(recurso.getPresencialSabado());
    recursoExportar.setPresencialDomingo(recurso.getPresencialDomingo());

    recursoExportar.setMultipleAdmite(recurso.getMultipleAdmite());
    recursoExportar.setCambiosAdmite(recurso.getCambiosAdmite());
    recursoExportar.setCambiosTiempo(recurso.getCambiosTiempo());
    recursoExportar.setCambiosUnidad(recurso.getCambiosUnidad());
    
		for (AgrupacionDato agrupacionDato : recurso.getAgrupacionDatos()) {
			if (agrupacionDato.getFechaBaja() == null) {
				AgrupacionDatoExport age = exportarAgrupacionDato(agrupacionDato);
				recursoExportar.getAgrupaciones().add(age);
			}
		}
		
		for (String idioma : recurso.getTextosRecurso().keySet()) {
			TextoRecursoExportar tre = exportarTextoRecurso(recurso.getTextosRecurso().get(idioma));
			recursoExportar.getTextosRecurso().put(idioma, tre);
		}

		return recursoExportar;
	}

	public static AgrupacionDatoExport exportarAgrupacionDato(AgrupacionDato ag) {

		if (ag == null) {
			return null;
		}

		AgrupacionDatoExport age = new AgrupacionDatoExport();

		age.setNombre(ag.getNombre());
		age.setEtiqueta(ag.getEtiqueta());
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
        dsexp.setSoloLectura(datoASolicitar.getSoloLectura());
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
	
	public static Recurso importarRecurso(RecursoExportar recursoExportar, String versionSAE) {

		if (recursoExportar == null) {
			return null;
		}

		Recurso recurso = new Recurso();

		recurso.setNombre(recursoExportar.getNombre());
		recurso.setDescripcion(recursoExportar.getDescripcion());
		recurso.setFechaInicio(recursoExportar.getFechaInicio());
		recurso.setFechaFin(recursoExportar.getFechaFin());
		recurso.setFechaInicioDisp(recursoExportar.getFechaInicioDisp());
		recurso.setFechaFinDisp(recursoExportar.getFechaFinDisp());
		recurso.setDiasInicioVentanaIntranet(recursoExportar.getDiasInicioVentanaIntranet());
		recurso.setDiasVentanaIntranet(recursoExportar.getDiasVentanaIntranet());
		recurso.setDiasInicioVentanaInternet(recursoExportar.getDiasInicioVentanaInternet());
		recurso.setDiasVentanaInternet(recursoExportar.getDiasVentanaInternet());
		recurso.setVentanaCuposMinimos(recursoExportar.getVentanaCuposMinimos());
		recurso.setCantDiasAGenerar(recursoExportar.getCantDiasAGenerar());
		recurso.setLargoListaEspera(recursoExportar.getLargoListaEspera());
		recurso.setFechaBaja(recursoExportar.getFechaBaja());
		recurso.setMostrarNumeroEnLlamador(recursoExportar.getMostrarNumeroEnLlamador());
		recurso.setVisibleInternet(recursoExportar.getVisibleInternet());
    recurso.setMostrarIdEnTicket(recursoExportar.getMostrarIdEnTicket());
		recurso.setMostrarNumeroEnTicket(recursoExportar.getMostrarNumeroEnTicket());
		recurso.setFuenteTicket(recursoExportar.getFuenteTicket());
		recurso.setTamanioFuenteChica(recursoExportar.getTamanioFuenteChica());
		recurso.setTamanioFuenteNormal(recursoExportar.getTamanioFuenteNormal());
		recurso.setTamanioFuenteGrande(recursoExportar.getTamanioFuenteGrande());
		recurso.setUsarLlamador(recursoExportar.getUsarLlamador());
		recurso.setSerie(recursoExportar.getSerie());
		recurso.setSabadoEsHabil(recursoExportar.getSabadoEsHabil());
    recurso.setDomingoEsHabil(recursoExportar.getDomingoEsHabil());
		recurso.setOficinaId(recursoExportar.getOficinaId());
		recurso.setDireccion(recursoExportar.getDireccion());
		recurso.setLocalidad(recursoExportar.getLocalidad());
		recurso.setDepartamento(recursoExportar.getDepartamento());
		recurso.setTelefonos(recursoExportar.getTelefonos());
		recurso.setHorarios(recursoExportar.getHorarios());
		recurso.setLatitud(recursoExportar.getLatitud());
		recurso.setLongitud(recursoExportar.getLongitud());
		recurso.setPresencialAdmite(recursoExportar.getPresencialAdmite());
		recurso.setPresencialCupos(recursoExportar.getPresencialCupos());
		recurso.setPresencialLunes(recursoExportar.getPresencialLunes());
		recurso.setPresencialMartes(recursoExportar.getPresencialMartes());
		recurso.setPresencialMiercoles(recursoExportar.getPresencialMiercoles());
		recurso.setPresencialJueves(recursoExportar.getPresencialJueves());
		recurso.setPresencialViernes(recursoExportar.getPresencialViernes());
		recurso.setPresencialSabado(recursoExportar.getPresencialSabado());
		recurso.setPresencialDomingo(recursoExportar.getPresencialDomingo());
    recurso.setMultipleAdmite(recursoExportar.getMultipleAdmite());
    recurso.setCambiosAdmite(recursoExportar.getCambiosAdmite());
    recurso.setCambiosTiempo(recursoExportar.getCambiosTiempo());
    recurso.setCambiosUnidad(recursoExportar.getCambiosUnidad());

		return recurso;
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
    agd.setEtiqueta(agexp.getEtiqueta());
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
    das.setSoloLectura(dasExp.getSoloLectura());
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
