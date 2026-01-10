package dao;

import Model.Njoftim;
import Model.User;
import db.DatabaseManager;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcNjoftimDao implements NjoftimDao {
    private final DataSource dataSource;
    public JdbcNjoftimDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Njoftim create(Njoftim njoftim) throws SQLException {

        if (njoftim.getDataKoha() == null) {
            njoftim.setDataKoha(LocalDateTime.now());
        }

        // sigurohu që tipi nuk është null
        if (njoftim.getTipi() == null) {
            throw new IllegalArgumentException("Njoftim duhet të ketë tipi të vendosur");
        }

        String sql = "INSERT INTO njoftimet (user_id, tipi, mesazhi, data_koha, eshte_lexuar) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, njoftim.getPerdoruesi().getId());
            ps.setString(2, njoftim.getTipi());
            ps.setString(3, njoftim.getMesazhi());
            ps.setTimestamp(4, Timestamp.valueOf(njoftim.getDataKoha()));
            ps.setBoolean(5, false);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    njoftim.setIdNjoftimi(rs.getLong(1));
                }
            }

            return njoftim;
        }
    }


    @Override
    public Optional<Njoftim> findById(Long idNjoftimi) throws SQLException {
        String sql = "SELECT * FROM njoftimet WHERE id_njoftimi = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idNjoftimi);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToNjoftim(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Njoftim> findByUser(Long userId) throws SQLException {
        List<Njoftim> list = new ArrayList<>();
        String sql = "SELECT * FROM njoftimet WHERE user_id = ? ORDER BY data_koha DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToNjoftim(rs));
                }
            }
        }

        return list;
    }

    @Override
    public List<Njoftim> findAll() throws SQLException {
        List<Njoftim> list = new ArrayList<>();
        String sql = "SELECT * FROM njoftimet ORDER BY data_koha DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapToNjoftim(rs));
            }
        }

        return list;
    }

    @Override
    public Njoftim update(Njoftim njoftim) throws SQLException {
        String sql = """
                UPDATE njoftimet 
                SET mesazhi = ?, data_koha = ?, eshte_lexuar = ?
                WHERE id_njoftimi = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, njoftim.getMesazhi());
            ps.setTimestamp(2, Timestamp.valueOf(njoftim.getDataKoha()));
            ps.setBoolean(3, njoftim.isEshteLexuar());
            ps.setLong(4, njoftim.getIdNjoftimi());

            ps.executeUpdate();
        }

        return njoftim;
    }

    @Override
    public void delete(Long idNjoftimi) throws SQLException {
        String sql = "DELETE FROM njoftimet WHERE id_njoftimi = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idNjoftimi);
            ps.executeUpdate();
        }
    }


    @Override
    public List<Njoftim> findByType(String type) throws SQLException {
        List<Njoftim> list = new ArrayList<>();

        String sql = "SELECT * FROM njoftime WHERE tipi = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToNjoftim(rs));
                }
            }
        }

        return list;
    }



    private Njoftim mapToNjoftim(ResultSet rs) throws SQLException {
        Njoftim njoftim = new Njoftim();

        njoftim.setIdNjoftimi(rs.getLong("id_njoftimi"));

        User user = new User() { };
        user.setId(rs.getLong("user_id"));
        njoftim.setPerdoruesi(user);

        njoftim.setMesazhi(rs.getString("mesazhi"));
        Timestamp ts = rs.getTimestamp("data_koha");
        if (ts != null) {
            njoftim.setDataKoha(ts.toLocalDateTime());
        }
        njoftim.setEshteLexuar(rs.getBoolean("eshte_lexuar"));

        return njoftim;
    }
}
