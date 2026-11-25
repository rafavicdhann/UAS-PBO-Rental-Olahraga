package model;

import java.util.ArrayList;
import transaction.DaftarBarang;
import transaction.KeranjangSewa;
import transaction.Transaksi;

/**
 * Kelas yang merepresentasikan akun Penyewa peralatan olahraga.
 * Memiliki keranjang sewa aktif dan daftar riwayat transaksi.
 */
public class Penyewa extends Akun {
    private KeranjangSewa keranjang;
    private ArrayList<Transaksi> riwayat;

    /**
     * Konstruktor Penyewa.
     *
     * @param username Username penyewa
     * @param password Password penyewa
     */
    public Penyewa(String username, String password) {
        super(username, password);
        this.keranjang = new KeranjangSewa();
        this.riwayat = new ArrayList<>();
    }

    /**
     * Menampilkan seluruh daftar barang yang terdaftar pada sistem.
     *
     * @param daftar Objek DaftarBarang sistem
     */
    public void lihatBarang(DaftarBarang daftar) {
        daftar.tampilkanSemuaBarang();
    }

    /**
     * Menambahkan transaksi yang telah disetujui ke dalam riwayat sewa penyewa.
     *
     * @param transaksi Objek Transaksi yang masuk riwayat
     */
    public void tambahRiwayat(Transaksi transaksi) {
        riwayat.add(transaksi);
    }

    /**
     * Menampilkan seluruh riwayat transaksi penyewa.
     */
    public void lihatRiwayat() {
        if (riwayat.isEmpty()) {
            System.out.println("Belum ada riwayat transaksi sewa.");
            return;
        }
        System.out.println("=== RIWAYAT TRANSAKSI SEWA (" + getUsername() + ") ===");
        for (Transaksi t : riwayat) {
            System.out.println(t);
        }
    }

    public KeranjangSewa getKeranjang() {
        return keranjang;
    }

    public ArrayList<Transaksi> getRiwayat() {
        return riwayat;
    }
}