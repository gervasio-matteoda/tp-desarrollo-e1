package com.tp.repositorio;

import com.tp.modelo.Ciudad;
import com.tp.modelo.Direccion;
import com.tp.modelo.Huesped;
import com.tp.modelo.Pais;
import com.tp.modelo.Provincia;
import com.tp.modelo.TipoDocumento;
import com.tp.persistencia.IHuespedDAO;
import com.tp.excepciones.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HuespedArchivoDAO implements IHuespedDAO{

    private final String RUTA_ARCHIVO = "data/huespedes.csv";
    private final String SEPARADOR = ",";
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public HuespedArchivoDAO() throws PersistenciaException {
        try {
            Path path = Paths.get("data");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al crear el directorio de datos para huéspedes.", e);
        }
    }

    // Convierte un objeto Huesped a una línea de texto para el archivo
    private String huespedToLinea(Huesped h) {
        String fechaNacStr = (h.getFechaDeNacimiento() != null) 
            ? h.getFechaDeNacimiento().format(DATE_FORMATTER) : "";
        Direccion dir = h.getDireccion();
        Ciudad ciu = dir.getCiudad();
        Provincia pro = ciu.getProvincia();
        Pais pa = pro.getPais();

        return String.join(SEPARADOR,
                h.getNombre(), h.getApellido(), h.getCuit(),
                h.getTipoDocumento().getTipo().name(), h.getNroDocumento(),
                fechaNacStr, h.getEmail(), h.getTelefono(), h.getOcupacion(),
                dir.getCalle(), String.valueOf(dir.getNroCalle()),
                String.valueOf(dir.getNroDepartamento()), String.valueOf(dir.getCodigoPostal()),
                ciu.getNombre(), pro.getNombre(), pa.getNombre()
        );
    }

    // Convierte una línea de texto a un objeto Conserje
    private Huesped mapToHuesped(String linea) throws PersistenciaException {
        String[] datos = linea.split(SEPARADOR, -1); // -1 para incluir campos vacíos al final

        try {
            LocalDate fechaNac = !datos[5].isEmpty() ? LocalDate.parse(datos[5], DATE_FORMATTER) : null;

            // Reconstruimos los objetos anidados desde el nivel más bajo
            Pais pais = new Pais(datos[15], 0); // El ID es irrelevante al leer, se usa el nombre
            Provincia provincia = new Provincia(datos[14], 0, pais);
            Ciudad ciudad = new Ciudad(datos[13], 0, provincia);

            Direccion direccion = new Direccion.Builder()
                .calle(datos[9])
                .nroCalle(Integer.parseInt(datos[10]))
                .nroDepartamento(Integer.parseInt(datos[11]))
                .codigoPostal(Integer.parseInt(datos[12]))
                .ciudad(ciudad)
                .build();
            
            TipoDocumento tipoDoc = new TipoDocumento(TipoDocumento.tipoDocumentoEnum.valueOf(datos[3]), "");

            return new Huesped.Builder()
                .nombre(datos[0]).apellido(datos[1]).cuit(datos[2])
                .tipoDocumento(tipoDoc).nroDocumento(datos[4])
                .fechaDeNacimiento(fechaNac)
                .email(datos[6]).telefono(datos[7]).ocupacion(datos[8])
                .direccion(direccion)
                .build();
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al parsear la línea del archivo de huéspedes: " + linea, e);
        }
    }

    // Escribe una lista de huespedes al archivo, sobreescribiendo el contenido
    private void escribirArchivo(List<Huesped> huespedes) throws PersistenciaException {
        Path path = Paths.get(RUTA_ARCHIVO);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Cabecera del archivo CSV
            writer.write("nombre,apellido,cuit,tipoDoc,nroDoc,fechaNac,email,telefono,ocupacion,calle,nroCalle,depto,codPostal,ciudad,provincia,pais");
            writer.newLine();

            for (Huesped h : huespedes) {
                writer.write(huespedToLinea(h));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error de escritura en el archivo de huéspedes: " + RUTA_ARCHIVO, e);
        }
    }

    @Override
    public void create(Huesped huesped) throws EntidadDuplicadaException, PersistenciaException {
        
        List<Huesped> huespedes = findAll();
        boolean duplicado = huespedes.stream().anyMatch(h ->
                h.getNroDocumento().equals(huesped.getNroDocumento()) &&
                h.getTipoDocumento().getTipo().equals(huesped.getTipoDocumento().getTipo())
        );

        if (duplicado) {
            throw new EntidadDuplicadaException("Ya existe un huésped con el mismo tipo y número de documento.");
        }
        huespedes.add(huesped);
        escribirArchivo(huespedes);
    }

    @Override
    public void update(Huesped huesped) throws EntidadNoEncontradaException, PersistenciaException {
        
        List<Huesped> huespedes = findAll();
        int index = -1;
        for (int i = 0; i < huespedes.size(); i++) {
            Huesped h = huespedes.get(i);
            if (h.getNroDocumento().equals(huesped.getNroDocumento()) &&
                h.getTipoDocumento().getTipo().equals(huesped.getTipoDocumento().getTipo())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new EntidadNoEncontradaException("No se encontró un huésped para actualizar con el documento proporcionado.");
        }
        huespedes.set(index, huesped);
        escribirArchivo(huespedes);
    }

    @Override
    public List<Huesped> findBy(Predicate<Huesped> filtro) throws PersistenciaException {
        return findAll().stream()
                .filter(filtro)
                .collect(Collectors.toList());
    }

    @Override
    public List<Huesped> findAll() throws PersistenciaException {
        
        Path path = Paths.get(RUTA_ARCHIVO);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            return Files.lines(path)
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        try {
                            return mapToHuesped(line);
                        } catch (PersistenciaException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenciaException("Error de lectura en el archivo de huéspedes: " + RUTA_ARCHIVO, e);
        } catch (RuntimeException e) {
            if(e.getCause() instanceof PersistenciaException) {
                throw (PersistenciaException) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public void delete(Huesped huesped) throws EntidadNoEncontradaException, PersistenciaException {
        
        List<Huesped> huespedes = findAll();
        boolean eliminado = huespedes.removeIf(h ->
                h.getNroDocumento().equals(huesped.getNroDocumento()) &&
                h.getTipoDocumento().getTipo().equals(huesped.getTipoDocumento().getTipo())
        );

        if (!eliminado) {
            throw new EntidadNoEncontradaException("No se encontró un huésped para eliminar con el documento proporcionado.");
        }
        escribirArchivo(huespedes);
    }
}
