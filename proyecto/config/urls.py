from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from rest_framework_simplejwt.views import TokenRefreshView
from rest_framework.routers import DefaultRouter

# Importar todos los ViewSets
from apps.usuarios.views import UsuarioViewSet
from apps.productos.views import ProductoViewSet
from apps.clientes.views import ClienteViewSet
from apps.pedidos.views import PedidoViewSet
from apps.pagos.views import PagoViewSet
from apps.ventas.views import VentaViewSet
from apps.inventario.views import InventarioViewSet, MovimientoInventarioViewSet
from apps.dashboard.views import DashboardViewSet

# Crear router
router = DefaultRouter()
router.register(r'usuarios', UsuarioViewSet)
router.register(r'productos', ProductoViewSet)
router.register(r'clientes', ClienteViewSet, basename='clientes')
router.register(r'pedidos', PedidoViewSet)
router.register(r'pagos', PagoViewSet)
router.register(r'ventas', VentaViewSet)
router.register(r'inventario', InventarioViewSet)
router.register(r'movimientos', MovimientoInventarioViewSet)
router.register(r'dashboard', DashboardViewSet)

urlpatterns = [
    path('', include('apps.pagina.urls')),
    path('admin/', admin.site.urls),
    path('api/', include(router.urls)),
    path('api/token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('api-auth/', include('rest_framework.urls')),
    path('clientes/', include('apps.clientes.urls')),
]

# Servir archivos media
if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
    urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
