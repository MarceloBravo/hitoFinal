-- ============================================================================
-- Inserciones de datos para el catálogo de productos (e-commerce)
-- Base de datos: PostgreSQL 16  |  Esquema generado por Hibernate (ddl-auto: update)
--
-- Tablas involucradas:
--   marks            (id, name, active)
--   categories       (id, name, active)
--   products         (id, mark_id, name, description, stock, weight,
--                     price_cost, price_sale, image_path)
--   product_categories (product_id, category_id)   <-- tabla de unión many-to-many
--
-- Las claves primarias son IDENTITY (bigserial). Para no depender de ids fijos,
-- se resuelven las FKs mediante subconsultas por nombre. El script es
-- idempotente: no duplica marcas, categorías ni relaciones ya existentes.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1) MARCAS
-- ----------------------------------------------------------------------------
INSERT INTO marks (name, active)
SELECT 'Apple',   TRUE WHERE NOT EXISTS (SELECT 1 FROM marks WHERE name = 'Apple')
UNION ALL
SELECT 'Samsung', TRUE WHERE NOT EXISTS (SELECT 1 FROM marks WHERE name = 'Samsung')
UNION ALL
SELECT 'Lenovo',  TRUE WHERE NOT EXISTS (SELECT 1 FROM marks WHERE name = 'Lenovo')
UNION ALL
SELECT 'Sony',    TRUE WHERE NOT EXISTS (SELECT 1 FROM marks WHERE name = 'Sony')
UNION ALL
SELECT 'Xiaomi',  TRUE WHERE NOT EXISTS (SELECT 1 FROM marks WHERE name = 'Xiaomi');

-- ----------------------------------------------------------------------------
-- 2) CATEGORÍAS
-- ----------------------------------------------------------------------------
INSERT INTO categories (name, active)
SELECT 'Electrónica', TRUE   WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Electrónica')
UNION ALL
SELECT 'Computación', TRUE   WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Computación')
UNION ALL
SELECT 'Audio',       TRUE   WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Audio')
UNION ALL
SELECT 'Celulares',   TRUE   WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Celulares')
UNION ALL
SELECT 'Gaming',      TRUE   WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Gaming')
UNION ALL
SELECT 'Oficina',     TRUE   WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Oficina');

-- ----------------------------------------------------------------------------
-- 3) PRODUCTOS
--    image_path usa la ruta pública servida por el backend (/uploads/...)
-- ----------------------------------------------------------------------------
INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'iPhone 15 Pro', 'Smartphone premium con chip A17 Pro y cámara de 48 MP.',
       25, 0.187, 800.0, 1099.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Apple'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'iPhone 15 Pro');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Galaxy S24', 'Smartphone Android con pantalla AMOLED 120 Hz y Galaxy AI.',
       40, 0.167, 650.0, 899.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Samsung'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Galaxy S24');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Notebook IdeaPad 5', 'Notebook ultraliviana para oficina y estudio, 16 GB RAM.',
       30, 1.65, 550.0, 749.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Lenovo'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Notebook IdeaPad 5');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Audífonos WH-1000XM5', 'Audífonos inalámbricos con cancelación activa de ruido.',
       60, 0.25, 280.0, 399.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Sony'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Audífonos WH-1000XM5');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Redmi Note 13 Pro', 'Smartphone gama media con carga rápida de 67 W.',
       80, 0.189, 200.0, 329.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Xiaomi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Redmi Note 13 Pro');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Monitor 24" Full HD', 'Monitor IPS 100 Hz con conexión HDMI y DP.',
       50, 3.20, 120.0, 189.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Samsung'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Monitor 24" Full HD');

