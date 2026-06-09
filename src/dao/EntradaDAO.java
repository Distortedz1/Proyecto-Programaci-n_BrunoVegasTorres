package dao;

import dto.EntradaDTO;
import model.Entrada;
import java.sql.SQLException;
import java.util.List;

public interface EntradaDAO {
    void insertar(Entrada entrada) throws SQLException;
    // listarTodos usa JOIN y devuelve DTOs
    List<EntradaDTO> listarTodos() throws SQLException;
    void eliminar(int id) throws SQLException;
}
