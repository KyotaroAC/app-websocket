-- =====================================================================
-- CHASQUIEXPRESS - SCRIPT DE MIGRACIÓN Y SEMILLA V2
-- Base de Datos: MySQL 8.x (AWS RDS)
-- =====================================================================

USE db_logistica;

-- ---------------------------------------------------------------------
-- 1. ACTUALIZACIÓN DE ROLES (RBAC LOGÍSTICO COMPLETO)
-- ---------------------------------------------------------------------
INSERT INTO roles (nombre) VALUES 
('ROLE_ADMIN'),
('ROLE_JEFE_AGENCIA'),
('ROLE_OPERARIO_VENTANILLA'),
('ROLE_ALMACENERO'),
('ROLE_CONDUCTOR'),
('ROLE_REPARTIDOR'),
('ROLE_AUDITOR')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- ---------------------------------------------------------------------
-- 2. TABLA USUARIOS: DNI Único, Fecha de Nacimiento y Soft Delete
-- ---------------------------------------------------------------------
-- Asegurar restricción única en DNI si no existe
SET @exist_uq_dni = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuarios' AND CONSTRAINT_NAME = 'uq_usuarios_dni');
SET @sql_uq_dni = IF(@exist_uq_dni = 0, 'ALTER TABLE usuarios ADD CONSTRAINT uq_usuarios_dni UNIQUE (dni);', 'SELECT 1;');
PREPARE stmt_uq_dni FROM @sql_uq_dni;
EXECUTE stmt_uq_dni;
DEALLOCATE PREPARE stmt_uq_dni;

-- Agregar fecha de nacimiento (para cálculo de edad)
SET @col_fn = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuarios' AND COLUMN_NAME = 'fecha_nacimiento');
SET @sql_fn = IF(@col_fn = 0, 'ALTER TABLE usuarios ADD COLUMN fecha_nacimiento DATE NULL AFTER apellidos;', 'SELECT 1;');
PREPARE stmt_fn FROM @sql_fn;
EXECUTE stmt_fn;
DEALLOCATE PREPARE stmt_fn;

-- Agregar campo 'activo' para Eliminación Lógica (Soft Delete)
SET @col_act = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuarios' AND COLUMN_NAME = 'activo');
SET @sql_act = IF(@col_act = 0, 'ALTER TABLE usuarios ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE AFTER estado_empleado;', 'SELECT 1;');
PREPARE stmt_act FROM @sql_act;
EXECUTE stmt_act;
DEALLOCATE PREPARE stmt_act;

-- ---------------------------------------------------------------------
-- 3. TABLA AGENCIAS: Soft Delete y Capacidad Volumétrica de Almacén
-- ---------------------------------------------------------------------
SET @col_ag_act = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agencias' AND COLUMN_NAME = 'activo');
SET @sql_ag_act = IF(@col_ag_act = 0, 'ALTER TABLE agencias ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE AFTER estado;', 'SELECT 1;');
PREPARE stmt_ag_act FROM @sql_ag_act;
EXECUTE stmt_ag_act;
DEALLOCATE PREPARE stmt_ag_act;

SET @col_ag_cap = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'agencias' AND COLUMN_NAME = 'capacidad_m3');
SET @sql_ag_cap = IF(@col_ag_cap = 0, 'ALTER TABLE agencias ADD COLUMN capacidad_m3 DECIMAL(10,2) NOT NULL DEFAULT 80.00 AFTER estado;', 'SELECT 1;');
PREPARE stmt_ag_cap FROM @sql_ag_cap;
EXECUTE stmt_ag_cap;
DEALLOCATE PREPARE stmt_ag_cap;

