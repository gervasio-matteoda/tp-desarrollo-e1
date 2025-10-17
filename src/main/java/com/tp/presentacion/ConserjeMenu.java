package com.tp.presentacion;

import com.tp.dto.ConserjeDTO;
import com.tp.excepciones.*;
import com.tp.servicios.ConserjeService;

import java.util.Scanner;
import java.io.Console;

public class ConserjeMenu {

    private final ConserjeService conserjeService;
    private final Scanner scanner;

    public ConserjeMenu() {
        this.conserjeService = ConserjeService.getInstancia();
        this.scanner = new Scanner(System.in);
    }

    public ConserjeDTO iniciarMenu() {
        System.out.println("- - - - - SISTEMA DE GESTIÓN HOTELERA - - - - -\n");
        int opcion = -1;
        ConserjeDTO conserjeAutenticado = null;

        while (conserjeAutenticado == null) {
            
            System.out.println("  MENÚ PRINCIPAL - GESTIÓN DE CONSERJES  ");
            System.out.println("=========================================");
            System.out.println("1. Iniciar Sesión");
            System.out.println("2. Registrar Nuevo Conserje");
            System.out.println("------------------------------------");
            System.out.println("0. Volver al Menú Principal/Salir");
            System.out.print("\nSeleccione una opción: ");
            
            try {
                String input = scanner.nextLine().trim();
                if (input.isBlank()) continue; 
                
                opcion = Integer.parseInt(input);
                limpiarConsola();
                
                switch (opcion) {
                    case 1:
                        limpiarConsola();
                        conserjeAutenticado = autenticar();
                        break;
                    case 2:
                        limpiarConsola();
                        registrarConserje();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema...");
                        return null; 
                    default:
                        mostrarError("Opción no válida. Intente de nuevo.");
                        limpiarCampos();
                        break;
                }
            } catch (NumberFormatException e) {
                mostrarError("Entrada inválida. Por favor ingrese un número.");
                limpiarCampos(); 
            }
        }
        return conserjeAutenticado;
    }

    private ConserjeDTO autenticar() {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        String usuario = pedirCampo("Usuario: ");
        String contrasenia = pedirCampo("Contraseña: "); // Modificar a pedirContraseniaOculta
        
        try {
            ConserjeDTO conserje = conserjeService.autenticarConserje(usuario, contrasenia);
            System.out.println("\nAutenticación exitosa. Bienvenido, " + conserje.getNombre());
            return conserje;
        } catch (ValidacionException e) {
            mostrarError("Error de validación: " + e.getMessage());
        } catch (AutenticacionException e) {
            mostrarError("Error de autenticación: " + e.getMessage());
        } catch (NegocioException e) {
            mostrarError("Error del sistema: " + e.getMessage());
        }
        limpiarCampos();
        return null;
    }

    private void registrarConserje() {
        System.out.println("\n--- CREAR CUENTA ---");

        String nombre = pedirCampo("Nombre (4-64 chars): ");
        String apellido = pedirCampo("Apellido (4-64 chars): ");
        String usuario = pedirCampo("Usuario (4-32 chars, solo letras, nums, _, -): ");
        String contrasenia = pedirCampo("Contraseña (5 letras, 3 nums, no secuencias): "); // Modificar a pedirContraseniaOculta

        ConserjeDTO nuevoConserje = new ConserjeDTO.Builder()
            .nombre(nombre)
            .apellido(apellido)
            .usuario(usuario)
            .contrasenia(contrasenia)
            .build();
        
        try {
            conserjeService.registrarConserje(nuevoConserje);
            System.out.println("\nCuenta creada con éxito para el usuario: " + usuario);
            System.out.println("Puede iniciar sesión ahora.");
        } catch (ValidacionException e) {
            mostrarError("Error de validación: " + e.getMessage());
        } catch (EntidadDuplicadaException e) {
            mostrarError("Error: " + e.getMessage());
        } catch (NegocioException e) {
            mostrarError("Error interno del sistema: " + e.getMessage());
        }
        limpiarCampos();;
    }


    private String pedirCampo(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private String pedirContraseniaOculta(String mensaje) {
        Console console = System.console();
        if (console != null) {
            System.out.print(mensaje);
            return new String(console.readPassword());
        } else {
            System.out.print(mensaje);
            return scanner.nextLine().trim();
        }
    }

    private void mostrarError(String mensaje) {
        System.out.println("\nERROR: " + mensaje);
    }

    private void limpiarCampos() {
        System.out.println("\n------------------------------------------");
        System.out.println("Presione ENTER para continuar y reintentar...");
        scanner.nextLine(); 

        limpiarConsola(); 
        
        System.out.println("Intente de nuevo o elija una opción diferente...");
    }

    private static void limpiarConsola() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
}