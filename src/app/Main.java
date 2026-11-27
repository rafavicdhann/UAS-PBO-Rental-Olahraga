package app;

import exception.BarangTidakTersediaException;
import exception.InputTidakValidException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import model.Admin;
import model.Akun;
import model.AlatKardio;
import model.AlatLatihanRingan;
import model.AlatRaket;
import model.Barang;
import model.Penyewa;
import payment.Pembayaran;
import payment.QRIS;
import payment.TransferBank;
import payment.Tunai;
import transaction.DaftarBarang;
import transaction.Invoice;
import transaction.KeranjangSewa;
import transaction.KeranjangSewa.ItemKeranjang;
import transaction.Transaksi;

/** Antarmuka CLI untuk Sistem Rental Peralatan Olahraga. */
public class Main {

    private final Scanner input = new Scanner(System.in);
    private final DaftarBarang daftarBarang = new DaftarBarang();
    private final ArrayList<Akun> daftarAkun = new ArrayList<>();
    private final ArrayList<Transaksi> semuaTransaksi = new ArrayList<>();

    public Main() {
        daftarAkun.add(new Admin("admin", "admin123"));
        daftarAkun.add(new Penyewa("penyewa", "penyewa123"));
    }

    public static void main(String[] args) {
        Main aplikasi = new Main();
        try {
            aplikasi.jalankan();
        } catch (NoSuchElementException ex) {
            System.out.println("\nInput berakhir. Program ditutup.");
        }
    }

    private void jalankan() {
        System.out.println("=== SPORT RENTAL INDONESIA ===");
        System.out.println("Akun awal: admin/admin123 | penyewa/penyewa123");
        while (true) {
            System.out.println("\n1. Masuk\n2. Daftar penyewa\n0. Keluar");
            int pilihan = bacaInt("Pilih menu: ", 0, 2);
            if (pilihan == 0) {
                System.out.println("Terima kasih.");
                return;
            }
            if (pilihan == 1) {
                prosesLogin();
            } else {
                prosesRegistrasi();
            }
        }
    }

    private void prosesLogin() {
        String username = bacaTeks("Username: ");
        String password = bacaTeks("Password: ");
        for (Akun akun : daftarAkun) {
            if (akun.login(username, password)) {
                System.out.println("Berhasil masuk sebagai " + akun.getUsername() + ".");
                if (akun instanceof Admin) {
                    menuAdmin((Admin) akun);
                } else {
                    menuPenyewa((Penyewa) akun);
                }
                return;
            }
        }
        System.out.println("Username atau password salah.");
    }

    private void prosesRegistrasi() {
        String username = bacaTeks("Username baru (tanpa spasi): ");
        if (username.matches(".*\\s+.*")) {
            System.out.println("Username tidak boleh mengandung spasi.");
            return;
        }
        for (Akun akun : daftarAkun) {
            if (akun.getUsername().equalsIgnoreCase(username)) {
                System.out.println("Username '" + username + "' sudah dipakai.");
                return;
            }
        }

        String password = bacaTeks("Password baru (minimal 4 karakter): ");
        if (password.length() < 4) {
            System.out.println("Password minimal 4 karakter.");
            return;
        }
        daftarAkun.add(new Penyewa(username, password));
        System.out.println("Akun '" + username + "' berhasil dibuat.");
    }

    private void menuAdmin(Admin admin) {
        while (true) {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Lihat katalog");
            System.out.println("2. Tambah barang");
            System.out.println("3. Hapus barang");
            System.out.println("4. Lihat transaksi");
            System.out.println("0. Logout");
            int pilihan = bacaInt("Pilih menu: ", 0, 4);
            switch (pilihan) {
                case 0:
                    return;
                case 1:
                    tampilkanKatalog();
                    break;
                case 2:
                    tambahBarang(admin);
                    break;
                case 3:
                    hapusBarang();
                    break;
                case 4:
                    lihatTransaksi(semuaTransaksi);
                    break;
                default:
                    throw new IllegalStateException("Pilihan menu admin tidak ditangani.");
            }
        }
    }

