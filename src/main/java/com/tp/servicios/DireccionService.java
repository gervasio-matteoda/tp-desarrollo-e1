package com.tp.servicios;


import com.tp.dto.DireccionDTO;
import com.tp.excepciones.*;
import com.tp.modelo.Ciudad;
import com.tp.modelo.Direccion;
import com.tp.persistencia.IDireccionDAO;
import com.tp.utils.DAOFactory;

public class DireccionService {
    private static DireccionService instancia;
    private GeoService geoService = GeoService.getInstancia();
    private IDireccionDAO direccionDAO;

    private DireccionService() {
        this.direccionDAO = DAOFactory.getDireccionDAO();
    }

    public static DireccionService getInstancia() {
        if (instancia == null) {
            instancia = new DireccionService();
        }
        return instancia;
    }

    public DireccionDTO registrarDireccion(DireccionDTO direccionDTO) throws NegocioException{
        try {
            validarDireccion(direccionDTO);
            Direccion direccion = mapDTOToDireccion(direccionDTO);
            direccion = direccionDAO.create(direccion);
            return mapDireccionToDTO(direccion);
        } catch (ValidacionException | PersistenciaException e) {
            throw new NegocioException("No se pudo registrar la direccion.", e);
        }
        
    }

    public void eliminarDireccion(DireccionDTO direccionDTO) throws NegocioException, ValidacionException, PersistenciaException {
        if(direccionDTO == null || direccionDTO.getId().isBlank()) {
            throw new ValidacionException("La direccion a eliminar no puede ser nula o tener un id vacio");
        }
        try {
            direccionDAO.delete(mapDTOToDireccion(direccionDTO));
        } catch (EntidadNoEncontradaException e) {
            throw new NegocioException("No se pudo eliminar la direccion. " + e.getMessage(), e);
        }
    }

    public void modificarDireccion(DireccionDTO direccionDTO) throws NegocioException {
        try {
            validarDireccion(direccionDTO);
            Direccion direccion = mapDTOToDireccion(direccionDTO);
            direccionDAO.update(direccion);
        } catch (ValidacionException | PersistenciaException e) {
            throw new NegocioException("No se pudo modificar la direccion.", e);
        }
    }

    public Direccion obtenerDireccion(String id) throws NegocioException, ValidacionException {
        if (id == null || id.isBlank()) {
            throw new ValidacionException("El id de la direccion no puede estar vacío.");
        }
        try {
            return direccionDAO.findBy(d -> d.getId().equals(id))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntidadNoEncontradaException("No existe una dirección con el id: " + id));
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al intentar obtener la dirección.", e);
        }
    }

    private void validarDireccion(DireccionDTO direccionDTO) throws ValidacionException {
        if (direccionDTO == null) {
            throw new ValidacionException("La dirección no puede ser nula.");
        }
        if (direccionDTO.getCalle() == null || direccionDTO.getCalle().isBlank() || direccionDTO.getCalle().length() > 64) {
            throw new ValidacionException("La calle del huesped es invalida. El campo se encuentra vacío o excede 64 caracteres.");
        }
        if (direccionDTO.getNroCalle() <= 0) {
            throw new ValidacionException("El número de calle debe es invalido. Debe ser un entero positivo.");
        }
        if (direccionDTO.getNroDepartamento() < 0) {
            throw new ValidacionException("El número de departamento es invalido. No puede ser negativo.");
        }
        if (direccionDTO.getNroPiso() < 0) {
            throw new ValidacionException("El número de piso es invalido. No puede ser negativo.");
        }
        if (direccionDTO.getCodigoPostal() <= 0) {
            throw new ValidacionException("El código postal es invalido. Debe ser un entero positivo.");
        }
        
    }

    public Direccion mapDTOToDireccion(DireccionDTO direccionDTO) throws ValidacionException, EntidadNoEncontradaException, PersistenciaException {
        try{
        Ciudad ciudad = geoService.obtenerCiudad(direccionDTO.getCiudad(), direccionDTO.getProvincia(), direccionDTO.getPais());
        return new Direccion.Builder()
            .id(direccionDTO.getId())
            .calle(direccionDTO.getCalle())
            .nroCalle(direccionDTO.getNroCalle())
            .nroDepartamento(direccionDTO.getNroDepartamento())
            .nroPiso(direccionDTO.getNroPiso())
            .codigoPostal(direccionDTO.getCodigoPostal())
            .ciudad(ciudad)
            .build();
        } catch (PersistenciaException e) {
            throw new PersistenciaException("Error al intentar mapear el DTO a Dirección", e);
        }
    }

   public DireccionDTO mapDireccionToDTO(Direccion direccion) throws NegocioException {
        
        if (direccion.getCalle() == null || direccion.getCalle().isBlank()) {
             try {
                direccion = obtenerDireccion(direccion.getId());
            } catch (Exception e) {
                throw new NegocioException("No se pudo cargar la información base de la dirección con ID: " + direccion.getId(), e);
            }
        }
       
        try {
            geoService.completarDatosGeograficos(direccion.getCiudad());
        } catch (PersistenciaException e) {
            throw new NegocioException("Error de integridad de datos. No se pudieron cargar los datos geográficos para la dirección con ID " + direccion.getId(), e);
        }

        return new DireccionDTO.Builder()
            .id(direccion.getId())
            .calle(direccion.getCalle())
            .nroCalle(direccion.getNroCalle())
            .nroDepartamento(direccion.getNroDepartamento())
            .nroPiso(direccion.getNroPiso())
            .codigoPostal(direccion.getCodigoPostal())
            .ciudad(direccion.getCiudad().getNombre())
            .provincia(direccion.getCiudad().getProvincia().getNombre())
            .pais(direccion.getCiudad().getProvincia().getPais().getNombre())
            .build();
    }
}
