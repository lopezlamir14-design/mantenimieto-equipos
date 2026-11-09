package com.proyecto.mantenimiento.modelo;
import java.time.LocalDate;

public class Equipo {
    private String codigo;
    private String nombre;
    private String ubicacion;
    private String modelo;
    private LocalDate fechadeadquisicion;
    private String estado;

    public Equipo(String codigo, String nombre, String ubicacion, String modelo,
        LocalDate fechadeadquisicion, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.modelo = modelo;
        this.fechadeadquisicion = fechadeadquisicion;
        this.estado = estado;
    }


    public String toLinea() {
        return String.join("|", codigo, nombre, ubicacion, modelo,
                fechadeadquisicion.toString(), estado);
    }
    public static Equipo desdeLinea(String linea) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 6) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }
        return new Equipo(p[0], p[1], p[2], p[3], LocalDate.parse(p[4]), p[5]);
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public LocalDate getFechaAdquisicion() { return fechadeadquisicion; }
    public void setFechaAdquisicion(LocalDate f) { this.fechadeadquisicion = f; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}