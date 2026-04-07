from rest_framework import serializers
from .models import Producto

class ProductoSerializer(serializers.ModelSerializer):
    """Serializer para el modelo Producto"""
    
    class Meta:
        model = Producto
        fields = ('id', 'nombre', 'descripcion', 'precio', 'imagen', 
                  'categoria', 'activo', 'fecha_creacion', 'fecha_actualizacion')
        read_only_fields = ('id', 'fecha_creacion', 'fecha_actualizacion')
    
    def validate_precio(self, value):
        if value <= 0:
            raise serializers.ValidationError("El precio debe ser mayor a 0")
        return value
