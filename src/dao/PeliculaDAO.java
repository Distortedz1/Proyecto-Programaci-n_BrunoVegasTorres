package dao;

import model.Pelicula;
import java.sql.SQLException;
import java.util.List;

public interface PeliculaDAO {
    void insertar(Pelicula pelicula) throws SQLException;
    List<Pelicula> listarTodos() throws SQLException;
    Pelicula buscarPorId(int id) throws SQLException;
    void actualizar(Pelicula pelicula) throws SQLException;
    void eliminar(int id) throws SQLException;
}
