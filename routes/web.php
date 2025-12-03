<?php

use Illuminate\Support\Facades\Route;

// -------------------------------
// Controladores de autenticación
// -------------------------------
use App\Http\Controllers\Auth\LoginController;
use App\Http\Controllers\Auth\RegisterController;

// -------------------------------
// Controladores de módulos CRUD
// -------------------------------
use App\Http\Controllers\Modulos\InventarioController;
use App\Http\Controllers\Modulos\ClienteController;
use App\Http\Controllers\Modulos\ProductoController;
use App\Http\Controllers\Modulos\VentasController;
use App\Http\Controllers\Modulos\PedidosController;
use App\Http\Controllers\Modulos\PromocionController;
use App\Http\Controllers\Modulos\MetodoPagoController;
use App\Http\Controllers\Modulos\DetallePedidoController;
use App\Http\Controllers\Modulos\TipoProductoController;

// -------------------------------
// Controlador de páginas estáticas
// -------------------------------
use App\Http\Controllers\PaginaController;

// ─── AUTENTICACIÓN ─────────────────────────────────────────────────────────────
Route::prefix('auth')->group(function () {
    Route::get('/login', [LoginController::class, 'showLoginForm'])->name('login');
    Route::post('/login', [LoginController::class, 'login'])->name('login.post');
    Route::post('/logout', [LoginController::class, 'logout'])->name('logout');
    Route::post('/register', [RegisterController::class, 'postRegister'])->name('register.post');
});

// ─── DASHBOARD ─────────────────────────────────────────────────────────────────
Route::get('/admin/dashboard', fn() => view('dashboard.dashboard'))->name('dashboard');
Route::get('/user/dashboard', fn() => 'Casass')->name('home');

// ─── PÁGINAS ESTÁTICAS ─────────────────────────────────────────────────────────
Route::controller(PaginaController::class)->group(function () {
    Route::get('/', 'index')->name('home');
    Route::get('/welcome', 'welcome')->name('welcome');
    Route::get('/acercade', 'acercade')->name('acercade');
    Route::get('/bonos', 'bonos')->name('bonos');
    Route::get('/catalogo', 'catalogo')->name('catalogo');
    Route::get('/compra-formulario', 'compraFormulario')->name('compra.formulario');
    Route::get('/formulario-categoria', 'formularioCategoria')->name('formulario.categoria');
    Route::get('/modulos', 'modulos')->name('modulos');
    Route::get('/seleccion', 'seleccion')->name('seleccion');
    Route::get('/ventas', 'ventas')->name('ventas');
});

// ─── MÓDULOS CRUD ──────────────────────────────────────────────────────────────
Route::prefix('formulario/inventario')->name('inventario.')->group(function () {
    Route::get('/', [InventarioController::class, 'index'])->name('index');
    Route::post('/', [InventarioController::class, 'store'])->name('store');
    Route::get('/{inventario}/edit', [InventarioController::class, 'edit'])->name('edit');
    Route::put('/{inventario}', [InventarioController::class, 'update'])->name('update');
    Route::delete('/{inventario}', [InventarioController::class, 'destroy'])->name('destroy');
});

Route::prefix('formulario/clientes')->name('clientes.')->group(function () {
    Route::get('/', [ClienteController::class, 'index'])->name('index');
    Route::post('/', [ClienteController::class, 'store'])->name('store');
    Route::get('/{cliente}/edit', [ClienteController::class, 'edit'])->name('edit');
    Route::put('/{cliente}', [ClienteController::class, 'update'])->name('update');
    Route::delete('/{cliente}', [ClienteController::class, 'destroy'])->name('destroy');
    Route::get('clientes/{cliente}', [ClienteController::class, 'show'])->name('show');

    // 📌 Rutas de exportación
    Route::get('/export/pdf', [ClienteController::class, 'reporte'])->name('reporte'); // descarga
    Route::get('/export/pdf/view', [ClienteController::class, 'reporteView'])->name('reporte.view'); // imprimir
});

Route::prefix('formulario/productos')->name('productos.')->group(function () {
    Route::get('/', [ProductoController::class, 'index'])->name('index');
    Route::post('/', [ProductoController::class, 'store'])->name('store');
    Route::get('/{producto}/edit', [ProductoController::class, 'edit'])->name('edit');
    Route::put('/{producto}', [ProductoController::class, 'update'])->name('update');
    Route::delete('/{producto}', [ProductoController::class, 'destroy'])->name('destroy');
    Route::get('productos/{producto}', [ProductoController::class, 'show'])->name('show');
});

