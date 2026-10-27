-- Roles con privilegios según matriz
INSERT INTO roles (nombre) VALUES ('TECNICO');
INSERT INTO roles (nombre) VALUES ('JEFE_CAMPO');
INSERT INTO roles (nombre) VALUES ('ADMINISTRADOR');

-- Privilegios por rol
-- TECNICO
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LOTE_LEER' FROM roles r WHERE r.nombre = 'TECNICO';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LABOR_LEER' FROM roles r WHERE r.nombre = 'TECNICO';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LABOR_CREAR' FROM roles r WHERE r.nombre = 'TECNICO';

-- JEFE_CAMPO
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LOTE_LEER' FROM roles r WHERE r.nombre = 'JEFE_CAMPO';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LABOR_LEER' FROM roles r WHERE r.nombre = 'JEFE_CAMPO';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LABOR_CREAR' FROM roles r WHERE r.nombre = 'JEFE_CAMPO';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LABOR_EDITAR' FROM roles r WHERE r.nombre = 'JEFE_CAMPO';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LABOR_ELIMINAR' FROM roles r WHERE r.nombre = 'JEFE_CAMPO';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'CAMPANIA_CERRAR' FROM roles r WHERE r.nombre = 'JEFE_CAMPO';

-- ADMINISTRADOR
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LOTE_LEER' FROM roles r WHERE r.nombre = 'ADMINISTRADOR';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'LABOR_LEER' FROM roles r WHERE r.nombre = 'ADMINISTRADOR';
INSERT INTO rol_privilegios (rol_id, privilegio) SELECT r.id, 'USUARIO_GESTIONAR' FROM roles r WHERE r.nombre = 'ADMINISTRADOR';

-- Usuarios de prueba (contraseña BCrypt de Agro2026*)
INSERT INTO usuarios (username, password, nombre_completo, enabled) VALUES ('tecnico1', '$2a$10$tSwRxWEpG43KxQQlqrUv7OXxN5yHTNLtrikrKwYpx5T9V0LMsojY6', 'Técnico de Campo 1', true);
INSERT INTO usuarios (username, password, nombre_completo, enabled) VALUES ('jefe1', '$2a$10$tSwRxWEpG43KxQQlqrUv7OXxN5yHTNLtrikrKwYpx5T9V0LMsojY6', 'Jefe de Campo', true);
INSERT INTO usuarios (username, password, nombre_completo, enabled) VALUES ('admin', '$2a$10$tSwRxWEpG43KxQQlqrUv7OXxN5yHTNLtrikrKwYpx5T9V0LMsojY6', 'Administrador', true);

INSERT INTO usuario_roles (usuario_id, rol_id) SELECT u.id, r.id FROM usuarios u, roles r WHERE u.username = 'tecnico1' AND r.nombre = 'TECNICO';
INSERT INTO usuario_roles (usuario_id, rol_id) SELECT u.id, r.id FROM usuarios u, roles r WHERE u.username = 'jefe1' AND r.nombre = 'JEFE_CAMPO';
INSERT INTO usuario_roles (usuario_id, rol_id) SELECT u.id, r.id FROM usuarios u, roles r WHERE u.username = 'admin' AND r.nombre = 'ADMINISTRADOR';

-- Lotes
INSERT INTO lotes (codigo, nombre, hectareas, cultivo, valle, campania_cerrada, dosis_maxima_por_hectarea) VALUES ('LOT-001', 'Lote Norte', 5.5, 'Maíz', 'Valle Norte', false, 2.0);
INSERT INTO lotes (codigo, nombre, hectareas, cultivo, valle, campania_cerrada, dosis_maxima_por_hectarea) VALUES ('LOT-002', 'Lote Sur', 3.0, 'Papa', 'Valle Sur', false, 1.5);
INSERT INTO lotes (codigo, nombre, hectareas, cultivo, valle, campania_cerrada, dosis_maxima_por_hectarea) VALUES ('LOT-003', 'Lote Central', 4.2, 'Trigo', 'Valle Central', false, 1.8);
INSERT INTO lotes (codigo, nombre, hectareas, cultivo, valle, campania_cerrada, dosis_maxima_por_hectarea) VALUES ('LOT-004', 'Lote Este', 6.0, 'Maíz', 'Valle Norte', true, 2.0);
INSERT INTO lotes (codigo, nombre, hectareas, cultivo, valle, campania_cerrada, dosis_maxima_por_hectarea) VALUES ('LOT-005', 'Lote Oeste', 2.5, 'Quinua', 'Valle Sur', false, 1.0);
INSERT INTO lotes (codigo, nombre, hectareas, cultivo, valle, campania_cerrada, dosis_maxima_por_hectarea) VALUES ('LOT-006', 'Lote Alto', 7.0, 'Papa', 'Valle Central', true, 1.5);
INSERT INTO lotes (codigo, nombre, hectareas, cultivo, valle, campania_cerrada, dosis_maxima_por_hectarea) VALUES ('LOT-007', 'Lote Bajo', 3.8, 'Trigo', 'Valle Norte', false, 1.8);
INSERT INTO lotes (codigo, nombre, hectareas, cultivo, valle, campania_cerrada, dosis_maxima_por_hectarea) VALUES ('LOT-008', 'Lote Medio', 4.0, 'Maíz', 'Valle Sur', false, 2.0);

