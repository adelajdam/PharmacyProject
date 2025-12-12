package Model;

import java.time.LocalDate;
import java.util.List;

public class Recete {
    private Long idRecete;
    private Klient klienti;
    private Farmacist farmacisti;
    private List<Produkt> produktet;
    private LocalDate dataRecetes;
    private String statusiRecetes;
    private byte[] fotoReceta;


    public Recete(){
    }


    public Recete(Long idRecete, Klient klienti, Farmacist farmacisti, List<Produkt> produktet, LocalDate dataRecetes, String statusiRecetes, byte[] fotoReceta){
        this.idRecete=idRecete;
        this.klienti=klienti;
        this.farmacisti=farmacisti;
        this.produktet=produktet;
        this.dataRecetes=dataRecetes;
        this.statusiRecetes=statusiRecetes;
        this.fotoReceta=fotoReceta;
    }


    public Long getIdRecete() {
        return idRecete;
    }

    public Klient getKlienti() {
        return klienti;
    }

    public Farmacist getFarmacisti() {
        return farmacisti;
    }

    public List<Produkt> getProduktet() {
        return produktet;
    }

    public LocalDate getDataRecetes() {
        return dataRecetes;
    }

    public String getStatusiRecetes() {
        return statusiRecetes;
    }

    public byte[] getFotoReceta() {
        return fotoReceta;
    }


    public void setIdRecete(Long idRecete) {
        this.idRecete = idRecete;
    }

    public void setKlienti(Klient klienti) {
        this.klienti = klienti;
    }

    public void setFarmacisti(Farmacist farmacisti) {
        this.farmacisti = farmacisti;
    }

    public void setProduktet(List<Produkt> produktet) {
        this.produktet = produktet;
    }

    public void setDataRecetes(LocalDate dataRecetes) {
        this.dataRecetes = dataRecetes;
    }

    public void setStatusiRecetes(String statusiRecetes) {
        this.statusiRecetes = statusiRecetes;
    }

    public void setFotoReceta(byte[] fotoReceta) {
        this.fotoReceta = fotoReceta;
    }
}
