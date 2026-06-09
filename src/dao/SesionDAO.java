package dao;

import model.Sesion;
import java.sql.SQLException;
import java.util.List;

public interface SesionDAO {

    /** Devuelve todas las sesiones futuras (o de hoy en adelante) de una película. */
    List<Sesion> listarPorPelicula(int peliculaId) throws SQLException;

    /** Devuelve todas las sesiones (para el panel de empleado). */
    List<Sesion> listarTodas() throws SQLException;

    void insertar(Sesion sesion) throws SQLException;
    void eliminar(int id) throws SQLException;
}
