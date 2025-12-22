/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Obstacle.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent obstacles with HP display
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics; // untuk perhitungan dimensi teks
import java.awt.Graphics;
import javax.imageio.ImageIO;

public class Obstacle extends Entity {
    private int hp;
    private final int MAX_HP = 50; // konstanta hp maksimal

    public Obstacle(int x, int y) {
        /*
         * Method Obstacle
         * Konstruktor untuk inisialisasi obstacle
         */

        // inisialisasi dengan ukuran default sementara (50, 50)
        super(x, y, 50, 50);
        this.hp = MAX_HP;
        loadAsset();
    }

    private void loadAsset() {
        /*
         * Method loadAsset
         * Method private untuk memuat gambar sprite obstacle
         */

        try {
            this.sprite = ImageIO.read(getClass().getResource("/assets/obstacle.png"));

            // REVISI: Memaksa ukuran menjadi 50x50 (atau lebih besar) agar tidak kekecilan
            // Meskipun gambar aslinya kecil, render akan men-scale gambar ke ukuran ini
            this.width = 50;
            this.height = 50;

        } catch (Exception e) {
            System.err.println("Error loading obstacle sprite: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * Obstacle bersifat statis (diam), jadi tidak ada logika pergerakan
         */
    }

    @Override
    public void render(Graphics g) {
        /*
         * Method render
         * Override method render untuk menampilkan sprite dan teks HP di tengahnya
         */

        super.render(g); // gambar sprite batu terlebih dahulu

        // konfigurasi font untuk teks HP
        g.setColor(Color.WHITE);
        Font font = new Font("SansSerif", Font.BOLD, 14);
        g.setFont(font);

        String hpText = String.valueOf(hp);
        FontMetrics metrics = g.getFontMetrics(font); // untuk mengukur dimensi teks

        // hitung posisi X (tengah horizontal)
        int textWidth = metrics.stringWidth(hpText);
        int textX = (int) x + (width - textWidth) / 2;

        // hitung posisi Y (tengah vertikal)
        // rumus: Y_Batu + (Setengah Tinggi Batu) - (Setengah Tinggi Teks) + Ascent
        int textY = (int) y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(hpText, textX, textY);
    }

    public boolean takeDamage(int damage) {
        /*
         * Method takeDamage
         * Mengurangi HP obstacle. Mengembalikan true jika hancur.
         */
        this.hp -= damage;
        return this.hp <= 0;
    }

    public void reset(int newX, int newY) {
        /*
         * Method reset
         * Mengembalikan kondisi obstacle seperti baru di posisi lain (respawn)
         */
        this.x = newX;
        this.y = newY;
        this.hp = MAX_HP;
    }

    public int getHp() {
        return hp;
    }
}
