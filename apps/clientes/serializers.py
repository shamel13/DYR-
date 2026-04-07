from rest_framework import serializers
from apps.usuarios.models import Usuario
from .services import GeocodingService
import pandas as pd
import uuid

class ClienteSerializer(serializers.ModelSerializer):
    """Serializer para clientes almacenados en el modelo Usuario"""

    class Meta:
        model = Usuario
        fields = (
            'id', 'username', 'email', 'first_name', 'last_name', 'rol',
            'tipo_documento', 'numero_documento', 'barrio', 'telefono',
            'ciudad', 'direccion', 'codigo_postal', 'latitud', 'longitud',
            'activo', 'fecha_creacion'
        )
        read_only_fields = ('id', 'fecha_creacion', 'rol')

    def create(self, validated_data):
        username = validated_data.get('username') or validated_data.get('email')
        if not username:
            username = f"cliente_{uuid.uuid4().hex[:8]}"
        validated_data['username'] = username
        user = Usuario.objects.create_user(**validated_data)
        self._geocode_usuario(user)
        return user

    def update(self, instance, validated_data):
        usuario = super().update(instance, validated_data)
        if any(key in validated_data for key in ['direccion', 'ciudad', 'codigo_postal']):
            self._geocode_usuario(usuario)
        return usuario

    def _geocode_usuario(self, usuario):
        coords = GeocodingService.geocode_address(
            usuario.direccion, usuario.ciudad, usuario.codigo_postal
        )
        if coords:
            usuario.latitud, usuario.longitud = coords
            usuario.save(update_fields=['latitud', 'longitud'])


class ClienteBulkUploadSerializer(serializers.Serializer):
    """Serializer para carga masiva de clientes desde archivo CSV o Excel"""
    archivo = serializers.FileField(help_text="Archivo CSV o Excel con clientes")

    def validate_archivo(self, value):
        if not value.name.endswith(('.csv', '.xlsx', '.xls')):
            raise serializers.ValidationError(
                "Solo se aceptan archivos CSV (.csv), Excel (.xlsx) o Excel 97-2003 (.xls)"
            )
        if value.size > 10 * 1024 * 1024:
            raise serializers.ValidationError("El archivo no puede exceder 10 MB")
        return value

    def _geocode_usuario(self, usuario):
        coords = GeocodingService.geocode_address(
            usuario.direccion, usuario.ciudad, usuario.codigo_postal
        )
        if coords:
            usuario.latitud, usuario.longitud = coords
            usuario.save(update_fields=['latitud', 'longitud'])

    def create(self, validated_data):
        archivo = validated_data['archivo']
        try:
            if archivo.name.endswith('.csv'):
                df = pd.read_csv(archivo)
            else:
                df = pd.read_excel(archivo)
        except Exception as e:
            raise serializers.ValidationError(f"Error al leer el archivo: {str(e)}")

        columnas_requeridas = {'nombre'}
        if not columnas_requeridas.issubset(set(df.columns)):
            raise serializers.ValidationError(
                f"El archivo debe contener al menos la columna: {', '.join(columnas_requeridas)}"
            )

        clientes_creados = 0
        clientes_actualizados = 0
        errores = []

        for idx, row in df.iterrows():
            try:
                nombre = str(row['nombre']).strip() if pd.notna(row['nombre']) else None
                if not nombre or nombre.lower() == 'nan':
                    errores.append(f"Fila {idx + 2}: El nombre es requerido")
                    continue

                apellido = str(row.get('apellido', '')).strip() if pd.notna(row.get('apellido')) else ''
                tipo_documento = str(row.get('tipo_documento', '')).strip() if pd.notna(row.get('tipo_documento')) else ''
                numero_documento = str(row.get('numero_documento', '')).strip() if pd.notna(row.get('numero_documento')) else ''
                barrio = str(row.get('barrio', '')).strip() if pd.notna(row.get('barrio')) else ''
                telefono = str(row.get('telefono', '')).strip() if pd.notna(row.get('telefono')) else ''
                ciudad = str(row.get('ciudad', '')).strip() if pd.notna(row.get('ciudad')) else ''
                direccion = str(row.get('direccion', '')).strip() if pd.notna(row.get('direccion')) else ''
                codigo_postal = str(row.get('codigo_postal', '')).strip() if pd.notna(row.get('codigo_postal')) else ''
                email = str(row.get('email', '')).strip() if pd.notna(row.get('email')) else ''

                if email and Usuario.objects.filter(email=email).exists():
                    usuario = Usuario.objects.filter(email=email).first()
                elif numero_documento:
                    usuario = Usuario.objects.filter(numero_documento=numero_documento, rol='cliente').first()
                else:
                    usuario = Usuario.objects.filter(first_name=nombre, last_name=apellido, rol='cliente').first()

                if usuario:
                    usuario.first_name = nombre
                    usuario.last_name = apellido
                    usuario.email = email or usuario.email
                    usuario.tipo_documento = tipo_documento
                    usuario.numero_documento = numero_documento
                    usuario.barrio = barrio
                    usuario.telefono = telefono
                    usuario.ciudad = ciudad
                    usuario.direccion = direccion
                    usuario.codigo_postal = codigo_postal
                    usuario.activo = True
                    usuario.save()
                    self._geocode_usuario(usuario)
                    clientes_actualizados += 1
                else:
                    username = email or numero_documento or f"cliente_{uuid.uuid4().hex[:8]}"
                    if Usuario.objects.filter(username=username).exists():
                        username = f"{username}_{uuid.uuid4().hex[:4]}"

                    if not email:
                        email = f"{username}@clientes.local"

                    usuario = Usuario.objects.create_user(
                        username=username,
                        email=email,
                        password=None,
                        first_name=nombre,
                        last_name=apellido,
                        rol='cliente',
                        tipo_documento=tipo_documento,
                        numero_documento=numero_documento,
                        barrio=barrio,
                        telefono=telefono,
                        ciudad=ciudad,
                        direccion=direccion,
                        codigo_postal=codigo_postal,
                        activo=True,
                    )
                    self._geocode_usuario(usuario)
                    clientes_creados += 1

            except Exception as e:
                errores.append(f"Fila {idx + 2}: {str(e)}")

        return {
            'clientes_creados': clientes_creados,
            'clientes_actualizados': clientes_actualizados,
            'total_filas': len(df),
            'errores': errores,
            'exitoso': len(errores) == 0
        }

