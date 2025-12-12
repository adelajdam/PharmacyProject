package dao;

import Model.*;
import db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcReceteDao implements ReceteDao {

    @Override
    public Recete create(Recete recete) throws SQLException {
        String sql = """
                INSERT INTO recetat
                (klient_id, farmacist_id, data_recetes, statusi_recetes, foto_receta)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            KlientLoguar klient = (KlientLoguar) recete.getKlienti();

            ps.setLong(1, klient.getId());
            ps.setLong(2, recete.getFarmacisti().getId());
            ps.setDate(3, Date.valueOf(recete.getDataRecetes()));
            ps.setString(4, recete.getStatusiRecetes());
            ps.setBytes(5, recete.getFotoReceta());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    recete.setIdRecete(rs.getLong(1));
                }
            }
        }

        return recete;
    }


    @Override
    public Optional<Recete> findById(Long idRecete) throws SQLException {
        String sql = "SELECT * FROM recetat WHERE id_recete = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idRecete);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToRecete(rs));
                }
            }
        }

        return Optional.empty();
    }


    @Override
    public List<Recete> findAll() throws SQLException {
        List<Recete> list = new ArrayList<>();
        String sql = "SELECT * FROM recetat";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapToRecete(rs));
            }
        }

        return list;
    }


    @Override
    public Recete update(Recete recete) throws SQLException {
        String sql = """
                UPDATE recetat
                SET farmacist_id = ?, data_recetes = ?, statusi_recetes = ?, foto_receta = ?
                WHERE id_recete = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, recete.getFarmacisti().getId());
            ps.setDate(2, Date.valueOf(recete.getDataRecetes()));
            ps.setString(3, recete.getStatusiRecetes());
            ps.setBytes(4, recete.getFotoReceta());
            ps.setLong(5, recete.getIdRecete());

            ps.executeUpdate();
        }

        return recete;
    }


    @Override
    public void delete(Long idRecete) throws SQLException {
        String sql = "DELETE FROM recetat WHERE id_recete = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idRecete);
            ps.executeUpdate();
        }
    }


    private Recete mapToRecete(ResultSet rs) throws SQLException {
        Recete r = new Recete();

        r.setIdRecete(rs.getLong("id_recete"));


        KlientLoguar klient = new KlientLoguar();
        klient.setId(rs.getLong("klient_id"));
        r.setKlienti(klient);


        Farmacist f = new Farmacist();
        f.setId(rs.getLong("farmacist_id"));
        r.setFarmacisti(f);

        Date sqlDate = rs.getDate("data_recetes");
        if (sqlDate != null) {
            r.setDataRecetes(sqlDate.toLocalDate());
        }

        r.setStatusiRecetes(rs.getString("statusi_recetes"));
        r.setFotoReceta(rs.getBytes("foto_receta"));


        r.setProduktet(new ArrayList<>());

        return r;
    }
}
