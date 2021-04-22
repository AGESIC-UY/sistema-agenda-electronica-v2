
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


