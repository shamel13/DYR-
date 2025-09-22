<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard - DYR</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="bg-gray-100 text-gray-800">

  <div class="flex min-h-screen">

    <!-- Sidebar -->
    <aside class="w-64 bg-gray-900 text-white flex flex-col">
      <div class="p-6 text-2xl font-bold flex items-center gap-2">
        <i class="fa-solid fa-shirt text-red-500"></i>
        DYR Admin
      </div>
      <nav class="flex-1 px-4 space-y-2">
        <button onclick="mostrarModulos()" class="w-full text-left py-2 px-3 rounded hover:bg-gray-700 transition flex items-center gap-2">
          <i class="fa-solid fa-box"></i> Módulos
        </button>
        <!-- acceso directo al listado de clientes -->
        <a href="{{ route('clientes.index') }}" class="block py-2 px-3 rounded hover:bg-gray-700 transition flex items-center gap-2">
          <i class="fa-solid fa-user"></i> Listado de Clientes
        </a>
        <!-- acceso directo al listado de productos -->
        <a href="{{ route('productos.index') }}" class="block py-2 px-3 rounded hover:bg-gray-700 transition flex items-center gap-2">
          <i class="fa-solid fa-box-open"></i> Listado de Productos
        </a>
        <!-- acceso directo al listado de inventario -->
        <a href="{{ route('inventario.index') }}" class="block py-2 px-3 rounded hover:bg-gray-700 transition flex items-center gap-2">
          <i class="fa-solid fa-warehouse"></i> Listado de Inventario
        </a>
        <!-- acceso directo al listado de pedidos -->
        <a href="{{ route('pedidos.index') }}" class="block py-2 px-3 rounded hover:bg-gray-700 transition flex items-center gap-2">
           <i class="fa-solid fa-truck"></i> Listado de Pedidos
        </a>
        <!-- acceso directo al listado de pedidos -->
        <a href="{{ route('ventas.index') }}" class="block py-2 px-3 rounded hover:bg-gray-700 transition flex items-center gap-2">
           <i class="fa-solid fa-cash-register"></i> Listado de Ventas
        </a>

      </nav>

      <div class="p-4 border-t border-gray-200 text-sm text-gray-600">
        @auth
          <p class="font-semibold">{{ Auth::user()->name }}</p>
          <form method="POST" action="{{ route('logout') }}">
            @csrf
            <button type="submit">Cerrar sesión</button>
          </form>
        @else
          <p class="italic">Usuario invitado</p>
          <a href="{{ route('login') }}" class="text-blue-400 hover:underline">Iniciar sesión</a>
        @endauth
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 p-8 overflow-y-auto">
      @auth
        <h1 class="text-3xl font-bold mb-6">
          Bienvenido al Panel de Administración, {{ Auth::user()->name }}
        </h1>
        <p>Tu rol: {{ Auth::user()->role }}</p>
      @else
        <h1 class="text-3xl font-bold mb-6">Bienvenido al Panel de Administración</h1>
        <p>Por favor <a href="{{ route('login') }}" class="text-blue-500 underline">inicia sesión</a> para acceder a todas las funciones.</p>
      @endauth

      <div id="seccion-modulos" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 hidden">

        <!-- Tarjeta Método de Pago -->
        <a href="{{ route('metodos_pago.index') }}" class="block bg-white p-6 rounded-lg shadow hover:shadow-md transition">
          <div class="flex items-center gap-4">
            <i class="fa-solid fa-credit-card text-2xl text-red-500"></i>
            <div>
              <h2 class="font-semibold text-lg">Métodos de Pago</h2>
              <p class="text-sm text-gray-500">Opciones de pago disponibles</p>
            </div>
          </div>
        </a>


        <!-- Tarjeta Detalle Pedido / Perfil -->
        <a href="{{ route('detalle_pedido.index') }}" class="block bg-white p-6 rounded-lg shadow hover:shadow-md transition">
          <div class="flex items-center gap-4">
            <i class="fa-solid fa-clipboard-list text-2xl text-red-500"></i>
            <div>
              <h2 class="font-semibold text-lg">Detalle Pedido</h2>
              <p class="text-sm text-gray-500">Ver o modificar detalle del pedido</p>
            </div>
          </div>
        </a>

      </div>
    </main>
  </div>

  <script>
    function mostrarModulos() {
      document.getElementById("seccion-modulos").classList.toggle("hidden");
    }
  </script>

</body>
</html>
