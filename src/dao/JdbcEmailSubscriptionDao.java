package dao;

import Model.EmailSubscription;
import Model.Produkt;
import db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEmailSubscriptionDao implements EmailSubscriptionDao {

    @Override
    public EmailSubscription create(EmailSubscription sub) throws SQLException {
        String sql = """
                INSERT INTO email_subscriptions (email, produkt_id, data_regjistrimit)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sub.getEmail());
            ps.setLong(2, sub.getProdukt().getIdProd());
            ps.setDate(3, Date.valueOf(sub.getDataKerkeses()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) sub.setIdEmail(rs.getLong(1));
            }
        }
        return sub;
    }

    @Override
    public Optional<EmailSubscription> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM email_subscriptions WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapToSub(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<EmailSubscription> findByProduktId(Long produktId) throws SQLException {
        List<EmailSubscription> list = new ArrayList<>();

        String sql = "SELECT * FROM email_subscription WHERE produkt_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, produktId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmailSubscription es = new EmailSubscription();
                    es.setIdEmail(rs.getLong("id_email"));
                    es.setEmail(rs.getString("email"));

                    Produkt p = new Produkt();
                    p.setIdProd(rs.getLong("produkt_id"));
                    es.setProdukt(p);

                    es.setDataKerkeses(rs.getDate("data_kerkeses").toLocalDate());
                    list.add(es);
                }
            }
        }

        return list;
    }

    @Override
    public List<EmailSubscription> findAll() throws SQLException {
        List<EmailSubscription> list = new ArrayList<>();
        String sql = "SELECT * FROM email_subscriptions";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapToSub(rs));
        }
        return list;
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM email_subscriptions WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private EmailSubscription mapToSub(ResultSet rs) throws SQLException {
        EmailSubscription sub = new EmailSubscription();

        sub.setIdEmail(rs.getLong("id"));
        sub.setEmail(rs.getString("email"));

        Produkt p = new Produkt();
        p.setIdProd(rs.getLong("produkt_id"));
        sub.setProdukt(p);

        sub.setDataKerkeses(rs.getDate("data_regjistrimit").toLocalDate());

        return sub;
    }
}

