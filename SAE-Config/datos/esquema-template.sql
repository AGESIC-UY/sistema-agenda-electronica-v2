--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.5
-- Dumped by pg_dump version 9.4.5
-- Started on 2016-08-31 15:50:03 ART

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 8 (class 2615 OID 24578)
-- Name: {esquema}; Type: SCHEMA; Schema: -; Owner: sae
--

CREATE SCHEMA {esquema};


ALTER SCHEMA {esquema} OWNER TO sae;

SET search_path = {esquema}, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 183 (class 1259 OID 24582)
-- Name: ae_acciones; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 184 (class 1259 OID 24588)
-- Name: ae_acciones_por_dato; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 185 (class 1259 OID 24591)
-- Name: ae_acciones_por_recurso; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 186 (class 1259 OID 24594)
-- Name: ae_agendas; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 187 (class 1259 OID 24597)
-- Name: ae_agrupaciones_datos; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 188 (class 1259 OID 24600)
-- Name: ae_anios; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_anios (
    id integer NOT NULL,
    anio integer NOT NULL,
    aepl_id integer NOT NULL
);


ALTER TABLE ae_anios OWNER TO sae;

--
-- TOC entry 189 (class 1259 OID 24603)
-- Name: ae_atencion; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 455 (class 1259 OID 28059)
-- Name: ae_comunicaciones; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 190 (class 1259 OID 24606)
-- Name: ae_constante_validacion; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 191 (class 1259 OID 24609)
-- Name: ae_datos_a_solicitar; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 192 (class 1259 OID 24613)
-- Name: ae_datos_del_recurso; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 193 (class 1259 OID 24616)
-- Name: ae_datos_reserva; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_datos_reserva (
    id integer NOT NULL,
    valor character varying(100) NOT NULL,
    aeds_id integer NOT NULL,
    aers_id integer NOT NULL
);


ALTER TABLE ae_datos_reserva OWNER TO sae;

--
-- TOC entry 194 (class 1259 OID 24619)
-- Name: ae_dias_del_mes; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_dias_del_mes (
    id integer NOT NULL,
    dia_del_mes integer NOT NULL,
    aepl_id integer NOT NULL
);


ALTER TABLE ae_dias_del_mes OWNER TO sae;

--
-- TOC entry 195 (class 1259 OID 24622)
-- Name: ae_dias_semana; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_dias_semana (
    id integer NOT NULL,
    dia_semana integer NOT NULL,
    aepl_id integer NOT NULL
);


ALTER TABLE ae_dias_semana OWNER TO sae;

--
-- TOC entry 196 (class 1259 OID 24625)
-- Name: ae_disponibilidades; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 533 (class 1259 OID 34124)
-- Name: ae_frases_captcha; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_frases_captcha (
    clave character varying(100) NOT NULL,
    frase character varying(1024),
    idioma character varying(5) NOT NULL
);


ALTER TABLE ae_frases_captcha OWNER TO sae;

--
-- TOC entry 197 (class 1259 OID 24628)
-- Name: ae_llamadas; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 198 (class 1259 OID 24631)
-- Name: ae_meses; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_meses (
    id integer NOT NULL,
    mes integer NOT NULL,
    aepl_id integer NOT NULL
);


ALTER TABLE ae_meses OWNER TO sae;

--
-- TOC entry 199 (class 1259 OID 24634)
-- Name: ae_parametros_accion; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 200 (class 1259 OID 24637)
-- Name: ae_parametros_autocompletar; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 201 (class 1259 OID 24640)
-- Name: ae_parametros_validacion; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 202 (class 1259 OID 24643)
-- Name: ae_plantillas; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 674 (class 1259 OID 37296)
-- Name: ae_preguntas_captcha; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_preguntas_captcha (
    clave character varying(100) NOT NULL,
    pregunta character varying(1024),
    respuesta character varying(25),
    idioma character varying(5) NOT NULL
);


ALTER TABLE ae_preguntas_captcha OWNER TO sae;

--
-- TOC entry 203 (class 1259 OID 24646)
-- Name: ae_recursos; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
    longitud numeric,
    mostrar_id_en_ticket boolean
);


ALTER TABLE ae_recursos OWNER TO sae;

--
-- TOC entry 204 (class 1259 OID 24652)
-- Name: ae_reservas; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 205 (class 1259 OID 24655)
-- Name: ae_reservas_disponibilidades; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_reservas_disponibilidades (
    aers_id integer NOT NULL,
    aedi_id integer NOT NULL
);


ALTER TABLE ae_reservas_disponibilidades OWNER TO sae;

--
-- TOC entry 206 (class 1259 OID 24658)
-- Name: ae_serv_autocomp_por_dato; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 207 (class 1259 OID 24661)
-- Name: ae_serv_autocompletar; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 208 (class 1259 OID 24667)
-- Name: ae_servicio_por_recurso; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_servicio_por_recurso (
    id integer NOT NULL,
    fecha_baja timestamp without time zone,
    aeserv_id integer NOT NULL,
    aere_id integer NOT NULL
);


