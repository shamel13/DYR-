import stripe
from django.conf import settings


class StripeService:
    """Servicio para crear PaymentIntents con Stripe."""

    def __init__(self):
        if not settings.STRIPE_SECRET_KEY:
            raise ValueError('STRIPE_SECRET_KEY no está configurado en settings.')
        stripe.api_key = settings.STRIPE_SECRET_KEY

    def crear_intent(self, amount, currency=None, metadata=None):
        """Crea un PaymentIntent en Stripe."""
        currency = currency or settings.STRIPE_CURRENCY
        intent = stripe.PaymentIntent.create(
            amount=amount,
            currency=currency,
            payment_method_types=['card'],
            metadata=metadata or {},
        )
        return intent

    def obtener_intent(self, payment_intent_id):
        """Recupera un PaymentIntent existente."""
        return stripe.PaymentIntent.retrieve(payment_intent_id)
