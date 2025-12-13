/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Explosion.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent animated explosion effect using sprite strip
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Explosion extends Entity {

    // --- KONFIGURASI BARU (SESUAI UKURAN ASLI GAMBAR ANDA) ---
    private final int FRAME_WIDTH = 40; // UBAH JADI 40 (Sesuai log debug)
    private final int FRAME_HEIGHT = 40; // UBAH JADI 40 (Sesuai log debug)
    private final int TOTAL_FRAMES = 8; // Tetap 8 (320 / 40 = 8)
    private final int ANIMATION_SPEED = 3;
    // ----------------------------------------------------------

    private Image[] animationFrames;
    private int currentFrameIndex;
    private int tickCounter;
    private boolean finished;

    public Explosion(int x, int y) {
        /*
         * Method Explosion
         */
        // Gunakan ukuran frame yang baru (40x40)
        super(x, y, 40, 40);

        this.animationFrames = new Image[TOTAL_FRAMES];
        this.currentFrameIndex = 0;
        this.tickCounter = 0;
        this.finished = false;

        loadAssets();
    }

    private void loadAssets() {
        try {
            // Load gambar strip
            BufferedImage sheet = ImageIO.read(getClass().getResource("/assets/explosion_strip.png"));

            for (int i = 0; i < TOTAL_FRAMES; i++) {
                int xPos = i * FRAME_WIDTH;
                // Potong sesuai ukuran 40x40
                animationFrames[i] = sheet.getSubimage(xPos, 0, FRAME_WIDTH, FRAME_HEIGHT);
            }

            this.sprite = animationFrames[0];

        } catch (Exception e) {
            System.err.println("Error loading explosion strip: " + e.getMessage());
            this.finished = true;
        }
    }

    @Override
    public void update() {
        if (finished)
            return;

        tickCounter++;
        if (tickCounter >= ANIMATION_SPEED) {
            tickCounter = 0;
            currentFrameIndex++;

            if (currentFrameIndex >= TOTAL_FRAMES) {
                finished = true;
                currentFrameIndex = TOTAL_FRAMES - 1;
            } else {
                this.sprite = animationFrames[currentFrameIndex];
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