ALTER TABLE ae_servicio_por_recurso OWNER TO sae;

--
-- TOC entry 459 (class 1259 OID 28095)
-- Name: ae_textos; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
--

CREATE TABLE ae_textos (
    codigo character varying(100) NOT NULL,
    idioma character varying(5) NOT NULL,
    texto character varying(4096) NOT NULL
);


ALTER TABLE ae_textos OWNER TO sae;

--
-- TOC entry 209 (class 1259 OID 24670)
-- Name: ae_textos_agenda; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 210 (class 1259 OID 24676)
-- Name: ae_textos_recurso; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 211 (class 1259 OID 24682)
-- Name: ae_validaciones; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 212 (class 1259 OID 24688)
-- Name: ae_validaciones_por_dato; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 213 (class 1259 OID 24691)
-- Name: ae_validaciones_por_recurso; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 214 (class 1259 OID 24694)
-- Name: ae_valor_constante_val_rec; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 215 (class 1259 OID 24697)
-- Name: ae_valores_del_dato; Type: TABLE; Schema: {esquema}; Owner: sae; Tablespace: 
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
-- TOC entry 216 (class 1259 OID 24701)
-- Name: s_ae_accion; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_accion
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_accion OWNER TO sae;

--
-- TOC entry 217 (class 1259 OID 24703)
-- Name: s_ae_acciondato; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_acciondato
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_acciondato OWNER TO sae;

--
-- TOC entry 218 (class 1259 OID 24705)
-- Name: s_ae_accionrecurso; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_accionrecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_accionrecurso OWNER TO sae;

--
-- TOC entry 219 (class 1259 OID 24707)
-- Name: s_ae_agenda; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_agenda
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_agenda OWNER TO sae;

--
-- TOC entry 220 (class 1259 OID 24709)
-- Name: s_ae_agrupacion_dato; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_agrupacion_dato
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_agrupacion_dato OWNER TO sae;

--
-- TOC entry 221 (class 1259 OID 24711)
-- Name: s_ae_anio; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_anio
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_anio OWNER TO sae;

--
-- TOC entry 222 (class 1259 OID 24713)
-- Name: s_ae_atencion; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_atencion
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_atencion OWNER TO sae;

--
-- TOC entry 454 (class 1259 OID 28057)
-- Name: s_ae_comunicaciones; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_comunicaciones
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_comunicaciones OWNER TO sae;

--
-- TOC entry 223 (class 1259 OID 24715)
-- Name: s_ae_constval; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_constval
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_constval OWNER TO sae;

--
-- TOC entry 224 (class 1259 OID 24717)
-- Name: s_ae_datoasolicitar; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_datoasolicitar
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_datoasolicitar OWNER TO sae;

--
-- TOC entry 225 (class 1259 OID 24719)
-- Name: s_ae_datodelrecurso; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_datodelrecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_datodelrecurso OWNER TO sae;

--
-- TOC entry 226 (class 1259 OID 24721)
-- Name: s_ae_datoreserva; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_datoreserva
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_datoreserva OWNER TO sae;

--
-- TOC entry 227 (class 1259 OID 24723)
-- Name: s_ae_dia_mes; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_dia_mes
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_dia_mes OWNER TO sae;

--
-- TOC entry 228 (class 1259 OID 24725)
-- Name: s_ae_dia_semana; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_dia_semana
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_dia_semana OWNER TO sae;

--
-- TOC entry 229 (class 1259 OID 24727)
-- Name: s_ae_disponibilidad; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_disponibilidad
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_disponibilidad OWNER TO sae;

--
-- TOC entry 230 (class 1259 OID 24729)
-- Name: s_ae_llamada; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_llamada
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_llamada OWNER TO sae;

--
-- TOC entry 231 (class 1259 OID 24731)
-- Name: s_ae_mes; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_mes
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_mes OWNER TO sae;

--
-- TOC entry 232 (class 1259 OID 24733)
-- Name: s_ae_paramaccion; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_paramaccion
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_paramaccion OWNER TO sae;

--
-- TOC entry 233 (class 1259 OID 24735)
-- Name: s_ae_parametros_autocompletar; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_parametros_autocompletar
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_parametros_autocompletar OWNER TO sae;

--
-- TOC entry 234 (class 1259 OID 24737)
-- Name: s_ae_paramval; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_paramval
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_paramval OWNER TO sae;

--
-- TOC entry 235 (class 1259 OID 24739)
-- Name: s_ae_plantilla; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_plantilla
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_plantilla OWNER TO sae;

--
-- TOC entry 236 (class 1259 OID 24741)
-- Name: s_ae_recurso; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_recurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_recurso OWNER TO sae;

--
-- TOC entry 237 (class 1259 OID 24743)
-- Name: s_ae_reserva; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_reserva
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_reserva OWNER TO sae;

