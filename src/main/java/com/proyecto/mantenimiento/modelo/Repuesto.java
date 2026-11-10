package com.proyecto.mantenimiento.modelo;

public class Repuesto {
    private int id;
    private String nombre;
    private int stock;
    private double precio;

    public Repuesto(int id, String nombre, int stock, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
    }

    public String toLinea() {
        return id + "|" + nombre + "|" + stock + "|" + precio;
    }

    public static Repuesto desdeLinea(String linea) {
        String[] partes = linea.split("\\|", -1);
        if (partes.length != 4) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }
        return new Repuesto(Integer.parseInt(partes[0]), partes[1],
                Integer.parseInt(partes[2]), Double.parseDouble(partes[3]));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}