package dao;

import Model.Recete;

import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

public interface ReceteDao {
        Recete create(Recete recete) throws SQLException;
        Optional<Recete> findById(Long id) throws SQLException;
        List<Recete> findAll() throws SQLException;
        Recete update(Recete recete) throws SQLException;
        void delete(Long id) throws SQLException;

}
