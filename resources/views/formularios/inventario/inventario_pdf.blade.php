<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reporte de Inventario</title>
    <style>
        body { font-family: DejaVu Sans, sans-serif; font-size: 12px; }
        h2 { text-align: center; margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { border: 1px solid #000; padding: 6px; text-align: center; }
        th { background-color: #f4f4f4; }
    </style>
</head>
<body>
    <h2>Reporte de Inventario</h2>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Producto</th>
                <th>Cantidad</th>
                <th>Fecha</th>
            </tr>
        </thead>
        <tbody>
            @foreach ($inventarios as $inv)
                <tr>
                    <td>{{ $inv->id }}</td>
                    <td>{{ $inv->producto->nombre ?? '-' }}</td>
                    <td>{{ $inv->stock_actual }}</td>
                    <td>{{ $inv->created_at ? $inv->created_at->format('Y-m-d') : '-' }}</td>
                </tr>
            @endforeach
        </tbody>
    </table>
</body>
</html>