package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import ficheros.GestorCSV;
import ficheros.InfoFicheros;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import modelo.Videojuego;

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
                case 3 -> exportarAXML();
                case 4 -> cargarDesdeXML();
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

    /** RF3: exporta el catálogo que haya en memoria a XML. */
    private static void exportarAXML() {
        if (catalogo.isEmpty()) {
            System.out.println("El catálogo está vacío. Carga primero un CSV (opción 1) o un XML (opción 4).");
            return;
        }

        try {
            CatalogoXML datosXML = new CatalogoXML(catalogo);

            JAXBContext contexto = JAXBContext.newInstance(CatalogoXML.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(datosXML, RUTA_XML.toFile());

            System.out.println("Catálogo exportado a " + RUTA_XML + " (" + catalogo.size() + " videojuegos).");
        } catch (JAXBException e) {
            System.out.println("Error al exportar el catálogo a XML: " + e.getMessage());
        }
    }

    /** RF4: carga el catálogo desde el XML. */
    private static void cargarDesdeXML() {
        if (!Files.exists(RUTA_XML)) {
            System.out.println("Error: no se puede cargar el catálogo. El fichero " + RUTA_XML + " no existe.");
            return;
        }

        try {
            JAXBContext contexto = JAXBContext.newInstance(CatalogoXML.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();

            CatalogoXML datosXML = (CatalogoXML) unmarshaller.unmarshal(RUTA_XML.toFile());

            List<Videojuego> juegosCargados = datosXML.getVideojuegos();

            if (!catalogoXMLValido(juegosCargados)) {
                System.out.println("Error: el XML contiene datos de videojuegos no válidos.");
                return;
            }

            catalogo = juegosCargados;

            if (catalogo.isEmpty()) {
                System.out.println("Aviso: el fichero XML no contiene ningún videojuego.");
            } else {
                System.out.println("Catálogo cargado desde " + RUTA_XML + ".");
                System.out.println("Videojuegos cargados: " + catalogo.size());
            }
        } catch (JAXBException e) {
            System.out.println("Error al leer el fichero XML: " + e.getMessage());
        }
    }

    /**
     * Comprueba los datos que JAXB ha cargado desde XML.
     * Se hace aquí para no modificar las clases del modelo ni otros ficheros del proyecto.
     */
    private static boolean catalogoXMLValido(List<Videojuego> juegos) {
        if (juegos == null) {
            return false;
        }

        Set<Integer> ids = new HashSet<>();

        for (Videojuego juego : juegos) {
            if (juego == null
                    || juego.getTitulo() == null
                    || juego.getTitulo().isBlank()
                    || juego.getPlataforma() == null
                    || juego.getGenero() == null
                    || juego.getPrecio() < 0
                    || juego.getStock() < 0
                    || !ids.add(juego.getId())) {
                return false;
            }
        }

        return true;
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

    /**
     * Clase auxiliar para que JAXB pueda representar el catálogo.
     * Está dentro de App para cumplir la condición de modificar únicamente la app.
     */
    @XmlRootElement(name = "catalogo")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CatalogoXML {

        @XmlElement(name = "videojuego")
        private List<Videojuego> videojuegos;

        public CatalogoXML() {
            videojuegos = new ArrayList<>();
        }

        public CatalogoXML(List<Videojuego> videojuegos) {
            this.videojuegos = videojuegos;
        }

        public List<Videojuego> getVideojuegos() {
            return videojuegos;
        }

        public void setVideojuegos(List<Videojuego> videojuegos) {
            this.videojuegos = videojuegos;
        }
    }
}
