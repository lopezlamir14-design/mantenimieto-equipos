package com.proyecto.mantenimiento.datos;

import com.proyecto.mantenimiento.modelo.Equipo;
import com.proyecto.mantenimiento.modelo.Orden;
import com.proyecto.mantenimiento.modelo.Repuesto;
import com.proyecto.mantenimiento.modelo.Tecnico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestorDatos {

    private static final String ARCHIVO_EQUIPOS = "equipos.txt";
    private static final String ARCHIVO_TECNICOS = "tecnicos.txt";
    private static final String ARCHIVO_REPUESTOS = "repuestos.txt";
    private static final String ARCHIVO_ORDENES = "ordenes.txt";

    private static final List<Equipo> equipos = new ArrayList<>();
    private static final List<Tecnico> tecnicos = new ArrayList<>();
    private static final List<Repuesto> repuestos = new ArrayList<>();
    private static final List<Orden> ordenes = new ArrayList<>();

    private static boolean cargado = false;

    // La primera vez que alguien pide una lista, se cargan todos los archivos
    private static void cargarSiHaceFalta() {
        if (cargado) {
            return;
        }
        cargado = true;
        crearDatosDeEjemploSiFaltan();
        cargarEquipos();
        cargarTecnicos();
        cargarRepuestos();
        cargarOrdenes();
    }

    private static void crearSiFalta(String archivo, List<String> lineas) {
        if (ArchivoDatos.existe(archivo)) {
            return;
        }
        try {
            ArchivoDatos.escribirLineas(archivo, lineas);
        } catch (IOException e) {
            System.err.println("No se pudo crear " + archivo + ": " + e.getMessage());
        }
    }

    private static void crearDatosDeEjemploSiFaltan() {
        List<String> eq = new ArrayList<>();
        eq.add("EQ-001|Torno CNC|Planta 1|TX-200|2021-03-15|Operativo");
        eq.add("EQ-002|Compresor de aire|Planta 2|CP-90|2019-07-01|En mantenimiento");
        eq.add("EQ-003|Fresadora|Planta 1|FR-50|2020-11-20|Operativo");
        eq.add("EQ-004|Soldadora MIG|Taller|SM-310|2022-01-10|Operativo");
        eq.add("EQ-005|Prensa hidráulica|Planta 2|PH-500|2018-05-30|Fuera de servicio");
        crearSiFalta(ARCHIVO_EQUIPOS, eq);

        List<String> te = new ArrayList<>();
        te.add("1|Carlos Ruiz|3001112233|Mecánica");
        te.add("2|Laura Gómez|3002223344|Eléctrica");
        te.add("3|Andrés Torres|3003334455|Hidráulica");
        crearSiFalta(ARCHIVO_TECNICOS, te);

        List<String> re = new ArrayList<>();
        re.add("1|Rodamiento 6204|20|15000.0");
        re.add("2|Correa de transmisión|10|32000.0");
        re.add("3|Filtro de aire|15|18500.0");
        re.add("4|Aceite hidráulico (galón)|8|95000.0");
        crearSiFalta(ARCHIVO_REPUESTOS, re);

        List<String> or = new ArrayList<>();
        or.add("1|EQ-001|1|Preventivo||2026-11-12|Programada|1|2|30000.0|50000.0");
        or.add("2|EQ-002|2|Correctivo|Sobrecalentamiento del motor|2026-11-05|En proceso|0|0|0.0|80000.0");
        or.add("3|EQ-003|1|Preventivo||2026-10-20|Cerrada|3|1|18500.0|40000.0");
        or.add("4|EQ-005|3|Correctivo|Fuga de aceite|2026-11-10|Programada|4|1|95000.0|120000.0");
        or.add("5|EQ-001|2|Correctivo|Falla eléctrica|2026-09-15|Cerrada|0|0|0.0|60000.0");
        crearSiFalta(ARCHIVO_ORDENES, or);
    }

    private static void cargarEquipos() {
        for (String linea : ArchivoDatos.leerLineas(ARCHIVO_EQUIPOS)) {
            try {
                equipos.add(Equipo.desdeLinea(linea));
            } catch (Exception e) {
                System.err.println("Línea ignorada en " + ARCHIVO_EQUIPOS + ": " + linea);
            }
        }
    }

    private static void cargarTecnicos() {
        for (String linea : ArchivoDatos.leerLineas(ARCHIVO_TECNICOS)) {
            try {
                tecnicos.add(Tecnico.desdeLinea(linea));
            } catch (Exception e) {
                System.err.println("Línea ignorada en " + ARCHIVO_TECNICOS + ": " + linea);
            }
        }
    }

    private static void cargarRepuestos() {
        for (String linea : ArchivoDatos.leerLineas(ARCHIVO_REPUESTOS)) {
            try {
                repuestos.add(Repuesto.desdeLinea(linea));
            } catch (Exception e) {
                System.err.println("Línea ignorada en " + ARCHIVO_REPUESTOS + ": " + linea);
            }
        }
    }

    private static void cargarOrdenes() {
        for (String linea : ArchivoDatos.leerLineas(ARCHIVO_ORDENES)) {
            try {
                ordenes.add(Orden.desdeLinea(linea));
            } catch (Exception e) {
                System.err.println("Línea ignorada en " + ARCHIVO_ORDENES + ": " + linea);
            }
        }
    }


    public static List<Equipo> getEquipos() {
        cargarSiHaceFalta();
        return equipos;
    }

    public static List<Tecnico> getTecnicos() {
        cargarSiHaceFalta();
        return tecnicos;
    }

    public static List<Repuesto> getRepuestos() {
        cargarSiHaceFalta();
        return repuestos;
    }

    public static List<Orden> getOrdenes() {
        cargarSiHaceFalta();
        return ordenes;
    }

    public static void guardarEquipos() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Equipo equipo : equipos) {
            lineas.add(equipo.toLinea());
        }
        ArchivoDatos.escribirLineas(ARCHIVO_EQUIPOS, lineas);
    }

    public static void guardarTecnicos() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Tecnico tecnico : tecnicos) {
            lineas.add(tecnico.toLinea());
        }
        ArchivoDatos.escribirLineas(ARCHIVO_TECNICOS, lineas);
    }

    public static void guardarRepuestos() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Repuesto repuesto : repuestos) {
            lineas.add(repuesto.toLinea());
        }
        ArchivoDatos.escribirLineas(ARCHIVO_REPUESTOS, lineas);
    }

    public static void guardarOrdenes() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Orden orden : ordenes) {
            lineas.add(orden.toLinea());
        }
        ArchivoDatos.escribirLineas(ARCHIVO_ORDENES, lineas);
    }
    public static Equipo buscarEquipo(String codigo) {
        for (Equipo equipo : getEquipos()) {
            if (equipo.getCodigo().equalsIgnoreCase(codigo)) {
                return equipo;
            }
        }
        return null;
    }

    public static Tecnico buscarTecnico(int id) {
        for (Tecnico tecnico : getTecnicos()) {
            if (tecnico.getId() == id) {
                return tecnico;
            }
        }
        return null;
    }

    public static Repuesto buscarRepuesto(int id) {
        for (Repuesto repuesto : getRepuestos()) {
            if (repuesto.getId() == id) {
                return repuesto;
            }
        }
        return null;
    }
    public static String nombreTecnico(int id) {
        Tecnico tecnico = buscarTecnico(id);
        if (tecnico == null) {
            return "(técnico eliminado)";
        }
        return tecnico.getNombre();
    }
    public static int siguienteIdTecnico() {
        int mayor = 0;
        for (Tecnico tecnico : getTecnicos()) {
            if (tecnico.getId() > mayor) {
                mayor = tecnico.getId();
            }
        }
        return mayor + 1;
    }

    public static int siguienteIdRepuesto() {
        int mayor = 0;
        for (Repuesto repuesto : getRepuestos()) {
            if (repuesto.getId() > mayor) {
                mayor = repuesto.getId();
            }
        }
        return mayor + 1;
    }

    public static int siguienteIdOrden() {
        int mayor = 0;
        for (Orden orden : getOrdenes()) {
            if (orden.getId() > mayor) {
                mayor = orden.getId();
            }
        }
        return mayor + 1;
    }



    public static boolean equipoTieneOrdenes(String codigo) {
        for (Orden orden : getOrdenes()) {
            if (orden.getEquipoCodigo().equalsIgnoreCase(codigo)) {
                return true;
            }
        }
        return false;
    }

    public static boolean tecnicoTieneOrdenes(int tecnicoId) {
        for (Orden orden : getOrdenes()) {
            if (orden.getTecnicoId() == tecnicoId) {
                return true;
            }
        }
        return false;
    }

    public static boolean repuestoTieneOrdenes(int repuestoId) {
        for (Orden orden : getOrdenes()) {
            if (orden.getRepuestoId() == repuestoId) {
                return true;
            }
        }
        return false;
    }

    public static String agregarOrden(Orden orden) throws IOException {
        if (orden.getRepuestoId() != 0) {
            Repuesto repuesto = buscarRepuesto(orden.getRepuestoId());
            if (repuesto == null) {
                return "El repuesto no existe";
            }
            if (orden.getCantRepuesto() < 1) {
                return "La cantidad de repuesto debe ser al menos 1";
            }
            if (orden.getCantRepuesto() > repuesto.getStock()) {
                return "Stock insuficiente: solo quedan " + repuesto.getStock();
            }
            // Todo está bien: se descuenta el stock y se calcula el costo de los repuestos
            repuesto.setStock(repuesto.getStock() - orden.getCantRepuesto());
            orden.setCostoRepuestos(orden.getCantRepuesto() * repuesto.getPrecio());
            guardarRepuestos();
        }
        orden.setId(siguienteIdOrden());
        getOrdenes().add(orden);
        guardarOrdenes();
        return null;
    }

    public static void eliminarOrden(Orden orden) throws IOException {
        if (orden.getRepuestoId() != 0 && !orden.getEstado().equals("Cerrada")) {
            Repuesto repuesto = buscarRepuesto(orden.getRepuestoId());
            if (repuesto != null) {
                repuesto.setStock(repuesto.getStock() + orden.getCantRepuesto());
                guardarRepuestos();
            }
        }
        getOrdenes().remove(orden);
        guardarOrdenes();
    }
}