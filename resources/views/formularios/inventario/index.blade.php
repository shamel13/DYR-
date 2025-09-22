<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Inventario</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 p-6">
  
  <div class="mb-6">
  <a href="{{ route('dashboard') }}" 
     class="inline-flex items-center px-4 py-2 bg-gray-500 text-white rounded-lg shadow hover:bg-gray-600 transition">
    <svg xmlns="http://www.w3.org/2000/svg" 
         class="h-5 w-5 mr-2" fill="none" 
         viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
    </svg>
    Volver
  </a>
</div>

  <h1 class="text-2xl font-bold mb-4">Gestión de Inventario</h1>

  @if(session('success'))
    <div class="bg-green-100 text-green-700 p-3 rounded mb-4">
      {{ session('success') }}
    </div>
  @endif

  @if($errors->any())
    <div class="bg-red-100 text-red-700 p-3 rounded mb-4">
      <strong>Se encontraron errores en el formulario:</strong>
      <ul class="list-disc pl-5">
        @foreach($errors->all() as $error)
          <li>{{ $error }}</li>
        @endforeach
      </ul>
    </div>
  @endif

  {{-- Formulario --}}
  <form method="POST" action="{{ route('inventario.store') }}" class="bg-white p-4 rounded shadow mb-6">
    @csrf
    <div class="grid grid-cols-2 gap-4">
      <select name="producto_id" class="border p-2 rounded" required>
        <option value="">Seleccione producto</option>
        @foreach($productos as $producto)
          <option value="{{ $producto->id }}">{{ $producto->nombre }}</option>
        @endforeach
      </select>
      <input type="number" name="stock_actual" class="border p-2 rounded" min="0" placeholder="Cantidad en stock" required>
    </div>
    <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Registrar</button>
  </form>
<form method="GET" action="{{ route('inventario.index') }}" class="bg-white p-4 rounded shadow mb-6">
  <div class="grid grid-cols-5 gap-4">
    <select name="producto_id" class="border p-2 rounded">
      <option value="">Producto</option>
      @foreach($productos as $producto)
        <option value="{{ $producto->id }}" {{ request('producto_id') == $producto->id ? 'selected' : '' }}>
          {{ $producto->nombre }}
        </option>
      @endforeach
    </select>
    <input type="number" name="stock_min" value="{{ request('stock_min') }}" placeholder="Stock mínimo" class="border p-2 rounded">
    <input type="number" name="stock_max" value="{{ request('stock_max') }}" placeholder="Stock máximo" class="border p-2 rounded">
 
  </div>
  <div class="mt-4 flex gap-2">
    <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Filtrar</button>
    <a href="{{ route('inventario.index') }}" class="bg-gray-500 text-white px-4 py-2 rounded">Limpiar</a>
  </div>
</form>
<a href="{{ route('inventario.reporte', request()->query()) }}" target="_blank"
       class="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition">
      🖨 Exportar PDF
    </a>
  {{-- Listado --}}
  <table class="w-full bg-white shadow rounded">
    <thead>
      <tr class="bg-gray-200 text-left">
        <th class="p-2">ID</th>
        <th class="p-2">Producto</th>
        <th class="p-2">Stock</th>
        <th class="p-2">Acciones</th>
      </tr>
    </thead>
    <tbody>
      @foreach($inventarios as $inv)
        <tr class="border-t">
          <td class="p-2">{{ $inv->id }}</td>
          <td class="p-2">{{ $inv->producto->nombre ?? '-' }}</td>
          <td class="p-2">{{ $inv->stock_actual }}</td>
          <td class="p-2">
            <a href="{{ route('inventario.edit', $inv) }}" class="text-blue-500">Editar</a> |
            <form action="{{ route('inventario.destroy', $inv) }}" method="POST" class="inline">
              @csrf @method('DELETE')
              <button onclick="return confirm('¿Eliminar registro?')" class="text-red-500">Eliminar</button>
            </form>
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>

  <div class="mt-4">{{ $inventarios->links() }}</div>

</body>
</html>
