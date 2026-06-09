package dao;

import model.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {
    Usuario validar(String username, String password) throws SQLException;
    void registrar(Usuario usuario) throws SQLException;
    List<Usuario> listarTodos() throws SQLException;
    void actualizar(Usuario usuario) throws SQLException;
    void eliminar(int id) throws SQLException;
    void cambiarPassword(int id, String nuevaPassword) throws SQLException;
}
