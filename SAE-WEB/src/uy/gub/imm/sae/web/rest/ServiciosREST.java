package uy.gub.imm.sae.web.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.common.factories.BusinessLocator;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;

@Path("/consultas")
public class ServiciosREST {

	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm"); 	
	
	@POST
  @Path("/reservas-por-agenda-y-documento")
  @Consumes("application/json")
  @Produces("application/json")
	public String getReservasDocumento(ReservasPorDocumentoInput input) {
		try {
			
			BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
			Consultas consultas = bl.getConsultas();
			
			List<Date> fechas = consultas.consultarReservasPorTokenYDocumento(input.getToken(), input.getIdAgenda(), 
					input.getIdRecurso(), input.getTipoDocumento(), input.getNumeroDocumento());
			
			StringBuilder resp = new StringBuilder("[");
			for(Date fecha : fechas) {
				if(resp.length()>1) {
					resp.append(",");
				}
				resp.append("{'fecha_agenda': '"+sdf.format(fecha)+"'}");
			}
			resp.append("]");
			
			return resp.toString();
		}catch(Exception ex) {
			return ex.getMessage();
		}
  }	
	
}
