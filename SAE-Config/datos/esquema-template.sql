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
-- Name: {empresa}; Type: SCHEMA; Schema: -; Owner: sae
--

CREATE SCHEMA {empresa};


ALTER SCHEMA {empresa} OWNER TO sae;

SET search_path = {empresa}, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: ae_acciones; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
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

--
-- Name: ae_acciones_por_dato; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_acciones_por_dato (
    id integer NOT NULL,
    fecha_desasociacion timestamp without time zone,
    nombre_parametro character varying(50) NOT NULL,
    aear_id integer NOT NULL,
    aeds_id integer NOT NULL
);


ALTER TABLE ae_acciones_por_dato OWNER TO sae;

--
-- Name: ae_acciones_por_recurso; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_acciones_por_recurso (
    id integer NOT NULL,
    evento character varying(1) NOT NULL,
    fecha_baja timestamp without time zone,
    orden_ejecucion integer,
    aeac_id integer NOT NULL,
    aere_id integer NOT NULL
);


ALTER TABLE ae_acciones_por_recurso OWNER TO sae;

--
-- Name: ae_agendas; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_agendas (
    id integer NOT NULL,
    descripcion character varying(1000) NOT NULL,
    fecha_baja timestamp without time zone,
    nombre character varying(100) NOT NULL,
    tramite_id character varying(25),
    timezone character varying(25),
    idiomas character varying(100),
    con_cda boolean DEFAULT false,
    tramite_codigo character varying(10),
    publicar_novedades boolean DEFAULT false NOT NULL,
    con_trazabilidad boolean DEFAULT false NOT NULL
);


ALTER TABLE ae_agendas OWNER TO sae;

--
-- Name: ae_agrupaciones_datos; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_agrupaciones_datos (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    nombre character varying(50) NOT NULL,
    orden integer NOT NULL,
    aere_id integer NOT NULL,
    borrar_flag boolean NOT NULL
);


ALTER TABLE ae_agrupaciones_datos OWNER TO sae;

--
-- Name: ae_anios; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_anios (
    id integer NOT NULL,
    anio integer NOT NULL,
    aepl_id integer NOT NULL
);


ALTER TABLE ae_anios OWNER TO sae;

--
-- Name: ae_atencion; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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

--
-- Name: ae_comunicaciones; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_comunicaciones (
    id integer NOT NULL,
    tipo_1 character varying(25) NOT NULL,
    tipo_2 character varying(25) NOT NULL,
    destino character varying(100) NOT NULL,
    recurso_id integer NOT NULL,
    reserva_id integer NOT NULL,
    procesado boolean DEFAULT false NOT NULL
);


ALTER TABLE ae_comunicaciones OWNER TO sae;

--
-- Name: ae_constante_validacion; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_constante_validacion (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    largo integer NOT NULL,
    nombre character varying(50) NOT NULL,
    tipo character varying(30) NOT NULL,
    aeva_id integer NOT NULL
);


ALTER TABLE ae_constante_validacion OWNER TO sae;

--
-- Name: ae_datos_a_solicitar; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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
    borrar_flag boolean DEFAULT true NOT NULL
);


ALTER TABLE ae_datos_a_solicitar OWNER TO sae;

--
-- Name: ae_datos_del_recurso; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_datos_del_recurso (
    id integer NOT NULL,
    etiqueta character varying(50) NOT NULL,
    orden integer NOT NULL,
    valor character varying(100) NOT NULL,
    aere_id integer NOT NULL
);


ALTER TABLE ae_datos_del_recurso OWNER TO sae;

--
-- Name: ae_datos_reserva; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_datos_reserva (
    id integer NOT NULL,
    valor character varying(100) NOT NULL,
    aeds_id integer NOT NULL,
    aers_id integer NOT NULL
);


ALTER TABLE ae_datos_reserva OWNER TO sae;

--
-- Name: ae_dias_del_mes; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_dias_del_mes (
    id integer NOT NULL,
    dia_del_mes integer NOT NULL,
    aepl_id integer NOT NULL
);


ALTER TABLE ae_dias_del_mes OWNER TO sae;

--
-- Name: ae_dias_semana; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_dias_semana (
    id integer NOT NULL,
    dia_semana integer NOT NULL,
    aepl_id integer NOT NULL
);


