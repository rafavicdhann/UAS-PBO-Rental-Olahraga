package model;

/**
 * Subclass untuk kategori peralatan kardio berat (misal: Sepeda Statis, Treadmill Lipat).
 * Memiliki tambahan biaya asuransi mesin per hari penyewaan.
 */
public class AlatKardio extends Barang {
    private double biayaAsuransiHarian;

    /**
     * Konstruktor AlatKardio.
     *
     * @param id                  Kode unik barang
     * @param nama                Nama alat kardio
     * @param tarifHarian         Tarif sewa per hari
     * @param biayaAsuransiHarian Biaya asuransi mesin per hari
     */
    public AlatKardio(String id, String nama, double tarifHarian, double biayaAsuransiHarian) {
        super(id, nama, tarifHarian);
        this.biayaAsuransiHarian = biayaAsuransiHarian;
    }

    public double getBiayaAsuransiHarian() {
        return biayaAsuransiHarian;
    }

    public void setBiayaAsuransiHarian(double biayaAsuransiHarian) {
        this.biayaAsuransiHarian = biayaAsuransiHarian;
    }

    @Override
    public double hitungBiaya(int hari) {
        return (getTarifHarian() + biayaAsuransiHarian) * hari;
    }

    @Override
    public String getKategori() {
        return "Alat Kardio (+Asuransi Mesin)";
    }
}