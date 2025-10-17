package com.tp.repositorio;

import com.tp.excepciones.PersistenciaException;
import com.tp.modelo.Huesped;
import com.tp.modelo.Ocupacion;
import com.tp.persistencia.IHuespedDAO;
import com.tp.persistencia.IOcupacionDAO;
import com.tp.utils.DAOFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OcupacionArchivoDAO implements IOcupacionDAO {

    private static OcupacionArchivoDAO instancia;
    private final Path RUTA_ARCHIVO = Paths.get("data/ocupaciones.csv");
    private final String SEPARADOR = ",";
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private OcupacionArchivoDAO() { }

    public static OcupacionArchivoDAO getInstancia() {
        if (instancia == null) {
            instancia = new OcupacionArchivoDAO();
        }
        return instancia;
    }
    
    private Huesped findHuespedById(String id) throws PersistenciaException {
        String[] parts = id.split(":");
        if (parts.length != 2) return null;
        
        IHuespedDAO huespedDAO = DAOFactory.getHuespedDAO();
        return huespedDAO.findBy(h -> h.getTipoDocumento().getTipo().name().equals(parts[0]) && h.getNroDocumento().equals(parts[1]))
            .stream().findFirst().orElse(null);
    }

    private Ocupacion mapToOcupacion(String linea) throws PersistenciaException {
        String[] datos = linea.split(SEPARADOR);
        
        Huesped responsable = findHuespedById(datos[3]);
        
        return new Ocupacion.Builder()
            .id(datos[0])
            .fechaIngreso(LocalDate.parse(datos[1], DATE_FORMATTER))
            .fechaEgreso(LocalDate.parse(datos[2], DATE_FORMATTER))
            .responsable(responsable)
            .acompanantes(new ArrayList<>())
            .build();
    }

    @Override
    public List<Ocupacion> findAll() throws PersistenciaException {
        if (!Files.exists(RUTA_ARCHIVO)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(RUTA_ARCHIVO).stream()
                .skip(1)
                .map(linea -> {
                    try {
                        return mapToOcupacion(linea);
                    } catch (PersistenciaException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer el archivo de ocupaciones.", e);
        }
    }

    @Override
    public List<Ocupacion> findBy(Predicate<Ocupacion> filtro) throws PersistenciaException {
        return findAll().stream().filter(filtro).collect(Collectors.toList());
    }
}