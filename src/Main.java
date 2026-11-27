import exception.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.*;
import payment.*;
import transaction.*;
import transaction.KeranjangSewa.ItemKeranjang;

/** Antarmuka GUI Swing untuk Sistem Rental Peralatan Olahraga. */
public class Main {

    private static final DateTimeFormatter FORMAT_TAMPIL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FORMAT_INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final DaftarBarang daftarBarang = new DaftarBarang();
    private final ArrayList<Akun> daftarAkun = new ArrayList<>();
    private final ArrayList<Transaksi> semuaTransaksi = new ArrayList<>();

    private final JFrame frame = new JFrame("Sport Rental Indonesia");
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final JTextField fieldUsername = new JTextField(18);
    private final JPasswordField fieldPassword = new JPasswordField(18);
    private final JSpinner spinnerDurasi = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
    private final JComboBox<String> comboPembayaran = new JComboBox<>(new String[]{"Transfer Bank", "QRIS", "Tunai"});
    private final JLabel labelAkun = new JLabel();
    private final JLabel labelTotal = new JLabel("Total: Rp0");

    private Admin adminAktif;
    private Penyewa penyewaAktif;
    private DefaultTableModel modelKatalog;
    private JTable tabelKatalogAdmin, tabelTransaksiAdmin, tabelKatalogPenyewa, tabelKeranjang, tabelRiwayat;
    private JTabbedPane tabsPenyewa;

