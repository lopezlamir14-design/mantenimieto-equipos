package com.proyecto.mantenimiento.modelo;

// Una fila del reporte sobre cuánto costó el mantenimiento de un equipo en un mes
public class FilaCosto {
    private String equipo;
    private String mes;
    private double total;
    private int cantidad;     // cuántas órdenes se sumaron

    public FilaCosto(String equipo, String mes) {
        this.equipo = equipo;
        this.mes = mes;
        this.total = 0;
        this.cantidad = 0;
    }

    public void sumar(double valor) {
        total = total + valor;
        cantidad = cantidad + 1;
    }

    public String getEquipo() { return equipo; }
    public String getMes() { return mes; }
    public double getTotal() { return total; }
    public int getCantidad() { return cantidad; }
}