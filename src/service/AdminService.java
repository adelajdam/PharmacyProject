package service;

import dao.UserDao;
import Model.*;
import db.DatabaseManager;
import dao.SystemConfigDao;
import dao.AuditLogDao;


import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AdminService {

    private final UserDao userDao;
    private final SystemConfigDao systemConfigDao;
    private final AuditLogDao auditLogDao;

    public AdminService(UserDao userDao,
                        SystemConfigDao systemConfigDao,
                        AuditLogDao auditLogDao) {

        this.userDao = userDao;
        this.systemConfigDao = systemConfigDao;
        this.auditLogDao = auditLogDao;
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

        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Përdoruesi nuk u gjet"));

        if ("KLIENT_LOGUAR".equals(user.getRole())) {
            throw new SecurityException("Administratori nuk mund të fshijë klientë");
        }

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

    public void updateSystemConfig(String key, String value, User admin) throws SQLException {
        ensureAdmin(admin);

        SystemConfig existingConfig = systemConfigDao.findByKey(key);

        if (existingConfig == null) {
            systemConfigDao.save(new SystemConfig(key, value));
        } else {
            systemConfigDao.update(key, value);
        }

        AuditLog log = new AuditLog();
        log.setUserId(admin.getId());
        log.setVeprimi("Updated system configuration: " + key + " = " + value);
        log.setDataKoha(java.time.LocalDateTime.now());

        auditLogDao.create(log);
    }



    /* ================== AUDIT / REPORTS ================== */

    public void logAction(User user, String veprimi) {
        try {
            AuditLog log = new AuditLog();
            log.setUserId(user.getId());
            log.setVeprimi(veprimi);

            auditLogDao.create(log);
        } catch (SQLException e) {
            throw new RuntimeException("Audit log dështoi", e);
        }
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

        } catch (org.h2.jdbc.JdbcSQLSyntaxErrorException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Tabela ekziston, restore nuk u ekzekutua: " + e.getMessage());
            } else {
                throw e;
            }
        }
    }


}
