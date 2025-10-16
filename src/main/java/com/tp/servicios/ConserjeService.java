package com.tp.servicios;

import com.tp.dto.ConserjeDTO;
import com.tp.excepciones.*;
import com.tp.modelo.Conserje;
import com.tp.persistencia.IConserjeDAO;
import com.tp.utils.DAOFactory;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class ConserjeService {

    private static ConserjeService instancia;
    private IConserjeDAO conserjeDAO;

    private ConserjeService() {
        this.conserjeDAO = DAOFactory.getConserjeDAO();
    }

    public static ConserjeService getInstancia() {
        if (instancia == null) {
            instancia = new ConserjeService();
        }
        return instancia;
    }

    public void registrarConserje(ConserjeDTO conserjeDTO) throws ValidacionException, EntidadDuplicadaException, NegocioException {
        try {
            validarConserje(conserjeDTO);
            Conserje conserje = mapDTOToConserje(conserjeDTO);
            conserjeDAO.create(conserje);
        } catch (PersistenciaException e) {
            throw new NegocioException("Error de persistencia al registrar el conserje.", e);
        }
    }

    public ConserjeDTO autenticarConserje(String usuario, String contrasenia) throws AutenticacionException, NegocioException, ValidacionException{
        if (usuario == null || usuario.isBlank() || contrasenia == null || contrasenia.isBlank()) { throw new ValidacionException("El usuario y la contraseña no pueden estar vacíos.");}

        try {
            Conserje conserje = conserjeDAO.findBy(c -> c.getUsuario().equals(usuario))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntidadNoEncontradaException("No existe un conserje con el usuario: " + usuario));

            ConserjeDTO conserjeDTO = mapConserjeToDTO(conserje);

            if (!conserjeDTO.getContrasenia().equals(contrasenia)) {
                throw new AutenticacionException("La contraseña es incorrecta.");
            }
            return conserjeDTO; // Autenticación exitosa
        } catch (PersistenciaException e) {
            throw new NegocioException("Error de persistencia al registrar el conserje.", e);
        }
    }

    public Optional<Conserje> buscarConserje(ConserjeDTO conserjeDTO) throws NegocioException, ValidacionException {
        if (conserjeDTO == null || conserjeDTO.getUsuario() == null || conserjeDTO.getUsuario().isBlank()) {
            throw new ValidacionException("El conserje o su nombre de usuario no pueden estar vacíos para la búsqueda.");
        }
        
        try {
            return conserjeDAO.findBy(c -> c.getUsuario().equals(conserjeDTO.getUsuario())).stream().findFirst();
        } catch (PersistenciaException e) {
            throw new NegocioException("Error en el sistema al buscar el conserje con usuario: " + conserjeDTO.getUsuario(), e);
        }
    }  

    public List<ConserjeDTO> listarConserjes() throws NegocioException {
        try {
            List<Conserje> conserjes = conserjeDAO.findAll();
            List<ConserjeDTO> conserjesDTO = new ArrayList<>();
            for (Conserje c: conserjes){
                conserjesDTO.add(mapConserjeToDTO(c));
            }
            return conserjesDTO;
        } catch (PersistenciaException e) {
            throw new NegocioException("Error en el sistema al obtener la lista de conserjes.", e);
        }
    }

    public void eliminarConserje(ConserjeDTO conserjeDTO) throws NegocioException, ValidacionException, PersistenciaException{
        if (conserjeDTO == null || conserjeDTO.getUsuario() == null || conserjeDTO.getUsuario().isBlank()) {
            throw new ValidacionException("El conserje a eliminar no puede ser nulo o tener un usuario vacío.");
        }
        List<Conserje> conserjes = conserjeDAO.findAll();
        boolean existeUsuario = conserjes.stream().anyMatch(c -> c.getUsuario().equals(conserjeDTO.getUsuario()));

        if (!existeUsuario) {
            throw new EntidadNoEncontradaException("No se encontró un conserje para eliminar con el usuario: " + conserjeDTO.getUsuario());
        }

        try {
            conserjeDAO.delete(mapDTOToConserje(conserjeDTO));
        } catch (PersistenciaException e) {
            throw new NegocioException("Error de persistencia al registrar el conserje.", e);
        }
    }

    private void validarConserje(ConserjeDTO conserjeDTO) throws ValidacionException, EntidadDuplicadaException, PersistenciaException {

        if (conserjeDTO.getNombre() == null || conserjeDTO.getNombre().isBlank() || conserjeDTO.getNombre().length() < 4 || conserjeDTO.getNombre().length() > 64) {
            throw new ValidacionException("El nombre del conserje es inválido. El campo debe tener entre 4 y 64 caracteres.");
        }
        if (conserjeDTO.getApellido() == null || conserjeDTO.getApellido().isBlank() || conserjeDTO.getApellido().length() < 4 || conserjeDTO.getApellido().length() > 64) {
            throw new ValidacionException("El apellido del conserje es inválido. El campo debe tener entre 4 y 64 caracteres.");
        }

        if (conserjeDTO.getUsuario() == null || conserjeDTO.getUsuario().length() < 4 || conserjeDTO.getUsuario().length() > 32) {
            throw new ValidacionException("El usuario es inválido. El campo debe tener entre 4 y 32 caracteres.");
        }

        // Caracteres Permitidos: Letras, números, guion bajo, guion medio
        if (!conserjeDTO.getUsuario().matches("^[a-zA-Z0-9_-]+$")) {
            throw new ValidacionException("El usuario solo puede contener letras, números, '_' y '-'.");
        }

        if (conserjeDTO.getContrasenia() == null || conserjeDTO.getContrasenia().isBlank() || conserjeDTO.getContrasenia().length() < 4 || conserjeDTO.getContrasenia().length() > 32) {
            throw new ValidacionException("La contraseña es inválida. El campo debe tener entre 4 y 32 caracteres.");
        }

        // Contadores y lista para extraer dígitos
        int contadorLetras = 0;
        int contadorNumeros = 0;
        List<Integer> numerosEnContrasenia = new ArrayList<>();

        for (char c : conserjeDTO.getContrasenia().toCharArray()) {
            if (Character.isLetter(c)) {
                contadorLetras++;
            } else if (Character.isDigit(c)) {
                contadorNumeros++;
                numerosEnContrasenia.add(Character.getNumericValue(c));
            }
        }

        if (contadorLetras < 5) {
            throw new ValidacionException("La contraseña debe tener al menos 5 letras.");
        }
        if (contadorNumeros < 3) {
            throw new ValidacionException("La contraseña debe tener al menos 3 números.");
        }

        // Validar si los números son iguales o consecutivos
        if (sonNumerosInvalidos(numerosEnContrasenia)) {
            throw new ValidacionException("Los números de la contraseña no pueden ser todos iguales ni consecutivos.");
        }

        List<Conserje> conserjes = conserjeDAO.findAll();
        boolean duplicado = conserjes.stream().anyMatch(c -> c.getUsuario().equals(conserjeDTO.getUsuario()));
        if (duplicado) {
            throw new EntidadDuplicadaException("Ya existe un conserje con el usuario: " + conserjeDTO.getUsuario());
        }

    }

    /* Verificar las reglas de los números en la contraseña. */
    private boolean sonNumerosInvalidos(List<Integer> numeros) {
        
        if (numeros.isEmpty()) return false;

        // Todos iguales
        boolean todosIguales = numeros.stream().distinct().count() == 1;
        if (todosIguales) return true;

        // Consecutivos crecientes o decrecientes
        boolean crecientes = true;
        boolean decrecientes = true;

        for (int i = 0; i < numeros.size() - 1; i++) {
            if (numeros.get(i + 1) != numeros.get(i) + 1) crecientes = false;
            if (numeros.get(i + 1) != numeros.get(i) - 1) decrecientes = false;
        }
        return crecientes || decrecientes;
    }

    private Conserje mapDTOToConserje(ConserjeDTO conserjeDTO) {
        Conserje nuevoConserje = new Conserje.Builder()
            .nombre(conserjeDTO.getNombre())
            .apellido(conserjeDTO.getApellido())
            .usuario(conserjeDTO.getUsuario())
            .contrasenia(conserjeDTO.getContrasenia())
            .build();
        
        return nuevoConserje;
    }

    private ConserjeDTO mapConserjeToDTO(Conserje conserje) {
        ConserjeDTO nuevoConserjeDTO = new ConserjeDTO.Builder()
            .nombre(conserje.getNombre())
            .apellido(conserje.getApellido())
            .usuario(conserje.getUsuario())
            .contrasenia(conserje.getContrasenia())
            .build();
        
        return nuevoConserjeDTO;
    }

}
