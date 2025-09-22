<?php

namespace App\Http\Controllers\Auth;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Http\Controllers\Controller;


class LoginController extends Controller
{
    // Mostrar formulario de login
    public function showLoginForm()
    {
        return view('pagina.login');
    }

    // Procesar login
    public function login(Request $request)
    {
        // Validación de campos
        $request->validate([
            'email' => 'required|email',
            'password' => 'required'
        ]);

        // Credenciales
        $credentials = $request->only('email', 'password');

        // Intentar login
        if (Auth::attempt($credentials)) {
            $user = Auth::user();

            // Redirigir según el rol
            if ($user->role === 'admin') {
                return redirect()->route('dashboard')
                                 ->with('success', 'Bienvenido Administrador');
            } else {
                return redirect()->route('home')
                                 ->with('success', 'Bienvenido Usuario');
            }
        }

        // Si falla el login
        return back()->withErrors([
            'email' => 'Las credenciales no son correctas.',
        ]);
    }

    // Cerrar sesión
    public function logout(Request $request)
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect()->route('login')->with('success', 'Sesión cerrada');
    }
}
