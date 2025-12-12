package Model;

import java.time.LocalDate;
import java.util.List;

public class Porosi {
    private Long idPorosi;
    private Klient klienti;
    private LocalDate dataPorosise;
    private double totali;
    private String status;
    private List<PorosiProdukt> produktet;



    public Porosi(){
    }


    public Porosi(Long idPorosi, Klient klienti, List<PorosiProdukt> produktet, LocalDate dataPorosise, double totali, String status){
        this.idPorosi=idPorosi;
        this.klienti=klienti;
        this.produktet=produktet;
        this.dataPorosise=dataPorosise;
        this.totali=totali;
        this.status=status;
    }


    public Long getIdPorosi() {
        return idPorosi;
    }

    public Klient getKlienti() {
        return klienti;
    }

    public List<PorosiProdukt> getProduktet() {
        return produktet;
    }

    public double getTotali() {
        return totali;
    }

    public LocalDate getDataPorosise() {
        return dataPorosise;
    }

    public String getStatus() {
        return status;
    }


    public void setIdPorosi(Long idPorosi) {
        this.idPorosi = idPorosi;
    }

    public void setKlienti(Klient klienti) {
        this.klienti = klienti;
    }

    public void setDataPorosise(LocalDate dataPorosise) {
        this.dataPorosise = dataPorosise;
    }

    public void setProduktet(List<PorosiProdukt> produktet) {
        this.produktet = produktet;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotali(double totali) {
        this.totali = totali;
    }
}
