package com.proyecto.mantenimiento.modelo;

public class Tecnico {
    private int id;
    private String nombre;
    private String telefono;
    private String especialidad;

    public Tecnico(int id, String nombre, String telefono, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.especialidad = especialidad;
    }

    public String toLinea() {
        return id + "|" + nombre + "|" + telefono + "|" + especialidad;
    }

    public static Tecnico desdeLinea(String linea) {
        String[] partes = linea.split("\\|", -1);
        if (partes.length != 4) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }
        return new Tecnico(Integer.parseInt(partes[0]), partes[1], partes[2], partes[3]);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