-- ---------------------------------------------------------------------
-- 4. TABLA VEHICULOS: Ciclo de Vida, Tipo, Kilometraje y Alertas MTC
-- ---------------------------------------------------------------------
SET @col_v_tipo = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'vehiculos' AND COLUMN_NAME = 'tipo_vehiculo');
SET @sql_v_tipo = IF(@col_v_tipo = 0, "ALTER TABLE vehiculos ADD COLUMN tipo_vehiculo VARCHAR(50) NOT NULL DEFAULT 'CAMION_FURGON' AFTER modelo;", 'SELECT 1;');
PREPARE stmt_v_tipo FROM @sql_v_tipo;
EXECUTE stmt_v_tipo;
DEALLOCATE PREPARE stmt_v_tipo;

SET @col_v_km = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'vehiculos' AND COLUMN_NAME = 'kilometraje');
SET @sql_v_km = IF(@col_v_km = 0, 'ALTER TABLE vehiculos ADD COLUMN kilometraje INT NOT NULL DEFAULT 0 AFTER capacidad_kg;', 'SELECT 1;');
PREPARE stmt_v_km FROM @sql_v_km;
EXECUTE stmt_v_km;
DEALLOCATE PREPARE stmt_v_km;

SET @col_v_soat = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'vehiculos' AND COLUMN_NAME = 'vencimiento_soat');
SET @sql_v_soat = IF(@col_v_soat = 0, 'ALTER TABLE vehiculos ADD COLUMN vencimiento_soat DATE NULL AFTER kilometraje;', 'SELECT 1;');
PREPARE stmt_v_soat FROM @sql_v_soat;
EXECUTE stmt_v_soat;
DEALLOCATE PREPARE stmt_v_soat;

SET @col_v_rev = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'vehiculos' AND COLUMN_NAME = 'vencimiento_revision');
SET @sql_v_rev = IF(@col_v_rev = 0, 'ALTER TABLE vehiculos ADD COLUMN vencimiento_revision DATE NULL AFTER vencimiento_soat;', 'SELECT 1;');
PREPARE stmt_v_rev FROM @sql_v_rev;
EXECUTE stmt_v_rev;
DEALLOCATE PREPARE stmt_v_rev;

SET @col_v_act = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'vehiculos' AND COLUMN_NAME = 'activo');
SET @sql_v_act = IF(@col_v_act = 0, 'ALTER TABLE vehiculos ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE AFTER estado;', 'SELECT 1;');
PREPARE stmt_v_act FROM @sql_v_act;
EXECUTE stmt_v_act;
DEALLOCATE PREPARE stmt_v_act;

-- ---------------------------------------------------------------------
-- 5. TABLA ENVIOS: Boleta/Factura, PIN de 4 Dígitos, Operación de Pago y Bahía
-- ---------------------------------------------------------------------
-- Tipo de comprobante (Boleta o Factura)
SET @col_e_comp = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'tipo_comprobante');
SET @sql_e_comp = IF(@col_e_comp = 0, "ALTER TABLE envios ADD COLUMN tipo_comprobante ENUM('BOLETA', 'FACTURA') NOT NULL DEFAULT 'BOLETA' AFTER codigo_tracking;", 'SELECT 1;');
PREPARE stmt_e_comp FROM @sql_e_comp;
EXECUTE stmt_e_comp;
DEALLOCATE PREPARE stmt_e_comp;

-- Serie y Correlativo
SET @col_e_serie = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'serie_comprobante');
SET @sql_e_serie = IF(@col_e_serie = 0, "ALTER TABLE envios ADD COLUMN serie_comprobante VARCHAR(10) NOT NULL DEFAULT 'B001' AFTER tipo_comprobante;", 'SELECT 1;');
PREPARE stmt_e_serie FROM @sql_e_serie;
EXECUTE stmt_e_serie;
DEALLOCATE PREPARE stmt_e_serie;

SET @col_e_num = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'numero_comprobante');
SET @sql_e_num = IF(@col_e_num = 0, 'ALTER TABLE envios ADD COLUMN numero_comprobante INT NOT NULL DEFAULT 1001 AFTER serie_comprobante;', 'SELECT 1;');
PREPARE stmt_e_num FROM @sql_e_num;
EXECUTE stmt_e_num;
DEALLOCATE PREPARE stmt_e_num;

