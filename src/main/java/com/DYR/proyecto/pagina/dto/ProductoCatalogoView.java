package com.DYR.proyecto.pagina.dto;

/**
 * Simple projection for catálogo: producto + stock actual + imagen.
 */
public class ProductoCatalogoView {

    private final Long id;
    private final String nombre;
    private final Double precio;
    private final String descripcion;
    private final Integer stockActual;
    private final String imagenUrl;

    public ProductoCatalogoView(Long id, String nombre, Double precio, String descripcion, Integer stockActual, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.stockActual = stockActual;
        this.imagenUrl = imagenUrl;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }
}
