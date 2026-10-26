-- =====================================================================
-- Base de datos de demo: recolección y procesamiento de PET
-- Motor: MySQL 8.0.16+ 
-- Esquema: `demo` (el mismo de spring.datasource.url)
-- ATENCION: borra y recrea el esquema `demo` completo.
-- Tablas y columnas alineadas con las entidades JPA del proyecto.
-- =====================================================================

DROP DATABASE IF EXISTS demo;
CREATE DATABASE demo CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE demo;

-- ---------------------------------------------------------------------
-- ESTRUCTURA
-- ---------------------------------------------------------------------

CREATE TABLE usuario (
    id_usuario INT         NOT NULL AUTO_INCREMENT,
    rol        VARCHAR(45) NOT NULL,
    nombre     VARCHAR(45) NOT NULL,
    apellido   VARCHAR(45) NOT NULL,
    email      VARCHAR(45) NOT NULL,
    activo               TINYINT(1)  NOT NULL,
    PRIMARY KEY (id_usuario),
    CONSTRAINT chk_usuario_rol
        CHECK (rol IN ('RECOLECTOR', 'OPERADOR', 'GERENTE', 'ADMINISTRADOR'))
) ENGINE = InnoDB;

CREATE TABLE punto_recoleccion (
    id_punto_recoleccion INT         NOT NULL AUTO_INCREMENT,
    nombre               VARCHAR(45) NOT NULL,
    direccion            VARCHAR(45) NOT NULL,
    activo               TINYINT(1)  NOT NULL,
    PRIMARY KEY (id_punto_recoleccion)
) ENGINE = InnoDB;

CREATE TABLE tipo_material (
    id_tipo_material INT          NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(100) NOT NULL,
    es_pet           TINYINT(1)   NOT NULL,
    PRIMARY KEY (id_tipo_material)
) ENGINE = InnoDB;

