package dao;

import Model.Foto;
import java.sql.SQLException;
import java.util.List;

public interface FotoDao {

    Foto create(Foto foto) throws SQLException;
    List<Foto> findByReceteId(Long receteId) throws SQLException;
    void delete(Long id) throws SQLException;
}
