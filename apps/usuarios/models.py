from django.db import models
from django.contrib.auth.models import AbstractUser
from django.core.validators import EmailValidator

class Usuario(AbstractUser):
    """Modelo personalizado de Usuario basado en AbstractUser"""
    
    ROLES_CHOICES = (
        ('admin', 'Administrador'),
        ('vendedor', 'Vendedor'),
        ('cliente', 'Cliente'),
        ('almacenista', 'Almacenista'),
    )
    
    groups = models.ManyToManyField('auth.Group', related_name='usuario_groups', blank=True)
    user_permissions = models.ManyToManyField('auth.Permission', related_name='usuario_permissions', blank=True)
    
    email = models.EmailField(unique=True, validators=[EmailValidator()])
    rol = models.CharField(max_length=20, choices=ROLES_CHOICES, default='cliente')
    tipo_documento = models.CharField(max_length=50, blank=True, null=True)
    numero_documento = models.CharField(max_length=50, blank=True, null=True)
    telefono = models.CharField(max_length=20, blank=True, null=True)
    barrio = models.CharField(max_length=100, blank=True, null=True)
    direccion = models.TextField(blank=True, null=True)
    ciudad = models.CharField(max_length=100, blank=True, null=True)
    codigo_postal = models.CharField(max_length=20, blank=True, null=True)
    latitud = models.DecimalField(max_digits=9, decimal_places=6, null=True, blank=True)
    longitud = models.DecimalField(max_digits=9, decimal_places=6, null=True, blank=True)
    fecha_creacion = models.DateTimeField(auto_now_add=True)
    fecha_actualizacion = models.DateTimeField(auto_now=True)
    activo = models.BooleanField(default=True)
    
    class Meta:
        verbose_name = 'Usuario'
        verbose_name_plural = 'Usuarios'
        db_table = 'usuarios'
        ordering = ['-fecha_creacion']
    
    def __str__(self):
        return f"{self.username} ({self.get_rol_display()})"

    @property
    def nombre(self):
        return self.first_name

    @property
    def apellido(self):
        return self.last_name
