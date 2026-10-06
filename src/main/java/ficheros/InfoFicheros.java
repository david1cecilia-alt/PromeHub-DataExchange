package ficheros;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * RF7: muestra información de los ficheros que usa la aplicación
 * (si existen, su tamaño y su ruta completa).
 */
public class InfoFicheros {

    /** Muestra por consola la información de un fichero. */
    public static void mostrarInfo(Path ruta) {
        System.out.println("Fichero: " + ruta.getFileName());
        System.out.println("  Ruta:   " + ruta.toAbsolutePath());

        if (!Files.exists(ruta)) {
            System.out.println("  Existe: no");
            return;
        }

        System.out.println("  Existe: sí");
        try {
            System.out.println("  Tamaño: " + Files.size(ruta) + " bytes");
        } catch (IOException e) {
            System.out.println("  Error: no se ha podido leer el tamaño del fichero (" + e.getMessage() + ")");
        }
    }
}