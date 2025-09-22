<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Cliente</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

    <div class="max-w-3xl mx-auto py-10 px-4">
        <!-- Botón volver -->
        <div class="mb-6">
            <a href="{{ route('clientes.index') }}"
               class="inline-flex items-center px-4 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600 transition">
                ← Volver al listado
            </a>
        </div>

        <h1 class="text-2xl font-bold text-blue-600 mb-6">Editar Cliente</h1>

        <!-- Alerta de validación -->
        @if ($errors->any())
            <div class="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
                <strong>⚠️ No se pudo actualizar la información:</strong>
                <ul class="list-disc pl-5 mt-2">
                    @foreach ($errors->all() as $error)
                        <li>{{ $error }}</li>
                    @endforeach
                </ul>
            </div>
        @endif

        <form action="{{ route('clientes.update', $cliente->id) }}" method="POST" class="bg-white p-6 rounded shadow">
            @csrf
            @method('PUT')

            <div class="mb-4">
                <label class="block text-sm font-medium">Nombre</label>
                <input type="text" name="nombre" value="{{ old('nombre', $cliente->nombre) }}"
                       class="w-full border rounded p-2 focus:ring focus:ring-blue-300" required>
            </div>

            <div class="mb-4">
                <label class="block text-sm font-medium">Correo electrónico</label>
                <input type="email" name="email" value="{{ old('email', $cliente->email) }}"
                       class="w-full border rounded p-2 focus:ring focus:ring-blue-300">
            </div>

            <div class="mb-4">
                <label class="block text-sm font-medium">Teléfono</label>
                <input type="text" name="telefono" value="{{ old('telefono', $cliente->telefono) }}"
                       class="w-full border rounded p-2 focus:ring focus:ring-blue-300">
            </div>

            <div class="mb-4">
                <label class="block text-sm font-medium">Dirección</label>
                <input type="text" name="direccion" value="{{ old('direccion', $cliente->direccion) }}"
                       class="w-full border rounded p-2 focus:ring focus:ring-blue-300">
            </div>

            <div class="mb-4">
                <label class="block text-sm font-medium">Ciudad</label>
                <input type="text" name="ciudad" value="{{ old('ciudad', $cliente->ciudad) }}"
                       class="w-full border rounded p-2 focus:ring focus:ring-blue-300">
            </div>

            <div class="text-right">
                <button type="submit" class="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded">
                    Guardar cambios
                </button>
            </div>
        </form>
    </div>
</body>
</html>
