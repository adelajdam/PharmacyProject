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

    // getters & setters
}
