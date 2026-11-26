package exception;

/**
 * Exception khusus yang dilempar ketika input dari pengguna tidak valid.
 * Contohnya lama sewa kurang dari satu hari, jumlah pembayaran nol atau
 * negatif, serta pilihan menu yang tidak sesuai.
 * <p>
 * Kelas ini merupakan checked exception sehingga pemanggilnya wajib
 * menangani dengan try-catch atau mendeklarasikan {@code throws}.
 */
public class InputTidakValidException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception dengan pesan kesalahan.
     *
     * @param message Pesan yang menjelaskan input apa yang tidak valid
     */
    public InputTidakValidException(String message) {
        super(message);
    }

    /**
     * Membuat exception dengan pesan kesalahan dan penyebab aslinya.
     *
     * @param message Pesan yang menjelaskan input apa yang tidak valid
     * @param cause   Exception lain yang menjadi penyebab, misalnya NumberFormatException
     */
    public InputTidakValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
