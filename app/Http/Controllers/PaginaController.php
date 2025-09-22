<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class PaginaController extends Controller
{
    public function index()
    {
        return view('pagina.index');
    }

    public function welcome()
    {
        return view('pagina.welcome');
    }

    public function dashboard()
    {
        return view('/dashboard/dashboard');
    }

    public function acercade()
    {
        return view('pagina.acercade');
    }

    public function bonos()
    {
        return view('pagina.bonos');
    }

    public function catalogo()
    {
        return view('pagina.catalogo');
    }

    public function compraFormulario()
    {
        return view('pagina.compra-formulario');
    }

    public function formularioCategoria()
    {
        return view('pagina.formulario-categoria');
    }

    public function login()
    {
        return view('Auth.login');
    }

    public function modulos()
    {
        return view('modulos');
    }

    // 🔴 Se eliminó el método productos() porque ahora lo maneja ProductoController

    public function seleccion()
    {
        return view('pagina.seleccion');
    }

    public function ventas()
    {
        return view('pagina.ventas');
    }
}
