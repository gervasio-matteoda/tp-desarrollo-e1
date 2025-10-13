package com.tp.repositorio;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.tp.excepciones.EntidadNoEncontradaException;
import com.tp.excepciones.PersistenciaException;
import com.tp.excepciones.ValidacionException;
import com.tp.modelo.Ciudad;
import com.tp.modelo.Direccion;
import com.tp.persistencia.IDireccionDAO;
import com.tp.servicios.GeoService;

public class DireccionArchivoDAO implements IDireccionDAO {

    private static DireccionArchivoDAO instancia;
    private GeoService geoService = GeoService.getInstancia();
    private final Path RUTA_ARCHIVO = Paths.get("data/direcciones.csv");
    private final String SEPARADOR = ",";

    private DireccionArchivoDAO() { }

    // Singleton
    public static DireccionArchivoDAO getInstancia() {
        if (instancia == null) {
            instancia = new DireccionArchivoDAO();
        }
        return instancia;
    }

    // Convierte una línea de texto a un objeto Direccion
    private Direccion mapToDireccion(String linea) throws PersistenciaException, ValidacionException {
        String[] datos = linea.split(SEPARADOR);
        try {
            Ciudad ciudad = geoService.obtenerCiudad(Integer.parseInt(datos[6]));
            return new Direccion.Builder()
                .id(datos[0])
                .calle(datos[1])
                .nroCalle(Integer.parseInt(datos[2]))
                .nroDepartamento(Integer.parseInt(datos[3]))
                .nroPiso(Integer.parseInt(datos[4]))
                .codigoPostal(Integer.parseInt(datos[5]))
                .ciudad(ciudad)
                .build();

        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al parsear la línea del archivo de direcciones: " + linea, e);
        }
        
    }

    // Convierte un objeto Direccion a una línea de texto para el archivo
    private String direccionToLinea(Direccion d) {
        Ciudad ciudad = d.getCiudad();
        return String.join(SEPARADOR,
                d.getId(),
                d.getCalle(), String.valueOf(d.getNroCalle()),
                String.valueOf(d.getNroDepartamento()), String.valueOf(d.getNroPiso()),
                String.valueOf(d.getCodigoPostal()),
                String.valueOf(ciudad.getId()));
    }

    // Escribe una lista de direcciones al archivo, sobreescribiendo el contenido
    private void escribirArchivo(List<Direccion> direcciones) throws PersistenciaException {
        
        try (BufferedWriter writer = Files.newBufferedWriter(RUTA_ARCHIVO)) {
            
            writer.write("id,calle,nroCalle,nroDepartamento,nroPiso,codigoPostal,idCiudad");
            writer.newLine();

            for (Direccion d : direcciones) {
                String linea = direccionToLinea(d);
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error de escritura en el archivo: " + RUTA_ARCHIVO, e);
        }
    }

    @Override
    public void create(Direccion direccion) throws PersistenciaException{
        List<Direccion> direcciones = findAll();
        Optional<Integer> maxId = direcciones.stream()
            .map(Direccion -> Integer.parseInt(Direccion.getId()))
            .max(Integer::compare);
        int nuevoId = maxId.isPresent() ? maxId.get() + 1 : 1;
        direccion.setId(Integer.toString(nuevoId));
        direcciones.add(direccion);
        escribirArchivo(direcciones);
    }

    @Override
    public void update(Direccion direccion) throws EntidadNoEncontradaException ,PersistenciaException {
        List<Direccion> direcciones = findAll();
        int index = -1;
        for (int i = 0; i < direcciones.size(); i++) {
            Direccion d = direcciones.get(i);
            if (d.getId().equals(direccion.getId())) {
                index = i;
                break;
            }
        }

        if (index == -1){
            throw new EntidadNoEncontradaException("No se encontró la direccion del huesped para actualizar");
        }
        direcciones.set(index, direccion);
        escribirArchivo(direcciones);
    }

    @Override
    public List<Direccion> findBy(Predicate<Direccion> filtro) throws PersistenciaException{
        return findAll().stream()
        .filter(filtro)
        .collect(Collectors.toList());
    }

    @Override
    public List<Direccion> findAll() throws PersistenciaException {
        
        try {
        
            if (!Files.exists(RUTA_ARCHIVO) || (Files.exists(RUTA_ARCHIVO) && Files.size(RUTA_ARCHIVO) == 0)) {
                return new ArrayList<>();
            }
            
            List<String> lineas = Files.readAllLines(RUTA_ARCHIVO);
            if (lineas.isEmpty()) {
                return new ArrayList<>(); // Si estaba vacio
            }

            return lineas.stream()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        try {
                            return mapToDireccion(line);
                        } catch (PersistenciaException e) {
                            throw new RuntimeException(e);
                        } catch (ValidacionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenciaException("Error de lectura en el archivo de direcciones: " + RUTA_ARCHIVO, e);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof PersistenciaException) {
                throw (PersistenciaException) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public void delete(Direccion direccion) throws EntidadNoEncontradaException, PersistenciaException {
        List<Direccion> direcciones = findAll();
        boolean eliminado = direcciones.removeIf(d -> d.getId().equals(direccion.getId()));
        if (!eliminado) {
            throw new EntidadNoEncontradaException("No se encontró la dirección para eliminar");
        }
        escribirArchivo(direcciones);
    }
}