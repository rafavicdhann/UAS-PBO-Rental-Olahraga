package model;

/**
 * Kelas abstrak yang merepresentasikan akun pengguna dalam sistem rental.
 */
public abstract class Akun {
    private String username;
    private String password;

    /**
     * Konstruktor Akun.
     *
     * @param username Nama pengguna untuk login
     * @param password Kata sandi untuk login
     */
    public Akun(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Memverifikasi kecocokan kredensial login pengguna.
     *
     * @param inputUser Username yang dimasukkan
     * @param inputPass Password yang dimasukkan
     * @return true jika cocok, false jika salah
     */
    public boolean login(String inputUser, String inputPass) {
        return this.username.equals(inputUser) && this.password.equals(inputPass);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
}