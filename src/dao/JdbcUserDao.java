package dao;

import Model.*;
import db.DatabaseManager;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDao implements UserDao {
    private final DataSource dataSource;
    public JdbcUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User create(User user) throws SQLException {
        String sql = """
                INSERT INTO users (emri, mbiemri, email, password, data_regjistrimit, role, adresa, nr_tel)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getEmri());
            ps.setString(2, user.getMbiemri());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setDate(5, user.getDataRegjistrimit() != null ? Date.valueOf(user.getDataRegjistrimit()) : null);
            ps.setString(6, user.getRole());

            if (user instanceof Klient klient) {
                ps.setString(7, klient.getAdresa());
                ps.setString(8, klient.getNrTel());
            } else {
                ps.setString(7, null);
                ps.setString(8, null);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapToUser(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapToUser(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapToUser(rs));
        }
        return list;
    }

    @Override
    public List<User> findByRole(String role) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapToUser(rs));
            }
        }
        return list;
    }

    @Override
    public User update(User user) throws SQLException {
        String sql = """
                UPDATE users SET emri = ?, mbiemri = ?, email = ?, password = ?, 
                data_regjistrimit = ?, role = ?, adresa = ?, nr_tel = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getEmri());
            ps.setString(2, user.getMbiemri());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setDate(5, user.getDataRegjistrimit() != null ? Date.valueOf(user.getDataRegjistrimit()) : null);
            ps.setString(6, user.getRole());

            if (user instanceof Klient klient) {
                ps.setString(7, klient.getAdresa());
                ps.setString(8, klient.getNrTel());
            } else {
                ps.setString(7, null);
                ps.setString(8, null);
            }

            ps.setLong(9, user.getId());
            ps.executeUpdate();
        }
        return user;
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private User mapToUser(ResultSet rs) throws SQLException {

        Long id = rs.getLong("id");
        String emri = rs.getString("emri");
        String mbiemri = rs.getString("mbiemri");
        String email = rs.getString("email");
        String password = rs.getString("password");

        LocalDate data = null;
        Date sqlDate = rs.getDate("data_regjistrimit");
        if (sqlDate != null) data = sqlDate.toLocalDate();

        String role = rs.getString("role");
        String adresa = rs.getString("adresa");
        String nrTel = rs.getString("nr_tel");

        return switch (role) {

            case "ADMINISTRATOR" ->
                    new Administrator(id, emri, mbiemri, password, data, "ADMINISTRATOR", email);

            case "FARMACIST" ->
                    new Farmacist(id, emri, mbiemri, password, data, "FARMACIST", email);


            case "KLIENT_LOGUAR" ->
                    new KlientLoguar(id, emri, mbiemri, password, data, adresa, nrTel, "KLIENT_LOGUAR", email);

            case "KLIENT_GUEST" -> {
                KlientGuest g = new KlientGuest(id);
                g.setEmri(emri);
                g.setMbiemri(mbiemri);
                g.setAdresa(adresa);
                g.setNrTel(nrTel);
                yield g;
            }

            default -> throw new SQLException("Roli i panjohur: " + role);
        };
    }
}

