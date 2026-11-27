package model;

import transaction.DaftarBarang;
import transaction.Transaksi;

/**
 * Kelas yang merepresentasikan akun Admin pengelola rental.
 * Memiliki hak akses untuk mengelola barang, menerima transaksi, dan konfirmasi pengembalian.
 */
public class Admin extends Akun {

    /**
     * Konstruktor Admin.
     *
     * @param username Username admin
     * @param password Password admin
     */
    public Admin(String username, String password) {
        super(username, password);
    }

    /**
     * Menambahkan barang baru ke dalam daftar barang rental.
     *
     * @param daftar Objek DaftarBarang sistem
     * @param barang Objek Barang yang akan ditambahkan
     */
    public void tambahBarang(DaftarBarang daftarBarang, Barang barang) {
        daftarBarang.tambahBarang(barang);
    }

    /**
     * Menyetujui transaksi sewa yang diajukan oleh penyewa dan mengubah status barang.
     *
     * @param transaksi Objek Transaksi yang akan diterima
     */
    public void terimaTransaksi(Transaksi transaksi) {
        transaksi.ubahStatus(Transaksi.STATUS_AKTIF);
        for (Barang barang : transaksi.getBarang()) {
            barang.setTersedia(false);
        }
        System.out.println("Transaksi " + transaksi.getId() + " berhasil disetujui.");
    }

    /**
     * Mengonfirmasi pengembalian barang dan mengembalikan status ketersediaan barang.
     *
     * @param transaksi Objek Transaksi yang dikembalikan
     */
    public void konfirmasiKembali(Transaksi transaksi) {
        transaksi.ubahStatus(Transaksi.STATUS_SELESAI);
        for (Barang barang : transaksi.getBarang()) {
            barang.setTersedia(true);
        }
        System.out.println("Pengembalian untuk transaksi " + transaksi.getId() + " telah dikonfirmasi.");
    }
}