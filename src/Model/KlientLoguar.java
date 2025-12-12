package Model;

import java.time.LocalDate;

public class KlientLoguar extends Klient {
   public KlientLoguar(){
       super();
    }


   public KlientLoguar(Long id, String emri, String mbiemri, String password, LocalDate dataRegjistrimit, String adresa, String nrTel, String role, String email){
        super(id, emri, mbiemri, password, dataRegjistrimit, adresa, nrTel, "KLIENT_LOGUAR", email);
    }
}
