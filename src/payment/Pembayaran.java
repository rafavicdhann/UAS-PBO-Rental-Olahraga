package payment;

import exception.InputTidakValidException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Interface yang menjadi kontrak untuk seluruh metode pembayaran
 * pada sistem rental peralatan olahraga.
 * <p>
 * Setiap metode pembayaran (Transfer Bank, QRIS, Tunai) wajib
 * mengimplementasikan method {@link #prosesPembayaran(double)}
 * dan {@link #getNamaMetode()}.
 */
public interface Pembayaran {

    /**
     * Memproses pembayaran sebesar jumlah tertentu (simulasi melalui CLI).
     *
     * @param jumlah Nominal pembayaran dalam Rupiah, harus lebih dari nol
     * @throws InputTidakValidException jika jumlah bernilai nol, negatif, atau bukan angka valid
     */
    void prosesPembayaran(double jumlah) throws InputTidakValidException;

    /**
     * Mendapatkan nama metode pembayaran, misalnya untuk dicetak pada invoice.
     *
     * @return Nama metode pembayaran
     */
    String getNamaMetode();

    /**
     * Memvalidasi jumlah pembayaran. Jumlah harus lebih dari nol.
     *
     * @param jumlah Nominal pembayaran yang akan diperiksa
     * @throws InputTidakValidException jika jumlah nol, negatif, NaN, atau tak hingga
     */
    default void validasiJumlah(double jumlah) throws InputTidakValidException {
        if (Double.isNaN(jumlah) || Double.isInfinite(jumlah) || jumlah <= 0) {
            throw new InputTidakValidException(
                    "Jumlah pembayaran tidak valid. Jumlah harus lebih dari Rp0.");
        }
    }

    /**
     * Mengubah angka menjadi format Rupiah Indonesia, contoh: {@code Rp150.000}.
     *
     * @param jumlah Nominal dalam Rupiah
     * @return Teks nominal berformat Rupiah
     */
    static String formatRupiah(double jumlah) {
        DecimalFormatSymbols simbol = new DecimalFormatSymbols(Locale.ROOT);
        simbol.setGroupingSeparator('.');
        simbol.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,##0", simbol);
        return "Rp" + format.format(jumlah);
    }
}
