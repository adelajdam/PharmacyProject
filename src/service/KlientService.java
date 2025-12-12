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
        this.porosiDao=porosiDao;
        this.porosiProduktDao=porosiProduktDao;
    }

    public Optional<Produkt> kerkoProdukt(String emerProd) throws SQLException {
        return produktDao.findByName(emerProd);
    }

    public List<Produkt> kerkoProdukteKategori(String kategori) throws SQLException {
        return produktDao.findByCategory(kategori);
    }


    public void shtoNeShporte(Klient klient, Long produktId, int quantity) throws SQLException {

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
    }

    public void fshiNgaShporta(Klient klient, Long produktId) throws SQLException {

        Optional<Shporta> shportaOpt = shportaDao.findByKlient(klient.getId());
        if (shportaOpt.isEmpty()) return;

        Shporta shporta = shportaOpt.get();

        shportaProduktDao.removeProdukt(shporta.getIdShporta(), produktId);
    }

    public Shporta merrShporten(Klient klient) throws SQLException {
        return shportaDao.findByKlient(klient.getId()).orElse(null);
    }

    private double llogaritTotalin(Shporta shporta) throws SQLException {
        double total = 0.0;

        for (ShportaProdukt sp : shporta.getProduktet()) {
            // Merr produktin nga DB për të marrë çmimin aktual
            Produkt p = produktDao.findById(sp.getProduktId())
                    .orElseThrow(() -> new SQLException("Produkti nuk u gjet"));

            total += p.getCmimi() * sp.getQuantity();
        }

        return total;
    }


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
    public Porosi realizoPorosi(Klient klient,
                                String emer,
                                String mbiemer,
                                String nrTel,
                                String adresa) throws SQLException {

        verifikoTeDhenatPerPorosi(klient, emer, mbiemer, nrTel, adresa);

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

        return porosi;
    }


}
