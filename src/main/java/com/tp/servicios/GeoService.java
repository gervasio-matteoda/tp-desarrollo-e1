package com.tp.servicios;

import com.tp.excepciones.EntidadNoEncontradaException;
import com.tp.excepciones.PersistenciaException;
import com.tp.excepciones.ValidacionException;
import com.tp.modelo.Ciudad;
import com.tp.modelo.Pais;
import com.tp.modelo.Provincia;
import com.tp.persistencia.ICiudadDAO;
import com.tp.persistencia.IPaisDAO;
import com.tp.persistencia.IProvinciaDAO;
import com.tp.utils.DAOFactory;
import java.util.List;
import java.util.stream.Collectors;

public class GeoService {

    private static GeoService instancia;
    private IPaisDAO paisDAO;
    private IProvinciaDAO provinciaDAO;
    private ICiudadDAO ciudadDAO;

    private GeoService() {
        this.paisDAO = DAOFactory.getPaisDAO();
        this.provinciaDAO = DAOFactory.getProvinciaDAO();
        this.ciudadDAO = DAOFactory.getCiudadDAO();
    }

    public static GeoService getInstancia() {
        if (instancia == null) {
            instancia = new GeoService();
        }
        return instancia;
    }

    private Ciudad validarCiudad(String nombreCiudad) throws ValidacionException, PersistenciaException, EntidadNoEncontradaException{
        if (nombreCiudad.isBlank()){
            throw new ValidacionException("El nombre de la ciudad no puede estar vacío.");
        }
        try {
            Ciudad ciudad = ciudadDAO.findBy(c -> c.getNombre().equalsIgnoreCase(nombreCiudad))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntidadNoEncontradaException("No existe la ciudad: " + nombreCiudad));
            return ciudad;
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Error al intentar validar la ciudad", e);
        }
    }

    private Provincia validarProvincia(String nombreProvincia) throws ValidacionException, PersistenciaException, EntidadNoEncontradaException{
        if (nombreProvincia.isBlank()){
            throw new ValidacionException("El nombre de la provincia no puede estar vacío.");
        }
        try {
            Provincia provincia = provinciaDAO.findBy(p -> p.getNombre().equalsIgnoreCase(nombreProvincia))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntidadNoEncontradaException("No existe la provincia: " + nombreProvincia));
            return provincia;
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Error al intentar validar la provincia", e);
        }
    }

    private Pais validarPais(String nombrePais) throws ValidacionException, PersistenciaException, EntidadNoEncontradaException {
        if (nombrePais.isBlank()){
            throw new ValidacionException("El nombre del país no puede estar vacío.");
        }
        try {
            Pais pais = paisDAO.findBy(p -> p.getNombre().equalsIgnoreCase(nombrePais))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntidadNoEncontradaException("No existe el país: " + nombrePais));
            return pais;
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Error al intentar validar el país", e);
        }
    }

    public Ciudad obtenerCiudad(String nombreCiudad, String nombreProvincia, String nombrePais) throws ValidacionException, PersistenciaException, EntidadNoEncontradaException {
        try {
            Pais pais = validarPais(nombrePais);
            Provincia provincia = validarProvincia(nombreProvincia);
            if (provincia.getPais().getId() != pais.getId()) {
                throw new ValidacionException("La provincia " + nombreProvincia + " no pertenece al país " + nombrePais + ".");
            }
            Ciudad ciudad = validarCiudad(nombreCiudad);
            if (ciudad.getProvincia().getId() != provincia.getId()) {
                throw new ValidacionException("La ciudad " + nombreCiudad + " no pertenece a la provincia " + nombreProvincia + ".");
            }
            return ciudad;
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Error al intentar obtener la ciudad", e);
        }    
    }

    public Ciudad obtenerCiudad(int idCiudad) throws ValidacionException, PersistenciaException, EntidadNoEncontradaException {
        if (idCiudad <= 0) {
            throw new ValidacionException("El ID de la ciudad debe ser un entero positivo.");
        }
        try {
            Ciudad ciudad = ciudadDAO.findBy(c -> c.getId() == idCiudad)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe una ciudad con ID: " + idCiudad));
            return ciudad;
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Error al intentar obtener la ciudad por ID", e);
        }
    }

    public Provincia obtenerProvincia(int idProvincia) throws ValidacionException, PersistenciaException, EntidadNoEncontradaException {
        if (idProvincia <= 0) {
            throw new ValidacionException("El ID de la provincia debe ser un entero positivo.");
        }
        try {
            Provincia provincia = provinciaDAO.findBy(p -> p.getId() == idProvincia)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe una provincia con ID: " + idProvincia));
            return provincia;
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Error al intentar obtener la provincia por ID", e);
        }
    }

    public Pais obtenerPais(int idPais) throws ValidacionException, PersistenciaException, EntidadNoEncontradaException {
        if (idPais <= 0) {
            throw new ValidacionException("El ID del país debe ser un entero positivo.");
        }
        try {
            Pais pais = paisDAO.findBy(p -> p.getId() == idPais)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe un país con ID: " + idPais));
            return pais;
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Error al intentar obtener el país por ID", e);
        }
    }

    public void completarDatosGeograficos(Ciudad ciudad) throws PersistenciaException, EntidadNoEncontradaException {
        if (ciudad == null || ciudad.getId() <= 0) return;

        Ciudad ciudadCompleta = ciudadDAO.findBy(c -> c.getId() == ciudad.getId())
            .stream()
            .findFirst()
            .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró la ciudad con ID: " + ciudad.getId()));
        ciudad.setNombre(ciudadCompleta.getNombre());

        int idProvincia = ciudadCompleta.getProvincia().getId();
        Provincia provincia = provinciaDAO.findBy(p -> p.getId() == idProvincia)
            .stream()
            .findFirst()
            .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró la provincia con ID: " + idProvincia));
        
        int idPais = provincia.getPais().getId();
        Pais pais = paisDAO.findBy(p -> p.getId() == idPais)
            .stream()
            .findFirst()
            .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el país con ID: " + idPais));
        provincia.setPais(pais);
        ciudad.setProvincia(provincia);
    }

    public List<String> obtenerPaises() throws PersistenciaException {
        try {
            return paisDAO.findAll().stream()
                .map(Pais::getNombre)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener la lista de países.", e);
        }
    }

    public List<String> obtenerProvinciasPorPais(String nombrePais) throws PersistenciaException, EntidadNoEncontradaException {
        try {
            // Primero, encontramos el país para obtener su ID
            Pais pais = paisDAO.findBy(p -> p.getNombre().equalsIgnoreCase(nombrePais))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró el país: " + nombrePais));

            // Luego, filtramos las provincias por el ID del país
            return provinciaDAO.findAll().stream()
                .filter(provincia -> provincia.getPais().getId() == pais.getId())
                .map(Provincia::getNombre)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener la lista de provincias.", e);
        }
    }

    public List<String> obtenerCiudadesPorProvincia(String nombreProvincia) throws PersistenciaException, EntidadNoEncontradaException {
        try {
            // Encontramos la provincia para obtener su ID
            Provincia provincia = provinciaDAO.findBy(p -> p.getNombre().equalsIgnoreCase(nombreProvincia))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró la provincia: " + nombreProvincia));

            // Filtramos las ciudades por el ID de la provincia
            return ciudadDAO.findAll().stream()
                .filter(ciudad -> ciudad.getProvincia().getId() == provincia.getId())
                .map(Ciudad::getNombre)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener la lista de ciudades.", e);
        }
    }
}
