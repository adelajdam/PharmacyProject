package Model;

public class ShportaProdukt {
    private long shportaId;
    private Produkt produkt; // ky duhet të ekzistojë
    private int quantity;

    public ShportaProdukt(Long shportaId, Produkt produkt, int quantity) {
        this.shportaId = shportaId;
        this.produkt = produkt;
        this.quantity = quantity;
    }

    public ShportaProdukt() {
    }

    // getter & setter
    public long getShportaId() { return shportaId; }
    public void setShportaId(long shportaId) { this.shportaId = shportaId; }

    public Produkt getProdukt() { return produkt; }
    public void setProdukt(Produkt produkt) { this.produkt = produkt; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
