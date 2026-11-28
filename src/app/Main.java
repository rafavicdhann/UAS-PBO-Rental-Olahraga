package app;

import exception.BarangTidakTersediaException;
import exception.InputTidakValidException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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

    // Kode warna ANSI agar tampilan terminal lebih mudah dibaca.
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    private static final int LEBAR_PANEL = 68;

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
            System.out.println("\n" + YELLOW + "Input terminal berakhir. Program ditutup." + RESET);
        }
    }

    // ========================= TAMPILAN CLI =========================

    private void jalankan() {
        tampilkanBanner();
        tampilkanPanel("AKUN DEMO", "Admin   : admin / admin123", "Penyewa : penyewa / penyewa123");

        while (true) {
            tampilkanMenu("MENU UTAMA",
                    "[1] Masuk ke akun",
                    "[2] Daftar akun penyewa",
                    "[0] Keluar dari aplikasi");

            int pilihan = bacaInt("Pilih menu", 0, 2);
            if (pilihan == 0) {
                tampilkanPanel("SAMPAI JUMPA", "Terima kasih telah menggunakan Sport Rental Indonesia.");
                return;
            }
            if (pilihan == 1) {
                prosesLogin();
            } else {
                prosesRegistrasi();
            }
        }
    }

    private void tampilkanBanner() {
        String garis = "═".repeat(LEBAR_PANEL - 2);
        System.out.println();
        System.out.println(CYAN + BOLD + "╔" + garis + "╗" + RESET);
        cetakBarisBanner("SPORT RENTAL INDONESIA");
        cetakBarisBanner("SISTEM PENYEWAAN PERALATAN OLAHRAGA");
        System.out.println(CYAN + BOLD + "╚" + garis + "╝" + RESET);
        System.out.println(DIM + "  Kelola penyewaan perlengkapan olahraga dengan mudah dan cepat." + RESET);
        System.out.println();
    }

    private void cetakBarisBanner(String teks) {
        int ruang = LEBAR_PANEL - 2;
        int kiri = Math.max(0, (ruang - teks.length()) / 2);
        int kanan = Math.max(0, ruang - teks.length() - kiri);
        System.out.println(CYAN + BOLD + "║" + RESET
                + " ".repeat(kiri) + WHITE + BOLD + teks + RESET
                + " ".repeat(kanan) + CYAN + BOLD + "║" + RESET);
    }

    private void tampilkanPanel(String judul, String... isi) {
        String garis = "─".repeat(LEBAR_PANEL - 2);
        System.out.println(CYAN + "┌" + garis + "┐" + RESET);
        cetakBarisPanel(judul, true);
        System.out.println(CYAN + "├" + garis + "┤" + RESET);
        for (String baris : isi) {
            cetakBarisPanel(baris, false);
        }
        System.out.println(CYAN + "└" + garis + "┘" + RESET);
    }

    private void cetakBarisPanel(String teks, boolean judul) {
        int lebarIsi = LEBAR_PANEL - 4;
        String teksTampil = teks.length() > lebarIsi ? teks.substring(0, lebarIsi - 3) + "..." : teks;
        String format = "%-" + lebarIsi + "s";
        String warnaTeks = judul ? BOLD + WHITE : WHITE;
        System.out.println(CYAN + "│" + RESET + " " + warnaTeks
                + String.format(format, teksTampil) + RESET + " " + CYAN + "│" + RESET);
    }

    private void tampilkanMenu(String judul, String... pilihan) {
        System.out.println();
        tampilkanPanel(judul, pilihan);
    }

    private void tampilkanJudul(String judul, String deskripsi) {
        System.out.println();
        System.out.println(BLUE + BOLD + "  » " + judul + RESET);
        if (deskripsi != null && !deskripsi.isBlank()) {
            System.out.println(DIM + "    " + deskripsi + RESET);
        }
        System.out.println(BLUE + "  " + "─".repeat(LEBAR_PANEL - 4) + RESET);
    }

    private void pesanSukses(String pesan) {
        System.out.println(GREEN + BOLD + "  ✔ Berhasil: " + RESET + pesan);
    }

    private void pesanGagal(String pesan) {
        System.out.println(RED + BOLD + "  ✖ Gagal: " + RESET + pesan);
    }

    private void pesanInfo(String pesan) {
        System.out.println(YELLOW + "  ● " + RESET + pesan);
    }

    private String potong(String teks, int batas) {
        if (teks == null) {
            return "-";
        }
        return teks.length() > batas ? teks.substring(0, batas - 3) + "..." : teks;
    }

    // ========================= LOGIN & REGISTRASI =========================

    private void prosesLogin() {
        tampilkanJudul("LOGIN", "Masukkan username dan password akun Anda.");
        String username = bacaTeks("Username");
        String password = bacaTeks("Password");

        for (Akun akun : daftarAkun) {
            if (akun.login(username, password)) {
                pesanSukses("Login berhasil. Selamat datang, " + akun.getUsername() + "!");
                if (akun instanceof Admin) {
                    menuAdmin((Admin) akun);
                } else {
                    menuPenyewa((Penyewa) akun);
                }
                return;
            }
        }
        pesanGagal("Username atau password yang dimasukkan salah.");
    }

    private void prosesRegistrasi() {
        tampilkanJudul("REGISTRASI PENYEWA", "Buat akun baru untuk mulai menyewa peralatan olahraga.");
        String username = bacaTeks("Username baru (tanpa spasi)");
        if (username.matches(".*\\s+.*")) {
            pesanGagal("Username tidak boleh mengandung spasi.");
            return;
        }
        if (username.isBlank()) {
            pesanGagal("Username tidak boleh kosong.");
            return;
        }

        for (Akun akun : daftarAkun) {
            if (akun.getUsername().equalsIgnoreCase(username)) {
                pesanGagal("Username '" + username + "' sudah digunakan.");
                return;
            }
        }

        String password = bacaTeks("Password baru (minimal 4 karakter)");
        if (password.length() < 4) {
            pesanGagal("Password minimal terdiri dari 4 karakter.");
            return;
        }

        daftarAkun.add(new Penyewa(username, password));
        pesanSukses("Akun penyewa '" + username + "' berhasil dibuat.");
    }

    // ========================= MENU ADMIN =========================

    private void menuAdmin(Admin admin) {
        while (true) {
            tampilkanMenu("PANEL ADMIN | " + admin.getUsername(),
                    "[1] Lihat katalog barang",
                    "[2] Tambah barang baru",
                    "[3] Hapus barang",
                    "[4] Lihat seluruh transaksi",
                    "[0] Logout");

            int pilihan = bacaInt("Pilih menu", 0, 4);
            switch (pilihan) {
                case 0:
                    pesanInfo("Anda telah logout dari akun admin.");
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
                    pesanGagal("Pilihan menu tidak dikenali.");
            }
        }
    }

    // ========================= MENU PENYEWA =========================

    private void menuPenyewa(Penyewa penyewa) {
        while (true) {
            tampilkanMenu("PANEL PENYEWA | " + penyewa.getUsername(),
                    "[1] Lihat katalog barang",
                    "[2] Tambah barang ke keranjang",
                    "[3] Lihat keranjang sewa",
                    "[4] Hapus barang dari keranjang",
                    "[5] Kosongkan keranjang",
                    "[6] Checkout dan pembayaran",
                    "[7] Proses pengembalian barang",
                    "[8] Riwayat transaksi",
                    "[0] Logout");

            int pilihan = bacaInt("Pilih menu", 0, 8);
            switch (pilihan) {
                case 0:
                    pesanInfo("Anda telah logout dari akun penyewa.");
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
                    pesanSukses("Keranjang berhasil dikosongkan.");
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
                    pesanGagal("Pilihan menu tidak dikenali.");
            }
        }
    }

    // ========================= KELOLA BARANG =========================

    private void tampilkanKatalog() {
        List<Barang> barangList = daftarBarang.getDaftarBarang();
        tampilkanJudul("KATALOG PERALATAN OLAHRAGA", "Daftar barang yang tersedia pada sistem rental.");
        if (barangList.isEmpty()) {
            pesanInfo("Katalog masih kosong. Belum ada barang yang terdaftar.");
            return;
        }

        System.out.printf(CYAN + BOLD + "  %-5s %-14s %-28s %-15s%n" + RESET,
                "NO", "KODE", "NAMA BARANG", "DETAIL");
        System.out.println(DIM + "  " + "─".repeat(65) + RESET);
        for (int i = 0; i < barangList.size(); i++) {
            Barang barang = barangList.get(i);
            // Jenis barang ditampilkan dari nama kelas objeknya.
            System.out.printf("  %-5d %-14s %-28s %-15s%n",
                    i + 1,
                    potong(barang.getId(), 13),
                    potong(barang.getNama(), 27),
                    potong(barang.getClass().getSimpleName(), 14));
        }
        System.out.println(DIM + "\n  Jenis barang ditampilkan pada kolom detail." + RESET);
    }

    private void tambahBarang(Admin admin) {
        tampilkanJudul("TAMBAH BARANG", "Pilih kategori dan lengkapi informasi barang baru.");
        tampilkanPanel("KATEGORI BARANG",
                "[1] Alat latihan ringan",
                "[2] Alat raket",
                "[3] Alat kardio");
        int kategori = bacaInt("Pilih kategori", 1, 3);
        String kode = bacaTeks("Kode barang").toUpperCase();
        if (daftarBarang.cariBarang(kode) != null) {
            pesanGagal("Kode '" + kode + "' sudah terdaftar.");
            return;
        }

        String nama = bacaTeks("Nama barang");
        double tarif = bacaDouble("Tarif sewa per hari (Rp)", 1);
        Barang barang;
        if (kategori == 1) {
            barang = new AlatLatihanRingan(kode, nama, tarif);
        } else if (kategori == 2) {
            double biayaSenar = bacaDouble("Biaya perawatan senar per sewa (Rp)", 0);
            barang = new AlatRaket(kode, nama, tarif, biayaSenar);
        } else {
            double biayaAsuransi = bacaDouble("Biaya asuransi per hari (Rp)", 0);
            barang = new AlatKardio(kode, nama, tarif, biayaAsuransi);
        }

        try {
            admin.tambahBarang(daftarBarang, barang);
            pesanSukses("Barang '" + nama + "' berhasil ditambahkan ke katalog.");
        } catch (IllegalArgumentException ex) {
            pesanGagal("Barang gagal ditambahkan: " + ex.getMessage());
        }
    }

    private void hapusBarang() {
        tampilkanJudul("HAPUS BARANG", "Masukkan kode barang yang ingin dihapus dari katalog.");
        String kode = bacaTeks("Kode barang");
        try {
            daftarBarang.hapusBarang(kode);
            pesanSukses("Barang dengan kode '" + kode + "' berhasil dihapus.");
        } catch (BarangTidakTersediaException ex) {
            pesanGagal(ex.getMessage());
        }
    }

    // ========================= KERANJANG SEWA =========================

    private void tambahKeranjang(Penyewa penyewa) {
        tampilkanJudul("TAMBAH KE KERANJANG", "Masukkan kode barang dan lama waktu penyewaan.");
        String kode = bacaTeks("Kode barang");
        int durasi = bacaInt("Durasi sewa (hari)", 1, 365);
        try {
            Barang barang = daftarBarang.cekTersedia(kode);
            penyewa.getKeranjang().tambahItem(barang, durasi);
            pesanSukses(barang.getNama() + " ditambahkan ke keranjang selama " + durasi + " hari.");
        } catch (BarangTidakTersediaException | InputTidakValidException ex) {
            pesanGagal(ex.getMessage());
        }
    }

    private void tampilkanKeranjang(KeranjangSewa keranjang) {
        ArrayList<ItemKeranjang> items = keranjang.getDaftarItem();
        tampilkanJudul("KERANJANG SEWA", "Periksa barang dan total biaya sebelum checkout.");
        if (items.isEmpty()) {
            pesanInfo("Keranjang Anda masih kosong.");
            return;
        }

        System.out.printf(CYAN + BOLD + "  %-5s %-28s %-12s %-20s%n" + RESET,
                "NO", "BARANG", "DURASI", "SUBTOTAL");
        System.out.println(DIM + "  " + "─".repeat(68) + RESET);
        for (int i = 0; i < items.size(); i++) {
            ItemKeranjang item = items.get(i);
            System.out.printf("  %-5d %-28s %-12s %-20s%n",
                    i + 1,
                    potong(item.getBarang().getNama(), 27),
                    item.getDurasiHari() + " hari",
                    Pembayaran.formatRupiah(item.hitungSubtotal()));
        }
        System.out.println(DIM + "  " + "─".repeat(68) + RESET);
        System.out.println(BOLD + GREEN + "  TOTAL PEMBAYARAN: "
                + Pembayaran.formatRupiah(keranjang.hitungTotalBiaya()) + RESET);
    }

    private void hapusKeranjang(KeranjangSewa keranjang) {
        tampilkanKeranjang(keranjang);
        if (keranjang.isEmpty()) {
            return;
        }

        int nomor = bacaInt("Nomor item yang ingin dihapus", 1, keranjang.getDaftarItem().size());
        try {
            keranjang.hapusItem(nomor - 1);
            pesanSukses("Item berhasil dihapus dari keranjang.");
        } catch (InputTidakValidException ex) {
            pesanGagal(ex.getMessage());
        }
    }

    // ========================= CHECKOUT & PEMBAYARAN =========================

    private void checkout(Penyewa penyewa) {
        KeranjangSewa keranjang = penyewa.getKeranjang();
        ArrayList<ItemKeranjang> items = keranjang.getDaftarItem();
        tampilkanJudul("CHECKOUT", "Konfirmasi pesanan dan pilih metode pembayaran.");
        if (items.isEmpty()) {
            pesanGagal("Keranjang masih kosong. Tambahkan barang sebelum checkout.");
            return;
        }

        tampilkanKeranjang(keranjang);
        tampilkanPanel("METODE PEMBAYARAN",
                "[1] Transfer bank",
                "[2] QRIS",
                "[3] Tunai");
        int pilihan = bacaInt("Pilih metode pembayaran", 1, 3);
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
            pesanSukses("Pembayaran berhasil. Invoice transaksi ditampilkan di bawah.");
            System.out.println("\n" + CYAN + Invoice.formatInvoice(transaksi) + RESET);
        } catch (BarangTidakTersediaException | InputTidakValidException ex) {
            pesanGagal("Checkout gagal: " + ex.getMessage());
        }
    }

    // ========================= PENGEMBALIAN =========================

    private void pengembalian(Penyewa penyewa) {
        ArrayList<Transaksi> riwayat = penyewa.getRiwayat();
        tampilkanJudul("PENGEMBALIAN BARANG", "Pilih transaksi yang barangnya akan dikembalikan.");
        if (riwayat.isEmpty()) {
            pesanInfo("Belum ada transaksi yang dapat diproses.");
            return;
        }

        tampilkanDaftarTransaksi(riwayat);
        int nomor = bacaInt("Nomor transaksi yang dikembalikan", 1, riwayat.size());
        Transaksi transaksi = riwayat.get(nomor - 1);
        if (transaksi.isSelesai()) {
            pesanInfo("Transaksi " + transaksi.getId() + " sudah selesai diproses.");
            return;
        }

        tampilkanPanel("TANGGAL PENGEMBALIAN",
                "[1] Gunakan tanggal hari ini",
                "[2] Masukkan tanggal sendiri",
                "[3] Simulasi keterlambatan");
        int mode = bacaInt("Pilih cara pengembalian", 1, 3);
        LocalDate tanggalKembali;
        if (mode == 1) {
            tanggalKembali = LocalDate.now();
        } else if (mode == 2) {
            tanggalKembali = bacaTanggal();
            if (tanggalKembali == null) {
                return;
            }
        } else {
            int telat = bacaInt("Jumlah hari setelah jatuh tempo", 0, 365);
            tanggalKembali = transaksi.getTanggalJatuhTempo().plusDays(telat);
        }

        try {
            transaksi.prosesPengembalian(tanggalKembali);
            pesanSukses("Pengembalian barang berhasil diproses.");
            System.out.println("\n" + CYAN + Invoice.formatInvoice(transaksi) + RESET);
        } catch (InputTidakValidException ex) {
            pesanGagal(ex.getMessage());
        }
    }

    private LocalDate bacaTanggal() {
        while (true) {
            String teksTanggal = bacaTeks("Tanggal kembali (yyyy-MM-dd)");
            try {
                return LocalDate.parse(teksTanggal);
            } catch (DateTimeParseException ex) {
                pesanGagal("Format tanggal tidak valid. Contoh format: 2026-11-27.");
            }
        }
    }

    // ========================= RIWAYAT TRANSAKSI =========================

    private void lihatTransaksi(ArrayList<Transaksi> transaksiList) {
        tampilkanJudul("RIWAYAT TRANSAKSI", "Pilih transaksi untuk melihat invoice lengkap.");
        if (transaksiList.isEmpty()) {
            pesanInfo("Belum ada transaksi yang tercatat.");
            return;
        }

        tampilkanDaftarTransaksi(transaksiList);
        int nomor = bacaInt("Nomor transaksi untuk melihat invoice (0 untuk kembali)", 0, transaksiList.size());
        if (nomor > 0) {
            System.out.println("\n" + CYAN + Invoice.formatInvoice(transaksiList.get(nomor - 1)) + RESET);
        }
    }

    private void tampilkanDaftarTransaksi(ArrayList<Transaksi> transaksiList) {
        System.out.println();
        System.out.println(CYAN + BOLD + "  DAFTAR TRANSAKSI" + RESET);
        System.out.println(DIM + "  " + "─".repeat(LEBAR_PANEL - 4) + RESET);
        for (int i = 0; i < transaksiList.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, transaksiList.get(i));
        }
    }

    // ========================= INPUT & VALIDASI =========================

    private String bacaTeks(String prompt) {
        System.out.print(YELLOW + "  ➜ " + RESET + prompt + ": " + CYAN);
        System.out.flush();
        if (!input.hasNextLine()) {
            throw new NoSuchElementException("Input terminal berakhir.");
        }
        String hasil = input.nextLine().trim();
        System.out.print(RESET);
        return hasil;
    }

    private int bacaInt(String prompt, int minimum, int maksimum) {
        while (true) {
            String teks = bacaTeks(prompt);
            try {
                int nilai = Integer.parseInt(teks);
                if (nilai < minimum || nilai > maksimum) {
                    pesanGagal("Masukkan angka antara " + minimum + " sampai " + maksimum + ".");
                    continue;
                }
                return nilai;
            } catch (NumberFormatException ex) {
                pesanGagal("Input harus berupa angka bulat.");
            }
        }
    }

    private double bacaDouble(String prompt, double minimum) {
        while (true) {
            String teks = bacaTeks(prompt);
            try {
                // Mendukung input desimal dengan koma maupun titik.
                double nilai = Double.parseDouble(teks.replace(',', '.'));
                if (Double.isNaN(nilai) || Double.isInfinite(nilai) || nilai < minimum) {
                    pesanGagal("Nilai minimal adalah " + minimum + ".");
                    continue;
                }
                return nilai;
            } catch (NumberFormatException ex) {
                pesanGagal("Masukkan nominal angka yang valid.");
            }
        }
    }
}

