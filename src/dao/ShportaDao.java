package dao;

import Model.Shporta;
import Model.ShportaProdukt;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShportaDao {
    Shporta create(Shporta shporta) throws SQLException;
    Optional<Shporta> findByKlient(Long klientId) throws SQLException;
    List<Shporta> findAll() throws SQLException;
    List<ShportaProdukt> findProduktetNeShporte(long shportaId) throws SQLException;
    void deleteByKlient(Long klientId) throws SQLException;
    void clearShporta(Long klientId) throws SQLException;
}


