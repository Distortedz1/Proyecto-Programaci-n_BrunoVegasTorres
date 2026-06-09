package dao;

import db.ConexionDB;
import model.Pelicula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDAOImpl implements PeliculaDAO {

    @Override
    public void insertar(Pelicula pelicula) throws SQLException {
        String sql = "INSERT INTO peliculas (titulo, genero, duracion, director, anio, precio) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pelicula.getTitulo());
            ps.setString(2, pelicula.getGenero());
            ps.setInt(3, pelicula.getDuracion());
            ps.setString(4, pelicula.getDirector());
            ps.setInt(5, pelicula.getAnio());
            ps.setDouble(6, pelicula.getPrecio());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pelicula.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR al insertar pelicula: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Pelicula> listarTodos() throws SQLException {
        List<Pelicula> lista = new ArrayList<>();
        String sql = "SELECT * FROM peliculas ORDER BY titulo";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Pelicula(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("genero"),
                    rs.getInt("duracion"),
                    rs.getString("director"),
                    rs.getInt("anio"),
                    rs.getDouble("precio")
                ));
            }
        } catch (SQLException e) {
            System.out.println("ERROR al listar peliculas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Pelicula buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM peliculas WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pelicula(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("genero"),
                        rs.getInt("duracion"),
                        rs.getString("director"),
                        rs.getInt("anio"),
                        rs.getDouble("precio")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR al buscar pelicula: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void actualizar(Pelicula pelicula) throws SQLException {
        String sql = "UPDATE peliculas SET titulo=?, genero=?, duracion=?, director=?, anio=?, precio=? WHERE id=?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pelicula.getTitulo());
            ps.setString(2, pelicula.getGenero());
            ps.setInt(3, pelicula.getDuracion());
            ps.setString(4, pelicula.getDirector());
            ps.setInt(5, pelicula.getAnio());
            ps.setDouble(6, pelicula.getPrecio());
            ps.setInt(7, pelicula.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ERROR al actualizar pelicula: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM peliculas WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("ERROR al eliminar pelicula: " + e.getMessage());
            throw e;
        }
    }
}
