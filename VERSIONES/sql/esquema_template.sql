
SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA {esquema};

ALTER SCHEMA {esquema} OWNER TO sae;

SET search_path = {esquema}, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TABLAS
--

CREATE TABLE ae_acciones (
    id integer NOT NULL,
    descripcion character varying(100) NOT NULL,
    fecha_baja timestamp without time zone,
    host character varying(100),
    nombre character varying(50) NOT NULL,
    servicio character varying(250) NOT NULL
);
ALTER TABLE ae_acciones OWNER TO sae;

CREATE TABLE ae_acciones_por_dato (
    id integer NOT NULL,
    fecha_desasociacion timestamp without time zone,
    nombre_parametro character varying(50) NOT NULL,
    aear_id integer NOT NULL,
    aeds_id integer NOT NULL
);
ALTER TABLE ae_acciones_por_dato OWNER TO sae;

CREATE TABLE ae_acciones_por_recurso (
    id integer NOT NULL,
    evento character varying(1) NOT NULL,
    fecha_baja timestamp without time zone,
    orden_ejecucion integer,
    aeac_id integer NOT NULL,
    aere_id integer NOT NULL
);
ALTER TABLE ae_acciones_por_recurso OWNER TO sae;

CREATE TABLE ae_agendas (
    id integer NOT NULL,
    descripcion character varying(1000) NOT NULL,
    fecha_baja timestamp without time zone,
    nombre character varying(100) NOT NULL,
    tramite_id character varying(25),
    timezone character varying(100),
    idiomas character varying(100),
    con_cda boolean DEFAULT false,
    tramite_codigo character varying(10),
    publicar_novedades boolean DEFAULT false NOT NULL,
    con_trazabilidad boolean DEFAULT false NOT NULL
);
ALTER TABLE ae_agendas OWNER TO sae;

CREATE TABLE ae_agrupaciones_datos (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    nombre character varying(50) NOT NULL,
    orden integer NOT NULL,
    aere_id integer NOT NULL,
    borrar_flag boolean NOT NULL,
    etiqueta character varying(50)
);
ALTER TABLE ae_agrupaciones_datos OWNER TO sae;

CREATE TABLE ae_anios (
    id integer NOT NULL,
    anio integer NOT NULL,
    aepl_id integer NOT NULL
);
ALTER TABLE ae_anios OWNER TO sae;

CREATE TABLE ae_atencion (
    id integer NOT NULL,
    asistio boolean NOT NULL,
    duracion integer NOT NULL,
    fact timestamp without time zone NOT NULL,
    fcrea timestamp without time zone NOT NULL,
    funcionario character varying(255) NOT NULL,
    aers_id integer NOT NULL
);
ALTER TABLE ae_atencion OWNER TO sae;

CREATE TABLE ae_comunicaciones (
    id integer NOT NULL,
    tipo_1 character varying(25) NOT NULL,
    tipo_2 character varying(25) NOT NULL,
    destino character varying(100) NOT NULL,
    recurso_id integer NOT NULL,
    reserva_id integer,
    token_id integer,
    procesado boolean DEFAULT false NOT NULL,
    mensaje character varying(4000)
);
ALTER TABLE ae_comunicaciones OWNER TO sae;

CREATE TABLE ae_constante_validacion (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    largo integer NOT NULL,
    nombre character varying(50) NOT NULL,
    tipo character varying(30) NOT NULL,
    aeva_id integer NOT NULL
);
ALTER TABLE ae_constante_validacion OWNER TO sae;

CREATE TABLE ae_datos_a_solicitar (
    id integer NOT NULL,
    ancho_despliegue integer NOT NULL,
    columna integer NOT NULL,
    es_clave boolean NOT NULL,
    etiqueta character varying(50) NOT NULL,
    fecha_baja timestamp without time zone,
    fila integer NOT NULL,
    incluir_en_llamador boolean NOT NULL,
    incluir_en_reporte boolean NOT NULL,
    largo integer,
    largo_en_llamador integer NOT NULL,
    nombre character varying(50) NOT NULL,
    orden_en_llamador integer NOT NULL,
    requerido boolean NOT NULL,
    texto_ayuda character varying(100),
    tipo character varying(30) NOT NULL,
    aead_id integer NOT NULL,
    aere_id integer NOT NULL,
    borrar_flag boolean DEFAULT true NOT NULL,
    solo_lectura boolean DEFAULT false NOT NULL,
    incluir_en_novedades boolean DEFAULT false NOT NULL
);
ALTER TABLE ae_datos_a_solicitar OWNER TO sae;

CREATE TABLE ae_datos_del_recurso (
    id integer NOT NULL,
    etiqueta character varying(50) NOT NULL,
    orden integer NOT NULL,
    valor character varying(100) NOT NULL,
    aere_id integer NOT NULL
);
ALTER TABLE ae_datos_del_recurso OWNER TO sae;

CREATE TABLE ae_datos_reserva (
    id integer NOT NULL,
    valor character varying(100) NOT NULL,
    aeds_id integer NOT NULL,
    aers_id integer NOT NULL
);
ALTER TABLE ae_datos_reserva OWNER TO sae;

CREATE TABLE ae_dias_del_mes (
    id integer NOT NULL,
    dia_del_mes integer NOT NULL,
    aepl_id integer NOT NULL
);
ALTER TABLE ae_dias_del_mes OWNER TO sae;

CREATE TABLE ae_dias_semana (
    id integer NOT NULL,
    dia_semana integer NOT NULL,
    aepl_id integer NOT NULL
);
ALTER TABLE ae_dias_semana OWNER TO sae;

