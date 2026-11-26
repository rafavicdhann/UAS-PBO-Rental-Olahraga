package payment;

import exception.InputTidakValidException;

/**
 * Metode pembayaran melalui transfer bank (simulasi).
 * Mengimplementasikan interface {@link Pembayaran}.
 */
public class TransferBank implements Pembayaran {
    private final String namaBank;
    private final String nomorRekening;

    /**
     * Membuat metode transfer bank dengan rekening tujuan bawaan rental.
     */
    public TransferBank() {
        this("Bank Sport Rental", "1234-5678-9012");
    }

    /**
     * Membuat metode transfer bank dengan rekening tujuan tertentu.
     *
     * @param namaBank      Nama bank tujuan
     * @param nomorRekening Nomor rekening tujuan
     */
    public TransferBank(String namaBank, String nomorRekening) {
        this.namaBank = namaBank;
        this.nomorRekening = nomorRekening;
    }

    /**
     * Mensimulasikan pembayaran melalui transfer bank dan menampilkan
     * informasi rekening tujuan serta jumlah yang dibayarkan.
     *
     * @param jumlah Nominal pembayaran dalam Rupiah
     * @throws InputTidakValidException jika jumlah nol atau negatif
     */
    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        validasiJumlah(jumlah);
        System.out.println("=== PEMBAYARAN: " + getNamaMetode().toUpperCase() + " ===");
        System.out.println("Bank Tujuan   : " + namaBank);
        System.out.println("No. Rekening  : " + nomorRekening);
        System.out.println("Jumlah        : " + Pembayaran.formatRupiah(jumlah));
        System.out.println("Status        : Transfer berhasil (simulasi).");
    }

    /**
     * Mendapatkan nama metode pembayaran.
     *
     * @return "Transfer Bank"
     */
    @Override
    public String getNamaMetode() {
        return "Transfer Bank";
    }

    /**
     * Mendapatkan nama bank tujuan.
     *
     * @return Nama bank
     */
    public String getNamaBank() {
        return namaBank;
    }

    /**
     * Mendapatkan nomor rekening tujuan.
     *
     * @return Nomor rekening
     */
    public String getNomorRekening() {
        return nomorRekening;
    }
}