ALTER TABLE ae_dias_semana OWNER TO sae;

--
-- Name: ae_disponibilidades; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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
    aere_id integer NOT NULL
);


ALTER TABLE ae_disponibilidades OWNER TO sae;

--
-- Name: ae_frases_captcha; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_frases_captcha (
    clave character varying(100) NOT NULL,
    frase character varying(1024),
    idioma character varying(5) NOT NULL
);


ALTER TABLE ae_frases_captcha OWNER TO sae;

--
-- Name: ae_llamadas; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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

--
-- Name: ae_meses; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_meses (
    id integer NOT NULL,
    mes integer NOT NULL,
    aepl_id integer NOT NULL
);


ALTER TABLE ae_meses OWNER TO sae;

--
-- Name: ae_parametros_accion; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_parametros_accion (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    largo integer NOT NULL,
    nombre character varying(50) NOT NULL,
    tipo character varying(30) NOT NULL,
    aeac_id integer NOT NULL
);


ALTER TABLE ae_parametros_accion OWNER TO sae;

--
-- Name: ae_parametros_autocompletar; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_parametros_autocompletar (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    modo integer NOT NULL,
    nombre character varying(50) NOT NULL,
    tipo character varying(30) NOT NULL,
    aeserv_id integer NOT NULL
);


ALTER TABLE ae_parametros_autocompletar OWNER TO sae;

--
-- Name: ae_parametros_validacion; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_parametros_validacion (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    largo integer NOT NULL,
    nombre character varying(50) NOT NULL,
    tipo character varying(30) NOT NULL,
    aeva_id integer NOT NULL
);


ALTER TABLE ae_parametros_validacion OWNER TO sae;

--
-- Name: ae_plantillas; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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

--
-- Name: ae_preguntas_captcha; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_preguntas_captcha (
    clave character varying(100) NOT NULL,
    pregunta character varying(1024),
    respuesta character varying(25),
    idioma character varying(5) NOT NULL
);


ALTER TABLE ae_preguntas_captcha OWNER TO sae;

--
-- Name: ae_recursos; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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
    reserva_multiple boolean NOT NULL,
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
    longitud numeric
);


ALTER TABLE ae_recursos OWNER TO sae;

--
-- Name: ae_reservas; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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
    trazabilidad_guid character varying(25)
);


ALTER TABLE ae_reservas OWNER TO sae;

--
-- Name: ae_reservas_disponibilidades; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_reservas_disponibilidades (
    aers_id integer NOT NULL,
    aedi_id integer NOT NULL
);


ALTER TABLE ae_reservas_disponibilidades OWNER TO sae;

--
-- Name: ae_serv_autocomp_por_dato; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_serv_autocomp_por_dato (
    id integer NOT NULL,
    fecha_desasociacion timestamp without time zone,
    nombre_parametro character varying(50) NOT NULL,
    aeds_id integer NOT NULL,
    aesr_id integer NOT NULL
);


ALTER TABLE ae_serv_autocomp_por_dato OWNER TO sae;

--
-- Name: ae_serv_autocompletar; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_serv_autocompletar (
    id integer NOT NULL,
    descripcion character varying(100) NOT NULL,
    fecha_baja timestamp without time zone,
    host character varying(100),
    nombre character varying(50) NOT NULL,
    servicio character varying(250) NOT NULL
);


ALTER TABLE ae_serv_autocompletar OWNER TO sae;

--
-- Name: ae_servicio_por_recurso; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_servicio_por_recurso (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    aeserv_id integer NOT NULL,
    aere_id integer NOT NULL
);


ALTER TABLE ae_servicio_por_recurso OWNER TO sae;

--
-- Name: ae_textos; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_textos (
    codigo character varying(100) NOT NULL,
    idioma character varying(5) NOT NULL,
    texto character varying(4096) NOT NULL
);


ALTER TABLE ae_textos OWNER TO sae;

--
-- Name: ae_textos_agenda; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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
    por_defecto boolean DEFAULT false NOT NULL,
    idioma character varying(5) NOT NULL
);


ALTER TABLE ae_textos_agenda OWNER TO sae;

--
-- Name: ae_textos_recurso; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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

