<?php

namespace App\Http\Controllers\Modulos;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\Venta;
use App\Models\Pedido;
use App\Models\Cliente;
use App\Models\User;
use Barryvdh\DomPDF\Facade\Pdf;

class VentasController extends Controller
{
    // Método privado para aplicar filtros
    private function aplicarFiltros(Request $request)
    {
        $query = Venta::with(['pedido.cliente', 'pedido.user']);

        if ($request->filled('cliente_id')) {
            $query->whereHas('pedido.cliente', function ($q) use ($request) {
                $q->where('id', $request->cliente_id);
            });
        }
        if ($request->filled('user_id')) {
            $query->whereHas('pedido.user', function ($q) use ($request) {
                $q->where('id', $request->user_id);
            });
        }
        if ($request->filled('total')) {
            $query->whereHas('pedido', function ($q) use ($request) {
                $q->where('valor_total', $request->total);
            });
        }
        if ($request->filled('fecha_inicio')) {
            $query->whereDate('fecha_venta', '>=', $request->fecha_inicio);
        }
        if ($request->filled('fecha_fin')) {
            $query->whereDate('fecha_venta', '<=', $request->fecha_fin);
        }

        return $query;
    }

    public function index(Request $request)
    {
        $query = $this->aplicarFiltros($request);
        $ventas = $query->paginate(10)->appends($request->except('page'));
        $pedidos = Pedido::with(['cliente', 'productos', 'user'])->get();
        $clientes = Cliente::all();
        $users = User::all();

        return view('formularios.ventas.index', compact('ventas', 'pedidos', 'clientes', 'users'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'pedido_id'   => 'required|exists:pedidos,id',
            'fecha_venta' => 'required|date',
        ], [
            'pedido_id.required' => 'Debe seleccionar un pedido.',
            'pedido_id.exists'   => 'El pedido seleccionado no existe.',
            'fecha_venta.required' => 'Debe ingresar una fecha de venta.',
            'fecha_venta.date'     => 'La fecha de venta no tiene un formato válido.',
        ]);

        Venta::create([
            'pedido_id'   => $request->pedido_id,
            'fecha_venta' => $request->fecha_venta,
        ]);

        return redirect()->route('ventas.index')->with('success', 'Venta registrada correctamente.');
    }

    public function edit(Venta $venta)
    {
        $pedidos = Pedido::with(['cliente', 'productos'])->get();
        $clientes = Cliente::all();
        $users = User::all();
        return view('formularios.ventas.edit', compact('venta', 'pedidos', 'clientes', 'users'));
    }

    public function update(Request $request, Venta $venta)
    {
        $request->validate([
            'pedido_id'   => 'required|exists:pedidos,id',
            'fecha_venta' => 'required|date',
        ]);

        $venta->update([
            'pedido_id'   => $request->pedido_id,
            'fecha_venta' => $request->fecha_venta,
        ]);

        return redirect()->route('ventas.index')->with('success', 'Venta actualizada correctamente.');
    }

    public function destroy(Venta $venta)
    {
        $venta->delete();
        return redirect()->route('ventas.index')->with('success', 'Venta eliminada correctamente.');
    }

    // Método para exportar reporte en PDF
    public function reporte(Request $request)
    {
        $ventas = $this->aplicarFiltros($request)->get();

        $pdf = Pdf::loadView('formularios.ventas.ventas_pdf', compact('ventas'))
                  ->setPaper('A4', 'landscape');

        return $pdf->stream('reporte_ventas.pdf');
    }
}
