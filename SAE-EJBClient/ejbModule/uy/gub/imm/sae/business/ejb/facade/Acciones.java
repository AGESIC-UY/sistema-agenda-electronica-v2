package uy.gub.imm.sae.business.ejb.facade;

import java.util.List;

import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ParametroAccion;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Accion;
import uy.gub.imm.sae.entity.AccionPorDato;
import uy.gub.imm.sae.entity.AccionPorRecurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

public interface Acciones {
	
	public Accion crearAccion(Accion a)throws UserException, BusinessException;
	public Accion modificarAccion(Accion a)throws UserException, BusinessException;
	public void eliminarAccion(Accion a)throws UserException, BusinessException;
	public List<Accion> consultarAcciones()throws ApplicationException;
	public Boolean existeAccionPorNombre(String nombreAccion) throws ApplicationException;
	public List<ParametroAccion> consultarParametrosDeLaAccion(Accion a) throws ApplicationException;
	
	//Metodos para manejar la asociacion de acciones a recursos.
	public List<AccionPorRecurso> obtenerAccionesDelRecurso(Recurso recurso) throws ApplicationException;
	public List<AccionPorDato> obtenerAsociacionesAccionPorDato(AccionPorRecurso ar) throws ApplicationException;
	public AccionPorDato asociarAccionPorDato(DatoASolicitar d, AccionPorRecurso ar, AccionPorDato ad) throws UserException, ApplicationException;
	public void desasociarAccionPorDato(AccionPorDato ad) throws UserException;
	public AccionPorRecurso modificarAccionPorRecurso(AccionPorRecurso ar) throws UserException, BusinessException;
	public AccionPorRecurso crearAccionPorRecurso(AccionPorRecurso ar) throws BusinessException, UserException;
	public void eliminarAccionPorRecurso(AccionPorRecurso vr) throws BusinessException;
	
}