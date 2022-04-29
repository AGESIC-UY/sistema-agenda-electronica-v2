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

package uy.gub.imm.sae.business.ejb.facade;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.servicios.ServiciosMiPerfilBean;
import uy.gub.imm.sae.business.utilidades.MailUtiles;
import uy.gub.imm.sae.business.utilidades.Metavariables;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Comunicacion;
import uy.gub.imm.sae.entity.Comunicacion.Tipo1;
import uy.gub.imm.sae.entity.Comunicacion.Tipo2;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.UserException;

/**
 * Estos metodos son usados para enviar las comunicaciones cada vez que un
 * usuario reserva o cancela una reservación. Por el momento hay cuatro tipos de
 * comunicaciones: por mail, por sms, por texto a voz y por mi perfil. Para
 * enviar un mail, es necesario que exista un campo llamado "Mail" dentro de la
 * agrupacion "DatosPersonales". El texto del mail se obtiene incorporando los
 * datos de la reserva en el texto definido para el envío de mail en la Agenda.
 * En el caso de SMS y TAV en realidad no se envía nada sino que se escribe en
 * una tabla llamada ae_comunicaciones a la espera de que un proceso externo los
 * tome y marque como procesadas. Para poder enviar un SMS es necesario que
 * exista un campo llamado "TelefonoMovil" dentro de la agrupacion
 * "DatosPersonales". Para poder enviar un TAV es necesario que exista un campo
 * llamado "TelefonoFijo" dentro de la agrupacion "DatosPersonales". En el caso
 * de Mi Perfil, se debe tener el número de documento del ciudadano.
 */
@Stateless
public class ComunicacionesBean implements ComunicacionesRemote {

    static Logger logger = Logger.getLogger(ComunicacionesBean.class);

    @PersistenceContext(unitName = "AGENDA-GLOBAL")
    private EntityManager globalEntityManager;

    @PersistenceContext(unitName = "SAE-EJB")
    private EntityManager entityManager;

    @EJB
    private ServiciosMiPerfilBean miPerfilBean;

    @Resource
    private SessionContext ctx;

    public void enviarComunicacionesConfirmacion(Empresa empresa, String linkCancelacion, String linkModificacion, Reserva reserva, String idioma,
                    String formatoFecha, String formatoHora) throws UserException {
        // Enviar mail
        String cuerpo = enviarMailConfirmacion(linkCancelacion, linkModificacion, reserva, idioma, formatoFecha, formatoHora);
        boolean mailOk = (cuerpo != null);
        // Almacenar datos para SMS y TextoAVoz, si corresponde
        almacenarSmsYTav(Comunicacion.Tipo2.RESERVA, reserva, formatoFecha, formatoHora);
        // Enviar aviso a MiPerfil
        boolean mipOk = enviarAvisoMiPerfil(empresa, reserva, Comunicacion.Tipo2.RESERVA);
        //Si no pudo enviar mail ni aviso de miperfil entonces lanzar una excepción
        if (!mailOk && !mipOk) {
            throw new UserException("no_se_pudo_enviar_notificacion_de_confirmacion_tome_nota_de_los_datos_de_la_reserva");
        }
    }

