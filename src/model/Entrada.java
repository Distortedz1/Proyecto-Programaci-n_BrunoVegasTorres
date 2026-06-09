package model;

public class Entrada {

    private int id;
    private int clienteId;
    private int sesionId;   // ahora referencia a sesiones, no a peliculas directamente
    private int cantidad;

    public Entrada() {}

    public Entrada(int id, int clienteId, int sesionId, int cantidad) {
        this.id        = id;
        this.clienteId = clienteId;
        this.sesionId  = sesionId;
        this.cantidad  = cantidad;
    }

    public Entrada(int clienteId, int sesionId, int cantidad) {
        this.clienteId = clienteId;
        this.sesionId  = sesionId;
        this.cantidad  = cantidad;
    }

    public int getId()        { return id; }
    public int getClienteId() { return clienteId; }
    public int getSesionId()  { return sesionId; }
    public int getCantidad()  { return cantidad; }

    public void setId(int id)           { this.id = id; }
    public void setClienteId(int c)     { this.clienteId = c; }
    public void setSesionId(int s)      { this.sesionId = s; }
    public void setCantidad(int cant)   { this.cantidad = cant; }
}
