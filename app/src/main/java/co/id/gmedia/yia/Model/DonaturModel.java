package co.id.gmedia.yia.Model;

public class DonaturModel {
    private String id;
    private String id_donatur;
    private String nama;
    private String alamat;
    private String kontak;

    private boolean dikunjungi = false;

    public DonaturModel(String id, String id_donatur, String nama, String alamat, String kontak){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
    }

    public DonaturModel(String id,  String id_donatur, String nama, String alamat, String kontak, boolean dikunjungi){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.dikunjungi = dikunjungi;
    }

    public String getId() {
        return id;
    }

    public String getId_donatur() {
        return id_donatur;
    }

    public void setDikunjungi(boolean dikunjungi) {
        this.dikunjungi = dikunjungi;
    }

    public boolean isDikunjungi() {
        return dikunjungi;
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
