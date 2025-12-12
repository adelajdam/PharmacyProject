package Model;

import java.time.LocalDate;

public class EmailSubscription {
    private Long idEmail;
    private String email;
    private Produkt produkt;
    private LocalDate dataKerkeses;

    public EmailSubscription() {}

    public EmailSubscription(Long idEmail, String email, Produkt produkt, LocalDate dataKerkeses) {
        this.idEmail = idEmail;
        this.email = email;
        this.produkt = produkt;
        this.dataKerkeses = dataKerkeses;
    }

    public Long getIdEmail() {
        return idEmail;
    }

    public String getEmail() {
        return email;
    }

    public Produkt getProdukt() {
        return produkt;
    }

    public LocalDate getDataKerkeses() {
        return dataKerkeses;
    }

    public void setIdEmail(Long id) {
        this.idEmail = idEmail;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    public void setDataKerkeses(LocalDate dataRegjistrimit) {
        this.dataKerkeses = dataRegjistrimit;
    }
}

