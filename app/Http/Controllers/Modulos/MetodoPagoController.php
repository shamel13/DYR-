<?php

namespace App\Http\Controllers\Modulos;
use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

class MetodoPagoController extends Controller
{
    public function index()
    {
        return view('formularios.formulario_metodos_de_pago');
    }

    public function store(Request $request)
    {
        return back()->with('success', 'Método de pago agregado: ' . $request->nombre_metodo);
    }
}
