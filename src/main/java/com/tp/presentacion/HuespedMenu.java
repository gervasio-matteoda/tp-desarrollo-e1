package com.tp.presentacion;
import com.tp.dto.ConserjeDTO;
import com.tp.servicios.GeoService;
import com.tp.servicios.HuespedService;
import com.tp.dto.ConserjeDTO;
import com.tp.dto.DireccionDTO;
import com.tp.dto.HuespedDTO;
import com.tp.excepciones.NegocioException;
import com.tp.excepciones.PersistenciaException;
import com.tp.excepciones.ValidacionException;
import com.tp.servicios.HuespedService;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class HuespedMenu {

    private final ConserjeDTO conserjeAutenticado;
    private final Scanner scanner;
    private final HuespedService huespedService;
    private final GeoService geoService;

    public HuespedMenu(ConserjeDTO conserje) {
        this.conserjeAutenticado = conserje;
        this.scanner = new Scanner(System.in);
        this.huespedService = HuespedService.getInstancia();
        this.geoService = GeoService.getInstancia();
    }

    public void iniciarMenu() {
        System.out.println("- - - - - SISTEMA DE GESTIÓN HOTELERA - - - - -\n");
        int opcion = -1;
        while (opcion != 0) {
            
            System.out.println("  MENÚ PRINCIPAL - GESTIÓN DE HUÉSPEDES  ");
            System.out.println("=========================================");
            System.out.println("1. Registrar Nuevo Huésped");
            System.out.println("2. Buscar Huéspedes (p/Modificar o Eliminar)");
            System.out.println("-----------------------------------------");
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
                        registrarHuesped();
                        break;
                    case 2:
                        limpiarConsola();
                        gestionarHuespedExistente();
                        break;
                    case 0:
                        System.out.println("Volviendo al menú principal...");
                        break;
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
    }

    private void gestionarHuespedExistente() {
        HuespedDTO huesped = buscarUnHuesped();

        if (huesped != null) {
            limpiarConsola();
            mostrarDatosHuesped(huesped);

            System.out.println("\n  ¿QUÉ DESEA HACER?  ");
            System.out.println("----------------------");
            System.out.println("1. Modificar Huésped");
            System.out.println("2. Eliminar Huésped");
            System.out.println("0. Volver");
            System.out.print("\nSeleccione una opción: ");

            String input = scanner.nextLine().trim();
            if (input.isBlank()) return;

            int opcion = Integer.parseInt(input);
            switch(opcion) {
                case 1:
                    limpiarConsola();
                    System.out.println("-> Modificación de Huésped (Próximamente)...");
                    limpiarCampos();
                    break;
                case 2:
                    limpiarConsola();
                    confirmarYEliminarHuesped(huesped);
                    break;
                case 0:
                    break;
                default:
                    mostrarError("Opción no válida.");
                    limpiarCampos();
                    break;
            }
        } else {
            limpiarCampos();
        }
    }

    private HuespedDTO buscarUnHuesped() {
        System.out.println("--- BÚSQUEDA DE HUÉSPED ---");
        String tipoDoc = pedirTipoDocumento();
        if (esCancelacion(tipoDoc)) return null;

        String nroDoc = pedirCampo("Número de Documento a buscar: ");
        if (esCancelacion(nroDoc)) return null;

        try {
            List<HuespedDTO> resultados = huespedService.buscarHuspedes("", "", nroDoc, tipoDoc);
            if (resultados.isEmpty()) {
                mostrarError("No se encontró ningún huésped con ese documento.");
                return null;
            }
            return resultados.get(0);
        } catch (NegocioException e) {
            mostrarError("Error al buscar huésped: " + e.getMessage());
            return null;
        }
    }

    private void mostrarDatosHuesped(HuespedDTO huesped) {
        System.out.println("--- DATOS DEL HUÉSPED ENCONTRADO ---");
        System.out.println("Nombre Completo: " + huesped.getNombre() + " " + huesped.getApellido());
        System.out.println("Documento:       " + huesped.getTipoDocumento() + " " + huesped.getNroDocumento());
        System.out.println("Teléfono:        " + huesped.getTelefono());
        System.out.println("Email:           " + (esNulo(huesped.getEmail()) ? "No especificado" : huesped.getEmail()));
        
        DireccionDTO dir = huesped.getDireccion();
        if (dir != null) {
            System.out.println("Dirección:       " + dir.getCalle() + " " + dir.getNroCalle() + ", " + dir.getCiudad() + ", " + dir.getProvincia());
        }
    }

    private void confirmarYEliminarHuesped(HuespedDTO huesped) {
        System.out.println("Huésped a eliminar: " + huesped.getNombre() + " " + huesped.getApellido());
        String confirmacion = pedirCampo("¿Está seguro que desea eliminar a este huésped? (SI/NO): ").toUpperCase();
        
        if (confirmacion.equals("SI")) {
            try {
                huespedService.eliminarHuesped(huesped);
                System.out.println("\n¡Huésped eliminado con éxito!");
            } catch (NegocioException | PersistenciaException e) {
                mostrarError(e.getMessage());
            }
        } else {
            System.out.println("\nOperación de eliminación cancelada.");
        }
        limpiarCampos();
    }

    private void registrarHuesped() {
        boolean cargarOtro = true;
        while (cargarOtro) {
            limpiarConsola(); // <-- MEJORA 1: Limpia la pantalla al iniciar un nuevo registro.
            System.out.println("--- REGISTRO DE NUEVO HUÉSPED ---");
            System.out.println("(*) Campo obligatorio. Escriba 'CANCELAR' en cualquier momento para salir.");

            HuespedDTO.Builder huespedBuilder = new HuespedDTO.Builder();
            DireccionDTO.Builder direccionBuilder = new DireccionDTO.Builder();
            
            String posicionIVA = "CONSUMIDOR FINAL";
            boolean datosCompletos = false;

            while (!datosCompletos) {
                // ... (toda la lógica de pedir datos sin cambios)
                if (esNulo(huespedBuilder.build().getApellido())) huespedBuilder.apellido(pedirCampo("(*) Apellido: "));
                if (esCancelacion(huespedBuilder.build().getApellido())) return;
                if (esNulo(huespedBuilder.build().getNombre())) huespedBuilder.nombre(pedirCampo("(*) Nombres: "));
                if (esCancelacion(huespedBuilder.build().getNombre())) return;
                if (esNulo(huespedBuilder.build().getTipoDocumento())) huespedBuilder.tipoDocumento(pedirTipoDocumento());
                if (esCancelacion(huespedBuilder.build().getTipoDocumento())) return;
                if (esNulo(huespedBuilder.build().getNroDocumento())) {
                    String nroDoc = pedirYVerificarDocumento(huespedBuilder.build().getTipoDocumento());
                    if (esCancelacion(nroDoc)) return;
                    huespedBuilder.nroDocumento(nroDoc);
                }
                if (huespedBuilder.build().getFechaDeNacimiento() == null) {
                    LocalDate fecha = pedirFecha("(*) Fecha de Nacimiento (DD/MM/YYYY): "); // Mensaje actualizado
                    if (fecha == null && !confirmarCancelacion()) continue;
                    if (fecha == null) return;
                    huespedBuilder.fechaDeNacimiento(fecha);
                }
                if (esNulo(huespedBuilder.build().getTelefono())) huespedBuilder.telefono(pedirCampo("(*) Teléfono: "));
                if (esCancelacion(huespedBuilder.build().getTelefono())) return;
                if (esNulo(huespedBuilder.build().getOcupacion())) huespedBuilder.ocupacion(pedirCampo("(*) Ocupación: "));
                if (esCancelacion(huespedBuilder.build().getOcupacion())) return;
                System.out.println("\n--- DATOS DE DIRECCIÓN ---");
                if (esNulo(direccionBuilder.build().getPais())) direccionBuilder.pais(pedirPais());
                if (esCancelacion(direccionBuilder.build().getPais())) return;
                if (esNulo(direccionBuilder.build().getProvincia())) direccionBuilder.provincia(pedirProvincia(direccionBuilder.build().getPais()));
                if (esCancelacion(direccionBuilder.build().getProvincia())) return;
                if (esNulo(direccionBuilder.build().getCiudad())) direccionBuilder.ciudad(pedirCiudad(direccionBuilder.build().getProvincia()));
                if (esCancelacion(direccionBuilder.build().getCiudad())) return;
                if (esNulo(direccionBuilder.build().getCalle())) direccionBuilder.calle(pedirCampo("(*) Calle: "));
                if (esCancelacion(direccionBuilder.build().getCalle())) return;
                if (direccionBuilder.build().getNroCalle() == 0) {
                    try {
                        direccionBuilder.nroCalle(Integer.parseInt(pedirCampo("(*) Número de Calle: ")));
                    } catch (NumberFormatException e) {
                        mostrarError("El número de calle debe ser un valor numérico.");
                        direccionBuilder.nroCalle(0);
                    }
                }
                if (direccionBuilder.build().getCodigoPostal() == 0) {
                    try {
                        direccionBuilder.codigoPostal(Integer.parseInt(pedirCampo("(*) Código Postal: ")));
                    } catch (NumberFormatException e) {
                        mostrarError("El código postal debe ser un valor numérico.");
                        direccionBuilder.codigoPostal(0);
                    }
                }
                if (direccionBuilder.build().getNroPiso() == 0) {
                    String pisoStr = pedirCampo("Piso (Opcional, presione ENTER para omitir): ");
                    if (!pisoStr.isBlank() && !esCancelacion(pisoStr)) {
                        try {
                            direccionBuilder.nroPiso(Integer.parseInt(pisoStr));
                        } catch (NumberFormatException e) { mostrarError("El piso debe ser un número."); }
                    }
                }
                if (direccionBuilder.build().getNroDepartamento() == 0) {
                    String deptoStr = pedirCampo("Departamento (Opcional, presione ENTER para omitir): ");
                    if (!deptoStr.isBlank() && !esCancelacion(deptoStr)) {
                        try {
                            direccionBuilder.nroDepartamento(Integer.parseInt(deptoStr));
                        } catch (NumberFormatException e) { mostrarError("El depto. debe ser un número."); }
                    }
                }
                System.out.println("\n--- DATOS DE IVA ---");
                System.out.println("Opciones: 'CONSUMIDOR FINAL', 'RESPONSABLE INSCRIPTO'");
                String ivaInput = pedirCampo("Posición frente al IVA (Actual: " + posicionIVA + "): ").toUpperCase();
                if (esCancelacion(ivaInput)) return;
                if (ivaInput.equals("RESPONSABLE INSCRIPTO")) {
                    posicionIVA = "RESPONSABLE INSCRIPTO";
                } else if (!ivaInput.isBlank()) {
                    posicionIVA = "CONSUMIDOR FINAL";
                }
                if (esNulo(huespedBuilder.build().getCuit())) {
                    if (posicionIVA.equals("RESPONSABLE INSCRIPTO")) {
                        huespedBuilder.cuit(pedirCampo("(*) CUIT (Obligatorio para Resp. Inscripto): "));
                    } else {
                        huespedBuilder.cuit(pedirCampo("CUIT (Opcional, presione ENTER para omitir): "));
                    }
                }
                if (esCancelacion(huespedBuilder.build().getCuit())) return;
                if (esNulo(huespedBuilder.build().getEmail())) huespedBuilder.email(pedirCampo("Email (Opcional, presione ENTER para omitir): "));
                if (esCancelacion(huespedBuilder.build().getEmail())) return;
                
                HuespedDTO huespedFinal = huespedBuilder.build();
                DireccionDTO direccionFinal = direccionBuilder.build();
                List<String> camposFaltantes = validarCampos(huespedFinal, direccionFinal, posicionIVA);
                
                if (camposFaltantes.isEmpty()) {
                    datosCompletos = true;
                } else {
                    mostrarError("Faltan los siguientes campos obligatorios: " + String.join(", ", camposFaltantes));
                    limpiarCampos();
                }
            }

            try {
                huespedBuilder.direccion(direccionBuilder.build());
                huespedService.registrarHuesped(huespedBuilder.build());
                System.out.println("\nEl huésped " + huespedBuilder.build().getNombre() + " " + huespedBuilder.build().getApellido() + " se cargó correctamente.");
                
                String respuesta = pedirCampo("¿Querés cargar otro? (SI/NO): ").toUpperCase();
                if (!respuesta.equals("SI")) {
                    cargarOtro = false;
                }
            } catch (NegocioException | PersistenciaException e) {
                mostrarError("Error al registrar el huésped: " + e.getMessage());
                e.printStackTrace();
                limpiarCampos();
            }
        }
    }

    private List<String> validarCampos(HuespedDTO huesped, DireccionDTO direccion, String posicionIVA) {
        List<String> faltantes = new ArrayList<>();
        if (esNulo(huesped.getApellido())) faltantes.add("Apellido");
        if (esNulo(huesped.getNombre())) faltantes.add("Nombres");
        if (esNulo(huesped.getTipoDocumento())) faltantes.add("Tipo de Documento");
        if (esNulo(huesped.getNroDocumento())) faltantes.add("Número de Documento");
        if (huesped.getFechaDeNacimiento() == null) faltantes.add("Fecha de Nacimiento");
        if (esNulo(huesped.getTelefono())) faltantes.add("Teléfono");
        if (esNulo(huesped.getOcupacion())) faltantes.add("Ocupación");
        if (esNulo(direccion.getPais())) faltantes.add("País");
        if (esNulo(direccion.getProvincia())) faltantes.add("Provincia");
        if (esNulo(direccion.getCiudad())) faltantes.add("Ciudad");
        if (esNulo(direccion.getCalle())) faltantes.add("Calle");
        if (direccion.getNroCalle() == 0) faltantes.add("Número de Calle");
        if (direccion.getCodigoPostal() == 0) faltantes.add("Código Postal");
        if (posicionIVA.contains("RESPONSABLE") && esNulo(huesped.getCuit())) {
            faltantes.add("CUIT (obligatorio para Responsable Inscripto)");
        }
        return faltantes;
    }

    private boolean esNulo(String valor) {
        return valor == null || valor.isBlank();
    }
    
    private boolean esCancelacion(String input) {
        return input != null && input.equalsIgnoreCase("CANCELAR");
    }
    
    private boolean confirmarCancelacion() {
        String confirmacion = pedirCampo("¿Seguro querés cancelar? (SI/NO): ").toUpperCase();
        return confirmacion.equals("SI");
    }

    private String pedirYVerificarDocumento(String tipoDoc) {
        while(true) {
            String nroDoc = pedirCampo("(*) Número de Documento: ");
            if (esCancelacion(nroDoc)) return "CANCELAR";
            try {
                if (huespedService.huespedYaExiste(tipoDoc, nroDoc)) {
                    System.out.println("¡CUIDADO! Este documento ya existe.");
                    String opcion = pedirCampo("¿Desea ACEPTAR IGUALMENTE o CORREGIR? (aceptar/corregir): ").toUpperCase();
                    if(opcion.equals("ACEPTAR") || opcion.equals("ACEPTAR IGUALMENTE")){
                        return nroDoc;
                    }
                } else {
                    return nroDoc;
                }
            } catch (PersistenciaException e) {
                mostrarError("No se pudo verificar el documento: " + e.getMessage());
                return nroDoc; // Permite continuar pero el registro puede fallar
            }
        }
    }

    private String pedirOpcionDeLista(String mensaje, List<String> opciones) {
        System.out.println(mensaje);
        System.out.println("Opciones: " + String.join(" / ", opciones));
        while(true) {
            String input = pedirCampo("Seleccione una opción: ");
            if(esCancelacion(input)) return "CANCELAR";
            if(opciones.stream().anyMatch(opt -> opt.equalsIgnoreCase(input))) {
                return input.toUpperCase();
            } else {
                mostrarError("Opción no válida. Por favor, elija una de la lista.");
            }
        }
    }

    private String pedirPais() {
        try {
            List<String> paises = geoService.obtenerPaises();
            return pedirOpcionDeLista("(*) País:", paises);
        } catch (PersistenciaException e) {
            mostrarError("No se pudieron cargar los países: " + e.getMessage());
            return pedirCampo("(*) Ingrese el País manualmente: ");
        }
    }

    private String pedirProvincia(String pais) {
        if (esNulo(pais) || esCancelacion(pais)) return "";
        try {
            List<String> provincias = geoService.obtenerProvinciasPorPais(pais);
            return pedirOpcionDeLista("(*) Provincia:", provincias);
        } catch (Exception e) {
            mostrarError("No se pudieron cargar las provincias: " + e.getMessage());
            return pedirCampo("(*) Ingrese la Provincia manualmente: ");
        }
    }

    private String pedirCiudad(String provincia) {
        if (esNulo(provincia) || esCancelacion(provincia)) return "";
        try {
            List<String> ciudades = geoService.obtenerCiudadesPorProvincia(provincia);
            return pedirOpcionDeLista("(*) Ciudad/Localidad:", ciudades);
        } catch (Exception e) {
            mostrarError("No se pudieron cargar las ciudades: " + e.getMessage());
            return pedirCampo("(*) Ingrese la Ciudad manualmente: ");
        }
    }

    private String pedirCampo(String mensaje) {
        System.out.print(mensaje);
        // Devuelve el texto en mayúsculas, excepto para el email.
        String input = scanner.nextLine().trim();
        return mensaje.toLowerCase().contains("email") ? input : input.toUpperCase();
    }

    private LocalDate pedirFecha(String mensaje) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            String input = pedirCampo(mensaje);
            if (esCancelacion(input)) return null;
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                mostrarError("Formato de fecha incorrecto. Use DD/MM/YYYY.");
            }
        }
    }

    private String pedirTipoDocumento() {
        while (true) {
            try {
                List<String> tipos = huespedService.obtenerTiposDeDocumento();
                System.out.println("Tipos de documento disponibles: " + String.join(", ", tipos));
                String tipo = pedirCampo("Tipo de Documento: ");
                if(esCancelacion(tipo)) return "CANCELAR";
                if(tipos.contains(tipo)){
                    return tipo;
                } else {
                    mostrarError("Tipo de documento no válido.");
                }
            } catch (NegocioException e) {
                mostrarError("No se pudieron cargar los tipos de documento: " + e.getMessage());
                return pedirCampo("Ingrese el tipo de documento manualmente: ");
            }
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
