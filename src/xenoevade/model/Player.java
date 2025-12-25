/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Player.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Class to represent the player spaceship
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;
import java.awt.Color; //untuk warna fallback
import java.awt.Graphics; //untuk menggambar
import java.awt.Image; //untuk variabel penampung gambar
import java.net.URL; //untuk URL resource
import javax.imageio.ImageIO; //untuk membaca file gambar

public class Player extends Entity {
    // variabel pergerakan
    private int velX, velY;
    private int speed = 5;

    // variabel aset gambar untuk animasi sederhana
    private Image imgCenter;
    private Image imgLeft;
    private Image imgRight;

    // variabel status pemain
    private int hp;
    private final int MAX_HP = 100;

    public Player() {
        this(0, 0);
    }

    public Player(int x, int y) {
        /*
         * Method Player
         * konstruktor untuk inisialisasi player
         */

        // inisialisasi dengan ukuran 64x64
        super(x, y, 64, 64);

        this.velX = 0;
        this.velY = 0;
        this.hp = MAX_HP;

        loadAssets();
    }

    private void loadAssets() {
        /*
         * Method loadAssets
         * memuat sprite player dan variasi arah gerak
         */
        try {
            // ambil resource dari classpath
            URL urlCenter = getClass().getResource("/assets/player.png");
            URL urlLeft = getClass().getResource("/assets/player_left.png");
            URL urlRight = getClass().getResource("/assets/player_right.png");

            // pastikan resource ditemukan sebelum diload
            if (urlCenter == null || urlLeft == null || urlRight == null) {
                System.err.println("aset player tidak lengkap");
                return;
            }

            imgCenter = ImageIO.read(urlCenter);
            imgLeft = ImageIO.read(urlLeft);
            imgRight = ImageIO.read(urlRight);

            // set tampilan awal
            this.sprite = imgCenter;

        } catch (Exception e) {
            System.err.println("gagal memuat sprite player: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        /*
         * Method update
         * mengatur posisi, animasi, dan batas layar
         */

        x += velX;
        y += velY;

        // logika animasi sprite berdasarkan arah
        if (velX < 0) {
            this.sprite = imgLeft;
        } else if (velX > 0) {
            this.sprite = imgRight;
        } else {
            this.sprite = imgCenter;
        }

        // cegah player keluar dari layar (clamp position)
        // asumsi ukuran layar game 800x600
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
         * mengubah vektor kecepatan berdasarkan input keyboard dan agar tidak patah-patah
         */

        // reset kecepatan sebelum kalkulasi baru
        velX = 0;
        velY = 0;

        // atur kecepatan berdasarkan input
        if (up)
            velY = -speed;
        if (down)
            velY = speed;
        if (left)
            velX = -speed;
        if (right)
            velX = speed;
    }

    public void takeDamage(int damage) {
        /*
         * Method takeDamage
         * mengurangi hp player
         */
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }
    
    @Override
    public void render(Graphics g) {
        /*
         * Method render
         * Menggambar visual player (pesawat) ke layar
         */
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            // Fallback visual warna biru jika sprite gagal dimuat
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }
    }

    public boolean isDead() {
        /*
         * Method isDead
         * cek apakah hp habis
         */
        return this.hp <= 0;
    }

    public int getHp() {
        return this.hp;
    }

    public int getMaxHp() {
        return MAX_HP;
    }

    public void setVelX(int velX) {
        this.velX = velX;
    }

    public void setVelY(int velY) {
        this.velY = velY;
    }
}
