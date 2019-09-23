package co.id.gmedia.yia.Model;

import java.util.Date;

public class HistoryDonaturModel {
    private DonaturModel donatur;
    private String waktu;
    private String jenis;
    private double jumlah;
    private Date tanggal;
    private boolean tambahan;

    public HistoryDonaturModel(DonaturModel donatur, String waktu,
                               String jenis, double jumlah,
                               Date tanggal, boolean tambahan) {
        this.donatur = donatur;
        this.waktu = waktu;
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.tambahan = tambahan;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public DonaturModel getDonatur() {
        return donatur;
    }

    public double getJumlah() {
        return jumlah;
    }

    public String getJenis() {
        return jenis;
    }

    public String getWaktu() {
        return waktu;
    }

    public boolean isTambahan() {
        return tambahan;
    }
}
