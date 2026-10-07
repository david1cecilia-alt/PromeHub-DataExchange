import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "catalogo")
@XmlAccessorType(XmlAccessType.FIELD)
public class Catalogo {

    @XmlElement(name = "videojuego")
    private List<Videojuego> videojuegos;

    // Constructor vacío necesario para JAXB
    public Catalogo() {
        videojuegos = new ArrayList<>();
    }

    // Constructor para crear un catálogo con una lista
    public Catalogo(List<Videojuego> videojuegos) {
        this.videojuegos = videojuegos;
    }

    public List<Videojuego> getVideojuegos() {
        return videojuegos;
    }

    public void setVideojuegos(List<Videojuego> videojuegos) {
        this.videojuegos = videojuegos;
    }
}
