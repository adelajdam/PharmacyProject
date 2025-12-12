package Model;

public class ShportaProdukt {
    private Long shportaId;
    private Long produktId;
    private int quantity;

    public ShportaProdukt() {}

    public ShportaProdukt(Long shportaId, Long produktId, int quantity) {
        this.shportaId = shportaId;
        this.produktId = produktId;
        this.quantity = quantity;
    }

    public Long getShportaId() { return shportaId; }
    public Long getProduktId() { return produktId; }
    public int getQuantity() { return quantity; }

    public void setShportaId(Long shportaId) { this.shportaId = shportaId; }
    public void setProduktId(Long produktId) { this.produktId = produktId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

