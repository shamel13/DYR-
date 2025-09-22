<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>DYR - Autenticación</title>

  <!-- Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>

  <!-- Font Awesome -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <!-- Montserrat Font -->
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&display=swap" rel="stylesheet">
  <style>
    * {
      font-family: "Montserrat", sans-serif;
    }
  </style>
</head>
<body class="bg-gray-100">

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
  </header>

  <!-- BOTONES DE ALTERNANCIA -->
  <div class="flex justify-center mt-10 space-x-4">
    <button id="btnLogin" onclick="showLogin()" class="px-4 py-2 rounded font-semibold bg-red-500 text-white">Iniciar Sesión</button>
    <button id="btnRegister" onclick="showRegister()" class="px-4 py-2 rounded font-semibold bg-gray-300 text-gray-800">Registrarse</button>
  </div>

  <!-- FORMULARIOS -->
  <div class="flex justify-center items-center mt-6">
    <div class="w-full max-w-md bg-white p-6 rounded shadow">

      <!-- Mensajes -->
      @if ($errors->any())
        <div class="text-red-600 text-sm mb-4">
          <ul class="list-disc pl-5">
            @foreach ($errors->all() as $error)
              <li>{{ $error }}</li>
            @endforeach
          </ul>
        </div>
      @endif

      @if (session('success'))
        <div class="text-green-600 text-sm mb-4">
          {{ session('success') }}
        </div>
      @endif

      <!-- LOGIN -->
      <form method="POST" action="{{ route('login.post') }}" id="loginForm">
        @csrf
        <h2 class="text-xl font-semibold mb-4 text-center">Iniciar Sesión</h2>
        <input type="email" name="email" placeholder="Correo" required class="w-full mb-3 px-3 py-2 border rounded focus:ring-2 focus:ring-red-500">
        <input type="password" name="password" placeholder="Contraseña" required class="w-full mb-3 px-3 py-2 border rounded focus:ring-2 focus:ring-red-500">
        <button type="submit" class="w-full bg-red-500 text-white py-2 rounded hover:bg-red-600 transition">Ingresar</button>
      </form>

      <!-- REGISTRO -->
      <form method="POST" action="{{ route('register.post') }}" id="registerForm" class="hidden">
        @csrf
        <h2 class="text-xl font-semibold mb-4 text-center">Registrarse</h2>
        <input type="text" name="name" placeholder="Nombre" required class="w-full mb-3 px-3 py-2 border rounded focus:ring-2 focus:ring-red-500">
        <input type="email" name="email" placeholder="Correo" required class="w-full mb-3 px-3 py-2 border rounded focus:ring-2 focus:ring-red-500">
        <input type="password" name="password" placeholder="Contraseña" required class="w-full mb-3 px-3 py-2 border rounded focus:ring-2 focus:ring-red-500">
        <input type="password" name="password_confirmation" placeholder="Confirmar Contraseña" required class="w-full mb-3 px-3 py-2 border rounded focus:ring-2 focus:ring-red-500">
        <button type="submit" class="w-full bg-red-500 text-white py-2 rounded hover:bg-red-600 transition">Registrarse</button>
      </form>

    </div>
  </div>

  <!-- SCRIPT -->
  <script>
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const btnLogin = document.getElementById('btnLogin');
    const btnRegister = document.getElementById('btnRegister');

    function showLogin() {
      loginForm.classList.remove('hidden');
      registerForm.classList.add('hidden');
      btnLogin.classList.replace('bg-gray-300', 'bg-red-500');
      btnLogin.classList.replace('text-gray-800', 'text-white');
      btnRegister.classList.replace('bg-red-500', 'bg-gray-300');
      btnRegister.classList.replace('text-white', 'text-gray-800');
    }

    function showRegister() {
      registerForm.classList.remove('hidden');
      loginForm.classList.add('hidden');
      btnRegister.classList.replace('bg-gray-300', 'bg-red-500');
      btnRegister.classList.replace('text-gray-800', 'text-white');
      btnLogin.classList.replace('bg-red-500', 'bg-gray-300');
      btnLogin.classList.replace('text-white', 'text-gray-800');
    }
  </script>

</body>
</html>