CREATE TABLE asignacion (
    id_asignacion      INT  NOT NULL AUTO_INCREMENT,
    fecha_desde        DATE NOT NULL,
    fecha_hasta        DATE NULL,
    USUARIO_id_usuario INT  NOT NULL,
    PUNTO_RECOLECCION_id_punto_recoleccion INT NOT NULL,
    PRIMARY KEY (id_asignacion),
    CONSTRAINT fk_asignacion_usuario
        FOREIGN KEY (USUARIO_id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_asignacion_punto
        FOREIGN KEY (PUNTO_RECOLECCION_id_punto_recoleccion) REFERENCES punto_recoleccion (id_punto_recoleccion)
) ENGINE = InnoDB;

CREATE TABLE recoleccion (
    id_recoleccion     INT  NOT NULL AUTO_INCREMENT,
    fecha              DATE NOT NULL,
    USUARIO_id_usuario INT  NOT NULL,
    PUNTO_RECOLECCION_id_punto_recoleccion INT NOT NULL,
    PRIMARY KEY (id_recoleccion),
    CONSTRAINT fk_recoleccion_usuario
        FOREIGN KEY (USUARIO_id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_recoleccion_punto
        FOREIGN KEY (PUNTO_RECOLECCION_id_punto_recoleccion) REFERENCES punto_recoleccion (id_punto_recoleccion)
) ENGINE = InnoDB;

-- cantidad es DOUBLE porque la entidad DetalleRecoleccion usa Double
CREATE TABLE detalle_recoleccion (
    id_detalle_recoleccion INT    NOT NULL AUTO_INCREMENT,
    cantidad               DOUBLE NOT NULL,
    RECOLECCION_id_recoleccion     INT NOT NULL,
    TIPO_MATERIAL_id_tipo_material INT NOT NULL,
    PRIMARY KEY (id_detalle_recoleccion),
    CONSTRAINT fk_detrec_recoleccion
        FOREIGN KEY (RECOLECCION_id_recoleccion) REFERENCES recoleccion (id_recoleccion),
    CONSTRAINT fk_detrec_tipo
        FOREIGN KEY (TIPO_MATERIAL_id_tipo_material) REFERENCES tipo_material (id_tipo_material)
) ENGINE = InnoDB;

-- La cantidad procesada NO es columna: se calcula sumando los detalles.
CREATE TABLE procesamiento (
    id_procesamiento   INT           NOT NULL AUTO_INCREMENT,
    fecha              DATE          NOT NULL,
    cantidad_rechazada DECIMAL(10,2) NOT NULL,
    USUARIO_id_usuario INT           NOT NULL,
    PRIMARY KEY (id_procesamiento),
    CONSTRAINT fk_procesamiento_usuario
        FOREIGN KEY (USUARIO_id_usuario) REFERENCES usuario (id_usuario)
) ENGINE = InnoDB;

CREATE TABLE detalle_procesamiento (
    id_detalle_procesamiento INT           NOT NULL AUTO_INCREMENT,
    cantidad                 DECIMAL(10,2) NOT NULL,
    PROCESAMIENTO_id_procesamiento INT NOT NULL,
    TIPO_MATERIAL_id_tipo_material INT NOT NULL,
    PRIMARY KEY (id_detalle_procesamiento),
    CONSTRAINT fk_detproc_procesamiento
        FOREIGN KEY (PROCESAMIENTO_id_procesamiento) REFERENCES procesamiento (id_procesamiento),
    CONSTRAINT fk_detproc_tipo
        FOREIGN KEY (TIPO_MATERIAL_id_tipo_material) REFERENCES tipo_material (id_tipo_material)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- DATOS FICTICIOS
-- ---------------------------------------------------------------------

-- Usuarios: 4 recolectores, 2 operadores, 1 gerente, 1 administrador
INSERT INTO usuario (id_usuario, rol, nombre, apellido, email, activo) VALUES
(1, 'RECOLECTOR',    'Lucía',    'Fernández', 'lucia.fernandez@demo.com', 1),
(2, 'RECOLECTOR',    'Martín',   'Gómez',     'martin.gomez@demo.com', 1),
(3, 'RECOLECTOR',    'Sofía',    'Ramírez',   'sofia.ramirez@demo.com', 1),
(4, 'RECOLECTOR',    'Diego',    'Herrera',   'diego.herrera@demo.com', 1),
(5, 'OPERADOR',      'Carolina', 'Méndez',    'carolina.mendez@demo.com', 1),
(6, 'OPERADOR',      'Julián',   'Castro',    'julian.castro@demo.com', 1),
(7, 'GERENTE',       'Valeria',  'Suárez',    'valeria.suarez@demo.com', 1),
(8, 'ADMINISTRADOR', 'Andrés',   'Molina',    'andres.molina@demo.com', 1);

-- Puntos de recolección (el 5 está inactivo)
INSERT INTO punto_recoleccion (id_punto_recoleccion, nombre, direccion, activo) VALUES
(1, 'Punto Centro',           'Av. San Martín 1200',        1),
(2, 'Punto Belgrano',         'Calle Belgrano 450',         1),
(3, 'Plaza Central',          'Plaza Central - Calle 9',    1),
(4, 'Punto Barrio Oeste',     'Barrio Oeste - Calle 22',    1),
(5, 'Punto Parque Industrial','Parque Industrial - Lote 7', 0);

-- Tipos de material (los 3 primeros son PET; el resto activa "no PET")
INSERT INTO tipo_material (id_tipo_material, nombre, es_pet) VALUES
(1, 'PET cristal', 1),
(2, 'PET verde',   1),
(3, 'PET color',   1),
(4, 'PEAD',        0),
(5, 'PVC',         0),
(6, 'PP',          0);

-- Asignaciones (RN-02). La validación usa CURRENT_DATE, por eso las vigentes
-- no tienen fecha_hasta: la demo funciona sin importar el día en que se presente.
INSERT INTO asignacion (id_asignacion, fecha_desde, fecha_hasta, USUARIO_id_usuario, PUNTO_RECOLECCION_id_punto_recoleccion) VALUES
(1, '2026-08-01', NULL,         1, 1),  -- Lucía  -> Punto Centro (vigente)
(2, '2026-08-01', NULL,         1, 2),  -- Lucía  -> Punto Belgrano (vigente)
(3, '2026-08-01', NULL,         2, 3),  -- Martín -> Plaza Central (vigente)
(4, '2026-09-01', NULL,         3, 4),  -- Sofía  -> Barrio Oeste (vigente)
(5, '2026-08-01', '2026-09-30', 2, 5),  -- Martín -> Parque Industrial (vencida)
(6, '2026-08-01', '2026-08-31', 4, 2);  -- Diego  -> Belgrano (vencida): sin puntos vigentes

-- Recolecciones (todas respetan las asignaciones de arriba)
INSERT INTO recoleccion (id_recoleccion, fecha, USUARIO_id_usuario, PUNTO_RECOLECCION_id_punto_recoleccion) VALUES
(1,  '2026-09-02', 1, 1),
(2,  '2026-09-09', 1, 2),
(3,  '2026-09-16', 2, 3),
(4,  '2026-09-23', 3, 4),
(5,  '2026-09-30', 1, 1),
(6,  '2026-10-07', 2, 3),
(7,  '2026-10-14', 3, 4),
(8,  '2026-10-19', 1, 2),
(9,  '2026-10-21', 2, 3),
(10, '2026-10-23', 1, 1);

INSERT INTO detalle_recoleccion (RECOLECCION_id_recoleccion, TIPO_MATERIAL_id_tipo_material, cantidad) VALUES
(1, 1, 45.5), (1, 2, 20.0), (1, 4,  8.0),
(2, 1, 30.0), (2, 3, 12.5),
(3, 1, 60.0), (3, 2, 25.0), (3, 5,  4.5),
(4, 1, 38.25), (4, 3, 15.0), (4, 6,  6.0),
(5, 1, 52.0), (5, 2, 18.5),
(6, 1, 70.0), (6, 3, 22.0), (6, 4,  9.5),
(7, 1, 41.0), (7, 2, 28.0),
(8, 1, 35.5), (8, 2, 14.0),
(9, 1, 48.0), (9, 3, 19.5), (9, 6,  5.0),
(10, 1, 55.0), (10, 2, 21.0);

-- Procesamientos (solo PET)
INSERT INTO procesamiento (id_procesamiento, fecha, cantidad_rechazada, USUARIO_id_usuario) VALUES
(1, '2026-09-05',  8.00, 5),
(2, '2026-09-12',  5.25, 5),
(3, '2026-09-19', 12.00, 6),
(4, '2026-09-26',  9.50, 6),
(5, '2026-10-03', 15.00, 5),
(6, '2026-10-10',  7.75, 6),
(7, '2026-10-17', 11.25, 5),
(8, '2026-10-24', 10.00, 6);

-- Totales procesados resultantes:
-- 1: 120.50, 2: 95.00, 3: 150.75, 4: 110.00, 5: 180.25, 6: 130.00, 7: 160.50, 8: 140.00
INSERT INTO detalle_procesamiento (PROCESAMIENTO_id_procesamiento, TIPO_MATERIAL_id_tipo_material, cantidad) VALUES
(1, 1,  80.00), (1, 2, 40.50),
(2, 1,  60.00), (2, 3, 35.00),
(3, 1,  90.75), (3, 2, 60.00),
(4, 1,  70.00), (4, 2, 25.00), (4, 3, 15.00),
(5, 1, 100.25), (5, 2, 50.00), (5, 3, 30.00),
(6, 1,  85.00), (6, 2, 45.00),
(7, 1,  95.50), (7, 3, 65.00),
(8, 1,  90.00), (8, 2, 50.00);

-- ---------------------------------------------------------------------
-- Consulta de control (equivale al CU-02 para octubre 2026)
-- ---------------------------------------------------------------------
-- SELECT p.fecha,
--        GROUP_CONCAT(t.nombre ORDER BY t.nombre SEPARATOR ', ') AS tipos_pet,
--        SUM(d.cantidad) AS cantidad_procesada,
--        p.cantidad_rechazada,
--        CONCAT(u.nombre, ' ', u.apellido) AS operador
-- FROM procesamiento p
-- JOIN usuario u ON u.id_usuario = p.USUARIO_id_usuario
-- JOIN detalle_procesamiento d ON d.PROCESAMIENTO_id_procesamiento = p.id_procesamiento
-- JOIN tipo_material t ON t.id_tipo_material = d.TIPO_MATERIAL_id_tipo_material
-- WHERE p.fecha BETWEEN '2026-10-01' AND '2026-10-31'
-- GROUP BY p.id_procesamiento
-- ORDER BY p.fecha DESC;
