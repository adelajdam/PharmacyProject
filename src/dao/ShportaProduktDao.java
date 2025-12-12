package dao;

import Model.ShportaProdukt;

import java.sql.SQLException;
import java.util.List;

public interface ShportaProduktDao {
    void addProdukt(Long shportaId, Long produktId, int quantity) throws SQLException;
    void removeProdukt(Long shportaId, Long produktId) throws SQLException;
    void updateQuantity(Long shportaId, Long produktId, int quantity) throws SQLException;
    List<ShportaProdukt> findByShportaId(Long shportaId) throws SQLException;
}

