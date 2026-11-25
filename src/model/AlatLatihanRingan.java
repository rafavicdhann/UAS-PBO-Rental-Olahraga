package model;

/**
 * Subclass untuk kategori peralatan latihan ringan (misal: Matras Yoga, Dumbbell).
 * Biaya dihitung murni berdasarkan tarif harian dikalikan durasi hari.
 */
public class AlatLatihanRingan extends Barang {

    /**
     * Konstruktor AlatLatihanRingan.
     *
     * @param id          Kode unik barang
     * @param nama        Nama alat latihan
     * @param tarifHarian Tarif sewa per hari
     */
    public AlatLatihanRingan(String id, String nama, double tarifHarian) {
        super(id, nama, tarifHarian);
    }

    @Override
    public double hitungBiaya(int hari) {
        return getTarifHarian() * hari;
    }

    @Override
    public String getKategori() {
        return "Alat Latihan Ringan";
    }
}