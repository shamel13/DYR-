<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Producto;
use App\Models\Pedido;
use Illuminate\Support\Facades\DB;

class CarritoController extends Controller
{
    // Mostrar carrito actual
    public function index()
    {
        $carrito = session()->get('carrito', []);
        return view('carrito.index', compact('carrito'));
    }

    // Agregar producto al carrito
    public function add(Request $request)
    {
        $productoId = $request->input('producto_id');
        $cantidad = $request->input('cantidad', 1);

        $producto = Producto::with('inventario')->findOrFail($productoId);

        if ($producto->inventario->stock_actual < $cantidad) {
            return back()->with('error', 'No hay suficiente stock para este producto.');
        }

        $carrito = session()->get('carrito', []);

        if (isset($carrito[$productoId])) {
            $carrito[$productoId]['cantidad'] += $cantidad;
        } else {
            $carrito[$productoId] = [
                'id' => $producto->id,
                'nombre' => $producto->nombre,
                'precio' => $producto->precio,
                'cantidad' => $cantidad,
            ];
        }

        session()->put('carrito', $carrito);

        return back()->with('success', 'Producto agregado al carrito.');
    }

    // Eliminar un producto del carrito
    public function remove($id)
    {
        $carrito = session()->get('carrito', []);
        if (isset($carrito[$id])) {
            unset($carrito[$id]);
            session()->put('carrito', $carrito);
        }
        return back()->with('success', 'Producto eliminado del carrito.');
    }

    // Vaciar carrito
    public function clear()
    {
        session()->forget('carrito');
        return back()->with('success', 'Carrito vaciado.');
    }

    // Confirmar compra (checkout)
    public function checkout()
    {
        $carrito = session()->get('carrito', []);

        if (empty($carrito)) {
            return back()->with('error', 'El carrito está vacío.');
        }

        DB::transaction(function () use ($carrito) {
            $pedido = Pedido::create([
                'cliente_id' => auth()->check() ? auth()->user()->cliente->id : null,
                'user_id' => auth()->id() ?? 1,
                'nombre_pedido' => 'Pedido desde carrito',
                'valor_total' => 0,
            ]);

            $total = 0;

            foreach ($carrito as $item) {
                $producto = Producto::with('inventario')->findOrFail($item['id']);

                $pedido->productos()->attach($producto->id, [
                    'cantidad' => $item['cantidad'],
                    'precio_unitario' => $producto->precio,
                ]);

                // Actualizamos stock en inventario
                $producto->inventario->decrement('stock_actual', $item['cantidad']);

                $total += $producto->precio * $item['cantidad'];
            }

            $pedido->update(['valor_total' => $total]);

            session()->forget('carrito');
        });

        return redirect()->route('pedidos.index')->with('success', 'Compra realizada con éxito.');
    }
}
