package service;

import dao.*;
import Model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FarmacistService {

    private final ProduktDao produktDao;
    private final ReceteDao receteDao;
    private final FatureDao fatureDao;
    private final NjoftimDao njoftimeDao;
    private final PorosiDao porosiDao;
    private final ChatBoxDao chatDao;

    public FarmacistService(
            ProduktDao produktDao,
            ReceteDao receteDao,
            FatureDao fatureDao,
            NjoftimDao njoftimeDao,
            PorosiDao porosiDao,
            ChatBoxDao chatDao
    ) {
        this.produktDao = produktDao;
        this.receteDao = receteDao;
        this.fatureDao = fatureDao;
        this.njoftimeDao = njoftimeDao;
        this.porosiDao = porosiDao;
        this.chatDao = chatDao;
    }

    /* =================== SECURITY =================== */
    private void ensureFarmacist(User user) {
        if (user == null || !"FARMACIST".equals(user.getRole())) {
            throw new SecurityException("Vetëm farmacisti mund të kryejë këtë veprim");
        }
    }

    /* =================== PRODUKTET =================== */

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

    public List<Njoftim> merrNjoftimeProduktJashteStokut(User farmacist) throws SQLException {
        ensureFarmacist(farmacist);
        return njoftimeDao.findByType("STOK_I_ULET");
    }

    /* =================== FATURET =================== */

    public List<Fature> merrFaturat(User farmacist) throws SQLException {
        ensureFarmacist(farmacist);
        return fatureDao.findAll();
    }

    /* =================== POROSITË =================== */

    public List<Porosi> merrPorosite(User farmacist) throws SQLException {
        ensureFarmacist(farmacist);
        return porosiDao.findAll();
    }


    /* =================== RECETAT =================== */

    public Recete miratoRecete(Long receteId, User farmacist) throws SQLException {
        ensureFarmacist(farmacist);
        Recete r = receteDao.findById(receteId)
                .orElseThrow(() -> new IllegalArgumentException("Receta nuk u gjet"));
        r.setStatusiRecetes("VLEFSHME");
        return receteDao.update(r);
    }

    public Recete refuzoRecete(Long receteId, User farmacist) throws SQLException {
        ensureFarmacist(farmacist);
        Recete r = receteDao.findById(receteId)
                .orElseThrow(() -> new IllegalArgumentException("Receta nuk u gjet"));
        r.setStatusiRecetes("JO_VLEFSHME");
        return receteDao.update(r);
    }

    /* =================== NJOFTIMET =================== */

    public List<Njoftim> merrNjoftimePorosiDyshimta(User farmacist) throws SQLException {
        ensureFarmacist(farmacist);
        return njoftimeDao.findByType("POROSI_DYSHIMTE");
    }

    public Njoftim krijoNjoftim(Njoftim n, User farmacist) throws SQLException {
        ensureFarmacist(farmacist);
        return njoftimeDao.create(n);
    }

    /* =================== CHAT BOX =================== */

    public Mesazh dergoMesazh(Long senderId,
                              Long receiverId,
                              String permbajtja,
                              User farmacist) throws SQLException {

        ensureFarmacist(farmacist);

        Mesazh m = new Mesazh(
                senderId,
                receiverId,
                permbajtja
        );

        return chatDao.create(m);
    }


    public List<Mesazh> merrMesazheNgaKlienti(
            Long farmacistId,
            Long klientId,
            User farmacist
    ) throws SQLException {

        ensureFarmacist(farmacist);

        // siguri shtesë: verifikon që id-ja e farmacistit përputhet
        if (!farmacist.getId().equals(farmacistId)) {
            throw new SecurityException("ID e farmacistit nuk përputhet");
        }

        // Merr të gjitha mesazhet midis farmacistit dhe klientit
        return chatDao.findByUsers(klientId, farmacistId);
    }

}
