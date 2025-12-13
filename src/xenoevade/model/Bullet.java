/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Bullet.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent projectiles (Player and Alien bullets)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import javax.imageio.ImageIO;

public class Bullet extends Entity {
    private int speed; // kecepatan peluru
    private boolean isPlayerBullet; // penanda kepemilikan peluru

    public Bullet(int x, int y, boolean isPlayerBullet) {
        /*
         * Method Bullet
         * Konstruktor untuk inisialisasi peluru
         * Menerima: x, y, dan status apakah ini peluru player (true) atau alien (false)
         */

        // Ukuran peluru diasumsikan kecil (misal 10x20)
        super(x, y, 10, 20);
        this.isPlayerBullet = isPlayerBullet;

        setupBullet();
    }

    private void setupBullet() {
        /*
         * Method setupBullet
         * Mengatur gambar dan arah gerak berdasarkan pemilik peluru
         */

        try {
            if (isPlayerBullet) {
                // Jika peluru player: Load gambar biru, gerak ke ATAS (negatif Y)
                this.sprite = ImageIO.read(getClass().getResource("/assets/bullet_player.png"));
                this.speed = -10;
            } else {
                // Jika peluru alien: Load gambar merah, gerak ke BAWAH (positif Y)
                this.sprite = ImageIO.read(getClass().getResource("/assets/bullet_alien.png"));
                this.speed = 5;
            }
        } catch (Exception e) {
            System.err.println("Error loading bullet sprite: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * Mengupdate posisi peluru berdasarkan kecepatan
         */

        y += speed;
    }

    public boolean isPlayerBullet() {
        /*
         * Method isPlayerBullet
         * Mengembalikan true jika peluru milik player
         */
        return isPlayerBullet;
    }
}
