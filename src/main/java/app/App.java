package app;

import ficheros.GestorCSV;
import ficheros.InfoFicheros;
import modelo.Videojuego;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

    // Catálogo cargado en memoria (se rellena al cargar desde CSV o desde XML)
    private static List<Videojuego> catalogo = new ArrayList<>();

    public static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> cargarDesdeCSV();
                case 2 -> mostrarCatalogo();
                case 3 -> pendiente("Exportar catálogo a XML");
                case 4 -> pendiente("Cargar catálogo desde XML");
                case 5 -> exportarACSV();
                case 6 -> buscarVideojuego();
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

    /** RF1: carga el catálogo desde el CSV. */
    private static void cargarDesdeCSV() {
        try {
            catalogo = GestorCSV.leer(RUTA_CSV);

            if (catalogo.isEmpty()) {
                System.out.println("Aviso: el fichero no contiene ningún videojuego válido.");
            } else {
                System.out.println("Catálogo cargado desde " + RUTA_CSV.getFileName() + ".");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: no se puede cargar el catálogo. " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error al leer el fichero CSV: " + e.getMessage());
        }
    }

    /** RF5: exporta el catálogo que haya en memoria a un CSV nuevo. */
    private static void exportarACSV() {
        if (catalogo.isEmpty()) {
            System.out.println("El catálogo está vacío. Carga primero un CSV (opción 1) o un XML (opción 4).");
            return;
        }

        try {
            GestorCSV.escribir(catalogo, RUTA_CSV_EXPORTADO);
            System.out.println("Catálogo exportado a " + RUTA_CSV_EXPORTADO + " (" + catalogo.size() + " videojuegos).");
        } catch (IOException e) {
            System.out.println("Error al escribir el fichero CSV: " + e.getMessage());
        }
    }

    /** RF2: muestra todos los videojuegos cargados en memoria. */
    private static void mostrarCatalogo() {
        if (catalogo.isEmpty()) {
            System.out.println("El catálogo está vacío. Carga primero un CSV (opción 1) o un XML (opción 4).");
            return;
        }

        System.out.println("\n--- Catálogo de videojuegos (" + catalogo.size() + ") ---");
        for (Videojuego juego : catalogo) {
            System.out.println(juego);
        }
    }

    /** RF6: busca un videojuego por su id o por su título. */
    private static void buscarVideojuego() {
        if (catalogo.isEmpty()) {
            System.out.println("El catálogo está vacío. Carga primero un CSV (opción 1) o un XML (opción 4).");
            return;
        }

        System.out.print("¿Buscar por (1) id o (2) título? ");
        String tipo = teclado.nextLine().trim();

        switch (tipo) {
            case "1" -> buscarPorId();
            case "2" -> buscarPorTitulo();
            default -> System.out.println("Error: escribe 1 para buscar por id o 2 para buscar por título.");
        }
    }

    /** Busca el videojuego con el id exacto que escriba el usuario. */
    private static void buscarPorId() {
        System.out.print("Escribe el id: ");
        String entrada = teclado.nextLine().trim();

        int idBuscado;
        try {
            idBuscado = Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            System.out.println("Error: \"" + entrada + "\" no es un id válido. El id debe ser un número.");
            return;
        }

        for (Videojuego juego : catalogo) {
            if (juego.getId() == idBuscado) {
                System.out.println("Encontrado: " + juego);
                return;
            }
        }
        System.out.println("No hay ningún videojuego con el id " + idBuscado + ".");
    }

    /** Busca los videojuegos cuyo título contenga el texto escrito (sin distinguir mayúsculas). */
    private static void buscarPorTitulo() {
        System.out.print("Escribe el título o parte de él: ");
        String texto = teclado.nextLine().trim().toLowerCase();

        if (texto.isEmpty()) {
            System.out.println("Error: el texto de búsqueda no puede estar vacío.");
            return;
        }

        int encontrados = 0;
        for (Videojuego juego : catalogo) {
            if (juego.getTitulo().toLowerCase().contains(texto)) {
                System.out.println(juego);
                encontrados++;
            }
        }

        if (encontrados == 0) {
            System.out.println("No hay ningún videojuego cuyo título contenga \"" + texto + "\".");
        } else {
            System.out.println("Resultados encontrados: " + encontrados);
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