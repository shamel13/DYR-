from rest_framework import serializers
from .models import Pedido, DetallePedido
from apps.productos.serializers import ProductoSerializer
from apps.clientes.serializers import ClienteSerializer

class DetallePedidoSerializer(serializers.ModelSerializer):
    """Serializer para detalles de pedido"""
    producto = ProductoSerializer(read_only=True)
    producto_id = serializers.IntegerField(write_only=True)
    
    class Meta:
        model = DetallePedido
        fields = ('id', 'producto', 'producto_id', 'cantidad', 'precio_unitario', 'subtotal')

class PedidoSerializer(serializers.ModelSerializer):
    """Serializer para el modelo Pedido"""
    cliente = ClienteSerializer(read_only=True)
    cliente_id = serializers.IntegerField(write_only=True)
    detalles = DetallePedidoSerializer(many=True, read_only=True)
    
    class Meta:
        model = Pedido
        fields = ('id', 'cliente', 'cliente_id', 'numero_pedido', 'fecha_pedido', 
                  'estado', 'total', 'direccion_envio', 'ciudad_envio', 
                  'codigo_postal_envio', 'transportista', 'numero_seguimiento', 
                  'notas', 'detalles', 'fecha_actualizacion')
        read_only_fields = ('id', 'numero_pedido', 'fecha_pedido', 'fecha_actualizacion')
