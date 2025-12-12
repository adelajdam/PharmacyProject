package Model;

import java.time.LocalDate;

public class Administrator extends  User {

    public Administrator(){
        super();
    }

    public Administrator(Long id, String emri, String mbiemri, String password, LocalDate dataRegjistrimit, String role, String email){
        super(id, emri, mbiemri, password, dataRegjistrimit, "ADMINISTRATOR", email);
    }
}
