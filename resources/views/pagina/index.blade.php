<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>DYR - Chaquetas</title>

  <!-- Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>

  <!-- Font Awesome -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">

  <!-- CSS separado -->
  <link rel="stylesheet" href="css/style.css">
</head>
<body class="bg-gray-50 font-[Poppins]">

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
  </header>

  <!-- CARRUSEL -->
  <section class="relative overflow-hidden">
    <div class="carrusel relative w-full h-[80vh] overflow-hidden">
      <div class="slides flex transition-transform duration-700 ease-in-out">

        <!-- SLIDE 1 -->
        <div class="relative w-full h-[80vh] flex-shrink-0">
          <img src="https://images.unsplash.com/photo-1551232864-3f0890e580d9?auto=format&fit=crop&w=1887&q=80"
               class="w-full h-full object-cover absolute inset-0 z-0" />
          <div class="absolute inset-0 bg-black opacity-40 z-10"></div>
          <div class="absolute z-20 text-white left-10 bottom-20 md:left-20 md:bottom-1/4 max-w-md fade-in">
            <h2 class="text-4xl md:text-5xl font-bold mb-4">Colección Otoño/Invierno</h2>
            <p class="text-lg mb-6">Descubre nuestras exclusivas chaquetas de cuero diseñadas para la mujer moderna.</p>
            <button onclick="location.href =  route('catalogo') "; class="bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-full font-medium transition">
              Ver Colección
            </button>
          </div>
        </div>

        <!-- SLIDE 2 -->
        <div class="relative w-full h-[80vh] flex-shrink-0">
          <img src="https://images.unsplash.com/photo-1539533018447-63fcce2678e3?auto=format&fit=crop&w=1887&q=80"
               class="w-full h-full object-cover absolute inset-0 z-0" />
          <div class="absolute inset-0 bg-black opacity-40 z-10"></div>
          <div class="absolute z-20 text-white right-10 bottom-20 md:right-20 md:bottom-1/4 max-w-md text-right fade-in">
            <span class="bg-red-600 text-white px-3 py-1 rounded-full text-sm font-medium mb-3 inline-block">30% OFF</span>
            <h2 class="text-4xl md:text-5xl font-bold mb-4">Oferta Especial</h2>
            <p class="text-lg mb-6">Aprovecha nuestros descuentos en chaquetas seleccionadas. Edición limitada.</p>
            <button class="bg-white hover:bg-gray-200 text-black px-6 py-3 rounded-full font-medium transition">
              Comprar Ahora
            </button>
          </div>
        </div>

        <!-- SLIDE 3 -->
        <div class="relative w-full h-[80vh] flex-shrink-0">
          <img src="https://images.unsplash.com/photo-1551232864-3f0890e580d9?auto=format&fit=crop&w=1887&q=80"
               class="w-full h-full object-cover absolute inset-0 z-0" />
          <div class="absolute inset-0 bg-black opacity-40 z-10"></div>
          <div class="absolute z-20 text-white left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2 text-center fade-in">
            <span class="bg-white text-black px-3 py-1 rounded-full text-sm font-medium mb-3 inline-block">NOVEDAD</span>
            <h2 class="text-4xl md:text-5xl font-bold mb-4">Línea Premium</h2>
            <p class="text-lg mb-6">Cuero italiano de primera calidad. Artesanía excepcional.</p>
            <button class="bg-transparent hover:bg-white hover:text-black border-2 border-white text-white px-6 py-3 rounded-full font-medium transition">
              Descubrir Más
            </button>
          </div>
        </div>

      </div>
    </div>

    <!-- Flechas -->
    <button id="prevBtn" class="absolute left-4 top-1/2 transform -translate-y-1/2 bg-black bg-opacity-50 text-white p-3 rounded-full z-30 hover:bg-opacity-70 transition">
      <i class="fas fa-chevron-left"></i>
    </button>
    <button id="nextBtn" class="absolute right-4 top-1/2 transform -translate-y-1/2 bg-black bg-opacity-50 text-white p-3 rounded-full z-30 hover:bg-opacity-70 transition">
      <i class="fas fa-chevron-right"></i>
    </button>

    <!-- Indicadores -->
    <div class="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex space-x-2 z-30">
      <button class="w-3 h-3 rounded-full bg-white bg-opacity-50 indicator-btn" data-slide="0"></button>
      <button class="w-3 h-3 rounded-full bg-white bg-opacity-50 indicator-btn" data-slide="1"></button>
      <button class="w-3 h-3 rounded-full bg-white bg-opacity-50 indicator-btn" data-slide="2"></button>
    </div>
  </section>

  <!-- JS separado -->
  <script src="js/js1.js"></script>


