<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Editar Inventario</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="max-w-4xl mx-auto py-10 px-4">
    <div class="mb-6">
      <a href="{{ route('inventario.index') }}" 
         class="inline-flex items-center px-4 py-2 bg-green-500 text-white rounded-lg shadow hover:bg-green-600 transition">
        ← Volver al listado
      </a>
    </div>

    <h1 class="text-3xl font-bold text-green-600 mb-6">Editar Inventario</h1>

    @if($errors->any())
      <div class="mb-4 p-3 bg-red-100 text-red-700 rounded">
        <ul class="list-disc pl-5">
          @foreach($errors->all() as $error)
            <li>{{ $error }}</li>
          @endforeach
        </ul>
      </div>
    @endif

    <form action="{{ route('inventario.update', $inventario->id) }}" method="POST" class="grid md:grid-cols-2 gap-6 bg-white p-6 rounded shadow">
      @csrf
      @method('PUT')

      <div>
        <label class="block text-sm font-medium">Producto</label>
        <select name="producto_id" class="input-field" required>
          @foreach($productos as $producto)
            <option value="{{ $producto->id }}" {{ $inventario->producto_id == $producto->id ? 'selected' : '' }}>
              {{ $producto->nombre }}
            </option>
          @endforeach
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium">Stock Actual</label>
        <input type="number" name="stock_actual" value="{{ old('stock_actual', $inventario->stock_actual) }}" class="input-field" required>
      </div>

      <div class="md:col-span-2 text-right">
        <button type="submit" class="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded">Actualizar</button>
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
      border-color: #22c55e;
      box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.3);
    }
  </style>
</body>
</html>
