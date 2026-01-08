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

    /* ================== HELPER ================== */

    private void ensureAdmin(User user) {
        if (user == null || !"ADMINISTRATOR".equals(user.getRole())) {
            throw new SecurityException("Vetëm administratori mund të kryejë këtë veprim");
        }
    }



    /* ================== USER MANAGEMENT ================== */

    // Krijimi i farmacistit
    public Farmacist createFarmacist(
            String emri,
            String mbiemri,
            String email,
            String password,
            User admin
    ) throws SQLException {

        ensureAdmin(admin);

        if (userDao.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Ky email është i zënë");

        Farmacist f = new Farmacist();
        f.setEmri(emri);
        f.setMbiemri(mbiemri);
        f.setEmail(email);
        f.setPassword(password);
        f.setRole("FARMACIST");
        f.setDataRegjistrimit(LocalDate.now());

        return (Farmacist) userDao.create(f);
    }

    // Krijimi i administratorit
    public Administrator createAdministrator(
            String emri,
            String mbiemri,
            String email,
            String password,
            User admin
    ) throws SQLException {

        ensureAdmin(admin);

        if (userDao.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Ky email është i zënë");

        Administrator a = new Administrator();
        a.setEmri(emri);
        a.setMbiemri(mbiemri);
        a.setEmail(email);
        a.setPassword(password);
        a.setRole("ADMINISTRATOR");
        a.setDataRegjistrimit(LocalDate.now());

        return (Administrator) userDao.create(a);
    }

    // Fshirja e përdoruesit
    public void deleteUser(Long userId, User admin) throws SQLException {
        ensureAdmin(admin);

        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty())
            throw new IllegalArgumentException("Përdoruesi nuk u gjet");

        userDao.delete(userId);
    }


    // Përditësimi i farmacistit
    public Farmacist updateFarmacist(
            Long farmacistId,
            String emri,
            String mbiemri,
            String email,
            String password,
            User admin
    ) throws SQLException {

        ensureAdmin(admin);

        User user = userDao.findById(farmacistId)
                .orElseThrow(() -> new IllegalArgumentException("Farmacisti nuk u gjet"));

        if (!"FARMACIST".equals(user.getRole()))
            throw new IllegalArgumentException("Ky përdorues nuk është farmacist");

        user.setEmri(emri);
        user.setMbiemri(mbiemri);
        user.setEmail(email);
        user.setPassword(password);

        return (Farmacist) userDao.update(user);
    }

    /* ================== QUERIES ================== */

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

    /* ================== SYSTEM CONFIG ================== */
    // (strukturë bazë – mund të zgjerohet)

    public void updateSystemConfig(String key, String value, User admin) {
        ensureAdmin(admin);
        // systemConfigDao.update(key, value);
    }

    /* ================== AUDIT / REPORTS ================== */
    // Placeholder – strukturë e gatshme për zgjerim

    public void logAction(Long userId, String action) {
        // auditLogDao.save(...)
    }

    /* ================== BACKUP & RESTORE ================== */

    public void backupDatabase(User admin) throws SQLException {
        ensureAdmin(admin);

        String backupFile = "backup_" + LocalDate.now() + ".sql";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {

            stmt.execute("SCRIPT TO '" + backupFile + "'");
        }
    }

    public void restoreDatabase(String backupFile, User admin) throws SQLException {
        ensureAdmin(admin);

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {

            stmt.execute("RUNSCRIPT FROM '" + backupFile + "'");
        }
    }
}
