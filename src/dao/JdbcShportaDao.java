package dao;

import Model.Shporta;
import Model.ShportaProdukt;
import db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcShportaDao implements ShportaDao {

    @Override
    public Shporta create(Shporta shporta) throws SQLException {

        String sql = """
                INSERT INTO shporta (klient_id)
                VALUES (?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, shporta.getKlientId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    shporta.setIdShporta(rs.getLong(1));
                }
            }
        }

        // ✅ shto produktet e shportës
        addProductsToShporta(shporta);

        return shporta;
    }


    private void addProductsToShporta(Shporta shporta) throws SQLException {
        String sql = """
                INSERT INTO shporta_produkt (shporta_id, produkt_id, quantity)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (ShportaProdukt sp : shporta.getProduktet()) {
                ps.setLong(1, shporta.getIdShporta());
                ps.setLong(2, sp.getProduktId());
                ps.setInt(3, sp.getQuantity());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }


    @Override
    public Optional<Shporta> findByKlient(Long klientId) throws SQLException {
        String sql = """
                SELECT * FROM shporta
                WHERE klient_id = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, klientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Shporta sh = mapToShporta(rs);
                    sh.setProduktet(getProductsForShporta(sh.getIdShporta()));
                    return Optional.of(sh);
                }
            }
        }

        return Optional.empty();
    }


    private List<ShportaProdukt> getProductsForShporta(Long shportaId) throws SQLException {
        List<ShportaProdukt> list = new ArrayList<>();

        String sql = """
                SELECT * FROM shporta_produkt
                WHERE shporta_id = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, shportaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ShportaProdukt(
                            rs.getLong("shporta_id"),
                            rs.getLong("produkt_id"),
                            rs.getInt("quantity")
                    ));
                }
            }
        }

        return list;
    }


    private Shporta mapToShporta(ResultSet rs) throws SQLException {
        Shporta shporta = new Shporta();

        shporta.setIdShporta(rs.getLong("id_shporta"));
        shporta.setKlientId(rs.getLong("klient_id"));

        return shporta;
    }


    @Override
    public List<Shporta> findAll() throws SQLException {
        List<Shporta> list = new ArrayList<>();
        String sql = "SELECT * FROM shporta";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Shporta s = mapToShporta(rs);
                s.setProduktet(getProductsForShporta(s.getIdShporta()));
                list.add(s);
            }
        }

        return list;
    }


    @Override
    public void deleteByKlient(Long klientId) throws SQLException {
        String sql = """
                DELETE FROM shporta
                WHERE klient_id = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, klientId);
            ps.executeUpdate();
        }
    }


    @Override
    public void clearShporta(Long klientId) throws SQLException {
        String sql = """
                DELETE FROM shporta_produkt
                WHERE shporta_id IN (
                    SELECT id_shporta FROM shporta
                    WHERE klient_id = ?
                )
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, klientId);
            ps.executeUpdate();
        }
    }
}
