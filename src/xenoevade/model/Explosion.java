/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Explosion.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent animated explosion effect using sprite strip
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.awt.Image; //untuk variabel penampung gambar
import java.awt.image.BufferedImage; //untuk manipulasi gambar
import java.net.URL; //untuk URL resource
import javax.imageio.ImageIO; //untuk membaca file gambar

public class Explosion extends Entity {
    // konstanta konfigurasi animasi
    private final int FRAME_WIDTH = 40;
    private final int FRAME_HEIGHT = 40;
    private final int TOTAL_FRAMES = 8;
    private final int ANIMATION_SPEED = 3;

    // variabel state animasi
    private Image[] animationFrames;
    private int currentFrameIndex;
    private int tickCounter;
    private boolean finished;

    public Explosion(int x, int y) {
        /*
         * Method Explosion
         * konstruktor untuk inisialisasi efek ledakan
         */

        // inisialisasi entity dengan ukuran frame
        super(x, y, 40, 40);

        this.animationFrames = new Image[TOTAL_FRAMES];
        this.currentFrameIndex = 0;
        this.tickCounter = 0;
        this.finished = false;

        loadAssets();
    }

    private void loadAssets() {
        /*
         * Method loadAssets
         * memuat sprite sheet dan memotongnya menjadi frame animasi
         */
        try {
            // ambil resource dari classpath
            URL url = getClass().getResource("/assets/explosion_strip.png");
            if (url == null)
                return;

            BufferedImage sheet = ImageIO.read(url);

            // looping untuk memotong gambar (sprite sheet slicing)
            for (int i = 0; i < TOTAL_FRAMES; i++) {
                int xPos = i * FRAME_WIDTH;
                animationFrames[i] = sheet.getSubimage(xPos, 0, FRAME_WIDTH, FRAME_HEIGHT);
            }

            // set frame pertama sebagai tampilan awal
            this.sprite = animationFrames[0];

        } catch (Exception e) {
            System.err.println("gagal memuat animasi ledakan: " + e.getMessage());
            this.finished = true;
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * mengatur kecepatan dan pergantian frame animasi
         */

        if (finished)
            return;

        tickCounter++;

        // ganti frame berdasarkan kecepatan animasi
        if (tickCounter >= ANIMATION_SPEED) {
            tickCounter = 0;
            currentFrameIndex++;

            if (currentFrameIndex >= TOTAL_FRAMES) {
                // jika frame habis, animasi selesai
                finished = true;
                currentFrameIndex = TOTAL_FRAMES - 1;
            } else {
                // update gambar sprite ke frame selanjutnya
                this.sprite = animationFrames[currentFrameIndex];
            }
        }
    }

    public boolean isFinished() {
        /*
         * Method isFinished
         * getter status apakah animasi sudah selesai
         */
        return finished;
    }
}
