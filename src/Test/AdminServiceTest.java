package Test;

import Model.*;
import dao.*;
import db.DatabaseManager;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AdminService;
import service.UserService;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {

    private UserService userService;
    private AdminService adminService;
    private User admin;

    @BeforeEach
    void setUp() throws SQLException {
        // Inicializo databazën
        DatabaseManager.initDatabase();
        DataSource dataSource = DatabaseManager.getDataSource();

        // DAO
        UserDao userDao = new JdbcUserDao(dataSource);
        SystemConfigDao systemConfigDao = new JdbcSystemConfigDao(dataSource);
        AuditLogDao auditLogDao = new JdbcAuditLogDao(dataSource);

        // Services
        userService = new UserService(userDao);
        adminService = new AdminService(userDao, systemConfigDao, auditLogDao);

        // Bootstrapi admin
        SystemBootstrap.createInitialAdmin(userDao);
        admin = userService.loginDashboard("admin@system.com", "admin123");
        assertNotNull(admin);
    }

    @Test
    void testCreateFarmacists() throws Exception {
        Farmacist f1 = adminService.createFarmacist("Ardit", "Kola", "ardit@farmaci.com", "pass123", admin);
        Farmacist f2 = adminService.createFarmacist("Elira", "Hysa", "elira@farmaci.com", "pass456", admin);

        assertNotNull(f1);
        assertNotNull(f2);

        assertEquals("ardit@farmaci.com", f1.getEmail());
        assertEquals("elira@farmaci.com", f2.getEmail());

        List<User> farmacists = adminService.getAllFarmacists(admin);
        assertEquals(2, farmacists.size());

        System.out.println("Farmacisti 1 u krijua: " + f1.getEmri() + " (" + f1.getEmail() + ")");
        System.out.println("Farmacisti 2 u krijua: " + f2.getEmri() + " (" + f2.getEmail() + ")");
    }

    @Test
    void testCreateAdmin() throws Exception {
        Administrator admin2 = adminService.createAdministrator("Elona", "Marku", "elona@admin.com", "admin456", admin);
        assertNotNull(admin2);
        assertEquals("elona@admin.com", admin2.getEmail());
        System.out.println("Admini u krijua: " + admin2.getEmri() + " (" + admin2.getEmail() + ")");
    }

    @Test
    void testCreateAndDeleteClient() throws Exception {
        KlientLoguar klient = userService.signUpKlient("Ana", "Dervishi", "ana@test.com", "ana123", "Tirane", "0691111111");
        assertNotNull(klient);

        // Delete client
        assertDoesNotThrow(() -> adminService.deleteUser(klient.getId(), admin));

        // Pas delete, login duhet të dështojë
        Exception exception = assertThrows(Exception.class, () ->
                userService.loginKlient("ana@test.com", "ana123")
        );
        assertTrue(exception.getMessage().contains("Gabim"));
    }

    @Test
    void testSecurityAccess() {
        KlientLoguar klient = assertDoesNotThrow(() ->
                userService.signUpKlient("Ana", "Dervishi", "ana@test.com", "ana123", "Tirane", "0691111111")
        );

        // Klienti nuk duhet të mund të shikojë farmacistët
        Exception exception = assertThrows(Exception.class, () -> adminService.getAllFarmacists(klient));
        assertTrue(exception.getMessage().contains("Access denied") || exception.getMessage().contains("siguria"));
    }

    @Test
    void testSystemConfigUpdate() throws Exception {
        // Update system config
        assertDoesNotThrow(() -> adminService.updateSystemConfig("maintenance_mode", "true", admin));
        System.out.println("System config 'maintenance_mode' u vendos në true");

        assertDoesNotThrow(() -> adminService.updateSystemConfig("maintenance_mode", "false", admin));
        System.out.println("System config 'maintenance_mode' u vendos në false");
    }

    @Test
    void testAuditLog() throws Exception {
        // Log action
        adminService.logAction(admin, "Test audit log");
        System.out.println("Veprimi 'Test audit log' u regjistrua për adminin");

        List<AuditLog> logs = adminService.getAuditLogs(admin);
        assertFalse(logs.isEmpty(), "Audit logs duhet të jenë jo bosh");
        assertEquals("Test audit log", logs.get(0).getVeprimi(), "Veprimi i parë duhet të jetë 'Test audit log'");

        System.out.println("Audit logs për adminin:");
        for (AuditLog log : logs) {
            System.out.println("• Veprimi: " + log.getVeprimi() + ", Koha: " + log.getDataKoha());
        }
    }

    @Test
    void testBackupAndRestore() {
        // Backup dhe restore
        assertDoesNotThrow(() -> adminService.backupDatabase(admin));
        String backupFile = "backup_" + java.time.LocalDate.now() + ".sql";
        assertDoesNotThrow(() -> adminService.restoreDatabase(backupFile, admin));
    }

    @Test
    void testListFarmacists() throws Exception {
        // Krijojmë disa farmacistë
        adminService.createFarmacist("Ardit", "Kola", "ardit@farmaci.com", "pass123", admin);
        adminService.createFarmacist("Elira", "Hysa", "elira@farmaci.com", "pass456", admin);

        // Merrim listën e farmacistëve
        List<User> farmacists = adminService.getAllFarmacists(admin);

        // Verifikojmë që ka 2 farmacistë
        assertEquals(2, farmacists.size());

        // Opsionale: verifikojmë emrat
        assertTrue(farmacists.stream().anyMatch(f -> f.getEmri().equals("Ardit")));
        assertTrue(farmacists.stream().anyMatch(f -> f.getEmri().equals("Elira")));

        // Nëse dëshiron të printosh për debug
        farmacists.forEach(f -> System.out.println("• " + f.getEmri() + " " + f.getMbiemri()));
    }

}