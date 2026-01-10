package Model;

import java.time.LocalDateTime;

public class AuditLog {

    private Long idLog;
    private Long userId;
    private String veprimi;
    private LocalDateTime dataKoha;

    public AuditLog() {}

    public AuditLog(Long idLog, Long userId, String veprimi, LocalDateTime dataKoha) {
        this.idLog = idLog;
        this.userId = userId;
        this.veprimi = veprimi;
        this.dataKoha = dataKoha;
    }

    public Long getIdLog() {
        return idLog;
    }

    public void setIdLog(Long idLog) {
        this.idLog = idLog;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getVeprimi() {
        return veprimi;
    }

    public void setVeprimi(String veprimi) {
        this.veprimi = veprimi;
    }

    public LocalDateTime getDataKoha() {
        return dataKoha;
    }

    public void setDataKoha(LocalDateTime dataKoha) {
        this.dataKoha = dataKoha;
    }
}