CREATE TABLE ae_disponibilidades (
    id integer NOT NULL,
    cupo integer NOT NULL,
    fecha date NOT NULL,
    fecha_baja timestamp without time zone,
    hora_fin timestamp without time zone NOT NULL,
    hora_inicio timestamp without time zone NOT NULL,
    numerador integer NOT NULL,
    version integer NOT NULL,
    aepl_id integer,
    aere_id integer NOT NULL,
    presencial boolean DEFAULT false NOT NULL
);
ALTER TABLE ae_disponibilidades OWNER TO sae;

CREATE TABLE ae_frases_captcha (
    clave character varying(100) NOT NULL,
    frase character varying(1024),
    idioma character varying(5) NOT NULL
);
ALTER TABLE ae_frases_captcha OWNER TO sae;

CREATE TABLE ae_llamadas (
    id integer NOT NULL,
    etiqueta character varying(100) NOT NULL,
    fecha date NOT NULL,
    hora timestamp without time zone NOT NULL,
    numero integer NOT NULL,
    puesto integer NOT NULL,
    aere_id integer NOT NULL,
    aers_id integer NOT NULL
);
ALTER TABLE ae_llamadas OWNER TO sae;

CREATE TABLE ae_meses (
    id integer NOT NULL,
    mes integer NOT NULL,
    aepl_id integer NOT NULL
);
ALTER TABLE ae_meses OWNER TO sae;

CREATE TABLE ae_parametros_accion (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    largo integer,
    nombre character varying(50) NOT NULL,
    tipo character varying(30),
    aeac_id integer NOT NULL
);
ALTER TABLE ae_parametros_accion OWNER TO sae;

CREATE TABLE ae_parametros_autocompletar (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    modo integer NOT NULL,
    nombre character varying(50) NOT NULL,
    tipo character varying(30) NOT NULL,
    aeserv_id integer NOT NULL
);
ALTER TABLE ae_parametros_autocompletar OWNER TO sae;

CREATE TABLE ae_parametros_validacion (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    largo integer,
    nombre character varying(50) NOT NULL,
    tipo character varying(30),
    aeva_id integer NOT NULL
);
ALTER TABLE ae_parametros_validacion OWNER TO sae;

CREATE TABLE ae_plantillas (
    id integer NOT NULL,
    cupo integer NOT NULL,
    fecha_baja timestamp without time zone,
    frecuencia integer NOT NULL,
    generacion_automatica boolean NOT NULL,
    hora_fin timestamp without time zone NOT NULL,
    hora_inicio timestamp without time zone NOT NULL,
    aere_id integer NOT NULL
);
ALTER TABLE ae_plantillas OWNER TO sae;

CREATE TABLE ae_preguntas_captcha (
    clave character varying(100) NOT NULL,
    pregunta character varying(1024),
    respuesta character varying(25),
    idioma character varying(5) NOT NULL
);
ALTER TABLE ae_preguntas_captcha OWNER TO sae;

CREATE TABLE ae_recursos (
    id integer NOT NULL,
    cant_dias_a_generar integer NOT NULL,
    descripcion character varying(1000) NOT NULL,
    dias_inicio_ventana_internet integer NOT NULL,
    dias_inicio_ventana_intranet integer NOT NULL,
    dias_ventana_internet integer NOT NULL,
    dias_ventana_intranet integer NOT NULL,
    fecha_baja timestamp without time zone,
    fecha_fin timestamp without time zone,
    fecha_fin_disp timestamp without time zone,
    fecha_inicio timestamp without time zone NOT NULL,
    fecha_inicio_disp timestamp without time zone NOT NULL,
    largo_lista_espera integer,
    mostrar_numero_en_llamador boolean NOT NULL,
    mostrar_numero_en_ticket boolean NOT NULL,
    nombre character varying(100) NOT NULL,
    sabado_es_habil boolean NOT NULL,
    serie character varying(3),
    usar_llamador boolean NOT NULL,
    ventana_cupos_minimos integer NOT NULL,
    version integer NOT NULL,
    visible_internet boolean NOT NULL,
    aeag_id integer NOT NULL,
    oficina_id character varying(25),
    direccion character varying(100),
    localidad character varying(100),
    departamento character varying(100),
    telefonos character varying(100),
    horarios character varying(100),
    latitud numeric,
    longitud numeric,
    mostrar_id_en_ticket boolean,
    domingo_es_habil boolean DEFAULT false NOT NULL,
    presencial_admite boolean DEFAULT false NOT NULL,
    presencial_lunes boolean DEFAULT false NOT NULL,
    presencial_martes boolean DEFAULT false NOT NULL,
    presencial_miercoles boolean DEFAULT false NOT NULL,
    presencial_jueves boolean DEFAULT false NOT NULL,
    presencial_viernes boolean DEFAULT false NOT NULL,
    presencial_sabado boolean DEFAULT false NOT NULL,
    presencial_domingo boolean DEFAULT false NOT NULL,
    presencial_cupos integer DEFAULT 0 NOT NULL,
    tamanio_fuente_grande integer DEFAULT 12 NOT NULL,
    tamanio_fuente_normal integer DEFAULT 10 NOT NULL,
    tamanio_fuente_chica integer DEFAULT 8 NOT NULL,
    fuente_ticket character varying(100) DEFAULT 'Helvetica-Bold'::character varying NOT NULL,
    multiple_admite boolean NOT NULL DEFAULT false,
    cambios_admite boolean NOT NULL DEFAULT false,
    cambios_tiempo integer DEFAULT null,
    cambios_unidad integer DEFAULT null,
    periodo_validacion integer NOT NULL DEFAULT 0,
    validar_por_ip boolean NOT NULL DEFAULT false,
    cantidad_por_ip integer DEFAULT null,
    periodo_por_ip integer DEFAULT null,
    ips_sin_validacion varchar(4000) DEFAULT NULL,
    cancela_tiempo integer NOT NULL DEFAULT 0,
    cancela_unidad integer NOT NULL DEFAULT 12,
    cancela_tipo varchar(1) NOT NULL DEFAULT 'I',
    mi_perfil_con_hab bool NOT NULL DEFAULT false,
  mi_perfil_can_hab bool NOT NULL DEFAULT false,
  mi_perfil_rec_hab bool NOT NULL DEFAULT false,
  mi_perfil_con_tit varchar(255) NULL DEFAULT NULL,
  mi_perfil_con_cor varchar(500) NULL DEFAULT NULL,
  mi_perfil_con_lar varchar(1024) NULL DEFAULT NULL,
  mi_perfil_con_ven int4 NULL,
  mi_perfil_can_tit varchar(255) NULL DEFAULT NULL,
  mi_perfil_can_cor varchar(500) NULL DEFAULT NULL,
  mi_perfil_can_lar varchar(1024) NULL DEFAULT NULL,
  mi_perfil_can_ven int4 NULL,
  mi_perfil_rec_tit varchar(255) NULL DEFAULT NULL,
  mi_perfil_rec_cor varchar(500) NULL DEFAULT NULL,
  mi_perfil_rec_lar varchar(1024) NULL DEFAULT NULL,
  mi_perfil_rec_ven int4 NULL,
  mi_perfil_rec_hora int4 NULL,
  mi_perfil_rec_dias int4 NULL,
  reserva_pen_tiempo_max integer DEFAULT NULL,
  reserva_pend_tiempo_max integer DEFAULT null,
  reserva_multiple_pend_tiempo_max int4 NULL
);
ALTER TABLE ae_recursos OWNER TO sae;

