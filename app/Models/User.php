<?php

namespace App\Models;

use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class User extends Authenticatable
{
    use HasFactory, Notifiable;

    protected $fillable = [
        'name', 'email', 'password', 'role',
    ];

    // Un usuario puede ser cliente
    public function cliente()
    {
        return $this->hasOne(Cliente::class);
    }

    // Un usuario puede tener pedidos
    public function pedidos()
    {
        return $this->hasMany(Pedido::class);
    }
}
