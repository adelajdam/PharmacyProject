package dao;

import Model.PorosiProdukt;
import java.sql.SQLException;
import java.util.List;

public interface PorosiProduktDao {
    void addProduktToPorosi(PorosiProdukt pp) throws SQLException;
    List<PorosiProdukt> findByPorosiId(Long porosiId) throws SQLException;
    void deleteByPorosiId(Long porosiId) throws SQLException;
    void deleteProduktFromPorosi(Long porosiId, Long produktId) throws SQLException;
    List<PorosiProdukt> findByProduktId(Long produktId) throws SQLException;
}

