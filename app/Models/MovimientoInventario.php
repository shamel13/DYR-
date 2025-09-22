<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class MovimientoInventario extends Model
{
    use HasFactory;

    protected $fillable = ['inventario_id', 'tipo', 'cantidad', 'descripcion', 'fecha'];

    public $timestamps = false; // usamos "fecha" en vez de created_at/updated_at

    // Relación con Inventario
    public function inventario()
    {
        return $this->belongsTo(Inventario::class);
    }
}
