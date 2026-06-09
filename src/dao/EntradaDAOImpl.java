package dao;

import db.ConexionDB;
import dto.EntradaDTO;
import model.Entrada;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntradaDAOImpl implements EntradaDAO {

    @Override
    public void insertar(Entrada entrada) throws SQLException {
        // entrada.getSesionId() en lugar de pelicula_id/fecha/sala (ahora están en sesiones)
        String sql = "INSERT INTO entradas (cliente_id, sesion_id, cantidad) VALUES (?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entrada.getClienteId());
            ps.setInt(2, entrada.getSesionId());
            ps.setInt(3, entrada.getCantidad());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entrada.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("ERROR al insertar entrada: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<EntradaDTO> listarTodos() throws SQLException {
        List<EntradaDTO> lista = new ArrayList<>();

        // JOIN con sesiones para obtener fecha, hora y sala; JOIN con peliculas para título y precio
        String sql =
            "SELECT e.id, " +
            "CONCAT(u.nombre, ' ', u.apellidos) AS cliente_nombre, " +
            "p.titulo AS titulo_pelicula, " +
            "e.cantidad, p.precio AS precio_unidad, " +
            "(e.cantidad * p.precio) AS subtotal, " +
            "s.fecha, s.hora, s.sala " +
            "FROM entradas e " +
            "JOIN clientes c  ON e.cliente_id  = c.usuario_id " +
            "JOIN usuarios u  ON c.usuario_id  = u.id " +
            "JOIN sesiones s  ON e.sesion_id   = s.id " +
            "JOIN peliculas p ON s.pelicula_id  = p.id " +
            "ORDER BY s.fecha DESC, s.hora DESC, e.id DESC";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new EntradaDTO(
                    rs.getInt("id"),
                    rs.getString("cliente_nombre"),
                    rs.getString("titulo_pelicula"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_unidad"),
                    rs.getDouble("subtotal"),
                    rs.getString("fecha") + " " + rs.getString("hora"),
                    rs.getString("sala")
                ));
            }
        } catch (SQLException e) {
            System.out.println("ERROR al listar entradas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM entradas WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR al eliminar entrada: " + e.getMessage());
            throw e;
        }
    }
}
