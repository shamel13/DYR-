<?php

namespace App\Http\Controllers\Modulos;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\Producto;
use Barryvdh\DomPDF\Facade\Pdf;

class ProductoController extends Controller
{
    /**
     * Mostrar listado de productos con filtros
     */
    public function index(Request $request)
    {
        $query = Producto::query();

        // Filtros de búsqueda
        if ($request->filled('nombre')) {
            $query->where('nombre', 'like', '%' . $request->nombre . '%');
        }
        if ($request->filled('precio_min')) {
            $query->where('precio', '>=', $request->precio_min);
        }
        if ($request->filled('precio_max')) {
            $query->where('precio', '<=', $request->precio_max);
        }

        $productos = $query->paginate(10)->appends($request->except('page'));

        return view('formularios.productos.listado_productos', compact('productos'));
    }

    /**
     * Mostrar formulario de creación
     */
    public function create()
    {
        return view('formularios.productos.create');
    }

    /**
     * Guardar un nuevo producto
     */
    public function store(Request $request)
    {
        $data = $request->validate([
            'nombre'      => 'required|string|max:255',
            'precio'      => 'required|numeric|min:0',
            'cantidad'    => 'required|integer|min:0',
            'descripcion' => 'nullable|string|max:1000',
            'imagen'      => 'nullable|image|max:2048',
        ], [
            'nombre.required'   => 'El nombre del producto es obligatorio.',
            'precio.required'   => 'El precio es obligatorio.',
            'precio.numeric'    => 'El precio debe ser un número válido.',
            'cantidad.required' => 'La cantidad es obligatoria.',
            'cantidad.integer'  => 'La cantidad debe ser un número entero.',
            'imagen.image'      => 'El archivo debe ser una imagen.',
            'imagen.max'        => 'La imagen no debe pesar más de 2MB.',
        ]);

        if ($request->hasFile('imagen')) {
            $data['imagen'] = $request->file('imagen')->store('productos', 'public');
        }

        Producto::create($data);

        return redirect()->route('productos.index')
            ->with('success', 'Producto creado correctamente.');
    }

    /**
     * Mostrar formulario de edición
     */
    public function edit(Producto $producto)
    {
        return view('formularios.productos.edit', compact('producto'));
    }

    /**
     * Actualizar un producto
     */
    public function update(Request $request, Producto $producto)
    {
        $data = $request->validate([
            'nombre'      => 'required|string|max:255',
            'precio'      => 'required|numeric|min:0',
            'cantidad'    => 'required|integer|min:0',
            'descripcion' => 'nullable|string|max:1000',
            'imagen'      => 'nullable|image|max:2048',
        ]);

        if ($request->hasFile('imagen')) {
            $data['imagen'] = $request->file('imagen')->store('productos', 'public');
        }

        $producto->update($data);

        return redirect()->route('productos.index')
            ->with('success', 'Producto actualizado correctamente.');
    }

    /**
     * Eliminar un producto
     */
    public function destroy(Producto $producto)
    {
        $producto->delete();

        return redirect()->route('productos.index')
            ->with('success', 'Producto eliminado correctamente.');
    }


    
public function reporte(Request $request)
{
    $query = Producto::query();

    if ($request->filled('nombre')) {
        $query->where('nombre', 'like', '%' . $request->nombre . '%');
    }
    if ($request->filled('precio_min')) {
        $query->where('precio', '>=', $request->precio_min);
    }
    if ($request->filled('precio_max')) {
        $query->where('precio', '<=', $request->precio_max);
    }

    $productos = $query->get();

    $pdf = Pdf::loadView('formularios.productos.reporte_pdf', compact('productos'))
              ->setPaper('A4', 'landscape');

    return $pdf->stream('reporte_productos.pdf');
}
}
