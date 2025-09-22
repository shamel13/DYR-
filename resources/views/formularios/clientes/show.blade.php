<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Detalle del Cliente</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="max-w-4xl mx-auto py-10 px-4">
    <div class="mb-6">
      <a href="{{ route('clientes.index') }}" class="px-4 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600 transition">
        ← Volver al listado
      </a>
    </div>

    <h1 class="text-3xl font-bold text-blue-600 mb-6">Detalle del Cliente: {{ $cliente->nombre }}</h1>

    <div class="bg-white shadow rounded p-6 space-y-4">
      <p><strong>Email:</strong> {{ $cliente->email ?? 'No registrado' }}</p>
      <p><strong>Teléfono:</strong> {{ $cliente->telefono ?? 'No registrado' }}</p>
      <p><strong>Dirección:</strong> {{ $cliente->direccion ?? 'No registrada' }}</p>
      <p><strong>Ciudad:</strong> {{ $cliente->ciudad ?? 'No registrada' }}</p>
      <p><strong>Fecha Registro:</strong> {{ $cliente->fecha_registro->format('d/m/Y H:i') }}</p>
    </div>

    <h2 class="text-2xl font-semibold text-gray-700 mt-8 mb-4">Historial de Pedidos</h2>
    <div class="bg-white shadow rounded p-6">
      @if($pedidos->isEmpty())
        <p class="text-gray-500">Este cliente aún no tiene pedidos.</p>
      @else
        <ul class="divide-y divide-gray-200">
          @foreach($pedidos as $pedido)
            <li class="py-2 flex justify-between">
              <span>Pedido #{{ $pedido->id }} - {{ $pedido->nombre_pedido }}</span>
              <span class="text-purple-600 font-bold">${{ number_format($pedido->valor_total, 2) }}</span>
            </li>
          @endforeach
        </ul>
      @endif
    </div>
  </div>
</body>
</html>
