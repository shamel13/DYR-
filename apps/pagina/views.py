from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from django.db.models import Sum, Q
from django.http import HttpResponse
import io
import json
from datetime import datetime
import uuid

from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer

from apps.usuarios.models import Usuario
from apps.clientes.services import GeocodingService
from apps.productos.models import Producto
from apps.inventario.models import Inventario, MovimientoInventario
from apps.pedidos.models import Pedido
from apps.pagos.models import Pago
from apps.ventas.models import Venta


def index(request):
    productos_destacados = Producto.objects.filter(activo=True)[:6]
    return render(request, 'pagina/index.html', {'productos_destacados': productos_destacados})


def catalogo(request):
    productos = Producto.objects.filter(activo=True).select_related('inventario')
    return render(request, 'pagina/catalogo.html', {'productos': productos})


def acerca(request):
    return render(request, 'pagina/acerca.html')


@login_required
def reportes_crud(request, modelo):
    modelo = modelo.lower()
    buffer = io.BytesIO()
    doc = SimpleDocTemplate(buffer, pagesize=A4, leftMargin=30, rightMargin=30, topMargin=30, bottomMargin=30)
    styles = getSampleStyleSheet()
    styles.add(ParagraphStyle(name='Subtitle', fontSize=10, leading=12, textColor=colors.HexColor('#6b7280')))
    styles.add(ParagraphStyle(name='TableHeader', fontSize=10, leading=12, textColor=colors.white, alignment=1, fontName='Helvetica-Bold'))
    styles.add(ParagraphStyle(name='Footer', fontSize=9, leading=11, textColor=colors.HexColor('#6b7280')))

    header_table = Table([
        [
            Paragraph('<para align=center><b><font size=18 color="#ffffff">DYR</font></b></para>', styles['Normal']),
            Paragraph(
                f'<b>Reporte de {modelo.title()}</b><br/><font size=10>Generado el {datetime.now().strftime("%d/%m/%Y %H:%M")}</font>',
                styles['Normal']
            ),
        ]
    ], colWidths=[60, 420])
    header_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor('#dc2626')),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('TEXTCOLOR', (0, 0), (-1, -1), colors.white),
        ('LEFTPADDING', (0, 0), (-1, -1), 12),
        ('RIGHTPADDING', (0, 0), (-1, -1), 12),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 16),
        ('TOPPADDING', (0, 0), (-1, -1), 16),
    ]))

    data = []

    if modelo == 'clientes':
        data.append(['ID', 'Nombre', 'Apellido', 'Email', 'Teléfono', 'Activo', 'Fecha Creación'])
        for cliente in Usuario.objects.filter(rol='cliente').all():
            data.append([
                cliente.id,
                cliente.first_name,
                cliente.last_name,
                cliente.email,
                cliente.telefono,
                'Sí' if getattr(cliente, 'activo', False) else 'No',
                getattr(cliente, 'fecha_creacion', ''),
            ])
    elif modelo == 'productos':
        data.append(['ID', 'Nombre', 'Precio', 'Categoría', 'Activo', 'Fecha Creación'])
        for producto in Producto.objects.all():
            data.append([
                producto.id,
                producto.nombre,
                producto.precio,
                producto.categoria,
                'Sí' if getattr(producto, 'activo', False) else 'No',
                getattr(producto, 'fecha_creacion', ''),
            ])
    elif modelo == 'inventario':
        data.append(['ID', 'Producto', 'Cantidad Total', 'Cantidad Disponible', 'Cantidad Reservada', 'Fecha Actualización'])
        for inventario in Inventario.objects.select_related('producto').all():
            data.append([
                inventario.id,
                inventario.producto.nombre if inventario.producto else '',
                inventario.cantidad_total,
                inventario.cantidad_disponible,
                inventario.cantidad_reservada,
                getattr(inventario, 'fecha_actualizacion', ''),
            ])
    elif modelo == 'pedidos':
        data.append(['ID', 'Número Pedido', 'Cliente', 'Estado', 'Transportista', 'Total', 'Fecha Pedido'])
        for pedido in Pedido.objects.select_related('cliente').all():
            data.append([
                pedido.id,
                pedido.numero_pedido,
                pedido.cliente.nombre if pedido.cliente else '',
                pedido.estado,
                pedido.transportista,
                pedido.total,
                pedido.fecha_pedido,
            ])
    elif modelo == 'pagos':
        data.append(['ID', 'Pedido', 'Método', 'Monto', 'Estado', 'Referencia', 'Fecha Pago'])
        for pago in Pago.objects.select_related('pedido').all():
            data.append([
                pago.id,
                pago.pedido.numero_pedido if pago.pedido else '',
                pago.metodo,
                pago.monto,
                pago.estado,
                pago.referencia_transaccion,
                getattr(pago, 'fecha_pago', ''),
            ])
    elif modelo == 'ventas':
        data.append(['ID', 'Número Venta', 'Cliente', 'Vendedor', 'Total Venta', 'Fecha Venta'])
        for venta in Venta.objects.select_related('cliente', 'vendedor').all():
            data.append([
                venta.id,
                venta.numero_venta,
                venta.cliente.nombre if venta.cliente else '',
                venta.vendedor.username if venta.vendedor else '',
                venta.total_venta,
                venta.fecha_venta,
            ])
    elif modelo == 'usuarios':
        data.append(['ID', 'Username', 'Email', 'Nombre', 'Apellido', 'Rol', 'Activo', 'Fecha Creación'])
        for usuario in Usuario.objects.exclude(is_superuser=True).all():
            data.append([
                usuario.id,
                usuario.username,
                usuario.email,
                usuario.first_name,
                usuario.last_name,
                getattr(usuario, 'rol', ''),
                'Sí' if usuario.is_active else 'No',
                getattr(usuario, 'date_joined', ''),
            ])
    else:
        messages.error(request, 'Reporte no disponible para este módulo.')
        return redirect('/')

    table = Table(data, repeatRows=1)
    table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#dc2626')),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.white),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
        ('FONTSIZE', (0, 0), (-1, 0), 10),
        ('BOTTOMPADDING', (0, 0), (-1, 0), 10),
        ('TOPPADDING', (0, 0), (-1, 0), 10),
        ('BACKGROUND', (0, 1), (-1, -1), colors.white),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor('#fef2f2')]),
        ('GRID', (0, 0), (-1, -1), 0.25, colors.HexColor('#fca5a5')),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ]))

    footer = Paragraph(f'Total de registros: {max(0, len(data) - 1)}', styles['Footer'])
    elements = [header_table, Spacer(1, 16), footer, Spacer(1, 12), table, Spacer(1, 12), footer]
    doc.build(elements)

    response = HttpResponse(content_type='application/pdf')
    response['Content-Disposition'] = f'attachment; filename="reporte_{modelo}.pdf"'
    response.write(buffer.getvalue())
    return response


