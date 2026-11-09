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

    public static List<String> leerLineas(String archivo) {
        Path ruta = CARPETA.resolve(archivo);
        try {
            if (!Files.exists(ruta)) {
                return new ArrayList<>();
            }
            return Files.readAllLines(ruta, StandardCharsets.UTF_8).stream()
                        .filter(l -> !l.isBlank())
                        .toList();
        } catch (IOException e) {
            System.err.println("No se pudo leer " + archivo + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public static void escribirLineas(String archivo, List<String> lineas) throws IOException {
        Files.createDirectories(CARPETA);
        Files.write(CARPETA.resolve(archivo), lineas, StandardCharsets.UTF_8);
    }
}