from rest_framework import viewsets, filters
from rest_framework.permissions import IsAuthenticated
from .models import Producto
from .serializers import ProductoSerializer

class ProductoViewSet(viewsets.ModelViewSet):
    queryset = Producto.objects.filter(activo=True)
    serializer_class = ProductoSerializer
    permission_classes = [IsAuthenticated]
    filter_backends = [filters.SearchFilter, filters.OrderingFilter]
    search_fields = ['nombre', 'categoria']
    ordering_fields = ['nombre', 'precio', 'fecha_creacion']
    ordering = ['nombre']
