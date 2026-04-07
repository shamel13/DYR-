from rest_framework import viewsets
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from .models import Dashboard
from .serializers import DashboardSerializer
from django.utils import timezone
from django.db.models import Sum
from apps.ventas.models import Venta
from apps.pedidos.models import Pedido
from apps.pagos.models import Pago
from apps.productos.models import Producto
from apps.usuarios.models import Usuario

class DashboardViewSet(viewsets.ModelViewSet):
    queryset = Dashboard.objects.all()
    serializer_class = DashboardSerializer
    permission_classes = [IsAuthenticated]
    
    @action(detail=False, methods=['get'])
    def resumen_hoy(self, request):
        hoy = timezone.now().date()
        total_ventas = Venta.objects.filter(fecha_venta__date=hoy).aggregate(Sum('total_venta'))['total_venta__sum'] or 0
        total_pedidos = Pedido.objects.filter(fecha_pedido__date=hoy).count()
        total_pagos = Pago.objects.filter(fecha_pago__date=hoy).aggregate(Sum('monto'))['monto__sum'] or 0
        
        return Response({
            'fecha': hoy,
            'total_ventas': float(total_ventas),
            'total_pedidos': total_pedidos,
            'total_pagos': float(total_pagos),
            'total_productos': Producto.objects.filter(activo=True).count(),
            'clientes_nuevos': Usuario.objects.filter(rol='cliente', fecha_creacion__date=hoy).count(),
        })
