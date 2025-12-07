-- Script para actualizar el estado de todos los usuarios existentes
UPDATE usuarios SET estado='activo' WHERE estado IS NULL OR estado='';
