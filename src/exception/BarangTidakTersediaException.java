package exception;

/**
 * Exception khusus yang dilempar ketika penyewa mencoba menyewa barang
 * yang sedang disewa oleh penyewa lain atau tidak tersedia.
 * <p>
 * Kelas ini merupakan checked exception sehingga pemanggilnya wajib
 * menangani dengan try-catch atau mendeklarasikan {@code throws}.
 */
public class BarangTidakTersediaException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception dengan pesan kesalahan.
     *
     * @param message Pesan yang menjelaskan barang mana yang tidak tersedia
     */
    public BarangTidakTersediaException(String message) {
        super(message);
    }

    /**
     * Membuat exception dengan pesan kesalahan dan penyebab aslinya.
     *
     * @param message Pesan yang menjelaskan barang mana yang tidak tersedia
     * @param cause   Exception lain yang menjadi penyebab
     */
    public BarangTidakTersediaException(String message, Throwable cause) {
        super(message, cause);
    }
}
