from django.db import models
from apps.pedidos.models import Pedido

class Pago(models.Model):
    """Modelo de Pagos"""
    METODOS_PAGO = (
        ('tarjeta_credito', 'Tarjeta de Crédito'),
        ('tarjeta_debito', 'Tarjeta de Débito'),
        ('transferencia', 'Transferencia Bancaria'),
        ('contraentrega', 'Contra Entrega'),
    )
    
    ESTADOS_PAGO = (
        ('pendiente', 'Pendiente'),
        ('procesando', 'Procesando'),
        ('completado', 'Completado'),
        ('rechazado', 'Rechazado'),
        ('reembolsado', 'Reembolsado'),
    )
    
    pedido = models.OneToOneField(Pedido, on_delete=models.CASCADE, related_name='pago')
    metodo = models.CharField(max_length=20, choices=METODOS_PAGO)
    monto = models.DecimalField(max_digits=15, decimal_places=2)
    estado = models.CharField(max_length=20, choices=ESTADOS_PAGO, default='pendiente')
    referencia_transaccion = models.CharField(max_length=100, blank=True, unique=True)
    fecha_pago = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    razon_rechazo = models.TextField(blank=True)
    
    class Meta:
        verbose_name = 'Pago'
        verbose_name_plural = 'Pagos'
        db_table = 'pagos'
        ordering = ['-fecha_pago']
    
    def __str__(self):
        return f"Pago {self.pedido.numero_pedido} - {self.get_estado_display()}"
