from rest_framework import serializers
from django.contrib.auth import authenticate
from .models import Usuario

class UsuarioSerializer(serializers.ModelSerializer):
    """Serializer para el modelo Usuario"""
    password = serializers.CharField(write_only=True, min_length=8)
    
    class Meta:
        model = Usuario
        fields = ('id', 'username', 'email', 'password', 'first_name', 'last_name', 
                  'rol', 'telefono', 'activo', 'fecha_creacion')
        read_only_fields = ('id', 'fecha_creacion')
    
    def create(self, validated_data):
        """Crear nuevo usuario con contraseña hasheada"""
        password = validated_data.pop('password')
        usuario = Usuario.objects.create_user(**validated_data)
        usuario.set_password(password)
        usuario.save()
        return usuario
    
    def update(self, instance, validated_data):
        """Actualizar usuario"""
        password = validated_data.pop('password', None)
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        if password:
            instance.set_password(password)
        instance.save()
        return instance

class LoginSerializer(serializers.Serializer):
    """Serializer para login"""
    username = serializers.CharField()
    password = serializers.CharField(write_only=True)
    
    def validate(self, data):
        user = authenticate(username=data.get('username'), password=data.get('password'))
        if not user:
            raise serializers.ValidationError("Credenciales inválidas")
        return user