CREATE TABLE ae_reservas (
    id integer NOT NULL,
    estado character varying(1) NOT NULL,
    fact timestamp without time zone NOT NULL,
    fcrea timestamp without time zone NOT NULL,
    numero integer,
    observaciones character varying(100),
    origen character varying(1),
    ucancela character varying(255),
    ucrea character varying(255),
    version integer NOT NULL,
    codigo_seguridad character varying(10) DEFAULT '00000'::character varying,
    trazabilidad_guid character varying(25),
    tramite_codigo character varying(10),
    tramite_nombre character varying(100),
    serie character varying(3),
    tcancela character varying(1),
    fcancela timestamp without time zone,
    aetr_id int4 NULL DEFAULT NULL,
    ip_origen varchar(16) DEFAULT NULL,
    flibera timestamp DEFAULT NULL,
    mi_perfil_notif boolean NOT NULL DEFAULT true,
	notificar boolean DEFAULT true,
    reserva_hija_id integer NULL,
    id_origen integer NULL
);
ALTER TABLE ae_reservas OWNER TO sae;

CREATE TABLE ae_reservas_disponibilidades (
    aers_id integer NOT NULL,
    aedi_id integer NOT NULL
);
ALTER TABLE ae_reservas_disponibilidades OWNER TO sae;

CREATE TABLE ae_roles_usuario_recurso (
    usuario_id integer NOT NULL,
    recurso_id integer NOT NULL,
    roles character varying(4000)
);
ALTER TABLE ae_roles_usuario_recurso OWNER TO sae;

CREATE TABLE ae_serv_autocomp_por_dato (
    id integer NOT NULL,
    fecha_desasociacion timestamp without time zone,
    nombre_parametro character varying(50) NOT NULL,
    aeds_id integer NOT NULL,
    aesr_id integer NOT NULL
);
ALTER TABLE ae_serv_autocomp_por_dato OWNER TO sae;

CREATE TABLE ae_serv_autocompletar (
    id integer NOT NULL,
    descripcion character varying(100) NOT NULL,
    fecha_baja timestamp without time zone,
    host character varying(100),
    nombre character varying(50) NOT NULL,
    servicio character varying(250) NOT NULL
);
ALTER TABLE ae_serv_autocompletar OWNER TO sae;

CREATE TABLE ae_servicio_por_recurso (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    aeserv_id integer NOT NULL,
    aere_id integer NOT NULL
);
ALTER TABLE ae_servicio_por_recurso OWNER TO sae;

CREATE TABLE ae_textos (
    codigo character varying(100) NOT NULL,
    idioma character varying(5) NOT NULL,
    texto character varying(4096) NOT NULL
);
ALTER TABLE ae_textos OWNER TO sae;

CREATE TABLE ae_textos_agenda (
    id integer NOT NULL,
    texto_paso1 character varying(1000),
    texto_paso2 character varying(1000),
    texto_paso3 character varying(1000),
    texto_sel_recurso character varying(50),
    texto_ticket character varying(1000),
    aeag_id integer NOT NULL,
    texto_correo_conf character varying(1000),
    texto_correo_canc character varying(1000),
    texto_correo_tras character varying(1000),
    por_defecto boolean DEFAULT false NOT NULL,
    idioma character varying(5) NOT NULL
);
ALTER TABLE ae_textos_agenda OWNER TO sae;

CREATE TABLE ae_textos_recurso (
    id integer NOT NULL,
    texto_paso2 character varying(1000),
    texto_paso3 character varying(1000),
    ticket_etiqueta_dos character varying(15),
    ticket_etiqueta_uno character varying(15),
    titulo_ciudadano_en_llamador character varying(255),
    titulo_puesto_en_llamador character varying(255),
    valor_etiqueta_dos character varying(30),
    valor_etiqueta_uno character varying(30),
    aere_id integer NOT NULL,
    idioma character varying(5) NOT NULL
);
ALTER TABLE ae_textos_recurso OWNER TO sae;

CREATE TABLE ae_tokens_reservas (
	id int4 NOT NULL,
	token varchar(50) NOT NULL,
	aere_id int4 NULL,
	fecha_inicio timestamp NOT NULL,
	ultima_reserva timestamp NULL,
	estado varchar(1) NOT NULL,
	cedula varchar(50) NOT NULL,
	nombre varchar(100) NOT NULL,
	correoe varchar(100) NOT NULL,
	tramite varchar(10) NULL,
	notas varchar(4000) NULL,
	"version" int4 NOT NULL,
  ip_origen varchar(16) DEFAULT NULL
);
ALTER TABLE ae_tokens_reservas OWNER TO sae;

