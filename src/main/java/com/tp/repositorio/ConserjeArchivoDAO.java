package com.tp.repositorio;

import com.tp.excepciones.*;
import com.tp.modelo.Conserje;
import com.tp.persistencia.IConserjeDAO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConserjeArchivoDAO implements IConserjeDAO {

    private final String RUTA_ARCHIVO = "data/conserjes.csv";
    private final String SEPARADOR = ",";

    public ConserjeArchivoDAO() throws PersistenciaException {
        try {
            Path path = Paths.get("data");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al crear el directorio de datos.", e);
        }
    }

    // Convierte una línea de texto a un objeto Conserje
    private Conserje mapToConserje(String linea) {
        String[] datos = linea.split(SEPARADOR);
        return new Conserje.Builder()
                .usuario(datos[0])
                .contrasenia(datos[1])
                .nombre(datos[2])
                .apellido(datos[3])
                .build();
    }
    
    // Escribe una lista de conserjes al archivo, sobreescribiendo el contenido
    private void escribirArchivo(List<Conserje> conserjes) throws PersistenciaException {
        
        Path path = Paths.get(RUTA_ARCHIVO);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            
            writer.write("usuario,contrasenia,nombre,apellido");
            writer.newLine();

            for (Conserje c : conserjes) {
                String linea = String.join(SEPARADOR,
                        c.getUsuario(), c.getContrasenia(),
                        c.getNombre(), c.getApellido());
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error de escritura en el archivo: " + RUTA_ARCHIVO, e);
        }
    }


    @Override
    public void create(Conserje conserje) throws EntidadDuplicadaException, PersistenciaException {
        
        List<Conserje> conserjes = findAll();
        boolean duplicado = conserjes.stream().anyMatch(c -> c.getUsuario().equals(conserje.getUsuario()));
        if (duplicado) {
            throw new EntidadDuplicadaException("Ya existe un conserje con el usuario: " + conserje.getUsuario());
        }
        conserjes.add(conserje);
        escribirArchivo(conserjes);
    }
    

    @Override
    public void delete(Conserje conserje) throws EntidadNoEncontradaException, PersistenciaException {
        
        List<Conserje> conserjes = findAll();
        boolean eliminado = conserjes.removeIf(c -> c.getUsuario().equals(conserje.getUsuario()));

        if (!eliminado) {
            throw new EntidadNoEncontradaException("No se encontró un conserje para eliminar con el usuario: " + conserje.getUsuario());
        }
        escribirArchivo(conserjes); // Dejamos que la PersistenciaException suba.
    }

    @Override
    public List<Conserje> findAll() throws PersistenciaException {
        
        Path path = Paths.get(RUTA_ARCHIVO);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            return Files.lines(path)
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(this::mapToConserje)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenciaException("Error de lectura en el archivo: " + RUTA_ARCHIVO, e);
        }
    }

    @Override
    public List<Conserje> findBy(Predicate<Conserje> filtro) throws PersistenciaException {
        return findAll().stream()
                .filter(filtro)
                .collect(Collectors.toList());
    }
}