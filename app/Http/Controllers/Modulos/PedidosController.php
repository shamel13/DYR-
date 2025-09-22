<?php

namespace App\Http\Controllers\Modulos;

use App\Http\Controllers\Controller;
use App\Models\Pedido;
use App\Models\User;
use App\Models\Producto;
use App\Models\Cliente;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Barryvdh\DomPDF\Facade\Pdf;

class PedidosController extends Controller
{
    // Método privado para aplicar filtros
    private function aplicarFiltros(Request $request)
    {
        $query = Pedido::with(['cliente', 'user', 'productos']);

        if ($request->filled('cliente_id')) {
            $query->where('cliente_id', $request->cliente_id);
        }
        if ($request->filled('user_id')) {
            $query->where('user_id', $request->user_id);
        }
        if ($request->filled('fecha_inicio')) {
            $query->whereDate('fecha_registro', '>=', $request->fecha_inicio);
        }
        if ($request->filled('fecha_fin')) {
            $query->whereDate('fecha_registro', '<=', $request->fecha_fin);
        }
        if ($request->filled('producto_id')) {
            $query->whereHas('productos', function ($q) use ($request) {
                $q->where('productos.id', $request->producto_id);
            });
        }

        return $query;
    }

    public function index(Request $request)
    {
        $query = $this->aplicarFiltros($request);
        $pedidos = $query->paginate(10)->appends($request->except('page'));
        $users = User::all();
        $productos = Producto::all();
        $clientes = Cliente::all();

        return view('formularios.pedidos.index', compact('pedidos', 'users', 'productos', 'clientes'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'cliente_id' => 'required|exists:clientes,id',
            'user_id'    => 'required|exists:users,id',
            'productos'  => 'required|array',
            'cantidades' => 'required|array',
            'productos.*' => 'exists:productos,id',
            'cantidades.*' => 'integer|min:1',
        ]);

        $pedido = Pedido::create([
            'cliente_id' => $request->cliente_id,
            'user_id'    => $request->user_id,
            'nombre_pedido' => 'Pedido de cliente '.$request->cliente_id,
            'valor_total' => 0,
            'fecha_registro' => now(),
        ]);

        $total = 0;
        foreach ($request->productos as $index => $productoId) {
            $producto = Producto::findOrFail($productoId);
            $cantidad = $request->cantidades[$index];
            $precioUnitario = $producto->precio;

            $pedido->productos()->attach($producto->id, [
                'cantidad' => $cantidad,
                'precio_unitario' => $precioUnitario,
            ]);

            $total += $precioUnitario * $cantidad;
        }

        $pedido->update(['valor_total' => $total]);

        return redirect()->route('pedidos.index')->with('success', 'Pedido registrado correctamente.');
    }

    public function edit(Pedido $pedido)
    {
        $users = User::all();
        $productos = Producto::all();
        $clientes = Cliente::all();

        $pedido->load('productos');

        return view('formularios.pedidos.edit', compact('pedido', 'users', 'productos', 'clientes'));
    }

    public function update(Request $request, Pedido $pedido)
    {
        $request->validate([
            'cliente_id'   => 'required|exists:clientes,id',
            'user_id'      => 'required|exists:users,id',
            'productos'    => 'required|array',
            'productos.*'  => 'exists:productos,id',
            'cantidades'   => 'required|array',
            'cantidades.*' => 'integer|min:1',
        ]);

        DB::transaction(function () use ($request, $pedido) {
            $pedido->update([
                'cliente_id'   => $request->cliente_id,
                'user_id'      => $request->user_id,
            ]);

            $pedido->productos()->detach();
            $total = 0;

            foreach ($request->productos as $index => $producto_id) {
                $producto = Producto::findOrFail($producto_id);
                $cantidad = $request->cantidades[$index];
                $precioUnitario = $producto->precio;

                $pedido->productos()->attach($producto_id, [
                    'cantidad' => $cantidad,
                    'precio_unitario' => $precioUnitario,
                ]);

                $total += $precioUnitario * $cantidad;
            }

            $pedido->update(['valor_total' => $total]);
        });

        return redirect()->route('pedidos.index')->with('success', 'Pedido actualizado correctamente.');
    }

    public function destroy(Pedido $pedido)
    {
        $pedido->productos()->detach();
        $pedido->delete();

        return redirect()->route('pedidos.index')->with('success', 'Pedido eliminado correctamente.');
    }

    public function show(Pedido $pedido)
    {
        $pedido->load(['cliente', 'productos']);
        return view('formularios.pedidos.show', compact('pedido'));
    }

    // Nuevo método para exportar reporte en PDF
    public function reporte(Request $request)
    {
        $pedidos = $this->aplicarFiltros($request)->get();
        $pdf = Pdf::loadView('formularios.pedidos.pedidos_pdf', compact('pedidos'))
                  ->setPaper('A4', 'landscape');

        return $pdf->stream('reporte_pedidos.pdf');
    }
}
