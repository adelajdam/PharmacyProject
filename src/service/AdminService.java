package service;

import dao.UserDao;
import Model.*;
import db.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdminService {

    private final UserDao userDao;

    public AdminService(UserDao userDao) {
        this.userDao = userDao;
    }


    private void ensureAdmin(User user) {
        if (user == null || !"ADMINISTRATOR".equals(user.getRole())) {
            throw new SecurityException("Vetëm administratori mund të kryejë këtë veprim");
        }
    }


    public void backupDatabase(User admin) throws SQLException {

        ensureAdmin(admin);

        String backupFile = "backup_" + LocalDate.now() + ".sql";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {

            stmt.execute("SCRIPT TO '" + backupFile + "'");

        } catch (SQLException e) {
            throw new SQLException("Backup-i dështoi: " + e.getMessage());
        }
    }


    public List<User> getAllFarmacists(User admin) throws SQLException {
        ensureAdmin(admin);
        return userDao.findByRole("FARMACIST");
    }


    public List<User> getAllClients(User admin) throws SQLException {
        ensureAdmin(admin);
        return userDao.findByRole("KLIENT_LOGUAR");
    }


    public List<User> getAllAdministrators(User admin) throws SQLException {
        ensureAdmin(admin);
        return userDao.findByRole("ADMINISTRATOR");
    }


    public void deleteUser(Long userId, User admin) throws SQLException {
        ensureAdmin(admin);

        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty())
            throw new IllegalArgumentException("Përdoruesi nuk u gjet");

        userDao.delete(userId);
    }
    public Farmacist updateFarmacist(
            Long farmacistId,
            String emri,
            String mbiemri,
            String email,
            String password,
            User admin
    ) throws SQLException {

        ensureAdmin(admin);

        Optional<User> userOpt = userDao.findById(farmacistId);
        if (userOpt.isEmpty())
            throw new IllegalArgumentException("Farmacisti nuk u gjet");

        User user = userOpt.get();
        if (!"FARMACIST".equals(user.getRole()))
            throw new IllegalArgumentException("Ky përdorues nuk është farmacist");

        user.setEmri(emri);
        user.setMbiemri(mbiemri);
        user.setEmail(email);
        user.setPassword(password);

        return (Farmacist) userDao.update(user);
    }


    public Administrator updateAdministrator(
            Long adminId,
            String emri,
            String mbiemri,
            String email,
            String password,
            User admin
    ) throws SQLException {

        ensureAdmin(admin);

        Optional<User> userOpt = userDao.findById(adminId);
        if (userOpt.isEmpty())
            throw new IllegalArgumentException("Administratori nuk u gjet");

        User user = userOpt.get();
        if (!"ADMINISTRATOR".equals(user.getRole()))
            throw new IllegalArgumentException("Ky përdorues nuk është administrator");

        user.setEmri(emri);
        user.setMbiemri(mbiemri);
        user.setEmail(email);
        user.setPassword(password);

        return (Administrator) userDao.update(user);
    }

}

