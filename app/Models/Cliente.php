<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Cliente extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id', 'nombre', 'email', 'telefono', 'direccion', 'ciudad', 'fecha_registro'
    ];

    public $timestamps = false;

    protected $casts = [
        'fecha_registro' => 'datetime',
    ];

    // Relación con User
    public function user()
    {
        return $this->belongsTo(User::class);
    }

    // Relación con Pedidos
    public function pedidos()
    {
        return $this->hasMany(Pedido::class);
    }
}
