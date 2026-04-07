from django.db import models

class Dashboard(models.Model):
    """Modelo para almacenar datos agregados del dashboard"""
    fecha = models.DateField(auto_now_add=True)
    total_ventas = models.DecimalField(max_digits=15, decimal_places=2, default=0)
    total_pedidos = models.IntegerField(default=0)
    total_pagos = models.DecimalField(max_digits=15, decimal_places=2, default=0)
    total_productos = models.IntegerField(default=0)
    clientes_nuevos = models.IntegerField(default=0)
    
    class Meta:
        verbose_name = 'Dashboard'
        verbose_name_plural = 'Dashboards'
        db_table = 'dashboard'
        unique_together = ['fecha']
    
    def __str__(self):
        return f"Dashboard {self.fecha}"

