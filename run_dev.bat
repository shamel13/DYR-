@echo off
REM Script para ejecutar el servidor Django en Windows

REM Activar venv
call venv\Scripts\activate.bat

REM Ejecutar servidor
python manage.py runserver 0.0.0.0:8000
