-- Script de migración: Mover información de envío de usuarios a pedidos
-- Fecha: 7 de diciembre de 2025

-- Paso 1: Agregar columnas de dirección de envío a la tabla pedidos
ALTER TABLE pedidos 
ADD COLUMN IF NOT EXISTS shipping_address VARCHAR(255),
ADD COLUMN IF NOT EXISTS shipping_city VARCHAR(100),
ADD COLUMN IF NOT EXISTS shipping_state VARCHAR(100),
ADD COLUMN IF NOT EXISTS shipping_postal_code VARCHAR(20),
ADD COLUMN IF NOT EXISTS shipping_country VARCHAR(100),
ADD COLUMN IF NOT EXISTS shipping_phone VARCHAR(50),
ADD COLUMN IF NOT EXISTS shipping_email VARCHAR(100);

-- Paso 2: Eliminar columnas de dirección de envío de la tabla usuarios
-- (Solo si estás seguro de que no las necesitas)
ALTER TABLE usuarios 
DROP COLUMN IF EXISTS shipping_address,
DROP COLUMN IF EXISTS shipping_city,
DROP COLUMN IF EXISTS shipping_state,
DROP COLUMN IF EXISTS shipping_postal_code,
DROP COLUMN IF EXISTS shipping_country,
DROP COLUMN IF EXISTS alt_address,
DROP COLUMN IF EXISTS alt_city,
DROP COLUMN IF EXISTS alt_state,
DROP COLUMN IF EXISTS alt_postal_code,
DROP COLUMN IF EXISTS alt_country;

-- Nota: La información de envío ahora se guardará en cada pedido individual,
-- lo cual es más apropiado ya que un usuario puede tener diferentes direcciones
-- de envío para diferentes pedidos.
