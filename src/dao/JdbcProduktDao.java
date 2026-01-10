package dao;

import Model.Produkt;
import db.DatabaseManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProduktDao implements ProduktDao {
    private final DataSource dataSource;
    public JdbcProduktDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Produkt create(Produkt produkt) throws SQLException {
        String sql = """
                INSERT INTO produktet (emri_prod, pershkrimi, cmimi, stok, kategori)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, produkt.getEmriProd());
            ps.setString(2, produkt.getPershkrimi());
            ps.setDouble(3, produkt.getCmimi());
            ps.setInt(4, produkt.getStok());
            ps.setString(5, produkt.getKategori());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    produkt.setIdProd(rs.getLong(1));
                }
            }
        }
        return produkt;
    }

    @Override
    public Optional<Produkt> findById(Long idProd) throws SQLException {
        String sql = "SELECT * FROM produktet WHERE id_prod = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idProd);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToProdukt(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Produkt> findByName(String emriProd) throws SQLException {
        String sql = "SELECT * FROM produktet WHERE emri_prod = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emriProd);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToProdukt(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Produkt> findByCategory(String kategori) throws SQLException {
        List<Produkt> list = new ArrayList<>();
        String sql = "SELECT * FROM produktet WHERE kategori = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kategori);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToProdukt(rs));
                }
            }
        }

        return list;
    }

    @Override
    public List<Produkt> sortByPrice(boolean asc) throws SQLException {
        List<Produkt> list = new ArrayList<>();
        String sql = "SELECT * FROM produktet ORDER BY cmimi " + (asc ? "ASC" : "DESC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapToProdukt(rs));
            }
        }

        return list;
    }

    @Override
    public List<Produkt> findAll() throws SQLException {
        List<Produkt> list = new ArrayList<>();
        String sql = "SELECT * FROM produktet";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapToProdukt(rs));
            }
        }
        return list;
    }

    @Override
    public Produkt update(Produkt produkt) throws SQLException {
        String sql = """
                UPDATE produktet
                SET emri_prod = ?, pershkrimi = ?, cmimi = ?, stok = ?, kategori = ?
                WHERE id_prod = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produkt.getEmriProd());
            ps.setString(2, produkt.getPershkrimi());
            ps.setDouble(3, produkt.getCmimi());
            ps.setInt(4, produkt.getStok());
            ps.setString(5, produkt.getKategori());
            ps.setLong(6, produkt.getIdProd());

            ps.executeUpdate();
        }

        return produkt;
    }

    @Override
    public void delete(Long idProd) throws SQLException {
        String sql = "DELETE FROM produktet WHERE id_prod = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idProd);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Produkt> findStokBelow(int threshold) throws SQLException {
        List<Produkt> list = new ArrayList<>();

        String sql = "SELECT * FROM produkt WHERE stok < ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, threshold);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToProdukt(rs));
                }
            }
        }

        return list;
    }


    private Produkt mapToProdukt(ResultSet rs) throws SQLException {
        return new Produkt(
                rs.getLong("id_prod"),
                rs.getString("emri_prod"),
                rs.getString("pershkrimi"),
                rs.getDouble("cmimi"),
                rs.getInt("stok"),
                rs.getString("kategori")
        );
    }
}





