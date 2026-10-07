package ficheros;

import modelo.Videojuego;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lectura y escritura del catálogo en formato CSV.
 * Formato: id,titulo,plataforma,genero,precio,stock,codigoProveedor (primera línea = cabecera).
 */
public class GestorCSV {

    private static final String SEPARADOR = ",";
    private static final int NUMERO_CAMPOS = 7;
    private static final String CABECERA = "id,titulo,plataforma,genero,precio,stock,codigoProveedor";

    /**
     * RF1: lee el CSV de forma secuencial (línea a línea) y crea un Videojuego por cada registro válido.
     * Los registros incorrectos se avisan por consola y se descartan, sin detener la lectura.
     *
     * @throws FileNotFoundException si el fichero no existe
     * @throws IOException si se produce un error al leer el fichero
     */
    public static List<Videojuego> leer(Path ruta) throws IOException {
        if (!Files.exists(ruta)) {
            throw new FileNotFoundException("El fichero " + ruta + " no existe.");
        }

        List<Videojuego> juegos = new ArrayList<>();
        Set<Integer> idsLeidos = new HashSet<>(); // para detectar ids duplicados
        int numeroLinea = 0;
        int descartados = 0;

        // try-with-resources: el fichero se cierra solo, aunque haya un error
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea = lector.readLine(); // la primera línea es la cabecera: se lee y se ignora
            numeroLinea++;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;

                if (linea.isBlank()) {
                    continue; // las líneas vacías no cuentan como registros
                }

                try {
                    Videojuego juego = convertirLinea(linea);

                    if (!idsLeidos.add(juego.getId())) {
                        throw new IllegalArgumentException("el id " + juego.getId() + " está repetido");
                    }
                    juegos.add(juego);

                } catch (NumberFormatException e) {
                    System.out.println("Línea " + numeroLinea + " descartada: el id, el precio o el stock no es un número válido.");
                    descartados++;
                } catch (IllegalArgumentException e) {
                    System.out.println("Línea " + numeroLinea + " descartada: " + e.getMessage() + ".");
                    descartados++;
                }
            }
        }

        System.out.println("Registros procesados: " + (juegos.size() + descartados)
                + " | válidos: " + juegos.size() + " | descartados: " + descartados);
        return juegos;
    }

    /**
     * Convierte una línea del CSV en un objeto Videojuego.
     *
     * @throws NumberFormatException si id, precio o stock no son números
     * @throws IllegalArgumentException si al registro le faltan o le sobran datos
     */
    private static Videojuego convertirLinea(String linea) {
        // El -1 hace que se conserven los campos vacíos del final (p. ej. "...,12,")
        String[] campos = linea.split(SEPARADOR, -1);

        if (campos.length != NUMERO_CAMPOS) {
            throw new IllegalArgumentException("tiene " + campos.length + " campos y se esperaban " + NUMERO_CAMPOS);
        }

        int id = Integer.parseInt(campos[0].trim());
        String titulo = campos[1].trim();
        String plataforma = campos[2].trim();
        String genero = campos[3].trim();
        double precio = Double.parseDouble(campos[4].trim());
        int stock = Integer.parseInt(campos[5].trim());
        String codigoProveedor = campos[6].trim();

        if (titulo.isEmpty()) {
            throw new IllegalArgumentException("el título está vacío");
        }
        if (precio < 0 || stock < 0) {
            throw new IllegalArgumentException("el precio y el stock no pueden ser negativos");
        }

        return new Videojuego(id, titulo, plataforma, genero, precio, stock, codigoProveedor);
    }

    /**
     * RF5: escribe el catálogo en un fichero CSV nuevo, con la misma cabecera que el original.
     * Si el catálogo viene del XML, codigoProveedor queda vacío porque el XML no lo contiene.
     *
     * @throws IOException si no se puede escribir el fichero
     */
    public static void escribir(List<Videojuego> juegos, Path ruta) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            escritor.write(CABECERA);
            escritor.newLine();

            for (Videojuego juego : juegos) {
                String codigo = (juego.getCodigoProveedor() == null) ? "" : juego.getCodigoProveedor();

                escritor.write(juego.getId() + SEPARADOR
                        + juego.getTitulo() + SEPARADOR
                        + juego.getPlataforma() + SEPARADOR
                        + juego.getGenero() + SEPARADOR
                        + juego.getPrecio() + SEPARADOR
                        + juego.getStock() + SEPARADOR
                        + codigo);
                escritor.newLine();
            }
        }
    }
}