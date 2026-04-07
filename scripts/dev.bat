@echo off
REM Script para ejecutar comandos comunes de Django en Windows

if "%1"=="start" (
    echo Iniciando servidor Django en http://localhost:8000
    venv\Scripts\python manage.py runserver
) else if "%1"=="migrate" (
    echo Ejecutando migraciones...
    venv\Scripts\python manage.py migrate
) else if "%1"=="makemigrations" (
    echo Creando archivos de migracion...
    venv\Scripts\python manage.py makemigrations
) else if "%1"=="admin" (
    echo Creando usuario admin...
    venv\Scripts\python manage.py createsuperuser
) else if "%1"=="superuser" (
    venv\Scripts\python manage.py createsuperuser
) else if "%1"=="shell" (
    echo Abriendo Django shell...
    venv\Scripts\python manage.py shell
) else if "%1"=="pip" (
    echo Instalando dependencias de requirements.txt...
    venv\Scripts\pip install -r requirements.txt
) else if "%1"=="collectstatic" (
    echo Recopilando archivos estaticos...
    venv\Scripts\python manage.py collectstatic --noinput
) else if "%1"=="reset" (
    echo Reseteando base de datos...
    del db.sqlite3
    venv\Scripts\python manage.py migrate
    echo Base de datos reseteada
) else (
    echo Comandos disponibles:
    echo   python dev.bat start              - Iniciar servidor de desarrollo
    echo   python dev.bat migrate            - Ejecutar migraciones
    echo   python dev.bat makemigrations     - Crear migraciones
    echo   python dev.bat admin              - Crear usuario admin
    echo   python dev.bat shell              - Abrir Django shell
    echo   python dev.bat pip                - Instalar dependencias
    echo   python dev.bat collectstatic      - Recopilar archivos estaticos
    echo   python dev.bat reset              - Resetear BD
)
