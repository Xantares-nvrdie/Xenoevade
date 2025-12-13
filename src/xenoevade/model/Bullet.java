/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Bullet.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent projectiles (Player and Alien bullets)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage; // Perlu ini untuk ambil width/height gambar
import java.io.IOException;

public class Bullet extends Entity {
    private boolean isPlayerBullet;

    // Kecepatan vektor (double agar bisa miring halus)
    private double velX;
    private double velY;

    // "Shadow Coordinates": Variabel double untuk hitungan presisi
    // Kita butuh ini karena Entity.x dan Entity.y tipenya int
    private double preciseX;
    private double preciseY;

    public Bullet(int x, int y, double velX, double velY, boolean isPlayer) {
        /*
         * Method Bullet
         * Konstruktor utama
         */

        // 1. Panggil super() kosong karena di Entity tidak ada constructor(int, int)
        super();

        // 2. Set posisi awal ke Entity (int)
        this.x = x;
        this.y = y;

        // 3. Set posisi presisi (double) untuk perhitungan gerak
        this.preciseX = x;
        this.preciseY = y;

        // 4. Set properti peluru
        this.velX = velX;
        this.velY = velY;
        this.isPlayerBullet = isPlayer;

        // 5. Load gambar dan set ukuran (width/height) entity
        setupSprite();
    }

    private void setupSprite() {
        /*
         * Method setupSprite
         * Memuat gambar dan mengatur ukuran peluru
         */
        try {
            BufferedImage img;

            if (isPlayerBullet) {
                img = ImageIO.read(getClass().getResource("/assets/bullet_player.png"));

                // OPSI 1: Set ukuran MANUAL (Hardcode) biar pas
                this.sprite = img;
                this.width = 12; // Ubah angka ini sesuai selera (lebar)
                this.height = 24; // Ubah angka ini sesuai selera (tinggi)

            } else {
                img = ImageIO.read(getClass().getResource("/assets/bullet_alien.png"));

                // Peluru Alien juga bisa diatur ukurannya
                this.sprite = img;
                this.width = 15;
                this.height = 15;
            }

            /*
             * * OPSI 2 (Alternatif): Jika ingin ukuran mengikuti gambar tapi diperbesar 2x
             * lipat (Scale)
             * Hapus bagian OPSI 1 di atas jika ingin pakai cara ini:
             * * int scaleFactor = 3; // Perbesar 3 kali lipat
             * this.width = img.getWidth() * scaleFactor;
             * this.height = img.getHeight() * scaleFactor;
             */

        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            System.err.println("Error loading bullet sprite: " + e.getMessage());
            // Fallback ukuran jika gambar gagal load
            this.width = 10;
            this.height = 20;
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * Mengupdate posisi menggunakan kalkulasi double, lalu setor ke int
         */

        // 1. Hitung posisi baru di variabel double (presisi)
        preciseX += velX;
        preciseY += velY;

        // 2. Update posisi asli Entity (casting ke int)
        // Ini yang akan dipakai oleh render() dan getBounds() di Entity
        this.x = (int) preciseX;
        this.y = (int) preciseY;
    }

    public boolean isPlayerBullet() {
        return isPlayerBullet;
    }
}
