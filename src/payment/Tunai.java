package payment;

import exception.InputTidakValidException;

/**
 * Metode pembayaran secara tunai langsung di kasir (simulasi).
 * Mengimplementasikan interface {@link Pembayaran}.
 */
public class Tunai implements Pembayaran {

    /**
     * Membuat metode pembayaran tunai.
     */
    public Tunai() {
        // Tidak ada atribut yang perlu diinisialisasi.
    }

    /**
     * Mensimulasikan pembayaran tunai di kasir dan menampilkan jumlah
     * uang yang diterima.
     *
     * @param jumlah Nominal pembayaran dalam Rupiah
     * @throws InputTidakValidException jika jumlah nol atau negatif
     */
    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        validasiJumlah(jumlah);
        System.out.println("=== PEMBAYARAN: " + getNamaMetode().toUpperCase() + " ===");
        System.out.println("Jumlah        : " + Pembayaran.formatRupiah(jumlah));
        System.out.println("Status        : Uang tunai diterima oleh kasir (simulasi).");
    }

    /**
     * Mendapatkan nama metode pembayaran.
     *
     * @return "Tunai"
     */
    @Override
    public String getNamaMetode() {
        return "Tunai";
    }
}
