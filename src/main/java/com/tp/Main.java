package com.tp;

import com.tp.dto.ConserjeDTO;
import com.tp.excepciones.PersistenciaException;
import com.tp.presentacion.ConserjeMenu;
import com.tp.presentacion.HuespedMenu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args){
        
        try {
            initializeDataDirectory();
        } catch (PersistenciaException e) {
            System.err.println("Error fatal al inicializar el directorio de datos: " + e.getMessage());
            e.printStackTrace();
            return; // Termina la aplicación si el directorio no se puede inicializar
        } 
        
        // Login/Autenticación
        while (true) {
            ConserjeMenu login = new ConserjeMenu();
            ConserjeDTO conserje = login.iniciarMenu();

            System.out.println("\n------------------------------------------");
            System.out.println("Presione ENTER para continuar...");
            scanner.nextLine(); 
            limpiarConsola();

            if (conserje != null) {
                HuespedMenu menuPrincipal = new HuespedMenu(conserje);
                menuPrincipal.iniciarMenu();
            } else { break; }
        }

        scanner.close();
        limpiarConsola();
        System.out.println("\nFin de la sesión. ¡Hasta pronto!");
    }

    private static void initializeDataDirectory() throws PersistenciaException {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            throw new PersistenciaException("Error al crear el directorio de datos para la aplicación.", e);
        }
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