--
-- Name: ae_validaciones; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_validaciones (
    id integer NOT NULL,
    descripcion character varying(100) NOT NULL,
    fecha_baja timestamp without time zone,
    host character varying(100),
    nombre character varying(50) NOT NULL,
    servicio character varying(250) NOT NULL
);


ALTER TABLE ae_validaciones OWNER TO sae;

--
-- Name: ae_validaciones_por_dato; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_validaciones_por_dato (
    id integer NOT NULL,
    fecha_desasociacion timestamp without time zone,
    nombre_parametro character varying(50) NOT NULL,
    aeds_id integer NOT NULL,
    aevr_id integer NOT NULL
);


ALTER TABLE ae_validaciones_por_dato OWNER TO sae;

--
-- Name: ae_validaciones_por_recurso; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_validaciones_por_recurso (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    orden_ejecucion integer,
    aere_id integer NOT NULL,
    aeva_id integer NOT NULL
);


ALTER TABLE ae_validaciones_por_recurso OWNER TO sae;

--
-- Name: ae_valor_constante_val_rec; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_valor_constante_val_rec (
    id integer NOT NULL,
    fecha_desasociacion timestamp without time zone,
    nombre_constante character varying(50) NOT NULL,
    valor character varying(100) NOT NULL,
    aevr_id integer NOT NULL
);


ALTER TABLE ae_valor_constante_val_rec OWNER TO sae;

--
-- Name: ae_valores_del_dato; Type: TABLE; Schema: {empresa}; Owner: sae; Tablespace: 
--

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

--
-- Name: s_ae_accion; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_accion
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_accion OWNER TO sae;

--
-- Name: s_ae_acciondato; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_acciondato
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_acciondato OWNER TO sae;

--
-- Name: s_ae_accionrecurso; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_accionrecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_accionrecurso OWNER TO sae;

--
-- Name: s_ae_agenda; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_agenda
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_agenda OWNER TO sae;

--
-- Name: s_ae_agrupacion_dato; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_agrupacion_dato
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_agrupacion_dato OWNER TO sae;

--
-- Name: s_ae_anio; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_anio
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_anio OWNER TO sae;

--
-- Name: s_ae_atencion; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_atencion
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_atencion OWNER TO sae;

--
-- Name: s_ae_comunicaciones; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_comunicaciones
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_comunicaciones OWNER TO sae;

--
-- Name: s_ae_constval; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_constval
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_constval OWNER TO sae;

--
-- Name: s_ae_datoasolicitar; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_datoasolicitar
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_datoasolicitar OWNER TO sae;

--
-- Name: s_ae_datodelrecurso; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_datodelrecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_datodelrecurso OWNER TO sae;

--
-- Name: s_ae_datoreserva; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_datoreserva
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_datoreserva OWNER TO sae;

--
-- Name: s_ae_dia_mes; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_dia_mes
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_dia_mes OWNER TO sae;

--
-- Name: s_ae_dia_semana; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_dia_semana
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_dia_semana OWNER TO sae;

--
-- Name: s_ae_disponibilidad; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_disponibilidad
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_disponibilidad OWNER TO sae;

--
-- Name: s_ae_llamada; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_llamada
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_llamada OWNER TO sae;

--
-- Name: s_ae_mes; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_mes
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_mes OWNER TO sae;

--
-- Name: s_ae_paramaccion; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_paramaccion
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_paramaccion OWNER TO sae;

--
-- Name: s_ae_parametros_autocompletar; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_parametros_autocompletar
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_parametros_autocompletar OWNER TO sae;

--
-- Name: s_ae_paramval; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_paramval
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_paramval OWNER TO sae;

--
-- Name: s_ae_plantilla; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_plantilla
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_plantilla OWNER TO sae;

--
-- Name: s_ae_recurso; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_recurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_recurso OWNER TO sae;

--
-- Name: s_ae_reserva; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_reserva
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_reserva OWNER TO sae;

--
-- Name: s_ae_serv_autocompletar; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_serv_autocompletar
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_serv_autocompletar OWNER TO sae;

--
-- Name: s_ae_servdato; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_servdato
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_servdato OWNER TO sae;

--
-- Name: s_ae_servrecurso; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_servrecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_servrecurso OWNER TO sae;

--
-- Name: s_ae_texto_agenda; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_texto_agenda
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_texto_agenda OWNER TO sae;

