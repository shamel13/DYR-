<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class AdminUserSeeder extends Seeder
{
    public function run()
    {
        $email = 'admin@dyr.com';

        if (!User::where('email', $email)->exists()) {
            User::create([
                'name'     => 'Administrador',
                'email'    => $email,
                'password' => Hash::make('admin123'),
                'role'     => 'admin',
            ]);
        }
    }
}