--
-- TOC entry 238 (class 1259 OID 24745)
-- Name: s_ae_serv_autocompletar; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_serv_autocompletar
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_serv_autocompletar OWNER TO sae;

--
-- TOC entry 239 (class 1259 OID 24747)
-- Name: s_ae_servdato; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_servdato
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_servdato OWNER TO sae;

--
-- TOC entry 240 (class 1259 OID 24749)
-- Name: s_ae_servrecurso; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_servrecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_servrecurso OWNER TO sae;

--
-- TOC entry 241 (class 1259 OID 24751)
-- Name: s_ae_texto_agenda; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_texto_agenda
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_texto_agenda OWNER TO sae;

--
-- TOC entry 242 (class 1259 OID 24753)
-- Name: s_ae_textorecurso; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_textorecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_textorecurso OWNER TO sae;

--
-- TOC entry 243 (class 1259 OID 24755)
-- Name: s_ae_valconstante; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_valconstante
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_valconstante OWNER TO sae;

--
-- TOC entry 244 (class 1259 OID 24757)
-- Name: s_ae_valdato; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_valdato
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_valdato OWNER TO sae;

--
-- TOC entry 245 (class 1259 OID 24759)
-- Name: s_ae_validacion; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_validacion
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_validacion OWNER TO sae;

--
-- TOC entry 246 (class 1259 OID 24761)
-- Name: s_ae_valorposible; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_valorposible
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_valorposible OWNER TO sae;

--
-- TOC entry 247 (class 1259 OID 24763)
-- Name: s_ae_valrecurso; Type: SEQUENCE; Schema: {esquema}; Owner: sae
--

CREATE SEQUENCE s_ae_valrecurso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_ae_valrecurso OWNER TO sae;

--
-- TOC entry 5088 (class 0 OID 24582)
-- Dependencies: 183
-- Data for Name: ae_acciones; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5089 (class 0 OID 24588)
-- Dependencies: 184
-- Data for Name: ae_acciones_por_dato; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5090 (class 0 OID 24591)
-- Dependencies: 185
-- Data for Name: ae_acciones_por_recurso; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5091 (class 0 OID 24594)
-- Dependencies: 186
-- Data for Name: ae_agendas; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5092 (class 0 OID 24597)
-- Dependencies: 187
-- Data for Name: ae_agrupaciones_datos; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5093 (class 0 OID 24600)
-- Dependencies: 188
-- Data for Name: ae_anios; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5094 (class 0 OID 24603)
-- Dependencies: 189
-- Data for Name: ae_atencion; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5154 (class 0 OID 28059)
-- Dependencies: 455
-- Data for Name: ae_comunicaciones; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5095 (class 0 OID 24606)
-- Dependencies: 190
-- Data for Name: ae_constante_validacion; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5096 (class 0 OID 24609)
-- Dependencies: 191
-- Data for Name: ae_datos_a_solicitar; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5097 (class 0 OID 24613)
-- Dependencies: 192
-- Data for Name: ae_datos_del_recurso; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5098 (class 0 OID 24616)
-- Dependencies: 193
-- Data for Name: ae_datos_reserva; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5099 (class 0 OID 24619)
-- Dependencies: 194
-- Data for Name: ae_dias_del_mes; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5100 (class 0 OID 24622)
-- Dependencies: 195
-- Data for Name: ae_dias_semana; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5101 (class 0 OID 24625)
-- Dependencies: 196
-- Data for Name: ae_disponibilidades; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5156 (class 0 OID 34124)
-- Dependencies: 533
-- Data for Name: ae_frases_captcha; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5102 (class 0 OID 24628)
-- Dependencies: 197
-- Data for Name: ae_llamadas; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5103 (class 0 OID 24631)
-- Dependencies: 198
-- Data for Name: ae_meses; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5104 (class 0 OID 24634)
-- Dependencies: 199
-- Data for Name: ae_parametros_accion; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5105 (class 0 OID 24637)
-- Dependencies: 200
-- Data for Name: ae_parametros_autocompletar; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5106 (class 0 OID 24640)
-- Dependencies: 201
-- Data for Name: ae_parametros_validacion; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5107 (class 0 OID 24643)
-- Dependencies: 202
-- Data for Name: ae_plantillas; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5157 (class 0 OID 37296)
-- Dependencies: 674
-- Data for Name: ae_preguntas_captcha; Type: TABLE DATA; Schema: {esquema}; Owner: sae
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
-- TOC entry 5108 (class 0 OID 24646)
-- Dependencies: 203
-- Data for Name: ae_recursos; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5109 (class 0 OID 24652)
-- Dependencies: 204
-- Data for Name: ae_reservas; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5110 (class 0 OID 24655)
-- Dependencies: 205
-- Data for Name: ae_reservas_disponibilidades; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5111 (class 0 OID 24658)
-- Dependencies: 206
-- Data for Name: ae_serv_autocomp_por_dato; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5112 (class 0 OID 24661)
-- Dependencies: 207
-- Data for Name: ae_serv_autocompletar; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5113 (class 0 OID 24667)
-- Dependencies: 208
-- Data for Name: ae_servicio_por_recurso; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5155 (class 0 OID 28095)
-- Dependencies: 459
-- Data for Name: ae_textos; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5114 (class 0 OID 24670)
-- Dependencies: 209
-- Data for Name: ae_textos_agenda; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5115 (class 0 OID 24676)
-- Dependencies: 210
-- Data for Name: ae_textos_recurso; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5116 (class 0 OID 24682)
-- Dependencies: 211
-- Data for Name: ae_validaciones; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5117 (class 0 OID 24688)
-- Dependencies: 212
-- Data for Name: ae_validaciones_por_dato; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5118 (class 0 OID 24691)
-- Dependencies: 213
-- Data for Name: ae_validaciones_por_recurso; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5119 (class 0 OID 24694)
-- Dependencies: 214
-- Data for Name: ae_valor_constante_val_rec; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5120 (class 0 OID 24697)
-- Dependencies: 215
-- Data for Name: ae_valores_del_dato; Type: TABLE DATA; Schema: {esquema}; Owner: sae
--



