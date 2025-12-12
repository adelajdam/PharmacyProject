package Model;

public class KlientGuest extends Klient {

    public KlientGuest(){
    }

    public KlientGuest(Long id){
        super(id, null, null, null, null, null, null, "KLIENT_GUEST", null);
    }

    public void setGuest(String emri, String mbiemri, String adresa, String nrTel, String role){
        setEmri(emri);
        setMbiemri(mbiemri);
        setAdresa(adresa);
        setNrTel(nrTel);
        setRole("KLIENT_GUEST");
    }
}
