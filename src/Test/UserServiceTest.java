package Test;

import Model.KlientLoguar;
import Model.User;
import dao.JdbcUserDao;
import dao.UserDao;
import db.DatabaseManager;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() throws SQLException {
        // Inicializo databazën
        DatabaseManager.initDatabase();

        // DataSource për H2 in-memory
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        // Inicializo DAO dhe Service
        userDao = new JdbcUserDao(dataSource);
        userService = new UserService(userDao);

        // Krijo admin-in e parë
        SystemBootstrap.createInitialAdmin(userDao);
    }

    @Test
    void testSignUpKlienti() throws SQLException {
        KlientLoguar k1 = userService.signUpKlient(
                "Ana", "Dervishi", "ana3@gmail.com", "ana123", "Tirane", "0691111111"
        );

        KlientLoguar k2 = userService.signUpKlient(
                "Erion", "Hoxha", "erion@gmail.com", "erion123", "Durres", "0682222222"
        );

        assertNotNull(k1);
        assertNotNull(k2);

        assertEquals("Ana", k1.getEmri());
        assertEquals("Erion", k2.getEmri());

        assertEquals("ana3@gmail.com", k1.getEmail());
        assertEquals("erion@gmail.com", k2.getEmail());
    }

    @Test
    void testLoginKlienti() throws SQLException {
        // Sign up klient
        userService.signUpKlient("Ana", "Dervishi", "ana@gmail.com", "ana123", "Tirane", "0691111111");

        KlientLoguar loggedClient = userService.loginKlient("ana@gmail.com", "ana123");
        System.out.println("Klienti u logua: " + loggedClient.getEmri());
        assertNotNull(loggedClient);
        assertEquals("Ana", loggedClient.getEmri());
    }

    @Test
    void testLoginAdmin() throws SQLException {
        User admin = userService.loginDashboard("admin@system.com", "admin123");
        System.out.println("Admini u logua: " + admin.getEmri());
        assertNotNull(admin);
        assertEquals("admin@system.com", admin.getEmail());
    }

//    @Test
//    void testLoginGabim() throws SQLException {
//        userService.signUpKlient("Ana", "Dervishi", "ana@gmail.com", "ana123", "Tirane", "0691111111");
//
//        // Password i gabuar
//        Exception e1 = assertThrows(Exception.class, () -> {
//            userService.loginKlient("ana@gmail.com", "gabim");
//        });
//        assertTrue(e1.getMessage().contains("Gabim"));
//
//        // Email i gabuar
//        Exception e2 = assertThrows(Exception.class, () -> {
//            userService.loginKlient("mira@gmail.com", "ana123");
//        });
//        assertTrue(e2.getMessage().contains("Gabim"));
//    }

    @Test
    void testLogout() throws SQLException {
        // Sign up dhe login klient
        userService.signUpKlient("Ana", "Dervishi", "ana@gmail.com", "ana123", "Tirane", "0691111111");
        userService.loginKlient("ana@gmail.com", "ana123");

        // Logout
        userService.logout();
        System.out.println("Logout u krye me sukses");

        // Përdorimi i një metodi pas logout mund të shkaktojë ndryshim të gjendjes,
        // por kjo varet nga implementimi i UserService (kryesisht duhet të mos ketë session)
        // Këtu thjesht verifiko që nuk hedh exception
        assertDoesNotThrow(() -> userService.logout());
    }
}