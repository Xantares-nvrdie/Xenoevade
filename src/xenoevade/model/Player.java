/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Player.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent the player spaceship
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.awt.Image; //untuk variabel penampung gambar
import javax.imageio.ImageIO; //untuk membaca file gambar

public class Player extends Entity {
    private int velX; // kecepatan horizontal
    private int velY; // kecepatan vertikal
    private int speed = 5; // kecepatan gerak player

    // Atribut untuk menyimpan variasi aset gambar
    private Image imgCenter;
    private Image imgLeft;
    private Image imgRight;

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
        loadAssets(); // memuat semua variasi gambar
    }

    private void loadAssets() {
        /*
         * Method loadAssets
         * Method private untuk memuat gambar sprite player (kiri, kanan, tengah)
         */
        try {
            // Mengambil gambar dari folder assets
            imgCenter = ImageIO.read(getClass().getResource("/assets/player.png"));
            imgLeft = ImageIO.read(getClass().getResource("/assets/player_left.png"));
            imgRight = ImageIO.read(getClass().getResource("/assets/player_right.png"));

            // Set gambar default (posisi lurus)
            this.sprite = imgCenter;

        } catch (Exception e) {
            System.err.println("Error loading player sprite: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * Logika pergerakan player dan animasi sprite berdasarkan input user
         */

        x += velX;
        y += velY;

        // Logika pergantian gambar berdasarkan arah gerak (Animasi)
        if (velX < 0) {
            this.sprite = imgLeft; // Jika gerak ke kiri, pakai gambar miring kiri
        } else if (velX > 0) {
            this.sprite = imgRight; // Jika gerak ke kanan, pakai gambar miring kanan
        } else {
            this.sprite = imgCenter; // Jika diam/gerak vertikal, pakai gambar lurus
        }

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

    public void setDirection(boolean up, boolean down, boolean left, boolean right) {
        /*
         * Method setDirection
         * Method untuk mengatur arah gerak player berdasarkan input keyboard
         */

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
