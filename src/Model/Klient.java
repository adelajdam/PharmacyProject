package Model;

import java.time.LocalDate;

public abstract class Klient extends User {
    private String adresa;
    private String nrTel;


    public Klient(){
        super();
    }

    public Klient(Long id, String emri, String mbiemri, String password, LocalDate dataRegjistrimit, String adresa, String nrTel, String role, String email){
        super(id, emri, mbiemri, password, dataRegjistrimit, role, email);
        this.adresa=adresa;
        this.nrTel=nrTel;
    }


    public String getAdresa() {
        return adresa;
    }

    public String getNrTel() {
        return nrTel;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setNrTel(String nrTel) {
        this.nrTel = nrTel;
    }
}
