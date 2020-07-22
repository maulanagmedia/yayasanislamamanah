package com.gmedia.modul.bluetoothprinter.Model;

import java.util.Date;
import java.util.List;

public class Transaksi {
    private String nama;
    private String alamat;
    private Double nominal;
    private Date tglPengambilan;
    private String sales;

    public Transaksi(String nama, String alamat, Double nominal, Date tglPengambilan, String sales){
        this.nama = nama;
        this.alamat = alamat;
        this.nominal = nominal;
        this.tglPengambilan = tglPengambilan;
        this.sales = sales;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Double getNominal() {
        return nominal;
    }

    public void setNominal(Double nominal) {
        this.nominal = nominal;
    }

    public Date getTglPengambilan() {
        return tglPengambilan;
    }

    public void setTglPengambilan(Date tglPengambilan) {
        this.tglPengambilan = tglPengambilan;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }
}
