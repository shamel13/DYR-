<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Editar Producto</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="max-w-4xl mx-auto py-10 px-4">
    <!-- Botón volver -->
    <div class="mb-6">
      <a href="{{ route('productos.index') }}" 
         class="inline-flex items-center px-4 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600 transition">
        ← Volver al listado
      </a>
    </div>

    <h1 class="text-3xl font-bold text-blue-600 mb-6">Editar Producto</h1>

    @if($errors->any())
      <div class="mb-4 p-3 bg-red-100 text-red-700 rounded">
        <ul class="list-disc pl-5">
          @foreach($errors->all() as $error)
            <li>{{ $error }}</li>
          @endforeach
        </ul>
      </div>
    @endif

    <form action="{{ route('productos.update', $producto->id) }}" method="POST" class="grid md:grid-cols-2 gap-6 bg-white p-6 rounded shadow">
      @csrf
      @method('PUT')

      <div>
        <label class="block text-sm font-medium">Nombre</label>
        <input type="text" name="nombre" value="{{ old('nombre', $producto->nombre) }}" class="input-field" required>
      </div>

      <div>
        <label class="block text-sm font-medium">Precio</label>
        <input type="number" step="0.01" name="precio" value="{{ old('precio', $producto->precio) }}" class="input-field" required>
      </div>

      <div>
        <label class="block text-sm font-medium">Cantidad</label>
        <input type="number" name="cantidad" value="{{ old('cantidad', $producto->cantidad) }}" class="input-field" required>
      </div>

      <div class="md:col-span-2">
        <label class="block text-sm font-medium">Descripción</label>
        <textarea name="descripcion" rows="3" class="input-field">{{ old('descripcion', $producto->descripcion) }}</textarea>
      </div>

      <div class="md:col-span-2 text-right">
        <button type="submit" class="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded">Actualizar</button>
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
      border-color: #3b82f6;
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
    }
  </style>
</body>
</html>
