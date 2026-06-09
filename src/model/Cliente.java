package model;

public class Cliente extends Usuario {

    private String telefono;
    private int    puntos;

    public Cliente() {}

    public Cliente(int id, String username, String password, String email,
                   String nombre, String apellidos, String dni,
                   String telefono, int puntos) {
        super(id, username, password, email, nombre, apellidos, dni, "cliente");
        this.telefono = telefono;
        this.puntos   = puntos;
    }

    public Cliente(String username, String password, String email,
                   String nombre, String apellidos, String dni,
                   String telefono, int puntos) {
        super(username, password, email, nombre, apellidos, dni, "cliente");
        this.telefono = telefono;
        this.puntos   = puntos;
    }

    public String getTelefono() { return telefono; }
    public int    getPuntos()   { return puntos; }

    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setPuntos(int puntos)        { this.puntos = puntos; }
}
