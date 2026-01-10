package dao;

import Model.SystemConfig;

import javax.sql.DataSource;
import java.sql.*;

public class JdbcSystemConfigDao implements SystemConfigDao {

    private final DataSource dataSource;

    public JdbcSystemConfigDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(SystemConfig config) {
        String sql = "INSERT INTO system_config (config_key, config_value) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, config.getKey());
            ps.setString(2, config.getValue());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save system config", e);
        }
    }

    @Override
    public void update(String key, String value) {
        String sql = "UPDATE system_config SET config_value = ? WHERE config_key = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, key);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update system config", e);
        }
    }

    @Override
    public SystemConfig findByKey(String key) {
        String sql = "SELECT id, config_key, config_value FROM system_config WHERE config_key = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SystemConfig config = new SystemConfig();
                    config.setId(rs.getLong("id"));
                    config.setKey(rs.getString("config_key"));
                    config.setValue(rs.getString("config_value"));
                    return config;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find system config", e);
        }

        return null;
    }
}
