package promehub;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final String FICHERO_CSV = "videojuegos.csv";
    private static final String FICHERO_XML = "catalogo.xml";
    private static final String FICHERO_CSV_SALIDA = "videojuegos_exportado.csv";

    private static CatalogoService service = new CatalogoService();

    public static void main(String[] args) {

        int opcion;

        do {
            mostrarMenu();
            opcion = leerOpcion();

            switch (opcion) {

                case 1:
                    cargarDesdeCSV();
                    break;

                case 2:
                    mostrarCatalogo();
                    break;

                case 3:
                    exportarXML();
                    break;

                case 4:
                    cargarDesdeXML();
                    break;

                case 5:
                    exportarCSV();
                    break;

                case 6:
                    buscarVideojuego();
                    break;

                case 7:
                    informacionFicheros();
                    break;

                case 0:
                    System.out.println("\nSaliendo de PromeHub Data Exchange...");
                    break;

                default:
                    System.out.println(
                            "\n[ERROR] Opción no válida. "
                            + "Debe seleccionar una opción del 0 al 7."
                    );
            }

        } while (opcion != 0);

        scanner.close();
    }

    /**
     * Muestra el menú principal de la aplicación.
     */
    private static void mostrarMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("       PROMEHUB DATA EXCHANGE");
        System.out.println("========================================");
        System.out.println("1. Cargar catálogo desde CSV");
        System.out.println("2. Mostrar catálogo");
        System.out.println("3. Exportar catálogo a XML");
        System.out.println("4. Cargar catálogo desde XML");
        System.out.println("5. Exportar catálogo a CSV");
        System.out.println("6. Buscar videojuego");
        System.out.println("7. Información de ficheros");
        System.out.println("0. Salir");
        System.out.println("========================================");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Lee la opción introducida por el usuario.
     */
    private static int leerOpcion() {

        try {
            return Integer.parseInt(scanner.nextLine());

        } catch (NumberFormatException e) {

            return -1;
        }
    }

    /**
     * RF1 - Cargar catálogo desde CSV.
     */
    private static void cargarDesdeCSV() {

        System.out.println("\n--- CARGAR CATÁLOGO DESDE CSV ---");

        File fichero = new File(FICHERO_CSV);

        if (!fichero.exists()) {

            System.out.println(
                    "[ERROR] No se ha encontrado el fichero CSV: "
                            + fichero.getAbsolutePath()
            );

            return;
        }

        try {

            int registros = service.cargarDesdeCSV(FICHERO_CSV);

            System.out.println(
                    "[OK] Catálogo cargado correctamente."
            );

            System.out.println(
                    "[INFO] Registros procesados: " + registros
            );

        } catch (Exception e) {

            System.out.println(
                    "[ERROR] No se ha podido cargar el catálogo desde CSV."
            );

            System.out.println(
                    "[DETALLE] " + e.getMessage()
            );
        }
    }

    /**
     * RF2 - Mostrar catálogo.
     */
    private static void mostrarCatalogo() {

        System.out.println("\n--- CATÁLOGO DE VIDEOJUEGOS ---");

        List<Videojuego> videojuegos = service.getVideojuegos();

        if (videojuegos == null || videojuegos.isEmpty()) {

            System.out.println(
                    "[INFO] No hay videojuegos cargados en el catálogo."
            );

            return;
        }

        System.out.println(
                "Total de videojuegos: " + videojuegos.size()
        );

        System.out.println();

        for (Videojuego videojuego : videojuegos) {

            System.out.println("----------------------------------------");
            System.out.println("ID: " + videojuego.getId());
            System.out.println("Título: " + videojuego.getTitulo());
            System.out.println("Plataforma: " + videojuego.getPlataforma());
            System.out.println("Género: " + videojuego.getGenero());
            System.out.println("Precio: " + videojuego.getPrecio() + " €");
            System.out.println("Stock: " + videojuego.getStock());
            System.out.println(
                    "Código proveedor: "
                            + videojuego.getCodigoProveedor()
            );
        }

        System.out.println("----------------------------------------");
    }

    /**
     * RF3 - Exportar catálogo a XML.
     */
    private static void exportarXML() {

        System.out.println("\n--- EXPORTAR CATÁLOGO A XML ---");

        if (service.getVideojuegos().isEmpty()) {

            System.out.println(
                    "[ERROR] No hay videojuegos cargados. "
                            + "Cargue primero un catálogo."
            );

            return;
        }

        try {

            service.exportarXML(FICHERO_XML);

            System.out.println(
                    "[OK] Catálogo exportado correctamente a XML."
            );

            System.out.println(
                    "[INFO] Fichero generado: "
                            + new File(FICHERO_XML).getAbsolutePath()
            );

        } catch (Exception e) {

            System.out.println(
                    "[ERROR] Se ha producido un error al generar el XML."
            );

            System.out.println(
                    "[DETALLE] " + e.getMessage()
            );
        }
    }

    /**
     * RF4 - Cargar catálogo desde XML.
     */
    private static void cargarDesdeXML() {

        System.out.println("\n--- CARGAR CATÁLOGO DESDE XML ---");

        File fichero = new File(FICHERO_XML);

        if (!fichero.exists()) {

            System.out.println(
                    "[ERROR] No se ha encontrado el fichero XML: "
                            + fichero.getAbsolutePath()
            );

            return;
        }

        try {

            int registros = service.cargarDesdeXML(FICHERO_XML);

            System.out.println(
                    "[OK] Catálogo XML cargado correctamente."
            );

            System.out.println(
                    "[INFO] Videojuegos recuperados: "
                            + registros
            );

        } catch (Exception e) {

            System.out.println(
                    "[ERROR] No se ha podido procesar el fichero XML."
            );

            System.out.println(
                    "[DETALLE] " + e.getMessage()
            );
        }
    }

    /**
     * RF5 - Exportar catálogo a CSV.
     */
    private static void exportarCSV() {

        System.out.println("\n--- EXPORTAR CATÁLOGO A CSV ---");

        if (service.getVideojuegos().isEmpty()) {

            System.out.println(
                    "[ERROR] No hay videojuegos cargados. "
                            + "Cargue primero un catálogo."
            );

            return;
        }

        try {

            service.exportarCSV(FICHERO_CSV_SALIDA);

            System.out.println(
                    "[OK] Catálogo exportado correctamente a CSV."
            );

            System.out.println(
                    "[INFO] Fichero generado: "
                            + new File(FICHERO_CSV_SALIDA)
                                    .getAbsolutePath()
            );

        } catch (Exception e) {

            System.out.println(
                    "[ERROR] No se ha podido generar el fichero CSV."
            );

            System.out.println(
                    "[DETALLE] " + e.getMessage()
            );
        }
    }

    /**
     * RF6 - Buscar videojuego por ID o título.
     */
    private static void buscarVideojuego() {

        System.out.println("\n--- BUSCAR VIDEOJUEGO ---");

        if (service.getVideojuegos().isEmpty()) {

            System.out.println(
                    "[ERROR] No hay videojuegos cargados."
            );

            return;
        }

        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por título");
        System.out.print("Seleccione el tipo de búsqueda: ");

        int opcion = leerOpcion();

        switch (opcion) {

            case 1:
                buscarPorId();
                break;

            case 2:
                buscarPorTitulo();
                break;

            default:
                System.out.println(
                        "[ERROR] Tipo de búsqueda no válido."
                );
        }
    }

    /**
     * Busca un videojuego mediante su identificador.
     */
    private static void buscarPorId() {

        System.out.print("Introduzca el ID del videojuego: ");

        String textoId = scanner.nextLine();

        try {

            int id = Integer.parseInt(textoId);

            Videojuego videojuego = service.buscarPorId(id);

            if (videojuego == null) {

                System.out.println(
                        "[INFO] No se ha encontrado ningún "
                                + "videojuego con ID " + id
                );

            } else {

                mostrarVideojuego(videojuego);
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "[ERROR] El ID debe ser un número entero."
            );
        }
    }

    /**
     * Busca un videojuego mediante su título.
     */
    private static void buscarPorTitulo() {

        System.out.print("Introduzca el título a buscar: ");

        String titulo = scanner.nextLine();

        if (titulo.isBlank()) {

            System.out.println(
                    "[ERROR] El título no puede estar vacío."
            );

            return;
        }

        List<Videojuego> resultados =
                service.buscarPorTitulo(titulo);

        if (resultados.isEmpty()) {

            System.out.println(
                    "[INFO] No se han encontrado videojuegos "
                            + "con ese título."
            );

        } else {

            System.out.println(
                    "[OK] Se han encontrado "
                            + resultados.size()
                            + " resultado(s)."
            );

            for (Videojuego videojuego : resultados) {

                mostrarVideojuego(videojuego);
            }
        }
    }

    /**
     * Muestra los datos de un único videojuego.
     */
    private static void mostrarVideojuego(Videojuego videojuego) {

        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("ID: " + videojuego.getId());
        System.out.println("Título: " + videojuego.getTitulo());
        System.out.println("Plataforma: " + videojuego.getPlataforma());
        System.out.println("Género: " + videojuego.getGenero());
        System.out.println("Precio: " + videojuego.getPrecio() + " €");
        System.out.println("Stock: " + videojuego.getStock());
        System.out.println(
                "Código proveedor: "
                        + videojuego.getCodigoProveedor()
        );
        System.out.println("----------------------------------------");
    }

    /**
     * RF7 - Información de ficheros.
     */
    private static void informacionFicheros() {

        System.out.println("\n--- INFORMACIÓN DE FICHEROS ---");

        mostrarInformacionFichero(FICHERO_CSV);
        mostrarInformacionFichero(FICHERO_XML);
        mostrarInformacionFichero(FICHERO_CSV_SALIDA);
    }

    /**
     * Muestra existencia, tamaño y ruta de un fichero.
     */
    private static void mostrarInformacionFichero(String nombre) {

        File fichero = new File(nombre);

        System.out.println();
        System.out.println("Fichero: " + nombre);
        System.out.println("Existe: "
                + (fichero.exists() ? "Sí" : "No"));

        if (fichero.exists()) {

            System.out.println(
                    "Tamaño: " + fichero.length() + " bytes"
            );

        } else {

            System.out.println("Tamaño: No disponible");
        }

        System.out.println(
                "Ruta: " + fichero.getAbsolutePath()
        );
    }
}
