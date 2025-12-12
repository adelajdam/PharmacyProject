package dao;

import Model.*;
import db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcFatureDao implements FatureDao {

    @Override
    public Fature create(Fature fature) throws SQLException {
        String sql = """
        INSERT INTO faturet
        (porosi_id, klient_id, data_fatures, shuma_totale, metoda_pageses)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, fature.getPorosi().getIdPorosi());
            ps.setLong(2, fature.getKlienti().getId());
            ps.setDate(3, Date.valueOf(fature.getDataFatures()));
            ps.setDouble(4, fature.getShumaTotale());
            ps.setString(5, fature.getMetodaPageses());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    fature.setIdFature(rs.getLong(1));
                }
            }
        }

        return fature;
    }


    @Override
    public Optional<Fature> findById(Long idFature) throws SQLException {
        String sql = "SELECT * FROM faturet WHERE id_fature = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idFature);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToFature(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Fature> findAll() throws SQLException {
        List<Fature> list = new ArrayList<>();
        String sql = "SELECT * FROM faturet";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapToFature(rs));
            }
        }

        return list;
    }

    @Override
    public Fature update(Fature fature) throws SQLException {
        String sql = """
        UPDATE faturet
        SET porosi_id = ?, klient_id = ?, data_fatures = ?, shuma_totale = ?, metoda_pageses = ?
        WHERE id_fature = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, fature.getPorosi().getIdPorosi());
            ps.setLong(2, fature.getKlienti().getId());
            ps.setDate(3, Date.valueOf(fature.getDataFatures()));
            ps.setDouble(4, fature.getShumaTotale());
            ps.setString(5, fature.getMetodaPageses());
            ps.setLong(6, fature.getIdFature());

            ps.executeUpdate();
        }

        return fature;
    }


    @Override
    public void delete(Long idFature) throws SQLException {
        String sql = "DELETE FROM faturet WHERE id_fature = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idFature);
            ps.executeUpdate();
        }
    }


    private Fature mapToFature(ResultSet rs) throws SQLException {
        Fature fature = new Fature();

        fature.setIdFature(rs.getLong("id_fature"));

        Porosi porosi = new Porosi();
        porosi.setIdPorosi(rs.getLong("porosi_id"));
        fature.setPorosi(porosi);

        Long klientId = rs.getLong("klient_id");
        String role = rs.getString("role");

        Klient klient;

        if ("KLIENT_LOGUAR".equals(role)) {
            klient = new KlientLoguar(
                    klientId,
                    rs.getString("emri"),
                    rs.getString("mbiemri"),
                    null,
                    null,
                    rs.getString("adresa"),
                    rs.getString("nr_tel"),
                    role,
                    rs.getString("email")
            );
        } else {
            klient = new KlientGuest(klientId);
            klient.setEmri(rs.getString("emri"));
            klient.setMbiemri(rs.getString("mbiemri"));
            klient.setAdresa(rs.getString("adresa"));
            klient.setNrTel(rs.getString("nr_tel"));
        }

        fature.setKlienti(klient);

        Date sqlDate = rs.getDate("data_fatures");
        if (sqlDate != null) {
            fature.setDataFatures(sqlDate.toLocalDate());
        }

        fature.setShumaTotale(rs.getDouble("shuma_totale"));
        fature.setMetodaPageses(rs.getString("metoda_pageses"));

        return fature;
    }


}

