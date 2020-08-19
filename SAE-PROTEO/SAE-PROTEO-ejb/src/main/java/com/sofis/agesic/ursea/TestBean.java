///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.sofis.agesic.ursea;
//
//import java.io.Serializable;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.annotation.PostConstruct;
//import javax.ejb.Singleton;
//import javax.ejb.LocalBean;
//import javax.ejb.Startup;
//import javax.inject.Inject;
//import uy.gub.imm.sae.business.dto.ReservaDTO;
//import uy.gub.sae.acciones.business.dto.RecursoDTO;
//import uy.gub.sae.acciones.business.ejb.ResultadoAccion;
//import uy.gub.sae.acciones.business.ejb.exception.InvalidParametersException;
//import uy.gub.sae.acciones.business.ejb.exception.UnexpectedAccionException;
//
///**
// *
// * @author bruno
// */
//@Singleton
//@Startup
//@LocalBean
//public class TestBean implements Serializable {
//
//	private static final SimpleDateFormat SDF_DIA = new SimpleDateFormat("dd/MM/yyyy");
//	private static final SimpleDateFormat SDF_HORA = new SimpleDateFormat("HH:mm");
//	private static final Logger LOGGER = Logger.getLogger(ProteoClientBean.class.getName());
//
//	@Inject
//	private ProteoClientBean bean;
//
//	@PostConstruct
//	public void init() {
//
//		try {
//
//			ReservaDTO reservaDTO = new ReservaDTO();
//			reservaDTO.setFecha(SDF_DIA.parse("19/02/2018"));
//			reservaDTO.setHoraInicio(SDF_HORA.parse("09:00"));
//			reservaDTO.setId(1);
//
//			RecursoDTO recursoDTO = new RecursoDTO();
//			recursoDTO.setAgendaDescripcion("1");
//			recursoDTO.setDescripcion("1");
//			recursoDTO.setId(1);
//
//			Map<String, Object> params = new HashMap<>();
//			params.put("cedula", "12121212");
//			params.put("serie", "NAD");
//			params.put("numeroCredencial", "123");
//			params.put("fechaNacimiento", "01/01/1991");
//			params.put("primerApellido", "AA");
//			params.put("segundoApellido", "BB");
//			params.put("primerNombre", "CC");
//			params.put("segundoNombre", "DD");
//			params.put("nombrePadre", "EE");
//			params.put("nombrePadre", "FF");
//			params.put("pruebaCiudadania", "GG");
//			params.put("tipoTramite", "INS");
//			params.put("RESERVA", reservaDTO);
//			params.put("RECURSO", recursoDTO);
//
//			ResultadoAccion res = bean.ejecutar("GRABAR_RESERVA", params);
//			
//
//		} catch (ParseException ex) {
//			LOGGER.log(Level.SEVERE, null, ex);
//		} catch (UnexpectedAccionException ex) {
//			LOGGER.log(Level.SEVERE, null, ex);
//		} catch (InvalidParametersException ex) {
//			LOGGER.log(Level.SEVERE, null, ex);
//		}
//
//	}
//
//}