-- Desglose tributario: Subtotal e IGV (18%)
SET @col_e_sub = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'subtotal');
SET @sql_e_sub = IF(@col_e_sub = 0, 'ALTER TABLE envios ADD COLUMN subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER costo_total;', 'SELECT 1;');
PREPARE stmt_e_sub FROM @sql_e_sub;
EXECUTE stmt_e_sub;
DEALLOCATE PREPARE stmt_e_sub;

SET @col_e_igv = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'igv');
SET @sql_e_igv = IF(@col_e_igv = 0, 'ALTER TABLE envios ADD COLUMN igv DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER subtotal;', 'SELECT 1;');
PREPARE stmt_e_igv FROM @sql_e_igv;
EXECUTE stmt_e_igv;
DEALLOCATE PREPARE stmt_e_igv;

-- CLAVE DE SEGURIDAD DE 4 DÍGITOS PARA RETIRO (PIN)
SET @col_e_pin = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'clave_entrega');
SET @sql_e_pin = IF(@col_e_pin = 0, "ALTER TABLE envios ADD COLUMN clave_entrega VARCHAR(4) NOT NULL DEFAULT '1234' AFTER igv;", 'SELECT 1;');
PREPARE stmt_e_pin FROM @sql_e_pin;
EXECUTE stmt_e_pin;
DEALLOCATE PREPARE stmt_e_pin;

-- Número de Operación de Pago (Yape/Plin o Tarjeta)
SET @col_e_op = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'numero_operacion_pago');
SET @sql_e_op = IF(@col_e_op = 0, 'ALTER TABLE envios ADD COLUMN numero_operacion_pago VARCHAR(50) NULL AFTER estado_pago;', 'SELECT 1;');
PREPARE stmt_e_op FROM @sql_e_op;
EXECUTE stmt_e_op;
DEALLOCATE PREPARE stmt_e_op;

-- Ubicación en Almacén (Bahía / Estante físico)
SET @col_e_ub = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'ubicacion_almacen');
SET @sql_e_ub = IF(@col_e_ub = 0, "ALTER TABLE envios ADD COLUMN ubicacion_almacen VARCHAR(50) NOT NULL DEFAULT 'BAHIA_RECEPCION' AFTER estado_actual;", 'SELECT 1;');
PREPARE stmt_e_ub FROM @sql_e_ub;
EXECUTE stmt_e_ub;
DEALLOCATE PREPARE stmt_e_ub;

-- Datos de quien recoge físicamente
SET @col_e_rec_dni = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'dni_receptor');
SET @sql_e_rec_dni = IF(@col_e_rec_dni = 0, 'ALTER TABLE envios ADD COLUMN dni_receptor VARCHAR(11) NULL AFTER ubicacion_almacen;', 'SELECT 1;');
PREPARE stmt_e_rec_dni FROM @sql_e_rec_dni;
EXECUTE stmt_e_rec_dni;
DEALLOCATE PREPARE stmt_e_rec_dni;

SET @col_e_rec_nom = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'nombre_receptor');
SET @sql_e_rec_nom = IF(@col_e_rec_nom = 0, 'ALTER TABLE envios ADD COLUMN nombre_receptor VARCHAR(150) NULL AFTER dni_receptor;', 'SELECT 1;');
PREPARE stmt_e_rec_nom FROM @sql_e_rec_nom;
EXECUTE stmt_e_rec_nom;
DEALLOCATE PREPARE stmt_e_rec_nom;

