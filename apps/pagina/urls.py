from django.urls import path
from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('pagina/index/', views.index, name='pagina_index'),
    path('pagina/catalogo/', views.catalogo, name='pagina_catalogo'),
    path('pagina/acerca/', views.acerca, name='pagina_acerca'),

    path('auth/login/', views.login_view, name='login'),
    path('auth/register/', views.register_view, name='register'),
    path('auth/profile/', views.profile, name='profile'),
    path('auth/logout/', views.logout_view, name='logout'),

    path('dashboard/home/', views.dashboard_home, name='dashboard_home'),

    path('clientes/', views.clientes_index, name='clientes_index'),
    path('clientes/store/', views.clientes_store, name='clientes_store'),
    path('clientes/<int:id>/', views.clientes_show, name='clientes_show'),
    path('clientes/<int:id>/edit/', views.clientes_edit, name='clientes_edit'),
    path('clientes/<int:id>/toggle/', views.clientes_toggle, name='clientes_toggle'),

    path('usuarios/', views.usuarios_index, name='usuarios_index'),
    path('usuarios/<int:id>/', views.usuarios_show, name='usuarios_show'),
    path('usuarios/<int:id>/edit/', views.usuarios_edit, name='usuarios_edit'),
    path('usuarios/<int:id>/toggle/', views.usuarios_toggle, name='usuarios_toggle'),

    path('productos/', views.productos_index, name='productos_index'),
    path('productos/<int:id>/', views.productos_show, name='productos_show'),
    path('productos/<int:id>/edit/', views.productos_edit, name='productos_edit'),
    path('productos/<int:id>/delete/', views.productos_delete, name='productos_delete'),

    path('inventario/', views.inventario_index, name='inventario_index'),
    path('inventario/<int:id>/', views.inventario_show, name='inventario_show'),
    path('inventario/<int:id>/edit/', views.inventario_edit, name='inventario_edit'),

    path('pedidos/', views.pedidos_index, name='pedidos_index'),
    path('pedidos/<int:id>/', views.pedidos_show, name='pedidos_show'),
    path('pedidos/<int:id>/edit/', views.pedidos_edit, name='pedidos_edit'),
    path('pedidos/<int:id>/toggle/', views.pedidos_toggle, name='pedidos_toggle'),
    path('pedidos/<int:id>/cambiar_estado/', views.pedidos_cambiar_estado, name='pedidos_cambiar_estado'),
    path('pedidos/<int:id>/cambiar_datos/', views.pedidos_cambiar_datos, name='pedidos_cambiar_datos'),

    path('ventas/', views.ventas_index, name='ventas_index'),
    path('ventas/<int:id>/', views.ventas_show, name='ventas_show'),
    path('ventas/<int:id>/edit/', views.ventas_edit, name='ventas_edit'),

    path('pagos/', views.pagos_index, name='pagos_index'),
    path('pagos/nuevo/', views.pagos_nuevo, name='pagos_nuevo'),
    path('pagos/<int:id>/', views.pagos_show, name='pagos_show'),
    path('pagos/<int:id>/confirmacion/', views.pagos_confirmacion, name='pagos_confirmacion'),

    path('reportes/<str:modelo>/', views.reportes_crud, name='reportes_crud'),
]