    /** Menyiapkan akun awal dan membangun seluruh layar GUI. */
    public Main() {
        daftarAkun.add(new Admin("admin", "admin123"));
        daftarAkun.add(new Penyewa("penyewa", "penyewa123"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 560);
        frame.setLocationRelativeTo(null);

        modelKatalog = buatModel("Kode", "Nama Alat", "Kategori", "Tarif/Hari", "Biaya Ekstra", "Status");
        root.add(buatPanelLogin(), "LOGIN");
        root.add(buatPanelAdmin(), "ADMIN");
        root.add(buatPanelPenyewa(), "PENYEWA");
        frame.setContentPane(root);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Gunakan tampilan bawaan jika tema OS gagal dimuat.
            }
            new Main().getFrame().setVisible(true);
        });
    }

    public JFrame getFrame() {
        return frame;
    }

    private JPanel buatPanelLogin() {
        JPanel card = new JPanel(new GridLayout(0, 1, 0, 8));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel judul = new JLabel("Sport Rental Indonesia");
        judul.setFont(new Font("SansSerif", Font.BOLD, 18));
        JButton tombolMasuk = buatTombol("Masuk", e -> prosesLogin());
        frame.getRootPane().setDefaultButton(tombolMasuk);

        card.add(judul);
        card.add(new JLabel("Username"));
        card.add(fieldUsername);
        card.add(new JLabel("Password"));
        card.add(fieldPassword);
        card.add(buatBaris(FlowLayout.LEFT, tombolMasuk, buatTombol("Daftar Penyewa", e -> prosesRegistrasi())));
        card.add(new JLabel("Akun: admin/admin123 | penyewa/penyewa123"));

        JPanel pembungkus = new JPanel(new GridBagLayout());
        pembungkus.add(card);
        return pembungkus;
    }

    private JPanel buatPanelAdmin() {
        tabelKatalogAdmin = buatTabel(modelKatalog);
        tabelTransaksiAdmin = buatTabel(buatModel("ID", "Penyewa", "Sewa", "Tempo", "Kembali", "Status", "Total", "Denda"));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Katalog Barang", buatTab(tabelKatalogAdmin,
                buatBaris(FlowLayout.LEFT, buatTombol("Tambah Barang", e -> tambahBarang()),
                        buatTombol("Hapus Barang", e -> hapusBarang()))));
        tabs.addTab("Semua Transaksi", buatTab(tabelTransaksiAdmin,
                buatBaris(FlowLayout.LEFT, buatTombol("Lihat Invoice", e -> lihatInvoice(tabelTransaksiAdmin, semuaTransaksi)))));
        return buatShell("Panel Admin", tabs);
    }

    private JPanel buatPanelPenyewa() {
        tabelKatalogPenyewa = buatTabel(modelKatalog);
        tabelKeranjang = buatTabel(buatModel("Kode", "Nama Alat", "Durasi", "Subtotal"));
        tabelRiwayat = buatTabel(buatModel("ID", "Sewa", "Tempo", "Kembali", "Status", "Total", "Denda"));

        JPanel bawahKeranjang = new JPanel(new BorderLayout());
        bawahKeranjang.add(buatBaris(FlowLayout.LEFT,
                buatTombol("Hapus", e -> hapusKeranjang()),
                buatTombol("Kosongkan", e -> { penyewaAktif.getKeranjang().kosongkanKeranjang(); perbaruiTabel(); })),
                BorderLayout.WEST);
        bawahKeranjang.add(buatBaris(FlowLayout.RIGHT,
                labelTotal, comboPembayaran, buatTombol("Checkout & Bayar", e -> checkout())),
                BorderLayout.EAST);

        tabsPenyewa = new JTabbedPane();
        tabsPenyewa.addTab("Katalog Barang", buatTab(tabelKatalogPenyewa,
                buatBaris(FlowLayout.LEFT, new JLabel("Durasi (hari):"), spinnerDurasi,
                        buatTombol("Tambah ke Keranjang", e -> tambahKeranjang()))));
        tabsPenyewa.addTab("Keranjang (0)", buatTab(tabelKeranjang, bawahKeranjang));
        tabsPenyewa.addTab("Pengembalian & Riwayat", buatTab(tabelRiwayat,
                buatBaris(FlowLayout.LEFT, buatTombol("Proses Pengembalian", e -> pengembalian()),
                        buatTombol("Lihat Invoice", e -> lihatInvoice(tabelRiwayat, penyewaAktif.getRiwayat())))));
        return buatShell("Portal Penyewa", tabsPenyewa);
    }

    private JPanel buatShell(String judulLayar, JTabbedPane tabs) {
        JLabel judul = new JLabel(judulLayar);
        judul.setFont(new Font("SansSerif", Font.BOLD, 16));
        JPanel atas = new JPanel(new BorderLayout());
        atas.add(buatBaris(FlowLayout.LEFT, judul, labelAkun), BorderLayout.WEST);
        atas.add(buatTombol("Logout", e -> {
            adminAktif = null;
            penyewaAktif = null;
            fieldUsername.setText("");
            fieldPassword.setText("");
            cards.show(root, "LOGIN");
        }), BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));
        panel.add(atas, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private void prosesLogin() {
        try {
            String user = wajibIsi(fieldUsername.getText(), "Username");
            String pass = wajibIsi(new String(fieldPassword.getPassword()), "Password");
            for (Akun akun : daftarAkun) {
                if (akun.login(user, pass)) {
                    labelAkun.setText("| Akun: " + akun.getUsername());
                    if (akun instanceof Admin) {
                        adminAktif = (Admin) akun;
                        cards.show(root, "ADMIN");
                    } else {
                        penyewaAktif = (Penyewa) akun;
                        cards.show(root, "PENYEWA");
                    }
                    perbaruiTabel();
                    return;
                }
            }
            throw new InputTidakValidException("Username atau password salah.");
        } catch (InputTidakValidException ex) {
            tampilError(ex.getMessage());
        }
    }

    private void prosesRegistrasi() {
        JTextField tfUserBaru = new JTextField();
        JPasswordField pfPassBaru = new JPasswordField();
        if (!tampilForm("Daftar Penyewa", new String[]{"Username (tanpa spasi)", "Password (min 4 karakter)"}, tfUserBaru, pfPassBaru)) {
            return;
        }
        try {
            String user = wajibIsi(tfUserBaru.getText(), "Username");
            if (user.contains(" ")) {
                throw new InputTidakValidException("Username tidak boleh mengandung spasi.");
            }
            for (Akun akun : daftarAkun) {
                if (akun.getUsername().equalsIgnoreCase(user)) {
                    throw new InputTidakValidException("Username '" + user + "' sudah dipakai.");
                }
            }
            String pass = wajibIsi(new String(pfPassBaru.getPassword()), "Password");
            if (pass.length() < 4) {
                throw new InputTidakValidException("Password minimal 4 karakter.");
            }
            daftarAkun.add(new Penyewa(user, pass));
            JOptionPane.showMessageDialog(frame, "Akun '" + user + "' berhasil dibuat.");
        } catch (InputTidakValidException ex) {
            tampilError(ex.getMessage());
        }
    }

   private void tambahBarang() {
        JComboBox<String> comboKat = new JComboBox<>(new String[]{"Latihan Ringan", "Raket (+Senar)", "Kardio (+Asuransi)"});
        JTextField tfKode = new JTextField(), tfNama = new JTextField(), tfTarif = new JTextField(), tfEkstra = new JTextField("0");
        tfEkstra.setEnabled(false);
        comboKat.addActionListener(e -> tfEkstra.setEnabled(comboKat.getSelectedIndex() != 0));

        if (!tampilForm("Tambah Barang", new String[]{"Kategori", "Kode", "Nama Alat", "Tarif/Hari (Rp)", "Biaya Ekstra (Rp)"},
                comboKat, tfKode, tfNama, tfTarif, tfEkstra)) {
            return;
        }
        try {
            String kode = wajibIsi(tfKode.getText(), "Kode").toUpperCase();
            if (daftarBarang.cariBarang(kode) != null) {
                throw new InputTidakValidException("Kode '" + kode + "' sudah ada.");
            }
            String nama = wajibIsi(tfNama.getText(), "Nama alat");
            double tarif = parseAngka(tfTarif.getText(), 1, "Tarif harian");

            Barang baru;
            int idx = comboKat.getSelectedIndex();
            if (idx == 0) {
                baru = new AlatLatihanRingan(kode, nama, tarif);
            } else if (idx == 1) {
                baru = new AlatRaket(kode, nama, tarif, parseAngka(tfEkstra.getText(), 0, "Biaya perawatan senar"));
            } else {
                baru = new AlatKardio(kode, nama, tarif, parseAngka(tfEkstra.getText(), 0, "Biaya asuransi harian"));
            }
            adminAktif.tambahBarang(daftarBarang, baru);
            perbaruiTabel();
        } catch (InputTidakValidException ex) {
            tampilError(ex.getMessage());
        }
    }

    private void hapusBarang() {
        try {
            String kode = kodeTerpilih(tabelKatalogAdmin);
            if (JOptionPane.showConfirmDialog(frame, "Hapus '" + kode + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                daftarBarang.hapusBarang(kode);
                perbaruiTabel();
            }
        } catch (InputTidakValidException | BarangTidakTersediaException ex) {
            tampilError(ex.getMessage());
        }
    }

    private void tambahKeranjang() {
        try {
            Barang barang = daftarBarang.cekTersedia(kodeTerpilih(tabelKatalogPenyewa));
            int durasi = (Integer) spinnerDurasi.getValue();
            penyewaAktif.getKeranjang().tambahItem(barang, durasi);
            perbaruiTabel();
            JOptionPane.showMessageDialog(frame, barang.getNama() + " (" + durasi + " hari) masuk ke keranjang.");
        } catch (InputTidakValidException | BarangTidakTersediaException ex) {
            tampilError(ex.getMessage());
        }
    }

    private void hapusKeranjang() {
        try {
            penyewaAktif.getKeranjang().hapusItem(tabelKeranjang.getSelectedRow());
            perbaruiTabel();
        } catch (InputTidakValidException ex) {
            tampilError(ex.getMessage());
        }
    }

    private void checkout() {
        KeranjangSewa keranjang = penyewaAktif.getKeranjang();
        try {
            if (keranjang.isEmpty()) {
                throw new InputTidakValidException("Keranjang masih kosong.");
            }
            ArrayList<ItemKeranjang> items = keranjang.getDaftarItem();
            for (ItemKeranjang item : items) {
                daftarBarang.cekTersedia(item.getBarang().getId());
            }

            Pembayaran metode;
            int pilih = comboPembayaran.getSelectedIndex();
            if (pilih == 0) {
                metode = new TransferBank();
            } else if (pilih == 1) {
                metode = new QRIS();
            } else {
                metode = new Tunai();
            }

            metode.prosesPembayaran(keranjang.hitungTotalBiaya());
            for (ItemKeranjang item : items) {
                item.getBarang().setTersedia(false);
            }
            Transaksi trx = new Transaksi(penyewaAktif, items, metode);
            penyewaAktif.tambahRiwayat(trx);
            semuaTransaksi.add(trx);
            keranjang.kosongkanKeranjang();
            perbaruiTabel();
            tampilDialogInvoice(trx);
        } catch (InputTidakValidException | BarangTidakTersediaException ex) {
            tampilError(ex.getMessage());
        }
    }

    private void pengembalian() {
        int baris = tabelRiwayat.getSelectedRow();
        if (baris < 0) {
            tampilError("Pilih transaksi di tabel riwayat dulu.");
            return;
        }
        Transaksi trx = penyewaAktif.getRiwayat().get(baris);
        if (trx.isSelesai()) {
            tampilError("Transaksi " + trx.getId() + " sudah selesai.");
            return;
        }

        JComboBox<String> comboMode = new JComboBox<>(new String[]{"Hari Ini", "Tanggal Manual (yyyy-MM-dd)", "Simulasi Telat N Hari"});
        JTextField tfNilai = new JTextField(LocalDate.now().format(FORMAT_INPUT));
        tfNilai.setEnabled(false);
        comboMode.addActionListener(e -> {
            int mode = comboMode.getSelectedIndex();
            tfNilai.setEnabled(mode != 0);
            tfNilai.setText(mode == 2 ? "0" : LocalDate.now().format(FORMAT_INPUT));
        });
        if (!tampilForm("Pengembalian " + trx.getId(), new String[]{"Mode Tanggal", "Tanggal / Hari Telat"}, comboMode, tfNilai)) {
            return;
        }

        try {
            LocalDate tglKembali;
            int mode = comboMode.getSelectedIndex();
            if (mode == 0) {
                tglKembali = LocalDate.now();
            } else if (mode == 1) {
                try {
                    tglKembali = LocalDate.parse(wajibIsi(tfNilai.getText(), "Tanggal"), FORMAT_INPUT);
                } catch (DateTimeParseException ex) {
                    throw new InputTidakValidException("Format tanggal harus yyyy-MM-dd.", ex);
                }
            } else {
                int telat = (int) parseAngka(tfNilai.getText(), 0, "Hari terlambat");
                if (telat > 365) {
                    throw new InputTidakValidException("Maksimal simulasi 365 hari.");
                }
                tglKembali = trx.getTanggalJatuhTempo().plusDays(telat);
            }
            trx.prosesPengembalian(tglKembali);
            perbaruiTabel();
            tampilDialogInvoice(trx);
        } catch (InputTidakValidException ex) {
            tampilError(ex.getMessage());
        }
    }

    private void perbaruiTabel() {
        modelKatalog.setRowCount(0);
        for (Barang b : daftarBarang.getDaftarBarang()) {
            String ekstra = "-";
            if (b instanceof AlatRaket) {
                ekstra = Pembayaran.formatRupiah(((AlatRaket) b).getBiayaPerawatanSenar()) + "/sewa";
            } else if (b instanceof AlatKardio) {
                ekstra = Pembayaran.formatRupiah(((AlatKardio) b).getBiayaAsuransiHarian()) + "/hari";
            }
            modelKatalog.addRow(new Object[]{b.getId(), b.getNama(), b.getKategori(),
                Pembayaran.formatRupiah(b.getTarifHarian()), ekstra, b.isTersedia() ? "Tersedia" : "Sedang Disewa"});
        }

        isiTabelTransaksi((DefaultTableModel) tabelTransaksiAdmin.getModel(), semuaTransaksi, true);
        if (penyewaAktif != null) {
            DefaultTableModel mKeranjang = (DefaultTableModel) tabelKeranjang.getModel();
            mKeranjang.setRowCount(0);
            ArrayList<ItemKeranjang> items = penyewaAktif.getKeranjang().getDaftarItem();
            for (ItemKeranjang x : items) {
                mKeranjang.addRow(new Object[]{x.getBarang().getId(), x.getBarang().getNama(),
                    x.getDurasiHari() + " hari", Pembayaran.formatRupiah(x.hitungSubtotal())});
            }
            labelTotal.setText("Total: " + Pembayaran.formatRupiah(penyewaAktif.getKeranjang().hitungTotalBiaya()));
            tabsPenyewa.setTitleAt(1, "Keranjang (" + items.size() + ")");
            isiTabelTransaksi((DefaultTableModel) tabelRiwayat.getModel(), penyewaAktif.getRiwayat(), false);
        }
    }

    private void isiTabelTransaksi(DefaultTableModel model, ArrayList<Transaksi> sumber, boolean denganPenyewa) {
        model.setRowCount(0);
        for (Transaksi t : sumber) {
            String kembali = t.getTanggalKembali() == null ? "-" : t.getTanggalKembali().format(FORMAT_TAMPIL);
            String total = Pembayaran.formatRupiah(t.getTotalBiayaAwal());
            String denda = Pembayaran.formatRupiah(t.getTotalDenda());
            if (denganPenyewa) {
                model.addRow(new Object[]{t.getId(), t.getPenyewa().getUsername(), t.getTanggalSewa().format(FORMAT_TAMPIL),
                    t.getTanggalJatuhTempo().format(FORMAT_TAMPIL), kembali, t.getStatus(), total, denda});
            } else {
                model.addRow(new Object[]{t.getId(), t.getTanggalSewa().format(FORMAT_TAMPIL),
                    t.getTanggalJatuhTempo().format(FORMAT_TAMPIL), kembali, t.getStatus(), total, denda});
            }
        }
    }

    private void lihatInvoice(JTable tabel, ArrayList<Transaksi> sumber) {
        int r = tabel.getSelectedRow();
        if (r < 0 || r >= sumber.size()) {
            tampilError("Pilih transaksi terlebih dahulu.");
            return;
        }
        tampilDialogInvoice(sumber.get(r));
    }

    private void tampilDialogInvoice(Transaksi trx) {
        JTextArea area = new JTextArea(Invoice.formatInvoice(trx));
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(480, 290));
        JOptionPane.showMessageDialog(frame, scroll, "Invoice " + trx.getId(), JOptionPane.PLAIN_MESSAGE);
    }

    private boolean tampilForm(String judul, String[] labels, Component... inputs) {
        JPanel p = new JPanel(new GridLayout(labels.length, 2, 8, 8));
        for (int i = 0; i < labels.length; i++) {
            p.add(new JLabel(labels[i]));
            p.add(inputs[i]);
        }
        return JOptionPane.showConfirmDialog(frame, p, judul, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private String kodeTerpilih(JTable tabel) throws InputTidakValidException {
        if (tabel.getSelectedRow() < 0) {
            throw new InputTidakValidException("Pilih barang di tabel terlebih dahulu.");
        }
        return String.valueOf(tabel.getValueAt(tabel.getSelectedRow(), 0));
    }

    private String wajibIsi(String teks, String field) throws InputTidakValidException {
        String hasil = teks == null ? "" : teks.trim();
        if (hasil.isEmpty()) {
            throw new InputTidakValidException(field + " tidak boleh kosong.");
        }
        return hasil;
    }

    private double parseAngka(String teks, double min, String field) throws InputTidakValidException {
        try {
            double v = Double.parseDouble(wajibIsi(teks, field));
            if (Double.isNaN(v) || Double.isInfinite(v) || v < min) {
                throw new InputTidakValidException(field + " minimal " + (long) min + ".");
            }
            return v;
        } catch (NumberFormatException ex) {
            throw new InputTidakValidException(field + " harus berupa angka valid.", ex);
        }
    }

    private void tampilError(String pesan) {
        JOptionPane.showMessageDialog(frame, pesan, "Validasi", JOptionPane.ERROR_MESSAGE);
    }

    private DefaultTableModel buatModel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private JTable buatTabel(DefaultTableModel model) {
        JTable tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setFillsViewportHeight(true);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tabel;
    }

    private JPanel buatTab(JTable tabel, Component bawah) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.add(new JScrollPane(tabel), BorderLayout.CENTER);
        p.add(bawah, BorderLayout.SOUTH);
        return p;
    }

    private JButton buatTombol(String teks, ActionListener aksi) {
        JButton b = new JButton(teks);
        b.addActionListener(aksi);
        return b;
    }

    private JPanel buatBaris(int align, Component... komponen) {
        JPanel p = new JPanel(new FlowLayout(align, 8, 2));
        for (Component c : komponen) {
            p.add(c);
        }
        return p;
    }
} 
