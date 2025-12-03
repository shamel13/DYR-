<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Producto extends Model
{
    use HasFactory;

    protected $fillable = ['nombre', 'descripcion', 'precio', 'cantidad', 'imagen'];

    // Relación con pedidos (N:M)
    public function pedidos()
    {
        return $this->belongsToMany(Pedido::class, 'pedido_producto')
                    ->withPivot('cantidad', 'precio_unitario')
                    ->withTimestamps();
    }

    // Relación con inventario
    public function inventario()
    {
        return $this->hasOne(Inventario::class);
    }
}
