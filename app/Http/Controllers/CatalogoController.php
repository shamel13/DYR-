<?php

namespace App\Http\Controllers;

use App\Models\Producto;
use Illuminate\Http\Request;

class CatalogoController extends Controller
{
    public function index(Request $request)
    {
        $query = Producto::with('inventario');

        if ($request->filled('nombre')) {
            $query->where('nombre', 'like', '%' . $request->nombre . '%');
        }
        if ($request->filled('precio_min')) {
            $query->where('precio', '>=', $request->precio_min);
        }
        if ($request->filled('precio_max')) {
            $query->where('precio', '<=', $request->precio_max);
        }
        if ($request->filled('stock')) {
            if ($request->stock == 'disponible') {
                $query->whereHas('inventario', function($q) {
                    $q->where('stock_actual', '>', 0);
                });
            } elseif ($request->stock == 'agotado') {
                $query->whereHas('inventario', function($q) {
                    $q->where('stock_actual', '<=', 0);
                });
            }
        }

        $productos = $query->get();

        return view('pagina.catalogo', compact('productos'));
    }
}