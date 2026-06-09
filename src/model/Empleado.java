package model;

public class Empleado extends Usuario {

    private String numEmpleado;
    private String puesto;

    public Empleado() {}

    public Empleado(int id, String username, String password, String email,
                    String nombre, String apellidos, String dni,
                    String numEmpleado, String puesto) {
        super(id, username, password, email, nombre, apellidos, dni, "empleado");
        this.numEmpleado = numEmpleado;
        this.puesto      = puesto;
    }

    public Empleado(String username, String password, String email,
                    String nombre, String apellidos, String dni,
                    String numEmpleado, String puesto) {
        super(username, password, email, nombre, apellidos, dni, "empleado");
        this.numEmpleado = numEmpleado;
        this.puesto      = puesto;
    }

    public String getNumEmpleado() { return numEmpleado; }
    public String getPuesto()      { return puesto; }

    public void setNumEmpleado(String n) { this.numEmpleado = n; }
    public void setPuesto(String p)      { this.puesto = p; }
}
