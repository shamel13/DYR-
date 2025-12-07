UPDATE producto 
SET imagen_url = CASE 
    WHEN id % 6 = 1 THEN '/uploads/productos/19eae8be-53bf-4e6b-96bf-8f06d63d9451.jpg'
    WHEN id % 6 = 2 THEN '/uploads/productos/1c57012b-d185-4fd4-9af5-f5d86e9f928f.jpg'
    WHEN id % 6 = 3 THEN '/uploads/productos/44a679d9-fbad-451d-8e7a-abe1f3098854.png'
    WHEN id % 6 = 4 THEN '/uploads/productos/4eb353d2-2fbc-4124-a33d-e36ba448b713.jpg'
    WHEN id % 6 = 5 THEN '/uploads/productos/72868707-87e6-4af8-82b5-ce316774be4d.jpg'
    ELSE '/uploads/productos/a07cbda4-d819-459d-a5f0-14bc36f2e573.jpeg'
END
WHERE imagen_url IS NULL;

SELECT COUNT(*) FROM producto WHERE imagen_url IS NOT NULL;
