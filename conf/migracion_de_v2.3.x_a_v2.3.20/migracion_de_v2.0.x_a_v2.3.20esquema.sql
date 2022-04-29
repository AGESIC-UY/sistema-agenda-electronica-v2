
-- =======================================================================================================================
-- 2.0.1

ALTER TABLE {esquema}.ae_recursos ADD COLUMN fuente_ticket character varying(100) NOT NULL DEFAULT 'helvetica';
ALTER TABLE {esquema}.ae_recursos ADD COLUMN tamanio_fuente_grande integer NOT NULL DEFAULT 12;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN tamanio_fuente_normal integer NOT NULL DEFAULT 10;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN tamanio_fuente_chica integer NOT NULL DEFAULT 8;

-- =======================================================================================================================
-- 2.0.2

-- Nada para hacer

-- =======================================================================================================================
-- 2.0.3

-- Nada para hacer

-- =======================================================================================================================
-- 2.0.4

-- Nada para hacer

-- =======================================================================================================================
-- 2.0.5

-- Nada para hacer

-- =======================================================================================================================
-- 2.0.6

-- Nada para hacer

-- =======================================================================================================================
-- 2.0.7

-- Nada para hacer

-- =======================================================================================================================
-- 2.0.8

-- Nada para hacer

-- =======================================================================================================================
-- 2.0.8.R1

-- Nada para hacer

-- =======================================================================================================================
-- 2.1.0

ALTER TABLE {esquema}.ae_recursos ADD COLUMN multiple_admite boolean NOT NULL DEFAULT false;
ALTER TABLE {esquema}.ae_recursos DROP COLUMN reserva_multiple;

CREATE TABLE {esquema}.ae_tokens_reservas (
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
	CONSTRAINT ae_tokens_reservas_pkey PRIMARY KEY (id),
	CONSTRAINT ae_tokens_reservas_recurso FOREIGN KEY (aere_id) REFERENCES {esquema}.ae_recursos(id)
) ;

ALTER TABLE {esquema}.ae_tokens_reservas OWNER TO sae;

CREATE SEQUENCE {esquema}.s_ae_token_reserva INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1;

ALTER SEQUENCE {esquema}.s_ae_token_reserva OWNER TO sae;

ALTER TABLE {esquema}.ae_reservas ADD COLUMN aetr_id int4 NULL DEFAULT NULL;
ALTER TABLE {esquema}.ae_reservas ADD CONSTRAINT ae_reservas_token FOREIGN KEY (aetr_id) REFERENCES {esquema}.ae_tokens_reservas(id);
CREATE INDEX ae_reservas_aetr_id_idx ON {esquema}.ae_reservas (aetr_id) ;

alter table {esquema}.ae_comunicaciones alter column reserva_id drop not null;
alter table {esquema}.ae_comunicaciones add column token_id int4;

-- =======================================================================================================================
-- 2.1.1

ALTER TABLE {esquema}.ae_recursos ADD COLUMN cambios_admite boolean NOT NULL DEFAULT false;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN cambios_tiempo integer DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN cambios_unidad integer DEFAULT null;

-- =======================================================================================================================
-- 2.2.0

ALTER TABLE {esquema}.ae_recursos ADD COLUMN periodo_validacion integer NOT NULL DEFAULT 0;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN validar_por_ip boolean NOT NULL DEFAULT false;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN cantidad_por_ip integer DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN periodo_por_ip integer DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN ips_sin_validacion varchar(4000) DEFAULT NULL;

ALTER TABLE {esquema}.ae_recursos ADD COLUMN cancela_tiempo integer NOT NULL DEFAULT 0;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN cancela_unidad integer NOT NULL DEFAULT 12;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN cancela_tipo varchar(1) NOT NULL DEFAULT 'I';

ALTER TABLE {esquema}.ae_reservas ADD COLUMN ip_origen varchar(16) DEFAULT NULL;
ALTER TABLE {esquema}.ae_reservas ADD COLUMN flibera timestamp DEFAULT NULL;
UPDATE {esquema}.ae_reservas set flibera = fcancela where estado='C';

ALTER TABLE {esquema}.ae_tokens_reservas ADD COLUMN ip_origen varchar(16) DEFAULT NULL;

-- =======================================================================================================================
-- 2.2.1

-- Nada para hacer

-- =======================================================================================================================
-- 2.3.0

ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_con_hab boolean NOT NULL DEFAULT true;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_can_hab boolean NOT NULL DEFAULT true;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_rec_hab boolean NOT NULL DEFAULT true;

ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_con_tit varchar(25) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_con_cor varchar(255) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_con_lar varchar(1024) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_con_ven integer DEFAULT null;

ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_can_tit varchar(25) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_can_cor varchar(255) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_can_lar varchar(1024) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_can_ven integer DEFAULT null;

ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_rec_tit varchar(25) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_rec_cor varchar(255) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_rec_lar varchar(1024) DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_rec_ven integer DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_rec_hora integer DEFAULT null;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN mi_perfil_rec_dias integer DEFAULT null;

