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

    
