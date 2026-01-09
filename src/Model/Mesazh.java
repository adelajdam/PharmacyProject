package Model;

import java.time.LocalDateTime;

public class Mesazh {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long receteId;   // mesazhi lidhet me receten
    private String permbajtja;
    private String fotoPath; // nëse mesazhi ka foto
    private LocalDateTime dataDergimit;

    public Mesazh() {}

    public Mesazh(Long senderId, Long receiverId, Long receteId,
                  String permbajtja, String fotoPath) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.receteId = receteId;
        this.permbajtja = permbajtja;
        this.fotoPath = fotoPath;
        this.dataDergimit = LocalDateTime.now();
    }

    public Mesazh(Long senderId,
                  Long receiverId,
                  String permbajtja) {

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.permbajtja = permbajtja;
        this.dataDergimit = LocalDateTime.now();
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Long getReceteId() {
        return receteId;
    }

    public String getPermbajtja() {
        return permbajtja;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public LocalDateTime getDataDergimit() {
        return dataDergimit;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setReceteId(Long receteId) {
        this.receteId = receteId;
    }

    public void setPermbajtja(String permbajtja) {
        this.permbajtja = permbajtja;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public void setDataDergimit(LocalDateTime dataDergimit) {
        this.dataDergimit = dataDergimit;
    }
}

