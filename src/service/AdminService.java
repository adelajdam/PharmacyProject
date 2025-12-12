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


}

