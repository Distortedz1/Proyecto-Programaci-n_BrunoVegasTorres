package dao;

import db.ConexionDB;
import model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void registrar(Cliente cliente) throws SQLException {
        if (cliente.getUsername() == null || cliente.getUsername().isEmpty()) {
            throw new SQLException("El username no puede estar vacío");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().isEmpty()) {
            throw new SQLException("El teléfono no puede estar vacío");
        }
        
        String sqlUsuario = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) " +
                            "VALUES (?, ?, ?, ?, ?, ?, 'cliente')";
        String sqlCliente = "INSERT INTO clientes (usuario_id, telefono, puntos) VALUES (?, ?, ?)";

        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);

            // 1. Insertar en usuarios
            try (PreparedStatement ps = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cliente.getUsername());
                ps.setString(2, cliente.getPassword());
                ps.setString(3, cliente.getEmail());
                ps.setString(4, cliente.getNombre());
                ps.setString(5, cliente.getApellidos());
                ps.setString(6, cliente.getDni());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        cliente.setId(keys.getInt(1));
                    }
                }
            }

            // 2. Insertar en clientes
            try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                ps.setInt(1, cliente.getId());
                ps.setString(2, cliente.getTelefono());
                ps.setInt(3, cliente.getPuntos());
                ps.executeUpdate();
            }

            con.commit();

        } catch (SQLException e) {
            if (con != null) con.rollback();
            System.out.println("ERROR al registrar cliente: " + e.getMessage());
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    @Override
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password, u.email, u.nombre, u.apellidos, u.dni, " +
                     "c.telefono, c.puntos " +
                     "FROM usuarios u JOIN clientes c ON u.id = c.usuario_id " +
                     "ORDER BY u.apellidos, u.nombre";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("nombre"),
                    rs.getString("apellidos"),
                    rs.getString("dni"),
                    rs.getString("telefono"),
                    rs.getInt("puntos")
                ));
            }
        } catch (SQLException e) {
            System.out.println("ERROR al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.id, u.username, u.password, u.email, u.nombre, u.apellidos, u.dni, " +
                     "c.telefono, c.puntos " +
                     "FROM usuarios u JOIN clientes c ON u.id = c.usuario_id WHERE u.id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getInt("puntos")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        String sqlU = "UPDATE usuarios SET username=?, email=?, nombre=?, apellidos=?, dni=? WHERE id=?";
        String sqlC = "UPDATE clientes SET telefono=? WHERE usuario_id=?";

        Connection con = null;
        try {
            con = ConexionDB.getConexion();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlU)) {
                ps.setString(1, cliente.getUsername());
                ps.setString(2, cliente.getEmail());
                ps.setString(3, cliente.getNombre());
                ps.setString(4, cliente.getApellidos());
                ps.setString(5, cliente.getDni());
                ps.setInt(6, cliente.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(sqlC)) {
                ps.setString(1, cliente.getTelefono());
                ps.setInt(2, cliente.getId());
                ps.executeUpdate();
            }

            con.commit();

        } catch (SQLException e) {
            if (con != null) con.rollback();
            System.out.println("ERROR al actualizar cliente: " + e.getMessage());
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
            System.out.println("ERROR al eliminar cliente: " + e.getMessage());
            throw e;
        }
    }
}
