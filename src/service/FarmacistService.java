package service;

import dao.*;
import Model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FarmacistService {

    private final ProduktDao produktDao;
    private final ReceteDao receteDao;
    private final FatureDao fatureDao;
    private final NjoftimDao njoftimeDao;
    private final PorosiDao porosiDao;

    public FarmacistService(ProduktDao produktDao,
                            ReceteDao receteDao,
                            FatureDao fatureDao,
                            NjoftimDao njoftimeDao,
                            PorosiDao porosiDao) {

        this.produktDao = produktDao;
        this.receteDao = receteDao;
        this.fatureDao = fatureDao;
        this.njoftimeDao = njoftimeDao;
        this.porosiDao = porosiDao;

    }


    private void ensureFarmacist(User user) {
        if (user == null || !"FARMACIST".equals(user.getRole())) {
            throw new SecurityException("Vetëm farmacisti mund të kryejë këtë veprim");
        }
    }


    public Produkt shtoProdukt(Produkt p, User farmacist) throws SQLException {
        ensureFarmacist(farmacist);
        return produktDao.create(p);
    }


    public Produkt updateProdukt(Produkt p, User farmacist) throws SQLException {
        ensureFarmacist(farmacist);

        return produktDao.update(p);
    }


    public void fshiProdukt(Long produktId, User farmacist) throws SQLException {
        ensureFarmacist(farmacist);

        produktDao.delete(produktId);
    }


    public List<Fature> merrFaturat(User farmacist) throws SQLException {
        ensureFarmacist(farmacist);

        return fatureDao.findAll();
    }


    public List<Njoftim> merrNjoftimePorosiDyshimta(User farmacist) throws SQLException {
        ensureFarmacist(farmacist);

        return njoftimeDao.findByType("POROSI_DYSHIMTE");
    }


    public List<Njoftim> merrNjoftimeProduktJashteStokut(User farmacist) throws SQLException {
        ensureFarmacist(farmacist);

        return njoftimeDao.findByType("STOK_I_ULET");
    }
    public List<Produkt> merrProduktetMeStokTeUlet(User farmacist) throws SQLException {
        ensureFarmacist(farmacist);

        return produktDao.findStokBelow(5);
    }

    public Fature krijoFature(Fature f, User farmacist) throws SQLException {
        ensureFarmacist(farmacist);

        f.setDataFatures(LocalDate.now());
        return fatureDao.create(f);
    }
    public Recete verifikoRecete(Long receteId, boolean aprovohet, User farmacist) throws SQLException {
        ensureFarmacist(farmacist);

        Optional<Recete> receteOpt = receteDao.findById(receteId);
        if (receteOpt.isEmpty())
            throw new IllegalArgumentException("Receta nuk u gjet");

        Recete r = receteOpt.get();
        r.setStatusiRecetes(aprovohet ? "APROVUAR" : "REFUZUAR");

        return receteDao.update(r);
    }
}