ALTER TABLE {esquema}.ae_reservas ADD COLUMN mi_perfil_notif boolean NOT NULL DEFAULT true;

-- =======================================================================================================================
-- 2.3.1


CREATE TABLE {esquema}.ae_acciones_miperfil_recurso
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
   
   CONSTRAINT ae_acciones_miperfil_recurso_pkey PRIMARY KEY (id),
   CONSTRAINT accion_recurso_fkey FOREIGN KEY (recurso_id) 
   REFERENCES ae_recursos (id) 
   ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE {esquema}.ae_acciones_miperfil_recurso OWNER TO sae;

CREATE SEQUENCE {esquema}.s_ae_acciones_miperfil;
ALTER TABLE {esquema}.s_ae_acciones_miperfil OWNER TO sae;

ALTER TABLE {esquema}.ae_recursos ALTER COLUMN mi_perfil_con_tit TYPE varchar(255);
ALTER TABLE {esquema}.ae_recursos ALTER COLUMN mi_perfil_can_tit TYPE varchar(255);
ALTER TABLE {esquema}.ae_recursos ALTER COLUMN mi_perfil_rec_tit TYPE varchar(255);

INSERT INTO {esquema}.ae_acciones_miperfil_recurso(
            id, recurso_id, titulo_con_1, url_con_1, destacada_con_1, titulo_con_2, 
            url_con_2, destacada_con_2, titulo_con_3, url_con_3, destacada_con_3, 
            titulo_con_4, url_con_4, destacada_con_4, titulo_con_5, url_con_5, 
            destacada_con_5, titulo_can_1, url_can_1, destacada_can_1, titulo_can_2, 
            url_can_2, destacada_can_2, titulo_can_3, url_can_3, destacada_can_3, 
            titulo_can_4, url_can_4, destacada_can_4, titulo_can_5, url_can_5, 
            destacada_can_5, titulo_rec_1, url_rec_1, destacada_rec_1, titulo_rec_2, 
            url_rec_2, destacada_rec_2, titulo_rec_3, url_rec_3, destacada_rec_3, 
            titulo_rec_4, url_rec_4, destacada_rec_4, titulo_rec_5, url_rec_5, 
            destacada_rec_5) 
            select nextval('s_ae_acciones_miperfil'), r.id, 'Ir a ubicacion', 'https://www.google.com.uy/maps/@{latitud},{longitud},15z', true,'Cancelar reserva','{linkBase}/sae/cancelarReserva/Paso1.xhtml?e={empresa}&a={agenda}&ri={reserva}', false,'','', false,'', '', false,'', '', false,
            'Ir a ubicacion', 'https://www.google.com.uy/maps/@{latitud},{longitud},15z', true,'', '', false,'', '', false,'', '', false,'', '', false,
            'Ir a ubicacion', 'https://www.google.com.uy/maps/@{latitud},{longitud},15z', true,'Cancelar reserva', '{linkBase}/sae/cancelarReserva/Paso1.xhtml?e={empresa}&a={agenda}&ri={reserva}', false,'', '', false,'', '', false,'', '', false 
            from sae.ae_recursos r; 

-- =======================================================================================================================
-- 2.3.2

-- Nada para hacer
			
			
-- =======================================================================================================================
-- 2.3.3

ALTER TABLE {esquema}.ae_reservas ADD COLUMN notificar boolean DEFAULT true;

-- =======================================================================================================================
-- 2.3.4

-- Nada para hacer

-- =======================================================================================================================
-- 2.3.4.1

-- Nada para hacer

-- =======================================================================================================================
-- 2.3.4.2

-- Nada para hacer
-- =======================================================================================================================
-- 2.3.5

-- Nada para hacer
- =======================================================================================================================
-- 2.3.6

-- Nada para hacer

-- =======================================================================================================================
-- 2.3.7

-- RQF-01: Usuario Administrador de recursos.
INSERT INTO "global".ae_textos (codigo, texto) VALUES('administradorDeRecursos', 'Administrador de recursos');


-- RQF-02: Servicio Rest para consultar modificaciones en recursos.

CREATE TABLE {esquema}.ae_recursos_aud (
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
	
	fecha_modificacion timestamp NULL,
	usuario varchar(45) NULL,
	"version" int4 NOT NULL,
	tipo_operacion int2 null, -- 0 creación, 1 modificación y 2 eliminación;,
	CONSTRAINT ae_recursos_aud_pkey PRIMARY KEY (id)
);

ALTER TABLE {esquema}.ae_recursos_aud OWNER TO sae;

CREATE SEQUENCE {esquema}.s_ae_recurso_aud
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807;
	
ALTER SEQUENCE {esquema}.s_ae_recurso_aud OWNER TO sae;

-- =======================================================================================================================

-- 2.3.8

-- Nada para hacer

-- =======================================================================================================================

-- 2.3.9

-- Nada para hacer

-- =======================================================================================================================

-- 2.3.10

-- Ejecutar este script especificando el esquema de base de datos de cada empresa, se agrega la columna reserva_hija_id en la tabla ae_reservas