-- Labores (mínimas requeridas)
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'RIEGO', '2026-10-20', 'tecnico1', 10.0, 'Riego matutino' FROM lotes l WHERE l.codigo = 'LOT-001';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'FUMIGACION', '2026-10-21', 'tecnico1', 5.0, 'Control de plagas' FROM lotes l WHERE l.codigo = 'LOT-001';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'COSECHA', '2026-10-22', 'tecnico1', 100.0, 'Cosecha parcial' FROM lotes l WHERE l.codigo = 'LOT-001';

INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'RIEGO', '2026-10-19', 'tecnico1', 5.0, 'Riego' FROM lotes l WHERE l.codigo = 'LOT-002';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'COSECHA', '2026-10-23', 'tecnico1', 50.0, 'Cosecha' FROM lotes l WHERE l.codigo = 'LOT-002';

INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'RIEGO', '2026-10-18', 'tecnico1', 8.0, 'Riego' FROM lotes l WHERE l.codigo = 'LOT-003';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'FUMIGACION', '2026-10-19', 'tecnico1', 3.0, 'Tratamiento' FROM lotes l WHERE l.codigo = 'LOT-003';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'COSECHA', '2026-10-24', 'tecnico1', 80.0, 'Cosecha' FROM lotes l WHERE l.codigo = 'LOT-003';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'RIEGO', '2026-10-17', 'tecnico1', 7.0, 'Riego adicional' FROM lotes l WHERE l.codigo = 'LOT-004';

INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'RIEGO', '2026-10-16', 'tecnico1', 4.0, 'Riego' FROM lotes l WHERE l.codigo = 'LOT-005';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'COSECHA', '2026-10-25', 'tecnico1', 30.0, 'Cosecha' FROM lotes l WHERE l.codigo = 'LOT-005';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'FUMIGACION', '2026-10-15', 'tecnico1', 9.0, 'Fumigación' FROM lotes l WHERE l.codigo = 'LOT-006';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'COSECHA', '2026-10-26', 'tecnico1', 90.0, 'Cosecha' FROM lotes l WHERE l.codigo = 'LOT-006';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'RIEGO', '2026-10-14', 'tecnico1', 6.0, 'Riego' FROM lotes l WHERE l.codigo = 'LOT-007';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'FUMIGACION', '2026-10-15', 'tecnico1', 4.0, 'Fumigación' FROM lotes l WHERE l.codigo = 'LOT-007';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'COSECHA', '2026-10-27', 'tecnico1', 70.0, 'Cosecha' FROM lotes l WHERE l.codigo = 'LOT-007';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'RIEGO', '2026-10-13', 'tecnico1', 5.0, 'Riego' FROM lotes l WHERE l.codigo = 'LOT-008';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'COSECHA', '2026-10-28', 'tecnico1', 60.0, 'Cosecha' FROM lotes l WHERE l.codigo = 'LOT-008';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'RIEGO', '2026-10-12', 'tecnico1', 3.0, 'Riego' FROM lotes l WHERE l.codigo = 'LOT-003';
INSERT INTO labores (lote_id, tipo, fecha, responsable, cantidad, observacion) SELECT l.id, 'FUMIGACION', '2026-10-11', 'tecnico1', 2.0, 'Fumigación menor' FROM lotes l WHERE l.codigo = 'LOT-002';
