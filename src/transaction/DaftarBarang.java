package transaction;

import exception.BarangTidakTersediaException;
import java.util.ArrayList;
import java.util.List;
import model.AlatKardio;
import model.AlatLatihanRingan;
import model.AlatRaket;
import model.Barang;

public class DaftarBarang {

    private final ArrayList<Barang> daftar = new ArrayList<>();

    /** Mengisi katalog dengan 6 data awal dari tiga kategori alat olahraga. */
    public DaftarBarang() {
        daftar.add(new AlatLatihanRingan("LR-001", "Matras Yoga Premium", 25000));
        daftar.add(new AlatLatihanRingan("LR-002", "Set Dumbbell 5kg", 35000));
        daftar.add(new AlatRaket("RK-001", "Raket Badminton Yonex", 45000, 15000));
        daftar.add(new AlatRaket("RK-002", "Raket Padel Carbon", 75000, 20000));
        daftar.add(new AlatKardio("KD-001", "Sepeda Statis Spin", 90000, 10000));
        daftar.add(new AlatKardio("KD-002", "Treadmill Lipat Pro", 140000, 20000));
    }

    /** Menambahkan barang baru ke katalog (kompatibel dengan {@link model.Admin}). */
    public void tambahBarang(Barang barang) {
        if (barang == null || barang.getId() == null || barang.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Data barang atau kode tidak boleh kosong.");
        }
        if (cariBarang(barang.getId()) != null) {
            throw new IllegalArgumentException("Kode barang '" + barang.getId() + "' sudah terdaftar.");
        }
        barang.setId(barang.getId().trim().toUpperCase());
        daftar.add(barang);
    }

    /** Mencari barang berdasarkan kode secara case-insensitive. */
    public Barang cariBarang(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        String target = id.trim();
        for (Barang b : daftar) {
            if (b.getId().equalsIgnoreCase(target)) {
                return b;
            }
        }
        return null;
    }

    /** Memastikan barang ada di katalog dan berstatus tersedia. */
    public Barang cekTersedia(String id) throws BarangTidakTersediaException {
        Barang barang = cariBarang(id);
        if (barang == null) {
            throw new BarangTidakTersediaException("Barang dengan kode '" + id + "' tidak ditemukan.");
        }
        if (!barang.isTersedia()) {
            throw new BarangTidakTersediaException("Barang '" + barang.getNama() + "' sedang disewa.");
        }
        return barang;
    }

    /** Menghapus barang dari katalog berdasarkan kode. */
    public void hapusBarang(String id) throws BarangTidakTersediaException {
        Barang barang = cariBarang(id);
        if (barang == null) {
            throw new BarangTidakTersediaException("Barang dengan kode '" + id + "' tidak ditemukan.");
        }
        daftar.remove(barang);
    }

    /** Menampilkan seluruh barang ke konsol memakai {@link Barang#toString()}. */
    public void tampilkanSemuaBarang() {
        if (daftar.isEmpty()) {
            System.out.println("Katalog barang masih kosong.");
            return;
        }
        for (Barang b : daftar) {
            System.out.println(b);
        }
    }

    public List<Barang> getDaftarBarang() {
        return new ArrayList<>(daftar);
    }
}
