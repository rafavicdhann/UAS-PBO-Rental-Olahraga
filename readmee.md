# Sistem Penyewaan (Rental) Peralatan Olahraga

Aplikasi berbasis *Command Line Interface* (CLI) yang dikembangkan menggunakan bahasa pemrograman Java untuk mengelola sistem penyewaan peralatan olahraga. Proyek ini dirancang dengan menerapkan prinsip-prinsip **Pemrograman Berorientasi Objek (PBO)** seperti *Encapsulation*, *Inheritance*, *Polymorphism*, *Abstraction*, *Interface*, *Collections*, dan *Exception Handling*.

## Fitur Utama

### 1. Autentikasi Pengguna
- Sistem login yang membedakan hak akses antara **Admin** dan **Penyewa**.

### 2. Fitur Admin
- **Manajemen Inventaris Barang:** Menambah, mengubah, dan menghapus data peralatan olahraga berdasarkan 3 kategori (`AlatLatihanRingan`, `AlatRaket`, dan `AlatKardio`) dengan perhitungan tarif yang berbeda (*Polymorphism*).
- **Persetujuan Transaksi:** Melihat daftar pengajuan sewa dan menyetujui transaksi penyewa sehingga status ketersediaan barang otomatis diperbarui.
- **Konfirmasi Pengembalian & Denda:** Memproses pengembalian barang sewaan dan menghitung denda keterlambatan secara otomatis berdasarkan selisih hari melewati tanggal jatuh tempo.

### 3. Fitur Penyewa
- **Katalog & Keranjang Sewa:** Melihat daftar peralatan olahraga yang tersedia dan menambahkannya ke dalam `KeranjangSewa`.
- **Checkout & Pembayaran:** Melakukan transaksi sewa dengan menentukan durasi hari serta memilih salah satu dari 3 metode pembayaran (`TransferBank`, `QRIS`, atau `Tunai`).
- **Invoice & Riwayat Sewa:** Menampilkan rincian nota/invoice transaksi dan melihat riwayat penyewaan.

## Struktur Direktori Proyek

```text
UAS-PBO-Rental-Olahraga/
├── README.md
└── src/
    ├── app/
    │   └── Main.java
    ├── exception/
    │   ├── BarangTidakTersediaException.java
    │   └── InputTidakValidException.java
    ├── model/
    │   ├── Akun.java
    │   ├── Admin.java
    │   ├── Penyewa.java
    │   ├── Barang.java
    │   ├── AlatLatihanRingan.java
    │   ├── AlatRaket.java
    │   └── AlatKardio.java
    ├── payment/
    │   ├── Pembayaran.java
    │   ├── QRIS.java
    │   ├── TransferBank.java
    │   └── Tunai.java
    └── transaction/
        ├── DaftarBarang.java
        ├── KeranjangSewa.java
        ├── Transaksi.java
        └── Invoice.java
```

## Cara Menjalankan Program

1. Buka terminal atau *Command Prompt* pada direktori root proyek (`UAS-PBO-Rental-Olahraga`).
2. Kompilasi seluruh file Java ke folder `bin`:
   ```bash
   javac -d bin src/exception/*.java src/model/*.java src/payment/*.java src/transaction/*.java src/app/*.java
   ```
3. Jalankan kelas utama `app.Main`:
   ```bash
   java -cp bin app.Main
   ```

## Daftar Anggota Kelompok

| No | Nama Lengkap | NPM | Kontribusi Utama |
| :- | :--- | :--- | :--- |
| 1 | **M. Rafa Vicdhan Aufa** | **250810701100018** | Package `model` (`Barang`, `Akun`, dan subclass) & `README.md` |
| 2 | **Fatin Fadel Al-Annari** | **250810701100061** | Package `payment`, package `exception`, & Class Diagram |
| 3 | **Thahiratul farhati** | **250810701100010** | Package `transaction` (`DaftarBarang`, `KeranjangSewa`, `Transaksi`, `Invoice`) & `app.Main` |