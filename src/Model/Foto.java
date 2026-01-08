package Model;

public class Foto {

    private Long id;
    private Long receteId;   // lidhja me receten
    private String fotoPath; // path ose URL e fotos

    public Foto() {}

    public Foto(Long receteId, String fotoPath) {
        this.receteId = receteId;
        this.fotoPath = fotoPath;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReceteId() { return receteId; }
    public void setReceteId(Long receteId) { this.receteId = receteId; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }
}
