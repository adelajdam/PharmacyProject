package dao;

import Model.AuditLog;
import java.sql.SQLException;
import java.util.List;

public interface AuditLogDao {
    AuditLog create(AuditLog log) throws SQLException;
    List<AuditLog> findAll() throws SQLException;
    List<AuditLog> findByUser(Long userId) throws SQLException;
}