def login_view(request):
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(request, username=username, password=password)
        if user is not None:
            # Si usuario tiene bandera custom https://docs.djangoproject.com/en/stable/topics/auth/default/#django.contrib.auth.models.AbstractBaseUser.is_active
            if not user.is_active:
                messages.error(request, 'Tu cuenta está desactivada. Contacta con el administrador.')
                return render(request, 'pagina/login.html')

            # Si es cliente, también confirmar estado del perfil de cliente
            if user.rol == 'cliente' and not user.activo:
                messages.error(request, 'Tu cuenta de cliente está desactivada. Contacta con el administrador.')
                return render(request, 'pagina/login.html')

            login(request, user)
            messages.success(request, 'Bienvenido, ' + user.username)
            if user.is_superuser or user.rol == 'admin':
                return redirect('dashboard_home')
            return redirect('index')
        messages.error(request, 'Credenciales incorrectas. Intenta nuevamente.')
    return render(request, 'pagina/login.html')


def register_view(request):
    if request.method == 'POST':
        email = request.POST.get('email')
        password = request.POST.get('password')
        nombre = request.POST.get('name')
        apellido = request.POST.get('lastname', '')
        tipo_documento = request.POST.get('tipo_documento')
        numero_documento = request.POST.get('numero_documento')
        telefono = request.POST.get('telefono')

        if Usuario.objects.filter(email=email).exists():
            messages.error(request, 'El email ya está registrado')
            return redirect('register')

        if not tipo_documento or not numero_documento:
            messages.error(request, 'Tipo y número de documento son obligatorios')
            return redirect('register')

        if not telefono or len(telefono) < 7:
            messages.error(request, 'Teléfono válido es obligatorio')
            return redirect('register')

        if len(nombre) < 3:
            messages.error(request, 'El nombre debe tener al menos 3 caracteres')
            return redirect('register')

        if len(password) < 6 or not (any(c.isalpha() for c in password) and any(c.isdigit() for c in password)):
            messages.error(request, 'La contraseña debe tener al menos 6 caracteres y contener letras y números')
            return redirect('register')

        try:
            usuario = Usuario.objects.create_user(
                username=email,
                email=email,
                password=password,
                first_name=nombre,
                last_name=apellido,
                tipo_documento=tipo_documento,
                numero_documento=numero_documento,
                telefono=telefono,
                rol='cliente',
                activo=True,
            )
            usuario.save()

            messages.success(request, 'Cuenta creada con éxito. Ahora inicia sesión')
            return redirect('login')
        except Exception as e:
            messages.error(request, 'Error al registrar: ' + str(e))
            return redirect('register')

    return render(request, 'pagina/register.html')