<!-- CATÁLOGO DESTACADO -->
<section class="py-16 bg-white">
  <div class="container mx-auto px-6">
    <h2 class="text-3xl md:text-4xl font-bold text-center mb-12 text-gray-800">Catálogo Destacado</h2>
    
    <div class="grid gap-8 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3">
      
      <!-- Tarjeta 1 -->
      <div class="bg-white shadow-lg rounded-2xl overflow-hidden transform hover:-translate-y-2 hover:shadow-xl transition duration-300">
        <img src="https://images.unsplash.com/photo-1520975661595-6453be3f7070?auto=format&fit=crop&w=800&q=80" alt="Chaqueta 1" class="w-full h-64 sm:h-48 object-cover">
        <div class="p-5">
          <h3 class="text-xl font-semibold text-gray-800">Chaqueta Elegante</h3>
          <p class="text-red-600 text-lg font-bold mt-2">$180.000</p>
          <button class="mt-4 w-full bg-red-500 hover:bg-red-600 text-white py-2 px-4 rounded-full transition font-medium">
            Agregar al carrito
          </button>
        </div>
      </div>

      <!-- Tarjeta 2 -->
      <div class="bg-white shadow-lg rounded-2xl overflow-hidden transform hover:-translate-y-2 hover:shadow-xl transition duration-300">
       <img id="atras" src="img/chaqueta.webp" alt="atras" class="w-full h-64 sm:h-48 object-cover">
        <div class="p-5">
          <h3 class="text-xl font-semibold text-gray-800">Chaqueta Casual</h3>
          <p class="text-red-600 text-lg font-bold mt-2">$150.000</p>
          <button class="mt-4 w-full bg-red-500 hover:bg-red-600 text-white py-2 px-4 rounded-full transition font-medium">
            Agregar al carrito
          </button>
        </div>
      </div>

      <!-- Tarjeta 3 -->
      <div class="bg-white shadow-lg rounded-2xl overflow-hidden transform hover:-translate-y-2 hover:shadow-xl transition duration-300">
       <img id="atras" src="img/chaqueta.webp" alt="atras" class="w-full h-64 sm:h-48 object-cover">
       
        <div class="p-5">
          
          <h3 class="text-xl font-semibold text-gray-800">Chaqueta Vintage</h3>
          <p class="text-red-600 text-lg font-bold mt-2">$220.000</p>
          <button class="mt-4 w-full bg-red-600 hover:bg-red-700 text-white py-2 px-4 rounded-full transition font-medium">
            Agregar al carrito
          </button>
        </div>
      </div>
    </div>
        </div> <!-- cierre de grid de tarjetas -->

    <!-- Botón Ver Todo -->
    <div class="mt-12 text-center">
      <a href="{{ route('catalogo') }}" class="inline-block bg-black text-white px-8 py-3 rounded-full text-lg font-semibold hover:bg-gray-800 transition">
        Ver todo
      </a>
    </div>
  </div> <!-- cierre container -->
</section>


