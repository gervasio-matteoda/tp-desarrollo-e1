package com.tp.repositorio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.tp.excepciones.*;
import com.tp.modelo.Pais;
import com.tp.persistencia.IPaisDAO;

public class PaisArchivoDAO implements IPaisDAO {

    private static PaisArchivoDAO instancia;
    private final Path RUTA_ARCHIVO = Paths.get("data/paises.csv");
    private final String SEPARADOR = ",";

    private PaisArchivoDAO() {}

    // Singleton
    public static PaisArchivoDAO getInstancia() {
        if (instancia == null) {
            instancia = new PaisArchivoDAO();
        }
        return instancia;
    }

    // Convierte una linea de texto a un objeto Pais
    private Pais mapToPais(String linea) throws PersistenciaException {
        String[] datos = linea.split(SEPARADOR);
        try {
            return new Pais.Builder()
                .id(Integer.parseInt(datos[0]))
                .nombre(datos[1])
                .build();
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al parsear la línea del archivo de paises: " + linea, e);
        }
    }
    
    @Override
    public List<Pais> findBy(Predicate<Pais> filtro) throws PersistenciaException {
        return findAll().stream()
            .filter(filtro)
            .collect(Collectors.toList());
    }

    @Override
    public List<Pais> findAll() throws PersistenciaException {
        try {
            if (!Files.exists(RUTA_ARCHIVO)) {
                Files.createFile(RUTA_ARCHIVO);
                return new ArrayList<>();
            }
            List<String> lineas = Files.readAllLines(RUTA_ARCHIVO);
            if (lineas.isEmpty()) {
                return new ArrayList<>();
            }
            return lineas.stream()
                .skip(1) // Saltar la cabecera
                .filter(line -> !line.isBlank())
                .map(line -> {
                    try {
                        return mapToPais(line);
                    } catch (PersistenciaException e) {
                       throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenciaException("Error de lectura en el archivo: " + RUTA_ARCHIVO, e);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof PersistenciaException) {
                throw (PersistenciaException) e.getCause();
            }
            throw e;
        }
    }
}
