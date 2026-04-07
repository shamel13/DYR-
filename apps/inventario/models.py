from django.db import models
from apps.productos.models import Producto
from apps.usuarios.models import Usuario

class Inventario(models.Model):
    """Modelo de Inventario - Stock de productos"""
    producto = models.OneToOneField(Producto, on_delete=models.CASCADE, related_name='inventario')
    cantidad_total = models.IntegerField(default=0)
    cantidad_disponible = models.IntegerField(default=0)
    cantidad_reservada = models.IntegerField(default=0)
    fecha_ultima_actualizacion = models.DateTimeField(auto_now=True)
    
    class Meta:
        verbose_name = 'Inventario'
        verbose_name_plural = 'Inventarios'
        db_table = 'inventario'
    
    def __str__(self):
        return f"Stock: {self.producto.nombre} ({self.cantidad_disponible})"

class MovimientoInventario(models.Model):
    """Movimientos de entrada/salida de inventario"""
    TIPOS_MOVIMIENTO = (
        ('entrada', 'Entrada'),
        ('salida', 'Salida'),
        ('ajuste', 'Ajuste'),
        ('devolucion', 'Devolución'),
    )
    
    producto = models.ForeignKey(Producto, on_delete=models.PROTECT, related_name='movimientos')
    tipo = models.CharField(max_length=20, choices=TIPOS_MOVIMIENTO)
    cantidad = models.IntegerField()
    razon = models.TextField(blank=True)
    usuario = models.ForeignKey(Usuario, on_delete=models.PROTECT)
    fecha = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        verbose_name = 'Movimiento de Inventario'
        verbose_name_plural = 'Movimientos de Inventario'
        db_table = 'movimientos_inventario'
        ordering = ['-fecha']
    
    def __str__(self):
        return f"{self.get_tipo_display()}: {self.producto.nombre} ({self.cantidad})"
