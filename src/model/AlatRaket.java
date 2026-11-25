package model;

/**
 * Subclass untuk kategori peralatan raket (misal: Raket Padel, Raket Badminton).
 * Memiliki tambahan biaya perawatan senar/grip per transaksi sewa.
 */
public class AlatRaket extends Barang {
    private double biayaPerawatanSenar;

    /**
     * Konstruktor AlatRaket.
     *
     * @param id                  Kode unik barang
     * @param nama                Nama raket
     * @param tarifHarian         Tarif sewa per hari
     * @param biayaPerawatanSenar Biaya tambahan perawatan senar/grip
     */
    public AlatRaket(String id, String nama, double tarifHarian, double biayaPerawatanSenar) {
        super(id, nama, tarifHarian);
        this.biayaPerawatanSenar = biayaPerawatanSenar;
    }

    public double getBiayaPerawatanSenar() {
        return biayaPerawatanSenar;
    }

    public void setBiayaPerawatanSenar(double biayaPerawatanSenar) {
        this.biayaPerawatanSenar = biayaPerawatanSenar;
    }

    @Override
    public double hitungBiaya(int hari) {
        return (getTarifHarian() * hari) + biayaPerawatanSenar;
    }

    @Override
    public String getKategori() {
        return "Alat Raket (+Perawatan Senar)";
    }
}