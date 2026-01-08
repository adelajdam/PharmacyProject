package Model;

import java.time.LocalDate;

public class Rimbursim {
    private Long idRimbursim;
    private Klient klienti;
    private Recete recete;
    private String status;
    private LocalDate dataAplikimit;

    public Rimbursim() {}

    public Rimbursim(Long idRimbursim, Klient klienti, Recete recete,
                     String status, LocalDate dataAplikimit) {
        this.idRimbursim = idRimbursim;
        this.klienti = klienti;
        this.recete = recete;
        this.status = status;
        this.dataAplikimit = dataAplikimit;
    }

    public Long getIdRimbursim() {
        return idRimbursim;
    }

    public Klient getKlienti() {
        return klienti;
    }

    public Recete getRecete() {
        return recete;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDataAplikimit() {
        return dataAplikimit;
    }

    public void setIdRimbursim(Long idRimbursim) {
        this.idRimbursim = idRimbursim;
    }

    public void setKlienti(Klient klienti) {
        this.klienti = klienti;
    }

    public void setRecete(Recete recete) {
        this.recete = recete;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDataAplikimit(LocalDate dataAplikimit) {
        this.dataAplikimit = dataAplikimit;
    }
}