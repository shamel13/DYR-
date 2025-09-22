<?php
namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Pedido; // O el modelo que uses para pedidos
use Illuminate\Support\Facades\Auth;

class CompraController extends Controller
{
    public function procesar(Request $request)
    {
        // Validar datos
        $request->validate([
            'nombre' => 'required|string|max:255',
            'direccion' => 'required|string|max:255',
            'telefono' => 'required|string|max:20',
            'email' => 'required|email|max:255',
            // Puedes agregar más validaciones
        ]);

        // Guardar el pedido
        $pedido = new Pedido();
        $pedido->user_id = Auth::id();
        $pedido->nombre = $request->nombre;
        $pedido->direccion = $request->direccion;
        $pedido->telefono = $request->telefono;
        $pedido->email = $request->email;
        // Aquí puedes guardar más datos, como los productos del carrito
        $pedido->save();

        // Redirigir con mensaje de éxito
        return redirect()->route('perfil.index')->with('success', '¡Pedido realizado y guardado en tu perfil!');
    }
}