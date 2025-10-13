package com.tp.servicios;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.tp.dto.DireccionDTO;
import com.tp.dto.HuespedDTO;
import com.tp.excepciones.*;
import com.tp.modelo.Huesped;
import com.tp.modelo.TipoDocumento;
import com.tp.persistencia.IHuespedDAO;
import com.tp.persistencia.ITipoDocumento;
import com.tp.utils.DAOFactory;

public class HuespedService {
    private static HuespedService instancia;
    private IHuespedDAO huespedDAO;
    private ITipoDocumento tipoDocumentoDAO;
    private DireccionService direccionService = DireccionService.getInstancia();

    private HuespedService() {
        this.huespedDAO = DAOFactory.getHuespedDAO();
        this.tipoDocumentoDAO = DAOFactory.gTipoDocumentoDAO();
    }
    
    public static HuespedService getInstancia() {
        if (instancia == null) {
            instancia = new HuespedService();
        }
        return instancia;
    }

    public void registrarHuesped(HuespedDTO huespedDTO) throws NegocioException, PersistenciaException{
            Huesped huesped;
        try {
            validarHuesped(huespedDTO);
            existeHuesped(huespedDTO);
            Huesped h = mapDTOToHuesped(huespedDTO);
            DireccionDTO direccion = direccionService.registrarDireccion(huespedDTO.getDireccion());
            h.getDireccion().setId(direccion.getId());
            huesped = h;
        } catch (ValidacionException | PersistenciaException e) {
            throw new NegocioException("No se pudo registrar la direccion o huesped invalido", e);
        }
        try {
            huespedDAO.create(huesped);
        } catch (PersistenciaException e) {
            direccionService.eliminarDireccion(huespedDTO.getDireccion());
            throw new NegocioException("No se pudo registrar el huesped.", e);
        }
        
    }

    public void eliminarHuesped(HuespedDTO huespedDTO) throws NegocioException, ValidacionException, PersistenciaException {
        if(huespedDTO == null || huespedDTO.getNroDocumento().isBlank() || huespedDTO.getTipoDocumento().isBlank()) {
            throw new ValidacionException("El huesped a eliminar no puede ser nulo o tener el numero de documento o tipo de documento vacio");
        }
        try {
            validarHuesped(huespedDTO);
            Huesped huesped = huespedDAO.findBy(h -> 
                h.getNroDocumento().equalsIgnoreCase(huespedDTO.getNroDocumento()) && 
                h.getTipoDocumento().getTipo().toString().equalsIgnoreCase(huespedDTO.getTipoDocumento())
            )
            .stream()
            .findFirst()
            .orElseThrow(() -> new EntidadNoEncontradaException("No existe un huesped con el tipo y numero de documento proporcionado."));
            
            
            huespedDAO.delete(huesped);
            direccionService.eliminarDireccion(huespedDTO.getDireccion());
        } catch (EntidadNoEncontradaException e) {
            throw new NegocioException("No se pudo eliminar el huesped. " + e.getMessage(), e);
        }
    }

    public void modificarHuesped(HuespedDTO huespedDTO) throws NegocioException, ValidacionException, PersistenciaException {
        if(huespedDTO == null || huespedDTO.getNroDocumento().isBlank() || huespedDTO.getTipoDocumento().isBlank()) {
            throw new ValidacionException("El huesped a modificar no puede ser nulo o tener el numero de documento o tipo de documento vacio");
        }
        try {
            validarHuesped(huespedDTO);
            Huesped huesped = mapDTOToHuesped(huespedDTO);
            direccionService.modificarDireccion(huespedDTO.getDireccion());
            huespedDAO.update(huesped);
        } catch (ValidacionException | PersistenciaException e) {
            throw new NegocioException("No se pudo modificar el huesped.", e);
        }
    }

    public List<HuespedDTO> buscarHuspedes(String nombre, String apellido, String nroDocumento,String tipoDoc) throws NegocioException, ValidacionException {
        
        Predicate<Huesped> filtro = h -> 
            (nombre.isEmpty() || h.getNombre().toLowerCase().contains(nombre.toLowerCase())) &&
            (apellido.isEmpty() || h.getApellido().toLowerCase().contains(apellido.toLowerCase())) &&
            (tipoDoc.isEmpty() || h.getTipoDocumento().getTipo().name().contains(tipoDoc)) &&
            (nroDocumento.isEmpty() || h.getNroDocumento().contains(tipoDoc));
        try {
            List<Huesped> huespedes = huespedDAO.findBy(filtro);
            return huespedes.stream()
                            .map(h -> mapHuespedToDTO(h))
                            .collect(Collectors.toList());
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al buscar huespedes.", e);
        }

    }