@login_required
def profile(request):
    if request.method == 'POST':
        user = request.user
        user.first_name = request.POST.get('first_name', user.first_name)
        user.email = request.POST.get('email', user.email)
        user.telefono = request.POST.get('telefono', user.telefono)
        user.tipo_documento = request.POST.get('tipo_documento', user.tipo_documento)
        user.numero_documento = request.POST.get('numero_documento', user.numero_documento)
        user.direccion = request.POST.get('direccion', user.direccion)
        user.barrio = request.POST.get('barrio', user.barrio)
        user.ciudad = request.POST.get('ciudad', user.ciudad)
        user.codigo_postal = request.POST.get('codigo_postal', user.codigo_postal)
        user.pais = request.POST.get('pais', user.pais)

        new_password = request.POST.get('new_password')
        if new_password:
            user.set_password(new_password)

        try:
            user.save()
            messages.success(request, 'Perfil actualizado correctamente')
        except Exception as e:
            messages.error(request, f'Error al actualizar perfil: {str(e)}')

        return redirect('profile')

    # Obtener pedidos del cliente
    pedidos = Pedido.objects.filter(cliente=request.user).prefetch_related('detalles__producto').select_related('pago')
    
    return render(request, 'pagina/profile.html', {'user': request.user, 'pedidos': pedidos})


@login_required
def dashboard_home(request):
    if not (request.user.is_superuser or request.user.rol == 'admin'):
        messages.warning(request, 'Acceso denegado: solo administradores pueden ver el tablero.')
        return redirect('profile')

    usuarios = Usuario.objects.filter(rol__in=['vendedor', 'cliente', 'almacenista']).exclude(is_superuser=True)
    total_clientes = Usuario.objects.filter(rol='cliente').count()
    total_productos = Producto.objects.count()
    total_pedidos = Pedido.objects.count()
    total_ventas = Venta.objects.count() + Pedido.objects.filter(estado='entregado').count()
    # Sumar montos de pagos completados (ingresos reales)
    ingresos_ventas = Pago.objects.filter(estado='completado').aggregate(total=Sum('monto'))['total'] or 0
    productos_poco_stock = Inventario.objects.filter(cantidad_disponible__lt=5).count()
    
    # Conteos de pedidos por estado
    pedidos_pendiente = Pedido.objects.filter(estado='pendiente').count()
    pedidos_procesando = Pedido.objects.filter(estado='procesando').count()
    pedidos_enviado = Pedido.objects.filter(estado='enviado').count()
    pedidos_entregado = Pedido.objects.filter(estado='entregado').count()
    pedidos_cancelado = Pedido.objects.filter(estado='cancelado').count()

    return render(request, 'dashboard/home.html', {
        'usuarios': usuarios,
        'total_clientes': total_clientes,
        'total_productos': total_productos,
        'total_pedidos': total_pedidos,
        'total_ventas': total_ventas,
        'ingresos_ventas': ingresos_ventas,
        'productos_poco_stock': productos_poco_stock,
        'pedidos_pendiente': pedidos_pendiente,
        'pedidos_procesando': pedidos_procesando,
        'pedidos_enviado': pedidos_enviado,
        'pedidos_entregado': pedidos_entregado,
        'pedidos_cancelado': pedidos_cancelado,
        'currentUserName': request.user.get_full_name() or request.user.username,
        'currentUserEmail': request.user.email,
    })