-- ----------------------------------------------------------------------------
-- 4) RELACIONES products <-> categories (tabla de unión)
-- ----------------------------------------------------------------------------
INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p
JOIN marks m   ON m.id = p.mark_id
JOIN categories c ON c.name IN ('Celulares', 'Electrónica')
WHERE p.name = 'iPhone 15 Pro'
  AND NOT EXISTS (SELECT 1 FROM product_categories pc
                  WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p
JOIN marks m   ON m.id = p.mark_id
JOIN categories c ON c.name IN ('Celulares', 'Electrónica')
WHERE p.name = 'Galaxy S24'
  AND NOT EXISTS (SELECT 1 FROM product_categories pc
                  WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p
JOIN marks m   ON m.id = p.mark_id
JOIN categories c ON c.name IN ('Computación', 'Oficina')
WHERE p.name = 'Notebook IdeaPad 5'
  AND NOT EXISTS (SELECT 1 FROM product_categories pc
                  WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p
JOIN marks m   ON m.id = p.mark_id
JOIN categories c ON c.name IN ('Audio', 'Electrónica')
WHERE p.name = 'Audífonos WH-1000XM5'
  AND NOT EXISTS (SELECT 1 FROM product_categories pc
                  WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p
JOIN marks m   ON m.id = p.mark_id
JOIN categories c ON c.name IN ('Celulares', 'Electrónica')
WHERE p.name = 'Redmi Note 13 Pro'
  AND NOT EXISTS (SELECT 1 FROM product_categories pc
                  WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p
JOIN marks m   ON m.id = p.mark_id
JOIN categories c ON c.name IN ('Computación', 'Gaming')
WHERE p.name = 'Monitor 24" Full HD'
  AND NOT EXISTS (SELECT 1 FROM product_categories pc
                  WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- ----------------------------------------------------------------------------
-- 5) PRODUCTOS ADICIONALES (para probar la paginación)
--    Se agregan 25 productos más sobre marcas y categorías ya creadas.
-- ----------------------------------------------------------------------------
INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'MacBook Air M3', 'Notebook ultraliviana con chip M3 y 13.6 pulgadas.', 20, 1.24, 900.0, 1299.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Apple'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'MacBook Air M3');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'iPad Air', 'Tablet con pantalla Liquid Retina y chip M1.', 35, 0.46, 450.0, 649.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Apple'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'iPad Air');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Apple Watch Series 9', 'Reloj inteligente con pantalla siempre activa.', 45, 0.04, 300.0, 429.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Apple'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Apple Watch Series 9');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'AirPods Pro 2', 'Audífonos inalámbricos con cancelación activa de ruido.', 70, 0.06, 180.0, 279.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Apple'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'AirPods Pro 2');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Galaxy Tab S9', 'Tablet premium con pantalla AMOLED 11 pulgadas.', 22, 0.50, 550.0, 749.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Samsung'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Galaxy Tab S9');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Galaxy Watch 6', 'Reloj inteligente con medición de salud avanzada.', 55, 0.06, 250.0, 359.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Samsung'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Galaxy Watch 6');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Galaxy Buds2 Pro', 'Audífonos inalámbricos con audio 24 bits.', 65, 0.07, 150.0, 229.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Samsung'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Galaxy Buds2 Pro');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Smart TV 55" Neo QLED', 'Televisor 4K con tecnología Neo QLED.', 15, 18.5, 900.0, 1199.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Samsung'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Smart TV 55" Neo QLED');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'ThinkPad X1 Carbon', 'Notebook empresarial ultraliviana con procesador Intel.', 18, 1.12, 950.0, 1399.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Lenovo'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'ThinkPad X1 Carbon');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Legion Go', 'Consola portátil de gaming con pantalla 8.8 pulgadas.', 12, 0.85, 600.0, 799.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Lenovo'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Legion Go');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Yoga 7i 2-en-1', 'Notebook convertible con pantalla táctil 14 pulgadas.', 28, 1.38, 650.0, 899.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Lenovo'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Yoga 7i 2-en-1');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'IdeaPad Gaming 3', 'Notebook gaming con RTX 4060 y 16 GB RAM.', 20, 2.34, 850.0, 1099.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Lenovo'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'IdeaPad Gaming 3');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'PlayStation 5', 'Consola de videojuegos de última generación.', 25, 4.50, 450.0, 549.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Sony'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'PlayStation 5');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Bravia 65" OLED', 'Televisor 4K OLED con procesador cognitivo.', 10, 22.0, 1200.0, 1599.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Sony'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Bravia 65" OLED');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Parlante SRS-XB100', 'Parlante Bluetooth portátil resistente al agua.', 90, 0.27, 45.0, 79.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Sony'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Parlante SRS-XB100');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Cámara Alpha ZV-E10', 'Cámara mirrorless para creadores de contenido.', 14, 0.34, 700.0, 949.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Sony'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Cámara Alpha ZV-E10');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Xiaomi 14 Ultra', 'Smartphone flagship con cámara Leica Quad.', 18, 0.21, 850.0, 1099.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Xiaomi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Xiaomi 14 Ultra');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Poco X6 Pro', 'Smartphone gamer con pantalla 120 Hz.', 75, 0.19, 250.0, 379.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Xiaomi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Poco X6 Pro');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Redmi Pad SE', 'Tablet económica con pantalla 11 pulgadas.', 60, 0.51, 120.0, 199.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Xiaomi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Redmi Pad SE');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Mi Band 8', 'Banda inteligente con monitoreo de salud.', 120, 0.03, 30.0, 49.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Xiaomi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Mi Band 8');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Scooter eléctrico Pro 2', 'Scooter plegable con alcance de 45 km.', 16, 14.2, 400.0, 599.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Xiaomi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Scooter eléctrico Pro 2');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Teclado Mecánico K7', 'Teclado mecánico inalámbrico con retroiluminación RGB.', 85, 0.80, 60.0, 99.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Lenovo'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Teclado Mecánico K7');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Mouse Bluetooth M4', 'Mouse inalámbrico ergonómico y silencioso.', 110, 0.09, 12.0, 24.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Samsung'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Mouse Bluetooth M4');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Cargador GaN 65W', 'Cargador rápido USB-C con tecnología GaN.', 95, 0.11, 25.0, 44.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Apple'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Cargador GaN 65W');

