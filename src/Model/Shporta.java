package Model;

import java.util.ArrayList;
import java.util.List;

public class Shporta {

    private Long idShporta;
    private Long klientId;
    private List<ShportaProdukt> produktet = new ArrayList<>();

    public Shporta() {}

    public Shporta(Long idShporta, Long klientId, List<ShportaProdukt> produktet) {
        this.idShporta = idShporta;
        this.klientId = klientId;
        this.produktet = produktet;
    }

    public Long getIdShporta() {
        return idShporta;
    }

    public void setIdShporta(Long idShporta) {
        this.idShporta = idShporta;
    }

    public Long getKlientId() {
        return klientId;
    }

    public void setKlientId(Long klientId) {
        this.klientId = klientId;
    }

    public List<ShportaProdukt> getProduktet() {
        return produktet;
    }

    public void setProduktet(List<ShportaProdukt> produktet) {
        this.produktet = produktet;
    }

}