CREATE TABLE ae_tramites_agendas (
    id integer NOT NULL,
    agenda_id integer NOT NULL,
    tramite_id character varying(25),
    tramite_codigo character varying(10),
    tramite_nombre character varying(100)
);
ALTER TABLE ae_tramites_agendas OWNER TO sae;

CREATE TABLE ae_validaciones (
    id integer NOT NULL,
    descripcion character varying(100) NOT NULL,
    fecha_baja timestamp without time zone,
    host character varying(100),
    nombre character varying(50) NOT NULL,
    servicio character varying(250) NOT NULL
);
ALTER TABLE ae_validaciones OWNER TO sae;

CREATE TABLE ae_validaciones_por_dato (
    id integer NOT NULL,
    fecha_desasociacion timestamp without time zone,
    nombre_parametro character varying(50) NOT NULL,
    aeds_id integer NOT NULL,
    aevr_id integer NOT NULL
);
ALTER TABLE ae_validaciones_por_dato OWNER TO sae;

CREATE TABLE ae_validaciones_por_recurso (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    orden_ejecucion integer,
    aere_id integer NOT NULL,
    aeva_id integer NOT NULL
);
ALTER TABLE ae_validaciones_por_recurso OWNER TO sae;

CREATE TABLE ae_valor_constante_val_rec (
    id integer NOT NULL,
    fecha_desasociacion timestamp without time zone,
    nombre_constante character varying(50) NOT NULL,
    valor character varying(100) NOT NULL,
    aevr_id integer NOT NULL
);
ALTER TABLE ae_valor_constante_val_rec OWNER TO sae;

CREATE TABLE ae_valores_del_dato (
    id integer NOT NULL,
    etiqueta character varying(50) NOT NULL,
    fecha_desde timestamp without time zone NOT NULL,
    fecha_hasta timestamp without time zone,
    orden integer NOT NULL,
    valor character varying(50) NOT NULL,
    aeds_id integer NOT NULL,
    borrar_flag boolean DEFAULT true NOT NULL
);
ALTER TABLE ae_valores_del_dato OWNER TO sae;

CREATE TABLE ae_acciones_miperfil_recurso
(
   id integer NOT NULL,
   recurso_id integer NOT NULL,
   
   titulo_con_1 character varying(100), 
   url_con_1 character varying(1024),  
   destacada_con_1 boolean,
   titulo_con_2 character varying(100), 
   url_con_2 character varying(1024),  
   destacada_con_2 boolean,
   titulo_con_3 character varying(100), 
   url_con_3 character varying(1024),  
   destacada_con_3 boolean,
   titulo_con_4 character varying(100), 
   url_con_4 character varying(1024),  
   destacada_con_4 boolean,
   titulo_con_5 character varying(100), 
   url_con_5 character varying(1024),  
   destacada_con_5 boolean,

   titulo_can_1 character varying(100), 
   url_can_1 character varying(1024),  
   destacada_can_1 boolean,
   titulo_can_2 character varying(100), 
   url_can_2 character varying(1024),  
   destacada_can_2 boolean,
   titulo_can_3 character varying(100), 
   url_can_3 character varying(1024),  
   destacada_can_3 boolean,
   titulo_can_4 character varying(100), 
   url_can_4 character varying(1024),  
   destacada_can_4 boolean,
   titulo_can_5 character varying(100), 
   url_can_5 character varying(1024),  
   destacada_can_5 boolean,

   titulo_rec_1 character varying(100), 
   url_rec_1 character varying(1024),  
   destacada_rec_1 boolean,
   titulo_rec_2 character varying(100), 
   url_rec_2 character varying(1024),  
   destacada_rec_2 boolean,
   titulo_rec_3 character varying(100), 
   url_rec_3 character varying(1024),  
   destacada_rec_3 boolean,
   titulo_rec_4 character varying(100), 
   url_rec_4 character varying(1024),  
   destacada_rec_4 boolean,
   titulo_rec_5 character varying(100), 
   url_rec_5 character varying(1024),  
   destacada_rec_5 boolean,
   
   CONSTRAINT ae_acciones_miperfil_recurso_pkey PRIMARY KEY (id)
);
ALTER TABLE ae_acciones_miperfil_recurso OWNER TO sae;


