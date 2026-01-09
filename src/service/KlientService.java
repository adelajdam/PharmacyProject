package service;

import Model.*;
import dao.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class KlientService {

    private final ProduktDao produktDao;
    private final ShportaDao shportaDao;
    private final ShportaProduktDao shportaProduktDao;
    private final PorosiDao porosiDao;
    private final PorosiProduktDao porosiProduktDao;
    private final ChatBoxDao chatBoxDao;
    private final UserDao userDao;

    public KlientService(ProduktDao produktDao,
                         ShportaDao shportaDao,
                         ShportaProduktDao shportaProduktDao,
                         PorosiDao porosiDao,
                         PorosiProduktDao porosiProduktDao,
                         ChatBoxDao chatBoxDao,
                         UserDao userDao) {

        this.produktDao = produktDao;
        this.shportaDao = shportaDao;
        this.shportaProduktDao = shportaProduktDao;
        this.porosiDao = porosiDao;
        this.porosiProduktDao = porosiProduktDao;
        this.chatBoxDao = chatBoxDao;
        this.userDao = userDao;
    }

    // --------------------- PRODUKTE ---------------------
    public Optional<Produkt> kerkoProdukt(String emerProd) throws ServiceException {
        try {
            return produktDao.findByName(emerProd);
        } catch (SQLException e) {
            throw new ServiceException("Gabim gjatë kërkimit të produktit: " + e.getMessage(), e);
        }
    }

    public List<Produkt> kerkoProdukteKategori(String kategori) throws ServiceException {
        try {
            return produktDao.findByCategory(kategori);
        } catch (SQLException e) {
            throw new ServiceException("Gabim gjatë kërkimit të produkteve sipas kategorisë: " + e.getMessage(), e);
        }
    }

    // --------------------- SHPORTA ---------------------
    public void shtoNeShporte(Klient klient, Long produktId, int quantity) throws ServiceException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Sasia duhet të jetë më e madhe se 0.");
        }

        try {
            Optional<Shporta> shportaOpt = shportaDao.findByKlient(klient.getId());
            Shporta shporta;

            if (shportaOpt.isEmpty()) {
                shporta = new Shporta();
                shporta.setKlientId(klient.getId());
                shporta = shportaDao.create(shporta);
            } else {
                shporta = shportaOpt.get();
            }

            shportaProduktDao.addProdukt(shporta.getIdShporta(), produktId, quantity);

            System.out.println("Produkti u shtua në shportë: ProduktID=" + produktId + ", Sasia=" + quantity);

        } catch (SQLException e) {
            throw new ServiceException("Gabim gjatë shtimit të produktit në shportë: " + e.getMessage(), e);
        }
    }

    public void fshiNgaShporta(Klient klient, Long produktId) throws ServiceException {
        try {
            Optional<Shporta> shportaOpt = shportaDao.findByKlient(klient.getId());
            if (shportaOpt.isEmpty()) return;

            Shporta shporta = shportaOpt.get();
            shportaProduktDao.removeProdukt(shporta.getIdShporta(), produktId);

            System.out.println("Produkti u hoq nga shporta: ProduktID=" + produktId);

        } catch (SQLException e) {
            throw new ServiceException("Gabim gjatë fshirjes së produktit nga shporta: " + e.getMessage(), e);
        }
    }

    public Shporta merrShporten(Klient klient) throws ServiceException {
        try {
            return shportaDao.findByKlient(klient.getId()).orElse(null);
        } catch (SQLException e) {
            throw new ServiceException("Gabim gjatë marrjes së shportës: " + e.getMessage(), e);
        }
    }

    // --------------------- LLOGARIT TOTALIN ---------------------
    private double llogaritTotalin(Shporta shporta) throws ServiceException {
        try {
            // Paralelizim për performancë
            return shporta.getProduktet().parallelStream()
                    .mapToDouble(sp -> {
                        try {
                            Produkt p = produktDao.findById(sp.getProduktId())
                                    .orElseThrow(() -> new SQLException("Produkti nuk u gjet: " + sp.getProduktId()));
                            return p.getCmimi() * sp.getQuantity();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .sum();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException) {
                throw new ServiceException("Gabim gjatë llogaritjes së totalit: " + e.getCause().getMessage(), e.getCause());
            }
            throw e;
        }
    }

    // --------------------- VERIFIKIMI ---------------------
    private void verifikoTeDhenatPerPorosi(Klient klient,
                                           String emer,
                                           String mbiemer,
                                           String nrTel,
                                           String adresa) {

        if (klient instanceof KlientLoguar) {
            return;
        }

        if (emer == null || emer.isBlank()
                || mbiemer == null || mbiemer.isBlank()
                || nrTel == null || nrTel.isBlank()
                || adresa == null || adresa.isBlank()) {

            throw new IllegalArgumentException(
                    "Klientët guest duhet të plotësojnë emër, mbiemër, numër telefoni dhe adresë."
            );
        }
    }

    // --------------------- REALIZIMI I POROSISË ---------------------
    public Porosi realizoPorosi(Klient klient,
                                String emer,
                                String mbiemer,
                                String nrTel,
                                String adresa) throws ServiceException {

        verifikoTeDhenatPerPorosi(klient, emer, mbiemer, nrTel, adresa);

        try {
            Shporta shporta = shportaDao.findByKlient(klient.getId()).orElse(null);
            if (shporta == null || shporta.getProduktet().isEmpty()) {
                throw new IllegalStateException("Shporta është bosh.");
            }

            double totali = llogaritTotalin(shporta);

            Porosi porosi = new Porosi();
            porosi.setKlienti(klient);
            porosi.setTotali(totali);

            porosi = porosiDao.create(porosi);

            for (ShportaProdukt sp : shporta.getProduktet()) {
                PorosiProdukt pp = new PorosiProdukt(
                        porosi.getIdPorosi(),
                        sp.getProduktId(),
                        sp.getQuantity()
                );
                porosiProduktDao.addProduktToPorosi(pp);
            }

            shportaDao.clearShporta(klient.getId());

            System.out.println("Porosia u krye me sukses: PorosiID=" + porosi.getIdPorosi());

            return porosi;

        } catch (SQLException e) {
            throw new ServiceException("Gabim gjatë realizimit të porosisë: " + e.getMessage(), e);
        }
    }

    // --------------------- EXCEPTION CUSTOM ---------------------
    public static class ServiceException extends Exception {
        public ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }


    private void ensureKlientLoguar(User user) {
        if (user == null || !"KLIENT_LOGUAR".equals(user.getRole())) {
            throw new SecurityException(
                    "Vetëm klientët e loguar mund të përdorin chatbox"
            );
        }
    }


    public Mesazh dergoMesazhMeRecete(
            Long klientId,
            Long farmacistId,
            Long receteId,
            String permbajtja,
            String fotoPath,
            User klient
    ) throws SQLException {

        ensureKlientLoguar(klient);

        if (!klient.getId().equals(klientId)) {
            throw new SecurityException("ID e klientit nuk përputhet");
        }

        Mesazh m = new Mesazh(
                klientId,
                farmacistId,
                receteId,
                permbajtja,
                fotoPath
        );

        return chatBoxDao.create(m);
    }


    public List<Mesazh> merrMesazheNgaFarmacisti(
            Long klientId,
            Long farmacistId,
            User klient
    ) throws SQLException {

        ensureKlientLoguar(klient);

        // siguri shtesë (rekomanduar)
        if (!klient.getId().equals(klientId)) {
            throw new SecurityException("ID e klientit nuk përputhet");
        }

        return chatBoxDao.findByUsers(farmacistId, klientId);
    }


    public KlientLoguar updateKlientPersonalData(
            Long klientId,
            String emri,
            String mbiemri,
            String email,
            String password,
            String adresa,
            String nrTel,
            User klient
    ) throws SQLException {

        ensureKlientLoguar(klient);

        if (!klient.getId().equals(klientId)) {
            throw new SecurityException("ID e klientit nuk përputhet");
        }

        Optional<User> userOpt = userDao.findById(klientId);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("Klienti nuk u gjet");

        KlientLoguar k = (KlientLoguar) userOpt.get();

        if (emri != null && !emri.isBlank()) k.setEmri(emri);
        if (mbiemri != null && !mbiemri.isBlank()) k.setMbiemri(mbiemri);
        if (email != null && !email.isBlank()) k.setEmail(email);
        if (password != null && !password.isBlank()) k.setPassword(password); // hash if needed
        if (adresa != null && !adresa.isBlank()) k.setAdresa(adresa);
        if (nrTel != null && !nrTel.isBlank()) k.setNrTel(nrTel);

        return (KlientLoguar) userDao.update(k);
    }


    public List<Porosi> getPorosiHistorik(Klient klient) throws SQLException {
        ensureKlientLoguar(klient);
        return porosiDao.findByKlientId(klient.getId());
    }



}
