from django.db import models
from apps.productos.models import Producto
from apps.usuarios.models import Usuario
from django.utils import timezone

class Venta(models.Model):
    """Modelo de Registros de Ventas"""
    cliente = models.ForeignKey(Usuario, on_delete=models.PROTECT, related_name='ventas_como_cliente')
    numero_venta = models.CharField(max_length=50, unique=True)
    fecha_venta = models.DateTimeField(auto_now_add=True)
    total_venta = models.DecimalField(max_digits=15, decimal_places=2)
    descripcion = models.TextField(blank=True)
    vendedor = models.ForeignKey(Usuario, on_delete=models.PROTECT, related_name='ventas')
    comision_vendedor = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    
    class Meta:
        verbose_name = 'Venta'
        verbose_name_plural = 'Ventas'
        db_table = 'ventas'
        ordering = ['-fecha_venta']
    
    def __str__(self):
        return f"Venta #{self.numero_venta}"

class DetalleVenta(models.Model):
    """Detalles de items en cada venta"""
    venta = models.ForeignKey(Venta, on_delete=models.CASCADE, related_name='detalles')
    producto = models.ForeignKey(Producto, on_delete=models.PROTECT)
    cantidad = models.IntegerField()
    precio_unitario = models.DecimalField(max_digits=10, decimal_places=2)
    subtotal = models.DecimalField(max_digits=15, decimal_places=2)
    
    class Meta:
        verbose_name = 'Detalle de Venta'
        verbose_name_plural = 'Detalles de Venta'
        db_table = 'detalles_venta'
    
    def __str__(self):
        return f"{self.venta.numero_venta} - {self.producto.nombre}"
