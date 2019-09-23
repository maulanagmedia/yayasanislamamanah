package co.id.gmedia.yia.Model;

import java.util.Date;

public class SurveyRiwayatModel {
    private DonaturModel donatur;
    private String waktu;
    private String jenis;
    private Date tanggal;
    private String lobi_kaleng;

    public SurveyRiwayatModel(DonaturModel donatur, String waktu,
                               String jenis, Date tanggal, String lobi_kaleng) {
        this.donatur = donatur;
        this.waktu = waktu;
        this.jenis = jenis;
        this.tanggal = tanggal;
        this.lobi_kaleng = lobi_kaleng;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public DonaturModel getDonatur() {
        return donatur;
    }

    public String getJenis() {
        return jenis;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getLobi_kaleng() {
        return lobi_kaleng;
    }
}
