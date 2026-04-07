from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from .models import Venta, DetalleVenta
from .serializers import VentaSerializer, DetalleVentaSerializer
import uuid

class VentaViewSet(viewsets.ModelViewSet):
    queryset = Venta.objects.all()
    serializer_class = VentaSerializer
    permission_classes = [IsAuthenticated]
    
    def perform_create(self, serializer):
        numero_venta = f"VTA-{uuid.uuid4().hex[:8].upper()}"
        serializer.save(numero_venta=numero_venta)
