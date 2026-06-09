package dao;

import db.ConexionDB;
import model.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAOImpl implements EmpleadoDAO {

    @Override
    public void registrar(Empleado empleado) throws SQLException {
        if (empleado.getUsername() == null || empleado.getUsername().isEmpty()) {
            throw new SQLException("El username no puede estar vacío");
        }
        if (empleado.getNumEmpleado() == null || empleado.getNumEmpleado().isEmpty()) {
            throw new SQLException("El número de empleado no puede estar vacío");
        }
        if (empleado.getPuesto() == null || empleado.getPuesto().isEmpty()) {
            throw new SQLException("El puesto no puede estar vacío");
        }
        
        String sqlUsuario  = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) " +
                             "VALUES (?, ?, ?, ?, ?, ?, 'empleado')";
        String sqlEmpleado = "INSERT INTO empleados (usuario_id, num_empleado, puesto) VALUES (?, ?, ?)";

        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, empleado.getUsername());
                ps.setString(2, empleado.getPassword());
                ps.setString(3, empleado.getEmail());
                ps.setString(4, empleado.getNombre());
                ps.setString(5, empleado.getApellidos());
                ps.setString(6, empleado.getDni());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        empleado.setId(keys.getInt(1));
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlEmpleado)) {
                ps.setInt(1, empleado.getId());
                ps.setString(2, empleado.getNumEmpleado());
                ps.setString(3, empleado.getPuesto());
                ps.executeUpdate();
            }

            con.commit();

        } catch (SQLException e) {
            if (con != null) con.rollback();
            System.out.println("ERROR al registrar empleado: " + e.getMessage());
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    @Override
    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password, u.email, u.nombre, u.apellidos, u.dni, " +
                     "e.num_empleado, e.puesto " +
                     "FROM usuarios u JOIN empleados e ON u.id = e.usuario_id " +
                     "ORDER BY u.apellidos, u.nombre";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Empleado(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("nombre"),
                    rs.getString("apellidos"),
                    rs.getString("dni"),
                    rs.getString("num_empleado"),
                    rs.getString("puesto")
                ));
            }
        } catch (SQLException e) {
            System.out.println("ERROR al listar empleados: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Empleado empleado) throws SQLException {
        String sqlU = "UPDATE usuarios SET username=?, email=?, nombre=?, apellidos=?, dni=? WHERE id=?";
        String sqlE = "UPDATE empleados SET num_empleado=?, puesto=? WHERE usuario_id=?";

        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlU)) {
                ps.setString(1, empleado.getUsername());
                ps.setString(2, empleado.getEmail());
                ps.setString(3, empleado.getNombre());
                ps.setString(4, empleado.getApellidos());
                ps.setString(5, empleado.getDni());
                ps.setInt(6, empleado.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(sqlE)) {
                ps.setString(1, empleado.getNumEmpleado());
                ps.setString(2, empleado.getPuesto());
                ps.setInt(3, empleado.getId());
                ps.executeUpdate();
            }

            con.commit();

        } catch (SQLException e) {
            if (con != null) con.rollback();
            System.out.println("ERROR al actualizar empleado: " + e.getMessage());
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ERROR al eliminar empleado: " + e.getMessage());
            throw e;
        }
    }
}
