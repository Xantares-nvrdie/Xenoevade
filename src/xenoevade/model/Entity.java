/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Entity.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Abstract class to represent game objects (Player, Alien, etc)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.awt.Color; // PENTING: Untuk warna fallback
import java.awt.Graphics; // PENTING: Untuk menggambar
import java.awt.Image; // PENTING: Untuk menampung aset gambar
import java.awt.Rectangle; // Untuk collision detection

public abstract class Entity {
    public int x; // Posisi x entity
    public int y; // Posisi y entity
    public int width; // Lebar entity
    public int height; // Tinggi entity

    // PENTING: Atribut ini harus ada untuk menyimpan gambar yang sudah di-load
    protected Image sprite;

    public Entity() {
        /* Method Entity (Default Constructor) */
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }

    public Entity(int x, int y, int width, int height) {
        /* Method Entity (Parameterized Constructor) */
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();
    /* Method update (Abstract) */

    // ==========================================
    // BAGIAN INI YANG PALING PENTING DIUBAH
    // ==========================================
    public void render(Graphics g) {
        /*
         * Method render
         * Tugasnya: Menggambar entity ke layar.
         * Logika: Cek dulu apakah 'sprite' (gambar) sudah ada isinya?
         */

        if (sprite != null) {
            // JIKA GAMBAR ADA: Gambar pakai image tersebut
            // Parameter: gambar, posisi x, posisi y, lebar, tinggi, observer (null)
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            // JIKA GAMBAR GAGAL LOAD/KOSONG: Gambar kotak sebagai cadangan
            // Kita kasih warna abu-abu biar beda dengan merah default
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);

            // Kembalikan warna ke default (hitam) agar tidak mempengaruhi gambar lain
            g.setColor(Color.BLACK);
        }
    }
    // ==========================================

    public Rectangle getBounds() {
        /* Method getBounds for collision */
        return new Rectangle(x, y, width, height);
    }
}
