package com.sofis.agesic.ursea;

import com.sofis.agesic.ursea.client.Reserva;
import com.sofis.agesic.ursea.client.Service;
import com.sofis.agesic.ursea.client.ServiceSoap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.ws.BindingProvider;
import org.json.JSONArray;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.sae.acciones.business.dto.RecursoDTO;
import uy.gub.sae.acciones.business.ejb.EjecutorAccionRemote;
import uy.gub.sae.acciones.business.ejb.ResultadoAccion;
import uy.gub.sae.acciones.business.ejb.exception.InvalidParametersException;
import uy.gub.sae.acciones.business.ejb.exception.UnexpectedAccionException;

@Stateless
@LocalBean
public class ProteoClientBean implements Serializable, EjecutorAccionRemote {

    private static final Logger LOGGER = Logger.getLogger(ProteoClientBean.class.getName());
    private static final SimpleDateFormat SDF_DIA = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SDF_DIA_CONSULTA = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_HORA = new SimpleDateFormat("HH:mm");
    private static final Properties CONFIG = new Properties();
    private static final Properties CONF = new Properties();

    static {
        try {
            String ruta = System.getProperty("jboss.server.base.dir") + "/configuration/conf.properties";
            Path path_conf = FileSystems.getDefault().getPath(ruta);
            CONFIG.load(new BufferedReader(new FileReader(path_conf.toFile())));

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ResultadoAccion ejecutar(String nombreAccion, Map<String, Object> params) throws UnexpectedAccionException, InvalidParametersException {
        ResultadoAccion res = new ResultadoAccion();
        String rut = System.getProperty("jboss.server.base.dir") + "/configuration/id_oficinas_reservas.properties";

        Path path = FileSystems.getDefault().getPath(rut);
        try {
            CONF.load(new BufferedReader(new FileReader(path.toFile())));
        } catch (FileNotFoundException ex) {
            res.addError("0", "No se pudo cargar el archivo de los ID de Recursos. No se encuentra el archivo.");
            Logger.getLogger(ProteoClientBean.class.getName()).log(Level.SEVERE, "No se pudo cargar el archivo de los ID de Recursos", ex.getMessage());
            return res;
        } catch (IOException ex) {
            res.addError("0", "No se pudo cargar el archivo de los ID de Recursos");
            Logger.getLogger(ProteoClientBean.class.getName()).log(Level.SEVERE, "No se pudo cargar el archivo de los ID de Recursos", ex.getMessage());
            return res;
        }
        ReservaDTO reservaDTO = (ReservaDTO) params.get("RESERVA");
        RecursoDTO recursoDTO = (RecursoDTO) params.get("RECURSO");
        InitialContext context;

        String invId = UUID.randomUUID().toString();
        int recursoId = -1;
        try {
            context = new InitialContext();
            Object obj = context.lookup("java:/postgres-sae-ds");
            DataSource dataSource = (javax.sql.DataSource) obj;
            String id_recursos = CONF.getProperty("ID_OFICINAS_RESERVA");
            JSONArray x = new JSONArray(id_recursos);
            //LOGGER.log(Level.INFO, String.format("Buscando ID de la Oficina: [%s]", recursoDTO.getDescripcion()));
            for (int i = 0; i < x.length(); i++) {
                String nombre = x.getJSONObject(i).get("nombre").toString();
                String id = String.valueOf(x.getJSONObject(i).get("id"));
                //LOGGER.log(Level.INFO, String.format("Nombre Oficina: [%s]", nombre));
                //LOGGER.log(Level.INFO, String.format("ID Oficina: [%s]", id));
                if (nombre.equals(recursoDTO.getDescripcion())) {
                    recursoId = Integer.parseInt(id);
                    break;
                }
            }

            if (recursoId == -1) {
                res.addError("0", "El Recurso no exite");
                LOGGER.log(Level.SEVERE, "Error obteniendo el ID de Recurso");
                return res;
            }

            try (Connection con = dataSource.getConnection()) {
                Statement st = con.createStatement();
                Statement st1 = con.createStatement();
                Statement st2 = con.createStatement();

                ResultSet rs = st.executeQuery("select * from global.ae_empresas");
                while (rs.next()) {
                    String sq = rs.getString("datasource");
                    ResultSet rs1 = st1.executeQuery("select 1 from " + sq + ".ae_recursos where id =" + recursoDTO.getId() + " and nombre='" + recursoDTO.getNombre() + "'");
                    if (rs1.next()) {
                        String query = "select tramite_codigo from " + sq + ".ae_reservas where id =" + reservaDTO.getId() + " and TO_DATE(to_char(fcrea,'YYYY-MM-DD'),'YYYY-MM-DD') = to_timestamp('" + SDF_DIA_CONSULTA.format(new Date()) + "', 'YYYY-MM-DD')";

                        ResultSet rs2 = st2.executeQuery(query);
                        if (rs2.next()) {
                            reservaDTO.setTramiteCodigo(rs2.getString("tramite_codigo"));
                        }
                        rs2.close();
                        st2.close();
                    }
                    rs1.close();
                    st1.close();
                }
                rs.close();
                st.close();
                con.close();
            }

        } catch (SQLException | NamingException ex) {
            res.addError("0", ex.getMessage());
            LOGGER.log(Level.SEVERE, String.format("[%s] Error obteniendo el Tipo de Tr\u00e1mite: %s", new Object[]{invId, ex.getMessage()}), ex);
            return res;
        }

        if ("PROTEO".equals(nombreAccion) && recursoId != -1) {

            try {
                String tramite = reservaDTO.getId() + "-" + recursoId;
                Integer idOficina = recursoId;
                String fecha = SDF_DIA.format(reservaDTO.getFecha());
                String turnoHora = SDF_HORA.format(reservaDTO.getHoraInicio());
                String serie = (String) params.get("series");
                String numeroCredencial = (String) params.get("numeroCredencial");
                Date fechaNacimiento = (Date) params.get("fechaNacimiento");
                String nombrePadre = (String) params.get("nombrePadre");
                String nombreMadre = (String) params.get("nombreMadre");
                String tipoTramite = reservaDTO.getTramiteCodigo();
                String primerApellido = (String) params.get("primerApellido");
                String segundoApellido = (String) params.get("segundoApellido");
                String primerNombre = (String) params.get("primerNombre");
                String segundoNombre = (String) params.get("segundoNombre");
                String pruebaCiudadania = (String) params.get("pruebaCiudadania");
                String cedula = (String) params.get("cedula");

                //fix strings
                fecha = fecha != null ? fecha.trim() : null;
                turnoHora = turnoHora != null ? turnoHora.trim() : null;
                serie = serie != null ? serie.trim() : "";
                numeroCredencial = numeroCredencial != null ? numeroCredencial.trim() : "";
                nombrePadre = nombrePadre != null ? nombrePadre.trim() : "";
                nombreMadre = nombreMadre != null ? nombreMadre.trim() : "";
                primerApellido = primerApellido != null ? primerApellido.trim() : "";
                segundoApellido = segundoApellido != null ? segundoApellido.trim() : "";
                primerNombre = primerNombre != null ? primerNombre.trim() : "";
                segundoNombre = segundoNombre != null ? segundoNombre.trim() : "";
                pruebaCiudadania = pruebaCiudadania != null ? pruebaCiudadania.trim() : "";
                String fechaNacimientoStr = fechaNacimiento != null ? SDF_DIA.format(fechaNacimiento) : "";
                cedula = cedula != null ? cedula.trim() : "";

//		Service service = new Service(new URL(config.getProperty("GRABAR_RESERVA_SERVICIO_WSDL")), new QName("http://tempuri.org/", "Service"));
                Service service = new Service();
                ServiceSoap port = service.getServiceSoap();

                String urlServicio = CONFIG.getProperty("GRABAR_RESERVA_SERVICIO");

                BindingProvider bindingProvider = (BindingProvider) port;
                bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlServicio);

                LOGGER.log(Level.INFO, "[{0}][{1}] Grabar reserva: idOficina={2},"
                        + " fecha={3},"
                        + " turnoHora={4},"
                        + " serie={5},"
                        + " numeroCredencial={6},"
                        + " fechaNacimiento={7},"
                        + " nombreMadre={8},"
                        + " nombrePadre={9},"
                        + " tipoTramite={10},"
                        + " primerApellido={11},"
                        + " segundoApellido={12},"
                        + " primerNombre={13},"
                        + " segundoNombre={14},"
                        + " pruebaCiudadania={15},"
                        + " cedula={16},"
                        + " ", new Object[]{invId, tramite, idOficina, fecha, turnoHora, serie,
                            numeroCredencial, fechaNacimientoStr, nombreMadre, nombrePadre, tipoTramite,
                            primerApellido, segundoApellido, primerNombre, segundoNombre,
                            pruebaCiudadania, cedula});

                Reserva reserva = port.grabarReservaWeb(idOficina, fecha, turnoHora,
                        serie, numeroCredencial, fechaNacimientoStr, nombrePadre,
                        nombreMadre, tipoTramite, primerApellido, segundoApellido,
                        primerNombre, segundoNombre, pruebaCiudadania, cedula);
                if (reserva.getMsgError() != null) {
                    res.addError("0", reserva.getMsgError());
                    LOGGER.log(Level.SEVERE, "[{0}][{1}] Error recibido como respuesta: {2}", new Object[]{invId, tramite, reserva.getMsgError()});
                }
                return res;

            } catch (Exception ex) {
                res.addError("0", ex.getMessage());
                LOGGER.log(Level.SEVERE, String.format("[%s] Error al invocar el servicio: %s", new Object[]{invId, ex.getMessage()}), ex);
            }
        } else {
            LOGGER.log(Level.SEVERE, "[{0}] Nombre de acci\u00f3n inv\u00e1lido: {1}", new Object[]{invId, nombreAccion});
        }

        return res;
    }

}
