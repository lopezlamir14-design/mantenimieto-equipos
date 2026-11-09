package com.proyecto.mantenimiento.datos;
import com.proyecto.mantenimiento.modelo.Equipo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class GestorDatos {
    private static final String ARCHIVO_EQUIPOS = "equipos.txt";
    private static final List<Equipo> equipos = new ArrayList<>();

    static {
        sembrarEquiposSiFalta();
        equipos.addAll(cargar(ARCHIVO_EQUIPOS, Equipo::desdeLinea));
    }

    private GestorDatos() { }

    /** Lee un archivo y convierte cada línea en un objeto. Las líneas malas se saltan. */
    private static <T> List<T> cargar(String archivo, Function<String, T> convertidor) {
        List<T> lista = new ArrayList<>();
        for (String linea : ArchivoDatos.leerLineas(archivo)) {
            try {
                lista.add(convertidor.apply(linea));
            } catch (Exception e) {
                System.err.println("Línea ignorada en " + archivo + ": " + linea);
            }
        }
        return lista;
    }

    private static void sembrarEquiposSiFalta() {
        if (ArchivoDatos.existe(ARCHIVO_EQUIPOS)) return;
        try {
            ArchivoDatos.escribirLineas(ARCHIVO_EQUIPOS, List.of(
                "EQ-001|Torno CNC|Planta 1|TX-200|2021-03-15|Operativo",
                "EQ-002|Compresor de aire|Planta 2|CP-90|2019-07-01|En mantenimiento",
                "EQ-003|Fresadora|Planta 1|FR-50|2020-11-20|Operativo",
                "EQ-004|Soldadora MIG|Taller|SM-310|2022-01-10|Operativo",
                "EQ-005|Prensa hidráulica|Planta 2|PH-500|2018-05-30|Fuera de servicio"));
        } catch (IOException e) {
            System.err.println("No se pudieron crear los datos de ejemplo: " + e.getMessage());
        }
    }
    public static List<Equipo> getEquipos() {
        return equipos;
    }
    public static Equipo buscarEquipo(String codigo) {
        return equipos.stream()
                .filter(e -> e.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElse(null);
    }

    public static void guardarEquipos() throws IOException {
        ArchivoDatos.escribirLineas(ARCHIVO_EQUIPOS,
                equipos.stream().map(Equipo::toLinea).toList());
    }
}