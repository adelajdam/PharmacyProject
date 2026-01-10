package dao;

import Model.Produkt;
import Model.ShportaProdukt;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcShportaProduktDao implements ShportaProduktDao {

    private final DataSource dataSource;

    public JdbcShportaProduktDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addProdukt(Long shportaId, Long produktId, int quantity) throws SQLException {

        String checkSql = "SELECT quantity FROM shporta_produkt WHERE shporta_id = ? AND produkt_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setLong(1, shportaId);
            checkPs.setLong(2, produktId);

            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                int newQty = rs.getInt("quantity") + quantity;
                updateQuantity(shportaId, produktId, newQty);
                return;
            }
        }

        String insertSql = "INSERT INTO shporta_produkt (shporta_id, produkt_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setLong(1, shportaId);
            ps.setLong(2, produktId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
    }

    @Override
    public void removeProdukt(Long shportaId, Long produktId) throws SQLException {
        String sql = "DELETE FROM shporta_produkt WHERE shporta_id = ? AND produkt_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, shportaId);
            ps.setLong(2, produktId);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateQuantity(Long shportaId, Long produktId, int quantity) throws SQLException {
        if (quantity <= 0) {
            removeProdukt(shportaId, produktId);
            return;
        }

        String sql = "UPDATE shporta_produkt SET quantity = ? WHERE shporta_id = ? AND produkt_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setLong(2, shportaId);
            ps.setLong(3, produktId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ShportaProdukt> findByShportaId(Long shportaId) throws SQLException {
        String sql = """
        SELECT sp.shporta_id, sp.quantity,
               p.id_prod, p.emri_prod, p.cmimi, p.stok, p.kategori
        FROM shporta_produkt sp
        JOIN produktet p ON sp.produkt_id = p.id_prod
        WHERE sp.shporta_id = ?
    """;

        List<ShportaProdukt> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, shportaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produkt produkt = new Produkt();
                    produkt.setIdProd(rs.getLong("id_prod"));
                    produkt.setEmriProd(rs.getString("emri_prod"));
                    produkt.setCmimi(rs.getDouble("cmimi"));
                    produkt.setStok(rs.getInt("stok"));
                    produkt.setKategori(rs.getString("kategori"));

                    ShportaProdukt sp = new ShportaProdukt();
                    sp.setShportaId(rs.getLong("shporta_id"));
                    sp.setProdukt(produkt);
                    sp.setQuantity(rs.getInt("quantity"));

                    result.add(sp);
                }
            }
        }

        return result;
    }

}