    private void validarHuesped(HuespedDTO huespedDTO) throws ValidacionException, PersistenciaException {
        if (huespedDTO.getNombre() == null || huespedDTO.getNombre().isBlank() || huespedDTO.getNombre().length() > 64) {
            throw new ValidacionException("El nombre del huesped es inválido. El campo se encuentra vacío o excede 64 caracteres.");
        }
        if (huespedDTO.getApellido() == null || huespedDTO.getApellido().isBlank() || huespedDTO.getApellido().length() > 64) {
            throw new ValidacionException("El apellido del huesped es inválido. El campo se encuentra vacío o excede 64 caracteres.");
        }
        if (huespedDTO.getCuit() != null && (huespedDTO.getCuit().isBlank() || huespedDTO.getCuit().length() > 11)) {
            throw new ValidacionException("El CUIT del huesped es inválido. El campo se encuentra vacío o excede 11 caracteres.");
        }
        if (huespedDTO.getTipoDocumento() == null) {
            throw new ValidacionException("El tipo de documento del huesped es inválido.");
        }
        if ( huespedDTO.getNroDocumento() == null || huespedDTO.getNroDocumento().isBlank() || huespedDTO.getNroDocumento().length() > 64) {
            throw new ValidacionException("El numero de documento del huesped es inválido. El campo se encuentra vacío o excede 64 caracteres.");
        }
        if (huespedDTO.getEmail() != null && (huespedDTO.getEmail().isBlank() || huespedDTO.getEmail().length() > 128 || !huespedDTO.getEmail().contains("@"))) {
            throw new ValidacionException("El email del huesped es inválido. El campo se encuentra vacío, excede 128 caracteres o no es una direccion de correo valida.");    
        }
        if (huespedDTO.getTelefono() == null || huespedDTO.getTelefono().isBlank() || huespedDTO.getTelefono().length() > 32) {
            throw new ValidacionException("El telefono del huesped es inválido. El campo se encuentra vacío o excede 32 caracteres.");
        }
        if (huespedDTO.getOcupacion() == null || huespedDTO.getOcupacion().isBlank() || huespedDTO.getOcupacion().length() > 64) {
            throw new ValidacionException("La ocupacion del huesped es inválida. El campo se encuentra vacío o excede 64 caracteres.");
        }
        if (huespedDTO.getDireccion() == null) {
            throw new ValidacionException("La direccion del huesped es inválida.");
        }
    }

    private void existeHuesped(HuespedDTO huespedDTO) throws PersistenciaException, ValidacionException{
        
        List<Huesped> huespedesExistentes = huespedDAO.findBy(h -> 
            h.getNroDocumento().equalsIgnoreCase(huespedDTO.getNroDocumento()) && 
            h.getTipoDocumento().getTipo().toString().equalsIgnoreCase(huespedDTO.getTipoDocumento())
        );
        if (!huespedesExistentes.isEmpty()) {
            throw new ValidacionException("Ya existe un huesped con el mismo tipo y numero de documento.");
        }
    }

    private Huesped mapDTOToHuesped(HuespedDTO dto) throws NegocioException, EntidadNoEncontradaException, PersistenciaException {
        try {
            TipoDocumento tipoDoc = new TipoDocumento();
            tipoDoc.setTipo(tipoDoc.valueOf(dto.getTipoDocumento()));
            return new Huesped.Builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .cuit(dto.getCuit())
                .tipoDocumento(tipoDoc)
                .nroDocumento(dto.getNroDocumento())
                .fechaDeNacimiento(dto.getFechaDeNacimiento())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .ocupacion(dto.getOcupacion())
                .direccion(direccionService.mapDTOToDireccion(dto.getDireccion()))
                .build();
        } catch (IllegalArgumentException e) {
            throw new NegocioException("Tipo de documento inválido: " + dto.getTipoDocumento(), e);
        } catch (ValidacionException e) {
            throw new NegocioException("Error al mapear la direccion del huesped.", e);
        }
    }

    private HuespedDTO mapHuespedToDTO(Huesped huesped) {
        return new HuespedDTO.Builder()
            .nombre(huesped.getNombre())
            .apellido(huesped.getApellido())
            .cuit(huesped.getCuit())
            .tipoDocumento(huesped.getTipoDocumento().getTipo().name())
            .nroDocumento(huesped.getNroDocumento())
            .fechaDeNacimiento(huesped.getFechaDeNacimiento())
            .email(huesped.getEmail())
            .telefono(huesped.getTelefono())
            .ocupacion(huesped.getOcupacion())
            .direccion(direccionService.mapDireccionToDTO(huesped.getDireccion()))
            .build();
    }

    public TipoDocumento buscarTipoDocumento(String tipo) throws Exception {
         if(tipo == null){
            throw new ValidacionException("El tipo de documento no puede ser nulo.");
        }
        if(tipo.isBlank()){
            throw new ValidacionException("El tipo de documento no puede estar vacío.");
        }
        try {
            TipoDocumento tipoDoc = tipoDocumentoDAO
                            .findBy(t -> t.getTipo().toString().equalsIgnoreCase(tipo))
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró un tipo de documento con el nombre: " + tipo));
            return tipoDoc;
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al buscar el tipo de documento: " + tipo, e);
        }
    }
}