@login_required
def clientes_index(request):
    query_nombre = request.GET.get('nombre', '')
    query_numero_documento = request.GET.get('numero_documento', '')
    query_telefono = request.GET.get('telefono', '')

    clientes = Usuario.objects.filter(rol='cliente').exclude(is_superuser=True)
    if query_nombre:
        clientes = clientes.filter(first_name__icontains=query_nombre)
    if query_numero_documento:
        clientes = clientes.filter(numero_documento__icontains=query_numero_documento)
    if query_telefono:
        clientes = clientes.filter(telefono__icontains=query_telefono)

    coords_json = {}
    for cliente in clientes:
        if cliente.latitud and cliente.longitud:
            coords_json[f'cliente_{cliente.id}'] = {
                'lat': float(cliente.latitud),
                'lon': float(cliente.longitud)
            }

    return render(request, 'formularios/clientes/index.html', {
        'clientes': clientes,
        'coords_json': json.dumps(coords_json),
        'success': request.GET.get('success', ''),
    })


@login_required
def clientes_store(request):
    if request.method == 'POST':
        email = request.POST.get('email', '').strip()
        nombre = request.POST.get('nombre', '')
        apellido = request.POST.get('apellido', '')
        tipo_documento = request.POST.get('tipo_documento', '')
        numero_documento = request.POST.get('numero_documento', '')
        barrio = request.POST.get('barrio', '')
        telefono = request.POST.get('telefono', '')
        ciudad = request.POST.get('ciudad', '')
        direccion = request.POST.get('direccion', '')
        codigo_postal = request.POST.get('codigo_postal', '')

        if email and Usuario.objects.filter(email=email).exists():
            messages.error(request, 'El correo ya está registrado para otro usuario.')
            return redirect('/clientes?success=')

        username = email or f"cliente_{numero_documento or uuid.uuid4().hex[:8]}"
        if Usuario.objects.filter(username=username).exists():
            username = f"{username}_{uuid.uuid4().hex[:4]}"

        if not email:
            email = f"{username}@clientes.local"

        cliente = Usuario.objects.create_user(
            username=username,
            email=email,
            password=None,
            first_name=nombre,
            last_name=apellido,
            rol='cliente',
            tipo_documento=tipo_documento,
            numero_documento=numero_documento,
            barrio=barrio,
            telefono=telefono,
            ciudad=ciudad,
            direccion=direccion,
            codigo_postal=codigo_postal,
            activo=True,
        )

        coords = GeocodingService.geocode_address(direccion, ciudad, codigo_postal)
        if coords:
            cliente.latitud, cliente.longitud = coords
            cliente.save(update_fields=['latitud', 'longitud'])

        return redirect('/clientes?success=Cliente registrado correctamente')
    return redirect('clientes_index')


@login_required
def clientes_show(request, id):
    cliente = get_object_or_404(Usuario, pk=id, rol='cliente', is_superuser=False)
    return render(request, 'formularios/clientes/show.html', {'cliente': cliente})


@login_required
def clientes_edit(request, id):
    cliente = get_object_or_404(Usuario, pk=id, rol='cliente', is_superuser=False)
    if request.method == 'POST':
        cliente.first_name = request.POST.get('nombre', cliente.first_name)
        cliente.last_name = request.POST.get('apellido', cliente.last_name)
        cliente.email = request.POST.get('email', cliente.email)
        cliente.tipo_documento = request.POST.get('tipo_documento', cliente.tipo_documento)
        cliente.numero_documento = request.POST.get('numero_documento', cliente.numero_documento)
        cliente.barrio = request.POST.get('barrio', cliente.barrio)
        cliente.telefono = request.POST.get('telefono', cliente.telefono)
        cliente.ciudad = request.POST.get('ciudad', cliente.ciudad)
        cliente.direccion = request.POST.get('direccion', cliente.direccion)
        cliente.codigo_postal = request.POST.get('codigo_postal', cliente.codigo_postal)

        lat_str = request.POST.get('latitud', '').strip()
        lon_str = request.POST.get('longitud', '').strip()
        if lat_str and lon_str:
            try:
                from decimal import Decimal
                cliente.latitud = Decimal(lat_str)
                cliente.longitud = Decimal(lon_str)
            except:
                pass

        cliente.save()
        coords = GeocodingService.geocode_address(cliente.direccion, cliente.ciudad, cliente.codigo_postal)
        if coords:
            cliente.latitud, cliente.longitud = coords
            cliente.save(update_fields=['latitud', 'longitud'])

        return redirect('/clientes?success=Cliente actualizado correctamente')
    return render(request, 'formularios/clientes/edit.html', {'cliente': cliente})


