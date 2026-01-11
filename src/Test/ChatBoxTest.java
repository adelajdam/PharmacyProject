package Test;

import Model.*;
import dao.*;
import db.DatabaseManager;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ChatBoxTest {

    private UserDao userDao;
    private ChatBoxDao chatBoxDao;
    private AdminService adminService;
    private UserService userService;
    private FarmacistService farmacistService;
    private KlientService klientService;

    private Farmacist f1;
    private KlientLoguar k1;

    @BeforeEach
    void setUp() throws SQLException {
        // Inicializo databazën
        DatabaseManager.initDatabase();
        userDao = new JdbcUserDao(DatabaseManager.getDataSource());
        chatBoxDao = new JdbcChatBoxDao(DatabaseManager.getDataSource());

        // Inicializo servisat
        adminService = new AdminService(userDao, null, null);
        userService = new UserService(userDao);
        farmacistService = new FarmacistService(null, null, null, null, null, chatBoxDao);
        klientService = new KlientService(null, null, null, null, null, chatBoxDao, userDao);

        // Krijo admin-in
        SystemBootstrap.createInitialAdmin(userDao);
        User admin = userDao.findByEmail("admin@system.com")
                .orElseThrow(() -> new RuntimeException("Admin nuk u gjet"));

        // Krijo dhe ruaj farmacistin
        f1 = adminService.createFarmacist(
                "Ardit", "Kola", "ardit@farmaci.com", "pass123", admin
        );

        // Krijo dhe ruaj klientin
        k1 = userService.signUpKlient(
                "Ana", "Dervishi", "ana@test.com", "ana123", "Tirane", "0691111111"
        );

        // Sigurohu që ID-të ekzistojnë
        System.out.println("Farmacisti ID: " + f1.getId());
        System.out.println("Klienti ID: " + k1.getId());
    }

    @Test
    void testChatClientToPharmacist() throws SQLException {
        // Krijo recetën
        Recete recete = new Recete();
        recete.setIdRecete(1L);


        // Dërgo mesazhin
        try {
            Mesazh m1 = klientService.dergoMesazhMeRecete(
                    k1.getId(),
                    f1.getId(),
                    recete.getIdRecete(),
                    "Pershendetje, kjo është një recetë test.",
                    "/path/to/foto.jpg",
                    k1
            );
            System.out.println("Mesazhi u krijua: " + m1.getPermbajtja());

            Mesazh m2 = farmacistService.dergoMesazh(
                    f1.getId(),
                    k1.getId(),
                    "Faleminderit, po e shqyrtoj recetën.",
                    f1
            );
            System.out.println("Mesazhi i farmacistit: " + m2.getPermbajtja());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}