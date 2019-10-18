package co.id.gmedia.yia.Model;

import java.util.ArrayList;
import java.util.List;

public class DonaturModel {
    private String id;
    private String id_donatur;
    private String nama;
    private String alamat;
    private String kontak;
    private String kaleng;
    private String latitude;
    private String lognitude;
    private String rk;

    private List<String> listUrlPhoto = new ArrayList<>();
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

    public DonaturModel(String id,  String id_donatur, String nama, String alamat, String kontak, String kaleng, boolean dikunjungi){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.kaleng = kaleng;
        this.dikunjungi = dikunjungi;
    }

    public DonaturModel(String id,  String id_donatur, String nama, String alamat, String kontak,
                        String kaleng, String latitude, String lognitude, boolean dikunjungi){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.kaleng = kaleng;
        this.latitude = latitude;
        this.lognitude = lognitude;
        this.dikunjungi = dikunjungi;
    }

    public DonaturModel(String id,  String id_donatur, String nama, String alamat, String kontak,
                        String kaleng, String latitude, String lognitude, boolean dikunjungi, List<String> listUrlPhoto){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.kaleng = kaleng;
        this.latitude = latitude;
        this.lognitude = lognitude;
        this.dikunjungi = dikunjungi;
        this.listUrlPhoto = listUrlPhoto;
    }

    public DonaturModel(String id,  String id_donatur, String nama, String alamat, String kontak, String latitude, String longitude, boolean dikunjungi){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.latitude = latitude;
        this.lognitude = longitude;
        this.dikunjungi = dikunjungi;
    }

    public DonaturModel(String id, String id_donatur, String nama, String alamat, String kontak, String latitude, String longitude, String kaleng, String rk, boolean dikunjungi){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.latitude = latitude;
        this.lognitude = longitude;
        this.kaleng = kaleng;
        this.rk = rk;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLognitude() {
        return lognitude;
    }

    public void setLognitude(String lognitude) {
        this.lognitude = lognitude;
    }

    public String getKaleng() {
        return kaleng;
    }

    public void setKaleng(String kaleng) {
        this.kaleng = kaleng;
    }

    public String getRk() {
        return rk;
    }

    public void setRk(String rk) {
        this.rk = rk;
    }

    public List<String> getListUrlPhoto() {
        return listUrlPhoto;
    }
}
