package modelo;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class Videojuego {

    @XmlAttribute
    private int id;

    @XmlElement
    private String titulo;

    @XmlElement
    private String plataforma;

    @XmlElement
    private String genero;

    @XmlElement
    private double precio;

    @XmlElement
    private int stock;

    @XmlTransient
    private String codigoProveedor;

    public Videojuego() {
    }

    public Videojuego(int id, String titulo, String plataforma,String genero, 
        double precio, int stock,String codigoProveedor) {

        this.id = id;
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.genero = genero;
        this.precio = precio;
        this.stock = stock;
        this.codigoProveedor = codigoProveedor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    @Override
    public String toString() {

        return "ID: " + id
                + " | Título: " + titulo
                + " | Plataforma: " + plataforma
                + " | Género: " + genero
                + " | Precio: " + precio
                + " € | Stock: " + stock
                + " | Código proveedor: " + codigoProveedor;
    }
}
