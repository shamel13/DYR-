package com.DYR.proyecto.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;   // nombre de login
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 8, max = 100, message = "El nombre debe tener entre 8 y 100 caracteres")
    private String name;       // nombre real
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]{6,}@(gmail|hotmail|outlook|yahoo)\\.(com|es|co)$", 
             message = "Solo se permiten correos de Gmail, Hotmail, Outlook o Yahoo con dominios .com, .es o .co")
    @Column(unique = true)
    private String email;
    
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String documentType;
    
    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^[0-9]{1,10}$", message = "El número de documento debe contener solo números y máximo 10 dígitos")
    @Column(unique = true)
    private String documentNumber;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{7,10}$", message = "El teléfono debe tener entre 7 y 10 dígitos")
    private String phone;
    // Dirección personal del usuario
    private String address;
    private String neighborhood;
    private String city;
    private String postalCode;
    private String country;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).+$", message = "La contraseña debe contener letras y números")
    private String password;
    private String role;       // ADMIN, VENDEDOR, CLIENTE, etc.
    @Column(columnDefinition = "varchar(20) default 'activo'")
    private String estado = "activo";  // activo o inactivo

    public Usuario() {}

    public Usuario(String username, String name, String email, String password, String role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Usuario(String username, String name, String email, String documentType, String documentNumber, String phone, String password, String role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
