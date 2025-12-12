package dao;

import Model.Produkt;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProduktDao {
    Produkt create(Produkt produkt) throws SQLException;
    Optional<Produkt> findById(Long idProd) throws SQLException;
    Optional<Produkt> findByName(String emriProd) throws SQLException;
    List<Produkt> findByCategory(String kategori) throws SQLException;
    List<Produkt> sortByPrice(boolean asc) throws SQLException;
    List<Produkt> findAll() throws SQLException;
    Produkt update(Produkt produkt) throws SQLException;
    void delete(Long idProd) throws SQLException;
    List<Produkt> findStokBelow(int threshold) throws SQLException;
}
