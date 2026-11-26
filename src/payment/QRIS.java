package payment;

import exception.InputTidakValidException;

/**
 * Metode pembayaran melalui QRIS (simulasi).
 * Mengimplementasikan interface {@link Pembayaran}.
 */
public class QRIS implements Pembayaran {
    private final String namaMerchant;

    /**
     * Membuat metode QRIS dengan nama merchant bawaan rental.
     */
    public QRIS() {
        this("Sport Rental");
    }

    /**
     * Membuat metode QRIS dengan nama merchant tertentu.
     *
     * @param namaMerchant Nama merchant penerima pembayaran
     */
    public QRIS(String namaMerchant) {
        this.namaMerchant = namaMerchant;
    }

    /**
     * Mensimulasikan pembayaran melalui pemindaian QRIS dan menampilkan
     * nama merchant serta jumlah yang dibayarkan.
     *
     * @param jumlah Nominal pembayaran dalam Rupiah
     * @throws InputTidakValidException jika jumlah nol atau negatif
     */
    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        validasiJumlah(jumlah);
        System.out.println("=== PEMBAYARAN: " + getNamaMetode().toUpperCase() + " ===");
        System.out.println("Merchant      : " + namaMerchant);
        System.out.println("Jumlah        : " + Pembayaran.formatRupiah(jumlah));
        System.out.println("Status        : Kode QR berhasil dipindai, pembayaran sukses (simulasi).");
    }

    /**
     * Mendapatkan nama metode pembayaran.
     *
     * @return "QRIS"
     */
    @Override
    public String getNamaMetode() {
        return "QRIS";
    }

    /**
     * Mendapatkan nama merchant.
     *
     * @return Nama merchant
     */
    public String getNamaMerchant() {
        return namaMerchant;
    }
}
