package com.tp;

import com.tp.excepciones.*;
import com.tp.modelo.Conserje;
import com.tp.servicios.ConserjeService;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        
        ConserjeService conserjeService;

        try {
            
            conserjeService = ConserjeService.getInstancia();
            System.out.println("--- INICIO DE PRUEBAS DEL SERVICIO DE CONSERJES ---");
            //testConserje(conserjeService); 
            System.out.println("\n--- FIN DE PRUEBAS ---");

        } catch (PersistenciaException e) {
            System.err.println("ERROR CRÍTICO: La aplicación no pudo inicializarse y se cerrará.");
            e.printStackTrace();
        }
    }

    private static void testConserje(ConserjeService conserjeService) {
        // Test 1: Intentar crear conserje con datos inválidos (contraseña)
        System.out.println("\n--- Test 1: Crear conserje con contraseña inválida ---");
        try {
            Conserje conserjeInvalido = new Conserje.Builder().nombre("Test").apellido("User").usuario("test1").contrasenia("pass123").build();
            conserjeService.registrarConserje(conserjeInvalido);
            System.out.println("❌ TEST 1 FALLÓ: No se lanzó NegocioException por datos inválidos.");
        } catch (NegocioException e) {
            System.out.println("✅ TEST 1 SUPERADO: Se capturó correctamente el error -> " + e.getMessage());
            if (e.getCause() instanceof ValidacionException) {
                System.out.println("   Causa: Validación correcta.");
            }
        }

        // Test 2: Crear un usuario válido
        System.out.println("\n--- Test 2: Crear un usuario válido ('admin') ---");
        Conserje admin = new Conserje.Builder().nombre("Admin").apellido("Principal").usuario("admin").contrasenia("claveSecreta134").build();
        try {
            conserjeService.registrarConserje(admin);
            System.out.println("✅ TEST 2 SUPERADO: Conserje 'admin' creado exitosamente.");
        } catch (NegocioException e) {
            System.out.println("❌ TEST 2 FALLÓ: Ocurrió un error inesperado al crear 'admin'.");
            e.printStackTrace();
        }

        // Test 3: Intentar crear un usuario duplicado
        System.out.println("\n--- Test 3: Intentar crear usuario duplicado ('admin') ---");
        try {
            conserjeService.registrarConserje(admin);
            System.out.println("❌ TEST 3 FALLÓ: No se lanzó NegocioException por usuario duplicado.");
        } catch (NegocioException e) {
            System.out.println("✅ TEST 3 SUPERADO: Se capturó correctamente el error -> " + e.getMessage());
        }
        
        // Test 4: Autenticación con usuario inexistente
        System.out.println("\n--- Test 4: Autenticar con usuario inexistente ---");
        try {
            conserjeService.autenticarConserje("noexiste", "password");
            System.out.println("❌ TEST 4 FALLÓ: No se lanzó AutenticacionException.");
        } catch (AutenticacionException e) {
            System.out.println("✅ TEST 4 SUPERADO: Se capturó correctamente el error -> " + e.getMessage());
        } catch (NegocioException e) {
            System.out.println("❌ TEST 4 FALLÓ con una excepción inesperada: " + e.getClass().getSimpleName());
        }

        // Test 5: Autenticación con contraseña incorrecta
        System.out.println("\n--- Test 5: Autenticar con contraseña incorrecta ---");
        try {
            conserjeService.autenticarConserje("admin", "error134");
            System.out.println("❌ TEST 5 FALLÓ: No se lanzó AutenticacionException.");
        } catch (AutenticacionException e) {
            System.out.println("✅ TEST 5 SUPERADO: Se capturó correctamente el error -> " + e.getMessage());
        } catch (NegocioException e) {
            System.out.println("❌ TEST 5 FALLÓ con una excepción inesperada: " + e.getClass().getSimpleName());
        }

        // Test 6: Autenticación exitosa
        System.out.println("\n--- Test 6: Autenticar correctamente ---");
        try {
            Conserje conserjeLogueado = conserjeService.autenticarConserje("admin", "claveSecreta134");
            System.out.println("✅ TEST 6 SUPERADO: Inicio de sesión exitoso. Bienvenido, " + conserjeLogueado.getNombre());
        } catch (NegocioException e) {
            System.out.println("❌ TEST 6 FALLÓ: Ocurrió un error inesperado.");
            e.printStackTrace();
        }

        // Test 7: Buscar conserje existente usando Optional
        System.out.println("\n--- Test 7: Buscar a 'admin' y encontrarlo ---");
        try {
            Optional<Conserje> conserjeOpt = conserjeService.buscarConserje(admin);
            conserjeOpt.ifPresentOrElse(
                c -> System.out.println("✅ TEST 7 SUPERADO: Conserje encontrado: " + c.getUsuario()),
                () -> System.out.println("❌ TEST 7 FALLÓ: No se encontró al conserje.")
            );
        } catch (NegocioException e) {
            System.out.println("❌ TEST 7 FALLÓ: Ocurrió un error inesperado.");
            e.printStackTrace();
        }

        // Test 8: Listar todos los conserjes
        System.out.println("\n--- Test 8: Listar todos los conserjes ---");
        try {
            conserjeService.registrarConserje(new Conserje.Builder().nombre("Juan").apellido("Perez").usuario("jperez").contrasenia("juanito975").build());
            List<Conserje> todos = conserjeService.listarConserjes();
            System.out.println("✅ TEST 8 SUPERADO: Se encontraron " + todos.size() + " conserjes.");
            todos.forEach(c -> System.out.println("\t- Usuario: " + c.getUsuario()));
        } catch (NegocioException e) {
            System.out.println("❌ TEST 8 FALLÓ: Ocurrió un error inesperado al listar.");
            e.printStackTrace();
        }

        // Test 9: Eliminar un conserje existente
        System.out.println("\n--- Test 9: Eliminar a 'admin' ---");
        try {
            conserjeService.eliminarConserje(admin);
            System.out.println("✅ TEST 9 SUPERADO: Conserje 'admin' eliminado.");
        } catch (NegocioException e) {
            System.out.println("❌ TEST 9 FALLÓ: Ocurrió un error inesperado al eliminar.");
            e.printStackTrace();
        }

        // Test 10: Verificar que el conserje fue eliminado
        System.out.println("\n--- Test 10: Listar conserjes restantes ---");
        try {
            List<Conserje> restantes = conserjeService.listarConserjes();
            System.out.println("✅ TEST 10 SUPERADO: Ahora hay " + restantes.size() + " conserjes.");
            restantes.forEach(c -> System.out.println("\t- Usuario: " + c.getUsuario()));
        } catch (NegocioException e) {
            System.out.println("❌ TEST 10 FALLÓ: Ocurrió un error inesperado al listar de nuevo.");
            e.printStackTrace();
        }
    }
}