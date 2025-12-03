<?php

namespace App\Exports;

use App\Models\Cliente;
use Maatwebsite\Excel\Concerns\FromCollection;
use Maatwebsite\Excel\Concerns\WithHeadings;

class ClientesExport implements FromCollection, WithHeadings
{
    public function collection()
    {
        return Cliente::select('id','nombre','email','telefono','ciudad','created_at')->get();
    }

    public function headings(): array
    {
        return ['ID', 'Nombre', 'Email', 'Teléfono', 'Ciudad', 'Fecha Registro'];
    }
}
