from django.db import models
from django.core.validators import FileExtensionValidator

class Producto(models.Model):
    """Modelo de Productos del catálogo"""
    nombre = models.CharField(max_length=255, unique=True)
    descripcion = models.TextField(blank=True, null=True)
    precio = models.DecimalField(max_digits=10, decimal_places=2)
    cantidad = models.IntegerField(default=0)
    imagen = models.ImageField(
        upload_to='productos/',
        blank=True,
        null=True,
        validators=[FileExtensionValidator(allowed_extensions=['jpg', 'jpeg', 'png', 'gif'])]
    )
    categoria = models.CharField(max_length=100, blank=True)
    activo = models.BooleanField(default=True)
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    
    class Meta:
        verbose_name = 'Producto'
        verbose_name_plural = 'Productos'
        db_table = 'productos'
        ordering = ['nombre']
    
    def __str__(self):
        return self.nombre
