from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework import status
from django.contrib.auth.decorators import login_required
from django.shortcuts import render
from django.views.decorators.http import require_http_methods
from apps.usuarios.models import Usuario
from .serializers import ClienteSerializer, ClienteBulkUploadSerializer

class ClienteViewSet(viewsets.ModelViewSet):
    queryset = Usuario.objects.filter(rol='cliente', activo=True)
    serializer_class = ClienteSerializer
    permission_classes = [IsAuthenticated]
    
    @action(detail=False, methods=['post'], permission_classes=[IsAuthenticated])
    def bulk_upload(self, request):
        """
        Endpoint para carga masiva de clientes desde archivo CSV o Excel
        
        Formato esperado:
        - CSV o Excel con columna 'nombre' (requerida)
        - Columnas opcionales: apellido, tipo_documento, numero_documento, barrio, telefono, ciudad, direccion, codigo_postal
        """
        serializer = ClienteBulkUploadSerializer(data=request.FILES)
        
        if serializer.is_valid():
            resultado = serializer.save()
            return Response(resultado, status=status.HTTP_201_CREATED)
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@login_required
@require_http_methods(["GET"])
def bulk_upload_clientes_view(request):
    """Vista para mostrar el formulario de carga masiva de clientes"""
    return render(request, 'clientes/bulk_upload.html')
