--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: global; Type: SCHEMA; Schema: -; Owner: sae
--

CREATE SCHEMA global;


ALTER SCHEMA global OWNER TO sae;

SET search_path = global, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: ae_captchas; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_captchas (
    clave character varying(100) NOT NULL,
    fase character varying(1024)
);


ALTER TABLE ae_captchas OWNER TO sae;

--
-- Name: ae_configuracion; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_configuracion (
    clave character varying(100) NOT NULL,
    valor character varying(1024)
);


ALTER TABLE ae_configuracion OWNER TO sae;

--
-- Name: ae_empresas; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_empresas (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    datasource character varying(25) NOT NULL,
    fecha_baja timestamp without time zone,
    org_id integer,
    org_codigo character varying(10),
    org_nombre character varying(100),
    unej_id integer,
    unej_codigo character varying(10),
    unej_nombre character varying(100),
    logo bytea,
    cc_finalidad character varying(100) DEFAULT ''::character varying NOT NULL,
    cc_responsable character varying(100) DEFAULT ''::character varying NOT NULL,
    cc_direccion character varying(100) DEFAULT ''::character varying NOT NULL,
    logo_texto character varying(100),
    timezone character varying(25),
    formato_fecha character varying(25),
    formato_hora character varying(25),
    oid character varying(25),
    pie_publico text
);


ALTER TABLE ae_empresas OWNER TO sae;

--
-- Name: ae_novedades; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_novedades (
    fecha_creacion timestamp with time zone NOT NULL,
    fecha_ult_intento timestamp without time zone NOT NULL,
    intentos integer DEFAULT 0 NOT NULL,
    datos character varying(4096) NOT NULL,
    enviado boolean DEFAULT false NOT NULL,
    id integer NOT NULL,
    reserva_id integer,
    empresa_id integer
);


ALTER TABLE ae_novedades OWNER TO sae;

--
-- Name: ae_oficinas; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_oficinas (
    id character varying(25) NOT NULL,
    tramite_id character varying(25) NOT NULL,
    nombre character varying(100) NOT NULL,
    direccion character varying(100),
    localidad character varying(100),
    departamento character varying(100),
    telefonos character varying(100),
    horarios character varying(100),
    comentarios character varying(1000)
);


ALTER TABLE ae_oficinas OWNER TO sae;

--
-- Name: ae_organismos; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_organismos (
    id integer NOT NULL,
    codigo character varying(25) NOT NULL,
    nombre character varying(100) NOT NULL
);


ALTER TABLE ae_organismos OWNER TO sae;

--
-- Name: ae_rel_usuarios_empresas; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_rel_usuarios_empresas (
    usuario_id integer NOT NULL,
    empresa_id integer NOT NULL
);


ALTER TABLE ae_rel_usuarios_empresas OWNER TO sae;

--
-- Name: ae_rel_usuarios_roles; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_rel_usuarios_roles (
    usuario_id integer NOT NULL,
    empresa_id integer NOT NULL,
    rol_nombre character varying NOT NULL
);


ALTER TABLE ae_rel_usuarios_roles OWNER TO sae;

--
-- Name: ae_textos; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_textos (
    codigo character varying(100) NOT NULL,
    texto character varying(4096) NOT NULL
);


ALTER TABLE ae_textos OWNER TO sae;

--
-- Name: ae_tokens; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_tokens (
    token character varying(25) NOT NULL,
    empresa_id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    fecha timestamp without time zone NOT NULL
);


ALTER TABLE ae_tokens OWNER TO sae;

--
-- Name: ae_tramites; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_tramites (
    id character varying(25) NOT NULL,
    empresa_id integer,
    nombre character varying(100) NOT NULL,
    quees character varying(1000) NOT NULL,
    temas character varying(1000) NOT NULL,
    online boolean NOT NULL
);


ALTER TABLE ae_tramites OWNER TO sae;

--
-- Name: ae_trazabilidad; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_trazabilidad (
    transaccion_id character varying(100) NOT NULL,
    fecha_creacion timestamp with time zone NOT NULL,
    fecha_ult_intento timestamp without time zone NOT NULL,
    intentos integer DEFAULT 0 NOT NULL,
    datos character varying(4096) NOT NULL,
    enviado boolean DEFAULT false NOT NULL,
    id integer NOT NULL,
    es_cabezal boolean DEFAULT false NOT NULL,
    reserva_id integer,
    empresa_id integer,
    es_final boolean DEFAULT false
);


ALTER TABLE ae_trazabilidad OWNER TO sae;

--
-- Name: ae_unidadesejecutoras; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_unidadesejecutoras (
    id integer NOT NULL,
    codigo character varying(25) NOT NULL,
    nombre character varying(100) NOT NULL
);


ALTER TABLE ae_unidadesejecutoras OWNER TO sae;

--
-- Name: ae_usuarios; Type: TABLE; Schema: global; Owner: sae; Tablespace: 
--

CREATE TABLE ae_usuarios (
    id integer NOT NULL,
    codigo character varying(25) NOT NULL,
    nombre character varying(100) NOT NULL,
    fecha_baja timestamp without time zone,
    password character varying(50),
    correoe character varying(100),
    superadmin boolean
);


ALTER TABLE ae_usuarios OWNER TO sae;

--
-- Name: s_ae_empresa; Type: SEQUENCE; Schema: global; Owner: sae
--

CREATE SEQUENCE s_ae_empresa
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_empresa OWNER TO sae;

--
-- Name: s_ae_novedades; Type: SEQUENCE; Schema: global; Owner: sae
--

CREATE SEQUENCE s_ae_novedades
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_novedades OWNER TO sae;

--
-- Name: s_ae_trazabilidad; Type: SEQUENCE; Schema: global; Owner: sae
--

CREATE SEQUENCE s_ae_trazabilidad
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_trazabilidad OWNER TO sae;

--
-- Name: s_ae_usuario; Type: SEQUENCE; Schema: global; Owner: sae
--

CREATE SEQUENCE s_ae_usuario
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_usuario OWNER TO sae;

--
-- Data for Name: ae_captchas; Type: TABLE DATA; Schema: global; Owner: sae
--



--
-- Data for Name: ae_configuracion; Type: TABLE DATA; Schema: global; Owner: sae
--

INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_URLSTS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_ROL', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_POLICY', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_ORG_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_ORG_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_ORG_KS_ALIAS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_KS_ALIAS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_TS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_TS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_WSATO_LINEA', 'http://testservicios.pge.red.uy/agesic/LineaService/preprod');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_WSAACTION_CABEZAL', 'http://ws.web.bruto.itramites.agesic.gub.uy/cabezalService/persist');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_WSAACTION_LINEA', 'http://ws.web.bruto.itramites.agesic.gub.uy/lineaService/persist');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_WSATO_CABEZAL', 'http://testservicios.pge.red.uy/agesic/cabezalService/preprod');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_WSATO', 'http://testservicios.pge.red.uy/SAENovedades/publicacion');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_WSAACTION', 'http://testservicios.pge.red.uy/SAENovedades/publicacion/nuevaNovedad');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_TIMEOUT', '3500');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_MAXINTENTOS', '10');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_MAXINTENTOS', '10');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_ORG_KS_ALIAS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_ORG_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_ORG_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_POLICY', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_ROL', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_KS_ALIAS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_TS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_TS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_TIMEOUT', '3500');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_URLSTS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_PRODUCTOR', 'AGESIC');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_TOPICO', 'SAENovedades');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_HABILITADO', 'FALSE');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_HABILITADO', 'TRUE');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAMITE_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAMITE_USER', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('IDIOMAS_SOPORTADOS', 'es');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_VERSION', '101');


--
-- Data for Name: ae_empresas; Type: TABLE DATA; Schema: global; Owner: sae
--


--
-- Data for Name: ae_novedades; Type: TABLE DATA; Schema: global; Owner: sae
--


--
-- Data for Name: ae_oficinas; Type: TABLE DATA; Schema: global; Owner: sae
--


--
-- Data for Name: ae_organismos; Type: TABLE DATA; Schema: global; Owner: sae
--


--
-- Data for Name: ae_rel_usuarios_empresas; Type: TABLE DATA; Schema: global; Owner: sae
--


--
-- Data for Name: ae_rel_usuarios_roles; Type: TABLE DATA; Schema: global; Owner: sae
--



--
-- Data for Name: ae_textos; Type: TABLE DATA; Schema: global; Owner: sae
--