--
-- Name: s_ae_textorecurso; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_textorecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_textorecurso OWNER TO sae;

--
-- Name: s_ae_valconstante; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_valconstante
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_valconstante OWNER TO sae;

--
-- Name: s_ae_valdato; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_valdato
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_valdato OWNER TO sae;

--
-- Name: s_ae_validacion; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_validacion
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_validacion OWNER TO sae;

--
-- Name: s_ae_valorposible; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_valorposible
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_valorposible OWNER TO sae;

--
-- Name: s_ae_valrecurso; Type: SEQUENCE; Schema: {empresa}; Owner: sae
--

CREATE SEQUENCE s_ae_valrecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_valrecurso OWNER TO sae;

--
-- Data for Name: ae_acciones; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_acciones_por_dato; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_acciones_por_recurso; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_agendas; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_agrupaciones_datos; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_anios; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_atencion; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_comunicaciones; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_constante_validacion; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_datos_a_solicitar; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_datos_del_recurso; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_datos_reserva; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_dias_del_mes; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_dias_semana; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_disponibilidades; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_frases_captcha; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_llamadas; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_meses; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_parametros_accion; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_parametros_autocompletar; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_parametros_validacion; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_plantillas; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_preguntas_captcha; Type: TABLE DATA; Schema: {empresa}; Owner: sae
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
-- Data for Name: ae_recursos; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_reservas; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_reservas_disponibilidades; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_serv_autocomp_por_dato; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_serv_autocompletar; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_servicio_por_recurso; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_textos; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_textos_agenda; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_textos_recurso; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_validaciones; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_validaciones_por_dato; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_validaciones_por_recurso; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_valor_constante_val_rec; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Data for Name: ae_valores_del_dato; Type: TABLE DATA; Schema: {empresa}; Owner: sae
--



--
-- Name: s_ae_accion; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_accion', 1, false);


--
-- Name: s_ae_acciondato; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_acciondato', 1, false);


--
-- Name: s_ae_accionrecurso; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_accionrecurso', 1, false);


--
-- Name: s_ae_agenda; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_agenda', 1, true);


--
-- Name: s_ae_agrupacion_dato; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_agrupacion_dato', 1, true);


--
-- Name: s_ae_anio; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_anio', 1, false);


--
-- Name: s_ae_atencion; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_atencion', 1, true);


--
-- Name: s_ae_comunicaciones; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_comunicaciones', 1, true);


--
-- Name: s_ae_constval; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_constval', 1, false);


--
-- Name: s_ae_datoasolicitar; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_datoasolicitar', 1, true);


--
-- Name: s_ae_datodelrecurso; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_datodelrecurso', 1, true);


--
-- Name: s_ae_datoreserva; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_datoreserva', 1, true);


--
-- Name: s_ae_dia_mes; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_dia_mes', 1, false);


--
-- Name: s_ae_dia_semana; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_dia_semana', 1, false);


--
-- Name: s_ae_disponibilidad; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_disponibilidad', 1, true);


--
-- Name: s_ae_llamada; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_llamada', 1, true);


--
-- Name: s_ae_mes; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_mes', 1, false);


--
-- Name: s_ae_paramaccion; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_paramaccion', 1, false);


--
-- Name: s_ae_parametros_autocompletar; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_parametros_autocompletar', 1, false);


--
-- Name: s_ae_paramval; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_paramval', 1, false);


--
-- Name: s_ae_plantilla; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_plantilla', 1, false);


--
-- Name: s_ae_recurso; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_recurso', 1, true);


--
-- Name: s_ae_reserva; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_reserva', 1, true);


--
-- Name: s_ae_serv_autocompletar; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_serv_autocompletar', 1, false);


--
-- Name: s_ae_servdato; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_servdato', 1, false);


--
-- Name: s_ae_servrecurso; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_servrecurso', 1, false);


--
-- Name: s_ae_texto_agenda; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_texto_agenda', 1, true);


--
-- Name: s_ae_textorecurso; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_textorecurso', 1, true);


--
-- Name: s_ae_valconstante; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_valconstante', 1, false);


--
-- Name: s_ae_valdato; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_valdato', 1, false);


--
-- Name: s_ae_validacion; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_validacion', 1, false);


--
-- Name: s_ae_valorposible; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_valorposible', 1, true);


