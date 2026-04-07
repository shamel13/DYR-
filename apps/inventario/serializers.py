from rest_framework import serializers
from .models import Inventario, MovimientoInventario
from apps.productos.serializers import ProductoSerializer
from apps.usuarios.serializers import UsuarioSerializer

class InventarioSerializer(serializers.ModelSerializer):
    """Serializer para el modelo Inventario"""
    producto = ProductoSerializer(read_only=True)
    producto_id = serializers.IntegerField(write_only=True)
    
    class Meta:
        model = Inventario
        fields = ('id', 'producto', 'producto_id', 'cantidad_total', 
                  'cantidad_disponible', 'cantidad_reservada', 'fecha_ultima_actualizacion')
        read_only_fields = ('id', 'fecha_ultima_actualizacion')

class MovimientoInventarioSerializer(serializers.ModelSerializer):
    """Serializer para movimientos de inventario"""
    producto = ProductoSerializer(read_only=True)
    producto_id = serializers.IntegerField(write_only=True)
    usuario = UsuarioSerializer(read_only=True)
    usuario_id = serializers.IntegerField(write_only=True)
    
    class Meta:
        model = MovimientoInventario
        fields = ('id', 'producto', 'producto_id', 'tipo', 'cantidad', 'razon', 
                  'usuario', 'usuario_id', 'fecha')
        read_only_fields = ('id', 'fecha')
