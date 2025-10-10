package com.tp;
import com.tp.excepciones.PersistenciaException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
public class Main {

    public static void main(String[] args) {
        
        try {
            initializeDataDirectory();
        } catch (PersistenciaException e) {
            System.err.println("Error fatal al inicializar el directorio de datos: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    private static void initializeDataDirectory() throws PersistenciaException {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            throw new PersistenciaException("Error al crear el directorio de datos para la aplicación.", e);
        }
    }
}