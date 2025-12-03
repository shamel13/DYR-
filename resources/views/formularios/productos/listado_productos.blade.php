<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Productos</title>
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

  <h1 class="text-2xl font-bold mb-4">Gestión de Productos</h1>

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
  <form method="POST" action="{{ route('productos.store') }}" enctype="multipart/form-data" class="bg-white p-4 rounded shadow mb-6">
    @csrf
    <div class="grid grid-cols-2 gap-4">
      <input type="text" name="nombre" placeholder="Nombre del producto" class="border p-2 rounded" required>
      <input type="number" name="precio" placeholder="Precio" step="0.01" class="border p-2 rounded" required>
      <input type="number" name="cantidad" placeholder="Cantidad" min="0" class="border p-2 rounded" required>
      <input type="text" name="descripcion" placeholder="Descripción" class="border p-2 rounded">
      <div class="col-span-2">
        <label class="block mb-1">Imagen</label>
        <input type="file" name="imagen" accept="image/*" class="border p-2 rounded w-full">
      </div>
    </div>
    <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Registrar</button>
  </form>

{{-- Formulario de búsqueda --}}
<form method="GET" action="{{ route('productos.index') }}" class="bg-white p-4 rounded shadow mb-6">
  <div class="grid grid-cols-4 gap-4">
    <input type="text" name="nombre" value="{{ request('nombre') }}" placeholder="Nombre" class="border p-2 rounded">
    <input type="number" name="precio_min" value="{{ request('precio_min') }}" placeholder="Precio mínimo" class="border p-2 rounded">
    <input type="number" name="precio_max" value="{{ request('precio_max') }}" placeholder="Precio máximo" class="border p-2 rounded">
  </div>
  <div class="mt-4 flex gap-2">
    <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Filtrar</button>
    <a href="{{ route('productos.index') }}" class="bg-gray-500 text-white px-4 py-2 rounded">Limpiar</a>
  </div>
</form>

<a href="{{ route('productos.reporte', request()->all()) }}" target="_blank"
  class="inline-flex items-center px-4 py-2 bg-red-600 text-white rounded-lg shadow hover:bg-red-700 transition">
    🖨 Imprimir Reporte
</a>
  {{-- Listado --}}
  <table class="w-full bg-white shadow rounded">
    <thead>
      <tr class="bg-gray-200 text-left">
        <th class="p-2">ID</th>
        <th class="p-2">Nombre</th>
        <th class="p-2">Precio</th>
        <th class="p-2">Cantidad</th>
        <th class="p-2">Imagen</th>
        <th class="p-2">Acciones</th>
      </tr>
    </thead>
    <tbody>
      @foreach($productos as $producto)
        <tr class="border-t">
          <td class="p-2">{{ $producto->id }}</td>
          <td class="p-2">{{ $producto->nombre }}</td>
          <td class="p-2">${{ $producto->precio }}</td>
          <td class="p-2">{{ $producto->cantidad }}</td>
          <td class="p-2">
            @if($producto->imagen)
              <img src="{{ asset('storage/' . $producto->imagen) }}" alt="Imagen de {{ $producto->nombre }}" class="h-16 rounded">
            @else
              Sin imagen
            @endif
          </td>
          <td class="p-2">
            <a href="{{ route('productos.edit', $producto) }}" class="text-blue-500">Editar</a> |
            <form action="{{ route('productos.destroy', $producto) }}" method="POST" class="inline">
              @csrf @method('DELETE')
              <button onclick="return confirm('¿Eliminar producto?')" class="text-red-500">Eliminar</button>
            </form>
          </td>
        </tr>
      @endforeach
    </tbody>
  </table>

  <div class="mt-4">{{ $productos->links() }}</div>

</body>
</html>
