from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from .models import Inventario, MovimientoInventario
from .serializers import InventarioSerializer, MovimientoInventarioSerializer
from apps.productos.models import Producto
from apps.productos.serializers import ProductoSerializer

class InventarioViewSet(viewsets.ModelViewSet):
    queryset = Inventario.objects.all()
    serializer_class = InventarioSerializer
    permission_classes = [IsAuthenticated]

    @action(detail=False, methods=['get'], url_path='productos-registrados')
    def productos_registrados(self, request):
        """Listar todos los productos con su inventario (si existe)."""
        productos = Producto.objects.filter(activo=True)
        data = []

        for producto in productos:
            inventario = Inventario.objects.filter(producto=producto).first()
            data.append({
                'producto': ProductoSerializer(producto).data,
                'inventario': InventarioSerializer(inventario).data if inventario else None
            })

        return Response(data)

    @action(detail=True, methods=['post'], url_path='ajustar-stock')
    def ajustar_stock(self, request, pk=None):
        """Ajustar stock total/disponible de un inventario existente."""
        inventario = self.get_object()
        cantidad = request.data.get('cantidad')

        if cantidad is None:
            return Response({'detail': 'Se requiere la cantidad a ajustar.'}, status=status.HTTP_400_BAD_REQUEST)

        try:
            cantidad_int = int(cantidad)
        except (TypeError, ValueError):
            return Response({'detail': 'La cantidad debe ser un número entero.'}, status=status.HTTP_400_BAD_REQUEST)

        inventario.cantidad_total = cantidad_int
        inventario.cantidad_disponible = cantidad_int
        inventario.save()

        return Response(InventarioSerializer(inventario).data)

    @action(detail=False, methods=['post'], url_path='registrar-producto')
    def registrar_producto(self, request):
        """Crear o actualizar el inventario de un producto desde lista de productos."""
        producto_id = request.data.get('producto_id')
        cantidad = request.data.get('cantidad')

        if not producto_id or cantidad is None:
            return Response({'detail': 'producto_id y cantidad son requeridos.'}, status=status.HTTP_400_BAD_REQUEST)

        try:
            producto = Producto.objects.get(id=producto_id)
        except Producto.DoesNotExist:
            return Response({'detail': 'Producto no encontrado.'}, status=status.HTTP_404_NOT_FOUND)

        try:
            cantidad_int = int(cantidad)
        except (TypeError, ValueError):
            return Response({'detail': 'La cantidad debe ser un número entero.'}, status=status.HTTP_400_BAD_REQUEST)

        inventario, _ = Inventario.objects.get_or_create(producto=producto)
        inventario.cantidad_total = cantidad_int
        inventario.cantidad_disponible = cantidad_int
        inventario.save()

        serializer = InventarioSerializer(inventario)
        return Response(serializer.data, status=status.HTTP_201_CREATED)


class MovimientoInventarioViewSet(viewsets.ModelViewSet):
    queryset = MovimientoInventario.objects.all()
    serializer_class = MovimientoInventarioSerializer
    permission_classes = [IsAuthenticated]
