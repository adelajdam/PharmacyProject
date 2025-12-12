package Model;

import java.time.LocalDate;

public class Farmacist extends  User {

    public Farmacist(){
        super();
    }

    public Farmacist(Long id, String emri, String mbiemri, String password, LocalDate dataRegjistrimit, String role, String email){
        super(id, emri, mbiemri, password, dataRegjistrimit, "FARMACIST", email);
    }
}
