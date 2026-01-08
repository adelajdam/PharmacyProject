package Model;

import java.time.LocalDateTime;

public class Mesazh {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String permbajtja;
    private LocalDateTime dataDergimit;

    public Mesazh() {}

    public Mesazh(Long senderId, Long receiverId, String permbajtja) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.permbajtja = permbajtja;
        this.dataDergimit = LocalDateTime.now();
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getPermbajtja() { return permbajtja; }
    public void setPermbajtja(String permbajtja) { this.permbajtja = permbajtja; }

    public LocalDateTime getDataDergimit() { return dataDergimit; }
    public void setDataDergimit(LocalDateTime dataDergimit) { this.dataDergimit = dataDergimit; }
}

