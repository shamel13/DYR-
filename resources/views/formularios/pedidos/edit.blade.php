<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Editar Pedido</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="max-w-4xl mx-auto py-10 px-4">
    <!-- Botón volver -->
    <div class="mb-6">
      <a href="{{ route('pedidos.index') }}" 
         class="inline-flex items-center px-4 py-2 bg-purple-500 text-white rounded-lg shadow hover:bg-purple-600 transition">
        ← Volver al listado
      </a>
    </div>

    <h1 class="text-3xl font-bold text-purple-600 mb-6">Editar Pedido</h1>

    @if($errors->any())
      <div class="mb-4 p-3 bg-red-100 text-red-700 rounded">
        <ul class="list-disc pl-5">
          @foreach($errors->all() as $error)
            <li>{{ $error }}</li>
          @endforeach
        </ul>
      </div>
    @endif

    <form action="{{ route('pedidos.update', $pedido->id) }}" method="POST" class="grid md:grid-cols-2 gap-6 bg-white p-6 rounded shadow">
      @csrf
      @method('PUT')

      <div>
        <label class="block text-sm font-medium">Cliente</label>
        <select name="cliente_id" class="input-field" required>
          @foreach($clientes as $cliente)
            <option value="{{ $cliente->id }}" {{ $pedido->cliente_id == $cliente->id ? 'selected' : '' }}>
              {{ $cliente->nombre }}
            </option>
          @endforeach
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium">Usuario</label>
        <select name="user_id" class="input-field" required>
          @foreach($users as $user)
            <option value="{{ $user->id }}" {{ $pedido->user_id == $user->id ? 'selected' : '' }}>
              {{ $user->name }}
            </option>
          @endforeach
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium">Producto</label>
        <select name="producto_id" class="input-field" required>
          @foreach($productos as $producto)
            <option value="{{ $producto->id }}" {{ $pedido->producto_id == $producto->id ? 'selected' : '' }}>
              {{ $producto->nombre }}
            </option>
          @endforeach
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium">Valor Total</label>
        <input type="number" step="0.01" name="valor_total" value="{{ old('valor_total', $pedido->valor_total) }}" class="input-field" required>
      </div>

      <div class="md:col-span-2 text-right">
        <button type="submit" class="bg-purple-500 hover:bg-purple-600 text-white px-6 py-2 rounded">Actualizar</button>
      </div>
    </form>
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
      border-color: #a855f7;
      box-shadow: 0 0 0 2px rgba(168, 85, 247, 0.3);
    }
  </style>
</body>
</html>
