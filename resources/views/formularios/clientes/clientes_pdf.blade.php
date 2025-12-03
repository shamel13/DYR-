<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reporte de Clientes</title>
    <style>
        body { font-family: DejaVu Sans, sans-serif; font-size: 12px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #000; padding: 6px; text-align: left; }
        th { background: #f2f2f2; }
        h2 { text-align: center; color: #e3342f; }
    </style>
</head>
<body>
    <h2>Reporte de Clientes</h2>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Email</th>
                <th>Teléfono</th>
                <th>Ciudad</th>
                <th>Fecha Registro</th>
            </tr>
        </thead>
        <tbody>
            @forelse($clientes as $cliente)
                <tr>
                    <td>{{ $cliente->id }}</td>
                    <td>{{ $cliente->nombre }}</td>
                    <td>{{ $cliente->email }}</td>
                    <td>{{ $cliente->telefono }}</td>
                    <td>{{ $cliente->ciudad }}</td>
                    <td>
                        {{ $cliente->created_at ? $cliente->created_at->format('d/m/Y') : now()->format('d/m/Y') }}
                    </td>
                </tr>
            @empty
                <tr>
                    <td colspan="6" style="text-align:center;">No se encontraron clientes</td>
                </tr>
            @endforelse
        </tbody>
    </table>
</body>
</html>
