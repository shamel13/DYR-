<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Editar Venta</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="max-w-4xl mx-auto py-10 px-4">
    <!-- Botón volver -->
    <div class="mb-6">
      <a href="{{ route('ventas.index') }}" 
         class="inline-flex items-center px-4 py-2 bg-red-500 text-white rounded-lg shadow hover:bg-red-600 transition">
        ← Volver al listado
      </a>
    </div>

    <h1 class="text-3xl font-bold text-red-600 mb-6">Editar Venta</h1>

    @if($errors->any())
      <div class="mb-4 p-3 bg-red-100 text-red-700 rounded">
        <ul class="list-disc pl-5">
          @foreach($errors->all() as $error)
            <li>{{ $error }}</li>
          @endforeach
        </ul>
      </div>
    @endif

    <form action="{{ route('ventas.update', $venta->id) }}" method="POST" class="grid md:grid-cols-2 gap-6 bg-white p-6 rounded shadow">
      @csrf
      @method('PUT')

      <div class="md:col-span-2">
        <label class="block text-sm font-medium">Pedido asociado</label>
        <select name="pedido_id" class="input-field" required>
          @foreach($pedidos as $pedido)
            <option value="{{ $pedido->id }}" {{ $venta->pedido_id == $pedido->id ? 'selected' : '' }}>
              Pedido #{{ $pedido->id }} - Cliente: {{ $pedido->cliente->nombre ?? 'N/A' }}
            </option>
          @endforeach
        </select>
      </div>

      <div class="md:col-span-2">
        <label class="block text-sm font-medium">Fecha de venta</label>
        <input type="date" name="fecha_venta" value="{{ old('fecha_venta', $venta->fecha_venta) }}" class="input-field" required>
      </div>

      <div class="md:col-span-2 text-right">
        <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Actualizar</button>
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
      border-color: #ef4444;
      box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.3);
    }
  </style>
</body>
</html>
