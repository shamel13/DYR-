from rest_framework import serializers
from .models import Dashboard

class DashboardSerializer(serializers.ModelSerializer):
    """Serializer para el modelo Dashboard"""
    
    class Meta:
        model = Dashboard
        fields = ('id', 'fecha', 'total_ventas', 'total_pedidos', 'total_pagos', 
                  'total_productos', 'clientes_nuevos')
        read_only_fields = ('id', 'fecha')
