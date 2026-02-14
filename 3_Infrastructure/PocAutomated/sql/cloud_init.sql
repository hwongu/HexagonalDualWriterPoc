-- cloud_init.sql
-- Base de Datos: Db_Cloud (Moderno)

-- 1. Limpieza
DROP TABLE IF EXISTS cliente;

-- 2. Creación de la tabla (Español)
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    razon_social VARCHAR(150),
    saldo_actual DECIMAL(15, 2)
);

-- 3. Inserción de datos iniciales
-- Iniciamos en 1001 para diferenciar visualmente los datos creados en Cloud
ALTER SEQUENCE cliente_id_cliente_seq RESTART WITH 1001;

INSERT INTO cliente (razon_social, saldo_actual) 
VALUES ('Bodega Don Pepe S.A. (Cloud)', 500.00);