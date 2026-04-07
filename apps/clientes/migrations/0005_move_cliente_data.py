from django.db import migrations
import uuid


def _generate_unique_email(username, existing_emails):
    email = f"{username}@clientes.local"
    while email in existing_emails:
        email = f"{username}_{uuid.uuid4().hex[:4]}@clientes.local"
    existing_emails.add(email)
    return email


def move_cliente_data(apps, schema_editor):
    Cliente = apps.get_model('clientes', 'Cliente')
    Usuario = apps.get_model('usuarios', 'Usuario')
    Pedido = apps.get_model('pedidos', 'Pedido')
    Venta = apps.get_model('ventas', 'Venta')

    existing_emails = set(Usuario.objects.values_list('email', flat=True))
    cliente_to_usuario = {}

    for cliente in Cliente.objects.all():
        usuario = None
        if getattr(cliente, 'usuario_id', None):
            try:
                usuario = Usuario.objects.get(pk=cliente.usuario_id)
            except Usuario.DoesNotExist:
                usuario = None

        if usuario:
            usuario.first_name = cliente.nombre or usuario.first_name
            usuario.last_name = cliente.apellido or usuario.last_name
            if getattr(cliente, 'tipo_documento', None):
                usuario.tipo_documento = cliente.tipo_documento
            if getattr(cliente, 'numero_documento', None):
                usuario.numero_documento = cliente.numero_documento
            if getattr(cliente, 'barrio', None):
                usuario.barrio = cliente.barrio
            if getattr(cliente, 'telefono', None):
                usuario.telefono = cliente.telefono
            if getattr(cliente, 'ciudad', None):
                usuario.ciudad = cliente.ciudad
            if getattr(cliente, 'direccion', None):
                usuario.direccion = cliente.direccion
            if getattr(cliente, 'codigo_postal', None):
                usuario.codigo_postal = cliente.codigo_postal
            if getattr(cliente, 'latitud', None) is not None:
                usuario.latitud = cliente.latitud
            if getattr(cliente, 'longitud', None) is not None:
                usuario.longitud = cliente.longitud
            usuario.rol = 'cliente'
            usuario.activo = cliente.activo
            if not usuario.email:
                usuario.email = _generate_unique_email(usuario.username, existing_emails)
            usuario.save()
        else:
            base_username = cliente.numero_documento or f"cliente_{cliente.pk}"
            username = base_username
            if Usuario.objects.filter(username=username).exists():
                username = f"{base_username}_{cliente.pk}"

            email = getattr(cliente, 'email', '') or _generate_unique_email(username, existing_emails)
            usuario = Usuario.objects.create(
                username=username,
                email=email,
                password='',
                first_name=cliente.nombre or '',
                last_name=cliente.apellido or '',
                rol='cliente',
                tipo_documento=getattr(cliente, 'tipo_documento', '') or '',
                numero_documento=getattr(cliente, 'numero_documento', '') or '',
                barrio=getattr(cliente, 'barrio', '') or '',
                telefono=getattr(cliente, 'telefono', '') or '',
                ciudad=getattr(cliente, 'ciudad', '') or '',
                direccion=getattr(cliente, 'direccion', '') or '',
                codigo_postal=getattr(cliente, 'codigo_postal', '') or '',
                latitud=getattr(cliente, 'latitud', None),
                longitud=getattr(cliente, 'longitud', None),
                activo=cliente.activo,
            )
            usuario.save()

        cliente_to_usuario[cliente.pk] = usuario.pk

    for cliente_id, usuario_id in cliente_to_usuario.items():
        Pedido.objects.filter(cliente_id=cliente_id).update(cliente_id=usuario_id)
        Venta.objects.filter(cliente_id=cliente_id).update(cliente_id=usuario_id)


class Migration(migrations.Migration):

    dependencies = [
        ('clientes', '0004_cliente_barrio_cliente_numero_documento_and_more'),
        ('usuarios', '0003_usuario_barrio_usuario_ciudad_usuario_codigo_postal_and_more'),
        ('pedidos', '0003_alter_pedido_cliente'),
        ('ventas', '0002_alter_venta_cliente'),
    ]

    operations = [
        migrations.RunPython(move_cliente_data, migrations.RunPython.noop),
    ]