CREATE TABLE ae_recursos_aud
(
   id int4 NOT NULL,
   id_recurso int4 NOT NULL,
   nombre varchar(100) NOT NULL,
   descripcion varchar(1000) NOT NULL,
   fecha_inicio timestamp NOT NULL,
   fecha_fin timestamp NULL,
   fecha_inicio_disp timestamp NOT NULL,
   fecha_fin_disp timestamp NULL,
   dias_inicio_ventana_intranet int4 NOT NULL,
   dias_ventana_intranet int4 NOT NULL,
   dias_inicio_ventana_internet int4 NOT NULL,
   dias_ventana_internet int4 NOT NULL,
   ventana_cupos_minimos int4 NOT NULL,
   cant_dias_a_generar int4 NOT NULL,
   largo_lista_espera int4 NULL,
   fecha_baja timestamp NULL,
   mostrar_numero_en_llamador bool NOT NULL,
   visible_internet bool NOT NULL,
   usar_llamador bool NOT NULL,
   serie varchar(3) NULL,
   sabado_es_habil bool NOT NULL,
   domingo_es_habil bool NOT NULL DEFAULT false,
   mostrar_numero_en_ticket bool NOT NULL,
   mostrar_id_en_ticket bool NULL,
   fuente_ticket varchar(100) NOT NULL DEFAULT 'Helvetica-Bold'::character varying,
   tamanio_fuente_grande int4 NOT NULL DEFAULT 12,
   tamanio_fuente_normal int4 NOT NULL DEFAULT 10,
   tamanio_fuente_chica int4 NOT NULL DEFAULT 8,
   oficina_id varchar(25) NULL,
   direccion varchar(100) NULL,
   localidad varchar(100) NULL,
   departamento varchar(100) NULL,
   telefonos varchar(100) NULL,
   horarios varchar(100) NULL,
   latitud numeric NULL,
   longitud numeric NULL,
   agenda int4 NOT NULL,
   presencial_admite bool NOT NULL DEFAULT false,
   presencial_cupos int4 NOT NULL DEFAULT 0,
   presencial_lunes bool NOT NULL DEFAULT false,
   presencial_martes bool NOT NULL DEFAULT false,
   presencial_miercoles bool NOT NULL DEFAULT false,
   presencial_jueves bool NOT NULL DEFAULT false,
   presencial_viernes bool NOT NULL DEFAULT false,
   presencial_sabado bool NOT NULL DEFAULT false,
   presencial_domingo bool NOT NULL DEFAULT false,
   multiple_admite bool NOT NULL DEFAULT false,
   cambios_admite bool NOT NULL DEFAULT false,
   cambios_tiempo int4 NULL,
   cambios_unidad int4 NULL,
   periodo_validacion int4 NOT NULL DEFAULT 0,
   validar_por_ip bool NOT NULL DEFAULT false,
   cantidad_por_ip int4 NULL,
   periodo_por_ip int4 NULL,
   ips_sin_validacion varchar(4000) NULL DEFAULT NULL::character varying,
   cancela_tiempo int4 NOT NULL DEFAULT 0,
   cancela_unidad int4 NOT NULL DEFAULT 12,
   cancela_tipo varchar(1) NOT NULL DEFAULT 'I'::character varying,
   mi_perfil_con_hab bool NOT NULL DEFAULT true,
   mi_perfil_con_tit varchar(200) NULL DEFAULT NULL::character varying,
   mi_perfil_con_cor varchar(500) NULL DEFAULT NULL::character varying,
   mi_perfil_con_lar varchar(3200) NULL DEFAULT NULL::character varying,
   mi_perfil_con_ven int4 NULL,
   mi_perfil_can_hab bool NOT NULL DEFAULT true,
   mi_perfil_can_tit varchar(200) NULL DEFAULT NULL::character varying,
   mi_perfil_can_cor varchar(500) NULL DEFAULT NULL::character varying,
   mi_perfil_can_lar varchar(3200) NULL DEFAULT NULL::character varying,
   mi_perfil_can_ven int4 NULL,
   mi_perfil_rec_hab bool NOT NULL DEFAULT true,
   mi_perfil_rec_tit varchar(200) NULL DEFAULT NULL::character varying,
   mi_perfil_rec_cor varchar(500) NULL DEFAULT NULL::character varying,
   mi_perfil_rec_lar varchar(3200) NULL DEFAULT NULL::character varying,
   mi_perfil_rec_ven int4 NULL,
   mi_perfil_rec_hora int4 NULL,
   mi_perfil_rec_dias int4 NULL,
   reserva_pen_tiempo_max integer DEFAULT NULL,
   reserva_pend_tiempo_max integer DEFAULT NULL, 
   reserva_multiple_pend_tiempo_max int4 NULL,
   fecha_modificacion timestamp NULL,
   usuario varchar(45) NULL,
   "version" int4 NOT NULL,
   tipo_operacion int2 null, -- 0 creación, 1 modificación y 2 eliminación;,
   
   CONSTRAINT ae_recursos_aud_pkey PRIMARY KEY (id)
);
ALTER TABLE ae_recursos_aud OWNER TO sae;

--
-- SECUENCIAS
--

CREATE SEQUENCE s_ae_accion START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_accion OWNER TO sae;

CREATE SEQUENCE s_ae_acciondato START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_acciondato OWNER TO sae;

CREATE SEQUENCE s_ae_accionrecurso START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_accionrecurso OWNER TO sae;

CREATE SEQUENCE s_ae_agenda START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_agenda OWNER TO sae;

CREATE SEQUENCE s_ae_agrupacion_dato START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_agrupacion_dato OWNER TO sae;

CREATE SEQUENCE s_ae_anio START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_anio OWNER TO sae;

CREATE SEQUENCE s_ae_atencion START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_atencion OWNER TO sae;

CREATE SEQUENCE s_ae_comunicaciones START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_comunicaciones OWNER TO sae;

CREATE SEQUENCE s_ae_constval START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_constval OWNER TO sae;

CREATE SEQUENCE s_ae_datoasolicitar START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_datoasolicitar OWNER TO sae;

CREATE SEQUENCE s_ae_datodelrecurso START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_datodelrecurso OWNER TO sae;

CREATE SEQUENCE s_ae_datoreserva START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_datoreserva OWNER TO sae;

CREATE SEQUENCE s_ae_dia_mes START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_dia_mes OWNER TO sae;

CREATE SEQUENCE s_ae_dia_semana START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_dia_semana OWNER TO sae;

CREATE SEQUENCE s_ae_disponibilidad START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_disponibilidad OWNER TO sae;

CREATE SEQUENCE s_ae_llamada START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_llamada OWNER TO sae;

CREATE SEQUENCE s_ae_mes START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_mes OWNER TO sae;

CREATE SEQUENCE s_ae_paramaccion START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_paramaccion OWNER TO sae;

CREATE SEQUENCE s_ae_parametros_autocompletar START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_parametros_autocompletar OWNER TO sae;

CREATE SEQUENCE s_ae_paramval START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_paramval OWNER TO sae;

CREATE SEQUENCE s_ae_plantilla START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_plantilla OWNER TO sae;

