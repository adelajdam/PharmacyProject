package Model;

import java.time.LocalDateTime;

public class Njoftim {
    private Long idNjoftimi;
    private User perdoruesi;
    private String mesazhi;
    private LocalDateTime dataKoha;
    private boolean eshteLexuar;
    private String tipi;


    public Njoftim(){
    }


    public Njoftim(String tipi, String mesazhi) {
        this.tipi = tipi;
        this.mesazhi = mesazhi;
    }


    public Njoftim(Long idNjoftimi, User perdoruesi, String mesazhi, LocalDateTime dataKoha, boolean eshteLexuar){
        this.idNjoftimi=idNjoftimi;
        this.perdoruesi=perdoruesi;
        this.mesazhi=mesazhi;
        this.dataKoha=dataKoha;
        this.eshteLexuar=eshteLexuar;
    }


    public Long getIdNjoftimi() {
        return idNjoftimi;
    }

    public User getPerdoruesi() {
        return perdoruesi;
    }

    public boolean isEshteLexuar() {
        return eshteLexuar;
    }

    public LocalDateTime getDataKoha() {
        return dataKoha;
    }

    public String getMesazhi() {
        return mesazhi;
    }

    public String getTipi() {
        return tipi;
    }

    public void setDataKoha(LocalDateTime dataKoha) {
        this.dataKoha = dataKoha;
    }

    public void setEshteLexuar(boolean eshteLexuar) {
        this.eshteLexuar = eshteLexuar;
    }

    public void setIdNjoftimi(Long idNjoftimi) {
        this.idNjoftimi = idNjoftimi;
    }

    public void setMesazhi(String mesazhi) {
        this.mesazhi = mesazhi;
    }

    public void setPerdoruesi(User perdoruesi) {
        this.perdoruesi = perdoruesi;
    }

    public void setTipi(String tipi) {
        this.tipi = tipi;
    }
}
