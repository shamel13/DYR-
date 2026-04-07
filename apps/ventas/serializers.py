from rest_framework import serializers
from .models import Venta, DetalleVenta
from apps.productos.serializers import ProductoSerializer
from apps.clientes.serializers import ClienteSerializer
from apps.usuarios.serializers import UsuarioSerializer

class DetalleVentaSerializer(serializers.ModelSerializer):
    """Serializer para detalles de venta"""
    producto = ProductoSerializer(read_only=True)
    producto_id = serializers.IntegerField(write_only=True)
    
    class Meta:
        model = DetalleVenta
        fields = ('id', 'producto', 'producto_id', 'cantidad', 'precio_unitario', 'subtotal')

class VentaSerializer(serializers.ModelSerializer):
    """Serializer para el modelo Venta"""
    cliente = ClienteSerializer(read_only=True)
    cliente_id = serializers.IntegerField(write_only=True)
    vendedor = UsuarioSerializer(read_only=True)
    detalles = DetalleVentaSerializer(many=True, read_only=True)
    
    class Meta:
        model = Venta
        fields = ('id', 'cliente', 'cliente_id', 'numero_venta', 'fecha_venta', 
                  'total_venta', 'descripcion', 'vendedor', 'comision_vendedor', 
                  'detalles', 'fecha_actualizacion')
        read_only_fields = ('id', 'numero_venta', 'fecha_venta', 'fecha_actualizacion')
