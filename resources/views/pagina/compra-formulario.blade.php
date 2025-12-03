<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Finalizar Compra - DYR</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
</head>
<body class="bg-gray-100 font-sans">

  <!-- NAVBAR -->
  <header class="bg-white shadow-md sticky top-0 z-50">
    <div class="container mx-auto px-4 py-3 flex justify-between items-center">
      <div class="flex items-center text-2xl font-bold text-gray-800">
        <i class="fa-solid fa-shirt text-red-500 mr-2"></i>
        <span>DYR</span>
      </div>
      <nav class="flex-1 flex justify-center gap-10">
        <a href="{{ route('home') }}" class="text-gray-700 hover:text-red-500 transition">Inicio</a>
        <a href="{{ route('acercade') }}" class="text-gray-700 hover:text-red-500 transition">Acerca de</a>
        <a href="{{ route('catalogo') }}" class="text-gray-700 hover:text-red-500 transition">Productos</a>
        <a href="{{ route('login') }}" class="text-gray-700 hover:text-red-500 transition">Iniciar sesión</a>
      </nav>
      <div class="flex gap-4 text-lg text-gray-700">
        <a href="{{ route('seleccion') }}"><i class="fa-solid fa-user hover:text-red-500 transition"></i></a>
        <a href="#"><i class="fa-solid fa-magnifying-glass hover:text-red-500 transition"></i></a>
      </div>
    </div>
  </header>

  <!-- Contenido -->
  <main class="container mx-auto py-12 px-4">
    <h2 class="text-3xl font-bold mb-8 text-center text-red-600 flex items-center justify-center gap-2">
      <i class="fa-solid fa-cart-shopping"></i> Finalizar Compra
    </h2>

    <div id="mensajeExito" class="hidden mb-6 max-w-xl mx-auto bg-green-100 text-green-800 border border-green-400 px-4 py-3 rounded text-center"></div>

    <form id="formularioCompra" class="max-w-xl mx-auto bg-white p-8 rounded-xl shadow-lg space-y-6">
      <div>
        <label class="block mb-1 font-semibold text-gray-700"><i class="fa-solid fa-user mr-1"></i> Nombre Completo</label>
        <input type="text" placeholder="Nombre Completo" required class="w-full border px-4 py-2 rounded focus:outline-none focus:ring-2 focus:ring-red-400 transition" />
      </div>
      <div>
        <label class="block mb-1 font-semibold text-gray-700"><i class="fa-solid fa-location-dot mr-1"></i> Dirección de Envío</label>
        <input type="text" placeholder="Dirección de Envío" required class="w-full border px-4 py-2 rounded focus:outline-none focus:ring-2 focus:ring-red-400 transition" />
      </div>
      <div>
        <label class="block mb-1 font-semibold text-gray-700"><i class="fa-solid fa-phone mr-1"></i> Teléfono</label>
        <input type="text" placeholder="Teléfono" required class="w-full border px-4 py-2 rounded focus:outline-none focus:ring-2 focus:ring-red-400 transition" />
      </div>
      <div>
        <label class="block mb-1 font-semibold text-gray-700"><i class="fa-solid fa-envelope mr-1"></i> Correo Electrónico</label>
        <input type="email" placeholder="Correo Electrónico" required class="w-full border px-4 py-2 rounded focus:outline-none focus:ring-2 focus:ring-red-400 transition" />
      </div>
      <button type="submit" class="w-full bg-red-600 hover:bg-red-700 text-white px-4 py-3 rounded font-bold text-lg shadow transition flex items-center justify-center gap-2">
        <i class="fa-solid fa-paper-plane"></i> Enviar Pedido
      </button>
    </form>

    <div class="mt-8 text-center">
      <a href="{{ route('catalogo') }}" class="inline-flex items-center text-gray-600 hover:text-red-600 transition">
        <i class="fa-solid fa-arrow-left mr-2"></i> Volver al catálogo
      </a>
    </div>
  </main>
<!-- Script de compra y mensaje profesional -->
<script>
  document.getElementById('formularioCompra').addEventListener('submit', function(e) {
    e.preventDefault();

    let orders = parseInt(localStorage.getItem('orders') || '0');
    orders++;
    localStorage.setItem('orders', orders);

    const mensaje = document.getElementById('mensajeExito');
    mensaje.innerHTML = `
      <div class="flex flex-col items-center gap-2">
        <i class="fa-solid fa-circle-check text-green-600 text-3xl"></i>
        <span class="font-semibold">¡Pedido enviado correctamente!</span>
        <span class="text-gray-700">Tu pedido ha sido guardado en tu <a href='{{ route('perfil.index') }}' class='text-red-600 underline hover:text-red-800'>perfil</a>.</span>
      </div>
    `;
    mensaje.classList.remove('hidden');

    e.target.reset();
    setTimeout(() => mensaje.classList.add('hidden'), 4000);
  });
</script>

</body>
</html>
