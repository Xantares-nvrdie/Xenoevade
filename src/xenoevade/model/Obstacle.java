/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Obstacle.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent obstacles (rocks/meteors)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import javax.imageio.ImageIO; //untuk membaca file gambar

public class Obstacle extends Entity {

    public Obstacle(int x, int y) {
        /*
         * Method Obstacle
         * Konstruktor untuk inisialisasi obstacle
         * Menerima masukan posisi awal x dan y
         */

        // Memanggil konstruktor Entity (x, y, width, height)
        // Ukuran diatur 50x50, sesuaikan dengan aset gambar Anda
        super(x, y, 50, 50);

        loadAsset();
    }

    private void loadAsset() {
        /*
         * Method loadAsset
         * Method private untuk memuat gambar sprite obstacle
         */

        try {
            // Mengambil gambar dari folder assets
            this.sprite = ImageIO.read(getClass().getResource("/assets/obstacle.png"));
        } catch (Exception e) {
            System.err.println("Error loading obstacle sprite: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * Obstacle bersifat statis (diam), jadi tidak ada logika pergerakan
         * Dibiarkan kosong untuk memenuhi kontrak abstract class Entity
         */
    }
}
