<?php

namespace App\Http\Controllers\Modulos;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\Inventario;
use App\Models\Producto;
use App\Models\User;
use Barryvdh\DomPDF\Facade\Pdf;

class InventarioController extends Controller
{
    // Método privado para aplicar filtros
   private function aplicarFiltros(Request $request)
{
    $query = Inventario::with('producto');

    if ($request->filled('producto_id')) {
        $query->where('producto_id', $request->producto_id);
    }
    if ($request->filled('stock_min')) {
        $query->where('stock_actual', '>=', $request->stock_min);
    }
    if ($request->filled('stock_max')) {
        $query->where('stock_actual', '<=', $request->stock_max);
    }
    if ($request->filled('fecha_inicio')) {
        $query->whereDate('created_at', '>=', $request->fecha_inicio);
    }
    if ($request->filled('fecha_fin')) {
        $query->whereDate('created_at', '<=', $request->fecha_fin);
    }

    return $query;
}
    public function index(Request $request)
    {
    $query = $this->aplicarFiltros($request);
    $inventarios = $query->paginate(10)->appends($request->except('page'));
    $productos = Producto::all();
    $users = User::all();

    return view('formularios.inventario.index', compact('inventarios', 'productos', 'users'));
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'producto_id'  => 'required|exists:productos,id',
            'stock_actual' => 'required|integer|min:0',
        ], [
            'producto_id.required' => 'Debe seleccionar un producto.',
            'producto_id.exists'   => 'El producto seleccionado no existe.',
            'stock_actual.required'=> 'Debe ingresar el stock.',
            'stock_actual.integer' => 'El stock debe ser un número entero.',
        ]);

        Inventario::create($data);

        return redirect()->route('inventario.index')->with('success', 'Inventario creado correctamente.');
    }

    public function edit(Inventario $inventario)
    {
        $productos = Producto::all();
        $users = User::all();
        return view('formularios.inventario.edit', compact('inventario', 'productos', 'users'));
    }

    public function update(Request $request, Inventario $inventario)
    {
        $data = $request->validate([
            'producto_id'  => 'required|exists:productos,id',
            'stock_actual' => 'required|integer|min:0',
        ]);

        $inventario->update($data);

        return redirect()->route('inventario.index')->with('success', 'Inventario actualizado correctamente.');
    }

    public function destroy(Inventario $inventario)
    {
        $inventario->delete();
        return redirect()->route('inventario.index')->with('success', 'Inventario eliminado correctamente.');
    }

    // Método para exportar reporte en PDF
    public function reporte(Request $request)
    {
        $inventarios = $this->aplicarFiltros($request)->get();

        $pdf = Pdf::loadView('formularios.inventario.inventario_pdf', compact('inventarios'))
                  ->setPaper('A4', 'landscape');

        return $pdf->stream('reporte_inventario.pdf');
    }
}
