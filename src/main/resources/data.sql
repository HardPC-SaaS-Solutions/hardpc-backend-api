-- 1. Categorías
INSERT INTO categorias (nombre, descripcion, estado, icono_url, fecha_creacion, fecha_actualizacion) VALUES
('Procesadores', 'CPUs para PC y Servidores', true, 'cpu_icon.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tarjetas de Video', 'GPUs para Gaming y Diseño', true, 'gpu_icon.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Memorias RAM', 'Memorias DDR4 y DDR5', true, 'ram_icon.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Almacenamiento', 'Discos SSD y HDD', true, 'ssd_icon.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Marcas
INSERT INTO marcas (nombre, estado, logo_url, fecha_creacion, fecha_actualizacion) VALUES
('Intel', true, 'intel_logo.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AMD', true, 'amd_logo.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('NVIDIA', true, 'nvidia_logo.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Corsair', true, 'corsair_logo.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Western Digital', true, 'wd_logo.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Unidad Medida
INSERT INTO unidades_medida (descripcion, abreviatura, estado, fecha_creacion, fecha_actualizacion) VALUES
('Unidad de empaque', 'UND', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Caja de 10 unidades', 'CJ10', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kit (Combo)', 'KIT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. Tipo Documento
INSERT INTO tipos_documento (nombre, longitud_exacta, estado, fecha_creacion, fecha_actualizacion) VALUES
('DNI', 8, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('RUC', 11, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Carnet de Extranjería', 9, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. Tipo Comprobante
INSERT INTO tipos_comprobantes (descripcion, codigo_sunat, estado, fecha_creacion, fecha_actualizacion) VALUES
('Factura Electrónica', '01', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Boleta de Venta Electrónica', '03', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Nota de Crédito', '07', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. Forma Pago
INSERT INTO formas_pago (descripcion, estado, fecha_creacion, fecha_actualizacion) VALUES
('Transferencia Bancaria', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tarjeta de Crédito / Débito', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Efectivo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Billetera Digital (Yape/Plin)', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. Roles
INSERT INTO roles (nombre, descripcion, estado, fecha_creacion, fecha_actualizacion) VALUES
('ADMIN', 'Acceso total al sistema', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VENDEDOR', 'Acceso a módulo de ventas y clientes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ALMACENERO', 'Control de inventario y recepciones', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 8. Locales
INSERT INTO locales (nombre, direccion, telefono, estado, fecha_creacion, fecha_actualizacion) VALUES
('Sede Norte', 'Av. Principal 456', '999888777', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sede Centro', 'Centro Histórico de Trujillo', '044-223344', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sede Mall', 'Mall Plaza', '911222333', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 9. Productos
INSERT INTO productos (codigo_sku, descripcion, precio_usd, meses_garantia, es_serializado, id_marca, id_categoria, id_unidad_medida, estado, fecha_creacion, fecha_actualizacion) VALUES
('SKU-I7-12700', 'Intel Core i7-12700K', 450.00, 36, true, 1, 1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SKU-R5-5600X', 'AMD Ryzen 5 5600X', 200.00, 36, true, 2, 1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SKU-RTX-3060', 'NVIDIA GeForce RTX 3060 12GB', 350.00, 24, true, 3, 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SKU-RAM-16GB', 'Corsair Vengeance LPX 16GB DDR4', 60.00, 60, false, 4, 3, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SKU-SSD-1TB', 'WD Blue SN570 1TB NVMe', 85.00, 36, true, 5, 4, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 10. Proveedores
INSERT INTO proveedores (ruc, razon_social, nombre_comercial, direccion, telefono, email, estado, fecha_creacion, fecha_actualizacion) VALUES
('20600011122', 'Tech Solutions S.A.C', 'TechSol', 'Calle Los Jazmines 123', '014445566', 'ventas@techsol.pe', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('20555544433', 'Distribuidora Gamer E.I.R.L.', 'GamerDist', 'Av. Argentina 555', '017778899', 'contacto@gamerdist.pe', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('20111222333', 'Importaciones Mayoristas S.A.', 'ImpoMayor', 'Jr. Paruro 999', '015552211', 'logistica@impomayor.pe', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 11. Usuarios (Personas + Usuarios)
-- Admin
INSERT INTO personas (id_persona, numero_documento, id_tipo_documento, nombres, apellidos, telefono, email, direccion, estado, fecha_creacion, fecha_actualizacion)
VALUES (1, '70605040', 1, 'Juan', 'Pérez', '900100200', 'juan@hardpc.com', 'Jr. Lima 789', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO usuarios (id_usuario, username, password, id_rol)
VALUES (1, 'admin_user', '$2a$10$8.UnVuG9shg.Y/...', 1);

-- Vendedor
INSERT INTO personas (id_persona, numero_documento, id_tipo_documento, nombres, apellidos, telefono, email, direccion, estado, fecha_creacion, fecha_actualizacion)
VALUES (3, '71223344', 1, 'Carlos', 'Mendoza', '933445566', 'carlos@hardpc.com', 'Urb. California', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO usuarios (id_usuario, username, password, id_rol)
VALUES (3, 'vendedor_01', '$2a$10$8.UnVuG9shg.Y/...', 2);

-- Almacenero
INSERT INTO personas (id_persona, numero_documento, id_tipo_documento, nombres, apellidos, telefono, email, direccion, estado, fecha_creacion, fecha_actualizacion)
VALUES (4, '75667788', 1, 'Miguel', 'Salinas', '988776655', 'miguel@hardpc.com', 'Av. España 101', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO usuarios (id_usuario, username, password, id_rol)
VALUES (4, 'almacen_01', '$2a$10$8.UnVuG9shg.Y/...', 3);

-- 12. Clientes (Personas + Clientes)
-- Cliente Mayorista
INSERT INTO personas (id_persona, numero_documento, id_tipo_documento, nombres, razon_social, telefono, email, direccion, estado, fecha_creacion, fecha_actualizacion)
VALUES (2, '10706050401', 2, 'Ana', 'Ana SAC', '900300400', 'ana@empresa.com', 'Av. Larco 100', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO clientes (id_cliente, tipo_cliente)
VALUES (2, 'MAYORISTA');

-- Cliente Minorista
INSERT INTO personas (id_persona, numero_documento, id_tipo_documento, nombres, apellidos, telefono, email, direccion, estado, fecha_creacion, fecha_actualizacion)
VALUES (5, '44556677', 1, 'Luis', 'Torres', '922113344', 'luis.t@gmail.com', 'Urb. El Golf', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO clientes (id_cliente, tipo_cliente)
VALUES (5, 'MINORISTA');

-- 13. Stock Local
INSERT INTO stock_local (id_producto, id_local, cantidad_actual, stock_minimo, fecha_creacion, fecha_actualizacion) VALUES
(1, 1, 15, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- i7 Sede Norte
(2, 2, 30, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Ryzen 5 Sede Centro
(3, 2, 10, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- RTX 3060 Sede Centro
(4, 1, 100, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- RAM Sede Norte (No serializado)
(4, 2, 50, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);  -- RAM Sede Centro (No serializado)

-- 14. Ingreso Compra (Cabecera)
INSERT INTO ingresos_compras (id_ingreso, id_proveedor, id_tipo_comprobante, id_usuario, id_local, serie_comprobante, numero_comprobante, fecha_ingreso, impuesto, total_compra, estado_ingreso, comprobante_doc_url, fecha_creacion, fecha_actualizacion) VALUES
(1, 1, 1, 1, 1, 'F001', '0000456', CURRENT_TIMESTAMP, 18.00, 5000.00, 'COMPLETADO', 'doc_url_1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 1, 4, 2, 'F002', '0000889', CURRENT_TIMESTAMP, 18.00, 3500.00, 'COMPLETADO', 'doc_url_2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 15. Detalle Ingreso
INSERT INTO detalles_ingresos (id_detalle_ingreso, id_ingreso, id_producto, cantidad, precio_compra_unitario, fecha_creacion, fecha_actualizacion) VALUES
(1, 1, 1, 10, 400.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 4, 50, 45.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- RAM
(3, 2, 2, 20, 175.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- Ryzen 5

-- 16. Item Serial
INSERT INTO items_seriales (numero_serie, condicion, estado_disponibilidad, fecha_fin_garantia, id_producto, id_local, id_detalle_ingreso, fecha_creacion, fecha_actualizacion) VALUES
('SN-INTEL-123', 'NUEVO', 'DISPONIBLE', '2027-04-22 00:00:00', 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SN-INTEL-124', 'NUEVO', 'VENDIDO', '2027-04-22 00:00:00', 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SN-AMD-999', 'NUEVO', 'DISPONIBLE', '2027-04-22 00:00:00', 2, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SN-AMD-888', 'NUEVO', 'DISPONIBLE', '2027-04-22 00:00:00', 2, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 17. Movimiento Inventario (Kardex)
INSERT INTO movimientos_inventario (tipo_movimiento, fecha_hora, cantidad, id_producto, id_item_serial, id_local_destino, id_usuario, observacion, fecha_creacion, fecha_actualizacion) VALUES
('ENTRADA', CURRENT_TIMESTAMP, 10, 1, NULL, 1, 1, 'Ingreso compra i7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ENTRADA', CURRENT_TIMESTAMP, 50, 4, NULL, 1, 1, 'Ingreso compra RAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SALIDA', CURRENT_TIMESTAMP, 1, 1, 2, NULL, 3, 'Venta i7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