CREATE SEQUENCE s_ae_recurso START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_recurso OWNER TO sae;

CREATE SEQUENCE s_ae_reserva START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_reserva OWNER TO sae;

CREATE SEQUENCE s_ae_serv_autocompletar START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_serv_autocompletar OWNER TO sae;

CREATE SEQUENCE s_ae_servdato START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_servdato OWNER TO sae;

CREATE SEQUENCE s_ae_servrecurso START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_servrecurso OWNER TO sae;

CREATE SEQUENCE s_ae_texto_agenda START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_texto_agenda OWNER TO sae;

CREATE SEQUENCE s_ae_textorecurso START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_textorecurso OWNER TO sae;

CREATE SEQUENCE s_ae_token_reserva START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_token_reserva OWNER TO sae;

CREATE SEQUENCE s_ae_tramites_agendas START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_tramites_agendas OWNER TO sae;

CREATE SEQUENCE s_ae_valconstante START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_valconstante OWNER TO sae;

CREATE SEQUENCE s_ae_valdato START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_valdato OWNER TO sae;

CREATE SEQUENCE s_ae_validacion START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_validacion OWNER TO sae;

CREATE SEQUENCE s_ae_valorposible START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_valorposible OWNER TO sae;

CREATE SEQUENCE s_ae_valrecurso START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_valrecurso OWNER TO sae;

CREATE SEQUENCE s_ae_acciones_miperfil;
ALTER TABLE s_ae_acciones_miperfil OWNER TO sae;

CREATE SEQUENCE s_ae_recurso_aud START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER TABLE s_ae_recurso_aud OWNER TO sae;
--
-- DATOS
--

INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000001', '¿De qué color era el caballo blanco de Artigas?', 'blanco', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000002', '¿Cuánto es dos más dos? Responde con palabras', 'cuatro', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000003', 'Escribe la tercera palabra de: Hoy está lloviendo', 'lloviendo', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000004', '¿Cuánto es tres más uno? Responde con palabras', 'cuatro', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000005', 'Escribe la segunda palabra de: Hoy es Lunes', 'es', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000006', '¿Cuánto es uno más uno? Responde con palabras', 'dos', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000007', '¿Cuánto es cinco más dos? Responde con palabras', 'siete', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000008', 'Escribe la primera palabra de: Hoy está soleado', 'hoy', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000009', 'De los números uno, cuatro y dos, ¿cuál es el menor?', 'uno', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000010', '¿Cuántos colores hay en la lista: torta, verde, hotel?', 'uno', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000011', '¿Cuál es el segundo dígito de 7101712? Responde con palabras', 'uno', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000012', 'Si el león es amarillo, ¿de qué color es el león?', 'amarillo', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000013', '¿Cuál de los siguientes es un color: libro, nariz, verde, queso?', 'verde', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000014', 'Si mañana es sábado, ¿qué día es hoy?', 'viernes', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000016', 'Escribe la segunda palabra de: Ayer tuve frío', 'tuve', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000017', 'Escribe la última palabra de: me caigo y no me levanto', 'levanto', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000018', 'Escribe la quinta palabra de: más vale pájaro en mano que cien volando', 'mano', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000020', 'Escribe la quinta palabra de: cuanto menos se piensa salta la liebre', 'salta', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000021', 'Un día en la vida, ¿cuántos días son? escribe en letras', 'uno', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000022', 'Tres tristes tigres comen trigo, ¿qué comen los tigres?', 'trigo', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000023', 'Si hoy me acuesto, despierto mañana; ¿cuándo me acuesto?', 'hoy', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000024', '¿Qué animal es mayor, el mono  o el elefante?', 'elefante', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000025', '¿Cuál es el primer mes del año?', 'enero', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000026', '¿Cuál es el segundo mes del año?', 'febrero', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000027', '¿Cuál es el tercer mes del año?', 'marzo', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000028', '¿Cuál es el cuarto mes del año?', 'abril', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000029', '¿Cuál es el quinto mes del año?', 'mayo', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000030', '¿Cuál es el sexto mes del año?', 'junio', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000031', '¿Cuál es el séptimo mes del año?', 'julio', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000032', '¿Cuál es el octavo mes del año?', 'agosto', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000033', '¿Cuál es el noveno mes del año?', 'setiembre', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000034', '¿Cuál es el décimo mes del año?', 'octubre', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000035', '¿Cuál es el penúltimo mes del año?', 'noviembre', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000036', '¿Cuál es el último mes del año?', 'diciembre', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000037', '¿Cuántos días tiene enero? Responde con números', '31', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000038', '¿Cuántos días tiene febrero? Responde con números', '28', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000039', '¿Cuántos días tiene marzo? Responde con números', '31', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000040', '¿Cuántos días tiene abril? Responde con números', '30', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000041', '¿Cuántos días tiene mayo? Responde con números', '31', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000042', '¿Cuántos días tiene junio? Responde con números', '30', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000043', '¿Cuántos días tiene julio? Responde con números', '31', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000044', '¿Cuántos días tiene agosto? Responde con números', '31', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000045', '¿Cuántos días tiene setiembre? Responde con números', '30', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000046', '¿Cuántos días tiene octubre? Responde con números', '31', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000047', '¿Cuántos días tiene noviembre? Responde con números', '30', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000048', '¿Cuántos días tiene diciembre? Responde con números', '31', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000049', 'Hola dijo Bartola; ¿quién dijo hola?', 'Bartola', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000050', '¿Cuál palabra es un color: amarillo, abejorro, mono?', 'amarillo', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000051', '¿Cuál palabra es un color: violeta, araña, morsa?', 'violeta', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000052', '¿Cuál palabra es un color: blanco, alce, mosca?', 'blanco', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000053', '¿Cuál palabra es un color: azul, almeja, mosquito?', 'azul', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000054', '¿Cuál palabra es un color: rojo, ardilla, casa?', 'rojo', 'es');
INSERT INTO ae_preguntas_captcha (clave, pregunta, respuesta, idioma) VALUES ('P000055', '¿Cuál palabra es un color: negro, vaca, auto?', 'negro', 'es');

