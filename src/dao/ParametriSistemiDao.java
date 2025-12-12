package dao;

import Model.ParametriSistemi;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ParametriSistemiDao {
    ParametriSistemi create(ParametriSistemi p) throws SQLException;
    Optional<ParametriSistemi> findByKey(String key) throws SQLException;
    List<ParametriSistemi> findAll() throws SQLException;
    ParametriSistemi update(ParametriSistemi p) throws SQLException;
    void delete(String key) throws SQLException;
}