--
-- Name: s_ae_valrecurso; Type: SEQUENCE SET; Schema: {empresa}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_valrecurso', 1, false);


--
-- Name: ae_acciones_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_acciones
    ADD CONSTRAINT ae_acciones_pkey PRIMARY KEY (id);


--
-- Name: ae_acciones_por_dato_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_acciones_por_dato
    ADD CONSTRAINT ae_acciones_por_dato_pkey PRIMARY KEY (id);


--
-- Name: ae_acciones_por_recurso_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_acciones_por_recurso
    ADD CONSTRAINT ae_acciones_por_recurso_pkey PRIMARY KEY (id);


--
-- Name: ae_agendas_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_agendas
    ADD CONSTRAINT ae_agendas_pkey PRIMARY KEY (id);


--
-- Name: ae_agrupaciones_datos_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_agrupaciones_datos
    ADD CONSTRAINT ae_agrupaciones_datos_pkey PRIMARY KEY (id);


--
-- Name: ae_anios_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_anios
    ADD CONSTRAINT ae_anios_pkey PRIMARY KEY (id);


--
-- Name: ae_atencion_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_atencion
    ADD CONSTRAINT ae_atencion_pkey PRIMARY KEY (id);


--
-- Name: ae_captchas_pk; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_frases_captcha
    ADD CONSTRAINT ae_captchas_pk PRIMARY KEY (clave);


--
-- Name: ae_comunicaciones_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_comunicaciones
    ADD CONSTRAINT ae_comunicaciones_pkey PRIMARY KEY (id);


--
-- Name: ae_constante_validacion_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_constante_validacion
    ADD CONSTRAINT ae_constante_validacion_pkey PRIMARY KEY (id);


--
-- Name: ae_datos_a_solicitar_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_datos_a_solicitar
    ADD CONSTRAINT ae_datos_a_solicitar_pkey PRIMARY KEY (id);


--
-- Name: ae_datos_del_recurso_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_datos_del_recurso
    ADD CONSTRAINT ae_datos_del_recurso_pkey PRIMARY KEY (id);


--
-- Name: ae_datos_reserva_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_datos_reserva
    ADD CONSTRAINT ae_datos_reserva_pkey PRIMARY KEY (id);


--
-- Name: ae_dias_del_mes_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_dias_del_mes
    ADD CONSTRAINT ae_dias_del_mes_pkey PRIMARY KEY (id);


--
-- Name: ae_dias_semana_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_dias_semana
    ADD CONSTRAINT ae_dias_semana_pkey PRIMARY KEY (id);


--
-- Name: ae_disponibilidades_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_disponibilidades
    ADD CONSTRAINT ae_disponibilidades_pkey PRIMARY KEY (id);


--
-- Name: ae_llamadas_aers_id_key; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_llamadas
    ADD CONSTRAINT ae_llamadas_aers_id_key UNIQUE (aers_id);


--
-- Name: ae_llamadas_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_llamadas
    ADD CONSTRAINT ae_llamadas_pkey PRIMARY KEY (id);


--
-- Name: ae_meses_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_meses
    ADD CONSTRAINT ae_meses_pkey PRIMARY KEY (id);


--
-- Name: ae_parametros_accion_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_parametros_accion
    ADD CONSTRAINT ae_parametros_accion_pkey PRIMARY KEY (id);


--
-- Name: ae_parametros_autocompletar_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_parametros_autocompletar
    ADD CONSTRAINT ae_parametros_autocompletar_pkey PRIMARY KEY (id);


--
-- Name: ae_parametros_validacion_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_parametros_validacion
    ADD CONSTRAINT ae_parametros_validacion_pkey PRIMARY KEY (id);


--
-- Name: ae_plantillas_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_plantillas
    ADD CONSTRAINT ae_plantillas_pkey PRIMARY KEY (id);


--
-- Name: ae_preguntas_pk; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_preguntas_captcha
    ADD CONSTRAINT ae_preguntas_pk PRIMARY KEY (clave);


--
-- Name: ae_recursos_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_recursos
    ADD CONSTRAINT ae_recursos_pkey PRIMARY KEY (id);


--
-- Name: ae_reservas_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_reservas
    ADD CONSTRAINT ae_reservas_pkey PRIMARY KEY (id);