@login_required
def clientes_toggle(request, id):
    cliente = get_object_or_404(Usuario, pk=id, rol='cliente', is_superuser=False)
    cliente.activo = not cliente.activo
    cliente.is_active = cliente.activo
    cliente.save()
    estado = 'activado' if cliente.activo else 'inhabilitado'
    return redirect(f'/clientes?success=Cliente {estado} correctamente')


@login_required
def usuarios_index(request):
    usuarios = Usuario.objects.exclude(is_superuser=True)
    return render(request, 'formularios/usuarios/index.html', {'usuarios': usuarios, 'success': request.GET.get('success', '')})


@login_required
def usuarios_show(request, id):
    usuario = get_object_or_404(Usuario, pk=id)
    return render(request, 'formularios/usuarios/show.html', {'usuario': usuario})


@login_required
def usuarios_edit(request, id):
    usuario = get_object_or_404(Usuario, pk=id)
    if request.method == 'POST':
        usuario.first_name = request.POST.get('name', usuario.first_name)
        usuario.last_name = request.POST.get('lastname', usuario.last_name)
        usuario.email = request.POST.get('email', usuario.email)
        usuario.username = request.POST.get('username', usuario.username)
        usuario.rol = request.POST.get('rol', usuario.rol)
        usuario.is_active = 'activo' in request.POST
        usuario.save()
        return redirect('/usuarios?success=Usuario actualizado correctamente')
    return render(request, 'formularios/usuarios/edit.html', {'usuario': usuario})


@login_required
def usuarios_toggle(request, id):
    usuario = get_object_or_404(Usuario, pk=id)
    usuario.is_active = not usuario.is_active
    usuario.save()
    estado = 'activado' if usuario.is_active else 'inhabilitado'
    return redirect(f'/usuarios?success=Usuario {estado} correctamente')


@login_required
@login_required
def productos_index(request):
    productos = Producto.objects.all()
    nombre = request.GET.get('nombre')
    if nombre:
        productos = productos.filter(nombre__icontains=nombre)
    
    if request.method == 'POST':
        # Crear nuevo producto
        nombre = request.POST.get('nombre')
        precio = request.POST.get('precio')
        descripcion = request.POST.get('descripcion')
        categoria = request.POST.get('categoria')
        imagen = request.FILES.get('imagen')
        
        try:
            producto = Producto.objects.create(
                nombre=nombre,
                precio=precio,
                descripcion=descripcion,
                categoria=categoria,
                imagen=imagen
            )
            return redirect('/productos?success=Producto creado correctamente')
        except Exception as e:
            return render(request, 'formularios/productos/index.html', {
                'productos': productos, 
                'error': f'Error al crear producto: {str(e)}'
            })
    
    return render(request, 'formularios/productos/index.html', {'productos': productos, 'success': request.GET.get('success', '')})


@login_required
def productos_show(request, id):
    producto = get_object_or_404(Producto, pk=id)
    return render(request, 'formularios/productos/show.html', {'producto': producto})


@login_required
def productos_edit(request, id):
    producto = get_object_or_404(Producto, pk=id)
    if request.method == 'POST':
        producto.nombre = request.POST.get('nombre', producto.nombre)
        producto.precio = request.POST.get('precio', producto.precio)
        producto.descripcion = request.POST.get('descripcion', producto.descripcion)
        producto.categoria = request.POST.get('categoria', producto.categoria)
        
        # Manejar imagen si se sube una nueva
        if 'imagen' in request.FILES:
            producto.imagen = request.FILES['imagen']
        
        producto.save()
        return redirect(f'/productos/{producto.id}?success=Producto actualizado correctamente')
    return render(request, 'formularios/productos/edit.html', {'producto': producto})


@login_required
def productos_delete(request, id):
    producto = get_object_or_404(Producto, pk=id)
    producto.activo = False
    producto.save()
    return redirect('/productos?success=Producto eliminado correctamente')


