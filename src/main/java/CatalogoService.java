package promehub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class CatalogoService {

    private List<Videojuego> videojuegos;

    // Constructor
    public CatalogoService() {
        videojuegos = new ArrayList<>();
    }

    // Devuelve la lista de videojuegos
    public List<Videojuego> getVideojuegos() {
        return videojuegos;
    }


    // =====================================================
    // 1. CARGAR CSV
    // CSV -> JAVA
    // =====================================================

    public int cargarDesdeCSV(String nombreFichero) throws IOException {

        videojuegos.clear();

        File fichero = new File(nombreFichero);

        // Comprobar si existe
        if (!fichero.exists()) {
            throw new IOException("El fichero CSV no existe.");
        }

        int registros = 0;

        // Lectura secuencial
        try (BufferedReader br =
                     new BufferedReader(new FileReader(fichero))) {

            // Leer y saltar la cabecera
            String linea = br.readLine();

            // Leer línea por línea
            while ((linea = br.readLine()) != null) {

                // Ignorar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                try {

                    // Separar los datos por comas
                    String[] datos = linea.split(",");

                    // Un videojuego debe tener 7 datos
                    if (datos.length != 7) {

                        System.out.println(
                                "[ERROR] Registro CSV incorrecto: "
                                        + linea
                        );

                        continue;
                    }

                    // Convertir los datos
                    int id = Integer.parseInt(datos[0].trim());

                    String titulo = datos[1].trim();

                    String plataforma = datos[2].trim();

                    String genero = datos[3].trim();

                    double precio =
                            Double.parseDouble(datos[4].trim());

                    int stock =
                            Integer.parseInt(datos[5].trim());

                    String codigoProveedor =
                            datos[6].trim();

                    // Crear el objeto
                    Videojuego videojuego =
                            new Videojuego(
                                    id,
                                    titulo,
                                    plataforma,
                                    genero,
                                    precio,
                                    stock,
                                    codigoProveedor
                            );

                    // Añadirlo a la colección
                    videojuegos.add(videojuego);

                    registros++;

                } catch (NumberFormatException e) {

                    System.out.println(
                            "[ERROR] Error numérico en el registro: "
                                    + linea
                    );
                }
            }
        }

        return registros;
    }


    // =====================================================
    // 2. EXPORTAR XML
    // JAVA -> XML
    // =====================================================

    public void exportarXML(String nombreFichero)
            throws JAXBException {

        // Crear un objeto Catalogo
        Catalogo catalogo =
                new Catalogo(videojuegos);

        // Crear contexto JAXB
        JAXBContext context =
                JAXBContext.newInstance(Catalogo.class);

        // Crear Marshaller
        Marshaller marshaller =
                context.createMarshaller();

        // XML bonito y ordenado
        marshaller.setProperty(
                Marshaller.JAXB_FORMATTED_OUTPUT,
                true
        );

        // Generar el fichero XML
        marshaller.marshal(
                catalogo,
                new File(nombreFichero)
        );
    }


    // =====================================================
    // 3. CARGAR XML
    // XML -> JAVA
    // =====================================================

    public int cargarDesdeXML(String nombreFichero)
            throws JAXBException {

        // Crear contexto JAXB
        JAXBContext context =
                JAXBContext.newInstance(Catalogo.class);

        // Crear Unmarshaller
        Unmarshaller unmarshaller =
                context.createUnmarshaller();

        // Leer el XML
        Catalogo catalogo =
                (Catalogo) unmarshaller.unmarshal(
                        new File(nombreFichero)
                );

        // Recuperar los videojuegos
        videojuegos =
                catalogo.getVideojuegos();

        return videojuegos.size();
    }


    // =====================================================
    // 4. EXPORTAR CSV
    // JAVA -> CSV
    // =====================================================

    public void exportarCSV(String nombreFichero)
            throws IOException {

        try (PrintWriter writer =
                     new PrintWriter(
                             new FileWriter(nombreFichero))) {

            // Cabecera
            writer.println(
                    "id,titulo,plataforma,genero,precio,stock,codigoProveedor"
            );

            // Escribir cada videojuego
            for (Videojuego videojuego : videojuegos) {

                writer.println(
                        videojuego.getId() + ","
                                + videojuego.getTitulo() + ","
                                + videojuego.getPlataforma() + ","
                                + videojuego.getGenero() + ","
                                + videojuego.getPrecio() + ","
                                + videojuego.getStock() + ","
                                + videojuego.getCodigoProveedor()
                );
            }
        }
    }


    // =====================================================
    // 5. BUSCAR POR ID
    // =====================================================

    public Videojuego buscarPorId(int id) {

        for (Videojuego videojuego : videojuegos) {

            if (videojuego.getId() == id) {
                return videojuego;
            }
        }

        return null;
    }


    // =====================================================
    // 6. BUSCAR POR TÍTULO
    // =====================================================

    public List<Videojuego> buscarPorTitulo(String titulo) {

        List<Videojuego> resultados =
                new ArrayList<>();

        for (Videojuego videojuego : videojuegos) {

            if (videojuego.getTitulo()
                    .toLowerCase()
                    .contains(titulo.toLowerCase())) {

                resultados.add(videojuego);
            }
        }

        return resultados;
    }
}
