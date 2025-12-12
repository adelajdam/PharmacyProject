package dao;

import Model.Fature;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FatureDao {
    Fature create(Fature fatura) throws SQLException;
    Optional<Fature> findById(Long idFature) throws SQLException;
    List<Fature> findAll() throws SQLException;
    Fature update(Fature fature) throws SQLException;
    void delete(Long idFature) throws SQLException;
}

