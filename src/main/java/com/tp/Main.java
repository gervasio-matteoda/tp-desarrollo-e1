package com.tp;

import com.tp.dto.ConserjeDTO;
import com.tp.excepciones.PersistenciaException;
import com.tp.presentacion.ConserjeMenu;
import com.tp.presentacion.HuespedMenu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args){
        
        try {
            initializeDataDirectory();
        } catch (PersistenciaException e) {
            System.err.println("Error fatal al inicializar el directorio de datos: " + e.getMessage());
            e.printStackTrace();
            return; // Termina la aplicación si el directorio no se puede inicializar
        } 
        
        // Login/Autenticación
        ConserjeMenu login = new ConserjeMenu();
        ConserjeDTO conserje = login.iniciarMenu();

        // ¿Éxito? Continua con el menú principal del sistema
        System.out.println("\nSesión iniciada para el conserje: " + conserje.getUsuario());

        // Continua con HuespedMenu
        // HuespedMenu menuPrincipal = new HuespedMenu(conserje);
        // menuPrincipal.iniciarMenu();

        System.out.println("\n👋 Fin de la sesión. ¡Hasta pronto!");
    }

    private static void initializeDataDirectory() throws PersistenciaException {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            throw new PersistenciaException("Error al crear el directorio de datos para la aplicación.", e);
        }
    }
}