--
--  CLAVES PRIMARIAS
--

ALTER TABLE ONLY ae_acciones ADD CONSTRAINT ae_acciones_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_acciones_por_dato ADD CONSTRAINT ae_acciones_por_dato_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_acciones_por_recurso ADD CONSTRAINT ae_acciones_por_recurso_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_agendas ADD CONSTRAINT ae_agendas_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_agrupaciones_datos ADD CONSTRAINT ae_agrupaciones_datos_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_anios ADD CONSTRAINT ae_anios_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_atencion ADD CONSTRAINT ae_atencion_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_frases_captcha ADD CONSTRAINT ae_captchas_pk PRIMARY KEY (clave);
ALTER TABLE ONLY ae_comunicaciones ADD CONSTRAINT ae_comunicaciones_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_constante_validacion ADD CONSTRAINT ae_constante_validacion_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_datos_a_solicitar ADD CONSTRAINT ae_datos_a_solicitar_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_datos_del_recurso ADD CONSTRAINT ae_datos_del_recurso_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_datos_reserva ADD CONSTRAINT ae_datos_reserva_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_dias_del_mes ADD CONSTRAINT ae_dias_del_mes_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_dias_semana ADD CONSTRAINT ae_dias_semana_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_disponibilidades ADD CONSTRAINT ae_disponibilidades_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_llamadas ADD CONSTRAINT ae_llamadas_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_meses ADD CONSTRAINT ae_meses_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_parametros_accion ADD CONSTRAINT ae_parametros_accion_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_parametros_autocompletar ADD CONSTRAINT ae_parametros_autocompletar_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_parametros_validacion ADD CONSTRAINT ae_parametros_validacion_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_plantillas ADD CONSTRAINT ae_plantillas_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_preguntas_captcha ADD CONSTRAINT ae_preguntas_pk PRIMARY KEY (clave);
ALTER TABLE ONLY ae_recursos ADD CONSTRAINT ae_recursos_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_reservas ADD CONSTRAINT ae_reservas_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_roles_usuario_recurso ADD CONSTRAINT ae_roles_usuario_recurso_pkey PRIMARY KEY (usuario_id, recurso_id);
ALTER TABLE ONLY ae_serv_autocomp_por_dato ADD CONSTRAINT ae_serv_autocomp_por_dato_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_serv_autocompletar ADD CONSTRAINT ae_serv_autocompletar_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_servicio_por_recurso ADD CONSTRAINT ae_servicio_por_recurso_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_textos_agenda ADD CONSTRAINT ae_textos_agenda_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_textos ADD CONSTRAINT ae_textos_pk PRIMARY KEY (codigo, idioma);
ALTER TABLE ONLY ae_textos_recurso ADD CONSTRAINT ae_textos_recurso_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_tokens_reservas ADD CONSTRAINT ae_tokens_reservas_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_tramites_agendas ADD CONSTRAINT ae_tramites_agenda_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_validaciones ADD CONSTRAINT ae_validaciones_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_validaciones_por_dato ADD CONSTRAINT ae_validaciones_por_dato_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_validaciones_por_recurso ADD CONSTRAINT ae_validaciones_por_recurso_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_valor_constante_val_rec ADD CONSTRAINT ae_valor_constante_val_rec_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ae_valores_del_dato ADD CONSTRAINT ae_valores_del_dato_pkey PRIMARY KEY (id);

--
--  CLAVES UNICAS
--

ALTER TABLE ONLY ae_llamadas ADD CONSTRAINT ae_llamadas_aers_id_key UNIQUE (aers_id);

--
-- INDICES
--

CREATE INDEX ae_reservas_aetr_id_idx ON ae_reservas USING btree (aetr_id);
CREATE INDEX ae_reservas_disponibilidades_disponibilidad ON ae_reservas_disponibilidades USING btree (aedi_id);
CREATE INDEX ae_reservas_disponibilidades_reserva ON ae_reservas_disponibilidades USING btree (aers_id);
CREATE INDEX ae_datos_reserva_aeds_id_idx ON ae_datos_reserva USING btree (aeds_id);
CREATE INDEX ae_datos_reserva_aers_id_idx ON ae_datos_reserva USING btree (aers_id);
CREATE INDEX ae_disponibilidades_fecha_idx ON ae_disponibilidades USING btree (fecha);
CREATE INDEX ae_reservas_estado_idx ON {esquema}.ae_reservas USING btree (estado);
CREATE INDEX ae_reservas_fcrea_idx ON {esquema}.ae_reservas USING btree (fcrea);
CREATE INDEX ae_disponibilidades_aere_id_idx ON {esquema}.ae_disponibilidades USING btree (aere_id);
CREATE INDEX ae_datos_reserva_valor_idx ON {esquema}.ae_datos_reserva USING btree (valor);
CREATE INDEX ae_datos_a_solicitar_nombre_idx ON {esquema}.ae_datos_a_solicitar USING btree (nombre);
CREATE INDEX ae_tokens_reservas_aere_id_idx ON {esquema}.ae_tokens_reservas (aere_id);
CREATE INDEX ae_tokens_reservas_ultima_reserva_idx ON {esquema}.ae_tokens_reservas (ultima_reserva);
CREATE INDEX ae_tokens_reservas_fecha_inicio_idx ON {esquema}.ae_tokens_reservas (fecha_inicio);
CREATE INDEX ae_tokens_reservas_estado_idx ON {esquema}.ae_tokens_reservas (estado);


-- FOREIGN KEYS 