--
-- TOC entry 5163 (class 0 OID 0)
-- Dependencies: 216
-- Name: s_ae_accion; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_accion', 1, false);


--
-- TOC entry 5164 (class 0 OID 0)
-- Dependencies: 217
-- Name: s_ae_acciondato; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_acciondato', 1, false);


--
-- TOC entry 5165 (class 0 OID 0)
-- Dependencies: 218
-- Name: s_ae_accionrecurso; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_accionrecurso', 1, false);


--
-- TOC entry 5166 (class 0 OID 0)
-- Dependencies: 219
-- Name: s_ae_agenda; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_agenda', 1, true);


--
-- TOC entry 5167 (class 0 OID 0)
-- Dependencies: 220
-- Name: s_ae_agrupacion_dato; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_agrupacion_dato', 1, true);


--
-- TOC entry 5168 (class 0 OID 0)
-- Dependencies: 221
-- Name: s_ae_anio; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_anio', 1, false);


--
-- TOC entry 5169 (class 0 OID 0)
-- Dependencies: 222
-- Name: s_ae_atencion; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_atencion', 1, true);


--
-- TOC entry 5170 (class 0 OID 0)
-- Dependencies: 454
-- Name: s_ae_comunicaciones; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_comunicaciones', 1, true);


--
-- TOC entry 5171 (class 0 OID 0)
-- Dependencies: 223
-- Name: s_ae_constval; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_constval', 1, false);


--
-- TOC entry 5172 (class 0 OID 0)
-- Dependencies: 224
-- Name: s_ae_datoasolicitar; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_datoasolicitar', 1, true);


--
-- TOC entry 5173 (class 0 OID 0)
-- Dependencies: 225
-- Name: s_ae_datodelrecurso; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_datodelrecurso', 1, true);


--
-- TOC entry 5174 (class 0 OID 0)
-- Dependencies: 226
-- Name: s_ae_datoreserva; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_datoreserva', 1, true);


--
-- TOC entry 5175 (class 0 OID 0)
-- Dependencies: 227
-- Name: s_ae_dia_mes; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_dia_mes', 1, false);


--
-- TOC entry 5176 (class 0 OID 0)
-- Dependencies: 228
-- Name: s_ae_dia_semana; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_dia_semana', 1, false);


--
-- TOC entry 5177 (class 0 OID 0)
-- Dependencies: 229
-- Name: s_ae_disponibilidad; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_disponibilidad', 1, true);


--
-- TOC entry 5178 (class 0 OID 0)
-- Dependencies: 230
-- Name: s_ae_llamada; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_llamada', 1, true);


--
-- TOC entry 5179 (class 0 OID 0)
-- Dependencies: 231
-- Name: s_ae_mes; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_mes', 1, false);


--
-- TOC entry 5180 (class 0 OID 0)
-- Dependencies: 232
-- Name: s_ae_paramaccion; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_paramaccion', 1, false);


--
-- TOC entry 5181 (class 0 OID 0)
-- Dependencies: 233
-- Name: s_ae_parametros_autocompletar; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_parametros_autocompletar', 1, false);


--
-- TOC entry 5182 (class 0 OID 0)
-- Dependencies: 234
-- Name: s_ae_paramval; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_paramval', 1, false);


--
-- TOC entry 5183 (class 0 OID 0)
-- Dependencies: 235
-- Name: s_ae_plantilla; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_plantilla', 1, false);


--
-- TOC entry 5184 (class 0 OID 0)
-- Dependencies: 236
-- Name: s_ae_recurso; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_recurso', 1, true);


--
-- TOC entry 5185 (class 0 OID 0)
-- Dependencies: 237
-- Name: s_ae_reserva; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_reserva', 1, true);


