package com.proyecto.mantenimiento.datos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public class ArchivoDatos {

    private static final Path CARPETA = Path.of("data");

    public static boolean existe(String archivo) {
        return Files.exists(CARPETA.resolve(archivo));
    }

    // Devuelve las líneas del archivo. Si no existe o falla, devuelve una lista vacía.
    public static List<String> leerLineas(String archivo) {
        List<String> lineas = new ArrayList<>();
        Path ruta = CARPETA.resolve(archivo);

        if (!Files.exists(ruta)) {
            return lineas;
        }

        try {
            List<String> todas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            for (String linea : todas) {
                if (!linea.isBlank()) {
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + archivo + ": " + e.getMessage());
        }
        return lineas;
    }
    public static void escribirLineas(String archivo, List<String> lineas) throws IOException {
        Files.createDirectories(CARPETA);
        Files.write(CARPETA.resolve(archivo), lineas, StandardCharsets.UTF_8);
    }
}