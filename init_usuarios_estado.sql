-- Actualizar todos los usuarios existentes a estado 'activo'
UPDATE usuarios SET estado='activo' WHERE estado IS NULL OR estado='';
-- Configurar el valor por defecto en la columna para nuevos registros
ALTER TABLE usuarios MODIFY COLUMN estado VARCHAR(20) DEFAULT 'activo';
