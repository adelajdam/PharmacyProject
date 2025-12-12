package Model;


public class PorosiProdukt {
    private Long porosiId;
    private Long produktId;
    private int quantity;

    public PorosiProdukt() {}

    public PorosiProdukt(Long porosiId, Long produktId, int quantity) {
        this.porosiId = porosiId;
        this.produktId = produktId;
        this.quantity = quantity;
    }

    public Long getPorosiId() {
        return porosiId;
    }

    public Long getProduktId() {
        return produktId;
    }

    public int getQuantity() {
        return quantity;
    }


    public void setPorosiId(Long porosiId) {
        this.porosiId = porosiId;
    }

    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

