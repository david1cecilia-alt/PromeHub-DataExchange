package ficheros;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import modelo.Catalogo;
import modelo.Videojuego;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class GestorXML {

    private List<Videojuego> videojuegos;

    // Constructor
    public GestorXML() {
        videojuegos = new ArrayList<>();
    }

    // Devuelve la lista de videojuegos
    public List<Videojuego> getVideojuegos() {
        return videojuegos;
    }

    // Recibe la lista que se va a exportar.
    // Corrección: sin este método no había forma de pasarle el catálogo
    // y exportarXML() siempre exportaba una lista vacía.
    public void setVideojuegos(List<Videojuego> videojuegos) {
        this.videojuegos = videojuegos;
    }

    // =====================================================
    // 1. EXPORTAR XML
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
    // 2. CARGAR XML
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

        // Corrección: por defecto JAXB ignora los datos que no puede convertir
        // (por ejemplo <precio>gratis</precio>) y deja el valor a 0.
        // Devolviendo false, cualquier error de conversión detiene la lectura
        // y lanza una JAXBException que se gestiona en App.
        unmarshaller.setEventHandler(evento -> false);

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
    // 3. BUSCAR POR ID
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
    // 4. BUSCAR POR TÍTULO
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

