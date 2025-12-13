/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Player.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent the player spaceship
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import javax.imageio.ImageIO; //untuk membaca file gambar

public class Player extends Entity {
    private int velX; // kecepatan horizontal
    private int velY; // kecepatan vertikal
    private int speed = 5; // kecepatan gerak player
    public Player(int x, int y) {
        /*
         * Method Player
         * Konstruktor untuk inisialisasi player
         * Menerima masukan posisi awal x dan y
         */

        // Memanggil konstruktor Entity (x, y, width, height)
        // Sesuaikan 64, 64 dengan ukuran asli gambar player.png Anda
        super(x, y, 64, 64);

        this.velX = 0;
        this.velY = 0;
        loadAsset();
    }

    private void loadAsset() {
        /*
         * Method loadAsset
         * Method private untuk memuat gambar sprite player
         */
        try {
            // Mengambil gambar dari folder assets
            this.sprite = ImageIO.read(getClass().getResource("/assets/player.png"));
        } catch (Exception e) {
            System.err.println("Error loading player sprite: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * Logika pergerakan player berdasarkan input user
         */

        x += velX;
        y += velY;

        // Logika agar player tidak keluar dari layar (Asumsi layar 800x600)
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (x > 800 - width)
            x = 800 - width;
        if (y > 600 - height)
            y = 600 - height;
    }
    
    // Method baru untuk mengatur arah gerak
    public void setDirection(boolean up, boolean down, boolean left, boolean right) {
        // Reset kecepatan
        velX = 0;
        velY = 0;

        // Set kecepatan berdasarkan tombol yang aktif
        if (up)
            velY = -speed;
        if (down)
            velY = speed;
        if (left)
            velX = -speed;
        if (right)
            velX = speed;
    }

    // Setter untuk kontrol keyboard
    public void setVelX(int velX) {
        this.velX = velX;
    }

    public void setVelY(int velY) {
        this.velY = velY;
    }
}