@login_required
def inventario_index(request):
    productos = Producto.objects.filter(activo=True)
    inventarios = Inventario.objects.select_related('producto').all()

    if request.method == 'POST':
        producto_id = request.POST.get('producto_id')
        cantidad_total = request.POST.get('cantidad_total')

        if not producto_id or cantidad_total is None:
            return redirect('/inventario?success=Debe seleccionar producto y cantidad')

        try:
            producto = Producto.objects.get(pk=producto_id, activo=True)
        except Producto.DoesNotExist:
            return redirect('/inventario?success=Producto no encontrado')

        try:
            cantidad_total = int(cantidad_total)
            if cantidad_total < 0:
                raise ValueError('Negativo')
        except Exception:
            return redirect('/inventario?success=Cantidad inválida')

        inventario, _ = Inventario.objects.get_or_create(producto=producto)
        inventario.cantidad_total = cantidad_total
        inventario.cantidad_disponible = cantidad_total
        inventario.save()

        return redirect('/inventario?success=Inventario registrado/actualizado correctamente')

    # Preparar productos con inventario
    inventario_por_producto = {inv.producto_id: inv for inv in inventarios}
    productos_inventario = []

    for producto in productos:
        inventario = inventario_por_producto.get(producto.id)
        productos_inventario.append({'producto': producto, 'inventario': inventario})

    return render(request, 'formularios/inventario/index.html', {
        'productos': productos,
        'inventarios': productos_inventario,
        'success': request.GET.get('success', '')
    })


@login_required
def inventario_show(request, id):
    inventario = get_object_or_404(Inventario, pk=id)
    return render(request, 'formularios/inventario/show.html', {'inventario': inventario})


@login_required
def inventario_edit(request, id):
    inventario = get_object_or_404(Inventario, pk=id)
    if request.method == 'POST':
        inventario.cantidad_total = request.POST.get('cantidad_total', inventario.cantidad_total)
        inventario.cantidad_disponible = request.POST.get('cantidad_disponible', inventario.cantidad_disponible)
        inventario.cantidad_reservada = request.POST.get('cantidad_reservada', inventario.cantidad_reservada)
        inventario.save()
        return redirect('/inventario?success=Inventario actualizado correctamente')
    return render(request, 'formularios/inventario/edit.html', {'inventario': inventario})


@login_required
def pedidos_index(request):
    pedidos = Pedido.objects.select_related('cliente', 'usuario_creacion').all()
    estado = request.GET.get('estado')
    transportista = request.GET.get('transportista')
    fecha_desde = request.GET.get('fecha_desde')
    fecha_hasta = request.GET.get('fecha_hasta')

    if estado:
        pedidos = pedidos.filter(estado=estado)
    if transportista:
        pedidos = pedidos.filter(transportista=transportista)
    if fecha_desde:
        pedidos = pedidos.filter(fecha_creacion__date__gte=fecha_desde)
    if fecha_hasta:
        pedidos = pedidos.filter(fecha_creacion__date__lte=fecha_hasta)

    # Estadísticas para las tarjetas
    pedidos_procesando = Pedido.objects.filter(estado='procesando')
    pedidos_completados = Pedido.objects.filter(estado__in=['entregado', 'completado'])
    pedidos_cancelados = Pedido.objects.filter(estado='cancelado')

    return render(request, 'formularios/pedidos/index.html', {
        'pedidos': pedidos,
        'pedidos_procesando': pedidos_procesando,
        'pedidos_completados': pedidos_completados,
        'pedidos_cancelados': pedidos_cancelados,
        'success': request.GET.get('success', '')
    })


@login_required
def pedidos_show(request, id):
    pedido = get_object_or_404(Pedido, pk=id)
    detalles = pedido.detalles.select_related('producto').all()
    return render(request, 'formularios/pedidos/show.html', {'pedido': pedido, 'detalles': detalles})


@login_required
def pedidos_edit(request, id):
    pedido = get_object_or_404(Pedido, pk=id)
    if request.method == 'POST':
        pedido.estado = request.POST.get('estado', pedido.estado)
        pedido.transportista = request.POST.get('transportista', pedido.transportista)
        pedido.numero_seguimiento = request.POST.get('numero_seguimiento', pedido.numero_seguimiento)
        pedido.notas = request.POST.get('notas', pedido.notas)
        pedido.save()
        return redirect('/pedidos?success=Pedido actualizado correctamente')
    return render(request, 'formularios/pedidos/edit.html', {'pedido': pedido})


@login_required
def pedidos_toggle(request, id):
    pedido = get_object_or_404(Pedido, pk=id)
    # Permite alternar entre cancelado y pendiente para simulación
    pedido.estado = 'pendiente' if pedido.estado == 'cancelado' else 'cancelado'
    pedido.save()
    return redirect('/pedidos?success=Estado de pedido actualizado correctamente')


@login_required
def pedidos_cambiar_estado(request, id):
    pedido = get_object_or_404(Pedido, pk=id)
    if request.method == 'POST':
        nuevo_estado = request.POST.get('estado')
        if nuevo_estado in dict(Pedido.ESTADOS_CHOICES):
            pedido.estado = nuevo_estado
            pedido.save()
            return redirect('/pedidos?success=Estado de pedido actualizado correctamente')
    return redirect('/pedidos')