<!-- INFORMACIÓN DE LA EMPRESA -->
<section class="bg-white py-20">
  <div class="container mx-auto px-6 flex flex-col lg:flex-row items-center gap-12">
    
    <!-- Imagen -->
    <div class="w-full lg:w-1/2">
      <img id="atras" src="img/chaqueta.webp" alt="atras" loading="lazy">
         
    </div>

    <!-- Texto -->
    <div class="w-full lg:w-1/2">
      <h2 class="text-3xl md:text-4xl font-bold text-gray-800 mb-6">Conoce DYR</h2>
      <p class="text-gray-600 text-lg leading-relaxed mb-4">
        En <span class="text-red-500 font-semibold">DYR</span> creemos que cada prenda cuenta una historia.
         Somos una marca colombiana especializada en chaquetas de cuero sintético de alta calidad, diseñadas
         para mujeres con carácter, estilo y autenticidad.
      </p>
      <p class="text-gray-600 text-lg leading-relaxed mb-6">
       Nuestra misión es brindar productos duraderos, elegantes y hechos con pasión.
        Con diseños únicos y materiales sostenibles, acompañamos a nuestras clientas 
        en cada paso, cada temporada y cada desafío.
      </p>
      <a href="{{ route('acercade') }}" class="inline-block bg-red-500 hover:bg-red-600 text-white px-6 py-3 rounded-full font-medium transition">
        Saber más
      </a>
    </div>

  </div>
</section>


 
<!-- FOOTER -->
<footer class="bg-gray-900 text-white py-12">
  <div class="container mx-auto px-6 grid grid-cols-1 md:grid-cols-4 gap-8">
    
    <!-- Logo e Introducción -->
    <div>
      <div class="flex items-center text-2xl font-bold mb-4">
        <i class="fa-solid fa-shirt text-red-500 mr-2"></i>
        <span>DYR</span>
      </div>
      <p class="text-gray-400">
        Chaquetas de cuero sintetico hechas en Colombia con estilo, calidad y carácter.
      </p>
    </div>

    <!-- Enlaces rápidos -->
    <div>
      <h3 class="text-lg font-semibold mb-4 text-white">Enlaces</h3>
      <ul class="space-y-2 text-gray-400">

        <li><a href="{{ route('login') }}" class="hover:text-orange-500 transition">Iniciar sesión</a></li>
        <li><a href="{{ route('catalogo') }}" class="hover:text-orange-500 transition">Productos</a></li>
        <li><a href="{{ route('acercade') }}" class="hover:text-orange-500 transition">Nosotros</a></li>
      </ul>
    </div>

    <!-- Contacto -->
    <div>
      <h3 class="text-lg font-semibold mb-4 text-white">Contáctanos</h3>
      <ul class="space-y-2 text-gray-400">
        <li><i class="fas fa-phone-alt mr-2 text-red-500"></i> +57 301 456 7890</li>
        <li><i class="fas fa-envelope mr-2 text-red-500"></i> contacto@dyr.com</li>
        <li><i class="fas fa-map-marker-alt mr-2 text-red-500"></i> Bogotá, Colombia</li>
      </ul>
    </div>

    <!-- Redes Sociales -->
    <div>
      <h3 class="text-lg font-semibold mb-4 text-white">Síguenos</h3>
      <div class="flex space-x-4 text-xl text-gray-400">
        <a href="#" class="hover:text-red-500 transition"><i class="fab fa-facebook-f"></i></a>
        <a href="#" class="hover:text-red-500 transition"><i class="fab fa-instagram"></i></a>
        <a href="#" class="hover:text-red-500 transition"><i class="fab fa-twitter"></i></a>
        <a href="#" class="hover:text-red-500 transition"><i class="fab fa-youtube"></i></a>
      </div>
    </div>

  </div>

  <!-- Línea divisoria -->
  <div class="border-t border-gray-700 mt-12 pt-6 text-center text-sm text-gray-500">
    © 2025 DYR. Todos los derechos reservados.
  </div>
</footer>


</body>
</html>
