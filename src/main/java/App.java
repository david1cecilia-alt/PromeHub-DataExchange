package main.java;

import ficheros.InfoFicheros;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * Clase principal de PromeHub Data Exchange.
 * Muestra el menú, lee la opción del usuario y llama a la funcionalidad correspondiente.
 */
public class App {

    // Rutas de los ficheros con los que trabaja la aplicación (carpeta datos/ del proyecto)
    private static final Path RUTA_CSV = Path.of("datos", "videojuegos.csv");
    private static final Path RUTA_XML = Path.of("datos", "catalogo.xml");
    private static final Path RUTA_CSV_EXPORTADO = Path.of("datos", "catalogo_exportado.csv");

    private static final Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> pendiente("Cargar catálogo desde CSV");
                case 2 -> pendiente("Mostrar catálogo");
                case 3 -> pendiente("Exportar catálogo a XML");
                case 4 -> pendiente("Cargar catálogo desde XML");
                case 5 -> pendiente("Exportar catálogo a CSV");
                case 6 -> pendiente("Buscar videojuego");
                case 7 -> mostrarInfoFicheros();
                case 0 -> System.out.println("Saliendo de PromeHub Data Exchange. ¡Hasta pronto!");
                case -1 -> { } // Ya se mostró el error en leerOpcion()
                default -> System.out.println("Error: la opción " + opcion + " no existe. Elige un número del 0 al 7.");
            }
        } while (opcion != 0);

        teclado.close();
    }

    /** Muestra el menú principal tal y como lo pide el enunciado. */
    private static void mostrarMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println(" PROMEHUB DATA EXCHANGE");
        System.out.println("========================================");
        System.out.println("1. Cargar catálogo desde CSV");
        System.out.println("2. Mostrar catálogo");
        System.out.println("3. Exportar catálogo a XML");
        System.out.println("4. Cargar catálogo desde XML");
        System.out.println("5. Exportar catálogo a CSV");
        System.out.println("6. Buscar videojuego");
        System.out.println("7. Información de ficheros");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    /**
     * Lee la opción escrita por el usuario.
     * Si no es un número, avisa del error y devuelve -1 para que el menú se repita.
     */
    private static int leerOpcion() {
        String entrada = teclado.nextLine().trim();

        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            System.out.println("Error: \"" + entrada + "\" no es una opción válida. Escribe un número del 0 al 7.");
            return -1;
        }
    }

    /** RF7: muestra existencia, tamaño y ruta de los ficheros que usa la aplicación. */
    private static void mostrarInfoFicheros() {
        System.out.println("\n--- Información de ficheros ---");
        InfoFicheros.mostrarInfo(RUTA_CSV);
        InfoFicheros.mostrarInfo(RUTA_XML);
        InfoFicheros.mostrarInfo(RUTA_CSV_EXPORTADO);
    }

    /** Aviso temporal para las opciones que dependen de las clases que aún no están en el repositorio. */
    private static void pendiente(String nombreOpcion) {
        System.out.println("La opción \"" + nombreOpcion + "\" todavía no está implementada.");
    }
}