<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Venta extends Model
{
    use HasFactory;

    protected $fillable = ['pedido_id', 'fecha_venta'];

    // Relación con pedido
    public function pedido()
    {
        return $this->belongsTo(Pedido::class);
    }
}