INSERT INTO ae_textos (codigo, texto) VALUES ('ingrese_al_sistema', 'Ingrese al Sistema de Agenda electrónica');
INSERT INTO ae_textos (codigo, texto) VALUES ('string', 'Texto');
INSERT INTO ae_textos (codigo, texto) VALUES ('contrasena', 'Contraseña');
INSERT INTO ae_textos (codigo, texto) VALUES ('ingresar', 'Ingresar');
INSERT INTO ae_textos (codigo, texto) VALUES ('ingrese_usuario_y_contrasena', 'Ingrese su código de usuario y contraseña');
INSERT INTO ae_textos (codigo, texto) VALUES ('saltar_al_contenido', 'Saltar al contenido');
INSERT INTO ae_textos (codigo, texto) VALUES ('empresa', 'Empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('agenda', 'Agenda');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccionar', 'Seleccionar');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso', 'Recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('usuario', 'Usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('cerrar_sesion', 'Cerrar sesión');
INSERT INTO ae_textos (codigo, texto) VALUES ('menu', 'Menu');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_aplicacion', 'SAE - Sistema de Agenda Electrónica');
INSERT INTO ae_textos (codigo, texto) VALUES ('informacion_adicional', 'Información adicional');
INSERT INTO ae_textos (codigo, texto) VALUES ('elegir_dia_y_hora', 'Elegir día y hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('volver_a_la_pagina_principal', 'Volver a la página principal');
INSERT INTO ae_textos (codigo, texto) VALUES ('nueva_reserva', 'Nueva reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('detalle_y_ubicacion', 'Detalle y ubicación');
INSERT INTO ae_textos (codigo, texto) VALUES ('dia_y_hora', 'Día y hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_necesarios', 'Datos necesarios');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirmacion', 'Confirmación');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccione_el_dia', 'Seleccione el día de su preferencia haciendo click con el mouse');
INSERT INTO ae_textos (codigo, texto) VALUES ('debajo_del_calendario_horarios_disponibles', 'Luego de seleccionar el día, debajo del calendario se mostrarán los horarios disponibles para ese día');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccione_un_horario', 'Seleccione un horario para continuar con la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('preferencia_de_horario', 'Preferencia de horario');
INSERT INTO ae_textos (codigo, texto) VALUES ('cualquier_horario', 'Cualquier horario');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_matutino', 'Solo por la mañana');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_vespertino', 'Solo por la tarde');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccione_un_dia', 'Seleccione un día');
INSERT INTO ae_textos (codigo, texto) VALUES ('horarios_diponibles', 'Horarios diponibles');
INSERT INTO ae_textos (codigo, texto) VALUES ('por_la_manana', 'Por la mañana');
INSERT INTO ae_textos (codigo, texto) VALUES ('por_la_tarde', 'Por la tarde');
INSERT INTO ae_textos (codigo, texto) VALUES ('completar_datos', 'Completar datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_marcados_obligatorios', 'Los datos que estén marcados con un asterisco (*) son obligatorios');
INSERT INTO ae_textos (codigo, texto) VALUES ('clausula_de_consentimiento_informado', 'Cláusula de consentimiento informado');
INSERT INTO ae_textos (codigo, texto) VALUES ('terminos_de_la_clausula', 'Términos de la cláusula');
INSERT INTO ae_textos (codigo, texto) VALUES ('acepto_los_terminos', 'Acepto los términos');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_acepto_los_terminos', 'No acepto los términos');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirmar_reserva', 'Confirmar reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_confirmada', 'Su reserva está confirmada');
INSERT INTO ae_textos (codigo, texto) VALUES ('guardar_ticket', 'Guardar ticket');
INSERT INTO ae_textos (codigo, texto) VALUES ('imprimir_ticket', 'Imprimir ticket');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelar_reserva', 'Cancelar reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('a_las', 'a las');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirma_cancelar_la_reserva', '¿Esta seguro que desea cancelar la reserva?');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelar_reserva_para_agenda', 'Cancelar reserva para');
INSERT INTO ae_textos (codigo, texto) VALUES ('continuar', 'Continuar');
INSERT INTO ae_textos (codigo, texto) VALUES ('su_reserva', 'Su reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_la_empresa', 'Debe especificar la empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_con_turnos_disponibles', 'Los días marcados en color verde tienen turnos disponibles');
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo_de_seguridad', 'Código de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo_de_seguridad_de_la_reserva', 'Código de cancelación de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_combinacion_de_parametros_especificada_no_es_valida', 'La combinación de parámetros especificada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_registrar_un_usuario_anonimo', 'No se pudo registrar un usuario anónimo para permitir esta invocación');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_empresa_especificada_no_es_valida', 'La empresa especificada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_agenda_especificada_no_es_valida', 'La agenda especificada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_reserva_o_ya_fue_cancelada', 'No se encuentra la reserva o la misma ya fue cancelada');
INSERT INTO ae_textos (codigo, texto) VALUES ('ingrese_el_codigo_de_seguridad', 'Ingrese el código de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_recurso_especificado_no_es_valido', 'El recurso especificado no es válido');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_ingresar_codigo_de_seguridad', 'Debe ingresar el código de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_ingresar_al_menos_dos_de_los_datos_solicitados', 'Debe ingresar al menos dos de los datos solicitados en la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_la_agenda', 'Debe especificar la agenda.');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_el_recurso', 'Debe especificar el recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encontraron_reservas', 'No se encontraron reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_la_reserva', 'Debe especificar la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_es_posible_cancelar_la_reserva', 'No es posible cancelar la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_cancelada_correctamente', 'Reserva cancelada correctamente');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_agenda_especificada_no_tiene_recursos', 'La agenda especificada no tiene recursos disponibles');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_no_habilitado_para_ser_accedido_desde_internet', 'El recurso especificado no está habilitado para ser accedido desde internet');
INSERT INTO ae_textos (codigo, texto) VALUES ('acceso_denegado', 'Acceso denegado');
INSERT INTO ae_textos (codigo, texto) VALUES ('sistema_en_mantenimiento', 'Sistema en mantenimiento. Por favor intente nuevamente más tarde');
INSERT INTO ae_textos (codigo, texto) VALUES ('sin_disponibilidades', 'No hay disponibilidades para la opción seleccionada');
INSERT INTO ae_textos (codigo, texto) VALUES ('clausula_de_consentimiento_informado_texto', 'De conformidad con la Ley N° 18.331, de 11 de agosto de 2008, de Protección de Datos Personales y Acción de Habeas Data (LPDP), los datos suministrados por usted quedarán incorporados en una base de datos, la cual será procesada exclusivamente para la siguiente finalidad: {finalidad}. "Los datos personales serán tratados con el grado de protección adecuado, tomándose las medidas de seguridad necesarias para evitar su alteración, pérdida, tratamiento o acceso no autorizado por parte de terceros que lo puedan utilizar para finalidades distintas para las que han sido solicitadas al usuario. El responsable de la base de datos es {responsable} y la dirección donde podrá ejercer los derechos de acceso, rectificación, actualización, inclusión o supresión, es {direccion}, según lo establecido en la LPDP.');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_aceptar_los_terminos_de_la_clausula_de_consentimiento_informado', 'Debe aceptar los términos de la cláusula de consentimiento informado para poder continuar');
INSERT INTO ae_textos (codigo, texto) VALUES ('ticket_de_reserva', 'Ticket de reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha', 'Fecha');
INSERT INTO ae_textos (codigo, texto) VALUES ('hora', 'Hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('serie', 'Serie');
INSERT INTO ae_textos (codigo, texto) VALUES ('numero', 'Número');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_realizada_el', 'Reserva realizada el día');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_un_horario', 'Debe seleccionar un día y una hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('ingrese_el_texto_que_aparece_en_la_imagen', 'Ingrese el texto que aparece en la imagen');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_ingresar_el_texto_que_aparece_en_la_imagen', 'Debe ingresar el texto que aparece en la imagen');
INSERT INTO ae_textos (codigo, texto) VALUES ('verificacion_de_seguridad', 'Verificación de seguridad');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_la_empresa', '¿Está seguro que desea eliminar la empresa?');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar', 'Eliminar');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelar', 'Cancelar');
INSERT INTO ae_textos (codigo, texto) VALUES ('listado_de_empresas', 'Listado de empresas');
INSERT INTO ae_textos (codigo, texto) VALUES ('identificador', 'Identificador');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre', 'Nombre');
INSERT INTO ae_textos (codigo, texto) VALUES ('acciones', 'Acciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('booleano', 'Sí/No');
INSERT INTO ae_textos (codigo, texto) VALUES ('lista', 'Lista desplegable');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_de_la_empresa', 'Datos de la empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('organismo', 'Organismo');
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo', 'Código');
INSERT INTO ae_textos (codigo, texto) VALUES ('recargar_listado', 'Recargar listado');
INSERT INTO ae_textos (codigo, texto) VALUES ('unidad_ejecutora', 'Unidad ejecutora');
INSERT INTO ae_textos (codigo, texto) VALUES ('logo', 'Logo');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_de_inicio_de_la_ventana_de_intranet', 'Días requeridos antes de comenzar a agendar');
INSERT INTO ae_textos (codigo, texto) VALUES ('subir', 'Subir');
INSERT INTO ae_textos (codigo, texto) VALUES ('logo_texto_alternativo', 'Texto alternativo del logo');
INSERT INTO ae_textos (codigo, texto) VALUES ('zona_horaria', 'Zona horaria');
INSERT INTO ae_textos (codigo, texto) VALUES ('formato_de_fecha', 'Formato de fecha');
INSERT INTO ae_textos (codigo, texto) VALUES ('formato_de_hora', 'Formato de hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('origen_de_datos', 'Origen de datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_para_la_clausula_de_consentimiento', 'Datos para la cláusula de consentimiento');
INSERT INTO ae_textos (codigo, texto) VALUES ('finalidad', 'Finalidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('responsable', 'Responsable');
INSERT INTO ae_textos (codigo, texto) VALUES ('direccion', 'Dirección');
INSERT INTO ae_textos (codigo, texto) VALUES ('guardar', 'Guardar');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_el_usuario', '¿Está seguro que desea eliminar el usuario?');
INSERT INTO ae_textos (codigo, texto) VALUES ('listado_de_usuarios', 'Listado de usuarios');
INSERT INTO ae_textos (codigo, texto) VALUES ('cedula_de_identidad', 'Cédula de identidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_y_apellido', 'Nombre y apellido');
INSERT INTO ae_textos (codigo, texto) VALUES ('correo_electronico', 'Correo electrónico');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar', 'Modificar');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_del_usuario', 'Datos del usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('superadministrador', 'Superadministrador');
INSERT INTO ae_textos (codigo, texto) VALUES ('enviar_por_correo', 'Enviar por correo');
INSERT INTO ae_textos (codigo, texto) VALUES ('roles_del_usuario_en_la_empresa', 'Roles del usuario en la empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('administrador', 'Administrador');
INSERT INTO ae_textos (codigo, texto) VALUES ('planificador', 'Planificador');
INSERT INTO ae_textos (codigo, texto) VALUES ('funcionario_de_call_center', 'Funcionario de call center');
INSERT INTO ae_textos (codigo, texto) VALUES ('funcionario_de_atencion', 'Funcionario de atención');
INSERT INTO ae_textos (codigo, texto) VALUES ('llamador', 'Llamador');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_de_la_agenda', 'Datos de la agenda');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_de_la_ventana_de_intranet', 'Duración de la ventana para agendar');
INSERT INTO ae_textos (codigo, texto) VALUES ('tramite', 'Trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('descripcion', 'Descripción');
INSERT INTO ae_textos (codigo, texto) VALUES ('misma_que_la_de_la_empresa', 'Misma que la de la empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_de_inicio_de_la_ventana_de_internet', 'Días requeridos antes de comenzar a agendar');
INSERT INTO ae_textos (codigo, texto) VALUES ('listado_de_agendas', 'Listado de agendas');
INSERT INTO ae_textos (codigo, texto) VALUES ('copiar', 'Copiar');
INSERT INTO ae_textos (codigo, texto) VALUES ('etiquetas', 'Etiquetas');
INSERT INTO ae_textos (codigo, texto) VALUES ('nomenclatura_para_recurso', 'Nomenclatura para ''Recurso'' (p.e. ''Oficina'', ''Centro'' o ''Sucursal'')');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_paso_1', 'Texto paso 1');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_para_el_paso_1_de_la_reserva', 'Texto para el paso 1 de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_paso_2', 'Texto paso 2');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_para_el_paso_2_de_la_reserva', 'Texto para el paso 2 de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_paso_3', 'Texto paso 3');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_para_el_paso_3_de_la_reserva', 'Texto para el paso 3 de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_para_el_ticket_de_reserva', 'Texto para el ticket de reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('correo_de_confirmacion', 'Correo de confirmación');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_para_el_correo_de_confirmacion', 'Texto para el correo de confirmación');
INSERT INTO ae_textos (codigo, texto) VALUES ('correo_de_cancelacion', 'Correo de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_para_el_correo_de_cancelacion', 'Texto para el correo de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('empresas', 'Empresas');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_empresa', 'Crear empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('consultar_empresas', 'Consultar empresas');
INSERT INTO ae_textos (codigo, texto) VALUES ('usuarios', 'Usuarios');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_usuario', 'Crear usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('consultar_usuarios', 'Consultar usuarios');
INSERT INTO ae_textos (codigo, texto) VALUES ('agendas', 'Agendas');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_agenda', 'Crear agenda');
INSERT INTO ae_textos (codigo, texto) VALUES ('consultar_agendas', 'Consultar agendas');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_textos_de_agendas', 'Modificar textos de agendas');
INSERT INTO ae_textos (codigo, texto) VALUES ('recursos', 'Recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_recurso', 'Crear recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('consultar_recursos', 'Consultar recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_textos_de_recursos', 'Modificar textos de recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_a_solicitar', 'Datos a solicitar');
INSERT INTO ae_textos (codigo, texto) VALUES ('agrupaciones', 'Agrupaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_dato', 'Crear dato');
INSERT INTO ae_textos (codigo, texto) VALUES ('consultar_datos', 'Consultar datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades', 'Disponibilidades');
INSERT INTO ae_textos (codigo, texto) VALUES ('consultar_disponibilidades', 'Consultar disponibilidades');
INSERT INTO ae_textos (codigo, texto) VALUES ('generar_un_dia', 'Generar un día');
INSERT INTO ae_textos (codigo, texto) VALUES ('copiar_dia', 'Copiar día');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_cupos', 'Modificar cupos');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar_disponibilidades', 'Eliminar disponibilidades');
INSERT INTO ae_textos (codigo, texto) VALUES ('reservas', 'Reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('reservar', 'Reservar');
INSERT INTO ae_textos (codigo, texto) VALUES ('considerar_el_sabado_como_dia_habil', 'Considerar como día hábil');
INSERT INTO ae_textos (codigo, texto) VALUES ('lista_de_espera', 'Lista de espera');
INSERT INTO ae_textos (codigo, texto) VALUES ('abrir_llamador', 'Abrir llamador');
INSERT INTO ae_textos (codigo, texto) VALUES ('oficina', 'Oficina');
INSERT INTO ae_textos (codigo, texto) VALUES ('localidad', 'Localidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('departamento', 'Departamento');
INSERT INTO ae_textos (codigo, texto) VALUES ('latitud', 'Latitud');
INSERT INTO ae_textos (codigo, texto) VALUES ('longitud', 'Longitud');
INSERT INTO ae_textos (codigo, texto) VALUES ('telefonos', 'Teléfonos');
INSERT INTO ae_textos (codigo, texto) VALUES ('horarios', 'Horarios');
INSERT INTO ae_textos (codigo, texto) VALUES ('inicio_de_vigencia', 'Inicio de vigencia');
INSERT INTO ae_textos (codigo, texto) VALUES ('fin_de_vigencia', 'Fin de vigencia');
INSERT INTO ae_textos (codigo, texto) VALUES ('cupos_minimos_de_la_ventana', 'Cupos mínimos de la ventana');
INSERT INTO ae_textos (codigo, texto) VALUES ('cantidad_de_dias_a_generar', 'Cantidad de días a generar');
INSERT INTO ae_textos (codigo, texto) VALUES ('listado_de_recursos', 'Listado de recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_el_dato', '¿Está seguro que desea eliminar el dato?');
INSERT INTO ae_textos (codigo, texto) VALUES ('informacion_adicional_del_recurso', 'Información adicional del recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_informacion', 'Agregar información');
INSERT INTO ae_textos (codigo, texto) VALUES ('orden', 'Orden');
INSERT INTO ae_textos (codigo, texto) VALUES ('etiqueta', 'Etiqueta');
INSERT INTO ae_textos (codigo, texto) VALUES ('valor', 'Valor');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_dato', 'Modificar dato');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_dato', 'Agregar dato');
INSERT INTO ae_textos (codigo, texto) VALUES ('volver', 'Volver');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_el_recurso', '¿Está seguro que desea eliminar el recurso?');
INSERT INTO ae_textos (codigo, texto) VALUES ('textos_del_llamador', 'Textos del llamador');
INSERT INTO ae_textos (codigo, texto) VALUES ('titulo_de_la_columna_de_datos', 'Título de la columna de datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('titulo_de_la_columna_del_puesto', 'Título de la columna del puesto');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_la_agenda', '¿Esta seguro que desea eliminar la agenda?');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_la_agrupacion', '¿Esta seguro que desea eliminar la agrupación?');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_agrupacion', 'Agregar agrupación');
INSERT INTO ae_textos (codigo, texto) VALUES ('listado_de_agrupaciones', 'Listado de agrupaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('agenda_creada', 'Agenda creada');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_agrupacion', 'Modificar agrupación');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_agrupacion', 'Crear agrupación');
INSERT INTO ae_textos (codigo, texto) VALUES ('vista_preliminar_del_formulario_de_ingreso_de_datos_de_la_reserva', 'Vista preliminar del formulario de ingreso de datos de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('ver_diseno', 'Ver diseño');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_generales', 'Datos generales');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_de_ayuda', 'Texto de ayuda');
INSERT INTO ae_textos (codigo, texto) VALUES ('tipo_de_dato', 'Tipo de dato');
INSERT INTO ae_textos (codigo, texto) VALUES ('largo_maximo', 'Largo máximo');
INSERT INTO ae_textos (codigo, texto) VALUES ('requerido', 'Requerido');
INSERT INTO ae_textos (codigo, texto) VALUES ('clave', 'Clave');
INSERT INTO ae_textos (codigo, texto) VALUES ('agrupacion', 'Agrupación');
INSERT INTO ae_textos (codigo, texto) VALUES ('fila', 'Fila');
INSERT INTO ae_textos (codigo, texto) VALUES ('diseno_del_formulario', 'Diseño del formulario');
INSERT INTO ae_textos (codigo, texto) VALUES ('diseno_del_reporte', 'Diseño del reporte');
INSERT INTO ae_textos (codigo, texto) VALUES ('incluir_en_el_reporte', 'Incluir en el reporte');
INSERT INTO ae_textos (codigo, texto) VALUES ('ancho', 'Ancho');
INSERT INTO ae_textos (codigo, texto) VALUES ('diseno_del_llamador', 'Diseño del llamador');
INSERT INTO ae_textos (codigo, texto) VALUES ('incluir_en_el_llamador', 'Incluir en el llamador');
INSERT INTO ae_textos (codigo, texto) VALUES ('largo', 'Largo');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_el_campo', '¿Esta seguro que desea eliminar el campo?');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_el_valor', '¿Esta seguro que desea eliminar el valor?');
INSERT INTO ae_textos (codigo, texto) VALUES ('valores_posibles_para_el_dato', 'Valores posibles para el dato');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_valor', 'Agregar valor');
INSERT INTO ae_textos (codigo, texto) VALUES ('vigencia_desde', 'Vigencia desde');
INSERT INTO ae_textos (codigo, texto) VALUES ('vigencia_hasta', 'Vigencia hasta');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_valor_posible', 'Modificar valor posible');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_valor_posible', 'Crear valor posible');
INSERT INTO ae_textos (codigo, texto) VALUES ('aplicar_a_todos_los_dias', 'Todos los días subsiguientes');
INSERT INTO ae_textos (codigo, texto) VALUES ('agenda_eliminada', 'Agenda eliminada');
INSERT INTO ae_textos (codigo, texto) VALUES ('desde', 'Desde');
INSERT INTO ae_textos (codigo, texto) VALUES ('hasta', 'Hasta');
INSERT INTO ae_textos (codigo, texto) VALUES ('consultar', 'Consultar');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_en_periodo_consultado', 'Disponibilidades en período consultado');
INSERT INTO ae_textos (codigo, texto) VALUES ('cupos_disponibles', 'Cupos disponibles');
INSERT INTO ae_textos (codigo, texto) VALUES ('ver_detalles', 'Ver detalles');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_para_el_dia', 'Disponibilidades para el día');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_por_la_manana', 'Disponibilidades por la mañana');
INSERT INTO ae_textos (codigo, texto) VALUES ('hora_de_inicio', 'Hora de inicio');
INSERT INTO ae_textos (codigo, texto) VALUES ('cupos_totales', 'Cupos totales');
INSERT INTO ae_textos (codigo, texto) VALUES ('cantidad_de_reservas', 'Cantidad de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_por_la_tarde', 'Disponibilidades por la tarde');
INSERT INTO ae_textos (codigo, texto) VALUES ('periodo_a_consultar', 'Período a consultar');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_para_la_cual_generar', 'Fecha para la cual generar');
INSERT INTO ae_textos (codigo, texto) VALUES ('nuevas_disponibilidades_para_la_fecha_consultada', 'Nuevas disponibilidades para la fecha consultada');
INSERT INTO ae_textos (codigo, texto) VALUES ('frecuencia', 'Frecuencia');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_disponibilidades', 'Crear disponibilidades');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccionar_la_fecha_modelo', 'Seleccionar la fecha modelo');
INSERT INTO ae_textos (codigo, texto) VALUES ('periodo_para_el_cual_generar', 'Período para el cual generar');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_de_inicio', 'Fecha de inicio');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_de_fin', 'Fecha de fin');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_modificar_las_disponibilidades_marcadas', '¿Esta seguro que desea modificar las disponibilidades marcadas?');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_para_la_cual_modificar', 'Fecha para la cual modificar');
INSERT INTO ae_textos (codigo, texto) VALUES ('todas_las_horas_del_turno_matutino', 'Todas las horas del turno matutino');
INSERT INTO ae_textos (codigo, texto) VALUES ('todas_las_horas_del_turno_vespertino', 'Todas las horas del turno vespertino');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificacion_de_disponibilidades_para_las_horas_seleccionadas', 'Modificación de disponibilidades para las horas seleccionadas');
INSERT INTO ae_textos (codigo, texto) VALUES ('horarios_seleccionados', 'Horarios seleccionados');
INSERT INTO ae_textos (codigo, texto) VALUES ('operacion_a_realizar', 'Operación a realizar');
INSERT INTO ae_textos (codigo, texto) VALUES ('aumentar_disponibilidades_en_la_cantidad_especificada', 'Aumentar disponibilidades en la cantidad especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('disminuir_disponibilidades_en_la_cantidad_especificada', 'Disminuir disponibilidades en la cantidad especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('establecer_disponibilidades_en_la_cantidad_especificada', 'Establecer disponibilidades en la cantidad especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('cantidad_de_cupos', 'Cantidad de cupos');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccionar_semana', 'Seleccionar semana');
INSERT INTO ae_textos (codigo, texto) VALUES ('semana', 'Semana');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_para_la_semana_consultada', 'Disponibilidades para la semana consultada');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar_disponibilidades_de_toda_la_semana', 'Eliminar disponibilidades de toda la semana');
INSERT INTO ae_textos (codigo, texto) VALUES ('hora_de_fin', 'Hora de fin');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccione_ubicacion', 'Seleccione ubicación');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccione_dia_y_hora', 'Seleccione día y hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_en_la_manana', 'Disponibilidades en la mañana');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_en_la_tarde', 'Disponibilidades en la tarde');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_reserva_esta_confirmada', 'La reserva está confirmada');
INSERT INTO ae_textos (codigo, texto) VALUES ('si', 'Sí');
INSERT INTO ae_textos (codigo, texto) VALUES ('no', 'No');
INSERT INTO ae_textos (codigo, texto) VALUES ('documento', 'Documento');
INSERT INTO ae_textos (codigo, texto) VALUES ('llamar_al_siguiente', 'Llamar al siguiente');
INSERT INTO ae_textos (codigo, texto) VALUES ('mostrar', 'Mostrar');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_personas_en_espera', 'No hay personas en espera');
INSERT INTO ae_textos (codigo, texto) VALUES ('volver_a_llamar', 'Volver a llamar');
INSERT INTO ae_textos (codigo, texto) VALUES ('falto', 'Faltó');
INSERT INTO ae_textos (codigo, texto) VALUES ('asistio', 'Asistió');
INSERT INTO ae_textos (codigo, texto) VALUES ('tamano_de_la_pantalla', 'Tamaño de la pantalla');
INSERT INTO ae_textos (codigo, texto) VALUES ('numero_de_puesto', 'Número de puesto');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_una_agenda_seleccionada', 'Debe haber una agenda seleccionada');
INSERT INTO ae_textos (codigo, texto) VALUES ('agrupacion_modificada', 'Agrupación modificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_una_agrupacion_seleccionada', 'Debe haber una agrupación seleccionada');
INSERT INTO ae_textos (codigo, texto) VALUES ('agrupacion_eliminada', 'Agrupación eliminada');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_un_recurso_seleccionado', 'Debe haber un recurso seleccionado');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_de_la_agrupacion_es_obligatorio', 'El nombre de la agrupación es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('agrupacion_creada', 'Agrupación creada');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_creadas', 'Disponibilidades creadas');
INSERT INTO ae_textos (codigo, texto) VALUES ('error_de_acceso_concurrente', 'Error de acceso concurrente. Intente más tarde.');
INSERT INTO ae_textos (codigo, texto) VALUES ('campo_modificado', 'Campo modificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('campo_eliminado', 'Campo eliminado');
INSERT INTO ae_textos (codigo, texto) VALUES ('dato_creado', 'Dato creado');
INSERT INTO ae_textos (codigo, texto) VALUES ('valor_modificado', 'Valor modificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('valor_eliminado', 'Valor eliminado');
INSERT INTO ae_textos (codigo, texto) VALUES ('valor_creado', 'Valor creado');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_correctos', 'Datos correctos, las validaciones se han ejecutado con éxito');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_modificadas', 'Disponibilidades modificadas');
INSERT INTO ae_textos (codigo, texto) VALUES ('agenda_modificada', 'Agenda modificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponibilidades_eliminadas', 'Disponibilidades eliminadas');
INSERT INTO ae_textos (codigo, texto) VALUES ('empresa_creada', 'Empresa creada');
INSERT INTO ae_textos (codigo, texto) VALUES ('empresa_modificada', 'Empresa modificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_creado', 'Recurso creado');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_eliminado', 'Recurso eliminado');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_copiado', 'Recurso copiado');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_modificado', 'Recurso modificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('dato_modificado', 'Dato modificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_un_dato_seleccionado', 'Debe haber un dato seleccionado');
INSERT INTO ae_textos (codigo, texto) VALUES ('dato_eliminado', 'Dato eliminado');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_cancelada', 'Reserva cancelada');
INSERT INTO ae_textos (codigo, texto) VALUES ('usuario_creado', 'Usuario creado');
INSERT INTO ae_textos (codigo, texto) VALUES ('usuario_modificado', 'Usuario modificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_un_usuario_seleccionado', 'Debe haber un usuario seleccionado');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_contrasena_fue_enviada', 'La contraseña fue enviada al usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_de_la_agenda_es_obligatorio', 'El nombre de la agenda es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_es_obligatoria', 'La fecha es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_hora_de_inicio_es_obligatoria', 'La hora de inicio es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_hora_de_fin_es_obligatoria', 'La hora de fin es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_crear_al_menos_una_agrupacion', 'Antes de crear datos a solicitar debe crear al menos una agrupación');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_permite_eliminar_esta_agrupacion', 'No se permite eliminar esta agrupación');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_permite_eliminar_este_dato', 'No se permite eliminar este dato');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_del_dato_es_obligatorio', 'El nombre del dato es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_un_valor', 'Debe seleccionar un valor');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_permite_eliminar_este_valor', 'No se permite eliminar este valor');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_del_valor_es_obligatorio', 'El nombre del valor es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_se_permiten_letras_y_numeros', 'Solo se permiten letras y números');
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo_de_usuario', 'Cédula de identidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_obtener_la_ultima_fecha_generada', 'No se pudo obtener la última fecha generada');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_se_permiten_hasta_5_caracteres', 'Solo se permiten hasta 5 caracteres');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_es_obligatoria', 'La fecha de inicio es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_es_obligatoria', 'La fecha de fin es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio', 'La fecha de fin debe ser posterior a la fecha de inicio');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_al_menos_una_disponibilidad', 'Debe seleccionar al menos una disponibilidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_disponibilidades_para_la_opcion_seleccionada', 'No hay disponibilidades para la opción seleccionada');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_un_horario_con_disponibilidades', 'Debe seleccionar un horario con disponibilidades');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_del_recurso_es_obligatorio', 'El nombre del recurso es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_descripcion_del_recurso_es_obligatoria', 'La descripción del recurso es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio', 'Los días de inicio de la ventana de intranet es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_inicio_de_la_ventana_de_intranet_debe_ser_mayor_a_cero', 'Los días de inicio de la ventana de intranet debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_la_ventana_de_intranet_es_obligatorio', 'Los días de la ventana de intranet es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero', 'Los días de la ventana de intranet debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero', 'Los días de la ventana de internet debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('version', 'Versión');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_la_ventana_de_internet_es_obligatorio', 'Los días de la ventana de internet es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_inicio_de_la_ventana_de_internet_debe_ser_mayor_a_cero', 'Los días de inicio de la ventana de internet debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_inicio_de_la_ventana_de_internet_es_obligatorio', 'Los días de inicio de la ventana de internet es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_cupos_minimos_es_obligatoria', 'La cantidad de cupos mínimos es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero', 'La cantidad de cupos mínimos debe ser mayor o igual a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_dias_a_generar_es_obligatoria', 'La cantidad de días a generar es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero', 'La cantidad de días a generar debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet', 'La cantidad de días a generar debe ser mayor o igual que la suma de los días de inicio de la ventana de internet y los días de la ventana de internet');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet', 'La cantidad de días a generar debe ser mayor o igual que la suma de los días de inicio de la ventana de intranet y los días de la ventana de intranet');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero', 'El largo de la lista de espera debe ser mayor que cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_un_recurso_con_el_nombre_especificado', 'Ya existe un recurso con el nombre especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_de_disponibilidad_debe_ser_posterior_a_la_fecha_de_inicio', 'La fecha de inicio de disponibilidad debe ser posterior a la fecha de inicio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_etiqueta_del_dato_es_obligatoria', 'La etiqueta del dato es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_una_reserva', 'Debe seleccionar una reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_codigo_del_organismo_es_obligatorio', 'El código del organismo es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_de_la_empresa_es_obligatorio', 'El nombre de la empresa es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_origen_de_datos_de_la_empresa_es_obligatorio', 'El origen de datos de la empresa es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_finalidad_para_la_clausula_de_consentimiento_informado_es_obligatoria', 'La finalidad para la cláusula de consentimiento informado es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_direccion_para_la_clausula_de_consentimiento_informado_es_obligatoria', 'La dirección para la cláusula de consentimiento informado es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_responsable_para_la_clausula_de_consentimiento_informado_es_obligatorio', 'El responsable para la cláusula de consentimiento informado es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_una_empresa_con_el_nombre_especificado', 'Ya existe una empresa con el nombre especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_obtener_los_tramites_porque_la_empresa_no_esta_asociada_a_ningun_organismo', 'No se puede obtener los tramites porque la empresa no está asociada a ningún organismo');
INSERT INTO ae_textos (codigo, texto) VALUES ('empresa_eliminada', 'Empresa eliminada');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso', 'La fecha de inicio debe ser igual o posterior a la fecha de inicio de la disponibilidad del recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_es_posible_confirmar_su_reserva', 'No es posible confirmar su reserva, solicite ayuda en forma telefónica');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_es_posible_cancelar_sus_reservas_anteriores', 'No es posible cancelar sus reservas anteriores, solicite ayuda en forma telefónica');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_tiene_una_reserva_para_el_dia', 'Ya tiene una reserva para el día');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_caracteres_escritos_no_son_correctos', 'Los caracteres escritos no coinciden con el texto que aparece en la imagen');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_completar_el_campo_campo', 'Debe completar el campo {campo}');
INSERT INTO ae_textos (codigo, texto) VALUES ('imagen_de_seguridad', 'Imagen de seguridad');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_del_usuario_es_obligatorio', 'El nombre del usuario es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_correo_electronico_del_usuario_es_obligatorio', 'El correo electrónico del usuario es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_una_agenda_con_el_nombre_especificado', 'Ya existe una agenda con el nombre especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_descripcion_de_la_agenda_es_obligatoria', 'La descripción de la agenda es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_agenda_especificada', 'No se encuentra la agenda especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_la_agenda_porque_hay_recursos_vivos', 'No se puede eliminar la agenda porque hay recursos vivos');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_la_agenda_porque_hay_reservas_vivas', 'No se puede eliminar la agenda porque hay reservas vivas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_el_recurso_porque_hay_disponibilidades_vivas', 'No se puede eliminar el recurso porque hay disponibilidades vivas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_el_recurso_porque_hay_reservas_vivas', 'No se puede eliminar el recurso porque hay reservas vivas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_el_recurso_porque_hay_agrupaciones_de_datos_vivas', 'No se puede eliminar el recurso porque hay agrupaciones de datos vivas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_el_recurso_porque_hay_validaciones_vivas', 'No se puede eliminar el recurso porque hay validaciones vivas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_el_recurso_especificado', 'No se encuentra el recurso especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_una_agrupacion_con_el_nombre_especificado', 'Ya existe una agrupación con el nombre especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_empresa_especificada', 'No se encuentra la empresa especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_agrupacion_especificada', 'No se encuentra la agrupación especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tipo_del_dato_es_obligatorio', 'El tipo del dato es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fila_del_dato_es_obligatoria', 'La fila del dato es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_columna_del_dato_es_obligatoria', 'La columna del dato es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_largo_del_dato_es_obligatorio', 'El largo del dato es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_ancho_de_despliegue_es_obligatorio', 'El ancho de despliegue es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_ancho_de_despliegue_debe_ser_mayor_a_cero', 'El ancho de despliegue debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_orden_en_el_llamador_es_obligatorio', 'El orden en el llamador es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fila_del_dato_debe_ser_mayor_a_cero', 'La fila del dato debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_un_dato_con_el_nombre_especificado', 'Ya existe un dato con el nombre especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_largo_del_dato_debe_ser_mayor_a_cero', 'El largo del dato debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_orden_en_el_llamador_debe_ser_mayor_a_cero', 'El orden en el llamador debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_valor_del_dato_es_obligatorio', 'El valor del dato es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_orden_del_dato_es_obligatorio', 'El orden del dato es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_orden_del_dato_debe_ser_mayor_a_cero', 'El orden del dato debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_el_dato_especificado', 'No se encuentra el dato especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_otro_valor_con_la_misma_etiqueta_y_valor', 'Ya existe otro valor con la misma etiqueta o valor que se solapa en el período');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_datos_para_mostrar', 'No hay datos para mostrar');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_hora_de_fin_debe_ser_posterior_a_la_hora_de_inicio', 'La hora de fin debe ser posterior a la hora de inicio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_frecuencia_debe_ser_mayor_que_cero', 'La frecuencia debe ser mayor que cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_frecuencia_es_obligatoria', 'La frecuencia es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_cupo_total_es_obligatorio', 'El cupo total es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_cupo_total_debe_ser_mayor_a_cero', 'El cupo total debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso', 'La fecha debe ser igual o posterior a la fecha de inicio de la disponibilidad del recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso', 'La fecha debe ser igual o anterior a la fecha de fin de la disponibilidad del recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_no_corresponde_a_un_dia_habil', 'La fecha no corresponde a un día hábil');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_alguna_disponibilidad_para_la_fecha_y_la_hora_de_inicio_especificadas', 'Ya existe alguna disponibilidad para la fecha y la hora de inicio especificadas');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_debe_estar_comprendida_en_el_periodo_fdesde_a_fhasta', 'La fecha debe estar comprendida en el período {fdesde} a {fhasta}');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existen_disponibilidades_para_algun_dia_en_el_periodo_especificado', 'Ya existen disponibilidades para algún día en el período especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_existen_disponibilidades_generadas_para_la_fecha_especificada', 'No existen disponibilidades generadas para la fecha especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_dias_comprendidos_en_el_periodo_debe_ser_menor_que_la_cantidad_de_dias_a_generar_para', 'La cantidad de dias comprendidos en el período debe ser menor que la cantidad de dias a generar para el recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso', 'La fecha de fin debe ser igual o anterior a la fecha de fin de la disponibilidad del recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('las_fechas_deben_estar_comprendidas_en_el_periodo_fdesde_a_fhasta_y_no_abarcar_mas_de_dias_dias', 'Las fechas deben estar comprendidas en el período {fdesde} a {fhasta} y no abarcar más de {dias} días');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_las_disponibilidades_porque_hay_reservas_vivas', 'No se puede eliminar las disponibilidades porque hay reservas vivas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_disponibilidades_para_el_periodo_especificado', 'No hay disponibilidades para el período especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_las_disponibilidades', '¿Esta seguro que desea eliminar las disponibilidades?');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_oid_de_la_empresa_es_obligatorio', 'El OID de la empresa es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('empresa_oid_ayuda', 'Este dato puede consultarlo en http://unaoid.gub.uy');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_generar_las_comunicaciones', 'No se pudo generar las comunicaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('oid', 'OID');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_reserva_especificada', 'No se encuentra la reserva especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_una_reserva_para_el_dia_especificado_con_los_datos_proporcionados', 'Ya existe una reserva para el día especificado con los datos proporcionados');
INSERT INTO ae_textos (codigo, texto) VALUES ('hay_campos_obligatorios_sin_completar', 'Hay campos obligatorios sin completar');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_campo_campo_debe_contener_solo_digitos', 'El campo {campo} debe contener solo dígitos');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_consultar_el_servicio_web', 'No se pudo consultar el servicio web');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_enviar_el_correo_porque_el_usuario_no_tiene_direccion_de_correo_electronico', 'No se puede enviar el correo porque el usuario no tiene dirección de correo electrónico');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_enviar_el_correo_electronico_de_confirmacion_tome_nota_de_los_datos_de_la_reserva', 'No se pudo enviar el correo electrónico de confirmación; tome nota de los datos de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo_de_trazabilidad', 'Código de trazabilidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('idioma', 'Idioma');
INSERT INTO ae_textos (codigo, texto) VALUES ('idiomas_soportados', 'Idiomas soportados');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_guardar_la_empresa_porque_no_existe_el_esquema', 'No se puede guardar la empresa porque no existe el origen de datos especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_modificar_las_fechas_porque_quedarian_disponibilidades_fuera_del_periodo_especificado', 'No se puede modificar las fechas porque quedarían disponibilidades fuera del período especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_modificar_las_fechas_porque_quedarian_reservas_fuera_del_periodo_especificado', 'No se puede modificar las fechas porque quedarían reservas fuera del período especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_ha_definido_ningun_idioma_valido_para_la_agenda', 'No se ha definido ningún idioma válido para la agenda');
INSERT INTO ae_textos (codigo, texto) VALUES ('establecer_como_idioma_por_defecto', 'Establecer como idioma por defecto');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_dia_y_la_hora_son_obligatorios', 'El día y la hora son obligatorios');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_numero_es_obligatorio', 'El número es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_numero_ingresado_no_es_valido', 'El número ingresado no es válido');
INSERT INTO ae_textos (codigo, texto) VALUES ('personalizacion_de_apariencia', 'Personalización de la apariencia');
INSERT INTO ae_textos (codigo, texto) VALUES ('pie_de_pagina_publico', 'Pie de página público');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_largo_en_el_llamador_es_obligatorio', 'El largo en el llamador es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_largo_en_el_llamador_debe_ser_mayor_a_cero', 'El largo en el llamador debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('sae', 'Sistema de Agenda Electrónica');
INSERT INTO ae_textos (codigo, texto) VALUES ('consultas', 'Consultas');
INSERT INTO ae_textos (codigo, texto) VALUES ('por_id.', 'Por identificador');
INSERT INTO ae_textos (codigo, texto) VALUES ('por_numero', 'Por número');
INSERT INTO ae_textos (codigo, texto) VALUES ('por_datos', 'Por datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte', 'Reporte');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_asistencia', 'Reporte de asistencia');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_atencion_funcionario', 'Reporte de atención por funcionario');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_tiempo_atencion_funcionario', 'Reporte de tiempo de atención por funcionario');
INSERT INTO ae_textos (codigo, texto) VALUES ('numero_de_la_reserva', 'Número de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('id._de_la_reserva', 'Id. de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_de_la_reserva', 'Datos de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('estado', 'Estado');
INSERT INTO ae_textos (codigo, texto) VALUES ('observaciones', 'Observaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_de_creacion', 'Fecha de creación');
INSERT INTO ae_textos (codigo, texto) VALUES ('minutos', 'Minutos');
INSERT INTO ae_textos (codigo, texto) VALUES ('id', 'Id');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_creacion', 'Fecha de creación');
INSERT INTO ae_textos (codigo, texto) VALUES ('usuario_creacion', 'Usuario de creación');
INSERT INTO ae_textos (codigo, texto) VALUES ('origen', 'Origen');
INSERT INTO ae_textos (codigo, texto) VALUES ('accion', 'Acción');
INSERT INTO ae_textos (codigo, texto) VALUES ('ver_detalle', 'Ver detalle');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_desde', 'Fecha desde');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_hasta', 'Fecha hasta');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_asistencia_por_periodo', 'Reporte de asistencia por período');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_atencion_por_periodo', 'Reporte de atención por período');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_tiempos_de_atencion', 'Reporte de tiempos de atención');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_y_numero_de_la_reserva', 'Fecha y número de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('una_pagina_por_hora', 'Una página por hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tamano_maximo_admitido_para_el_logo_es_de_1mb', 'El tamaño máximo admitido para el logo es de 1MB');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_texto_alternativo_del_logo_no_puede_estar_vacio', 'El texto alternativo del logo es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_cupos_a_aumentar_no_puede_ser_cero', 'La cantidad de cupos a aumentar no puede ser cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_cupos_a_disminuir_no_puede_ser_cero', 'La cantidad de cupos a disminuir no puede ser cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_permiten_valores_negativos', 'No se permiten valores negativos');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_campo_nombre_solo_puede_contener_letras_sin_tildes,_numeros_y__', 'El campo Nombre solo puede contener letras sin tildes, números y _');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_campo_nombre_no_puede_comenzar_con_un_numero', 'El campo Nombre no puede comenzar con un número');
INSERT INTO ae_textos (codigo, texto) VALUES ('correo_electronico_no_valido', 'La dirección de correo electrónico no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_valor_para_el_campo_cantidad_de_cupos_debe_ser_numerico', 'El valor para el campo Cantidad de cupos debe ser numérico');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_largo_del_llamador_debe_ser_mayor_a_cero', 'El largo del llamador debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_una_empresa_con_el_mismo_valor_para_origen_de_datos', 'Ya existe una empresa con el mismo origen de datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('existe_una_empresa_eliminada_con_el_mismo_valor_para_origen_de_datos', 'Ya existe una empresa eliminada con el mismo origen de datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_debe_ser_posterior_a_la_fecha_fdesde', 'La fecha de fin debe ser posterior a la fecha de inicio');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_la_empresa_porque_hay_reservas_vivas', 'No se puede eliminar la empresa porque hay reservas vivas');
INSERT INTO ae_textos (codigo, texto) VALUES ('mail_no_valido', 'La dirección de correo electrónica no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('continuar_tramite', 'Contrinuar con el trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensajes_en_el_formulario_error', 'Hay {count} errores en el formulario que debe corregir');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensajes_en_el_formulario_warn', 'Hay {count} advertencias a las cuales debe prestar atención');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensajes_en_el_formulario_info', 'Ejecución exitosa');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_orden_de_la_agrupacion_es_obligatorio', 'El órden de la agrupación es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_orden_de_la_agrupacion_debe_ser_mayor_a_cero', 'El orden de la agrupación debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_modifcar_una_agrupacion_eliminada', 'No se puede modifcar una agrupacion dada de baja');
INSERT INTO ae_textos (codigo, texto) VALUES ('cual_es_la_palabra_1_de_la_frase', '¿Cuál es la primera palabra de la frase "{frase}"?');
INSERT INTO ae_textos (codigo, texto) VALUES ('cual_es_la_palabra_2_de_la_frase', '¿Cuál es la segunda palabra de la frase "{frase}"?');
INSERT INTO ae_textos (codigo, texto) VALUES ('cual_es_la_palabra_3_de_la_frase', '¿Cuál es la tercera palabra de la frase "{frase}"?');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_agrupacion_ya_esta_eliminada', 'La agrupación ya está eliminada');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_eliminar_la_agrupación_porque_tiene_datos_asociados', 'No se puede eliminar la agrupación porque tiene datos a solicitar asociados');
INSERT INTO ae_textos (codigo, texto) VALUES ('requiere_cda', 'Control de acceso');
INSERT INTO ae_textos (codigo, texto) VALUES ('considerar_todas', 'Considerar todas');
INSERT INTO ae_textos (codigo, texto) VALUES ('cual_es_la_palabra_4_de_la_frase', '¿Cuál es la cuarta palabra de la frase "{frase}"?');
INSERT INTO ae_textos (codigo, texto) VALUES ('cual_es_la_palabra_5_de_la_frase', '¿Cuál es la quinta palabra de la frase "{frase}"?');
INSERT INTO ae_textos (codigo, texto) VALUES ('solicitar_otra_frase', 'Solicitar otra frase');
INSERT INTO ae_textos (codigo, texto) VALUES ('consulte_al_administrador_de_bases_de_datos', 'Consulte al administrador de bases de datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_existe_el_origen_de_datos', 'No existe el origen de datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_sesion_ha_expirado', 'La sesión ha expirado');
INSERT INTO ae_textos (codigo, texto) VALUES ('ha_pasado_demasiado_tiempo_desde_su_ultima_accion', 'Ha pasado demasiado tiempo desde su última acción');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_volver_al_sitio_desde_donde_accedio_para_volver_a_comenzar', 'Debe volver al sitio desde el cual accedió para volver a comenzar');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_codigo_de_la_unidad_ejecutora_es_obligatorio', 'El código de la unidad ejecutora es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_si_no_tiene_datos_asociados', 'Solo si no tiene datos asociados');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_modifcar_un_dato_eliminado', 'No se puede modifcar un dato eliminado');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensajes_login_error', 'Error de inicio de sesión');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_campo_campo_solo_puede_contener_digitos', 'El campo {campo} solo puede contener dígitos');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_es_una_direccion_de_correo_electronico_valida', 'El valor ingresado no es una dirección de correo electrónico válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_valor_ingresado_no_es_aceptable', 'El valor ingresado no es aceptable');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_respuesta_a_la_pregunta_de_seguridad_no_es_correcta', 'La respuesta a la pregunta de seguridad no es correcta');
INSERT INTO ae_textos (codigo, texto) VALUES ('olvido_su_contraseña', 'Si olvidó su contraseña póngase en contacto con soporte@agesic.com.uy');
INSERT INTO ae_textos (codigo, texto) VALUES ('cupo_por_periodo', 'Cupo por período');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_cupo_por_periodo_es_obligatorio', 'El cupo por período es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_cupo_por_periodo_debe_ser_mayor_a_cero', 'El cupo por período debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_valor_es_obligatorio', 'El valor es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_disponibilidad_especificada', 'No se encuentra la disponibilidad especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_modifcar_una_disponibilidad_eliminada', 'No se puede modifcar una disponibilidad dada de baja');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_cupo_debe_ser_mayor_o_igual_a_la_cantidad_de_reservas_existentes', 'El valor del cupo debe ser mayor o igual a la cantidad de reservas existentes');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_cupo_se_modifico_parcialmente_porque_hay_mas_reservas', 'El cupo se modificó parcialmente porque hay más reservas que el valor solicitado');
INSERT INTO ae_textos (codigo, texto) VALUES ('para_diahora_el_cupo_se_modifico_parcialmente_porque_hay_mas_reservas', 'El cupo para {diahora} se modificó parcialmente porque hay más reservas que el valor solicitado');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_identificador_de_la_reserva_es_obligatorio', 'El identificador de la reserva es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_identificador_de_la_reserva_debe_ser_numerico', 'El identificador de la reserva debe ser numérico');
INSERT INTO ae_textos (codigo, texto) VALUES ('cedula_de_identidad_invalida', 'La cédula de identidad ingresada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_se_puede_asignar_un_rol', 'Solo se puede asignar un rol al usuario en una empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_para_intranet', 'Configuración para intranet');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_de_inicio_de_la_ventana_de_intranet_descripcion', 'Cantidad de días que existen entre la fecha de hoy y la fecha en la cual se puede comenzar a hacer reservas. Por ejemplo, se puede reservar a partir de dos días desde que se ingresó al sistema.');
INSERT INTO ae_textos (codigo, texto) VALUES ('cantidad_de_dias_siguientes_disponibles', 'Cantidad de días siguientes disponibles para hacer una reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_de_inicio_de_la_ventana_de_internet_descripcion', 'Cantidad de días que existen entre la fecha de hoy y la fecha en la cual se puede comenzar a hacer reservas. Por ejemplo, se puede reservar a partir de dos días desde que se ingresó al sistema.');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_del_llamador', 'Configuración del llamador');
INSERT INTO ae_textos (codigo, texto) VALUES ('serie_asociada_a_los_numeros_de_reserva', 'Serie asociada a los números de reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirmacion_de_datos', 'Confirmación de datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tipo_de_documento_es_obligatorio', 'El tipo de documento es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_numero_de_documento_es_obligatorio', 'El número de documento es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_datos_ingresados_no_son_correctos', 'Los datos ingresados no son correctos');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_una_reserva_seleccionada', 'Debe haber una reserva seleccionada');
INSERT INTO ae_textos (codigo, texto) VALUES ('tipo_de_documento', 'Tipo de documento');
INSERT INTO ae_textos (codigo, texto) VALUES ('numero_de_documento', 'Número de documento');
INSERT INTO ae_textos (codigo, texto) VALUES ('ir_a_la_busqueda_de_reservas', 'Ir a la búsqueda de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_estado_es_obligatorio', 'El estado es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_reservas', 'Reporte de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_asistencias', 'Reporte de asistencias');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_codigo_del_tramite_es_obligatorio', 'El código del trámite es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('lugar', 'lugar');
INSERT INTO ae_textos (codigo, texto) VALUES ('lugares', 'lugares');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_del_recurso', 'Datos generales');
INSERT INTO ae_textos (codigo, texto) VALUES ('inicio_de_disponibilidad', 'Inicio de atención al público');
INSERT INTO ae_textos (codigo, texto) VALUES ('fin_de_disponibilidad', 'Fin de atención al público');
INSERT INTO ae_textos (codigo, texto) VALUES ('disponible_para_internet', 'Visible en internet');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_de_la_ventana_de_internet', 'Duración de la ventana para agendar');
INSERT INTO ae_textos (codigo, texto) VALUES ('largo_de_la_lista_de_espera', 'Número de filas en la lista de espera');
INSERT INTO ae_textos (codigo, texto) VALUES ('con_trazabilidad', 'Integrar con trazabilidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_para_internet', 'Configuración para internet');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_reserva_por_periodo_y_estado', 'Reporte de reserva por período y estado');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_codigo_de_seguridad_es_obligatorio', 'El código de cancelación es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_olvide_comunicarle_al_ciudadano_el_codigo_de_seguridad_de_la_reserva', 'No olvide comunicarle al ciudadano el código de cancelación de la reserva, ya que lo necesitará en caso de que decida cancelarla.');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_reservas_periodo', 'Reporte de reservas por período');
INSERT INTO ae_textos (codigo, texto) VALUES ('ninguna', 'Ninguna');
INSERT INTO ae_textos (codigo, texto) VALUES ('ninguno', 'Ninguno');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva', 'Reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('ha_ocurrido_un_error_grave', 'Ha ocurrido un error grave que no permite continuar con su solicitud');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_no_encontrado', 'Recurso no encontrado');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_recurso_solicitado_no_existe', 'El recurso solicitado no existe');
INSERT INTO ae_textos (codigo, texto) VALUES ('verifique_la_direccion_especificada', 'Verifique que la dirección especificada sea correcta');
INSERT INTO ae_textos (codigo, texto) VALUES ('error_no_solucionable', 'Error no solucionable');
INSERT INTO ae_textos (codigo, texto) VALUES ('ha_ocurrido_un_error_no_solucionable', 'Ha ocurrido un error no solucionable');
INSERT INTO ae_textos (codigo, texto) VALUES ('cerrar', 'Cerrar');
INSERT INTO ae_textos (codigo, texto) VALUES ('seguimiento', 'Seguimiento del trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('mostrar_numero_de_reserva_en_el_llamador', 'Visible en el llamador');
INSERT INTO ae_textos (codigo, texto) VALUES ('mostrar_numero_de_reserva_en_el_ticket', 'Mostrar serie y número');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_responder_la_pregunta_de_seguridad', 'Debe responder la pregunta de seguridad');
INSERT INTO ae_textos (codigo, texto) VALUES ('publicar_novedades', 'Publicar en PDI');
INSERT INTO ae_textos (codigo, texto) VALUES ('novedades', 'Novedades');
INSERT INTO ae_textos (codigo, texto) VALUES ('dato_es', 'Este dato es');
INSERT INTO ae_textos (codigo, texto) VALUES ('incluir', 'Incluir');
INSERT INTO ae_textos (codigo, texto) VALUES ('aplicar_a', 'Aplicar a');
INSERT INTO ae_textos (codigo, texto) VALUES ('sabado', 'Sábado');
INSERT INTO ae_textos (codigo, texto) VALUES ('tipo_usuario', 'Tipo de usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('rol', 'Rol');
INSERT INTO ae_textos (codigo, texto) VALUES ('mapa_de_locacion', 'Mapa de locación');
INSERT INTO ae_textos (codigo, texto) VALUES ('fechas_disponibles', 'Fechas disponibles');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_no_valida', 'La fecha especificada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('ingreso_electronico', 'Ingreso electrónico');
INSERT INTO ae_textos (codigo, texto) VALUES ('login_deshabilitado', 'Inicio de sesión deshabilitado');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_login_ha_sido_deshabilitado', 'El inicio de sesión local ha sido deshabilitado');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_configurar_cda', 'Debe configurar la autenticación mediante CDA');
INSERT INTO ae_textos (codigo, texto) VALUES ('ingreso_de_usuario', 'Ingreso de usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_cargar_tramites', 'No se pudo cargar la lista de trámites');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_cargar_oficinas', 'No se pudo cargar la lista de oficinas');
INSERT INTO ae_textos (codigo, texto) VALUES ('se_cargaron_n_oficinas', 'Se cargaron {cant} oficinas');
INSERT INTO ae_textos (codigo, texto) VALUES ('se_cargaron_n_tramites', 'Se cargaron {cant} tramites');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_actualizar_lista_de_organismos', 'No se pudo actualizar la lista de organismos');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_actualizar_lista_de_unidades_ejecutoras', 'No se pudo actualizar la lista de unidades ejecutoras');
INSERT INTO ae_textos (codigo, texto) VALUES ('lista_de_organismos_actualizada', 'La lista de organismos fue actualizada');
INSERT INTO ae_textos (codigo, texto) VALUES ('lista_de_unidades_ejecutas_actualizada', 'La lista de unidades ejecutoras fue actualizada');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_otro_superadmin', 'No hay otro superadministrador');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_determinar_si_hay_otro_superadministrador', 'No se pudo determinar si hay otro superadministrador');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_numero_de_puesto_no_es_valido', 'El número de puesto no es válido');
INSERT INTO ae_textos (codigo, texto) VALUES ('exportar', 'Exportar');
INSERT INTO ae_textos (codigo, texto) VALUES ('importar', 'Importar');
INSERT INTO ae_textos (codigo, texto) VALUES ('importar_recurso', 'Importar recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('archivo', 'Archivo');
INSERT INTO ae_textos (codigo, texto) VALUES ('subir_archivo', 'Subir archivo');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tamano_maximo_admitido_es_de_1mb', 'El archivo debe ser menor a 1 MB');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_cargar_el_archivo', 'No se pudo cargar el archivo');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_realizar_la_exportacion', 'No se pudo realizar la exportación');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_importado_exitosamente', 'Recusro importado exitosamente');
INSERT INTO ae_textos (codigo, texto) VALUES ('archivo_cargado', 'Archivo cargado');
INSERT INTO ae_textos (codigo, texto) VALUES ('gestionar_tokens', 'Gestionar tokens');
INSERT INTO ae_textos (codigo, texto) VALUES ('token', 'Token');
INSERT INTO ae_textos (codigo, texto) VALUES ('crear_token', 'Crear token');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_es_obligatorio', 'El nombre es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_correo_electronico_es_obligatorio', 'El correo electrónico es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('parametros_incorrectos', 'Parámetros incorrectos');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_el_token', '¿Esta seguro que desea eliminar el token?');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_empresa', 'Modificar empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_usuario', 'Modificar usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_agenda', 'Modificar agenda');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_recurso', 'Modificar recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('gestionar_agrupaciones', 'Gestionar agrupaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('realizar_reserva', 'Realizar reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccionar_agenda_recurso', 'Seleccionar agenda y recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('inicio', 'Inicio');
INSERT INTO ae_textos (codigo, texto) VALUES ('lista_de_llamadas', 'Lista de llamadas');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_por_id', 'Reserva por id');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_por_numero', 'Reserva por número');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_por_datos', 'Reserva por datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('mostrar_id_de_reserva_en_el_ticket', 'Mostrar identificador de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('id_de_la_reserva', 'Identificador de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('recargar_listado_de_organismos', 'Recargar organismos');
INSERT INTO ae_textos (codigo, texto) VALUES ('recargar_listado_de_unidades_ejecutoras', 'Recargar unidades ejecutoras');
INSERT INTO ae_textos (codigo, texto) VALUES ('recargar_listado_de_tramites', 'Recargar trámites');
INSERT INTO ae_textos (codigo, texto) VALUES ('recargar_listado_de_oficinas', 'Recargar oficinas');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_codigo_del_usuario_es_obligatorio', 'La cédula de identidad del usuario es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_campos_indicados_con_asterisco_son_obligatorios', 'Los campos indicados con * son obligatorios');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_cargar_un_archivo', 'Debe cargar un archivo');
INSERT INTO ae_textos (codigo, texto) VALUES ('archivo_no_cargado', 'Archivo no cargado');
INSERT INTO ae_textos (codigo, texto) VALUES ('puede_usar_las_siguientes_metavariables', 'Puede utilizar las siguientes metavariables');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_de_la_agenda_o_tramite', 'Nombre de la agenda o trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_del_recurso_u_oficina', 'Nombre del recurso u oficina');
INSERT INTO ae_textos (codigo, texto) VALUES ('direccion_fisica_donde_debe_concurrir', 'Dirección física donde debe concurrir el ciudadano');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_cuando_debe_concurrir', 'Fecha en la cual debe concurrir el ciudadano');
INSERT INTO ae_textos (codigo, texto) VALUES ('hora_cuando_debe_concurrir', 'Hora en la cual debe concurrir el ciudadano');
INSERT INTO ae_textos (codigo, texto) VALUES ('serie_asociada_al_recurso', 'Serie asociada al recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo_de_cancelacion_de_la_reserva', 'Código de cancelación de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo_de_trazabilidad_de_la_reserva', 'Código de trazabilidad de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('enlace_a_la_pagina_de_cancelacion', 'Enlace a la página directa de cancelación de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('identificador_de_la_reserva', 'Identificador de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('puede_ingresar_codigo_html', 'Puede ingresar código HTML');


--
-- Data for Name: ae_tokens; Type: TABLE DATA; Schema: global; Owner: sae
--



--
-- Data for Name: ae_tramites; Type: TABLE DATA; Schema: global; Owner: sae
--



--
-- Data for Name: ae_trazabilidad; Type: TABLE DATA; Schema: global; Owner: sae
--



--
-- Data for Name: ae_unidadesejecutoras; Type: TABLE DATA; Schema: global; Owner: sae
--



--
-- Data for Name: ae_usuarios; Type: TABLE DATA; Schema: global; Owner: sae
--



--
-- Name: s_ae_empresa; Type: SEQUENCE SET; Schema: global; Owner: sae
--

SELECT pg_catalog.setval('s_ae_empresa', 1, true);


--
-- Name: s_ae_novedades; Type: SEQUENCE SET; Schema: global; Owner: sae
--

SELECT pg_catalog.setval('s_ae_novedades', 1, true);


--
-- Name: s_ae_trazabilidad; Type: SEQUENCE SET; Schema: global; Owner: sae
--

SELECT pg_catalog.setval('s_ae_trazabilidad', 1, true);


--
-- Name: s_ae_usuario; Type: SEQUENCE SET; Schema: global; Owner: sae
--

SELECT pg_catalog.setval('s_ae_usuario', 1, true);


--
-- Name: ae_captchas_pk; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_captchas
    ADD CONSTRAINT ae_captchas_pk PRIMARY KEY (clave);


--
-- Name: ae_configuracion_pk; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_configuracion
    ADD CONSTRAINT ae_configuracion_pk PRIMARY KEY (clave);


--
-- Name: ae_empresas_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_empresas
    ADD CONSTRAINT ae_empresas_pkey PRIMARY KEY (id);


--
-- Name: ae_novedades_pk; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_novedades
    ADD CONSTRAINT ae_novedades_pk PRIMARY KEY (id);


--
-- Name: ae_oficinas_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_oficinas
    ADD CONSTRAINT ae_oficinas_pkey PRIMARY KEY (id);


--
-- Name: ae_oficinas_un1; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_oficinas
    ADD CONSTRAINT ae_oficinas_un1 UNIQUE (tramite_id, nombre);


--
-- Name: ae_organismos_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_organismos
    ADD CONSTRAINT ae_organismos_pkey PRIMARY KEY (id);


--
-- Name: ae_organismos_un1; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_organismos
    ADD CONSTRAINT ae_organismos_un1 UNIQUE (codigo);


--
-- Name: ae_rel_usuarios_empresas_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_rel_usuarios_empresas
    ADD CONSTRAINT ae_rel_usuarios_empresas_pkey PRIMARY KEY (usuario_id, empresa_id);


--
-- Name: ae_rel_usuarios_roles_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_rel_usuarios_roles
    ADD CONSTRAINT ae_rel_usuarios_roles_pkey PRIMARY KEY (usuario_id, empresa_id, rol_nombre);


--
-- Name: ae_textos_pk; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_textos
    ADD CONSTRAINT ae_textos_pk PRIMARY KEY (codigo);


--
-- Name: ae_tokens_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_tokens
    ADD CONSTRAINT ae_tokens_pkey PRIMARY KEY (token);


--
-- Name: ae_tramites_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_tramites
    ADD CONSTRAINT ae_tramites_pkey PRIMARY KEY (id);


--
-- Name: ae_trazabilidad_pk; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_trazabilidad
    ADD CONSTRAINT ae_trazabilidad_pk PRIMARY KEY (id);


--
-- Name: ae_unidadesejecutoras_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_unidadesejecutoras
    ADD CONSTRAINT ae_unidadesejecutoras_pkey PRIMARY KEY (id);


--
-- Name: ae_unidadesejecutoras_un1; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_unidadesejecutoras
    ADD CONSTRAINT ae_unidadesejecutoras_un1 UNIQUE (codigo);


--
-- Name: ae_usuarios_pkey; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_usuarios
    ADD CONSTRAINT ae_usuarios_pkey PRIMARY KEY (id);


--
-- Name: ae_usuarios_un1; Type: CONSTRAINT; Schema: global; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_usuarios
    ADD CONSTRAINT ae_usuarios_un1 UNIQUE (codigo);


--
-- PostgreSQL database dump complete
--

