package dao;

import db.ConexionDB;
import model.Sesion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SesionDAOImpl implements SesionDAO {

    @Override
    public List<Sesion> listarPorPelicula(int peliculaId) throws SQLException {
        List<Sesion> lista = new ArrayList<>();
        // Solo sesiones de hoy en adelante, ordenadas por fecha y hora
        String sql = "SELECT id, pelicula_id, fecha, TIME_FORMAT(hora,'%H:%i') AS hora, sala, aforo " +
                     "FROM sesiones " +
                     "WHERE pelicula_id = ? AND fecha >= CURDATE() " +
                     "ORDER BY fecha, hora";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, peliculaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Sesion(
                        rs.getInt("id"),
                        rs.getInt("pelicula_id"),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("sala"),
                        rs.getInt("aforo")
                    ));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Sesion> listarTodas() throws SQLException {
        List<Sesion> lista = new ArrayList<>();
        String sql = "SELECT id, pelicula_id, fecha, TIME_FORMAT(hora,'%H:%i') AS hora, sala, aforo " +
                     "FROM sesiones ORDER BY fecha, hora";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Sesion(
                    rs.getInt("id"),
                    rs.getInt("pelicula_id"),
                    rs.getString("fecha"),
                    rs.getString("hora"),
                    rs.getString("sala"),
                    rs.getInt("aforo")
                ));
            }
        }
        return lista;
    }

    @Override
    public void insertar(Sesion sesion) throws SQLException {
        String sql = "INSERT INTO sesiones (pelicula_id, fecha, hora, sala, aforo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, sesion.getPeliculaId());
            ps.setString(2, sesion.getFecha());
            ps.setString(3, sesion.getHora());
            ps.setString(4, sesion.getSala());
            ps.setInt   (5, sesion.getAforo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) sesion.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM sesiones WHERE id = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
