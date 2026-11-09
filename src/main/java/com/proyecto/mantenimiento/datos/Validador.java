package com.proyecto.mantenimiento.datos;

public class Validador {

    private Validador() { }

    public static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            return campo + " es obligatorio";
        }
        if (valor.contains("|")) {
            return campo + " no puede contener el símbolo |";
        }
        return null;
    }
}