--
-- Name: ae_serv_autocomp_por_dato_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_serv_autocomp_por_dato
    ADD CONSTRAINT ae_serv_autocomp_por_dato_pkey PRIMARY KEY (id);


--
-- Name: ae_serv_autocompletar_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_serv_autocompletar
    ADD CONSTRAINT ae_serv_autocompletar_pkey PRIMARY KEY (id);


--
-- Name: ae_servicio_por_recurso_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_servicio_por_recurso
    ADD CONSTRAINT ae_servicio_por_recurso_pkey PRIMARY KEY (id);


--
-- Name: ae_textos_agenda_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_textos_agenda
    ADD CONSTRAINT ae_textos_agenda_pkey PRIMARY KEY (id);


--
-- Name: ae_textos_pk; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_textos
    ADD CONSTRAINT ae_textos_pk PRIMARY KEY (codigo, idioma);


--
-- Name: ae_textos_recurso_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_textos_recurso
    ADD CONSTRAINT ae_textos_recurso_pkey PRIMARY KEY (id);


--
-- Name: ae_validaciones_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_validaciones
    ADD CONSTRAINT ae_validaciones_pkey PRIMARY KEY (id);


--
-- Name: ae_validaciones_por_dato_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_validaciones_por_dato
    ADD CONSTRAINT ae_validaciones_por_dato_pkey PRIMARY KEY (id);


--
-- Name: ae_validaciones_por_recurso_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_validaciones_por_recurso
    ADD CONSTRAINT ae_validaciones_por_recurso_pkey PRIMARY KEY (id);


--
-- Name: ae_valor_constante_val_rec_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_valor_constante_val_rec
    ADD CONSTRAINT ae_valor_constante_val_rec_pkey PRIMARY KEY (id);


--
-- Name: ae_valores_del_dato_pkey; Type: CONSTRAINT; Schema: {empresa}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_valores_del_dato
    ADD CONSTRAINT ae_valores_del_dato_pkey PRIMARY KEY (id);


--
-- Name: fk1c09bf24104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_disponibilidades
    ADD CONSTRAINT fk1c09bf24104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: fk1c09bf249a9bb7b2; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_disponibilidades
    ADD CONSTRAINT fk1c09bf249a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- Name: fk28a15dc69a9bb7b2; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_dias_semana
    ADD CONSTRAINT fk28a15dc69a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- Name: fk3360aa44104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_agrupaciones_datos
    ADD CONSTRAINT fk3360aa44104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: fk3ce7cc09104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_datos_a_solicitar
    ADD CONSTRAINT fk3ce7cc09104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: fk3ce7cc091876ae95; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_datos_a_solicitar
    ADD CONSTRAINT fk3ce7cc091876ae95 FOREIGN KEY (aead_id) REFERENCES ae_agrupaciones_datos(id);


--
-- Name: fk3e0b63a4cc9035ed; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_parametros_autocompletar
    ADD CONSTRAINT fk3e0b63a4cc9035ed FOREIGN KEY (aeserv_id) REFERENCES ae_serv_autocompletar(id);


--
-- Name: fk3f09314323ebf200; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_constante_validacion
    ADD CONSTRAINT fk3f09314323ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);


--
-- Name: fk42202b5436760caf; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_valores_del_dato
    ADD CONSTRAINT fk42202b5436760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- Name: fk4da30a11e4b40066; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_parametros_accion
    ADD CONSTRAINT fk4da30a11e4b40066 FOREIGN KEY (aeac_id) REFERENCES ae_acciones(id);


--
-- Name: fk5ff42436104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_datos_del_recurso
    ADD CONSTRAINT fk5ff42436104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: fk66d0a85036760caf; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_validaciones_por_dato
    ADD CONSTRAINT fk66d0a85036760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- Name: fk66d0a8508d2f46a5; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_validaciones_por_dato
    ADD CONSTRAINT fk66d0a8508d2f46a5 FOREIGN KEY (aevr_id) REFERENCES ae_validaciones_por_recurso(id);


--
-- Name: fk6e5144f336760caf; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_acciones_por_dato
    ADD CONSTRAINT fk6e5144f336760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- Name: fk6e5144f362f9440d; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_acciones_por_dato
    ADD CONSTRAINT fk6e5144f362f9440d FOREIGN KEY (aear_id) REFERENCES ae_acciones_por_recurso(id);


