package model;

/**
 * Kelas abstrak yang merepresentasikan entitas dasar peralatan olahraga yang disewakan.
 * Menerapkan konsep Abstraction dan Encapsulation.
 */
public abstract class Barang {
    private String id;
    private String nama;
    private double tarifHarian;
    private boolean tersedia;

    /**
     * Konstruktor untuk membuat objek Barang baru.
     *
     * @param id          Kode unik barang
     * @param nama        Nama peralatan olahraga
     * @param tarifHarian Harga sewa dasar per hari
     */
    public Barang(String id, String nama, double tarifHarian) {
        this.id = id;
        this.nama = nama;
        this.tarifHarian = tarifHarian;
        this.tersedia = true;
    }

    /**
     * Menghitung total biaya sewa berdasarkan jumlah hari.
     * Method abstrak yang wajib di-override oleh setiap kategori barang.
     *
     * @param hari Durasi penyewaan dalam hari
     * @return Total biaya sewa dalam Rupiah
     */
    public abstract double hitungBiaya(int hari);

    /**
     * Mendapatkan informasi kategori spesifik barang.
     *
     * @return Nama kategori barang
     */
    public abstract String getKategori();

    // Getter dan Setter (Encapsulation)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getTarifHarian() {
        return tarifHarian;
    }

    public void setTarifHarian(double tarifHarian) {
        this.tarifHarian = tarifHarian;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    @Override
    public String toString() {
        String status = tersedia ? "Tersedia" : "Sedang Disewa";
        return String.format("[%s] %s (%s) - Rp%.0f/hari | Status: %s",
                id, nama, getKategori(), tarifHarian, status);
    }
}