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
    private String rk, rt, rw;
    private String idKota, idKecamatan, idKelurahan, kota, kecamatan, kelurahan;
    private String nominal, jenisDonatur, tanggal, keterangan, wa;

    private List<String> listUrlPhoto = new ArrayList<>();
    private boolean dikunjungi = false;

    public DonaturModel(){

    }

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

    public DonaturModel(String id,  String id_donatur, String nama, String alamat, String kontak, String wa, boolean dikunjungi){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.wa = wa;
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

    public DonaturModel(String id, String id_donatur, String nama, String alamat, String kontak, String latitude, String longitude, String kaleng, String rk, String idKota, String idKecamatan, String idKelurahan, String kota, String kecamatan, String kelurahan, boolean dikunjungi){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.latitude = latitude;
        this.lognitude = longitude;
        this.kaleng = kaleng;
        this.rk = rk;
        this.idKota = idKota;
        this.idKecamatan = idKecamatan;
        this.idKelurahan = idKelurahan;
        this.kota = kota;
        this.kecamatan = kecamatan;
        this.kelurahan = kelurahan;
        this.dikunjungi = dikunjungi;
    }

    public DonaturModel(String id, String id_donatur, String nama, String alamat, String kontak, String latitude, String longitude, String kaleng, String rk, String idKota, String idKecamatan, String idKelurahan, String kota, String kecamatan, String kelurahan, boolean dikunjungi,String keterangan){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.latitude = latitude;
        this.lognitude = longitude;
        this.kaleng = kaleng;
        this.rk = rk;
        this.idKota = idKota;
        this.idKecamatan = idKecamatan;
        this.idKelurahan = idKelurahan;
        this.kota = kota;
        this.kecamatan = kecamatan;
        this.kelurahan = kelurahan;
        this.dikunjungi = dikunjungi;
        this.keterangan = keterangan;
    }

    public DonaturModel(String id, String id_donatur, String nama, String alamat, String kontak, String latitude, String longitude, String kaleng, String rk, String idKota, String idKecamatan, String idKelurahan, String kota, String kecamatan, String kelurahan, boolean dikunjungi,String keterangan,List<String> listPhoto){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.latitude = latitude;
        this.lognitude = longitude;
        this.kaleng = kaleng;
        this.rk = rk;
        this.idKota = idKota;
        this.idKecamatan = idKecamatan;
        this.idKelurahan = idKelurahan;
        this.kota = kota;
        this.kecamatan = kecamatan;
        this.kelurahan = kelurahan;
        this.dikunjungi = dikunjungi;
        this.keterangan = keterangan;
        this.listUrlPhoto = listPhoto;
    }

    public DonaturModel(String id, String id_donatur, String nama, String alamat, String rt, String rw, String kontak,String kaleng,  String latitude, String longitude, boolean dikunjungi,String keterangan,List<String> listPhoto){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.rt = rt;
        this.rw = rw;
        this.kontak = kontak;
        this.latitude = latitude;
        this.lognitude = longitude;
        this.kaleng = kaleng;
        this.dikunjungi = dikunjungi;
        this.keterangan = keterangan;
        this.listUrlPhoto = listPhoto;
    }

    public DonaturModel(String id, String id_donatur, String nama, String alamat, String rt, String rw, String kontak,String wa,String kaleng,  String latitude, String longitude, boolean dikunjungi,String keterangan,List<String> listPhoto){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.rt = rt;
        this.rw = rw;
        this.kontak = kontak;
        this.latitude = latitude;
        this.lognitude = longitude;
        this.kaleng = kaleng;
        this.dikunjungi = dikunjungi;
        this.keterangan = keterangan;
        this.listUrlPhoto = listPhoto;
        this.wa = wa;
    }

    public DonaturModel(String id, String id_donatur, String nama, String alamat, String kontak, String latitude, String longitude, String kaleng, String rk, String idKota, String idKecamatan, String idKelurahan, String kota, String kecamatan, String kelurahan, boolean dikunjungi,String keterangan,List<String> listPhoto, String rt,String rw, String wa){
        this.id = id;
        this.id_donatur = id_donatur;
        this.nama = nama;
        this.alamat = alamat;
        this.kontak = kontak;
        this.latitude = latitude;
        this.lognitude = longitude;
        this.kaleng = kaleng;
        this.rk = rk;
        this.idKota = idKota;
        this.idKecamatan = idKecamatan;
        this.idKelurahan = idKelurahan;
        this.kota = kota;
        this.kecamatan = kecamatan;
        this.kelurahan = kelurahan;
        this.dikunjungi = dikunjungi;
        this.keterangan = keterangan;
        this.listUrlPhoto = listPhoto;
        this.rt =rt;
        this.rw = rw;
        this.wa =wa;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId_donatur(String id_donatur) {
        this.id_donatur = id_donatur;
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

    public void setNama(String nama){
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setAlamat(String alamat){
        this.alamat = alamat;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setKontak(String kontak){
        this.kontak= kontak;
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

    public String getIdKota() {
        return idKota;
    }

    public void setIdKota(String idKota) {
        this.idKota = idKota;
    }

    public String getIdKecamatan() {
        return idKecamatan;
    }

    public void setIdKecamatan(String idKecamatan) {
        this.idKecamatan = idKecamatan;
    }

    public String getIdKelurahan() {
        return idKelurahan;
    }

    public void setIdKelurahan(String idKelurahan) {
        this.idKelurahan = idKelurahan;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getJenisDonatur() {
        return jenisDonatur;
    }

    public void setJenisDonatur(String jenisDonatur) {
        this.jenisDonatur = jenisDonatur;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getRw() {
        return rw;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public String getWa() {
        return wa;
    }

    public void setWa(String wa) {
        this.wa = wa;
    }
}