SET @col_e_rec_fec = (SELECT COUNT(*) FROM information_schema.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'envios' AND COLUMN_NAME = 'fecha_entrega');
SET @sql_e_rec_fec = IF(@col_e_rec_fec = 0, 'ALTER TABLE envios ADD COLUMN fecha_entrega DATETIME NULL AFTER nombre_receptor;', 'SELECT 1;');
PREPARE stmt_e_rec_fec FROM @sql_e_rec_fec;
EXECUTE stmt_e_rec_fec;
DEALLOCATE PREPARE stmt_e_rec_fec;

-- ---------------------------------------------------------------------
-- 6. ACTUALIZAR SUB-TOTAL E IGV EN REGISTROS EXISTENTES
-- ---------------------------------------------------------------------
UPDATE envios 
SET subtotal = ROUND(costo_total / 1.18, 2),
    igv = ROUND(costo_total - (costo_total / 1.18), 2)
WHERE subtotal = 0.00 AND costo_total > 0;

-- ---------------------------------------------------------------------
-- 7. VEHÍCULOS DE DEMOSTRACIÓN CON CICLO DE VIDA COMPLETO
-- ---------------------------------------------------------------------
INSERT INTO vehiculos (placa, marca, modelo, tipo_vehiculo, capacidad_kg, kilometraje, vencimiento_soat, vencimiento_revision, estado, activo) VALUES
('ABC-123', 'Volvo', 'FH 460 Globetrotter', 'TRACTOCAMION_SEMITRAILER', 15000.00, 84200, '2027-04-15', '2027-05-20', 'DISPONIBLE', TRUE),
('XYZ-789', 'Mercedes-Benz', 'Atego 1726', 'CAMION_FURGON', 8000.00, 112450, '2027-02-10', '2027-03-12', 'DISPONIBLE', TRUE),
('HUA-552', 'Hino', '500 Series Wide', 'CAMION_FURGON', 6500.00, 45200, '2027-08-30', '2027-09-15', 'EN_MANTENIMIENTO', TRUE),
('FUR-301', 'Hyundai', 'H350 Carga', 'FURGONETA_URBANA', 2200.00, 31400, '2027-01-20', '2027-02-14', 'DISPONIBLE', TRUE),
('MOT-889', 'Honda', 'GL 150 Cargo', 'MOTO_COURIER', 60.00, 12800, '2026-12-05', '2026-12-20', 'DISPONIBLE', TRUE)
ON DUPLICATE KEY UPDATE 
  marca = VALUES(marca),
  modelo = VALUES(modelo),
  tipo_vehiculo = VALUES(tipo_vehiculo),
  capacidad_kg = VALUES(capacidad_kg),
  estado = VALUES(estado);

-- ---------------------------------------------------------------------
-- 8. NÓMINA DE USUARIOS DEMOSTRATIVOS (BCrypt para contraseña '123456')
-- Hash verificado: $2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm
-- ---------------------------------------------------------------------
-- Obtener IDs de roles para inserción segura
SET @id_admin = (SELECT id FROM roles WHERE nombre = 'ROLE_ADMIN' LIMIT 1);
SET @id_jefe  = (SELECT id FROM roles WHERE nombre = 'ROLE_JEFE_AGENCIA' LIMIT 1);
SET @id_vent  = (SELECT id FROM roles WHERE nombre = 'ROLE_OPERARIO_VENTANILLA' LIMIT 1);
SET @id_almac = (SELECT id FROM roles WHERE nombre = 'ROLE_ALMACENERO' LIMIT 1);
SET @id_cond  = (SELECT id FROM roles WHERE nombre = 'ROLE_CONDUCTOR' LIMIT 1);
SET @id_rep   = (SELECT id FROM roles WHERE nombre = 'ROLE_REPARTIDOR' LIMIT 1);

