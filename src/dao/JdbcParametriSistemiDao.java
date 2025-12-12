package dao;

import Model.ParametriSistemi;
import db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcParametriSistemiDao implements ParametriSistemiDao {

    @Override
    public ParametriSistemi create(ParametriSistemi p) throws SQLException {
        String sql = """
                INSERT INTO sistemi_parametra (param_key, param_value)
                VALUES (?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getKey());
            ps.setString(2, p.getValue());
            ps.executeUpdate();
        }
        return p;
    }

    @Override
    public Optional<ParametriSistemi> findByKey(String key) throws SQLException {
        String sql = "SELECT * FROM sistemi_parametra WHERE param_key = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapToParam(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ParametriSistemi> findAll() throws SQLException {
        List<ParametriSistemi> list = new ArrayList<>();
        String sql = "SELECT * FROM sistemi_parametra";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapToParam(rs));
        }
        return list;
    }

    @Override
    public ParametriSistemi update(ParametriSistemi p) throws SQLException {
        String sql = """
                UPDATE sistemi_parametra
                SET param_value = ?
                WHERE param_key = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getValue());
            ps.setString(2, p.getKey());
            ps.executeUpdate();
        }
        return p;
    }

    @Override
    public void delete(String key) throws SQLException {
        String sql = "DELETE FROM sistemi_parametra WHERE param_key = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ps.executeUpdate();
        }
    }

    private ParametriSistemi mapToParam(ResultSet rs) throws SQLException {
        return new ParametriSistemi(
                rs.getString("param_key"),
                rs.getString("param_value")
        );
    }
}

