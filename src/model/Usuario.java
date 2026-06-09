package model;

public class Usuario {

    private int    id;
    private String username;
    private String password;
    private String email;
    private String nombre;
    private String apellidos;
    private String dni;
    private String rol;

    public Usuario() {}

    public Usuario(int id, String username, String password, String email,
                   String nombre, String apellidos, String dni, String rol) {
        this.id        = id;
        this.username  = username;
        this.password  = password;
        this.email     = email;
        this.nombre    = nombre;
        this.apellidos = apellidos;
        this.dni       = dni;
        this.rol       = rol;
    }

    public Usuario(String username, String password, String email,
                   String nombre, String apellidos, String dni, String rol) {
        this.username  = username;
        this.password  = password;
        this.email     = email;
        this.nombre    = nombre;
        this.apellidos = apellidos;
        this.dni       = dni;
        this.rol       = rol;
    }

    public int    getId()        { return id; }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public String getEmail()     { return email; }
    public String getNombre()    { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getDni()       { return dni; }
    public String getRol()       { return rol; }

    public void setId(int id)                { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email)       { this.email = email; }
    public void setNombre(String nombre)     { this.nombre = nombre; }
    public void setApellidos(String a)       { this.apellidos = a; }
    public void setDni(String dni)           { this.dni = dni; }
    public void setRol(String rol)           { this.rol = rol; }

    @Override
    public String toString() {
        return nombre + " " + apellidos + " (" + username + ")";
    }
}
