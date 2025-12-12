package dao;

import Model.PorosiProdukt;
import db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPorosiProduktDao implements PorosiProduktDao {

    @Override
    public void addProduktToPorosi(PorosiProdukt pp) throws SQLException {
        String sql = "INSERT INTO porosi_produkt (porosi_id, produkt_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, pp.getPorosiId());
            ps.setLong(2, pp.getProduktId());
            ps.setInt(3, pp.getQuantity());

            ps.executeUpdate();
        }
    }

    @Override
    public List<PorosiProdukt> findByPorosiId(Long porosiId) throws SQLException {
        List<PorosiProdukt> list = new ArrayList<>();

        String sql = "SELECT * FROM porosi_produkt WHERE porosi_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, porosiId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PorosiProdukt(
                            rs.getLong("porosi_id"),
                            rs.getLong("produkt_id"),
                            rs.getInt("quantity")
                    ));
                }
            }
        }

        return list;
    }

    @Override
    public void deleteByPorosiId(Long porosiId) throws SQLException {
        String sql = "DELETE FROM porosi_produkt WHERE porosi_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, porosiId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteProduktFromPorosi(Long porosiId, Long produktId) throws SQLException {
        String sql = "DELETE FROM porosi_produkt WHERE porosi_id = ? AND produkt_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, porosiId);
            ps.setLong(2, produktId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<PorosiProdukt> findByProduktId(Long produktId) throws SQLException {
        List<PorosiProdukt> list = new ArrayList<>();

        String sql = "SELECT * FROM porosi_produkt WHERE produkt_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, produktId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PorosiProdukt(
                            rs.getLong("porosi_id"),
                            rs.getLong("produkt_id"),
                            rs.getInt("quantity")
                    ));
                }
            }
        }

        return list;
    }
}

