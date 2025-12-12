package dao;

import Model.AuditLog;
import Model.User;
import db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcAuditLogDao implements AuditLogDao {

    @Override
    public AuditLog create(AuditLog log) throws SQLException {
        String sql = """
                INSERT INTO audit_log (user_id, veprimi, data_koha)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, log.getUser().getId());
            ps.setString(2, log.getVeprimi());
            ps.setTimestamp(3, Timestamp.valueOf(log.getDataKoha()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) log.setIdLog(rs.getLong(1));
            }
        }
        return log;
    }

    @Override
    public List<AuditLog> findAll() throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_log ORDER BY data_koha DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapToLog(rs));
        }
        return list;
    }

    @Override
    public List<AuditLog> findByUser(Long userId) throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_log WHERE user_id = ? ORDER BY data_koha DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapToLog(rs));
            }
        }
        return list;
    }

    private AuditLog mapToLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();

        log.setIdLog(rs.getLong("id"));

        User u = new User() {};
        u.setId(rs.getLong("user_id"));
        log.setUser(u);

        log.setVeprimi(rs.getString("veprimi"));
        log.setDataKoha(rs.getTimestamp("data_koha").toLocalDateTime());

        return log;
    }
}