--
-- Name: fk79b9a11211242882; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_reservas_disponibilidades
    ADD CONSTRAINT fk79b9a11211242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);


--
-- Name: fk79b9a112406004b7; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_reservas_disponibilidades
    ADD CONSTRAINT fk79b9a112406004b7 FOREIGN KEY (aedi_id) REFERENCES ae_disponibilidades(id);


--
-- Name: fk8a30e71e8d2f46a5; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_valor_constante_val_rec
    ADD CONSTRAINT fk8a30e71e8d2f46a5 FOREIGN KEY (aevr_id) REFERENCES ae_validaciones_por_recurso(id);


--
-- Name: fk96b86f5fe4ef2a07; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_textos_agenda
    ADD CONSTRAINT fk96b86f5fe4ef2a07 FOREIGN KEY (aeag_id) REFERENCES ae_agendas(id);


--
-- Name: fk9e323ab1104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_validaciones_por_recurso
    ADD CONSTRAINT fk9e323ab1104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: fk9e323ab123ebf200; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_validaciones_por_recurso
    ADD CONSTRAINT fk9e323ab123ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);


--
-- Name: fk9ecc9f5911242882; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_datos_reserva
    ADD CONSTRAINT fk9ecc9f5911242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);


--
-- Name: fk9ecc9f5936760caf; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_datos_reserva
    ADD CONSTRAINT fk9ecc9f5936760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- Name: fka0da2cfc104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_llamadas
    ADD CONSTRAINT fka0da2cfc104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: fka0da2cfc11242882; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_llamadas
    ADD CONSTRAINT fka0da2cfc11242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);


--
-- Name: fka1b7fd05e4ef2a07; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_recursos
    ADD CONSTRAINT fka1b7fd05e4ef2a07 FOREIGN KEY (aeag_id) REFERENCES ae_agendas(id);


--
-- Name: fkacfc169736760caf; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_serv_autocomp_por_dato
    ADD CONSTRAINT fkacfc169736760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- Name: fkacfc1697bcb2c28e; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_serv_autocomp_por_dato
    ADD CONSTRAINT fkacfc1697bcb2c28e FOREIGN KEY (aesr_id) REFERENCES ae_servicio_por_recurso(id);


--
-- Name: fkade6372e104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_acciones_por_recurso
    ADD CONSTRAINT fkade6372e104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: fkade6372ee4b40066; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_acciones_por_recurso
    ADD CONSTRAINT fkade6372ee4b40066 FOREIGN KEY (aeac_id) REFERENCES ae_acciones(id);


--
-- Name: fkc717af5423ebf200; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_parametros_validacion
    ADD CONSTRAINT fkc717af5423ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);


--
-- Name: fkd1fddf1a9a9bb7b2; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_dias_del_mes
    ADD CONSTRAINT fkd1fddf1a9a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- Name: fkd75adbaf104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_servicio_por_recurso
    ADD CONSTRAINT fkd75adbaf104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: fkd75adbafcc9035ed; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_servicio_por_recurso
    ADD CONSTRAINT fkd75adbafcc9035ed FOREIGN KEY (aeserv_id) REFERENCES ae_serv_autocompletar(id);


--
-- Name: fkd841909c11242882; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_atencion
    ADD CONSTRAINT fkd841909c11242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);


--
-- Name: fke2ce44e59a9bb7b2; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_anios
    ADD CONSTRAINT fke2ce44e59a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- Name: fke3736bee9a9bb7b2; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_meses
    ADD CONSTRAINT fke3736bee9a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- Name: fkf9c6590b104398e1; Type: FK CONSTRAINT; Schema: {empresa}; Owner: sae
--

ALTER TABLE ONLY ae_plantillas
    ADD CONSTRAINT fkf9c6590b104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- Name: {empresa}; Type: ACL; Schema: -; Owner: sae
--

REVOKE ALL ON SCHEMA {empresa} FROM PUBLIC;
REVOKE ALL ON SCHEMA {empresa} FROM sae;
GRANT ALL ON SCHEMA {empresa} TO sae;
GRANT ALL ON SCHEMA {empresa} TO postgres;
GRANT ALL ON SCHEMA {empresa} TO PUBLIC;


--
-- PostgreSQL database dump complete
--

