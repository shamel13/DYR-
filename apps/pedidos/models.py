from django.db import models
from apps.productos.models import Producto
from apps.usuarios.models import Usuario

class Pedido(models.Model):
    """Modelo de Pedidos"""
    ESTADOS_CHOICES = (
        ('pendiente', 'Pendiente'),
        ('procesando', 'Procesando'),
        ('enviado', 'Enviado'),
        ('entregado', 'Entregado'),
        ('cancelado', 'Cancelado'),
    )
    
    cliente = models.ForeignKey(Usuario, on_delete=models.PROTECT, related_name='pedidos_cliente')
    numero_pedido = models.CharField(max_length=50, unique=True)
    fecha_pedido = models.DateTimeField(auto_now_add=True)
    estado = models.CharField(max_length=20, choices=ESTADOS_CHOICES, default='pendiente')
    total = models.DecimalField(max_digits=15, decimal_places=2, default=0)
    
    # Información de envío
    direccion_envio = models.TextField()
    ciudad_envio = models.CharField(max_length=100)
    codigo_postal_envio = models.CharField(max_length=20)
    transportista = models.CharField(max_length=100, blank=True)
    numero_seguimiento = models.CharField(max_length=100, blank=True)
    
    notas = models.TextField(blank=True)
    usuario_creacion = models.ForeignKey(Usuario, on_delete=models.PROTECT)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    
    class Meta:
        verbose_name = 'Pedido'
        verbose_name_plural = 'Pedidos'
        db_table = 'pedidos'
        ordering = ['-fecha_pedido']
    
    def __str__(self):
        return f"Pedido #{self.numero_pedido}"

class DetallePedido(models.Model):
    """Detalles de items en cada pedido"""
    pedido = models.ForeignKey(Pedido, on_delete=models.CASCADE, related_name='detalles')
    producto = models.ForeignKey(Producto, on_delete=models.PROTECT)
    cantidad = models.IntegerField()
    precio_unitario = models.DecimalField(max_digits=10, decimal_places=2)
    subtotal = models.DecimalField(max_digits=15, decimal_places=2)
    
    class Meta:
        verbose_name = 'Detalle de Pedido'
        verbose_name_plural = 'Detalles de Pedido'
        db_table = 'detalles_pedido'
    
    def __str__(self):
        return f"{self.pedido.numero_pedido} - {self.producto.nombre}"
