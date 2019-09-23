package co.id.gmedia.yia.Model;

public class DonaturModel {
    private String nama;
    private String alamat;
    private String kontak;

    public DonaturModel(String nama, String alamat, String kontak){
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getKontak() {
        return kontak;
    }
}
