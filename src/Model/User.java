package Model;

import java.time.LocalDate;

public abstract class User {
    private Long id;
    private String emri;
    private String mbiemri;
    private String password;
    private LocalDate dataRegjistrimit;
    private String role;
    private String email;

    public User(){
    }

    public User(Long id, String emri, String mbiemri, String password, LocalDate dataRegjistrimit, String role, String email){
        this.id=id;
        this.emri=emri;
        this.mbiemri=mbiemri;
        this.password=password;
        this.dataRegjistrimit=dataRegjistrimit;
        this.role=role;
        this.email=email;
    }


    //get&set


    public Long getId() {
        return id;
    }

    public String getEmri() {
        return emri;
    }

    public String getMbiemri() {
        return mbiemri;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getDataRegjistrimit() {
        return dataRegjistrimit;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmri(String emri) {
        this.emri = emri;
    }

    public void setMbiemri(String mbiemri) {
        this.mbiemri = mbiemri;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDataRegjistrimit(LocalDate dataRegjistrimit) {
        this.dataRegjistrimit = dataRegjistrimit;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
