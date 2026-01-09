package dao;

import Model.Porosi;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PorosiDao {
    Porosi create(Porosi porosi) throws SQLException;
    Optional<Porosi> findById(Long idPorosi) throws SQLException;
    List<Porosi> findAll() throws SQLException;
    void delete(Long idPorosi) throws SQLException;
    List<Porosi> findByKlientId(Long klientId) throws SQLException;
}
