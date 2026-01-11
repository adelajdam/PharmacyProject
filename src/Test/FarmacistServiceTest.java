package Test;

import Model.*;
import dao.*;
import db.DatabaseManager;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;
import service.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FarmacistServiceTest {

    private FarmacistService farmacistService;
    private NjoftimService njoftimService;
    private AdminService adminService;
    private UserService userService;
    private ProduktDao produktDao;
    private User admin;
    private Farmacist f1;
    private KlientLoguar k1;
    private ReceteDao receteDao;

    @BeforeAll
    void setup() throws SQLException {
        System.out.println("===== INIT DATABASE =====");
        DatabaseManager.initDatabase();
        DataSource dataSource = DatabaseManager.getDataSource();

        // ----------------- DAO -----------------
        produktDao = new JdbcProduktDao(dataSource);
        NjoftimDao njoftimDao = new JdbcNjoftimDao(dataSource);
        PorosiProduktDao porosiProdDao = new JdbcPorosiProduktDao(dataSource);
        JdbcPorosiDao porosiDao = new JdbcPorosiDao(dataSource, porosiProdDao);
        EmailSubscriptionDao emailSubDao = new JdbcEmailSubscriptionDao(dataSource);
        EmailService emailService = new EmailService();
        UserDao userDao = new JdbcUserDao(dataSource);
        SystemConfigDao systemConfigDao = new JdbcSystemConfigDao(dataSource);
        AuditLogDao auditLogDao = new JdbcAuditLogDao(dataSource);
        FatureDao fatureDao = new JdbcFatureDao(dataSource);
        receteDao = new JdbcReceteDao();

        // ----------------- SERVICES -----------------
        farmacistService = new FarmacistService(
                produktDao, receteDao, fatureDao, njoftimDao, porosiDao, null
        );

        njoftimService = new NjoftimService(
                produktDao, porosiDao, njoftimDao, emailSubDao, emailService, porosiProdDao
        );

        adminService = new AdminService(userDao, systemConfigDao, auditLogDao);
        userService = new UserService(userDao);

        SystemBootstrap.createInitialAdmin(userDao);
        admin = userDao.findByEmail("admin@system.com").orElseThrow(
                () -> new RuntimeException("Admin nuk u gjet")
        );

        // ================= CREATE FARMACIST =================
        f1 = adminService.createFarmacist(
                "Ardit",
                "Kola",
                "ardit@farmaci.com",
                "pass123",
                admin
        );
        System.out.println("Farmacisti u krijua me sukses: " + f1.getEmail());

        // ================= CREATE CLIENT =================
        k1 = userService.signUpKlient(
                "Ana",
                "Dervishi",
                "ana@test.com",
                "ana123",
                "Tirane",
                "0691111111"
        );
        System.out.println("Klienti u krijua me sukses: " + k1.getEmail());
    }

    @Test
    void testAddAndUpdateProdukt() throws SQLException {
        System.out.println("\n===== SHTO PRODUKT =====");
        Produkt p1 = new Produkt();
        p1.setEmriProd("Ibuprofen");
        p1.setStok(10);
        p1.setCmimi(2.0);

        Produkt added = farmacistService.shtoProdukt(p1, f1);
        System.out.println("Produkt added: " + added.getEmriProd() + ", Stok: " + added.getStok() + ", Cmimi: " + added.getCmimi());

        assertNotNull(added);
        assertEquals("Ibuprofen", added.getEmriProd());
        assertEquals(10, added.getStok());
        assertEquals(2.0, added.getCmimi());

        // UPDATE
        System.out.println("\n--- UPDATE PRODUKT ---");
        added.setCmimi(3.0);
        Produkt updated = farmacistService.updateProdukt(added, f1);
        System.out.println("Produkti u perditesua: " + updated.getEmriProd() + ", Stok: " + updated.getStok() + ", Cmimi: " + updated.getCmimi());

        assertEquals(3.0, updated.getCmimi());
    }

    @Test
    void testFaturatAndPorosite() throws SQLException {
        System.out.println("\n--- FATURAT ---");
        List<Fature> faturat = farmacistService.merrFaturat(f1);
        if (faturat.isEmpty()) {
            System.out.println("Nuk ka fatura të regjistruara.");
        } else {
            for (Fature f : faturat) {
                System.out.println("- Fatura ID: " + f.getIdFature() + " | Totali: " + f.getShumaTotale());
            }
        }

        System.out.println("\n--- POROSITË ---");
        List<Porosi> porosite = farmacistService.merrPorosite(f1);
        if (porosite.isEmpty()) {
            System.out.println("Nuk ka porosi të regjistruara.");
        } else {
            for (Porosi p : porosite) {
                System.out.println("- Porosi ID: " + p.getIdPorosi() + " | Totali: " + p.getTotali());
            }
        }
    }

    @Test
    void testStockNotifications() throws SQLException {
        System.out.println("\n===== STOKU BIE =====");
        Produkt p1 = new Produkt();
        p1.setEmriProd("Ibuprofen");
        p1.setStok(10);

        Produkt added = farmacistService.shtoProdukt(p1, f1);

        assertNotNull(added);
        assertEquals("Ibuprofen", added.getEmriProd());
        assertEquals(10, added.getStok());

        p1.setStok(4);
        farmacistService.updateProdukt(p1, f1);
        njoftimService.kontrolloStokun(f1);

        List<Njoftim> njoftime1 = njoftimService.getAllNjoftime();
        System.out.println("Njoftime pas stok i ulet:");
        njoftime1.forEach(n -> System.out.println("• " + n.getMesazhi()));

        System.out.println("\n===== STOKU ZERO =====");
        p1.setStok(0);
        farmacistService.updateProdukt(p1, f1);
        njoftimService.kontrolloStokun(f1);

        List<Njoftim> njoftime2 = njoftimService.getAllNjoftime();
        System.out.println("Njoftime pas stok 0:");
        njoftime2.forEach(n -> System.out.println("• " + n.getMesazhi()));
    }

    @Test
    void testDeleteProdukt() throws SQLException {
        System.out.println("\n--- DELETE PRODUKT ---");

        // Krijoj produkt pa vendosur ID
        Produkt p1 = new Produkt();
        p1.setEmriProd("Ibuprofen");
        p1.setStok(10);

        // Shto produktin (kjo i jep ID automatikisht)
        Produkt added = farmacistService.shtoProdukt(p1, f1);

        // Verifikimi i shtimit
        assertNotNull(added);

        // Fshi produktin
        farmacistService.fshiProdukt(added.getIdProd(), f1);
        System.out.println("Produkti u fshi: " + added.getEmriProd());

        // Kontrolloj që nuk ekziston më
        assertFalse(produktDao.findById(added.getIdProd()).isPresent(),
                "Produkti duhet të jetë fshirë nga baza");
    }


    @Test
    void testReceteFlow() throws Exception {
        System.out.println("\n===== TEST RECETAT E FARMACISTIT =====");

        Recete recete = new Recete();
        recete.setKlienti(k1);
        recete.setDataRecetes(LocalDate.now());
        recete.setStatusiRecetes("PENDING");
        recete.setFarmacisti(f1);
        recete = receteDao.create(recete);
        System.out.println("Receta e krijuar: ID=" + recete.getIdRecete() + " | Status: " + recete.getStatusiRecetes());
        assertEquals("PENDING", recete.getStatusiRecetes());

        Recete miratuar = farmacistService.miratoRecete(recete.getIdRecete(), f1);
        System.out.println("Receta miratuar: ID=" + miratuar.getIdRecete() + " | Status: " + miratuar.getStatusiRecetes());
        assertEquals("VLEFSHME", miratuar.getStatusiRecetes());

        Recete recete2 = new Recete();
        recete2.setKlienti(k1);
        recete2.setDataRecetes(LocalDate.now());
        recete2.setStatusiRecetes("PENDING");
        recete2.setFarmacisti(f1);
        recete2 = receteDao.create(recete2);
        System.out.println("Receta e krijuar për refuzim: ID=" + recete2.getIdRecete() + " | Status: " + recete2.getStatusiRecetes());

        Recete refuzuar = farmacistService.refuzoRecete(recete2.getIdRecete(), f1);
        System.out.println("Receta refuzuar: ID=" + refuzuar.getIdRecete() + " | Status: " + refuzuar.getStatusiRecetes());
        assertEquals("JO_VLEFSHME", refuzuar.getStatusiRecetes());
    }
}