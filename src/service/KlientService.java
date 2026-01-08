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

    public KlientService(ProduktDao produktDao,
                         ShportaDao shportaDao,
                         ShportaProduktDao shportaProduktDao,
                         PorosiDao porosiDao,
                         PorosiProduktDao porosiProduktDao) {

        this.produktDao = produktDao;
        this.shportaDao = shportaDao;
        this.shportaProduktDao = shportaProduktDao;
        this.porosiDao = porosiDao;
        this.porosiProduktDao = porosiProduktDao;
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
}
