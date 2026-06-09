package model;

public class Pelicula {

    private int    id;
    private String titulo;
    private String genero;
    private int    duracion;
    private String director;
    private int    anio;
    private double precio;

    public Pelicula() {}

    public Pelicula(int id, String titulo, String genero, int duracion,
                    String director, int anio, double precio) {
        this.id       = id;
        this.titulo   = titulo;
        this.genero   = genero;
        this.duracion = duracion;
        this.director = director;
        this.anio     = anio;
        this.precio   = precio;
    }

    public Pelicula(String titulo, String genero, int duracion,
                    String director, int anio, double precio) {
        this.titulo   = titulo;
        this.genero   = genero;
        this.duracion = duracion;
        this.director = director;
        this.anio     = anio;
        this.precio   = precio;
    }

    public int    getId()       { return id; }
    public String getTitulo()   { return titulo; }
    public String getGenero()   { return genero; }
    public int    getDuracion() { return duracion; }
    public String getDirector() { return director; }
    public int    getAnio()     { return anio; }
    public double getPrecio()   { return precio; }

    public void setId(int id)             { this.id = id; }
    public void setTitulo(String t)       { this.titulo = t; }
    public void setGenero(String g)       { this.genero = g; }
    public void setDuracion(int d)        { this.duracion = d; }
    public void setDirector(String d)     { this.director = d; }
    public void setAnio(int a)            { this.anio = a; }
    public void setPrecio(double p)       { this.precio = p; }

    @Override
    public String toString() {
        return titulo + " (" + anio + ")";
    }
}