    //Este método es similar a enviarComunicacionesConfirmacion pero aplica a un TokenReserva que contenga múltiples reservas en lugar de a una sola.
    public void enviarComunicacionesConfirmacion(Empresa empresa, String templateLinkCancelacion, String templateLinkModificacion, TokenReserva tokenReserva,
                    String idioma, String formatoFecha, String formatoHora) throws UserException {
        // Obtener las reservas asociadas al token
        String eql = "SELECT r FROM Reserva r WHERE r.token.id=:tokenId AND r.datosReserva IS NOT EMPTY ORDER BY r.id";
        @SuppressWarnings("unchecked")
        List<Reserva> reservas = (List<Reserva>) entityManager.createQuery(eql).setParameter("tokenId", tokenReserva.getId()).getResultList();
        // Procesar cada reserva e ir conformando un cuerpo de texto para enviar
        // a la persona que realiza las reservas
        StringBuilder cuerpos = new StringBuilder();
        for (Reserva reserva : reservas) {
            if (reserva.getEstado().equals(Estado.R)) {
                // Enviar mail
                String linkCancelacion = templateLinkCancelacion.replace("{idReserva}", reserva.getId().toString());
                String linkModificacion = templateLinkModificacion.replace("{idReserva}", reserva.getId().toString());
                String cuerpo;
                try {
                    cuerpo = enviarMailConfirmacion(linkCancelacion, linkModificacion, reserva, idioma, formatoFecha, formatoHora);
                } catch (Exception ex) {
                    cuerpo = null;
                }
                if (cuerpo == null) {
                    cuerpo = "Reserva id {idReserva} confirmada.".replace("{idReserva}", reserva.getId().toString());
                }
                cuerpos.append("<br />Documento: " + reserva.getTipoDocumento() + " " + reserva.getNumeroDocumento() + "<br /><br />" + cuerpo);
                cuerpos.append("<br />*************************<br />");
                // Almacenar datos para SMS y TextoAVoz, si corresponde
                almacenarSmsYTav(Comunicacion.Tipo2.RESERVA, reserva, formatoFecha, formatoHora);
                // Enviar aviso a MiPerfil
                enviarAvisoMiPerfil(empresa, reserva, Comunicacion.Tipo2.RESERVA);
            }
        }
        // Enviar las comunicaciones a la persona que realiza las reservas
        if (tokenReserva.getCorreoe() != null) {
            try {
                String cuerpo = cuerpos.toString();
                MailUtiles.enviarMail(tokenReserva.getCorreoe(), "Confirmación de reserva múltiple", cuerpo, MailUtiles.CONTENT_TYPE_HTML);
                // No se guarda el cuerpo porque podría ser muy grande si hay
                // muchas reservas, y puede verse en el envío de cada reserva
                // individual
                Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVAMULTIPLE, tokenReserva.getCorreoe(), tokenReserva.getRecurso(),
                                tokenReserva, "");
                comunicacion.setProcesado(true);
                entityManager.persist(comunicacion);
            } catch (MessagingException mEx) {
                try {
                    Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVAMULTIPLE, tokenReserva.getCorreoe(), tokenReserva.getRecurso(),
                                    tokenReserva, "");
                    entityManager.persist(comunicacion);
                } catch (Exception ex) {
                    logger.error("No se pudo enviar una comunicación por confirmación de reserva múltiple", ex);
                    ex.printStackTrace();
                }
            }
        }
    }

	public void enviarComunicacionesCancelacion(Empresa empresa, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws UserException {
	    enviarComunicacionesCancelacion(empresa, reserva, idioma, formatoFecha, formatoHora, null, null);
	}

    public void enviarComunicacionesCancelacion(Empresa empresa, Reserva reserva, String idioma, String formatoFecha, String formatoHora, String asunto,
                    String cuerpo) throws UserException {
        // Enviar mail
        boolean mailOk = enviarMailCancelacion(reserva, idioma, formatoFecha, formatoHora, asunto, cuerpo);
        // Almacenar datos para SMS y TextoAVoz, si corresponde
        almacenarSmsYTav(Comunicacion.Tipo2.CANCELA, reserva, formatoFecha, formatoHora);
        // Enviar aviso a MiPerfil
        boolean mipOk = enviarAvisoMiPerfil(empresa, reserva, Comunicacion.Tipo2.CANCELA);
        // Si no se pudo enviar mail ni aviso a mi perfil lanzar una excepción
        if (!mailOk && !mipOk) {
            throw new UserException("no_se_pudo_enviar_notificacion_de_cancelacion");
        }
    }

    public void enviarComunicacionesTraslado(Empresa empresa, String linkCancelacion, String linkModificacion, Reserva reservaOrigen, Reserva reservaDestino, 
                    String idioma, String formatoFecha, String formatoHora) throws UserException {
        // Enviar mail
        enviarMailTraslado(linkCancelacion, linkModificacion, reservaOrigen, reservaDestino, idioma, formatoFecha, formatoHora);
        // No se envia aviso a MiPerfil, según los requerimientos del encargo 34
    }

    private String enviarMailConfirmacion(String linkCancelacion, String linkModificacion, Reserva reserva, String idioma, String formatoFecha,
                    String formatoHora) {
        // Se envía el mail obligatorio al usuario
        Recurso recurso = null;
        String email = null;
        try {
            recurso = reserva.getDisponibilidades().get(0).getRecurso();
            Agenda agenda = recurso.getAgenda();
            TextoAgenda textoAgenda = null;
            if (agenda.getTextosAgenda() != null && idioma != null) {
                textoAgenda = agenda.getTextosAgenda().get(idioma);
            }
            if (textoAgenda == null) {
                for (TextoAgenda ta : agenda.getTextosAgenda().values()) {
                    if (ta.isPorDefecto()) {
                        textoAgenda = ta;
                    }
                }
            }
            if (textoAgenda == null) {
                textoAgenda = new TextoAgenda();
            }
            // Obtener el mail del usuario, que es el dato a solicitar llamado
            // "Mail" en la agrupacion que no se puede borrar
            String cuerpo = null;
            cuerpo = textoAgenda.getTextoCorreoConf();
            if (cuerpo != null && !cuerpo.isEmpty()) {
                cuerpo = Metavariables.remplazarMetavariables(cuerpo, reserva, formatoFecha, formatoHora, linkCancelacion, linkModificacion);
                for (DatoReserva dato : reserva.getDatosReserva()) {
                    DatoASolicitar datoSol = dato.getDatoASolicitar();
                    if (DatoASolicitar.CORREO_ELECTRONICO.equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
                        email = dato.getValor();
                        MailUtiles.enviarMail(email, "Confirmación de reserva", cuerpo, MailUtiles.CONTENT_TYPE_HTML);
                        Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva, cuerpo);
                        comunicacion.setProcesado(true);
                        entityManager.persist(comunicacion);
                    }
                }
                return cuerpo;
            } else {
                // Indicar que no se pudo enviar el mail
                return null;
            }
        } catch (MessagingException mEx) {
            logger.warn("No se pudo enviar el mail de confirmación", mEx);
            try {
                if (recurso != null) {
                    if (email == null) {
                        email = "***";
                    }
                    Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva, null);
                    entityManager.persist(comunicacion);
                }
            } catch (Exception ex) {
                //
            }
            // Indicar que no se pudo enviar el mail
            return null;
        }
    }

    private boolean enviarMailCancelacion(Reserva reserva, String idioma, String formatoFecha, String formatoHora, String asunto, String cuerpo) {
        // Se envía el mail obligatorio al usuario
        Recurso recurso = null;
        try {
            recurso = reserva.getDisponibilidades().get(0).getRecurso();
            if (cuerpo == null) {
                Agenda agenda = recurso.getAgenda();
                TextoAgenda textoAgenda = null;
                if (agenda.getTextosAgenda() != null) {
                    textoAgenda = agenda.getTextosAgenda().get(idioma);
                }
                if (textoAgenda == null) {
                    for (TextoAgenda ta : agenda.getTextosAgenda().values()) {
                        if (ta.isPorDefecto()) {
                            textoAgenda = ta;
                        }
                    }
                }
                if (textoAgenda != null) {
                    cuerpo = textoAgenda.getTextoCorreoCanc();
                }
            }
            if (asunto == null) {
                asunto = "Cancelación de reserva";
            }
            // Obtener el mail del usuario, que es el dato a solicitar llamado
            // "Mail" en la agrupacion que no se puede borrar
            String emailTo = null;
            for (DatoReserva dato : reserva.getDatosReserva()) {
                DatoASolicitar datoSol = dato.getDatoASolicitar();
                if (DatoASolicitar.CORREO_ELECTRONICO.equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
                    emailTo = dato.getValor();
                }
            }
            if (emailTo != null) {
                if (cuerpo != null && !cuerpo.isEmpty()) {
                    cuerpo = Metavariables.remplazarMetavariables(cuerpo, reserva, formatoFecha, formatoHora, "", "");
                    MailUtiles.enviarMail(emailTo, asunto, cuerpo, MailUtiles.CONTENT_TYPE_HTML);
                }
                Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.CANCELA, emailTo, recurso, reserva, asunto + "/" + cuerpo);
                comunicacion.setProcesado(true);
                entityManager.persist(comunicacion);
            } else {
                // Indicar que no se pudo enviar el mail
                return false;
            }
            // Indicar que se pudo enviar el mail
            return true;
        } catch (MessagingException mEx) {
            // Indicar que no se pudo enviar el mail
            logger.warn("No se pudo enviar el mail de cancelación", mEx);
            return false;
        }
    }
    
    private String enviarMailTraslado(String linkCancelacion, String linkModificacion, Reserva reservaOrigen, Reserva reservaDestino, String idioma, 
                    String formatoFecha, String formatoHora) {
        // Se envía el mail obligatorio al usuario
        Recurso recurso = null;
        String email = null;
        try {
            recurso = reservaDestino.getDisponibilidades().get(0).getRecurso();
            Agenda agenda = recurso.getAgenda();
            TextoAgenda textoAgenda = null;
            if (agenda.getTextosAgenda() != null && idioma != null) {
                textoAgenda = agenda.getTextosAgenda().get(idioma);
            }
            if (textoAgenda == null) {
                for (TextoAgenda ta : agenda.getTextosAgenda().values()) {
                    if (ta.isPorDefecto()) {
                        textoAgenda = ta;
                    }
                }
            }
            if (textoAgenda == null) {
                textoAgenda = new TextoAgenda();
            }
            // Obtener el mail del usuario, que es el dato a solicitar llamado
            // "Mail" en la agrupacion que no se puede borrar
            String cuerpo = null;
            cuerpo = textoAgenda.getTextoCorreoTras();
            if (cuerpo != null && !cuerpo.isEmpty()) {
                cuerpo = Metavariables.remplazarMetavariables(cuerpo, reservaOrigen, reservaDestino, formatoFecha, formatoHora, linkCancelacion, linkModificacion);
                for (DatoReserva dato : reservaDestino.getDatosReserva()) {
                    DatoASolicitar datoSol = dato.getDatoASolicitar();
                    if (DatoASolicitar.CORREO_ELECTRONICO.equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
                        email = dato.getValor();
                        MailUtiles.enviarMail(email, "Traslado de reserva", cuerpo, MailUtiles.CONTENT_TYPE_HTML);
                        Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.TRASLADA, email, recurso, reservaDestino, cuerpo);
                        comunicacion.setProcesado(true);
                        entityManager.persist(comunicacion);
                    }
                }
                return cuerpo;
            } else {
                // Indicar que no se pudo enviar el mail
                return null;
            }
        } catch (MessagingException mEx) {
            logger.warn("No se pudo enviar el mail de confirmación", mEx);
            try {
                if (recurso != null) {
                    if (email == null) {
                        email = "***";
                    }
                    Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.TRASLADA, email, recurso, reservaDestino, null);
                    entityManager.persist(comunicacion);
                }
            } catch (Exception ex) {
                //
            }
            // Indicar que no se pudo enviar el mail
            return null;
        }
    }

    private boolean almacenarSmsYTav(Comunicacion.Tipo2 tipo2, Reserva reserva, String formatoFecha, String formatoHora) {
        // Se envía el mail obligatorio al usuario
        try {
            Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
            // Obtener los numeros de telefono del usuario, que son los datos a
            // solicitar llamados "TelefonoMovil" y "TelefonoFijo" en la
            // agrupacion que no se puede borrar
            for (DatoReserva dato : reserva.getDatosReserva()) {
                DatoASolicitar datoSol = dato.getDatoASolicitar();
                if ("TelefonoMovil".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
                    // Tiene telefono movil, se envia SMS
                    String telefonoMovil = dato.getValor();
                    Comunicacion comunicacion = new Comunicacion(Tipo1.SMS, tipo2, telefonoMovil, recurso, reserva, null);
                    entityManager.persist(comunicacion);
                }
                if ("TelefonoFijo".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
                    // Tiene telefono movil, se envia SMS
                    String telefonoFijo = dato.getValor();
                    Comunicacion comunicacion = new Comunicacion(Tipo1.TEXTOAVOZ, tipo2, telefonoFijo, recurso, reserva, null);
                    entityManager.persist(comunicacion);
                }
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean enviarAvisoMiPerfil(Empresa empresa, Reserva reserva, Comunicacion.Tipo2 tipoAviso) {
        return miPerfilBean.enviarAviso(empresa, reserva, tipoAviso);
    }

}
