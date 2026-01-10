package dao;

import Model.Mesazh;
import db.DatabaseManager;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcChatBoxDao implements ChatBoxDao {
    private final DataSource dataSource;
    public JdbcChatBoxDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Mesazh create(Mesazh mesazh) throws SQLException {
        String sql = "INSERT INTO chatbox (sender_id, receiver_id, permbajtja, data_dergimit) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, mesazh.getSenderId());
            stmt.setLong(2, mesazh.getReceiverId());
            stmt.setString(3, mesazh.getPermbajtja());
            stmt.setTimestamp(4, Timestamp.valueOf(mesazh.getDataDergimit()));

            int affected = stmt.executeUpdate();

            if (affected == 0) {
                throw new SQLException("Dërgimi i mesazhit dështoi, asnjë rresht i futur.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mesazh.setId(generatedKeys.getLong(1));
                }
            }
        }

        return mesazh;
    }

    @Override
    public List<Mesazh> findByUsers(Long user1Id, Long user2Id) throws SQLException {
        String sql = "SELECT * FROM chatbox WHERE (sender_id=? AND receiver_id=?) OR (sender_id=? AND receiver_id=?) ORDER BY data_dergimit ASC";
        List<Mesazh> mesazhe = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user1Id);
            stmt.setLong(2, user2Id);
            stmt.setLong(3, user2Id);
            stmt.setLong(4, user1Id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Mesazh m = new Mesazh();
                m.setId(rs.getLong("id"));
                m.setSenderId(rs.getLong("sender_id"));
                m.setReceiverId(rs.getLong("receiver_id"));
                m.setPermbajtja(rs.getString("permbajtja"));
                m.setDataDergimit(rs.getTimestamp("data_dergimit").toLocalDateTime());
                mesazhe.add(m);
            }
        }

        return mesazhe;
    }

    @Override
    public List<Mesazh> findAllForUser(Long userId) throws SQLException {
        String sql = "SELECT * FROM chatbox WHERE sender_id=? OR receiver_id=? ORDER BY data_dergimit ASC";
        List<Mesazh> mesazhe = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Mesazh m = new Mesazh();
                m.setId(rs.getLong("id"));
                m.setSenderId(rs.getLong("sender_id"));
                m.setReceiverId(rs.getLong("receiver_id"));
                m.setPermbajtja(rs.getString("permbajtja"));
                m.setDataDergimit(rs.getTimestamp("data_dergimit").toLocalDateTime());
                mesazhe.add(m);
            }
        }

        return mesazhe;
    }
}

