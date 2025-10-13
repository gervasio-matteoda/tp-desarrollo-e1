package com.tp.repositorio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.tp.excepciones.PersistenciaException;
import com.tp.modelo.TipoDocumento;
import com.tp.persistencia.ITipoDocumento;

public class TipoDocumentoArchivoDAO implements ITipoDocumento {

    private static TipoDocumentoArchivoDAO instancia;
    private final Path RUTA_ARCHIVO = Paths.get("data/tipos_documento.txt");
    private final String SEPARADOR = ",";
    
    private TipoDocumentoArchivoDAO() { }

    //Singleton
    public static TipoDocumentoArchivoDAO getInstancia() {
        if (instancia == null) {
            instancia = new TipoDocumentoArchivoDAO();
        }
        return instancia;
    }

    //Convierte una línea de texto a un objeto TipoDocumento
    private TipoDocumento mapToTipoDocumento(String linea) throws PersistenciaException {
        String[] datos = linea.split(SEPARADOR, -1);
        try {
            return new TipoDocumento(TipoDocumento.tipoDocumentoEnum.valueOf(datos[0]), datos[1]);
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException("Error al parsear la línea del archivo de tipos de documento: " + linea, e);
        }
    }

    @Override
    public List<TipoDocumento> findBy(Predicate<TipoDocumento> filtro) throws PersistenciaException{
        return findAll().stream()
                .filter(filtro)
                .collect(Collectors.toList());
    }

     @Override
    public List<TipoDocumento> findAll() throws PersistenciaException {
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
                        return mapToTipoDocumento(line);
                    } catch (PersistenciaException e) {
                        throw new RuntimeException(new PersistenciaException("Error al parsear la línea del archivo de tipos de documento: " + line, e));
                    }
                })
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenciaException("Error de lectura en el archivo de tipos de documento: " + RUTA_ARCHIVO, e);
        } catch (RuntimeException e) {
            if(e.getCause() instanceof PersistenciaException) {
                throw (PersistenciaException) e.getCause();
            }
            throw e;
        }
    }

}