--
-- TOC entry 5186 (class 0 OID 0)
-- Dependencies: 238
-- Name: s_ae_serv_autocompletar; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_serv_autocompletar', 1, false);


--
-- TOC entry 5187 (class 0 OID 0)
-- Dependencies: 239
-- Name: s_ae_servdato; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_servdato', 1, false);


--
-- TOC entry 5188 (class 0 OID 0)
-- Dependencies: 240
-- Name: s_ae_servrecurso; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_servrecurso', 1, false);


--
-- TOC entry 5189 (class 0 OID 0)
-- Dependencies: 241
-- Name: s_ae_texto_agenda; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_texto_agenda', 1, true);


--
-- TOC entry 5190 (class 0 OID 0)
-- Dependencies: 242
-- Name: s_ae_textorecurso; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_textorecurso', 1, true);


--
-- TOC entry 5191 (class 0 OID 0)
-- Dependencies: 243
-- Name: s_ae_valconstante; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_valconstante', 1, false);


--
-- TOC entry 5192 (class 0 OID 0)
-- Dependencies: 244
-- Name: s_ae_valdato; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_valdato', 1, false);


--
-- TOC entry 5193 (class 0 OID 0)
-- Dependencies: 245
-- Name: s_ae_validacion; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_validacion', 1, false);


--
-- TOC entry 5194 (class 0 OID 0)
-- Dependencies: 246
-- Name: s_ae_valorposible; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_valorposible', 1, true);


--
-- TOC entry 5195 (class 0 OID 0)
-- Dependencies: 247
-- Name: s_ae_valrecurso; Type: SEQUENCE SET; Schema: {esquema}; Owner: sae
--

SELECT pg_catalog.setval('s_ae_valrecurso', 1, false);


--
-- TOC entry 4868 (class 2606 OID 25359)
-- Name: ae_acciones_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_acciones
    ADD CONSTRAINT ae_acciones_pkey PRIMARY KEY (id);


--
-- TOC entry 4870 (class 2606 OID 25361)
-- Name: ae_acciones_por_dato_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_acciones_por_dato
    ADD CONSTRAINT ae_acciones_por_dato_pkey PRIMARY KEY (id);


--
-- TOC entry 4872 (class 2606 OID 25363)
-- Name: ae_acciones_por_recurso_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_acciones_por_recurso
    ADD CONSTRAINT ae_acciones_por_recurso_pkey PRIMARY KEY (id);


--
-- TOC entry 4874 (class 2606 OID 25365)
-- Name: ae_agendas_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_agendas
    ADD CONSTRAINT ae_agendas_pkey PRIMARY KEY (id);


--
-- TOC entry 4876 (class 2606 OID 25367)
-- Name: ae_agrupaciones_datos_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_agrupaciones_datos
    ADD CONSTRAINT ae_agrupaciones_datos_pkey PRIMARY KEY (id);


--
-- TOC entry 4878 (class 2606 OID 25369)
-- Name: ae_anios_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_anios
    ADD CONSTRAINT ae_anios_pkey PRIMARY KEY (id);


--
-- TOC entry 4880 (class 2606 OID 25371)
-- Name: ae_atencion_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_atencion
    ADD CONSTRAINT ae_atencion_pkey PRIMARY KEY (id);


--
-- TOC entry 4938 (class 2606 OID 34131)
-- Name: ae_captchas_pk; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_frases_captcha
    ADD CONSTRAINT ae_captchas_pk PRIMARY KEY (clave);


--
-- TOC entry 4934 (class 2606 OID 28063)
-- Name: ae_comunicaciones_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_comunicaciones
    ADD CONSTRAINT ae_comunicaciones_pkey PRIMARY KEY (id);


--
-- TOC entry 4882 (class 2606 OID 25373)
-- Name: ae_constante_validacion_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_constante_validacion
    ADD CONSTRAINT ae_constante_validacion_pkey PRIMARY KEY (id);


--
-- TOC entry 4884 (class 2606 OID 25375)
-- Name: ae_datos_a_solicitar_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_datos_a_solicitar
    ADD CONSTRAINT ae_datos_a_solicitar_pkey PRIMARY KEY (id);


--
-- TOC entry 4886 (class 2606 OID 25377)
-- Name: ae_datos_del_recurso_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_datos_del_recurso
    ADD CONSTRAINT ae_datos_del_recurso_pkey PRIMARY KEY (id);


--
-- TOC entry 4888 (class 2606 OID 25379)
-- Name: ae_datos_reserva_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_datos_reserva
    ADD CONSTRAINT ae_datos_reserva_pkey PRIMARY KEY (id);


--
-- TOC entry 4890 (class 2606 OID 25381)
-- Name: ae_dias_del_mes_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_dias_del_mes
    ADD CONSTRAINT ae_dias_del_mes_pkey PRIMARY KEY (id);