ALTER TABLE {esquema}.ae_reservas ADD COLUMN reserva_hija_id int4 NULL; 
	
-- =======================================================================================================================
-- 2.3.11

-- Ejecutar este script especificando el esquema de base de datos de cada empresa, se agrega la columna incluir_en_novedades a la tabla ae_datos_solicitar

ALTER TABLE {esquema}.ae_datos_a_solicitar ADD COLUMN incluir_en_novedades boolean DEFAULT false NOT NULL;
	
-- =======================================================================================================================
-- 2.3.12

-- Nada para hacer

-- =======================================================================================================================
-- 2.3.13

-- Nada para hacer

-- =======================================================================================================================
-- 2.3.14
-- Ejecutar este script especificando el esquema de base de datos de cada empresa, se agregan dos columnas en la tabla ae_recursos y ae_recursos_aud
ALTER TABLE {esquema}.ae_recursos ADD COLUMN reserva_pen_tiempo_max integer DEFAULT NULL;
ALTER TABLE {esquema}.ae_recursos_aud ADD COLUMN reserva_pend_tiempo_max integer DEFAULT NULL;
ALTER TABLE {esquema}.ae_recursos ADD COLUMN reserva_multiple_pend_tiempo_max integer DEFAULT NULL;
ALTER TABLE {esquema}.ae_recursos_aud ADD COLUMN reserva_multiple_pend_tiempo_max integer DEFAULT NULL;

-- Ejecutar este script especificando el esquema de base de datos de cada empresa para actualizar los dos nuevos parámetros en todos los recursos de una empresa
-- (cambiar los valores de 10 por los valores reales)
update {esquema}.ae_recursos set reserva_pen_tiempo_max=10;
update {esquema}.ae_recursos set reserva_multiple_pend_tiempo_max=10;

-- Ejecutar este script especificando el esquema de base de datos de cada empresa para actualizar los dos nuevos parámetros en los recursos de una agenda de una empresa
-- (cambiar los valores de 10 por los valores reales)
update {esquema}.ae_recursos set reserva_pen_tiempo_max=10 where aeag_id=10;
update {esquema}.ae_recursos set reserva_multiple_pend_tiempo_max=10 where aeag_id=10;

-- =======================================================================================================================

-- =======================================================================================================================
-- 2.3.15

ALTER TABLE {esquema}.ae_reservas ADD id_origen int4 NULL;

ALTER TABLE {esquema}.ae_textos_agenda ADD texto_correo_tras varchar(1000) NULL;

-- =======================================================================================================================
-- 2.3.15-R1

alter table {esquema}.ae_recursos alter column mi_perfil_con_cor set data type varchar(500);
alter table {esquema}.ae_recursos alter column mi_perfil_can_cor set data type varchar(500);
alter table {esquema}.ae_recursos alter column mi_perfil_rec_cor set data type varchar(500);

alter table {esquema}.ae_recursos alter column mi_perfil_con_hab set default false;
alter table {esquema}.ae_recursos alter column mi_perfil_can_hab set default false;
alter table {esquema}.ae_recursos alter column mi_perfil_rec_hab set default false;

-- =======================================================================================================================
-- 2.3.15-R2

-- Nada para hacer

-- =======================================================================================================================
-- 2.3.16

-- Nada para hacer

-- 2.3.16 - R1

CREATE INDEX ae_reservas_estado_idx ON {esquema}.ae_reservas USING btree (estado);
CREATE INDEX ae_reservas_fcrea_idx ON {esquema}.ae_reservas USING btree (fcrea);
CREATE INDEX ae_disponibilidades_aere_id_idx ON {esquema}.ae_disponibilidades USING btree (aere_id);
CREATE INDEX ae_datos_reserva_aers_id_idx ON {esquema}.ae_datos_reserva USING btree (aers_id);
CREATE INDEX ae_datos_reserva_aeds_id_idx ON {esquema}.ae_datos_reserva USING btree (aeds_id);
CREATE INDEX ae_datos_reserva_valor_idx ON {esquema}.ae_datos_reserva USING btree (valor);
CREATE INDEX ae_disponibilidades_fecha_idx ON {esquema}.ae_disponibilidades USING btree (fecha);
CREATE INDEX ae_datos_a_solicitar_nombre_idx ON {esquema}.ae_datos_a_solicitar USING btree (nombre);

-- 2.3.17

CREATE INDEX ae_tokens_reservas_aere_id_idx ON {esquema}.ae_tokens_reservas (aere_id);
CREATE INDEX ae_tokens_reservas_ultima_reserva_idx ON {esquema}.ae_tokens_reservas (ultima_reserva);
CREATE INDEX ae_tokens_reservas_fecha_inicio_idx ON {esquema}.ae_tokens_reservas (fecha_inicio);
CREATE INDEX ae_tokens_reservas_estado_idx ON {esquema}.ae_tokens_reservas (estado);

-- 2.3.17 R1

-- Nada para hacer

-- 2.3.18

-- Nada para hacer

-- 2.3.19

-- Nada para hacer

-- 2.3.20

-- Nada para hacer