    private void menuPenyewa(Penyewa penyewa) {
        while (true) {
            System.out.println("\n=== MENU PENYEWA (" + penyewa.getUsername() + ") ===");
            System.out.println("1. Lihat katalog");
            System.out.println("2. Tambah barang ke keranjang");
            System.out.println("3. Lihat keranjang");
            System.out.println("4. Hapus barang dari keranjang");
            System.out.println("5. Kosongkan keranjang");
            System.out.println("6. Checkout dan bayar");
            System.out.println("7. Proses pengembalian");
            System.out.println("8. Lihat riwayat transaksi");
            System.out.println("0. Logout");
            int pilihan = bacaInt("Pilih menu: ", 0, 8);
            switch (pilihan) {
                case 0:
                    return;
                case 1:
                    tampilkanKatalog();
                    break;
                case 2:
                    tambahKeranjang(penyewa);
                    break;
                case 3:
                    tampilkanKeranjang(penyewa.getKeranjang());
                    break;
                case 4:
                    hapusKeranjang(penyewa.getKeranjang());
                    break;
                case 5:
                    penyewa.getKeranjang().kosongkanKeranjang();
                    System.out.println("Keranjang dikosongkan.");
                    break;
                case 6:
                    checkout(penyewa);
                    break;
                case 7:
                    pengembalian(penyewa);
                    break;
                case 8:
                    lihatTransaksi(penyewa.getRiwayat());
                    break;
                default:
                    throw new IllegalStateException("Pilihan menu penyewa tidak ditangani.");
            }
        }
    }

    private void tampilkanKatalog() {
        System.out.println("\n=== KATALOG BARANG ===");
        for (Barang barang : daftarBarang.getDaftarBarang()) {
            System.out.println(barang);
        }
    }

    private void tambahBarang(Admin admin) {
        System.out.println("Kategori: 1. Latihan ringan  2. Raket  3. Kardio");
        int kategori = bacaInt("Pilih kategori: ", 1, 3);
        String kode = bacaTeks("Kode barang: ").toUpperCase();
        if (daftarBarang.cariBarang(kode) != null) {
            System.out.println("Kode '" + kode + "' sudah terdaftar.");
            return;
        }
        String nama = bacaTeks("Nama barang: ");
        double tarif = bacaDouble("Tarif per hari (Rp): ", 1);
        Barang barang;
        if (kategori == 1) {
            barang = new AlatLatihanRingan(kode, nama, tarif);
        } else if (kategori == 2) {
            double biayaSenar = bacaDouble("Biaya perawatan senar per sewa (Rp): ", 0);
            barang = new AlatRaket(kode, nama, tarif, biayaSenar);
        } else {
            double biayaAsuransi = bacaDouble("Biaya asuransi per hari (Rp): ", 0);
            barang = new AlatKardio(kode, nama, tarif, biayaAsuransi);
        }
        try {
            admin.tambahBarang(daftarBarang, barang);
            System.out.println("Barang berhasil ditambahkan.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Barang gagal ditambahkan: " + ex.getMessage());
        }
    }

