from rest_framework import viewsets, status
from rest_framework.permissions import IsAuthenticated
from .models import Pedido, DetallePedido
from .serializers import PedidoSerializer, DetallePedidoSerializer
import uuid

class PedidoViewSet(viewsets.ModelViewSet):
    queryset = Pedido.objects.all()
    serializer_class = PedidoSerializer
    permission_classes = [IsAuthenticated]
    
    def perform_create(self, serializer):
        numero_pedido = f"PED-{uuid.uuid4().hex[:8].upper()}"
        serializer.save(usuario_creacion=self.request.user, numero_pedido=numero_pedido)
