-- Actualizar productos existentes con imágenes de placeholder
UPDATE producto 
SET imagen_url = CONCAT('/uploads/productos/', UUID(), '.jpg')
WHERE imagen_url IS NULL OR imagen_url = '';

-- Verificar que se actualizaron
SELECT id, nombre, imagen_url FROM producto LIMIT 5;
