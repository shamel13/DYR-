<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Pedidos</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

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

<div class="max-w-6xl mx-auto py-10 px-4">
  <h1 class="text-3xl font-bold text-red-600 mb-6">Gestión de Pedidos</h1>


  <!-- Formulario -->
  <form action="{{ route('pedidos.store') }}" method="POST" class="bg-white p-6 rounded shadow mb-8">
    @csrf
    <div class="grid md:grid-cols-2 gap-6">
      <div>
        <label class="block text-sm font-medium">Cliente</label>
        <select name="cliente_id" class="input-field" required>
          <option value="">Seleccione un cliente</option>
          @foreach($clientes as $cliente)
            <option value="{{ $cliente->id }}">{{ $cliente->nombre }}</option>
          @endforeach
        </select>
      </div>
      <div>
        <label class="block text-sm font-medium">Usuario (Vendedor)</label>
        <select name="user_id" class="input-field" required>
          <option value="">Seleccione un usuario</option>
          @foreach($users as $user)
            <option value="{{ $user->id }}">{{ $user->name }}</option>
          @endforeach
        </select>
      </div>
    </div>
    <div class="mt-6">
      <label class="block text-sm font-medium mb-2">Productos</label>
      <div id="productos-container">
        <div class="flex gap-3 mb-2">
          <select name="productos[]" class="input-field w-1/2" required>
            <option value="">Seleccione un producto</option>
            @foreach($productos as $producto)
              <option value="{{ $producto->id }}">{{ $producto->nombre }} - ${{ $producto->precio }}</option>
            @endforeach
          </select>
          <input type="number" name="cantidades[]" min="1" value="1" class="input-field w-1/2" required>
        </div>
      </div>
      <button type="button" onclick="agregarProducto()" class="mt-2 px-3 py-1 bg-green-500 text-white rounded">+ Añadir otro producto</button>
    </div>
    <div class="text-right mt-6">
      <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Guardar Pedido</button>
    </div>
  </form>

  
 {{-- Formulario de búsqueda --}}
<form method="GET" action="{{ route('pedidos.index') }}" class="bg-white p-4 rounded shadow mb-6">
  <div class="grid grid-cols-5 gap-4">
    <select name="cliente_id" class="border p-2 rounded">
      <option value="">Cliente</option>
      @foreach($clientes as $cliente)
        <option value="{{ $cliente->id }}" {{ request('cliente_id') == $cliente->id ? 'selected' : '' }}>
          {{ $cliente->nombre }}
        </option>
      @endforeach
    </select>
    <select name="user_id" class="border p-2 rounded">
      <option value="">Usuario</option>
      @foreach($users as $user)
        <option value="{{ $user->id }}" {{ request('user_id') == $user->id ? 'selected' : '' }}>
          {{ $user->name }}
        </option>
      @endforeach
    </select>
    <input type="number" name="total" value="{{ request('total') }}" placeholder="Total" class="border p-2 rounded">
    
  </div>
  <div class="mt-4 flex gap-2">
    <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Filtrar</button>
    <a href="{{ route('pedidos.index') }}" class="bg-gray-500 text-white px-4 py-2 rounded">Limpiar</a>
    
  </div>
</form>

<!-- Botón para descargar todo el reporte PDF -->
<a href="{{ route('pedidos.reporte') }}" target="_blank"
    class="inline-flex items-center px-4 py-2 bg-red-600 text-white rounded-lg shadow hover:bg-red-700 transition">
    🖨 Imprimir Reporte
</a>

  <h2 class="text-2xl font-bold mb-4">Listado de Pedidos</h2>
  <table class="w-full bg-white rounded shadow">
    <thead>
      <tr class="bg-gray-200">
        <th class="px-4 py-2">ID</th>
        <th class="px-4 py-2">Cliente</th>
        <th class="px-4 py-2">Usuario</th>
        <th class="px-4 py-2">Total</th>
        <th class="px-4 py-2">Acciones</th>
      </tr>
    </thead>
    <tbody>
      @foreach($pedidos as $pedido)
      <tr class="border-b">
        <td class="px-4 py-2">{{ $pedido->id }}</td>
        <td class="px-4 py-2">{{ $pedido->cliente->nombre }}</td>
        <td class="px-4 py-2">{{ $pedido->user->name }}</td>
        <td class="px-4 py-2">${{ $pedido->valor_total }}</td>
        <td class="px-4 py-2 flex gap-3">
          <a href="{{ route('pedidos.show', $pedido->id) }}" class="text-blue-600 hover:underline">Ver</a>
          <a href="{{ route('pedidos.edit', $pedido->id) }}" class="text-yellow-600 hover:underline">Editar</a>
          <form action="{{ route('pedidos.destroy', $pedido->id) }}" method="POST" onsubmit="return confirm('¿Eliminar este pedido?')">
            @csrf
            @method('DELETE')
            <button type="submit" class="text-red-600 hover:underline">Eliminar</button>
          </form>
        </td>
      </tr>
      @endforeach
    </tbody>
  </table>
</div>

<style>
  .input-field {
    margin-top: 0.25rem;
    width: 100%;
    border: 1px solid #d1d5db;
    border-radius: 0.5rem;
    padding: 0.5rem 1rem;
    outline: none;
  }
  .input-field:focus {
    border-color: #fb923c;
    box-shadow: 0 0 0 2px rgba(251, 146, 60, 0.3);
  }
</style>

<script>
  function agregarProducto() {
    const container = document.getElementById('productos-container');
    const div = document.createElement('div');
    div.classList.add('flex', 'gap-3', 'mb-2');
    div.innerHTML = `
      <select name="productos[]" class="input-field w-1/2" required>
        <option value="">Seleccione un producto</option>
        @foreach($productos as $producto)
          <option value="{{ $producto->id }}">{{ $producto->nombre }} - ${{ $producto->precio }}</option>
        @endforeach
      </select>
      <input type="number" name="cantidades[]" min="1" value="1" class="input-field w-1/2" required>
    `;
    container.appendChild(div);
  }
</script>
</body>
</html>
