package com.proyecto.mantenimiento.datos;
import com.proyecto.mantenimiento.modelo.Equipo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestorDatos {

    private static final String ARCHIVO_EQUIPOS = "equipos.txt";

    private static final List<Equipo> equipos = new ArrayList<>();
    private static boolean equiposCargados = false;

    // La primera vez que alguien pide la lista, se carga desde el archivo
    public static List<Equipo> getEquipos() {
        if (!equiposCargados) {
            crearDatosDeEjemploSiFaltan();
            cargarEquipos();
            equiposCargados = true;
        }
        return equipos;
    }

    private static void crearDatosDeEjemploSiFaltan() {
        if (ArchivoDatos.existe(ARCHIVO_EQUIPOS)) {
            return;
        }
        List<String> ejemplo = new ArrayList<>();
        ejemplo.add("EQ-001|Torno CNC|Planta 1|TX-200|2021-03-15|Operativo");
        ejemplo.add("EQ-002|Compresor de aire|Planta 2|CP-90|2019-07-01|En mantenimiento");
        ejemplo.add("EQ-003|Fresadora|Planta 1|FR-50|2020-11-20|Operativo");
        ejemplo.add("EQ-004|Soldadora MIG|Taller|SM-310|2022-01-10|Operativo");
        ejemplo.add("EQ-005|Prensa hidráulica|Planta 2|PH-500|2018-05-30|Fuera de servicio");
        try {
            ArchivoDatos.escribirLineas(ARCHIVO_EQUIPOS, ejemplo);
        } catch (IOException e) {
            System.err.println("No se pudieron crear los datos de ejemplo: " + e.getMessage());
        }
    }

    private static void cargarEquipos() {
        List<String> lineas = ArchivoDatos.leerLineas(ARCHIVO_EQUIPOS);
        for (String linea : lineas) {
            try {
                equipos.add(Equipo.desdeLinea(linea));
            } catch (Exception e) {
                System.err.println("Línea ignorada en " + ARCHIVO_EQUIPOS + ": " + linea);
            }
        }
    }

    public static void guardarEquipos() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Equipo equipo : equipos) {
            lineas.add(equipo.toLinea());
        }
        ArchivoDatos.escribirLineas(ARCHIVO_EQUIPOS, lineas);
    }

    public static Equipo buscarEquipo(String codigo) {
        for (Equipo equipo : getEquipos()) {
            if (equipo.getCodigo().equalsIgnoreCase(codigo)) {
                return equipo;
            }
        }
        return null;
    }
}