@login_required
def pedidos_cambiar_datos(request, id):
    pedido = get_object_or_404(Pedido, pk=id)
    if request.method == 'POST':
        nuevo_estado = request.POST.get('estado')
        nuevo_transportista = request.POST.get('transportista')
        if nuevo_estado in dict(Pedido.ESTADOS_CHOICES):
            pedido.estado = nuevo_estado
        if nuevo_transportista:
            pedido.transportista = nuevo_transportista
        pedido.save()
        return redirect('/pedidos?success=Datos de pedido actualizados correctamente')
    return redirect('/pedidos')


@login_required
def ventas_index(request):
    ventas = Venta.objects.select_related('cliente', 'vendedor').all()
    pedidos_entregados = Pedido.objects.filter(estado='entregado').select_related('cliente', 'usuario_creacion')
    return render(request, 'formularios/ventas/index.html', {
        'ventas': ventas,
        'pedidos_entregados': pedidos_entregados,
        'success': request.GET.get('success', ''),
    })


@login_required
def ventas_show(request, id):
    venta = get_object_or_404(Venta, pk=id)
    return render(request, 'formularios/ventas/show.html', {'venta': venta})


@login_required
def ventas_edit(request, id):
    venta = get_object_or_404(Venta, pk=id)
    if request.method == 'POST':
        venta.total_venta = request.POST.get('total_venta', venta.total_venta)
        venta.descripcion = request.POST.get('descripcion', venta.descripcion)
        venta.comision_vendedor = request.POST.get('comision_vendedor', venta.comision_vendedor)
        venta.save()
        return redirect('/ventas?success=Venta actualizada correctamente')
    return render(request, 'formularios/ventas/edit.html', {'venta': venta})


@login_required
def pagos_index(request):
    pagos = Pago.objects.select_related('pedido').all()
    return render(request, 'formularios/pagos/index.html', {'pagos': pagos, 'success': request.GET.get('success', '')})


