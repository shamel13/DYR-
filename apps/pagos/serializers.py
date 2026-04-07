from rest_framework import serializers
from .models import Pago
from apps.pedidos.serializers import PedidoSerializer

class PagoSerializer(serializers.ModelSerializer):
    """Serializer para el modelo Pago"""
    pedido = PedidoSerializer(read_only=True)
    pedido_id = serializers.IntegerField(write_only=True)
    
    class Meta:
        model = Pago
        fields = ('id', 'pedido', 'pedido_id', 'metodo', 'monto', 'estado', 
                  'referencia_transaccion', 'fecha_pago', 'razon_rechazo')
        read_only_fields = ('id', 'fecha_pago')
    
    def validate_monto(self, value):
        if value <= 0:
            raise serializers.ValidationError("El monto debe ser mayor a 0")
        return value