-- Admin Principal (DNI 12345678)
INSERT INTO usuarios (dni, nombres, apellidos, fecha_nacimiento, email, telefono, direccion, sueldo_base, fecha_ingreso, estado_empleado, activo, password_hash, id_rol, id_agencia)
VALUES 
('12345678', 'Carlos Alberto', 'Mendoza Quispe', '1988-06-14', 'gerencia@chasquiexpress.online', '987111222', 'Av. Javier Prado Este 2450, San Borja, Lima', 4500.00, '2023-01-15', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_admin, 1)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  nombres = VALUES(nombres),
  fecha_nacimiento = VALUES(fecha_nacimiento),
  activo = TRUE;

-- Sede Lima Centro (Agencia 1)
INSERT INTO usuarios (dni, nombres, apellidos, fecha_nacimiento, email, telefono, direccion, sueldo_base, fecha_ingreso, estado_empleado, activo, password_hash, id_rol, id_agencia)
VALUES 
('10000001', 'Rosa Elena', 'Valdivia Torres', '1990-03-22', 'jefatura.lima@chasquiexpress.online', '987222333', 'Jr. Lampa 840, Lima Cercado', 3200.00, '2023-03-01', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_jefe, 1),
('10000002', 'Luis Miguel', 'Huamán Castro', '1996-09-10', 'ventanilla.lima@chasquiexpress.online', '987333444', 'Av. Abancay 412, Lima', 1800.00, '2023-06-15', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_vent, 1),
('10000003', 'Jorge Eduardo', 'Alarcón Soto', '1992-11-05', 'almacen.lima@chasquiexpress.online', '987444555', 'Av. Argentina 1220, Callao', 1950.00, '2023-04-10', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_almac, 1),
('10000004', 'Manuel Jesús', 'Zavaleta Rivas', '1985-04-18', 'transporte@chasquiexpress.online', '987555666', 'Panamericana Norte Km 22, SMP, Lima', 2600.00, '2023-02-20', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_cond, 1)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  fecha_nacimiento = VALUES(fecha_nacimiento),
  activo = TRUE;

-- Sede Huacho Terminal (Agencia 2)
INSERT INTO usuarios (dni, nombres, apellidos, fecha_nacimiento, email, telefono, direccion, sueldo_base, fecha_ingreso, estado_empleado, activo, password_hash, id_rol, id_agencia)
VALUES 
('20000001', 'Patricia Lucía', 'Campos Vega', '1991-08-30', 'jefatura.huacho@chasquiexpress.online', '987666777', 'Av. 28 de Julio 340, Huacho', 3000.00, '2023-05-01', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_jefe, 2),
('20000002', 'Kevin Alexander', 'Palomino Silva', '1999-12-02', 'ventanilla.huacho@chasquiexpress.online', '987777888', 'Calle Colón 115, Huacho', 1750.00, '2023-08-10', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_vent, 2),
('20000003', 'Daniel Alberto', 'Ortiz Bravo', '1994-07-25', 'almacen.huacho@chasquiexpress.online', '987888999', 'Av. San Martín 780, Huaura', 1900.00, '2023-07-15', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_almac, 2)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  fecha_nacimiento = VALUES(fecha_nacimiento),
  activo = TRUE;

-- Repartidor Courier de Última Milla (DNI 80001111)
INSERT INTO usuarios (dni, nombres, apellidos, fecha_nacimiento, email, telefono, direccion, sueldo_base, fecha_ingreso, estado_empleado, activo, password_hash, id_rol, id_agencia)
VALUES 
('80001111', 'Javier Ignacio', 'Benítez Cruz', '1997-05-14', 'courier.huacho@chasquiexpress.online', '987999000', 'Jr. Salaverry 230, Huacho', 1850.00, '2023-09-01', 'ACTIVO', TRUE, '$2a$10$sAP4H2gtxM2P4PIYYc1Xo.Hz77BeOFXuwDAteszcVVgpWoQYFlAMm', @id_rep, 2)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  fecha_nacimiento = VALUES(fecha_nacimiento),
  activo = TRUE;

SELECT 'MIGRACION Y DATOS SEMILLA COMPLETADOS EXITOSAMENTE' AS resultado;
