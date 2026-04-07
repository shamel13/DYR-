from django.urls import path
from .views import bulk_upload_clientes_view

app_name = 'clientes'

urlpatterns = [
    path('bulk-upload/', bulk_upload_clientes_view, name='bulk_upload'),
]
