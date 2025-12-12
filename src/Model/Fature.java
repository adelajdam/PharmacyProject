package Model;

import java.time.LocalDate;

public class Fature {
    private Long idFature;
    private Porosi porosi;
    private Klient klienti;
    private LocalDate dataFatures;
    private double shumaTotale;
    private String metodaPageses;

    public Fature(){
    }

    public Fature(Long idFature, Porosi porosi, Klient klienti, LocalDate dataFatures, double shumaTotale, String metodaPageses){
        this.idFature=idFature;
        this.porosi=porosi;
        this.klienti=klienti;
        this.dataFatures=dataFatures;
        this.shumaTotale=shumaTotale;
        this.metodaPageses=metodaPageses;
    }

    public Long getIdFature() {
        return idFature;
    }

    public Klient getKlienti() {
        return klienti;
    }

    public Porosi getPorosi() {
        return porosi;
    }

    public LocalDate getDataFatures() {
        return dataFatures;
    }

    public double getShumaTotale() {
        return shumaTotale;
    }

    public String getMetodaPageses() {
        return metodaPageses;
    }


    public void setKlienti(Klient klienti) {
        this.klienti = klienti;
    }

    public void setIdFature(Long idFature) {
        this.idFature = idFature;
    }

    public void setDataFatures(LocalDate dataFatures) {
        this.dataFatures = dataFatures;
    }

    public void setMetodaPageses(String metodaPageses) {
        this.metodaPageses = metodaPageses;
    }

    public void setPorosi(Porosi porosi) {
        this.porosi = porosi;
    }

    public void setShumaTotale(double shumaTotale) {
        this.shumaTotale = shumaTotale;
    }
}
