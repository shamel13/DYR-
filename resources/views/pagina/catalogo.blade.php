<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Catálogo - DYR</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"/>
</head>
<body class="font-sans bg-white text-gray-800">

  <!-- Navbar -->
  <header class="bg-white shadow-md sticky top-0 z-50">
    <div class="container mx-auto px-4 py-3 flex justify-between items-center">
      <a href="{{ route('home') }}" class="text-2xl font-bold flex items-center gap-2 text-gray-800">
        <i class="fa-solid fa-shirt text-red-500"></i> DYR
      </a>
      <nav class="flex-1 flex justify-center gap-10">
        <a href="{{ route('home') }}" class="text-gray-700 hover:text-red-500 transition">Inicio</a>
        <a href="{{ route('acercade') }}" class="text-gray-700 hover:text-red-500 transition">Acerca de</a>
        <a href="{{ route('catalogo') }}" class="text-gray-700 hover:text-red-500 transition">Productos</a>
        <a href="{{ route('login') }}" class="text-gray-700 hover:text-red-500 transition">Iniciar sesión</a>
      </nav>
          <div class="space-x-4 flex items-center">
  @auth
    <a href="{{ route('perfil.index') }}" class="flex items-center gap-2 text-gray-700 hover:text-red-500 transition">
      <i class="fa-solid fa-user"></i>
      <span>{{ Auth::user()->name }}</span>
    </a>

    <form method="POST" action="{{ route('logout') }}">
      @csrf
      <button type="submit" class="text-gray-700 hover:text-red-500 transition">
        Cerrar sesión
      </button>
    </form>
  @else
    <a href="{{ route('login') }}" class="text-gray-700 hover:text-red-500 transition">
      <i class="fa-solid fa-user"></i> Iniciar sesión
    </a>
  @endauth

  <a href="#"><i class="fa-solid fa-magnifying-glass hover:text-red-500 transition"></i></a>

  <div class="relative">
    <i class="fas fa-shopping-bag text-xl cursor-pointer hover:text-red-500" onclick="toggleCart()"></i>
    <span id="cart-count" class="absolute -top-2 -right-2 bg-red-600 text-white text-xs w-5 h-5 rounded-full flex items-center justify-center">0</span>
  </div>
</div>

  </header>

  {{-- Filtros de búsqueda --}}
<form method="GET" action="{{ route('catalogo') }}" class="bg-white p-4 rounded shadow mb-8 max-w-4xl mx-auto">
  <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
    <input type="text" name="nombre" value="{{ request('nombre') }}" placeholder="Nombre" class="border p-2 rounded w-full" />
    <input type="number" name="precio_min" value="{{ request('precio_min') }}" placeholder="Precio mínimo" class="border p-2 rounded w-full" min="0" />
    <input type="number" name="precio_max" value="{{ request('precio_max') }}" placeholder="Precio máximo" class="border p-2 rounded w-full" min="0" />
    <select name="stock" class="border p-2 rounded w-full">
      <option value="">Stock</option>
      <option value="disponible" {{ request('stock') == 'disponible' ? 'selected' : '' }}>Disponible</option>
      <option value="agotado" {{ request('stock') == 'agotado' ? 'selected' : '' }}>Agotado</option>
    </select>
  </div>
  <div class="mt-4 flex gap-2 justify-end">
    <button type="submit" class="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded">Filtrar</button>
    <a href="{{ route('catalogo') }}" class="bg-gray-500 text-white px-4 py-2 rounded">Limpiar</a>
  </div>
</form>

  <!-- Productos -->
  <section class="py-12 px-4 max-w-7xl mx-auto grid gap-8 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
    @foreach($productos as $producto)
  <div class="product bg-white shadow rounded-xl overflow-hidden hover:shadow-lg transition">
    @if($producto->imagen)
      <img src="{{ asset('storage/' . $producto->imagen) }}" alt="{{ $producto->nombre }}" class="w-full h-64 object-cover">
      @php $imgUrl = asset('storage/' . $producto->imagen); @endphp
    @else
      <img src="{{ asset('img/default.png') }}" alt="Sin imagen" class="w-full h-64 object-cover">
      @php $imgUrl = asset('img/default.png'); @endphp
    @endif
    <div class="p-4">
      <h3 class="font-semibold text-lg mb-1">{{ $producto->nombre }}</h3>
      <p class="text-orange-600 font-bold mb-2">${{ number_format($producto->precio, 0, ',', '.') }}</p>

      @if($producto->inventario && $producto->inventario->stock_actual > 0)
        <button onclick="addToCart('{{ $producto->nombre }}', {{ $producto->precio }}, '{{ $imgUrl }}')" 
                class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-full text-sm w-full">
          Agregar al carrito
        </button>
      @else
        <span class="text-gray-500 text-sm">Agotado</span>
      @endif
    </div>
  </div>
