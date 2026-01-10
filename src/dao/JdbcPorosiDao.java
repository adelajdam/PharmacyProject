package dao;

import Model.*;
import db.DatabaseManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPorosiDao implements PorosiDao {
    private final DataSource dataSource;
    private final PorosiProduktDao porosiProduktDao;

    public JdbcPorosiDao(DataSource dataSource, PorosiProduktDao porosiProduktDao) {
        this.dataSource = dataSource;
        this.porosiProduktDao = porosiProduktDao;
    }

    @Override
    public Porosi create(Porosi porosi) throws SQLException {

        String sql = """
            INSERT INTO porosite
            (klient_id, klient_type, data_porosise, totali, status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Klient klient = porosi.getKlienti();

            ps.setLong(1, klient.getId());
            ps.setString(2, klient instanceof KlientLoguar ? "KLIENT_LOGUAR" : "KLIENT_GUEST");
            ps.setDate(3, Date.valueOf(porosi.getDataPorosise()));
            ps.setDouble(4, porosi.getTotali());
            ps.setString(5, porosi.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    porosi.setIdPorosi(rs.getLong(1));
                }
            }
        }

//        for (PorosiProdukt pp : porosi.getProduktet()) {
//            pp.setPorosiId(porosi.getIdPorosi());
//            porosiProduktDao.addProduktToPorosi(pp);
//        }

        return porosi;
    }

    @Override
    public Optional<Porosi> findById(Long idPorosi) throws SQLException {
        String sql = "SELECT * FROM porosite WHERE id_porosi = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idPorosi);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Porosi p = mapToOrder(rs);

                    List<PorosiProdukt> produktet = porosiProduktDao.findByPorosiId(idPorosi);
                    p.setProduktet(produktet);

                    return Optional.of(p);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Porosi> findAll() throws SQLException {
        List<Porosi> list = new ArrayList<>();
        String sql = "SELECT * FROM porosite";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Porosi p = mapToOrder(rs);

                List<PorosiProdukt> produktet = porosiProduktDao.findByPorosiId(p.getIdPorosi());
                p.setProduktet(produktet);

                list.add(p);
            }
        }

        return list;
    }

    @Override
    public void delete(Long idPorosi) throws SQLException {

        porosiProduktDao.deleteByPorosiId(idPorosi);

        String sql = "DELETE FROM porosite WHERE id_porosi = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idPorosi);
            ps.executeUpdate();
        }
    }



    @Override
    public List<Porosi> findByKlientId(Long klientId) throws SQLException {
        List<Porosi> list = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM porosite WHERE klient_id = ? ORDER BY id_porosi DESC")) {

            stmt.setLong(1, klientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToOrder(rs));
                }
            }
        }

        return list;
    }



    private Porosi mapToOrder(ResultSet rs) throws SQLException {
        Porosi porosi = new Porosi();
        porosi.setIdPorosi(rs.getLong("id_porosi"));

        String klientType = rs.getString("klient_type");
        long klientId = rs.getLong("klient_id");

        Klient klient;

        if ("KLIENT_LOGUAR".equals(klientType)) {
            klient = new KlientLoguar();
        } else {
            klient = new KlientGuest();
        }

        klient.setId(klientId);
        porosi.setKlienti(klient);

        Date sqlDate = rs.getDate("data_porosise");
        if (sqlDate != null) {
            porosi.setDataPorosise(sqlDate.toLocalDate());
        }

        porosi.setTotali(rs.getDouble("totali"));
        porosi.setStatus(rs.getString("status"));

        return porosi;
    }
}
