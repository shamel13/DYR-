<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Detalle de la Venta</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="max-w-4xl mx-auto py-10 px-4">
    <div class="mb-6">
      <a href="{{ route('ventas.index') }}" class="px-4 py-2 bg-red-500 text-white rounded-lg shadow hover:bg-red-600 transition">
        ← Volver al listado
      </a>
    </div>

    <h1 class="text-3xl font-bold text-red-600 mb-6">Detalle de la Venta #{{ $venta->id }}</h1>

    <div class="bg-white shadow rounded p-6 space-y-4">
      <p><strong>Pedido asociado:</strong> Pedido #{{ $venta->pedido->id ?? 'N/A' }}</p>
      <p><strong>Cliente:</strong> {{ $venta->pedido->cliente->nombre ?? 'N/A' }}</p>
      <p><strong>Productos:</strong> {{ $venta->pedido->producto->nombre ?? 'N/A' }}</p>
      <p><strong>Fecha de Venta:</strong> {{ \Carbon\Carbon::parse($venta->fecha_venta)->format('d/m/Y') }}</p>
    </div>
  </div>
</body>
</html>
