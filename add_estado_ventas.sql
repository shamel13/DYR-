-- Agregar columna estado a la tabla ventas
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS estado VARCHAR(50) DEFAULT 'Completado';

-- Actualizar registros existentes para que tengan estado 'Completado'
UPDATE ventas SET estado = 'Completado' WHERE estado IS NULL;
