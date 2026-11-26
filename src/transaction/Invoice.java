package transaction;

import java.time.format.DateTimeFormatter;
import payment.Pembayaran;
import transaction.KeranjangSewa.ItemKeranjang;

/** Memformat struk atau invoice transaksi penyewaan dan pengembalian. */
public class Invoice {

    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Invoice() {
    }

    /** Membentuk teks invoice lengkap untuk ditampilkan pada dialog GUI. */
    public static String formatInvoice(Transaksi transaksi) {
        if (transaksi == null) {
            return "";
        }
        String garis = "-".repeat(52);
        boolean selesai = transaksi.isSelesai();
        StringBuilder sb = new StringBuilder();
        sb.append("SPORT RENTAL INDONESIA - ")
                .append(selesai ? "STRUK PENGEMBALIAN" : "INVOICE SEWA").append('\n')
                .append(garis).append('\n')
                .append(String.format("%-14s : %s%n", "ID Transaksi", transaksi.getId()))
                .append(String.format("%-14s : %s%n", "Penyewa", transaksi.getPenyewa().getUsername()))
                .append(String.format("%-14s : %s%n", "Status", transaksi.getStatus()))
                .append(String.format("%-14s : %s%n", "Tanggal Sewa", transaksi.getTanggalSewa().format(FORMAT_TANGGAL)))
                .append(String.format("%-14s : %s%n", "Jatuh Tempo", transaksi.getTanggalJatuhTempo().format(FORMAT_TANGGAL)));
        if (selesai && transaksi.getTanggalKembali() != null) {
            sb.append(String.format("%-14s : %s%n", "Tgl Kembali", transaksi.getTanggalKembali().format(FORMAT_TANGGAL)));
        }
        sb.append(String.format("%-14s : %s%n", "Metode Bayar", transaksi.getMetodePembayaran().getNamaMetode()))
                .append(garis).append('\n')
                .append(String.format("%-7s %-22s %6s %14s%n", "Kode", "Nama Barang", "Durasi", "Subtotal"))
                .append(garis).append('\n');

        for (ItemKeranjang item : transaksi.getDaftarItem()) {
            sb.append(String.format("%-7s %-22.22s %4dhr %14s%n", item.getBarang().getId(),
                    item.getBarang().getNama(), item.getDurasiHari(), Pembayaran.formatRupiah(item.hitungSubtotal())));
        }

        sb.append(garis).append('\n')
                .append(String.format("%-14s : %s%n", "Biaya Sewa", Pembayaran.formatRupiah(transaksi.getTotalBiayaAwal())));
        if (selesai) {
            long telat = transaksi.hitungHariTerlambat(transaksi.getTanggalKembali());
            String info = telat > 0
                    ? telat + " hr x " + Pembayaran.formatRupiah(Transaksi.TARIF_DENDA_PER_HARI)
                    : "Tepat Waktu";
            sb.append(String.format("%-14s : %s (%s)%n", "Denda", Pembayaran.formatRupiah(transaksi.getTotalDenda()), info));
        }
        sb.append(String.format("%-14s : %s", "TOTAL", Pembayaran.formatRupiah(transaksi.getTotalAkhir())));
        return sb.toString();
    }
}
