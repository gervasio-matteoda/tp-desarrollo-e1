package com.tp.servicios;

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

    public void registrarConserje(Conserje conserje) throws NegocioException {
        try {
            validarConserje(conserje);
            conserjeDAO.create(conserje);
        } catch (ValidacionException | PersistenciaException e) {
            throw new NegocioException("No se pudo registrar el conserje.", e);
        }
    }

    public Conserje autenticarConserje(String usuario, String contrasenia) throws AutenticacionException, NegocioException, ValidacionException{
        if (usuario == null || usuario.isBlank() || contrasenia == null || contrasenia.isBlank()) {
                throw new ValidacionException("El usuario y la contraseña no pueden estar vacíos.");
            }

        try {
            Conserje conserje = conserjeDAO.findBy(c -> c.getUsuario().equals(usuario))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new EntidadNoEncontradaException("No existe un conserje con el usuario: " + usuario));

            if (!conserje.getContrasenia().equals(contrasenia)) {
                throw new AutenticacionException("Contraseña incorrecta para el usuario: " + usuario);
            }
            return conserje; // Autenticación exitosa
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al intentar autenticar al conserje.", e);
        }
    }


    public Optional<Conserje> buscarConserje(Conserje conserje) throws NegocioException, ValidacionException {
        
        if (conserje == null || conserje.getUsuario() == null || conserje.getUsuario().isBlank()) {
            throw new ValidacionException("El conserje o su nombre de usuario no pueden estar vacíos para la búsqueda.");
        }
        
        try {
            return conserjeDAO.findBy(c -> c.getUsuario().equals(conserje.getUsuario())).stream().findFirst();
        } catch (PersistenciaException e) {
            throw new NegocioException("Error en el sistema al buscar el conserje con usuario: " + conserje.getUsuario(), e);
        }
    }  

    public List<Conserje> listarConserjes() throws NegocioException {
        try {
            return conserjeDAO.findAll();
        } catch (PersistenciaException e) {
            throw new NegocioException("Error en el sistema al obtener la lista de conserjes.", e);
        }
    }

    public void eliminarConserje(Conserje conserje) throws NegocioException, ValidacionException {
        if (conserje == null || conserje.getUsuario() == null || conserje.getUsuario().isBlank()) {
            throw new ValidacionException("El conserje a eliminar no puede ser nulo o tener un usuario vacío.");
        }
        try {
            conserjeDAO.delete(conserje);
        } catch (PersistenciaException e) { // EntidadNoEncontradaException es una PersistenciaException
            throw new NegocioException("No se pudo eliminar el conserje.", e);
        }
    }

    private void validarConserje(Conserje conserje) throws ValidacionException {

        if (conserje.getNombre() == null || conserje.getNombre().isBlank() || conserje.getNombre().length() > 64) {
            throw new ValidacionException("El nombre del conserje es inválido. El campo se encuentra vacío o excede 64 caracteres.");
        }
        if (conserje.getApellido() == null || conserje.getApellido().isBlank() || conserje.getApellido().length() > 64) {
            throw new ValidacionException("El apellido del conserje es inválido. El campo se encuentra vacío o excede 64 caracteres.");
        }

        String usuario = conserje.getUsuario();
        if (usuario == null || usuario.length() < 4 || usuario.length() > 32) {
            throw new ValidacionException("El usuario debe tener entre 4 y 32 caracteres.");
        }
        // Caracteres Permitidos: Letras, números, guion bajo, guion medio
        if (!usuario.matches("^[a-zA-Z0-9_-]+$")) {
            throw new ValidacionException("El usuario solo puede contener letras, números, '_' y '-'.");
        }

        String contrasenia = conserje.getContrasenia();
        if (contrasenia == null || contrasenia.isBlank() || contrasenia.length() > 32) {
            throw new ValidacionException("La contraseña es inválida. El campo se encuentra vacío o excede 32 caracteres.");
        }

        // Contadores y lista para extraer dígitos
        int contadorLetras = 0;
        int contadorNumeros = 0;
        List<Integer> numerosEnContrasenia = new ArrayList<>();

        for (char c : contrasenia.toCharArray()) {
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

}
