# Script para ejecutar servidor Django en Windows

# Activar venv si existe
if (Test-Path "venv\Scripts\Activate.ps1") {
    & "venv\Scripts\Activate.ps1"
} else {
    Write-Host "⚠️  No se encontró virtualenv. Creando..."
    python -m venv venv
    & "venv\Scripts\Activate.ps1"
    pip install -r requirements.txt
}

# Ejecutar servidor
Write-Host "🚀 Iniciando servidor Django..."
python manage.py runserver
