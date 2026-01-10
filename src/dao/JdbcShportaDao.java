package dao;

import Model.Produkt;
import Model.Shporta;
import Model.ShportaProdukt;
import db.DatabaseManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcShportaDao implements ShportaDao {
    private final DataSource dataSource;
    public JdbcShportaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
                ps.setLong(2, sp.getProdukt().getIdProd());
                ps.setInt(3, sp.getQuantity());
                ps.addBatch();
            }

            ps.executeBatch(); // mos harro ta ekzekutosh batch-in
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
        SELECT sp.shporta_id, sp.quantity,
               p.id_prod, p.emri_prod, p.cmimi, p.stok, p.kategori
        FROM shporta_produkt sp
        JOIN produktet p ON sp.produkt_id = p.id_prod
        WHERE sp.shporta_id = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, shportaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Krijo objektin Produkt
                    Produkt produkt = new Produkt();
                    produkt.setIdProd(rs.getLong("id_prod"));
                    produkt.setEmriProd(rs.getString("emri_prod"));
                    produkt.setCmimi(rs.getDouble("cmimi"));
                    produkt.setStok(rs.getInt("stok"));
                    produkt.setKategori(rs.getString("kategori"));

                    // Krijo ShportaProdukt me Produkt brenda
                    ShportaProdukt sp = new ShportaProdukt();
                    sp.setShportaId(rs.getLong("shporta_id"));
                    sp.setProdukt(produkt);
                    sp.setQuantity(rs.getInt("quantity"));

                    list.add(sp);
                }
            }
        }

        return list;
    }


    @Override
    public List<ShportaProdukt> findProduktetNeShporte(long shportaId) throws SQLException {

        String sql = """
        SELECT
            sp.shporta_id,
            sp.produkt_id,
            sp.quantity,
            p.emri_prod,
            p.cmimi,
            p.stok,
            p.kategori
        FROM shporta_produkt sp
        JOIN produktet p ON sp.produkt_id = p.id_prod
        WHERE sp.shporta_id = ?
        """;

        List<ShportaProdukt> lista = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, shportaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Produkt produkt = new Produkt();
                produkt.setIdProd(rs.getLong("produkt_id"));
                produkt.setEmriProd(rs.getString("emri_prod"));
                produkt.setCmimi(rs.getDouble("cmimi"));
                produkt.setStok(rs.getInt("stok"));
                produkt.setKategori(rs.getString("kategori"));

                ShportaProdukt sp = new ShportaProdukt();
                sp.setShportaId(rs.getLong("shporta_id"));
                sp.setProdukt(produkt);
                sp.setQuantity(rs.getInt("quantity"));

                lista.add(sp);
            }
        }

        return lista;
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
