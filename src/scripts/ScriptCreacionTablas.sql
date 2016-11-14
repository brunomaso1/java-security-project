CREATE DATABASE "SeguridadDB"
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'es_UY.UTF-8'
       LC_CTYPE = 'es_UY.UTF-8'
       CONNECTION LIMIT = -1;
	   
create table rol (
	id_rol serial primary key,
	descripcion varchar(20)
);

create table permiso (
	id_permiso smallint primary key,
	descripcion varchar(30) unique
);

﻿create table usuario (
	id_usuario serial primary key,
	nombre varchar(50),
	nombre_usuario varchar(30) unique,
	password varchar(100),
	id_rol smallint,
	foreign key (id_rol) references rol (id_rol)
);

create table log (
	id_usuario smallint,
	accion varchar(100),
	fecha varchar(8),
	hora varchar(6),
	foreign key (id_usuario) references usuario (id_usuario)
);

create table rol_permiso (
	id_rol smallint,
	id_permiso smallint,
	primary key(id_rol, id_permiso),
	foreign key (id_rol) references rol (id_rol),
	foreign key (id_permiso) references permiso (id_permiso)
);

INSERT INTO permiso VALUES (1, 'Encriptar');
INSERT INTO permiso VALUES (2, 'Desencriptar');
INSERT INTO permiso VALUES (3, 'Otorgar permisos');
INSERT INTO permiso VALUES (4, 'Quitar permisos');

INSERT INTO rol (descripcion) VALUES ('Administrador');
INSERT INTO rol (descripcion) VALUES ('Operario');

INSERT INTO rol_permiso VALUES (1, 3);
INSERT INTO rol_permiso VALUES (1, 4);

insert into usuario (nombre, nombre_usuario, password, id_rol) values ('Juan Martin', 'jm', '7a51d064a1a216a692f753fcdab276e4ff201a01d8b66f56d50d4d719fd0dc87', 1);
insert into usuario (nombre, nombre_usuario, password, id_rol) values ('Guzmán Olivera', 'guzman', '7a51d064a1a216a692f753fcdab276e4ff201a01d8b66f56d50d4d719fd0dc87', 2)


