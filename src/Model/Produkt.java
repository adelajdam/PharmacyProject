package Model;

public class Produkt {
    private Long idProd;
    private String emriProd;
    private String pershkrimi;
    private double cmimi;
    private int stok;
    private String kategori;

    public Produkt() {
    }

    public Produkt(Long idProd, String emriProd, String pershkrimi, double cmimi, int stok, String kategori) {
        this.idProd = idProd;
        this.emriProd = emriProd;
        this.pershkrimi = pershkrimi;
        this.cmimi = cmimi;
        this.stok = stok;
        this.kategori = kategori;
    }


    public Long getIdProd() {
        return idProd;
    }

    public String getEmriProd() {
        return emriProd;
    }


    public String getPershkrimi() {
        return pershkrimi;
    }

    public double getCmimi() {
        return cmimi;
    }


    public int getStok() {
        return stok;
    }

    public String getKategori() {
        return kategori;
    }


    public void setIdProd(Long idProd) {
        this.idProd = idProd;
    }


    public void setEmriProd(String emriProd) {
        this.emriProd = emriProd;
    }


    public void setCmimi(double cmimi) {
        this.cmimi = cmimi;
    }


    public void setKategori(String kategori) {
        this.kategori = kategori;
    }


    public void setPershkrimi(String pershkrimi) {
        this.pershkrimi = pershkrimi;
    }


    public void setStok(int stok) {
        this.stok = stok;
    }
}
