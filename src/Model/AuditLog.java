package Model;

import java.time.LocalDateTime;

public class AuditLog {
    private Long idLog;
    private User user;
    private String veprimi;
    private LocalDateTime dataKoha;

    public AuditLog() {}

    public AuditLog(Long idLog, User user, String veprimi, LocalDateTime dataKoha) {
        this.idLog = idLog;
        this.user = user;
        this.veprimi = veprimi;
        this.dataKoha = dataKoha;
    }

    public Long getIdLog() {
        return idLog;
    }

    public User getUser() {
        return user;
    }

    public String getVeprimi() {
        return veprimi;
    }

    public LocalDateTime getDataKoha() {
        return dataKoha;
    }

    public void setIdLog(Long idLog) {
        this.idLog = idLog;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setVeprimi(String veprimi) {
        this.veprimi = veprimi;
    }

    public void setDataKoha(LocalDateTime dataKoha) {
        this.dataKoha = dataKoha;
    }
}

