<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Clientes</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 p-6">

<div class="mb-6 flex justify-between items-center">
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

<h1 class="text-3xl font-bold text-red-600 mb-6">Gestión de Clientes</h1>

{{-- Alertas de éxito --}}
@if(session('success'))
  <div class="bg-green-100 text-green-700 p-3 rounded mb-4">
    {{ session('success') }}
  </div>
@endif

{{-- Errores --}}
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


{{-- Formulario de registro --}}
<form method="POST" action="{{ route('clientes.store') }}" class="bg-white p-4 rounded shadow mb-6">
  @csrf
  <div class="grid grid-cols-2 gap-4">
    <input name="nombre" placeholder="Nombre" class="border p-2 rounded" required>
    <input name="email" placeholder="Correo electrónico" type="email" class="border p-2 rounded">
    <input name="telefono" placeholder="Teléfono" class="border p-2 rounded">
    <input name="direccion" placeholder="Dirección" class="border p-2 rounded">
    <input name="ciudad" placeholder="Ciudad" class="border p-2 rounded">
  </div>
  <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Registrar</button>
</form>


{{-- Formulario de búsqueda --}}
<form method="GET" action="{{ route('clientes.index') }}" class="bg-white p-4 rounded shadow mb-6">
  <div class="grid grid-cols-5 gap-4">
    <input name="nombre" value="{{ request('nombre') }}" placeholder="Nombre" class="border p-2 rounded">
    <input name="email" value="{{ request('email') }}" placeholder="Correo" class="border p-2 rounded">
    <input name="telefono" value="{{ request('telefono') }}" placeholder="Teléfono" class="border p-2 rounded">
    <input name="ciudad" value="{{ request('ciudad') }}" placeholder="Ciudad" class="border p-2 rounded">
    <input type="date" name="fecha_registro" value="{{ request('fecha_registro') }}" class="border p-2 rounded">
  </div>
  <div class="mt-4 flex gap-2">
    <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Filtrar</button>
    <a href="{{ route('clientes.index') }}" class="bg-gray-500 text-white px-4 py-2 rounded">Limpiar</a>
  </div>
</form>



  <a href="{{ route('clientes.reporte') }}" target="_blank"
     class="inline-flex items-center px-4 py-2 bg-red-600 text-white rounded-lg shadow hover:bg-red-700 transition">
    🖨 Imprimir Reporte
  </a>
{{-- Listado --}}
<table class="w-full bg-white shadow rounded">
  <thead>
    <tr class="bg-gray-200 text-left">
      <th class="p-2">ID</th>
      <th class="p-2">Nombre</th>
      <th class="p-2">Email</th>
      <th class="p-2">Teléfono</th>
      <th class="p-2">Acciones</th>
    </tr>
  </thead>
  <tbody>
    @forelse($clientes as $cliente)
      <tr class="border-t">
        <td class="p-2">{{ $cliente->id }}</td>
        <td class="p-2">{{ $cliente->nombre }}</td>
        <td class="p-2">{{ $cliente->email }}</td>
        <td class="p-2">{{ $cliente->telefono }}</td>
        <td class="p-2">
          <a href="{{ route('clientes.edit', $cliente) }}" class="text-blue-500">Editar</a> |
          <a href="{{ route('clientes.show', $cliente) }}" class="text-green-500">Historial</a> |
          <form action="{{ route('clientes.destroy', $cliente) }}" method="POST" class="inline">
            @csrf @method('DELETE')
            <button onclick="return confirm('¿Eliminar cliente?')" class="text-red-500">Eliminar</button>
          </form>
        </td>
      </tr>
    @empty
      <tr>
        <td colspan="5" class="p-4 text-center text-gray-500">No se encontraron clientes</td>
      </tr>
    @endforelse
  </tbody>
</table>

<div class="mt-4">{{ $clientes->links() }}</div>

</body>
</html>
