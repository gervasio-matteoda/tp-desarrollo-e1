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
import com.tp.modelo.Ciudad;
import com.tp.modelo.Provincia;
import com.tp.persistencia.ICiudadDAO;
import com.tp.servicios.GeoService;

public class CiudadArchivoDAO implements ICiudadDAO {

    private static CiudadArchivoDAO instancia;
    private final Path RUTA_ARCHIVO = Paths.get("data/ciudades.csv");
    private final String SEPARADOR = ",";

    private CiudadArchivoDAO() { }

    //Singleton
    public static CiudadArchivoDAO getInstancia() {
        if (instancia == null) {
            instancia = new CiudadArchivoDAO();
        }
        return instancia;
    }

    //Convierte una linea de texto a un objeto Ciudad
    private Ciudad mapToCiudad(String linea) throws PersistenciaException, ValidacionException {
        GeoService geoService = GeoService.getInstancia();
        String[] datos = linea.split(SEPARADOR);
        try {
            Provincia provincia = geoService.obtenerProvincia(Integer.parseInt(datos[2])); 
            return new Ciudad.Builder()
                .id(Integer.parseInt(datos[0]))
                .nombre(datos[1])
                .provincia(provincia)
                .build();
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al parsear la línea del archivo de ciudades: " + linea, e);
        } 
    }

    @Override
    public List<Ciudad> findBy(Predicate<Ciudad> filtro) throws PersistenciaException {
        return findAll().stream()
            .filter(filtro)
            .collect(Collectors.toList());
    }

    @Override
    public List<Ciudad> findAll() throws PersistenciaException {
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
                        return mapToCiudad(line);
                    } catch (PersistenciaException e) {
                        throw new RuntimeException(e);
                    } catch (ValidacionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenciaException("Error de lectura en el archivo de ciudades: " + RUTA_ARCHIVO, e);
        } catch (RuntimeException e) {
            if(e.getCause() instanceof PersistenciaException) {
                throw (PersistenciaException) e.getCause();
            }
            throw e;
        }
    }
}