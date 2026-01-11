package Test;

import Model.*;
import dao.*;
import db.DatabaseManager;
import org.junit.jupiter.api.*;
import service.AdminService;
import service.FarmacistService;
import service.KlientService;
import service.UserService;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KlientServiceTest {

    private KlientService klientService;
    private UserService userService;
    private AdminService adminService;
    private FarmacistService farmacistService;
    private Produkt produkt;
    private ProduktDao produktDao;
    private ChatBoxDao chatBoxDao;
    private User admin;
    private Farmacist f1;
    private KlientLoguar k1;

    @BeforeAll
    void setupDatabase() throws SQLException {
        System.out.println("===== INIT DATABASE =====");
        DatabaseManager.initDatabase();
        DataSource ds = DatabaseManager.getDataSource();

        // ================= DAO =================
        produktDao = new JdbcProduktDao(ds);
        ShportaDao shportaDao = new JdbcShportaDao(ds);
        ShportaProduktDao shportaProduktDao = new JdbcShportaProduktDao(ds);
        PorosiProduktDao porosiProdDao = new JdbcPorosiProduktDao(ds);
        JdbcPorosiDao porosiDao = new JdbcPorosiDao(ds, porosiProdDao);
        chatBoxDao = new JdbcChatBoxDao(ds);
        UserDao userDao = new JdbcUserDao(ds);
        SystemConfigDao systemConfigDao = new JdbcSystemConfigDao(ds);
        AuditLogDao auditLogDao = new JdbcAuditLogDao(ds);

        // ================= SERVICE =================
        klientService = new KlientService(
                produktDao,
                shportaDao,
                shportaProduktDao,
                porosiDao,
                porosiProdDao,
                chatBoxDao,
                userDao
        );

        userService = new UserService(userDao);
        adminService = new AdminService(userDao, systemConfigDao, auditLogDao);
        farmacistService = new FarmacistService(produktDao, null, null, null, null, null);

        // ================= CREATE ADMIN =================
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

        // ================= CREATE CLIENT =================
        k1 = userService.signUpKlient(
                "Ana",
                "Dervishi",
                "ana@gmail.com",
                "ana123",
                "Tirane",
                "0691111111"
        );

        produkt = new Produkt();
        produkt.setEmriProd("Paracetamol");
        produkt.setCmimi(2.5);
        produkt.setStok(50);

        produkt = farmacistService.shtoProdukt(produkt, f1);
        System.out.println("Produkt i shtuar për test: " + produkt.getEmriProd() + " | ID: " + produkt.getIdProd());
    }



    @Test
    void testSearchProdukt() throws KlientService.ServiceException, SQLException {
        System.out.println("\n===== SEARCH PRODUCT =====");

        Produkt found = klientService.kerkoProdukt("Paracetamol")
                .orElseThrow(() -> new RuntimeException("Produkti nuk ekziston"));

        System.out.println("Produkti u gjet me sukses: " + found.getEmriProd());
        assertEquals("Paracetamol", found.getEmriProd());
    }

    @Test
    void testAddToCartAndCheck() throws KlientService.ServiceException {
        System.out.println("\n===== ADD TO CART =====");

        klientService.shtoNeShporte(k1, produkt.getIdProd(), 3);

        Shporta shporta = klientService.merrShporten(k1);

        System.out.println("Produkte në shportë: " + shporta.getProduktet().size());
        shporta.getProduktet().forEach(sp ->
                System.out.println("- " + sp.getProdukt().getEmriProd()
                        + " | Sasia: " + sp.getQuantity()
                        + " | Cmimi: " + sp.getProdukt().getCmimi())
        );

        assertEquals(1, shporta.getProduktet().size());
        assertEquals(3, shporta.getProduktet().get(0).getQuantity());
    }

    @Test
    void testPlaceOrder() throws KlientService.ServiceException, SQLException{
        System.out.println("\n===== ADD PRODUCT TO CART BEFORE ORDER =====");

        // Shto produktin global në shportë
        klientService.shtoNeShporte(k1, produkt.getIdProd(), 3);

        Shporta shporta = klientService.merrShporten(k1);
        System.out.println("Produkte në shportë para porosisë: " + shporta.getProduktet().size());
        assertFalse(shporta.getProduktet().isEmpty(), "Shporta duhet të ketë produkt");

        System.out.println("\n===== PLACE ORDER =====");

        Porosi porosi = klientService.realizoPorosi(
                k1,
                "Ana",
                "Dervishi",
                "0691111111",
                "Tirane"
        );

        System.out.println("Porosi u krijua me ID: " + porosi.getIdPorosi());
        System.out.println("Totali: " + porosi.getTotali());

        assertNotNull(porosi);
        assertTrue(porosi.getTotali() > 0);

        System.out.println("\n===== ORDER HISTORY =====");

        List<Porosi> historik = klientService.getPorosiHistorik(k1);
        System.out.println("Nr porosive: " + historik.size());

        historik.forEach(pr ->
                System.out.println("PorosiID=" + pr.getIdPorosi() + ", Totali=" + pr.getTotali())
        );

        assertFalse(historik.isEmpty());
    }


    @Test
    void testUpdateKlientData() throws Exception {
        System.out.println("\n===== TEST UPDATE KLIENT PERSONAL DATA =====");

        System.out.println("Para update:");
        System.out.println("Emri: " + k1.getEmri());
        System.out.println("Mbiemri: " + k1.getMbiemri());
        System.out.println("Email: " + k1.getEmail());
        System.out.println("Adresa: " + k1.getAdresa());
        System.out.println("NrTel: " + k1.getNrTel());

        // ================= UPDATE =================
        KlientLoguar updated = klientService.updateKlientPersonalData(
                k1.getId(),
                "Mira",       // emri i ri
                "Kola",   // mbiemri i ri
                "mirakola@gmail.com", // email i ri
                "newpass123",      // password i ri
                "Tirane, Rruga e Re", // adresa e re
                "0692222222",      // nrTel i ri
                k1                 // user që bën update
        );

        System.out.println("\nPas update:");
        System.out.println("Emri: " + updated.getEmri());
        System.out.println("Mbiemri: " + updated.getMbiemri());
        System.out.println("Email: " + updated.getEmail());
        System.out.println("Adresa: " + updated.getAdresa());
        System.out.println("NrTel: " + updated.getNrTel());

        assertEquals("Mira", updated.getEmri());
        assertEquals("Kola", updated.getMbiemri());
        assertEquals("mirakola@gmail.com", updated.getEmail());
        assertEquals("Tirane, Rruga e Re", updated.getAdresa());
        assertEquals("0692222222", updated.getNrTel());
    }
}