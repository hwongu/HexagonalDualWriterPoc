-- Recuerda primero crear la base de datos
CREATE DATABASE "Db_Cloud";

-- 1. Limpieza
DROP TABLE IF EXISTS cliente;

-- 2. Creación de la tabla 
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,    -- En español
    razon_social VARCHAR(150),        -- En español
    saldo_actual DECIMAL(15, 2)       -- En español
);

-- 3. Inserción de datos 
INSERT INTO cliente (razon_social, saldo_actual) 
VALUES ('Bodega Don Pepe S.A.', 500.00);

-- Verificamos
SELECT * FROM cliente;