--
-- TOC entry 4892 (class 2606 OID 25383)
-- Name: ae_dias_semana_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_dias_semana
    ADD CONSTRAINT ae_dias_semana_pkey PRIMARY KEY (id);


--
-- TOC entry 4894 (class 2606 OID 25385)
-- Name: ae_disponibilidades_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_disponibilidades
    ADD CONSTRAINT ae_disponibilidades_pkey PRIMARY KEY (id);


--
-- TOC entry 4896 (class 2606 OID 25387)
-- Name: ae_llamadas_aers_id_key; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_llamadas
    ADD CONSTRAINT ae_llamadas_aers_id_key UNIQUE (aers_id);


--
-- TOC entry 4898 (class 2606 OID 25389)
-- Name: ae_llamadas_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_llamadas
    ADD CONSTRAINT ae_llamadas_pkey PRIMARY KEY (id);


--
-- TOC entry 4900 (class 2606 OID 25391)
-- Name: ae_meses_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_meses
    ADD CONSTRAINT ae_meses_pkey PRIMARY KEY (id);


--
-- TOC entry 4902 (class 2606 OID 25393)
-- Name: ae_parametros_accion_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_parametros_accion
    ADD CONSTRAINT ae_parametros_accion_pkey PRIMARY KEY (id);


--
-- TOC entry 4904 (class 2606 OID 25395)
-- Name: ae_parametros_autocompletar_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_parametros_autocompletar
    ADD CONSTRAINT ae_parametros_autocompletar_pkey PRIMARY KEY (id);


--
-- TOC entry 4906 (class 2606 OID 25397)
-- Name: ae_parametros_validacion_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_parametros_validacion
    ADD CONSTRAINT ae_parametros_validacion_pkey PRIMARY KEY (id);


--
-- TOC entry 4908 (class 2606 OID 25399)
-- Name: ae_plantillas_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_plantillas
    ADD CONSTRAINT ae_plantillas_pkey PRIMARY KEY (id);


--
-- TOC entry 4940 (class 2606 OID 37303)
-- Name: ae_preguntas_pk; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_preguntas_captcha
    ADD CONSTRAINT ae_preguntas_pk PRIMARY KEY (clave);


--
-- TOC entry 4910 (class 2606 OID 25401)
-- Name: ae_recursos_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_recursos
    ADD CONSTRAINT ae_recursos_pkey PRIMARY KEY (id);


--
-- TOC entry 4912 (class 2606 OID 25403)
-- Name: ae_reservas_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_reservas
    ADD CONSTRAINT ae_reservas_pkey PRIMARY KEY (id);


--
-- TOC entry 4914 (class 2606 OID 25405)
-- Name: ae_serv_autocomp_por_dato_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_serv_autocomp_por_dato
    ADD CONSTRAINT ae_serv_autocomp_por_dato_pkey PRIMARY KEY (id);


--
-- TOC entry 4916 (class 2606 OID 25407)
-- Name: ae_serv_autocompletar_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_serv_autocompletar
    ADD CONSTRAINT ae_serv_autocompletar_pkey PRIMARY KEY (id);


--
-- TOC entry 4918 (class 2606 OID 25409)
-- Name: ae_servicio_por_recurso_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_servicio_por_recurso
    ADD CONSTRAINT ae_servicio_por_recurso_pkey PRIMARY KEY (id);


--
-- TOC entry 4920 (class 2606 OID 25413)
-- Name: ae_textos_agenda_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_textos_agenda
    ADD CONSTRAINT ae_textos_agenda_pkey PRIMARY KEY (id);


--
-- TOC entry 4936 (class 2606 OID 28127)
-- Name: ae_textos_pk; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_textos
    ADD CONSTRAINT ae_textos_pk PRIMARY KEY (codigo, idioma);


--
-- TOC entry 4922 (class 2606 OID 25417)
-- Name: ae_textos_recurso_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_textos_recurso
    ADD CONSTRAINT ae_textos_recurso_pkey PRIMARY KEY (id);


--
-- TOC entry 4924 (class 2606 OID 25419)
-- Name: ae_validaciones_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_validaciones
    ADD CONSTRAINT ae_validaciones_pkey PRIMARY KEY (id);


--
-- TOC entry 4926 (class 2606 OID 25421)
-- Name: ae_validaciones_por_dato_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_validaciones_por_dato
    ADD CONSTRAINT ae_validaciones_por_dato_pkey PRIMARY KEY (id);


--
-- TOC entry 4928 (class 2606 OID 25423)
-- Name: ae_validaciones_por_recurso_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_validaciones_por_recurso
    ADD CONSTRAINT ae_validaciones_por_recurso_pkey PRIMARY KEY (id);


--
-- TOC entry 4930 (class 2606 OID 25425)
-- Name: ae_valor_constante_val_rec_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_valor_constante_val_rec
    ADD CONSTRAINT ae_valor_constante_val_rec_pkey PRIMARY KEY (id);