Route::prefix('formulario/ventas')->name('ventas.')->group(function () {
    Route::get('/', [VentasController::class, 'index'])->name('index');
    Route::post('/', [VentasController::class, 'store'])->name('store');
    Route::get('/{venta}/edit', [VentasController::class, 'edit'])->name('edit');
    Route::put('/{venta}', [VentasController::class, 'update'])->name('update');
    Route::delete('/{venta}', [VentasController::class, 'destroy'])->name('destroy');
});

Route::prefix('formulario/pedidos')->name('pedidos.')->group(function () {
    Route::get('/', [PedidosController::class, 'index'])->name('index');
    Route::post('/', [PedidosController::class, 'store'])->name('store');
    Route::get('/{pedido}/edit', [PedidosController::class, 'edit'])->name('edit');
    Route::put('/{pedido}', [PedidosController::class, 'update'])->name('update');
    Route::delete('/{pedido}', [PedidosController::class, 'destroy'])->name('destroy');
    Route::get('/pedidos/{pedido}', [PedidosController::class, 'show'])->name('show');

    // 📌 Rutas de exportación
    Route::get('/export/pdf', [PedidosController::class, 'exportPdf'])->name('export.pdf');       // descarga
    Route::get('/export/pdf/view', [PedidosController::class, 'exportPdfView'])->name('export.pdf.view'); // vista para imprimir
});

Route::prefix('formulario/promociones')->name('promociones.')->group(function () {
    Route::get('/', [PromocionController::class, 'index'])->name('index');
    Route::post('/', [PromocionController::class, 'store'])->name('store');
});

Route::prefix('formulario/metodos-de-pago')->name('metodos_pago.')->group(function () {
    Route::get('/', [MetodoPagoController::class, 'index'])->name('index');
    Route::post('/', [MetodoPagoController::class, 'store'])->name('store');
});

Route::prefix('formulario/tipo-producto')->name('tipo_producto.')->group(function () {
    Route::get('/', [TipoProductoController::class, 'index'])->name('index');
    Route::post('/', [TipoProductoController::class, 'store'])->name('store');
});

Route::prefix('formulario/detalle-pedido')->name('detalle_pedido.')->group(function () {
    Route::get('/', [DetallePedidoController::class, 'index'])->name('index');
    Route::post('/', [DetallePedidoController::class, 'store'])->name('store');
});

// ─── CARRITO ───────────────────────────────────────────────────────────────────
use App\Http\Controllers\CarritoController;
Route::prefix('carrito')->group(function () {
    Route::get('/', [CarritoController::class, 'index'])->name('carrito.index');
    Route::post('/add', [CarritoController::class, 'add'])->name('carrito.add');
    Route::delete('/remove/{id}', [CarritoController::class, 'remove'])->name('carrito.remove');
    Route::delete('/clear', [CarritoController::class, 'clear'])->name('carrito.clear');
    Route::post('/checkout', [CarritoController::class, 'checkout'])->name('carrito.checkout');
});

// ─── CATÁLOGO ──────────────────────────────────────────────────────────────────
use App\Http\Controllers\CatalogoController;
Route::get('/catalogo', [CatalogoController::class, 'index'])->name('catalogo');

// ─── PERFIL ────────────────────────────────────────────────────────────────────
use App\Http\Controllers\PerfilController;
use Illuminate\Support\Facades\Auth;

Route::get('/perfil', [PerfilController::class, 'index'])
    ->middleware('auth')
    ->name('perfil.index');

// ─── LOGOUT ────────────────────────────────────────────────────────────────────
Route::post('/logout', function (\Illuminate\Http\Request $request) {
    Auth::logout();
    $request->session()->invalidate();
    $request->session()->regenerateToken();
    return redirect()->route('home');
})->name('logout');

Route::get('pedidos/reporte', [PedidosController::class, 'reporte'])->name('pedidos.reporte');
Route::get('ventas/reporte', [VentasController::class, 'reporte'])->name('ventas.reporte');
Route::get('inventario/reporte', [InventarioController::class, 'reporte'])->name('inventario.reporte');
Route::get('productos', [ProductoController::class, 'index'])->name('productos.index');
Route::post('productos', [ProductoController::class, 'store'])->name('productos.store');
Route::get('productos/reporte', [ProductoController::class, 'reporte'])->name('productos.reporte');
Route::get('productos/reporte', [ProductoController::class, 'reporte'])->name('productos.reporte');
