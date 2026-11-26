package transaction;

import exception.BarangTidakTersediaException;
import exception.InputTidakValidException;
import java.util.ArrayList;
import model.Barang;

/** Mengelola daftar item sementara di keranjang sewa {@link model.Penyewa}. */
public class KeranjangSewa {

    /** Satu baris item sewa berisi objek barang dan durasi dalam hari. */
    public static class ItemKeranjang {
        private final Barang barang;
        private final int durasiHari;

        public ItemKeranjang(Barang barang, int durasiHari) throws InputTidakValidException {
            if (barang == null || durasiHari <= 0) {
                throw new InputTidakValidException("Barang harus valid dan durasi minimal 1 hari.");
            }
            this.barang = barang;
            this.durasiHari = durasiHari;
        }

        /** Menghitung subtotal biaya memakai polimorfisme {@link Barang#hitungBiaya(int)}. */
        public double hitungSubtotal() {
            return barang.hitungBiaya(durasiHari);
        }

        public Barang getBarang() {
            return barang;
        }

        public int getDurasiHari() {
            return durasiHari;
        }
    }

    private final ArrayList<ItemKeranjang> items = new ArrayList<>();

    /** Menambahkan barang ke keranjang setelah cek ketersediaan dan duplikasi. */
    public void tambahItem(Barang barang, int durasiHari)
            throws BarangTidakTersediaException, InputTidakValidException {
        if (barang == null || !barang.isTersedia()) {
            throw new BarangTidakTersediaException("Barang tidak tersedia atau sedang disewa.");
        }
        for (ItemKeranjang item : items) {
            if (item.getBarang().getId().equalsIgnoreCase(barang.getId())) {
                throw new BarangTidakTersediaException("'" + barang.getNama() + "' sudah ada di keranjang.");
            }
        }
        items.add(new ItemKeranjang(barang, durasiHari));
    }

    /** Menghapus item pada indeks tertentu (mulai 0). */
    public void hapusItem(int index) throws InputTidakValidException {
        if (index < 0 || index >= items.size()) {
            throw new InputTidakValidException("Pilih item keranjang terlebih dahulu.");
        }
        items.remove(index);
    }

    /** Menghitung total biaya seluruh item di keranjang. */
    public double hitungTotalBiaya() {
        double total = 0;
        for (ItemKeranjang item : items) {
            total += item.hitungSubtotal();
        }
        return total;
    }

    public void kosongkanKeranjang() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public ArrayList<ItemKeranjang> getDaftarItem() {
        return new ArrayList<>(items);
    }
}
