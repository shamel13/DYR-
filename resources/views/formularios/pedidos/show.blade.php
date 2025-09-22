<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Detalle del Pedido</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="max-w-4xl mx-auto py-10 px-4">
    <div class="mb-6">
      <a href="{{ route('pedidos.index') }}" class="px-4 py-2 bg-purple-500 text-white rounded-lg shadow hover:bg-purple-600 transition">
        ← Volver al listado
      </a>
    </div>

    <h1 class="text-3xl font-bold text-purple-600 mb-6">Detalle del Pedido #{{ $pedido->id }}</h1>

    <div class="bg-white shadow rounded p-6 space-y-4">
      <p><strong>Cliente:</strong> {{ $pedido->cliente->nombre ?? 'N/A' }}</p>
      <p><strong>Usuario:</strong> {{ $pedido->user->name ?? 'N/A' }}</p>
      <p><strong>Producto:</strong> {{ $pedido->producto->nombre ?? 'N/A' }}</p>
      <p><strong>Valor Total:</strong> ${{ number_format($pedido->valor_total, 2) }}</p>
      <p><strong>Fecha Registro:</strong> {{ $pedido->fecha_registro->format('d/m/Y H:i') }}</p>
    </div>
  </div>
</body>
</html>
