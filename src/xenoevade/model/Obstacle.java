/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Obstacle.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent obstacles with HP display
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.awt.Color; //untuk warna teks
import java.awt.Font; //untuk font teks
import java.awt.FontMetrics; //untuk mengukur teks
import java.awt.Graphics; //untuk menggambar
import java.net.URL; //untuk URL resource
import javax.imageio.ImageIO; //untuk membaca file gambar

public class Obstacle extends Entity {
    private int hp; // health points obstacle
    private final int MAX_HP = 50; // nilai hp maksimum

    public Obstacle() {
        // default constructor
        this(0, 0);
    }

    public Obstacle(int x, int y) {
        /*
         * Method Obstacle
         * konstruktor untuk inisialisasi obstacle
         */

        // inisialisasi ukuran default 50x50
        super(x, y, 50, 50);
        this.hp = MAX_HP;
        loadAsset();
    }

    private void loadAsset() {
        /*
         * Method loadAsset
         * memuat gambar sprite obstacle dari resources
         */
        try {
            URL url = getClass().getResource("/assets/obstacle.png");
            if (url == null)
                return;

            this.sprite = ImageIO.read(url);

            // paksa ukuran menjadi 50x50 untuk konsistensi render
            this.width = 50;
            this.height = 50;

        } catch (Exception e) {
            System.err.println("gagal memuat obstacle: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * kosong karena obstacle bersifat statis (diam)
         */
    }

    @Override
    public void render(Graphics g) {
        /*
         * Method render
         * override untuk menampilkan sprite dan teks hp di tengah
         */

        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            // Fallback visual jika aset gagal load (opsional, untuk debugging)
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, width, height);
        }

        // konfigurasi tampilan teks
        g.setColor(Color.WHITE);
        Font font = new Font("SansSerif", Font.BOLD, 14);
        g.setFont(font);

        String hpText = String.valueOf(hp);
        FontMetrics metrics = g.getFontMetrics(font);

        // kalkulasi posisi tengah (center alignment)
        int textWidth = metrics.stringWidth(hpText);
        int textX = x + (width - textWidth) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(hpText, textX, textY);
    }

    public boolean takeDamage(int damage) {
        /*
         * Method takeDamage
         * mengurangi hp dan mengembalikan status hancur
         */
        this.hp -= damage;
        return this.hp <= 0;
    }

    public void reset(int newX, int newY) {
        /*
         * Method reset
         * mengembalikan kondisi obstacle di posisi baru (respawn)
         */
        this.x = newX;
        this.y = newY;
        this.hp = MAX_HP;
    }

    public int getHp() {
        return hp;
    }
}
