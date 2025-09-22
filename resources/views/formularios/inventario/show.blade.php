<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Detalle del Inventario</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="max-w-4xl mx-auto py-10 px-4">
    <div class="mb-6">
      <a href="{{ route('inventario.index') }}" class="px-4 py-2 bg-yellow-500 text-white rounded-lg shadow hover:bg-yellow-600 transition">
        ← Volver al listado
      </a>
    </div>

    <h1 class="text-3xl font-bold text-yellow-600 mb-6">Detalle de Inventario</h1>

    <div class="bg-white shadow rounded p-6 space-y-4">
      <p><strong>Producto:</strong> {{ $inventario->producto->nombre ?? 'N/A' }}</p>
      <p><strong>Stock Actual:</strong> {{ $inventario->stock_actual }}</p>
      <p><strong>Última Actualización:</strong> {{ $inventario->updated_at?->format('d/m/Y H:i') ?? 'N/A' }}</p>
    </div>

    <h2 class="text-2xl font-semibold text-gray-700 mt-8 mb-4">Movimientos de Inventario</h2>
    <div class="bg-white shadow rounded p-6">
      @if(isset($movimientos) && $movimientos->isNotEmpty())
        <ul class="divide-y divide-gray-200">
          @foreach($movimientos as $mov)
            <li class="py-2 flex justify-between">
              <span>{{ ucfirst($mov->tipo) }} - {{ $mov->descripcion }}</span>
              <span class="text-gray-700">Cantidad: {{ $mov->cantidad }}</span>
              <span class="text-gray-500">{{ $mov->created_at->format('d/m/Y H:i') }}</span>
            </li>
          @endforeach
        </ul>
      @else
        <p class="text-gray-500">No hay movimientos registrados para este inventario.</p>
      @endif
    </div>
  </div>
</body>
</html>
