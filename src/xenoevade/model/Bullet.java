/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Bullet.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent projectiles (Player and Alien bullets)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import javax.imageio.ImageIO; // Untuk membaca file gambar
import java.io.IOException; // Untuk penanganan IO Exception
import java.net.URL; // Untuk URL resource
import java.awt.Graphics; // Untuk menggambar
import java.awt.Color; // Untuk warna fallback

public class Bullet extends Entity {
    // Tipe peluru: true = peluru pemain, false = peluru alien
    private boolean isPlayerBullet;

    // Kecepatan vektor
    private double velX;
    private double velY;

    // Koordinat presisi tinggi untuk kalkulasi pergerakan halus
    private double preciseX;
    private double preciseY;

    public Bullet() {
        /*
         * Method Bullet
         * Konstruktor default (posisi 0,0, peluru pemain)
         */
        this(0, 0, 0.0, -10.0, true);
    }

    public Bullet(int x, int y, double velX, double velY, boolean isPlayer) {
        /*
         * Method Bullet
         * Konstruktor utama
         */
        super(); // Memanggil konstruktor Entity parent

        // Inisialisasi posisi (Integer dan Double)
        this.x = x;
        this.y = y;
        this.preciseX = x;
        this.preciseY = y;

        // Inisialisasi properti
        this.velX = velX;
        this.velY = velY;
        this.isPlayerBullet = isPlayer;

        loadAssets();
    }

    private void loadAssets() {
        /*
         * Method loadAssets
         * Memuat sprite dan menentukan hitbox peluru
         */
        String path;
        int targetWidth;
        int targetHeight;

        // Tentukan konfigurasi aset berdasarkan tipe peluru
        if (isPlayerBullet) {
            path = "/assets/bullet_player.png";
            targetWidth = 12;
            targetHeight = 24;
        } else {
            path = "/assets/bullet_alien.png";
            targetWidth = 15;
            targetHeight = 15;
        }

        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                throw new IOException("Resource not found: " + path);
            }

            this.sprite = ImageIO.read(url);
            this.width = targetWidth;
            this.height = targetHeight;

        } catch (Exception e) {
            System.err.println("Gagal memuat sprite bullet: " + e.getMessage());
            // Fallback ukuran hitbox jika gambar gagal dimuat
            this.width = 10;
            this.height = 10;
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * Mengupdate posisi menggunakan kalkulasi double presisi
         */

        // 1. Hitung posisi baru (Double)
        preciseX += velX;
        preciseY += velY;

        /*
         * why:
         * y += velY -> y += (int) velY jadi kalo velY < 1.0 bakal selalu 0
         * sehingga peluru ga bakal gerak.misal y =100, velY=0.5 -> y=100+0=100 terus
         */

        // 2. Konversi ke posisi render (Integer)
        this.x = (int) preciseX;
        this.y = (int) preciseY;
    }

    public boolean isPlayerBullet() {
        return isPlayerBullet;
    }
    
    @Override
    public void render(Graphics g) {
        /*
         * Method render
         * Menampilkan visual peluru
         */
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            // Visual fallback sederhana
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, width, height);
        }
    }
}
