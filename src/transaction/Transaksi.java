package transaction;

import exception.InputTidakValidException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import model.Barang;
import model.Penyewa;
import payment.Pembayaran;
import transaction.KeranjangSewa.ItemKeranjang;

/** Mencatat transaksi sewa, jatuh tempo, pengembalian, dan denda keterlambatan. */
public class Transaksi {

    public static final double TARIF_DENDA_PER_HARI = 15000.0;
    public static final String STATUS_AKTIF = "AKTIF";
    public static final String STATUS_SELESAI = "SELESAI";

    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static int penghitungId = 1;

    private final String id;
    private final Penyewa penyewa;
    private final ArrayList<ItemKeranjang> daftarItem;
    private final double totalBiayaAwal;
    private final Pembayaran metodePembayaran;
    private final LocalDate tanggalSewa;
    private final LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali;
    private String status;
    private double totalDenda;

    /** Membuat transaksi baru dengan ID otomatis, tanggal sewa hari ini, dan jatuh tempo terlama. */
    public Transaksi(Penyewa penyewa, ArrayList<ItemKeranjang> items, Pembayaran metodePembayaran)
            throws InputTidakValidException {
        if (penyewa == null || items == null || items.isEmpty() || metodePembayaran == null) {
            throw new InputTidakValidException("Data transaksi tidak lengkap.");
        }
        this.id = String.format("TRX-%03d", penghitungId++);
        this.penyewa = penyewa;
        this.daftarItem = new ArrayList<>(items);
        this.metodePembayaran = metodePembayaran;

        double total = 0;
        int durasiTerlama = 1;
        for (ItemKeranjang item : items) {
            total += item.hitungSubtotal();
            if (item.getDurasiHari() > durasiTerlama) {
                durasiTerlama = item.getDurasiHari();
            }
        }
        this.totalBiayaAwal = total;
        this.tanggalSewa = LocalDate.now();
        this.tanggalJatuhTempo = tanggalSewa.plusDays(durasiTerlama);
        this.status = STATUS_AKTIF;
    }

    /** Menghitung jumlah hari keterlambatan melewati tanggal jatuh tempo. */
    public long hitungHariTerlambat(LocalDate tanggalPengembalian) {
        if (tanggalPengembalian == null || !tanggalPengembalian.isAfter(tanggalJatuhTempo)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(tanggalJatuhTempo, tanggalPengembalian);
    }

    /** Memproses pengembalian barang, memulihkan status barang, dan menghitung denda. */
    public void prosesPengembalian(LocalDate tanggalPengembalian) throws InputTidakValidException {
        if (isSelesai()) {
            throw new InputTidakValidException("Transaksi " + id + " sudah selesai.");
        }
        if (tanggalPengembalian == null || tanggalPengembalian.isBefore(tanggalSewa)) {
            throw new InputTidakValidException(
                    "Tanggal kembali tidak boleh sebelum " + tanggalSewa.format(FORMAT_TANGGAL) + ".");
        }
        for (ItemKeranjang item : daftarItem) {
            item.getBarang().setTersedia(true);
        }
        this.tanggalKembali = tanggalPengembalian;
        this.totalDenda = hitungHariTerlambat(tanggalPengembalian) * TARIF_DENDA_PER_HARI;
        this.status = STATUS_SELESAI;
    }

    /** Memperbarui status transaksi (dipakai oleh {@link model.Admin}). */
    public void ubahStatus(String statusBaru) {
        if (statusBaru != null && !statusBaru.trim().isEmpty()) {
            this.status = statusBaru.trim();
        }
    }

    /** Mengambil daftar {@link Barang} pada transaksi (dipakai oleh {@link model.Admin}). */
    public ArrayList<Barang> getBarang() {
        ArrayList<Barang> hasil = new ArrayList<>();
        for (ItemKeranjang item : daftarItem) {
            hasil.add(item.getBarang());
        }
        return hasil;
    }

    public String getId() { return id; }
    public Penyewa getPenyewa() { return penyewa; }
    public ArrayList<ItemKeranjang> getDaftarItem() { return new ArrayList<>(daftarItem); }
    public double getTotalBiayaAwal() { return totalBiayaAwal; }
    public Pembayaran getMetodePembayaran() { return metodePembayaran; }
    public LocalDate getTanggalSewa() { return tanggalSewa; }
    public LocalDate getTanggalJatuhTempo() { return tanggalJatuhTempo; }
    public LocalDate getTanggalKembali() { return tanggalKembali; }
    public String getStatus() { return status; }
    public double getTotalDenda() { return totalDenda; }
    public double getTotalAkhir() { return totalBiayaAwal + totalDenda; }

    public boolean isSelesai() {
        return tanggalKembali != null
                || STATUS_SELESAI.equalsIgnoreCase(status)
                || "SELESAI / DIKEMBALIKAN".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        String kembali = tanggalKembali == null ? "-" : tanggalKembali.format(FORMAT_TANGGAL);
        return String.format("[%s] %s | Sewa: %s | Tempo: %s | Kembali: %s | %s | Total: %s | Denda: %s",
                id, penyewa.getUsername(), tanggalSewa.format(FORMAT_TANGGAL),
                tanggalJatuhTempo.format(FORMAT_TANGGAL), kembali, status,
                Pembayaran.formatRupiah(totalBiayaAwal), Pembayaran.formatRupiah(totalDenda));
    }
}
