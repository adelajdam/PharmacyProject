package dao;

import Model.Njoftim;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface NjoftimDao {
    Njoftim create(Njoftim njoftim) throws SQLException;
    Optional<Njoftim> findById(Long idNjoftimi) throws SQLException;
    List<Njoftim> findByUser(Long userId) throws SQLException;
    List<Njoftim> findAll() throws SQLException;
    Njoftim update(Njoftim njoftim) throws SQLException;
    void delete(Long idNjoftimi) throws SQLException;
    List<Njoftim> findByType(String type) throws SQLException;
}


