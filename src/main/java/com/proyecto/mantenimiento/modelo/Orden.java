package com.proyecto.mantenimiento.modelo;

import java.time.LocalDate;

public class Orden {
    private int id;
    private String equipoCodigo;
    private int tecnicoId;
    private String tipo;
    private String falla;
    private LocalDate fecha;
    private String estado;
    private int repuestoId;
    private int cantRepuesto;
    private double costoRepuestos;
    private double costoManoObra;

    public Orden(int id, String equipoCodigo, int tecnicoId, String tipo, String falla,
                LocalDate fecha, String estado, int repuestoId, int cantRepuesto,
                double costoRepuestos, double costoManoObra) {
        this.id = id;
        this.equipoCodigo = equipoCodigo;
        this.tecnicoId = tecnicoId;
        this.tipo = tipo;
        this.falla = falla;
        this.fecha = fecha;
        this.estado = estado;
        this.repuestoId = repuestoId;
        this.cantRepuesto = cantRepuesto;
        this.costoRepuestos = costoRepuestos;
        this.costoManoObra = costoManoObra;
    }

    public String toLinea() {
        return id + "|" + equipoCodigo + "|" + tecnicoId + "|" + tipo + "|" + falla + "|"
                + fecha + "|" + estado + "|" + repuestoId + "|" + cantRepuesto + "|"
                + costoRepuestos + "|" + costoManoObra;
    }

    public static Orden desdeLinea(String linea) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 11) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }
        return new Orden(Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]), p[3], p[4],
                LocalDate.parse(p[5]), p[6], Integer.parseInt(p[7]), Integer.parseInt(p[8]),
                Double.parseDouble(p[9]), Double.parseDouble(p[10]));
    }

    public double getCostoTotal() {
        return costoRepuestos + costoManoObra;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEquipoCodigo() { return equipoCodigo; }

    public int getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(int tecnicoId) { this.tecnicoId = tecnicoId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFalla() { return falla; }
    public void setFalla(String falla) { this.falla = falla; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getRepuestoId() { return repuestoId; }

    public int getCantRepuesto() { return cantRepuesto; }

    public double getCostoRepuestos() { return costoRepuestos; }
    public void setCostoRepuestos(double costoRepuestos) { this.costoRepuestos = costoRepuestos; }

    public double getCostoManoObra() { return costoManoObra; }
    public void setCostoManoObra(double costoManoObra) { this.costoManoObra = costoManoObra; }
}