    private void hapusBarang() {
        String kode = bacaTeks("Kode barang yang dihapus: ");
        try {
            daftarBarang.hapusBarang(kode);
            System.out.println("Barang berhasil dihapus.");
        } catch (BarangTidakTersediaException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void tambahKeranjang(Penyewa penyewa) {
        String kode = bacaTeks("Kode barang: ");
        int durasi = bacaInt("Durasi sewa (hari): ", 1, 365);
        try {
            Barang barang = daftarBarang.cekTersedia(kode);
            penyewa.getKeranjang().tambahItem(barang, durasi);
            System.out.println(barang.getNama() + " ditambahkan ke keranjang.");
        } catch (BarangTidakTersediaException | InputTidakValidException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void tampilkanKeranjang(KeranjangSewa keranjang) {
        ArrayList<ItemKeranjang> items = keranjang.getDaftarItem();
        if (items.isEmpty()) {
            System.out.println("Keranjang masih kosong.");
            return;
        }
        System.out.println("\n=== KERANJANG ===");
        for (int i = 0; i < items.size(); i++) {
            ItemKeranjang item = items.get(i);
            System.out.printf("%d. %s | %d hari | %s%n", i + 1,
                    item.getBarang().getNama(), item.getDurasiHari(),
                    Pembayaran.formatRupiah(item.hitungSubtotal()));
        }
        System.out.println("Total: " + Pembayaran.formatRupiah(keranjang.hitungTotalBiaya()));
    }

    private void hapusKeranjang(KeranjangSewa keranjang) {
        tampilkanKeranjang(keranjang);
        if (keranjang.isEmpty()) {
            return;
        }
        int nomor = bacaInt("Nomor item yang dihapus: ", 1, keranjang.getDaftarItem().size());
        try {
            keranjang.hapusItem(nomor - 1);
            System.out.println("Item dihapus dari keranjang.");
        } catch (InputTidakValidException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void checkout(Penyewa penyewa) {
        KeranjangSewa keranjang = penyewa.getKeranjang();
        ArrayList<ItemKeranjang> items = keranjang.getDaftarItem();
        if (items.isEmpty()) {
            System.out.println("Keranjang masih kosong.");
            return;
        }
        System.out.println("Total pembayaran: " + Pembayaran.formatRupiah(keranjang.hitungTotalBiaya()));
        System.out.println("Metode: 1. Transfer Bank  2. QRIS  3. Tunai");
        int pilihan = bacaInt("Pilih metode pembayaran: ", 1, 3);
        Pembayaran metode;
        if (pilihan == 1) {
            metode = new TransferBank();
        } else if (pilihan == 2) {
            metode = new QRIS();
        } else {
            metode = new Tunai();
        }

        try {
            for (ItemKeranjang item : items) {
                daftarBarang.cekTersedia(item.getBarang().getId());
            }
            Transaksi transaksi = new Transaksi(penyewa, items, metode);
            metode.prosesPembayaran(keranjang.hitungTotalBiaya());
            for (ItemKeranjang item : items) {
                item.getBarang().setTersedia(false);
            }
            penyewa.tambahRiwayat(transaksi);
            semuaTransaksi.add(transaksi);
            keranjang.kosongkanKeranjang();
            System.out.println("\n" + Invoice.formatInvoice(transaksi));
        } catch (BarangTidakTersediaException | InputTidakValidException ex) {
            System.out.println("Checkout gagal: " + ex.getMessage());
        }
    }

    private void pengembalian(Penyewa penyewa) {
        ArrayList<Transaksi> riwayat = penyewa.getRiwayat();
        if (riwayat.isEmpty()) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        tampilkanDaftarTransaksi(riwayat);
        int nomor = bacaInt("Nomor transaksi yang dikembalikan: ", 1, riwayat.size());
        Transaksi transaksi = riwayat.get(nomor - 1);
        if (transaksi.isSelesai()) {
            System.out.println("Transaksi " + transaksi.getId() + " sudah selesai.");
            return;
        }

        System.out.println("Tanggal kembali: 1. Hari ini  2. Input tanggal  3. Simulasi telat");
        int mode = bacaInt("Pilih cara: ", 1, 3);
        LocalDate tanggalKembali;
        if (mode == 1) {
            tanggalKembali = LocalDate.now();
        } else if (mode == 2) {
            tanggalKembali = bacaTanggal();
            if (tanggalKembali == null) {
                return;
            }
        } else {
            int telat = bacaInt("Jumlah hari setelah jatuh tempo (0-365): ", 0, 365);
            tanggalKembali = transaksi.getTanggalJatuhTempo().plusDays(telat);
        }

        try {
            transaksi.prosesPengembalian(tanggalKembali);
            System.out.println("Pengembalian berhasil diproses.\n" + Invoice.formatInvoice(transaksi));
        } catch (InputTidakValidException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private LocalDate bacaTanggal() {
        while (true) {
            String teksTanggal = bacaTeks("Tanggal kembali (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(teksTanggal);
            } catch (DateTimeParseException ex) {
                System.out.println("Tanggal tidak valid. Gunakan format yyyy-MM-dd.");
            }
        }
    }

    private void lihatTransaksi(ArrayList<Transaksi> transaksiList) {
        if (transaksiList.isEmpty()) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        tampilkanDaftarTransaksi(transaksiList);
        int nomor = bacaInt("Nomor transaksi untuk melihat invoice (0 untuk kembali): ", 0, transaksiList.size());
        if (nomor > 0) {
            System.out.println("\n" + Invoice.formatInvoice(transaksiList.get(nomor - 1)));
        }
    }

    private void tampilkanDaftarTransaksi(ArrayList<Transaksi> transaksiList) {
        System.out.println("\n=== DAFTAR TRANSAKSI ===");
        for (int i = 0; i < transaksiList.size(); i++) {
            System.out.println((i + 1) + ". " + transaksiList.get(i));
        }
    }

    private String bacaTeks(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) {
            throw new NoSuchElementException("Input terminal berakhir.");
        }
        return input.nextLine().trim();
    }

    private int bacaInt(String prompt, int minimum, int maksimum) {
        while (true) {
            String teks = bacaTeks(prompt);
            try {
                int nilai = Integer.parseInt(teks);
                if (nilai < minimum || nilai > maksimum) {
                    System.out.println("Masukkan angka antara " + minimum + " dan " + maksimum + ".");
                    continue;
                }
                return nilai;
            } catch (NumberFormatException ex) {
                System.out.println("Masukkan angka bulat yang valid.");
            }
        }
    }

    private double bacaDouble(String prompt, double minimum) {
        while (true) {
            String teks = bacaTeks(prompt);
            try {
                double nilai = Double.parseDouble(teks);
                if (Double.isNaN(nilai) || Double.isInfinite(nilai) || nilai < minimum) {
                    System.out.println("Nilai minimal " + minimum + ".");
                    continue;
                }
                return nilai;
            } catch (NumberFormatException ex) {
                System.out.println("Masukkan angka yang valid.");
            }
        }
    }
}
