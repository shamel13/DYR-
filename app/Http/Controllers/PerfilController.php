<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class PerfilController extends Controller
{
    public function index()
    {
        $user = Auth::user();

        // Si el usuario tiene pedidos
        $pedidos = $user->pedidos()->with('productos')->get();

        return view('perfil.index', compact('user', 'pedidos'));
    }
}
