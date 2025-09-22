<?php

namespace App\Http\Controllers\Modulos;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\Cliente;
use Barryvdh\DomPDF\Facade\Pdf; // 👈 Importar DomPDF

class ClienteController extends Controller
{
    public function index(Request $request)
    {
        $query = Cliente::query();

        // Filtros
        if ($request->filled('nombre')) {
            $query->where('nombre', 'like', '%' . $request->nombre . '%');
        }
        if ($request->filled('email')) {
            $query->where('email', 'like', '%' . $request->email . '%');
        }
        if ($request->filled('telefono')) {
            $query->where('telefono', 'like', '%' . $request->telefono . '%');
        }
        if ($request->filled('ciudad')) {
            $query->where('ciudad', 'like', '%' . $request->ciudad . '%');
        }
        if ($request->filled('fecha_registro')) {
            $query->whereDate('created_at', $request->fecha_registro);
        }

        $clientes = $query->paginate(10)->appends($request->except('page'));

        return view('formularios.clientes.listado_clientes', compact('clientes'));
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'nombre'   => 'required|string|min:3|max:150',
            'email'    => 'nullable|email|max:150|unique:clientes,email',
            'telefono' => 'nullable|regex:/^[0-9]{7,15}$/',
            'direccion'=> 'nullable|string|max:255',
            'ciudad'   => 'nullable|string|max:100',
        ]);

        $cliente = new Cliente($data);

        if (auth()->check()) {
            $cliente->user_id = auth()->id();
        }

        $cliente->save();

        return redirect()->route('clientes.index')->with('success', 'Cliente registrado correctamente.');
    }

    public function edit(Cliente $cliente)
    {
        return view('formularios.clientes.edit', compact('cliente'));
    }

    public function update(Request $request, Cliente $cliente)
    {
        $data = $request->validate([
            'nombre'   => 'required|string|min:3|max:150',
            'email'    => 'nullable|email|max:150|unique:clientes,email,' . $cliente->id,
            'telefono' => 'nullable|regex:/^[0-9]{7,15}$/',
            'direccion'=> 'nullable|string|max:255',
            'ciudad'   => 'nullable|string|max:100',
        ]);

        $cliente->update($data);

        return redirect()->route('clientes.index')->with('success', 'Cliente actualizado correctamente.');
    }

    public function destroy(Cliente $cliente)
    {
        $cliente->delete();
        return redirect()->route('clientes.index')->with('success', 'Cliente eliminado correctamente.');
    }

    public function show(Cliente $cliente)
    {
        $pedidos = $cliente->pedidos()->with('productos')->get();
        return view('formularios.clientes.show', compact('cliente', 'pedidos'));
    }

 // 📄 Nuevo método para exportar reporte en PDF
public function reporte(Request $request)
{
    $query = Cliente::query();

    // Aplicar filtros
    if ($request->filled('nombre')) {
        $query->where('nombre', 'like', '%' . $request->nombre . '%');
    }
    if ($request->filled('email')) {
        $query->where('email', 'like', '%' . $request->email . '%');
    }
    if ($request->filled('telefono')) {
        $query->where('telefono', 'like', '%' . $request->telefono . '%');
    }
    if ($request->filled('ciudad')) {
        $query->where('ciudad', 'like', '%' . $request->ciudad . '%');
    }
    if ($request->filled('fecha_registro')) {
        $query->whereDate('created_at', $request->fecha_registro);
    }

    $clientes = $query->get();

    // 👉 Cambiamos download() por stream()
    $pdf = Pdf::loadView('formularios.clientes.clientes_pdf', compact('clientes'))
              ->setPaper('A4', 'landscape');

    return $pdf->stream('reporte_clientes.pdf'); 
}


}