@endforeach
  </section>

  <!-- Toast Notification -->
  <div id="toast" class="hidden fixed bottom-6 right-6 bg-green-600 text-white py-2 px-4 rounded shadow z-50">
    Producto agregado al carrito
  </div>
<!-- Panel del Carrito Mejorado -->
<div id="cart-panel" class="cart-panel hidden fixed top-0 right-0 w-96 h-full bg-white shadow-xl z-50 flex flex-col">
  <!-- Header -->
  <div class="flex items-center justify-between p-4 border-b bg-red-500 text-white">
    <h2 class="text-xl font-bold">🛒 Tu Carrito</h2>
    <button onclick="toggleCart()" class="hover:text-gray-200">
      <i class="fas fa-times"></i>
    </button>
  </div>

  <!-- Lista de productos -->
  <div id="cart-items" class="flex-1 overflow-y-auto p-4 space-y-4"></div>

  <!-- Total y botones -->
  <div class="p-4 border-t bg-gray-50">
    <div class="flex justify-between items-center mb-4">
      <span class="font-semibold text-lg">Total:</span>
      <span id="cart-total" class="font-bold text-red-600 text-lg">$0</span>
    </div>
    @auth
    <button onclick="confirmarCompra()" 
            class="block text-center bg-green-600 hover:bg-green-700 text-white w-full py-2 rounded font-medium transition">
      Finalizar compra
    </button>
    @endauth

    @guest
    <button onclick="showLoginMessage()" 
            class="block text-center bg-green-600 hover:bg-green-700 text-white w-full py-2 rounded font-medium transition">
      Finalizar compra
    </button>
    @endguest

    <!-- Mensaje flotante para usuarios no autenticados -->
    <div id="login-message" class="hidden fixed bottom-6 right-6 bg-red-600 text-white py-2 px-4 rounded shadow z-50">
      Debes iniciar sesión para finalizar la compra.
    </div>
  </div>
</div>

<script>
let cart = [];

function addToCart(nombre, precio, imagen) {
  const index = cart.findIndex(item => item.nombre === nombre);
  if (index !== -1) {
    cart[index].cantidad += 1;
  } else {
    cart.push({ nombre, precio, imagen, cantidad: 1 });
  }
  updateCart();
  showToast();
}

function updateCart() {
  const cartItems = document.getElementById("cart-items");
  const cartCount = document.getElementById("cart-count");
  const cartTotal = document.getElementById("cart-total");

  cartItems.innerHTML = "";
  let total = 0;

  if (cart.length === 0) {
    cartItems.innerHTML = `<p class="text-gray-500 text-sm">Tu carrito está vacío.</p>`;
  } else {
    cart.forEach((item, idx) => {
      total += item.precio * item.cantidad;
      cartItems.innerHTML += `
        <div class="flex items-center gap-4 bg-white shadow rounded-lg p-3 mb-2">
          <img src="${item.imagen}" alt="${item.nombre}" class="w-16 h-16 object-cover rounded">
          <div class="flex-1">
            <h3 class="font-semibold text-sm">${item.nombre}</h3>
            <p class="text-red-500 font-bold text-sm">$${item.precio.toLocaleString()}</p>
            <div class="flex items-center gap-2 mt-2">
              <button onclick="changeQuantity(${idx}, -1)" class="bg-gray-200 px-2 rounded hover:bg-gray-300 text-lg font-bold">-</button>
              <span class="px-2">${item.cantidad}</span>
              <button onclick="changeQuantity(${idx}, 1)" class="bg-gray-200 px-2 rounded hover:bg-gray-300 text-lg font-bold">+</button>
              <button onclick="removeFromCart(${idx})" class="ml-4 text-red-500 hover:text-red-700" title="Eliminar"><i class="fa fa-trash"></i></button>
            </div>
          </div>
        </div>
      `;
    });
  }

  cartCount.innerText = cart.reduce((sum, item) => sum + item.cantidad, 0);
  cartTotal.innerText = `$${total.toLocaleString()}`;
}

function changeQuantity(index, delta) {
  cart[index].cantidad += delta;
  if (cart[index].cantidad <= 0) {
    cart.splice(index, 1);
  }
  updateCart();
}

function removeFromCart(index) {
  cart.splice(index, 1);
  updateCart();
}

function toggleCart() {
  document.getElementById("cart-panel").classList.toggle("hidden");
}

function showToast() {
  const toast = document.getElementById("toast");
  if (!toast) return;
  toast.classList.remove("hidden");
  setTimeout(() => toast.classList.add("hidden"), 2000);
}

function showLoginMessage() {
  const msg = document.getElementById("login-message");
  msg.classList.remove("hidden");
  setTimeout(() => msg.classList.add("hidden"), 2000);
}

function confirmarCompra() {
  if (confirm('¿Desea continuar con su compra?')) {
    window.location.href = "{{ route('compra.formulario') }}";
  }
}
</script>
</body>
</html>