--
-- TOC entry 4932 (class 2606 OID 25427)
-- Name: ae_valores_del_dato_pkey; Type: CONSTRAINT; Schema: {esquema}; Owner: sae; Tablespace: 
--

ALTER TABLE ONLY ae_valores_del_dato
    ADD CONSTRAINT ae_valores_del_dato_pkey PRIMARY KEY (id);


--
-- TOC entry 4957 (class 2606 OID 25668)
-- Name: fk1c09bf24104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_disponibilidades
    ADD CONSTRAINT fk1c09bf24104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 4956 (class 2606 OID 25673)
-- Name: fk1c09bf249a9bb7b2; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_disponibilidades
    ADD CONSTRAINT fk1c09bf249a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- TOC entry 4955 (class 2606 OID 25678)
-- Name: fk28a15dc69a9bb7b2; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_dias_semana
    ADD CONSTRAINT fk28a15dc69a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- TOC entry 4945 (class 2606 OID 25683)
-- Name: fk3360aa44104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_agrupaciones_datos
    ADD CONSTRAINT fk3360aa44104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 4950 (class 2606 OID 25688)
-- Name: fk3ce7cc09104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_datos_a_solicitar
    ADD CONSTRAINT fk3ce7cc09104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 4949 (class 2606 OID 25693)
-- Name: fk3ce7cc091876ae95; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_datos_a_solicitar
    ADD CONSTRAINT fk3ce7cc091876ae95 FOREIGN KEY (aead_id) REFERENCES ae_agrupaciones_datos(id);


--
-- TOC entry 4962 (class 2606 OID 25698)
-- Name: fk3e0b63a4cc9035ed; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_parametros_autocompletar
    ADD CONSTRAINT fk3e0b63a4cc9035ed FOREIGN KEY (aeserv_id) REFERENCES ae_serv_autocompletar(id);


--
-- TOC entry 4948 (class 2606 OID 25703)
-- Name: fk3f09314323ebf200; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_constante_validacion
    ADD CONSTRAINT fk3f09314323ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);


--
-- TOC entry 4978 (class 2606 OID 25708)
-- Name: fk42202b5436760caf; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_valores_del_dato
    ADD CONSTRAINT fk42202b5436760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- TOC entry 4961 (class 2606 OID 25713)
-- Name: fk4da30a11e4b40066; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_parametros_accion
    ADD CONSTRAINT fk4da30a11e4b40066 FOREIGN KEY (aeac_id) REFERENCES ae_acciones(id);


--
-- TOC entry 4951 (class 2606 OID 25718)
-- Name: fk5ff42436104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_datos_del_recurso
    ADD CONSTRAINT fk5ff42436104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 4974 (class 2606 OID 25723)
-- Name: fk66d0a85036760caf; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_validaciones_por_dato
    ADD CONSTRAINT fk66d0a85036760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- TOC entry 4973 (class 2606 OID 25728)
-- Name: fk66d0a8508d2f46a5; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_validaciones_por_dato
    ADD CONSTRAINT fk66d0a8508d2f46a5 FOREIGN KEY (aevr_id) REFERENCES ae_validaciones_por_recurso(id);


--
-- TOC entry 4942 (class 2606 OID 25733)
-- Name: fk6e5144f336760caf; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_acciones_por_dato
    ADD CONSTRAINT fk6e5144f336760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- TOC entry 4941 (class 2606 OID 25738)
-- Name: fk6e5144f362f9440d; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_acciones_por_dato
    ADD CONSTRAINT fk6e5144f362f9440d FOREIGN KEY (aear_id) REFERENCES ae_acciones_por_recurso(id);


--
-- TOC entry 4967 (class 2606 OID 25743)
-- Name: fk79b9a11211242882; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_reservas_disponibilidades
    ADD CONSTRAINT fk79b9a11211242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);


--
-- TOC entry 4966 (class 2606 OID 25748)
-- Name: fk79b9a112406004b7; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_reservas_disponibilidades
    ADD CONSTRAINT fk79b9a112406004b7 FOREIGN KEY (aedi_id) REFERENCES ae_disponibilidades(id);


--
-- TOC entry 4977 (class 2606 OID 25753)
-- Name: fk8a30e71e8d2f46a5; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_valor_constante_val_rec
    ADD CONSTRAINT fk8a30e71e8d2f46a5 FOREIGN KEY (aevr_id) REFERENCES ae_validaciones_por_recurso(id);


--
-- TOC entry 4972 (class 2606 OID 25758)
-- Name: fk96b86f5fe4ef2a07; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_textos_agenda
    ADD CONSTRAINT fk96b86f5fe4ef2a07 FOREIGN KEY (aeag_id) REFERENCES ae_agendas(id);


--
-- TOC entry 4976 (class 2606 OID 25763)
-- Name: fk9e323ab1104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_validaciones_por_recurso
    ADD CONSTRAINT fk9e323ab1104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 4975 (class 2606 OID 25768)
