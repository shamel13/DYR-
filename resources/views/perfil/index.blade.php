<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Perfil de Usuario</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

<div class="max-w-5xl mx-auto py-10 px-6">
  <div class="mb-6">
   <a href="{{ url()->previous() }}"
     class="inline-flex items-center px-4 py-2 bg-gray-500 text-white rounded-lg shadow hover:bg-gray-600 transition">
    <svg xmlns="http://www.w3.org/2000/svg" 
         class="h-5 w-5 mr-2" fill="none" 
         viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
    </svg>
    Volver
  </a>
</div>
  <h1 class="text-3xl font-bold text-red-600 mb-6">Perfil de Usuario</h1>

  <!-- Datos del usuario -->
  <div class="bg-white p-6 rounded shadow mb-8">
    <h2 class="text-xl font-semibold mb-4">Información personal</h2>
    <p><strong>Nombre:</strong> {{ $user->name }}</p>
    <p><strong>Email:</strong> {{ $user->email }}</p>
    <p><strong>Fecha de registro:</strong> {{ $user->created_at->format('d/m/Y') }}</p>

    <!-- Botón de logout -->
    <form method="POST" action="{{ route('logout') }}" class="mt-4">
      @csrf
      <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded">
        Cerrar sesión
      </button>
    </form>
  </div>

  <!-- Historial de pedidos -->
  <div class="bg-white p-6 rounded shadow">
    <h2 class="text-xl font-semibold mb-4">Historial de Pedidos</h2>
    @if($pedidos->count())
      <table class="w-full border-collapse">
        <thead>
          <tr class="bg-gray-200">
            <th class="px-4 py-2">#</th>
            <th class="px-4 py-2">Fecha</th>
            <th class="px-4 py-2">Productos</th>
            <th class="px-4 py-2">Total</th>
          </tr>
        </thead>
        <tbody>
          @foreach($pedidos as $pedido)
          <tr class="border-b">
            <td class="px-4 py-2">{{ $pedido->id }}</td>
            <td class="px-4 py-2">{{ $pedido->fecha_registro->format('d/m/Y H:i') }}</td>
            <td class="px-4 py-2">
              <ul class="list-disc list-inside">
                @foreach($pedido->productos as $producto)
                  <li>{{ $producto->nombre }} (x{{ $producto->pivot->cantidad }})</li>
                @endforeach
              </ul>
            </td>
            <td class="px-4 py-2 font-bold">${{ number_format($pedido->valor_total, 0, ',', '.') }}</td>
          </tr>
          @endforeach
        </tbody>
      </table>
    @else
      <p class="text-gray-600">Aún no has realizado pedidos.</p>
    @endif
  </div>
</div>

</body>
</html>
