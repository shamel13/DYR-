from django import template
register = template.Library()

@register.filter
def coord_format(value):
    """Formatea coordenadas con punto decimal para Google Maps"""
    if value is None:
        return ''
    try:
        return f'{float(value):.6f}'
    except (ValueError, TypeError):
        return ''
