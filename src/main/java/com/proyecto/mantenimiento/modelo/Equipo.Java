package com.proyecto.mantenimiento.modelo;

import java.time.LocalDate;

public class Equipo {
    private String codigo;
    private String nombre;
    private String ubicacion;
    private String modelo;
    private LocalDate fechaAdquisicion;
    private String estado;

    public Equipo(String codigo, String nombre, String ubicacion, String modelo,
        LocalDate fechaAdquisicion, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.modelo = modelo;
        this.fechaAdquisicion = fechaAdquisicion;
        this.estado = estado;
    }
    public String toLinea() {
        return codigo + "|" + nombre + "|" + ubicacion + "|" + modelo + "|"
                + fechaAdquisicion + "|" + estado;
    }


    public static Equipo desdeLinea(String linea) {
        String[] partes = linea.split("\\|", -1);
        if (partes.length != 6) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }
        LocalDate fecha = LocalDate.parse(partes[4]);
        return new Equipo(partes[0], partes[1], partes[2], partes[3], fecha, partes[5]);
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDate fecha) { this.fechaAdquisicion = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}