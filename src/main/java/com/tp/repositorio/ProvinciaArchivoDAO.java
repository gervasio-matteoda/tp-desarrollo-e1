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
import com.tp.modelo.Provincia;
import com.tp.persistencia.IProvinciaDAO;
import com.tp.servicios.GeoService;

public class ProvinciaArchivoDAO implements IProvinciaDAO{

    private static ProvinciaArchivoDAO instancia;
    private final Path RUTA_ARCHIVO = Paths.get("data/provincias.csv");
    private final String SEPARADOR = ",";

    private ProvinciaArchivoDAO() { }

    // Singleton
    public static ProvinciaArchivoDAO getInstancia() {
        if (instancia == null) {
            instancia = new ProvinciaArchivoDAO();
        }
        return instancia;
    }

    // Convierte una linea de texto a un objeto Provincia
    private Provincia mapToProvincia(String linea) throws PersistenciaException, ValidacionException {
        GeoService geoService = GeoService.getInstancia();
        String[] datos = linea.split(SEPARADOR);
        try {
            Pais pais = geoService.obtenerPais(Integer.parseInt(datos[2]));
            return new Provincia.Builder()
                .id(Integer.parseInt(datos[0]))
                .nombre(datos[1])
                .pais(pais)
                .build();
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al parsear la línea del archivo de provincias: " + linea, e);
        } 
    }

    @Override
    public List<Provincia> findBy(Predicate<Provincia> filtro) throws PersistenciaException {
        return findAll().stream()
            .filter(filtro)
            .collect(Collectors.toList());
    }

    @Override
    public List<Provincia> findAll() throws PersistenciaException {
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
                .skip(1)
                .filter(line -> !line.isBlank())
                .map(line -> {
                    try {
                        return mapToProvincia(line);
                    } catch (PersistenciaException e) {
                        throw new RuntimeException(e);
                    } catch (ValidacionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenciaException("Error de lectura en el archivo de provincias: " + RUTA_ARCHIVO, e);
        } catch (RuntimeException e) {
            if(e.getCause() instanceof PersistenciaException) {
                throw (PersistenciaException) e.getCause();
            }
            throw e;
        }
    }
    
}