ALTER TABLE ONLY ae_disponibilidades ADD CONSTRAINT fk1c09bf24104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_disponibilidades ADD CONSTRAINT fk1c09bf249a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);
ALTER TABLE ONLY ae_dias_semana ADD CONSTRAINT fk28a15dc69a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);
ALTER TABLE ONLY ae_agrupaciones_datos ADD CONSTRAINT fk3360aa44104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_datos_a_solicitar ADD CONSTRAINT fk3ce7cc09104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_datos_a_solicitar ADD CONSTRAINT fk3ce7cc091876ae95 FOREIGN KEY (aead_id) REFERENCES ae_agrupaciones_datos(id);
ALTER TABLE ONLY ae_parametros_autocompletar ADD CONSTRAINT fk3e0b63a4cc9035ed FOREIGN KEY (aeserv_id) REFERENCES ae_serv_autocompletar(id);
ALTER TABLE ONLY ae_constante_validacion ADD CONSTRAINT fk3f09314323ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);
ALTER TABLE ONLY ae_valores_del_dato ADD CONSTRAINT fk42202b5436760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);
ALTER TABLE ONLY ae_parametros_accion ADD CONSTRAINT fk4da30a11e4b40066 FOREIGN KEY (aeac_id) REFERENCES ae_acciones(id);
ALTER TABLE ONLY ae_datos_del_recurso ADD CONSTRAINT fk5ff42436104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_validaciones_por_dato ADD CONSTRAINT fk66d0a85036760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);
ALTER TABLE ONLY ae_validaciones_por_dato ADD CONSTRAINT fk66d0a8508d2f46a5 FOREIGN KEY (aevr_id) REFERENCES ae_validaciones_por_recurso(id);
ALTER TABLE ONLY ae_acciones_por_dato ADD CONSTRAINT fk6e5144f336760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);
ALTER TABLE ONLY ae_acciones_por_dato ADD CONSTRAINT fk6e5144f362f9440d FOREIGN KEY (aear_id) REFERENCES ae_acciones_por_recurso(id);
ALTER TABLE ONLY ae_reservas ADD CONSTRAINT ae_reservas_token FOREIGN KEY (aetr_id) REFERENCES ae_tokens_reservas(id);
ALTER TABLE ONLY ae_reservas_disponibilidades ADD CONSTRAINT fk79b9a11211242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);
ALTER TABLE ONLY ae_reservas_disponibilidades ADD CONSTRAINT fk79b9a112406004b7 FOREIGN KEY (aedi_id) REFERENCES ae_disponibilidades(id);
ALTER TABLE ONLY ae_valor_constante_val_rec ADD CONSTRAINT fk8a30e71e8d2f46a5 FOREIGN KEY (aevr_id) REFERENCES ae_validaciones_por_recurso(id);
ALTER TABLE ONLY ae_textos_agenda ADD CONSTRAINT fk96b86f5fe4ef2a07 FOREIGN KEY (aeag_id) REFERENCES ae_agendas(id);
ALTER TABLE ONLY ae_tokens_reservas ADD CONSTRAINT ae_tokens_reservas_recurso FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_validaciones_por_recurso ADD CONSTRAINT fk9e323ab1104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_validaciones_por_recurso ADD CONSTRAINT fk9e323ab123ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);
ALTER TABLE ONLY ae_datos_reserva ADD CONSTRAINT fk9ecc9f5911242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);
ALTER TABLE ONLY ae_datos_reserva ADD CONSTRAINT fk9ecc9f5936760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);
ALTER TABLE ONLY ae_llamadas ADD CONSTRAINT fka0da2cfc104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_llamadas ADD CONSTRAINT fka0da2cfc11242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);
ALTER TABLE ONLY ae_recursos ADD CONSTRAINT fka1b7fd05e4ef2a07 FOREIGN KEY (aeag_id) REFERENCES ae_agendas(id);
ALTER TABLE ONLY ae_serv_autocomp_por_dato ADD CONSTRAINT fkacfc169736760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);
ALTER TABLE ONLY ae_serv_autocomp_por_dato ADD CONSTRAINT fkacfc1697bcb2c28e FOREIGN KEY (aesr_id) REFERENCES ae_servicio_por_recurso(id);
ALTER TABLE ONLY ae_acciones_por_recurso ADD CONSTRAINT fkade6372e104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_acciones_por_recurso ADD CONSTRAINT fkade6372ee4b40066 FOREIGN KEY (aeac_id) REFERENCES ae_acciones(id);
ALTER TABLE ONLY ae_parametros_validacion ADD CONSTRAINT fkc717af5423ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);
ALTER TABLE ONLY ae_dias_del_mes ADD CONSTRAINT fkd1fddf1a9a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);
ALTER TABLE ONLY ae_servicio_por_recurso ADD CONSTRAINT fkd75adbaf104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);
ALTER TABLE ONLY ae_servicio_por_recurso ADD CONSTRAINT fkd75adbafcc9035ed FOREIGN KEY (aeserv_id) REFERENCES ae_serv_autocompletar(id);
ALTER TABLE ONLY ae_atencion ADD CONSTRAINT fkd841909c11242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);
ALTER TABLE ONLY ae_anios ADD CONSTRAINT fke2ce44e59a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);
ALTER TABLE ONLY ae_meses ADD CONSTRAINT fke3736bee9a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);
ALTER TABLE ONLY ae_plantillas ADD CONSTRAINT fkf9c6590b104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);

--
-- PERMISOS
--

REVOKE ALL ON SCHEMA {esquema} FROM PUBLIC;
REVOKE ALL ON SCHEMA {esquema} FROM sae;
GRANT ALL ON SCHEMA {esquema} TO sae;
GRANT ALL ON SCHEMA {esquema} TO postgres;
GRANT ALL ON SCHEMA {esquema} TO PUBLIC;

--
-- FIN
--
