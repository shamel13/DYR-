<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Pedido extends Model
{
    use HasFactory;

    protected $fillable = [
        'cliente_id',
        'user_id',
        'producto_id',
        'nombre_pedido',
        'valor_total',
        'fecha_registro',
    ];

    public $timestamps = false; // porque tu tabla usa fecha_registro en lugar de created_at/updated_at

    protected $casts = [
        'fecha_registro' => 'datetime',
    ];

    // 🔗 Relación con Cliente
    public function cliente()
    {
        return $this->belongsTo(Cliente::class);
    }

    // 🔗 Relación con Usuario (quien registró el pedido, admin o vendedor)
    public function user()
    {
        return $this->belongsTo(User::class);
    }

    // 🔗 Relación con Producto
    public function productos()
    {
        return $this->belongsToMany(Producto::class, 'pedido_producto')
                    ->withPivot('cantidad', 'precio_unitario')
                    ->withTimestamps();
    }
}