@login_required
@login_required
def pagos_nuevo(request):
    carrito = None
    total_carrito = 0
    
    if request.method == 'POST':
        # Obtener datos del formulario
        monto = request.POST.get('monto')
        metodo = request.POST.get('metodo')
        
        # Si hay carrito en sesión, crear pedido automáticamente
        carrito_json = request.POST.get('carrito_json') or request.session.get('carrito')
        
        # Debug: Imprimir información
        print(f"DEBUG - carrito_json recibido: {carrito_json[:100] if carrito_json else 'VACÍO'}")
        print(f"DEBUG - POST data: {request.POST}")
        
        if carrito_json and monto:
            try:
                carrito_data = json.loads(carrito_json)
                print(f"DEBUG - Items en carrito: {len(carrito_data.get('items', []))}")
                
                # Crear pedido
                usuario = request.user
                cliente = usuario if getattr(usuario, 'rol', '') == 'cliente' else usuario

                pedido = Pedido.objects.create(
                    cliente=cliente,
                    numero_pedido=f"PED-{usuario.id}-{Pedido.objects.count() + 1}",
                    estado='pendiente',
                    total=float(monto),
                    direccion_envio=getattr(cliente, 'direccion', 'No especificada'),
                    ciudad_envio=getattr(cliente, 'ciudad', 'No especificada'),
                    codigo_postal_envio=getattr(cliente, 'codigo_postal', ''),
                    usuario_creacion=usuario,
                )

                # Crear detalles del pedido y decrementar stock
                from apps.pedidos.models import DetallePedido
                for item in carrito_data.get('items', []):
                    producto = Producto.objects.filter(pk=item.get('id')).first()
                    print(f"DEBUG - Procesando item ID {item.get('id')}, cantidad {item.get('cantidad')}, producto encontrado: {producto is not None}")

                    if producto:
                        cantidad_item = item.get('cantidad', 1)
                        # Crear detalle del pedido
                        DetallePedido.objects.create(
                            pedido=pedido,
                            producto=producto,
                            cantidad=cantidad_item,
                            precio_unitario=item.get('precio', 0),
                            subtotal=float(item.get('precio', 0)) * cantidad_item,
                        )
                        # Decrementar stock en Inventario (cantidad_disponible)
                        inventario = getattr(producto, 'inventario', None)
                        if inventario:
                            print(f"DEBUG - Inventario antes: {inventario.cantidad_disponible}")
                            inventario.cantidad_disponible = max(0, inventario.cantidad_disponible - cantidad_item)
                            inventario.cantidad_total = max(0, inventario.cantidad_total - cantidad_item)
                            inventario.save()
                            print(f"DEBUG - Inventario después: {inventario.cantidad_disponible}")
                        else:
                            print(f"WARNING: Producto {producto.id} no tiene inventario asignado")

                        # Actualizar / sincronizar campo producto.cantidad si lo usan.
                        print(f"DEBUG - Producto.cantidad antes: {producto.cantidad}")
                        producto.cantidad = max(0, producto.cantidad - cantidad_item)
                        producto.save()
                        print(f"DEBUG - Producto.cantidad después: {producto.cantidad}")

                # Crear pago
                    import uuid
                    referencia = str(uuid.uuid4())
                    # Asegurar que la referencia sea única
                    from django.db import IntegrityError
                    max_trials = 5
                    for i in range(max_trials):
                        try:
                            Pago.objects.create(
                                pedido=pedido,
                                metodo=metodo,
                                monto=monto,
                                estado='pendiente',
                                referencia_transaccion=referencia,
                            )
                            break
                        except IntegrityError:
                            referencia = str(uuid.uuid4())
                            if i == max_trials - 1:
                                raise

                    # Limpiar sesión
                    if 'carrito' in request.session:
                        del request.session['carrito']

                    return redirect(f'/pagos/{Pago.objects.filter(pedido=pedido).first().id}/')
                    # Limpiar sesión
                    if 'carrito' in request.session:
                        del request.session['carrito']
                    
                    return redirect(f'/pagos/{Pago.objects.filter(pedido=pedido).first().id}/')
            except Exception as e:
                import traceback
                traceback.print_exc()
                messages.error(request, f'Error al procesar el pago: {str(e)}')
        else:
            # Flujo pedido existente
            pedido_id = request.POST.get('pedido_id')
            if pedido_id and monto:
                pedido = get_object_or_404(Pedido, pk=pedido_id)
                import uuid
                referencia = str(uuid.uuid4())
                from django.db import IntegrityError
                for i in range(5):
                    try:
                        Pago.objects.create(
                            pedido=pedido,
                            metodo=metodo,
                            monto=monto,
                            estado='pendiente',
                            referencia_transaccion=referencia,
                        )
                        break
                    except IntegrityError:
                        referencia = str(uuid.uuid4())
                        if i == 4:
                            raise

                return redirect('/pagos?success=Pago registrado correctamente')
            messages.error(request, 'No se ha recibido carrito ni pedido válido. Por favor vuelve a intentar.')
    
    # GET request: mostrar formulario
    pedidos = Pedido.objects.filter(pago__isnull=True)
    
    # Obtener datos del usuario que actúa como cliente
    usuario = request.user
    datos_cliente = {
        'nombre': usuario.first_name or usuario.username,
        'apellido': usuario.last_name or '',
        'ciudad': usuario.ciudad or '',
        'direccion': usuario.direccion or '',
        'codigo_postal': usuario.codigo_postal or '',
        'telefono': getattr(usuario, 'telefono', ''),
    }
    
    # Nota: El carrito viene de localStorage en el cliente y se envía como hidden input en POST
    # En GET simplemente mostramos el formulario vacío, el JS rellenará los datos desde localStorage
    
    return render(request, 'formularios/pagos/nuevo.html', {
        'pedidos': pedidos,
        'carrito': None,  # Se rellenará desde JS localStorage
        'total_carrito': 0,
        'datos_cliente': json.dumps(datos_cliente),
    })


@login_required
def pagos_show(request, id):
    pago = get_object_or_404(Pago, pk=id)
    return render(request, 'formularios/pagos/show.html', {'pago': pago})


@login_required
def pagos_confirmacion(request, id):
    pago = get_object_or_404(Pago, pk=id)
    pago.estado = 'completado'
    pago.save()

    # Actualizar estado del pedido asociado para reflejar el pago completado
    pedido = pago.pedido
    if pedido:
        pedido.estado = 'completado'
        pedido.save()

    return render(request, 'formularios/pagos/confirmacion.html', {'pago': pago})


@login_required
def logout_view(request):
    logout(request)
    messages.info(request, 'Sesión cerrada correctamente')
    return redirect('login')
