package model;

/**
 * Representa una sesión (proyección) concreta de una película.
 * Una película puede tener varias sesiones en distintas fechas/horas/salas.
 */
public class Sesion {

    private int    id;
    private int    peliculaId;
    private String fecha;   // formato "YYYY-MM-DD"
    private String hora;    // formato "HH:MM"
    private String sala;
    private int    aforo;

    public Sesion() {}

    public Sesion(int id, int peliculaId, String fecha, String hora, String sala, int aforo) {
        this.id         = id;
        this.peliculaId = peliculaId;
        this.fecha      = fecha;
        this.hora       = hora;
        this.sala       = sala;
        this.aforo      = aforo;
    }

    public Sesion(int peliculaId, String fecha, String hora, String sala, int aforo) {
        this.peliculaId = peliculaId;
        this.fecha      = fecha;
        this.hora       = hora;
        this.sala       = sala;
        this.aforo      = aforo;
    }

    public int    getId()         { return id; }
    public int    getPeliculaId() { return peliculaId; }
    public String getFecha()      { return fecha; }
    public String getHora()       { return hora; }
    public String getSala()       { return sala; }
    public int    getAforo()      { return aforo; }

    public void setId(int id)             { this.id = id; }
    public void setPeliculaId(int pid)    { this.peliculaId = pid; }
    public void setFecha(String fecha)    { this.fecha = fecha; }
    public void setHora(String hora)      { this.hora = hora; }
    public void setSala(String sala)      { this.sala = sala; }
    public void setAforo(int aforo)       { this.aforo = aforo; }

    /** Se muestra en el JComboBox de selección de sesión. */
    @Override
    public String toString() {
        return fecha + "  " + hora + "  — " + sala;
    }
}
