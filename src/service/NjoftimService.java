package service;

import Model.*;
import dao.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class NjoftimService {

    private final ProduktDao produktDao;
    private final PorosiDao porosiDao;
    private final NjoftimDao njoftimeDao;
    private final EmailSubscriptionDao emailSubscriptionDao;
    private final EmailService emailService;
    private final PorosiProduktDao porosiProduktDao;

    public NjoftimService(ProduktDao produktDao, PorosiDao porosiDao, NjoftimDao njoftimeDao, EmailSubscriptionDao emailSubscriptionDao, EmailService emailService, PorosiProduktDao porosiProduktDao) {
        this.produktDao = produktDao;
        this.porosiDao = porosiDao;
        this.njoftimeDao = njoftimeDao;
        this.emailSubscriptionDao=emailSubscriptionDao;
        this.emailService=emailService;
        this.porosiProduktDao=porosiProduktDao;
    }


    public void kontrolloStokun(User marrësiNjoftimit) throws SQLException {
        List<Produkt> lista = produktDao.findAll();

        for (Produkt p : lista) {

            if (p.getStok() == 0) {
                Njoftim n = new Njoftim(
                        "PRODUKT_JASHTE_STOKUT",
                        "Produkti '" + p.getEmriProd() + "' ka stok 0"
                );
                n.setPerdoruesi(marrësiNjoftimit);
                njoftimeDao.create(n);
            }

            else if (p.getStok() < 5) {
                Njoftim n = new Njoftim(
                        "STOK_I_ULET",
                        "Produkti '" + p.getEmriProd() + "' ka stok të ulët (" + p.getStok() + ")"
                );
                n.setPerdoruesi(marrësiNjoftimit);
                njoftimeDao.create(n);
            }
        }
    }


    public void kontrolloPorositeDyshimta(User marrësiNjoftimit) throws SQLException {
        List<Porosi> porosite = porosiDao.findAll();

        for (Porosi p : porosite) {

            boolean dyshimt = false;

            if (p.getTotali() > 50000) {
                dyshimt = true;
            }

            int totalQuantity = p.getProduktet()
                    .stream()
                    .mapToInt(PorosiProdukt::getQuantity)
                    .sum();

            if (totalQuantity > 20) {
                dyshimt = true;
            }

            if (p.getKlienti() == null || p.getKlienti().getId() == null) {
                dyshimt = true;
            }

            if (dyshimt) {
                Njoftim n = new Njoftim(
                        "POROSI_DYSHIMTE",
                        "Porosi e dyshimtë nga klienti ID: " + p.getKlienti().getId()
                );
                n.setPerdoruesi(marrësiNjoftimit);
                njoftimeDao.create(n);
            }
        }
    }


    public void njoftoGuestsKurProduktKthehetNeStok(Produkt produkt) throws SQLException {
        if (produkt.getStok() <= 0) return;

        List<EmailSubscription> subs = emailSubscriptionDao.findByProduktId(produkt.getIdProd());

        for (EmailSubscription s : subs) {


            emailService.dergoEmail(
                    s.getEmail(),
                    "Produkti është rikthyer në stok",
                    "Përshëndetje!\n\n" +
                            "Produkti '" + produkt.getEmriProd() + "' është rikthyer në stok.\n" +
                            "Ju falenderojmë që përdorni platformën tonë!"
            );


            emailSubscriptionDao.delete(s.getIdEmail());
        }
    }


    public void njoftoKlientetLoguarPerProduktNeStok(Produkt produkt) throws SQLException {
        if (produkt.getStok() <= 0) return;

        List<PorosiProdukt> porosiProdukte = porosiProduktDao.findByProduktId(produkt.getIdProd());


        for (PorosiProdukt pp : porosiProdukte) {

            Optional<Porosi> porosiOpt = porosiDao.findById(pp.getPorosiId());
            if (porosiOpt.isEmpty()) continue;

            Porosi porosi = porosiOpt.get();


            if (!(porosi.getKlienti() instanceof KlientLoguar)) {
                continue;
            }


            Njoftim n = new Njoftim(
                    "PRODUKT_NE_STOK",
                    "Produkti '" + produkt.getEmriProd() + "' është rikthyer në stok!"
            );

            n.setPerdoruesi(porosi.getKlienti());

            njoftimeDao.create(n);
        }
    }


}
