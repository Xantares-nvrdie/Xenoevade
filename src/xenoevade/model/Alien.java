/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Alien.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent enemy aliens
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import javax.imageio.ImageIO;

public class Alien extends Entity {
    private int speed; // kecepatan gerak alien

    public Alien(int x, int y) {
        /*
         * Method Alien
         * Konstruktor untuk inisialisasi alien
         * Menerima masukan posisi awal x dan y
         */

        // Sesuaikan 50, 50 dengan ukuran asli gambar alien.png
        super(x, y, 50, 50);
        this.speed = 2; // Alien bergerak turun dengan kecepatan 2
        loadAsset();
    }

    private void loadAsset() {
        /* Method loadAsset dengan DEBUGGING */
        System.out.println("=== DEBUGGING PLAYER ASSET ===");

        // Coba 1: Menggunakan getResource (Standard)
        java.net.URL url = getClass().getResource("/assets/player.png");
        

        if (url != null) {
            try {
                this.sprite = ImageIO.read(url);
                
                return;
            } catch (Exception e) {
                System.err.println("-> GAGAL baca file classpath: " + e.getMessage());
            }
        }

        // Coba 2: Menggunakan File Langsung (Jurus Darurat untuk Localhost)
        // Ini mencari langsung ke folder src di macbook anda
        try {
            java.io.File file = new java.io.File("src/assets/player.png");
            if (file.exists()) {
                this.sprite = ImageIO.read(file);
                
            } else {
                System.out.println("-> GAGAL: File tidak ditemukan di path: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("-> Error baca file direct: " + e.getMessage());
        }
        System.out.println("===============================");
    }

    @Override
    public void update() {
        /*
         * Method update
         * Logika pergerakan alien (otomatis turun ke bawah)
         */

        y += speed;
    }
}
