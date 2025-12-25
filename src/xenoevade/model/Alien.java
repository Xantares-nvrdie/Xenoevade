/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Alien.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent enemy aliens
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import javax.imageio.ImageIO; //untuk membaca file gambar
import java.net.URL; //untuk URL resource
import java.awt.Graphics; //untuk menggambar
import java.awt.Color; //untuk warna fallback

public class Alien extends Entity {
    private int speed; // kecepatan gerak alien

    public Alien() {
        // default constructor
        this(0, 0);
    }
    
    public Alien(int x, int y) {
        /*
         * Method Alien
         * Konstruktor untuk inisialisasi alien
         * Menerima masukan posisi awal x dan y
         */

        // Inisialisasi posisi dan ukuran (50x50 px)
        super(x, y, 50, 50);
        this.speed = 2; // Alien bergerak turun dengan kecepatan 2
        loadAsset();
    }

    private void loadAsset() {
        /*
         * Method loadAsset
         * Memuat gambar sprite alien dari resources
         */
        try {
            // Mengambil resource dari classpath (Kompatibel dengan JAR)
            // Pastikan file 'alien.png' ada di folder assets
            URL url = getClass().getResource("/assets/alien.png");

            if (url == null) {
                // Jika tidak ditemukan, tampilkan pesan error
                System.err.println("Asset tidak ditemukan: /assets/alien.png");
                return;
            }
            // Membaca gambar dari URL resource
            this.sprite = ImageIO.read(url);

        } catch (Exception e) {
            // Menangani error saat memuat gambar
            System.err.println("Gagal memuat aset alien: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * Logika pergerakan alien (otomatis turun ke bawah)
         */
        y += speed;
    }

    @Override
    public void render(Graphics g) {
        /*
         * Method render
         * Menampilkan visual alien pada koordinat aktif
         */
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            // Visual fallback (jika sprite gagal load)
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
}
