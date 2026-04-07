from django.apps import AppConfig
from django.db.models.signals import post_migrate


def create_default_admin(sender, **kwargs):
    from django.contrib.auth import get_user_model

    User = get_user_model()
    username = 'admin'
    password = 'Admin123!'
    email = 'admin@example.com'

    if not User.objects.filter(username=username).exists():
        User.objects.create_superuser(
            username=username,
            email=email,
            password=password,
            first_name='Admin',
            last_name='User',
        )


class UsuariosConfig(AppConfig):
    name = 'apps.usuarios'

    def ready(self):
        post_migrate.connect(create_default_admin, sender=self)
