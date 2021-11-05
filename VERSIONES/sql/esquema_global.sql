
SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA global;

ALTER SCHEMA global OWNER TO sae;

SET search_path = global, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TABLAS
--

CREATE TABLE ae_captchas (
    clave character varying(100) NOT NULL,
    fase character varying(1024)
);
ALTER TABLE ae_captchas OWNER TO sae;

CREATE TABLE ae_configuracion (
    clave character varying(100) NOT NULL,
    valor character varying(1024)
);
ALTER TABLE ae_configuracion OWNER TO sae;

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
    timezone character varying(100),
    formato_fecha character varying(25),
    formato_hora character varying(25),
    oid character varying(50),
    pie_publico text
);
ALTER TABLE ae_empresas OWNER TO sae;

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

CREATE TABLE ae_organismos (
    id integer NOT NULL,
    codigo character varying(25) NOT NULL,
    nombre character varying(100) NOT NULL
);
ALTER TABLE ae_organismos OWNER TO sae;

CREATE TABLE ae_rel_usuarios_empresas (
    usuario_id integer NOT NULL,
    empresa_id integer NOT NULL
);
ALTER TABLE ae_rel_usuarios_empresas OWNER TO sae;

CREATE TABLE ae_rel_usuarios_roles (
    usuario_id integer NOT NULL,
    empresa_id integer NOT NULL,
    rol_nombre character varying NOT NULL
);
ALTER TABLE ae_rel_usuarios_roles OWNER TO sae;

CREATE TABLE ae_textos (
    codigo character varying(100) NOT NULL,
    texto character varying(4096) NOT NULL
);
ALTER TABLE ae_textos OWNER TO sae;

CREATE TABLE ae_tokens (
    token character varying(25) NOT NULL,
    empresa_id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    fecha timestamp without time zone NOT NULL
);
ALTER TABLE ae_tokens OWNER TO sae;

CREATE TABLE ae_tramites (
    id character varying(25) NOT NULL,
    empresa_id integer,
    nombre character varying(100) NOT NULL,
    quees character varying(1000) NOT NULL,
    temas character varying(1000) NOT NULL,
    online boolean NOT NULL
);
ALTER TABLE ae_tramites OWNER TO sae;

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

CREATE TABLE ae_unidadesejecutoras (
    id integer NOT NULL,
    codigo character varying(25) NOT NULL,
    nombre character varying(100) NOT NULL
);
ALTER TABLE ae_unidadesejecutoras OWNER TO sae;

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
-- SECUENCIAS
--

CREATE SEQUENCE s_ae_empresa START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_empresa OWNER TO sae;

CREATE SEQUENCE s_ae_novedades START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_novedades OWNER TO sae;

CREATE SEQUENCE s_ae_trazabilidad START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_trazabilidad OWNER TO sae;

CREATE SEQUENCE s_ae_usuario START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_usuario OWNER TO sae;

--
-- DATOS
--

INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_WSATO_LINEA', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_WSAACTION_CABEZAL', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_WSAACTION_LINEA', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_WSATO_CABEZAL', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_WSATO', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_WSAACTION', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_TIMEOUT', '3500');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_MAXINTENTOS', '10');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_MAXINTENTOS', '10');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_TIMEOUT', '3500');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_PRODUCTOR', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_TOPICO', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_HABILITADO', 'FALSE');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_VERSION', '101');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAMITE_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAMITE_USER', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_TS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_ORG_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_ORG_KS_ALIAS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_KS_ALIAS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_ORG_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_SSL_TS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_URLSTS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_ROL', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_POLICY', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_LOCATION_LINEA', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('MOSTRAR_FECHA_ACTUAL', 'true');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_ORG_KS_ALIAS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_KS_ALIAS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_ORG_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_TS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_TS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_SSL_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_ORG_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_URLSTS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_POLICY', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_ROL', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_HABILITADO', 'false');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_XML_LOG', 'false');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAMITE_TIMEOUT', '9000');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAZABILIDAD_LOCATION_CABEZAL', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_NOVEDADES_LOCATION', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAMITE_LOCATION_GUIA', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_TRAMITE_LOCATION_INFO', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('IDIOMAS_SOPORTADOS', 'es');
INSERT INTO ae_configuracion (clave, valor) VALUES ('GOOGLE_ANALYTICS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('RESERVA_PENDIENTE_TIEMPO_MAXIMO', '10');
INSERT INTO ae_configuracion (clave, valor) VALUES ('RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO', '2880');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_MIPERFIL_HABILITADO', 'false');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_MIPERFIL_URL', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_MIPERFIL_KS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_MIPERFIL_KS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_MIPERFIL_TS_PATH', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('WS_MIPERFIL_TS_PASS', '');
INSERT INTO ae_configuracion (clave, valor) VALUES ('DOMINIO', 'https://localhost:443');
INSERT INTO ae_configuracion (clave, valor) VALUES ('JSON_ESCAPE', 'false');
INSERT INTO ae_configuracion (clave, valor) VALUES ('MIPERFIL_OID', '');
INSERT INTO ae_configuracion (clave, valor) VALUES('CARGA_MASIVA_DIAS_RECURSOS_NUEVOS', '30');
INSERT INTO ae_configuracion (clave, valor) VALUES('CARGA_MASIVA_DIAS_RECURSOS_EXISTENTES', '15');

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
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo_de_seguridad', 'Código de seguridad');
INSERT INTO ae_textos (codigo, texto) VALUES ('codigo_de_seguridad_de_la_reserva', 'Código de seguridad de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_combinacion_de_parametros_especificada_no_es_valida', 'La combinación de parámetros especificada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_registrar_un_usuario_anonimo', 'No se pudo registrar un usuario anónimo para permitir esta invocación');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_empresa_especificada_no_es_valida', 'La empresa especificada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_agenda_especificada_no_es_valida', 'La agenda especificada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_reserva_o_ya_fue_cancelada', 'No se encuentra la reserva o la misma ya fue cancelada');
INSERT INTO ae_textos (codigo, texto) VALUES ('ingrese_el_codigo_de_seguridad', 'Ingrese el código de seguridad');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_recurso_especificado_no_es_valido', 'El recurso especificado no es válido');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_ingresar_codigo_de_seguridad', 'Debe ingresar el código de seguridad');
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
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_un_horario', 'Debe seleccionar un día y una hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('ingrese_el_texto_que_aparece_en_la_imagen', 'Ingrese el texto que aparece en la imagen');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_ingresar_el_texto_que_aparece_en_la_imagen', 'Debe ingresar el texto que aparece en la imagen');
INSERT INTO ae_textos (codigo, texto) VALUES ('verificacion_de_seguridad', 'Verificación de seguridad');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_la_empresa', '¿Está seguro que desea eliminar la empresa?');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar', 'Eliminar');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelar', 'Cancelar');
INSERT INTO ae_textos (codigo, texto) VALUES ('horarios_diponibles', 'Horarios disponibles');
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
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_la_ventana_de_intranet_es_obligatorio', 'Los días de la ventana de intranet es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero', 'Los días de la ventana de intranet debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero', 'Los días de la ventana de internet debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('version', 'Versión');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_la_ventana_de_internet_es_obligatorio', 'Los días de la ventana de internet es obligatorio');
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
INSERT INTO ae_textos (codigo, texto) VALUES ('ha_pasado_demasiado_tiempo_desde_su_ultima_accion', 'Ha pasado demasiado tiempo desde su última acción');
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
INSERT INTO ae_textos (codigo, texto) VALUES ('continuar_tramite', 'Continuar con el trámite');
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
INSERT INTO ae_textos (codigo, texto) VALUES ('el_codigo_de_seguridad_es_obligatorio', 'El código de seguridad es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_olvide_comunicarle_al_ciudadano_el_codigo_de_seguridad_de_la_reserva', 'No olvide comunicarle al ciudadano el código de seguridad de la reserva, ya que lo necesitará en caso de que decida cancelarla o modificarla.');
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
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_realizada_el', 'Reserva realizada');
INSERT INTO ae_textos (codigo, texto) VALUES ('considerar_el_domingo_como_dia_habil', 'Considerar como día hábil');
INSERT INTO ae_textos (codigo, texto) VALUES ('domingo', 'Domingo');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_tramite', 'Agregar trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('quitar_tramite', 'Quitar trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_al_menos_un_tramite', 'Debe asociar al menos un trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_del_tramite_es_obligatorio', 'El nombre del trámite es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('tramites_asociados', 'Trámites asociados');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_el_tramite', 'Debe seleccionar el trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_de_la_agenda', 'Nombre de la agenda');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_del_recurso', 'Nombre del recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_del_tramite', 'Nombre del trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('mantenimiento_de_acciones', 'Mantenimiento de acciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('listado_de_acciones', 'Listado de acciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('servicio', 'Servicio');
INSERT INTO ae_textos (codigo, texto) VALUES ('host', 'Host');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_la_accion', '¿Está seguro que desea eliminar la acción?');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_accion', 'Agregar acción');
INSERT INTO ae_textos (codigo, texto) VALUES ('parametros', 'Parámetros');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_parametro', 'Agregar parámetro');
INSERT INTO ae_textos (codigo, texto) VALUES ('definir', 'Definir');
INSERT INTO ae_textos (codigo, texto) VALUES ('asociar', 'Asociar');
INSERT INTO ae_textos (codigo, texto) VALUES ('accion_creada', 'Acción creada');
INSERT INTO ae_textos (codigo, texto) VALUES ('accion_modificada', 'Acción modificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('accion_eliminada', 'Acción eliminada');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_de_la_accion_es_obligatorio', 'El nombre de la acción es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_servicio_de_la_accion_es_obligatorio', 'El servicio de la acción es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_host_de_la_accion_es_obligatorio', 'El host de la acción es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_del_parametro_es_obligatorio', 'El nombre del parámetro es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_largo_del_parametro_es_obligatorio', 'El largo del parámetro es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('asociar_acciones_a_recurso', 'Asociar acciones al recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_importado_exitosamente', 'Recurso importado correctamente');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_de_la_accion', 'Datos de la acción');
INSERT INTO ae_textos (codigo, texto) VALUES ('orden_de_ejecucion', 'Orden de ejecución');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_de_la_asignacion', 'Datos de la asignación');
INSERT INTO ae_textos (codigo, texto) VALUES ('evento', 'Evento');
INSERT INTO ae_textos (codigo, texto) VALUES ('dato_a_solicitar', 'Dato a solicitar');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_accion_es_obligatoria', 'La acción es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_orden_de_ejecucion_es_obligatorio', 'El orden de ejecución es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_orden_de_ejecucion_debe_ser_mayor_a_cero', 'El orden de ejecución debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('parametro', 'Parámetro');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_parametro_es_obligatorio', 'El parámetro es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_dato_a_solicitar_es_obligatorio', 'El dato a solicitar es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('validaciones', 'Validaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('mantenimiento_de_validaciones', 'Mantenimiento de validaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_validacion', 'Agregar validación');
INSERT INTO ae_textos (codigo, texto) VALUES ('listado_de_validaciones', 'Listado de validaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_eliminar_la_validacion', '¿Está seguro que desea eliminar la validación?');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_de_la_validacion', 'Datos de la validación');
INSERT INTO ae_textos (codigo, texto) VALUES ('validacion_creada', 'Validación creada');
INSERT INTO ae_textos (codigo, texto) VALUES ('validacion_modificada', 'Validación modificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('validacion_eliminada', 'Validación eliminada');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_de_la_validacion_es_obligatorio', 'El nombre de la validación es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_servicio_de_la_validacion_es_obligatorio', 'El servicio de la validación es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_host_de_la_validacion_es_obligatorio', 'El host de la validación es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('asociar_validaciones_a_recurso', 'Asociar acciones al recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_validacion_es_obligatoria', 'La validación es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('validacion', 'Validación');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_descripcion_de_la_accion_es_obligatoria', 'La descripción de la acción es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_de_la_accion_es_demasiado_largo', 'El nombre es demasiado largo');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_descripcion_de_la_accion_es_demasiado_larga', 'La descripción es demasiado larga');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_servicio_de_la_accion_es_demasiado_largo', 'El servicio es demasiado largo');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_host_de_la_accion_es_demasiado_largo', 'El host es demasiado largo');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_del_parametro_es_demasiado_largo', 'El nombre del parámetro es demasiado largo');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_accion_se_encuentra_asociada_a_un_recurso', 'La acción se encuentra asociada a un recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_modificar_los_parametros_si_estan_en_uso', 'No se puede modificar los parámetros si están en uso');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_descripcion_de_la_validacion_es_obligatoria', 'La descripción de la validación es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_una_accion_con_el_nombre_especificado', 'Ya existe una acción con el nombre especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_accion_especificada', 'No se encuentra la acción especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_de_la_validacion_es_demasiado_largo', 'El nombre es demasiado largo');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_servicio_de_la_validacion_es_demasiado_largo', 'El servicio es demasiado largo');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_descripcion_de_la_alidacion_es_demasiado_larga', 'La descripción es demasiado larga');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_host_de_la_validacion_es_demasiado_largo', 'El host es demasiado largo');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_existe_una_validacion_con_el_nombre_especificado', 'Ya existe una validación con el nombre especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_validacion_especificada', 'No se encuentra la validación especificada');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_validacion_se_encuentra_asociada_a_un_recurso', 'La acción se encuentra asociada a un recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('reservar', 'Reservar');
INSERT INTO ae_textos (codigo, texto) VALUES ('tipo', 'Tipo');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_codigo_y_el_nombre_del_tramite_son_obligatorios', 'El código y el nombre del trámite son obligatorios');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_al_menos_un_idioma', 'Debe seleccionar al menos un idioma');
INSERT INTO ae_textos (codigo, texto) VALUES ('agregar_asociacion', 'Agregar asociación');
INSERT INTO ae_textos (codigo, texto) VALUES ('hay_tramites_repetidos', 'Ha especificado el mismo trámite más de una vez');
INSERT INTO ae_textos (codigo, texto) VALUES ('agenda_copiada', 'Agenda copiada');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_la_disponibilidad', 'Debe especificar la disponibilidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_horario_acaba_de_quedar_sin_cupos', 'El horario seleccionado acaba de quedar sin cupos, debe elegir otro horario');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_generaron_disponibilidades_para_todos_los_horarios', 'No se generaron disponibilidades para todas las horas porque ya estaban generadas anteriormente');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_de_vigencia_es_obligatoria', 'La fecha de inicio de vigencia es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_de_vigencia_debe_ser_posterior_a_la_fecha_de_inicio_de_vigencia', 'La fecha de fin de vigencia debe ser posterior a la fecha de inicio de vigencia');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_de_disponibilidad_es_obligatoria', 'La fecha de inicio de atención al público es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_de_disponibilidad_es_obligatoria', 'La fecha de fin de atención al público es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_de_disponibilidad_debe_ser_posterior_a_la_fecha_de_disponibilidad_de_vigencia', 'La fecha de fin de atención al público debe ser posterior a la fecha de inicio de atención al público');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_de_disponibilidad_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_vigencia', 'La fecha de inicio de atención al público debe ser posterior a la fecha de inicio de vigencia');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_de_vigencia_es_obligatoria', 'La fecha de fin de vigencia es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_de_disponibilidad_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_vigencia', 'La fecha de fin de atención al público debe ser anterior a la fecha de fin de vigencia');
INSERT INTO ae_textos (codigo, texto) VALUES ('este_campo_sera_su_codigo_de_usuario', 'Este campo será su código de usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_reserva_no_corresponde_al_recurso_seleccionado', 'La reserva no corresponde al recurso seleccionado');
INSERT INTO ae_textos (codigo, texto) VALUES ('reportes', 'Reportes');
INSERT INTO ae_textos (codigo, texto) VALUES ('roles_del_usuario_por_recurso', 'Roles del usuario por recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('generador_de_reportes', 'Generador de reportes');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_a_aplicar', 'Días a los cuales aplicar');
INSERT INTO ae_textos (codigo, texto) VALUES ('lunes', 'Lunes');
INSERT INTO ae_textos (codigo, texto) VALUES ('martes', 'Martes');
INSERT INTO ae_textos (codigo, texto) VALUES ('miercoles', 'Miércoles');
INSERT INTO ae_textos (codigo, texto) VALUES ('jueves', 'Jueves');
INSERT INTO ae_textos (codigo, texto) VALUES ('viernes', 'Viernes');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_de_atencion_presencial', 'Configuración de atención presencial');
INSERT INTO ae_textos (codigo, texto) VALUES ('atencion_presencial', 'Atención presencial');
INSERT INTO ae_textos (codigo, texto) VALUES ('admite_atencion_presencial', 'Admite atención presencial');
INSERT INTO ae_textos (codigo, texto) VALUES ('cupos_por_dia', 'Cupos por día');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_cupos_por_dia_debe_ser_mayor_a_cero', 'La cantidad de cupos por día debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_inicio_de_la_ventana_de_internet_debe_ser_mayor_o_igual_a_cero', 'Los días de inicio de la ventana de internet debe ser mayor o igual a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('los_dias_de_inicio_de_la_ventana_de_intranet_debe_ser_mayor_o_igual_a_cero', 'Los días de inicio de la ventana de intranet debe ser mayor o igual a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_recurso_no_admite_atencion_presencial', 'El recurso seleccionado no admite atención presencial');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_cupos_disponibles_para_hoy', 'No hay cupos disponibles para hoy');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_la_ventana', 'Debe especificar la ventana');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_el_token_especificado', 'No se encuentra el token especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_ventana_especificada_no_es_valida', 'La ventana especificada no es válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_atencion_presencial', 'Reporte de atención presencial');
INSERT INTO ae_textos (codigo, texto) VALUES ('puesto', 'Puesto');
INSERT INTO ae_textos (codigo, texto) VALUES ('funcionario', 'Funcionario');
INSERT INTO ae_textos (codigo, texto) VALUES ('tiempo_en_minutos', 'Tiempo (mins)');
INSERT INTO ae_textos (codigo, texto) VALUES ('atencion', 'Atención');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_marcado', 'No marcado');
INSERT INTO ae_textos (codigo, texto) VALUES ('asistencias', 'Asistencias');
INSERT INTO ae_textos (codigo, texto) VALUES ('inasistencias', 'Inasistencias');
INSERT INTO ae_textos (codigo, texto) VALUES ('atenciones', 'Atenciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_para_todas_las_agendas_y_recursos', 'No tiene un recurso ni agenda seleccionada, el reporte se genera contemplando a todos los recursos y agendas');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_para_todos_los_recursos', 'No tiene un recurso seleccionado, el reporte se genera contemplando a todos los recursos de la agenda seleccionada');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_recurso_no_admite_atencion_presencial_para_hoy', 'El recurso no admite atención presencial en el día de hoy');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_recursos_disponibles_para_la_agenda_seleccionada', 'No hay recursos disponibles para la agenda seleccionada');
INSERT INTO ae_textos (codigo, texto) VALUES ('presencial', 'Presencial');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar_disponibilidades_semana', 'Eliminar disponibilidades por semana');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar_disponibilidades_periodo', 'Eliminar disponibilidades por período');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar_disponibilidades_de_todo_el_periodo', 'Eliminar disponibilidades de todo el período');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar_semana', 'Eliminar semana');
INSERT INTO ae_textos (codigo, texto) VALUES ('eliminar_periodo', 'Eliminar período');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_etiqueta_de_la_agrupacion_es_obligatoria', 'La etiqueta de la agrupación es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_lectura_si_hay_valor_en_la_url', 'Solo lectura si se especifica el valor en la URL');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelar_reserva_por_periodo', 'Cancelar reservas por período');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_cancelar_las_reservas', '¿Está seguro que desea cancelar todas las reservas del período especificado?');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelar_las_reservas_de_todo_el_periodo', 'Cancelar todas las reservas del período');
INSERT INTO ae_textos (codigo, texto) VALUES ('comunicacion_a_enviar', 'Comunicación a enviar');
INSERT INTO ae_textos (codigo, texto) VALUES ('asunto_del_mensaje', 'Asunto del mensaje');
INSERT INTO ae_textos (codigo, texto) VALUES ('cuerpo_del_mensaje', 'Cuerpo del mensaje');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_asunto_del_mensaje_es_obligatorio', 'El asunto del mensaje es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_cuerpo_del_mensaje_es_obligatorio', 'El cuerpo del mensaje es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('reservas_canceladas', 'Reservas canceladas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_enviar_comunicacion_para_las_reservas', 'No se pudo enviar mensaje de cancelación a algunas reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_una_direccion_de_correo_a_la_cual_enviar_el_mensaje', 'No hay una dirección de correo electrónico a la cual enviar el mensaje');
INSERT INTO ae_textos (codigo, texto) VALUES ('pendiente', 'Pendiente');
INSERT INTO ae_textos (codigo, texto) VALUES ('reservada', 'Reservada');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelada', 'Cancelada');
INSERT INTO ae_textos (codigo, texto) VALUES ('usada', 'Usada');
INSERT INTO ae_textos (codigo, texto) VALUES ('tipo_de_cancelacion', 'Tipo de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('usuario_de_cancelacion', 'Usuario de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_de_cancelacion', 'Fecha de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_de_la_agenda_no_es_valido', 'El nombre de la agenda no es válido');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_nombre_del_recurso_no_es_valido', 'El nombre del recurso no es válido');
INSERT INTO ae_textos (codigo, texto) VALUES ('quitar_logo', 'Quitar logo');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_al_menos_un_dia', 'Debe selecconar al menos un día de la semana');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_debe_ser_igual_o_posterior_a_hoy', 'La fecha debe ser igual o posterior a hoy');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_debe_ser_igual_o_posterior_a_hoy', 'La fecha de inicio debe ser igual o posterior a hoy');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_debe_ser_igual_o_posterior_a_hoy', 'La fecha de fin debe ser igual o posterior a hoy');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_recurso_no_esta_vigente', 'El recurso no está vigente');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_del_ticket', 'Configuración del ticket');
INSERT INTO ae_textos (codigo, texto) VALUES ('fuente', 'Fuente');
INSERT INTO ae_textos (codigo, texto) VALUES ('tamanio_fuente_chica', 'Tamaño de la fuente chica');
INSERT INTO ae_textos (codigo, texto) VALUES ('tamanio_fuente_normal', 'Tamaño de la fuente normal');
INSERT INTO ae_textos (codigo, texto) VALUES ('tamanio_fuente_grande', 'Tamaño de la fuente grande');
INSERT INTO ae_textos (codigo, texto) VALUES ('previsualizar_ticket', 'Previsualizar ticket');
INSERT INTO ae_textos (codigo, texto) VALUES ('es_la_ultima_empresa', 'Es la última empresa');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_quedar_al_menos_una_empresa', 'Debe quedar al menos una empresa viva');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_debe_ser_anterior_a_la_fecha_de_fin', 'La fecha de inicio debe ser anterior a la fecha de fin');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_realizar_la_importacion', 'No se pudo realizar la importación');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_es_invalida', 'La fecha de inicio es inválida');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_es_invalida', 'La fecha de fin es inválida');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_campo_campo_no_tiene_una_fecha_valida', 'El valor del campo {campo} no es una fecha válida');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_de_vigencia_es_invalida', 'La fecha de inicio de vigencia es inválida');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_de_vigencia_es_invalida', 'La fecha de fin de vigencia es inválida');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_inicio_de_disponibilidad_es_invalida', 'La fecha de inicio de atención al público es inválida');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_de_fin_de_disponibilidad_es_invalida', 'La fecha de fin de atenciónal público  es inválida');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_es_invalida', 'La fecha es inválida');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensajes_en_el_formulario_error', 'Hay {count} errores en el formulario');
INSERT INTO ae_textos (codigo, texto) VALUES ('pagina', 'Página');
INSERT INTO ae_textos (codigo, texto) VALUES ('pagina_anterior', 'Anterior');
INSERT INTO ae_textos (codigo, texto) VALUES ('pagina_siguiente', 'Siguiente');
INSERT INTO ae_textos (codigo, texto) VALUES ('pagina_primera', 'Primera');
INSERT INTO ae_textos (codigo, texto) VALUES ('pagina_ultima', 'Última');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_hay_cupos_disponibles_para_el_recurso', 'En la oficina seleccionada no hay cupos disponibles');
INSERT INTO ae_textos (codigo, texto) VALUES ('proximamente_se_añadiran_cupos', 'A la brevedad se añadirán cupos');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_tiene_una_reserva_para_el_dia_seleccionado', 'Ya tiene una reserva para el día seleccionado');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_se_permite_una_reserva_diaria', 'Solo se permite una reserva diaria');
INSERT INTO ae_textos (codigo, texto) VALUES ('volver_al_paso_anterior_para_seleccionar_otro_dia', 'Puede volver al paso anterior para seleccionar otro día disponible');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensaje_en_el_formulario_error', 'Hay un error en el formulario');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensaje_en_el_formulario_info', 'Ejecución exitosa');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensaje_en_el_formulario_warn', 'Hay una advertencia a la cual debe prestar atención');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_periodo_no_puede_ser_mayor_a_un_ano', 'El período no puede ser mayor a un año');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion', 'Configuración');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_global', 'Configuración global');
INSERT INTO ae_textos (codigo, texto) VALUES ('editar_configuracion', 'Editar configuración');
INSERT INTO ae_textos (codigo, texto) VALUES ('solicite_turno_para_ser_atendido', 'Solicite turno para ser atendido');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_tengo_cedula', 'No tengo cédula de identidad');
INSERT INTO ae_textos (codigo, texto) VALUES ('solicitar_turno', 'Solicitar turno');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_seleccionar_el_tipo_de_documento', 'Debe seleccionar el tipo de documento');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_ingresar_el_numero_de_documento', 'Debe ingresar el número de documento');
INSERT INTO ae_textos (codigo, texto) VALUES ('solicitud_confirmada', 'Solicitud confirmada');
INSERT INTO ae_textos (codigo, texto) VALUES ('sera_llamado_por_documento', 'Será llamado por su número de documento');
INSERT INTO ae_textos (codigo, texto) VALUES ('solicitud_de_atencion', 'Solicitud de atención');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_tiene_acceso_al_recurso_o_no_existe', 'No tiene acceso al recurso solicitado o éste no existe');
INSERT INTO ae_textos (codigo, texto) VALUES ('si_error_contacte_al_administrador', 'Si cree que es un error contacte al administrador');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_de_reserva_multiple', 'Configuración de reserva múltiple');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_multiple', 'Reserva múltiple');
INSERT INTO ae_textos (codigo, texto) VALUES ('admite_reserva_multiple', 'Admite reserva múltiple');
INSERT INTO ae_textos (codigo, texto) VALUES ('reservas_incluidas', 'Reservas incluidas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_es_posible_cambiar_de', 'No es posible cambiar de');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_hay_una_reserva', 'Ya hay una reserva añadida');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirmar_reservas', 'Confirmar reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirmacion_de_reservas', 'Confirmación de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('reservas_confirmadas', 'Todas las reservas están confirmadas');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_y_hora', 'Fecha y hora');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccionar_recurso', 'Seleccionar recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('otra_reserva', 'Otra reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cedula_es_obligatoria', 'La cédula de identidad es requerida');
INSERT INTO ae_textos (codigo, texto) VALUES ('identificacion', 'Identificación');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelar_reservas', 'Cancelar reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_token_esta_cancelado', 'Las reservas ya están canceladas');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_token_esta_confirmado', 'Las reservas ya están confirmadas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_es_posible_cambiar_de_recurso', 'No es posible cambiar de recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_el_token', 'No se encuentra el token especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_token_ha_expirado', 'El token especificado ha expirado');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_es_posible_cambiar_de_tramite', 'No es posible cambiar de trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirma_cancelar_las_reservas', '¿Confirma la cancelación de todas las reservas?');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_recurso_no_admite_reservas_multiples', 'El recurso indicado no admite reservas múltiples');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_el_token_de_reservas', 'Debe especificar el token de reservas múltiples');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_reserva_no_corresponde_al_token', 'La reserva no corresponde al token');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_original', 'Reserva original');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccionar_reserva', 'Seleccionar reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_nueva', 'Reserva nueva');
INSERT INTO ae_textos (codigo, texto) VALUES ('modificar_reserva', 'Modificar reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_los_datos_personales', 'Debe especificar los datos personales');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_el_tramite', 'Debe especificar el trámite');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_el_token_de_reservas_especificado', 'No se encuentra el token de reservas especificado');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_el_token', 'Debe especificar el token');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_reserva_esta_utilizada', 'La reserva está utilizada');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_de_cambios_de_reservas', 'Configuración de cambios de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('cambios_reserva', 'Cambios de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('admite_cambios_reserva', 'Admite cambios de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('tiempo_previo', 'Tiempo previo');
INSERT INTO ae_textos (codigo, texto) VALUES ('tiempo_unidad', 'Unidad de tiempo');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias', 'Días');
INSERT INTO ae_textos (codigo, texto) VALUES ('horas', 'Horas');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tiempo_previo_para_cambios_es_requerido', 'El tiempo previo para cambios es requerido');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tiempo_previo_para_cambios_debe_ser_mayor_a_cero', 'El tiempo previo para cambios debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('enlace_a_la_pagina_de_modificacion', 'Enlace a la página de modificación');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_recurso_no_admite_cambios_de_reservas', 'El recurso no admite cambios de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_reserva_especificada_ya_no_admite_cambios', 'La reserva ya no admite cambios');
INSERT INTO ae_textos (codigo, texto) VALUES ('periodo_validacion', 'Período de validación');
INSERT INTO ae_textos (codigo, texto) VALUES ('ya_tiene_una_reserva_para_el_periodo', 'Ya tiene una reserva para el período {periodo}');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_periodo_de_validacion_es_obligatorio', 'El período de validación es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_periodo_de_validacion_debe_ser_mayor_o_igual_a_cero', 'El período de validación debe ser mayor o igual a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('solo_se_permite_una_reserva_en_un_periodo_de_dias', 'Solo se permite una reserva en un período de {dias} días');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_de_validacion_por_ip', 'Configuración de validación por dirección IP');
INSERT INTO ae_textos (codigo, texto) VALUES ('validar_por_ip', 'Validar por dirección IP');
INSERT INTO ae_textos (codigo, texto) VALUES ('cantidad_reservas_por_ip', 'Cantidad de reservas por dirección IP');
INSERT INTO ae_textos (codigo, texto) VALUES ('periodo_validacion_por_ip', 'Período de validación');
INSERT INTO ae_textos (codigo, texto) VALUES ('direcciones_ip_sin_validacion', 'Direcciones IP sin validación');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_reservas_por_ip_es_obligatoria', 'La cantidad de reservas por IP es obligatoria');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_cantidad_de_reservas_por_ip_debe_ser_mayor_a_cero', 'La cantidad de reservas por IP debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_periodo_de_validacion_por_ip_es_obligatorio', 'El período de validación por IP es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_periodo_de_validacion_por_ip_debe_ser_mayor_o_igual_a_cero', 'El período de validación por IP debe ser mayor o igual a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('limite_de_reservas_para_la_direccion_ip_alcanzado', 'No se admiten más de {cantidad} reservas desde una misma dirección IP');
INSERT INTO ae_textos (codigo, texto) VALUES ('consulta_de_cancelaciones', 'Consulta de cancelaciones');
INSERT INTO ae_textos (codigo, texto) VALUES ('creacion', 'Creación');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelacion', 'Cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('todos', 'Todos');
INSERT INTO ae_textos (codigo, texto) VALUES ('todas', 'Todas');
INSERT INTO ae_textos (codigo, texto) VALUES ('cantidad_de_elementos', 'Cantidad de elementos');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_de_cancelaciones_de_reservas', 'Configuración de cancelaciones de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('cancelaciones_de_reservas', 'Cancelaciones de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('tipo_liberacion_cupo', 'Tipo de liberación del cupo');
INSERT INTO ae_textos (codigo, texto) VALUES ('inmediata', 'Inmediata');
INSERT INTO ae_textos (codigo, texto) VALUES ('diferida', 'Diferida');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tiempo_previo_para_cancelaciones_es_requerido', 'El tiempo previo para cancelaciones es requerido');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tiempo_previo_para_cancelaciones_debe_ser_mayor_o_igual_a_cero', 'El tiempo previo para cancelaciones debe ser mayor o igual a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('el_tipo_de_cancelacion_es_obligatorio', 'El tipo de cancelación es obligatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('ha_expirado_el_plazo_de_cancelacion', 'Ha expirado el plazo de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_de_liberacion', 'Fecha de liberación');
INSERT INTO ae_textos (codigo, texto) VALUES ('liberar', 'Liberar');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_reserva_o_no_esta_cancelada', 'No se encuentra la reserva o no está cancelada');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_liberada', 'El cupo de la reserva ha sido liberado');
INSERT INTO ae_textos (codigo, texto) VALUES ('esta_seguro_que_desea_liberar_el_cupo_de_la_reserva', '¿Está seguro que desea liberar el cupo de la reserva?');
INSERT INTO ae_textos (codigo, texto) VALUES ('configuracion_de_mi_perfil', 'Configuración de Mi Perfil');
INSERT INTO ae_textos (codigo, texto) VALUES ('enviar_aviso_al_confirmar', 'Enviar un aviso al confirmar');
INSERT INTO ae_textos (codigo, texto) VALUES ('enviar_aviso_al_cancelar', 'Enviar un aviso al cancelar');
INSERT INTO ae_textos (codigo, texto) VALUES ('enviar_aviso_recordatorio', 'Enviar un recordatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_enviar_notificacion_de_confirmacion_tome_nota_de_los_datos_de_la_reserva', 'No se pudo enviar una notificación al usuario; tome nota de los datos de la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_enviar_notificacion_de_cancelacion', 'No se pudo enviar una notificación al usuario');
INSERT INTO ae_textos (codigo, texto) VALUES ('acc_titulo', 'Título');
INSERT INTO ae_textos (codigo, texto) VALUES ('acc_url', 'URL');
INSERT INTO ae_textos (codigo, texto) VALUES ('acc_destacada', 'Destacada');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_una_unica_accion_de_confirmacion_destacada', 'Debe haber una (y solo una) acción de aviso al confirmar marcada como destacada, cuya URL no sea vacía.');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_una_unica_accion_de_cancelacion_destacada', 'Debe haber una (y solo una) acción de aviso al cancelar marcada como destacada, cuya URL no sea vacía.');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_haber_una_unica_accion_de_recordatorio_destacada', 'Debe haber una (y solo una) acción de recordatorio marcada como destacada, cuya URL no sea vacía');
INSERT INTO ae_textos (codigo, texto) VALUES ('vencimiento_aviso_al_confirmar', 'Vencimiento de aviso al confirmar');
INSERT INTO ae_textos (codigo, texto) VALUES ('vencimiento_aviso_al_cancelar', 'Vencimiento de aviso al cancelar');
INSERT INTO ae_textos (codigo, texto) VALUES ('vencimiento_recordatorio', 'Vencimiento de recordatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('hora_recordatorio', 'Hora de recordatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('dias_recordatorio', 'Días de recordatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('textos_aviso_al_confirmar', 'Textos aviso al confirmar');
INSERT INTO ae_textos (codigo, texto) VALUES ('textos_aviso_al_cancelar', 'Textos aviso al cancelar');
INSERT INTO ae_textos (codigo, texto) VALUES ('textos_recordatorio', 'Textos recordatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_titulo_aviso_al_confirmar', 'Título del aviso al confirmar');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_corto_aviso_al_confirmar', 'Texto corto del aviso al confirmar');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_largo_aviso_al_confirmar', 'Texto largo del aviso al confirmar');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_titulo_aviso_al_cancelar', 'Título del aviso al cancelar');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_corto_aviso_al_cancelar', 'Texto corto del aviso al cancelar');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_largo_aviso_al_cancelar', 'Texto largo del aviso al cancelar');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_titulo_recordatorio', 'Título del recordatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_corto_recordatorio', 'Texto corto del recordatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_largo_recordatorio', 'Texto largo del recordatorio');
INSERT INTO ae_textos (codigo, texto) VALUES ('administradorDeRecursos', 'Administrador de recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('reporte_recursos_por_agenda','Reporte de Recursos por Agenda');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_hasta_debe_ser_posterior_a_la_fecha_desde', 'La fecha hasta debe ser posterior a la fecha desde');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_la_fecha_reserva_dos', 'Debe especificar la fecha para la reserva de la segunda dosis');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_el_tipo_doc_reserva_dos', 'Debe especificar el tipo de documento para la reserva de la segunda dosis');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_encuentra_la_disponibilidad2_especificada', 'No se encuentra la disponibilidad para la reserva de la segunda dosis');
INSERT INTO ae_textos (codigo, texto) VALUES ('error_marcar_reservas_pares', 'Error: no se ha podido crear las reservas de primera y segunda dosis');
INSERT INTO ae_textos (codigo, texto) VALUES ('debe_especificar_la_reserva_segunda', 'Debe especificar la reserva de la segunda dosis');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_reserva_primer_dosis_esta_utilizada', 'La reserva de primer dosis está utilizada');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puede_cancelar_la_reserva_dos', 'No se puede cancelar la reserva de la segunda dosis por si sola, la cancelación se hace mediante la reserva de la primer dosis y el sistema cancela ambas reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_existen_reservas_para_confirmar', 'No existen reservas para confirmar');
INSERT INTO ae_textos (codigo, texto) VALUES ('envio_novedades_como_dato_extra', 'Envío a novedades como dato extra');
INSERT INTO ae_textos (codigo, texto) VALUES ('la_fecha_reserva_dos_debe_ser_posterior_a_la_fecha_reserva_uno', 'La fecha de la reserva dos debe ser posterior a la fecha de la reserva uno');
INSERT INTO ae_textos (codigo, texto) VALUES ('error_marcar_reserva', 'Error: no se ha podido crear la reserva');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_pendiente_tiempo_max', 'Reserva pendiente tiempo máximo');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_multiple_pendiente_tiempo_max', 'Reserva múltiple pendiente tiempo máximo');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_pendiente_tiempo_max_debe_ser_mayor_a_cero', 'El tiempo máximo para una reserva pendiente debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('reserva_multiple_pendiente_tiempo_max_debe_ser_mayor_a_cero', 'El tiempo máximo para las reservas múltiples pendientes debe ser mayor a cero');
INSERT INTO ae_textos (codigo, texto) VALUES ('mover_reservas', 'Mover reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('datos_de_reservas_a_mover', 'Datos de las reservas a mover');
INSERT INTO ae_textos (codigo, texto) VALUES ('agenda_origen', 'Agenda origen');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_origen', 'Recurso origen');
INSERT INTO ae_textos (codigo, texto) VALUES ('agenda_destino', 'Agenda destino');
INSERT INTO ae_textos (codigo, texto) VALUES ('recurso_destino', 'Recurso destino');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_reservas', 'Fecha de las reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('enviar_correo', 'Enviar notificaciones a los usuarios');
INSERT INTO ae_textos (codigo, texto) VALUES ('generar_novedades', 'Generar novedades');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_cargar_lista_de_agendas', 'No se pudo cargar la lista de agendas');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_pudo_cargar_lista_de_recursos', 'No se pudo cargar la lista de recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirmar_movimiento_reservas', 'Confirmar movimiento de reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('ejecutar', 'Ejecutar');
INSERT INTO ae_textos (codigo, texto) VALUES ('validar', 'Validar');
INSERT INTO ae_textos (codigo, texto) VALUES ('correo_de_traslado', 'Correo de cambio de recurso');
INSERT INTO ae_textos (codigo, texto) VALUES ('texto_para_el_correo_de_traslado', 'Texto para el correo de traslado');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_de_la_agenda_origen', 'Nombre de la agenda de origen');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_del_recurso_origen', 'Nombre del recurso de origen');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_de_la_agenda_destino', 'Nombre de la agenda de destino');
INSERT INTO ae_textos (codigo, texto) VALUES ('nombre_del_recurso_destino', 'Nombre del recurso de destino');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_se_puedo_mover_las_reservas', 'No se pudo mover todas las reservas');
INSERT INTO ae_textos (codigo, texto) VALUES ('paso_uno_datos_reservas_mover', 'Paso 1 - Datos de las reservas a mover');
INSERT INTO ae_textos (codigo, texto) VALUES ('paso_dos_datos_reservas_mover', 'Paso 2 - Datos del recurso destino de las reservas');
INSERT INTO ae_textos(codigo, texto)  VALUES('carga_masiva_recursos_disponibilidades', 'Carga masiva de recursos y disponibilidades');
INSERT INTO ae_textos(codigo, texto)  VALUES('no_existen_reservas_recurso_origen', 'No existen reservas a mover en la fecha y horario seleccionado');
INSERT INTO ae_textos(codigo, texto)  VALUES('recurso_origen_recurso_destino_hora_inicio_distintas', 'Si el recurso origen es igual al recurso destino, las horas inicio deben ser distintas');
INSERT INTO ae_textos(codigo, texto) VALUES('no_se_validar_las_reservas', 'No se ha podido validar en el paso 2');
INSERT INTO ae_textos (codigo, texto) VALUES('debe_seleccionar_agenda_recurso_destino', 'Debe seleccionar agenda destino y recurso destino');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_destino', 'Fecha destino');
INSERT INTO ae_textos (codigo, texto) VALUES ('fecha_destino_vacia', 'Seleccione una fecha destino');
INSERT INTO ae_textos (codigo, texto) VALUES ('envio_de_comunicacion', 'Envío de comunicación');
INSERT INTO ae_textos (codigo, texto) VALUES ('aplica_envio_de_correo', 'Aplica envío de correo electrónico');
INSERT INTO ae_textos (codigo, texto) VALUES ('actualizacion_masiva', 'Actualización Masiva');
INSERT INTO ae_textos (codigo, texto) VALUES ('no_enviar_comunicacion_cancelacion', 'Tenga en cuenta que no se enviará una comunicación de cancelación');
INSERT INTO ae_textos (codigo, texto) VALUES ('todos_los_recursos', 'Todos los recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('agendas_recursos', 'Agendas y Recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('aplicar_todos', 'Aplicar a todos');
INSERT INTO ae_textos (codigo, texto) VALUES ('seleccionar_recursos', 'Seleccionar Recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('aplicar_todos_recursos', 'Aplicar a todos los recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('mensaje_aplica_para_todos', 'En caso de estar seleccionado afectará a todos los recursos de todas las agendas de la empresa seleccionada.');
INSERT INTO ae_textos (codigo, texto) VALUES ('recursos_agenda', 'Recursos de la agenda');
INSERT INTO ae_textos (codigo, texto) VALUES ('recursos_confirmados', 'Recursos confirmados');
INSERT INTO ae_textos (codigo, texto) VALUES ('confirmar_recursos', 'Confirmar recursos');
INSERT INTO ae_textos (codigo, texto) VALUES ('actualizacion_recursos', 'Se han modificado x recursos de la agenda actualmente seleccionada');
INSERT INTO ae_textos (codigo, texto) VALUES ('actualizar_datos', 'Actualizar datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('actualizacion_masiva_datos', 'Actualización masiva de datos');
INSERT INTO ae_textos (codigo, texto) VALUES ('carga_masiva', 'Carga Masiva');
UPDATE ae_textos SET texto='Correo de cambio de recurso' WHERE codigo='correo_de_traslado';
UPDATE ae_textos SET texto='Tenga en cuenta que no se ha podido enviar la notificación por correo electrónico; tome nota de los datos de la reserva.' WHERE codigo='no_se_pudo_enviar_notificacion_de_confirmacion_tome_nota_de_los_datos_de_la_reserva';
update global.ae_textos set texto='Se han modificado x recursos' where codigo = 'actualizacion_recursos';
INSERT INTO global.ae_textos(codigo, texto) VALUES ('reporte_errores_actualizacion_masiva_reservas', 'Reporte de errores de actualización masiva de recursos');

--
-- CLAVES PRIMARIAS
--

ALTER TABLE ONLY ae_captchas ADD CONSTRAINT ae_captchas_pk PRIMARY KEY (clave);
ALTER TABLE ONLY ae_configuracion ADD CONSTRAINT ae_configuracion_pk PRIMARY KEY (clave);
ALTER TABLE ONLY ae_empresas ADD CONSTRAINT ae_empresas_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_novedades ADD CONSTRAINT ae_novedades_pk PRIMARY KEY (id);
ALTER TABLE ONLY ae_oficinas ADD CONSTRAINT ae_oficinas_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_organismos ADD CONSTRAINT ae_organismos_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_rel_usuarios_empresas ADD CONSTRAINT ae_rel_usuarios_empresas_pkey PRIMARY KEY (usuario_id, empresa_id);
ALTER TABLE ONLY ae_rel_usuarios_roles ADD CONSTRAINT ae_rel_usuarios_roles_pkey PRIMARY KEY (usuario_id, empresa_id, rol_nombre);
ALTER TABLE ONLY ae_textos ADD CONSTRAINT ae_textos_pk PRIMARY KEY (codigo);
ALTER TABLE ONLY ae_tokens ADD CONSTRAINT ae_tokens_pkey PRIMARY KEY (token);
ALTER TABLE ONLY ae_tramites ADD CONSTRAINT ae_tramites_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_trazabilidad ADD CONSTRAINT ae_trazabilidad_pk PRIMARY KEY (id);
ALTER TABLE ONLY ae_unidadesejecutoras ADD CONSTRAINT ae_unidadesejecutoras_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_usuarios ADD CONSTRAINT ae_usuarios_pkey PRIMARY KEY (id);

--
--  CLAVES UNICAS
--

ALTER TABLE ONLY ae_oficinas ADD CONSTRAINT ae_oficinas_un1 UNIQUE (tramite_id, nombre);
ALTER TABLE ONLY ae_organismos ADD CONSTRAINT ae_organismos_un1 UNIQUE (codigo);
ALTER TABLE ONLY ae_unidadesejecutoras ADD CONSTRAINT ae_unidadesejecutoras_un1 UNIQUE (codigo);
ALTER TABLE ONLY ae_usuarios ADD CONSTRAINT ae_usuarios_un1 UNIQUE (codigo);

--
-- INDICES
--

CREATE INDEX ae_novedades_intentos_idx ON "global".ae_novedades (intentos);
CREATE INDEX ae_novedades_enviado_idx ON "global".ae_novedades (enviado);

--
-- FIN
--
