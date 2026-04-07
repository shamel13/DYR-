# 🚀 Proyecto DYR - Django

Migración completada de Java Spring Boot a **Python Django** con REST API.

## 📋 Requisitos

- Python 3.12+
- pip (Python package manager)
- MySQL 8.0+ (opcional, para producción)

## ⚡ Inicio Rápido

### 1. Clonar/Descargar el proyecto

```bash
cd proyecto
```

### 2. Crear entorno virtual (si no existe)

```bash
python -m venv venv

# Activar entorno
# En Windows:
venv\Scripts\activate
# En Mac/Linux:
source venv/bin/activate
```

### 3. Instalar dependencias

```bash
pip install -r requirements.txt
```

### 4. Aplicar migraciones

```bash
python manage.py migrate
```

### 5. Crear usuario admin (opcional)

```bash
python manage.py createsuperuser
```

### 6. Iniciar servidor

```bash
python manage.py runserver
```

✅ El servidor estará en `http://localhost:8000`

---

## 🛠️ Comandos Útiles

### Desarrollo
```bash
# Iniciar servidor en puerto específico
python manage.py runserver 8080

# Abrir Django shell para experimentos
python manage.py shell

# Crear migraciones nuevas
python manage.py makemigrations

# Aplicar migraciones
python manage.py migrate

# Resetear base de datos
rm db.sqlite3
python manage.py migrate
```

### Scripts Rápidos (Windows)
```bash
dev.bat start              # Iniciar servidor
dev.bat migrate            # Ejecutar migraciones
dev.bat makemigrations     # Crear migraciones
dev.bat admin              # Crear admin
dev.bat shell              # Abrir shell
dev.bat reset              # Resetear BD
```

---

## 📚 API Endpoints

### Base URL
```
http://localhost:8000/api
```

### Autenticación
```
POST   /usuarios/register/          - Registrarse
POST   /usuarios/login/             - Login (obtener JWT)
GET    /usuarios/me/                - Mi perfil
```

### Recursos Principales
```
/usuarios/                 - Gestión de usuarios
/productos/                - Catálogo de productos
/clientes/                 - Gestión de clientes
/pedidos/                  - Gestión de pedidos
/pagos/                    - Gestión de pagos
/ventas/                   - Registro de ventas
/inventario/               - Control de inventario
/movimientos/              - Movimientos de stock
/dashboard/                - Dashboard y estadísticas
```

---

## 🔐 Ejemplo de Login

```bash
# 1. Registrarse
curl -X POST http://localhost:8000/api/usuarios/register/ \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan",
    "email": "juan@gmail.com",
    "password": "MiPassword123"
  }'

# 2. Login y obtener token
curl -X POST http://localhost:8000/api/usuarios/login/ \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan",
    "password": "MiPassword123"
  }'

# 3. Usar token para llamadas autenticadas
curl -H "Authorization: Bearer {access_token}" \
  http://localhost:8000/api/usuarios/
```

---

## 🗄️ Base de Datos

### Desarrollo (SQLite)
- Archivo: `db.sqlite3`
- Sin configuración necesaria
- Ideal para desarrollo local

### Producción (MySQL)
1. Crear base de datos:
```sql
CREATE DATABASE proyecto_dyr CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Editar `config/settings.py`:
```python
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'proyecto_dyr',
        'USER': 'tu_usuario',
        'PASSWORD': 'tu_contraseña',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}
```

3. Aplicar migraciones:
```bash
python manage.py migrate
```

---

## 📁 Estructura del Proyecto

```
proyecto/
├── config/                  # Configuración central
│   ├── settings.py         # Configuraciones Django
│   ├── urls.py             # Rutas principales
│   └── wsgi.py
├── usuarios/               # App de usuarios
├── productos/              # App de productos
├── clientes/               # App de clientes
├── pedidos/                # App de pedidos
├── pagos/                  # App de pagos
├── ventas/                 # App de ventas
├── inventario/             # App de inventario
├── dashboard/              # App de dashboard
├── templates/              # Plantillas HTML
├── static/                 # CSS, JS, imágenes
├── media/                  # Uploads de usuario
├── manage.py               # Script de gestión
├── requirements.txt        # Dependencias
└── db.sqlite3              # Base de datos
```

---

## 🔧 Configuración

### Variables de Entorno
Copiar `.env.example` a `.env`:

```bash
cp .env.example .env
```

Editar según necesites.

### CORS
Para permitir frontend en otro puerto:

```python
# config/settings.py
CORS_ALLOWED_ORIGINS = [
    'http://localhost:3000',    # React
    'http://localhost:8080',    # Vue
    'http://tu-dominio.com',
]
```

---

## 🚀 Deployment

### Usando Gunicorn

```bash
# Instalar Gunicorn
pip install gunicorn

# Correr aplicación
gunicorn config.wsgi --bind 0.0.0.0:8000
```

### Con Nginx (proxy reverso)
```nginx
server {
    listen 80;
    server_name tu-dominio.com;

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /static/ {
        alias /ruta/al/proyecto/staticfiles/;
    }

    location /media/ {
        alias /ruta/al/proyecto/media/;
    }
}
```

---

## 📝 Documentación

- [Django Official Docs](https://docs.djangoproject.com/)
- [Django REST Framework](https://www.django-rest-framework.org/)
- [JWT en Django](https://django-rest-framework-simplejwt.readthedocs.io/)

---

## 🐛 Troubleshooting

### Puerto 8000 en uso
```bash
# Windows
netstat -ano | findstr :8000
taskkill /PID {PID} /F

# Mac/Linux
lsof -i :8000
kill -9 {PID}
```

### Problemas de importación
```bash
# Reinstalar dependencias
pip install --force-reinstall -r requirements.txt
```

### Base de datos corrupta
```bash
# Resetear
rm db.sqlite3
python manage.py migrate
```

---

## 📞 Soporte

Para problemas o preguntas, revisa la documentación en `MIGRACION_DJANGO_COMPLETA.md`

---

**Estado:** ✅ Proyecto Django migrado y funcional

