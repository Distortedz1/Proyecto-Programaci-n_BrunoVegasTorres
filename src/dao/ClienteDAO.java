package dao;

import model.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {
    // Inserta en usuarios + clientes en una transacción
    void registrar(Cliente cliente) throws SQLException;
    List<Cliente> listarTodos() throws SQLException;
    Cliente buscarPorId(int id) throws SQLException;
    void actualizar(Cliente cliente) throws SQLException;
    void eliminar(int id) throws SQLException;
}
