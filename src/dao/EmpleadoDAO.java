package dao;

import model.Empleado;
import java.sql.SQLException;
import java.util.List;

public interface EmpleadoDAO {
    void registrar(Empleado empleado) throws SQLException;
    List<Empleado> listarTodos() throws SQLException;
    void actualizar(Empleado empleado) throws SQLException;
    void eliminar(int id) throws SQLException;
}
