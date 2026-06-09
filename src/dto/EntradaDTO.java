package dto;

// DTO para mostrar entradas con datos del cliente y película resueltos por JOIN.
// Solo se usa en la vista; no contiene lógica de negocio.
public class EntradaDTO {

    private int    id;
    private String clienteNombre;
    private String tituloPelicula;
    private int    cantidad;
    private double precioUnidad;
    private double subtotal;
    private String fecha;
    private String sala;

    public EntradaDTO() {}

    public EntradaDTO(int id, String clienteNombre, String tituloPelicula,
                      int cantidad, double precioUnidad, double subtotal,
                      String fecha, String sala) {
        this.id             = id;
        this.clienteNombre  = clienteNombre;
        this.tituloPelicula = tituloPelicula;
        this.cantidad       = cantidad;
        this.precioUnidad   = precioUnidad;
        this.subtotal       = subtotal;
        this.fecha          = fecha;
        this.sala           = sala;
    }

    public int    getId()             { return id; }
    public String getClienteNombre()  { return clienteNombre; }
    public String getTituloPelicula() { return tituloPelicula; }
    public int    getCantidad()       { return cantidad; }
    public double getPrecioUnidad()   { return precioUnidad; }
    public double getSubtotal()       { return subtotal; }
    public String getFecha()          { return fecha; }
    public String getSala()           { return sala; }

    public void setId(int id)                      { this.id = id; }
    public void setClienteNombre(String c)         { this.clienteNombre = c; }
    public void setTituloPelicula(String t)        { this.tituloPelicula = t; }
    public void setCantidad(int cantidad)          { this.cantidad = cantidad; }
    public void setPrecioUnidad(double p)          { this.precioUnidad = p; }
    public void setSubtotal(double s)              { this.subtotal = s; }
    public void setFecha(String fecha)             { this.fecha = fecha; }
    public void setSala(String sala)               { this.sala = sala; }
}
