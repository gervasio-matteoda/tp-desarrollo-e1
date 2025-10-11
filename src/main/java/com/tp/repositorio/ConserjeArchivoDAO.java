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
    
    private static ConserjeArchivoDAO instancia;
    private final Path RUTA_ARCHIVO = Paths.get("data/conserjes.csv");
    private final String SEPARADOR = ",";

    private ConserjeArchivoDAO() { }

    // Singleton
    public static ConserjeArchivoDAO getInstancia() {
        if (instancia == null) {
            instancia = new ConserjeArchivoDAO();
        }
        return instancia;
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
        
        try (BufferedWriter writer = Files.newBufferedWriter(RUTA_ARCHIVO)) {
            
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
    public void create(Conserje conserje) throws PersistenciaException{
        List<Conserje> conserjes = findAll();
        conserjes.add(conserje);
        escribirArchivo(conserjes);
    }
    

    @Override
    public void delete(List<Conserje> conserjes) throws PersistenciaException {
        escribirArchivo(conserjes);
    }

    @Override
    public List<Conserje> findAll() throws PersistenciaException {
        
        try {
        
            if (!Files.exists(RUTA_ARCHIVO) || (Files.exists(RUTA_ARCHIVO) && Files.size(RUTA_ARCHIVO) == 0)) {
                return new ArrayList<>();
            }
            
            List<String> lineas = Files.readAllLines(RUTA_ARCHIVO);
            
            if (lineas.isEmpty()) {
                return new ArrayList<>(); 
            }

            return lineas.stream()
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