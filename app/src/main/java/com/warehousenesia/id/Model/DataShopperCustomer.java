package com.warehousenesia.id.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class DataShopperCustomer {

    @SerializedName("id_transaksi")
    @Expose
    String id_transaksi;

    @SerializedName("id_customer")
    @Expose
    String id_customer;

    @SerializedName("waktu")
    @Expose
    String waktu;

    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("nama_lengkap")
    @Expose
    String nama_lengkap;

    @SerializedName("nama_produk")
    @Expose
    String nama_produk;

    @SerializedName("gambar")
    @Expose
    String gambar;

    @SerializedName("catatan")
    @Expose
    String catatan;

    @SerializedName("jumlah_barang")
    @Expose
    String jumlah_barang;

    public String getId_transaksi() {
        return id_transaksi;
    }

    public void setId_transaksi(String id_transaksi) {
        this.id_transaksi = id_transaksi;
    }

    public String getId_customer() {
        return id_customer;
    }

    public void setId_customer(String id_customer) {
        this.id_customer = id_customer;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getJumlah_barang() {
        return jumlah_barang;
    }

    public void setJumlah_barang(String jumlah_barang) {
        this.jumlah_barang = jumlah_barang;
    }
}