INSERT INTO products (mark_id, name, description, stock, weight, price_cost, price_sale, image_path)
SELECT m.id, 'Webcam Full HD', 'Cámara web Full HD con micrófono integrado.', 70, 0.21, 30.0, 54.99, '/uploads/image_not_found.png'
FROM marks m WHERE m.name = 'Sony'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.name = 'Webcam Full HD');

-- ----------------------------------------------------------------------------
-- 6) RELACIONES products <-> categories de los productos adicionales
-- ----------------------------------------------------------------------------
INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p JOIN categories c ON c.name IN ('Computación', 'Oficina')
WHERE p.name IN ('MacBook Air M3', 'ThinkPad X1 Carbon', 'Yoga 7i 2-en-1', 'Teclado Mecánico K7')
  AND NOT EXISTS (SELECT 1 FROM product_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p JOIN categories c ON c.name IN ('Computación', 'Electrónica')
WHERE p.name IN ('iPad Air', 'Galaxy Tab S9', 'Redmi Pad SE', 'Webcam Full HD')
  AND NOT EXISTS (SELECT 1 FROM product_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p JOIN categories c ON c.name IN ('Celulares', 'Electrónica')
WHERE p.name IN ('Xiaomi 14 Ultra', 'Poco X6 Pro', 'Mi Band 8')
  AND NOT EXISTS (SELECT 1 FROM product_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p JOIN categories c ON c.name IN ('Audio', 'Electrónica')
WHERE p.name IN ('AirPods Pro 2', 'Galaxy Buds2 Pro', 'Parlante SRS-XB100')
  AND NOT EXISTS (SELECT 1 FROM product_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p JOIN categories c ON c.name IN ('Gaming', 'Electrónica')
WHERE p.name IN ('Legion Go', 'IdeaPad Gaming 3', 'PlayStation 5')
  AND NOT EXISTS (SELECT 1 FROM product_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO product_categories (product_id, category_id)
SELECT p.id, c.id
FROM products p JOIN categories c ON c.name IN ('Electrónica')
WHERE p.name IN ('Apple Watch Series 9', 'Galaxy Watch 6', 'Smart TV 55" Neo QLED', 'Bravia 65" OLED',
                 'Cámara Alpha ZV-E10', 'Scooter eléctrico Pro 2', 'Cargador GaN 65W', 'Mouse Bluetooth M4')
  AND NOT EXISTS (SELECT 1 FROM product_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);