-- Name: fk9e323ab123ebf200; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_validaciones_por_recurso
    ADD CONSTRAINT fk9e323ab123ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);


--
-- TOC entry 4953 (class 2606 OID 25773)
-- Name: fk9ecc9f5911242882; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_datos_reserva
    ADD CONSTRAINT fk9ecc9f5911242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);


--
-- TOC entry 4952 (class 2606 OID 25778)
-- Name: fk9ecc9f5936760caf; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_datos_reserva
    ADD CONSTRAINT fk9ecc9f5936760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- TOC entry 4959 (class 2606 OID 25783)
-- Name: fka0da2cfc104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_llamadas
    ADD CONSTRAINT fka0da2cfc104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 4958 (class 2606 OID 25788)
-- Name: fka0da2cfc11242882; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_llamadas
    ADD CONSTRAINT fka0da2cfc11242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);


--
-- TOC entry 4965 (class 2606 OID 25793)
-- Name: fka1b7fd05e4ef2a07; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_recursos
    ADD CONSTRAINT fka1b7fd05e4ef2a07 FOREIGN KEY (aeag_id) REFERENCES ae_agendas(id);


--
-- TOC entry 4969 (class 2606 OID 25798)
-- Name: fkacfc169736760caf; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_serv_autocomp_por_dato
    ADD CONSTRAINT fkacfc169736760caf FOREIGN KEY (aeds_id) REFERENCES ae_datos_a_solicitar(id);


--
-- TOC entry 4968 (class 2606 OID 25803)
-- Name: fkacfc1697bcb2c28e; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_serv_autocomp_por_dato
    ADD CONSTRAINT fkacfc1697bcb2c28e FOREIGN KEY (aesr_id) REFERENCES ae_servicio_por_recurso(id);


--
-- TOC entry 4944 (class 2606 OID 25808)
-- Name: fkade6372e104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_acciones_por_recurso
    ADD CONSTRAINT fkade6372e104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 4943 (class 2606 OID 25813)
-- Name: fkade6372ee4b40066; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_acciones_por_recurso
    ADD CONSTRAINT fkade6372ee4b40066 FOREIGN KEY (aeac_id) REFERENCES ae_acciones(id);


--
-- TOC entry 4963 (class 2606 OID 25823)
-- Name: fkc717af5423ebf200; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_parametros_validacion
    ADD CONSTRAINT fkc717af5423ebf200 FOREIGN KEY (aeva_id) REFERENCES ae_validaciones(id);


--
-- TOC entry 4954 (class 2606 OID 25828)
-- Name: fkd1fddf1a9a9bb7b2; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_dias_del_mes
    ADD CONSTRAINT fkd1fddf1a9a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- TOC entry 4971 (class 2606 OID 25833)
-- Name: fkd75adbaf104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_servicio_por_recurso
    ADD CONSTRAINT fkd75adbaf104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 4970 (class 2606 OID 25838)
-- Name: fkd75adbafcc9035ed; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_servicio_por_recurso
    ADD CONSTRAINT fkd75adbafcc9035ed FOREIGN KEY (aeserv_id) REFERENCES ae_serv_autocompletar(id);


--
-- TOC entry 4947 (class 2606 OID 25843)
-- Name: fkd841909c11242882; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_atencion
    ADD CONSTRAINT fkd841909c11242882 FOREIGN KEY (aers_id) REFERENCES ae_reservas(id);


--
-- TOC entry 4946 (class 2606 OID 25848)
-- Name: fke2ce44e59a9bb7b2; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_anios
    ADD CONSTRAINT fke2ce44e59a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- TOC entry 4960 (class 2606 OID 25853)
-- Name: fke3736bee9a9bb7b2; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_meses
    ADD CONSTRAINT fke3736bee9a9bb7b2 FOREIGN KEY (aepl_id) REFERENCES ae_plantillas(id);


--
-- TOC entry 4964 (class 2606 OID 25858)
-- Name: fkf9c6590b104398e1; Type: FK CONSTRAINT; Schema: {esquema}; Owner: sae
--

ALTER TABLE ONLY ae_plantillas
    ADD CONSTRAINT fkf9c6590b104398e1 FOREIGN KEY (aere_id) REFERENCES ae_recursos(id);


--
-- TOC entry 5162 (class 0 OID 0)
-- Dependencies: 8
-- Name: {esquema}; Type: ACL; Schema: -; Owner: sae
--

REVOKE ALL ON SCHEMA {esquema} FROM PUBLIC;
REVOKE ALL ON SCHEMA {esquema} FROM sae;
GRANT ALL ON SCHEMA {esquema} TO sae;
GRANT ALL ON SCHEMA {esquema} TO postgres;
GRANT ALL ON SCHEMA {esquema} TO PUBLIC;


-- Completed on 2016-08-31 15:50:03 ART

--
-- PostgreSQL database dump complete
--

