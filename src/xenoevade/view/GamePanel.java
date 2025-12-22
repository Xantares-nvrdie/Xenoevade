/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: GamePanel.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Game Canvas View (Painting & Input) with Sprite Sheet HUD
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import xenoevade.model.Entity; //untuk ambil data posisi
import xenoevade.viewmodel.GameVM; //otak permainan
import javax.swing.*; //gui components
import java.awt.*; //graphics painting
import java.awt.event.KeyAdapter; //input keyboard
import java.awt.event.KeyEvent; //kode tombol
import java.awt.image.BufferedImage; // BARU: Butuh BufferedImage untuk slicing
import java.beans.PropertyChangeEvent; //listener
import java.beans.PropertyChangeListener; //listener interface
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements PropertyChangeListener {
    private GameVM viewModel;
    private MainFrame mainFrame;
    private Image backgroundImage;

    // BARU: Variabel terpisah untuk 3 kondisi hati
    private Image heartFullImg;
    private Image heartHalfImg;
    private Image heartEmptyImg;

    // Flag input keyboard
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;

    public GamePanel(MainFrame mainFrame, String username) {
        /* Method GamePanel */
        this.mainFrame = mainFrame;
        this.viewModel = new GameVM(username);

        this.viewModel.addPropertyChangeListener(this);
        this.setBackground(new Color(20, 2, 40));
        this.setFocusable(true);

        loadAssets(); // Load background dan slice aset hati
        setupInput();

        this.viewModel.startGame();
    }

    private void loadAssets() {
        /*
         * Method loadAssets (Updated)
         * Memuat background dan memotong sprite sheet hati
         */
        try {
            // Load Background
            java.net.URL bgUrl = getClass().getResource("/assets/background.png");
            if (bgUrl != null) {
                this.backgroundImage = ImageIO.read(bgUrl);
            }

            // --- BARU: LOGIKA SLICING SPRITE SHEET HATI ---
            // Pastikan nama file sheet hati Anda benar (misal: hearts.png)
            java.net.URL heartSheetUrl = getClass().getResource("/assets/hearts.png");

            if (heartSheetUrl != null) {
                // Baca sebagai BufferedImage agar bisa dipotong
                BufferedImage sheet = ImageIO.read(heartSheetUrl);

                // Asumsi sheet terdiri dari 3 gambar berjejer horizontal
                int cellWidth = sheet.getWidth() / 3; // Lebar satu sel hati
                int cellHeight = sheet.getHeight(); // Tinggi satu sel hati

                // Potong gambar menggunakan getSubimage(x, y, width, height)
                // Hati Penuh (Indeks 0)
                heartFullImg = sheet.getSubimage(0, 0, cellWidth, cellHeight);
                // Hati Setengah (Indeks 1 -> geser x sejauh 1 cellWidth)
                heartHalfImg = sheet.getSubimage(cellWidth, 0, cellWidth, cellHeight);
                // Hati Kosong (Indeks 2 -> geser x sejauh 2 cellWidth)
                heartEmptyImg = sheet.getSubimage(cellWidth * 2, 0, cellWidth, cellHeight);
            }
            // ----------------------------------------------

        } catch (Exception e) {
            System.err.println("Gagal memuat aset visual: " + e.getMessage());
            e.printStackTrace(); // Print stack trace biar tau error detailnya
        }
    }

    private void setupInput() {
        /* Method setupInput */
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_A)
                    isLeft = true;
                if (k == KeyEvent.VK_D)
                    isRight = true;
                if (k == KeyEvent.VK_W)
                    isUp = true;
                if (k == KeyEvent.VK_S)
                    isDown = true;
                viewModel.updatePlayerInput(isUp, isDown, isLeft, isRight);

                if (k == KeyEvent.VK_SPACE)
                    viewModel.playerShoot();
                if (k == KeyEvent.VK_ENTER) {
                    viewModel.stopGame(true);
                    mainFrame.showMenu();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_A)
                    isLeft = false;
                if (k == KeyEvent.VK_D)
                    isRight = false;
                if (k == KeyEvent.VK_W)
                    isUp = false;
                if (k == KeyEvent.VK_S)
                    isDown = false;
                viewModel.updatePlayerInput(isUp, isDown, isLeft, isRight);
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        /* Method propertyChange (Observer) */
        if ("render".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(this::repaint);
        } else if ("gameOver".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "GAME OVER! Misi Gagal.");
                mainFrame.showMenu();
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        /* Method paintComponent */
        super.paintComponent(g);

        // 0. Gambar Background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(20, 2, 40));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // RENDER ENTITAS GAME
        Entity p = viewModel.getPlayer();
        if (p != null)
            p.render(g);

        synchronized (viewModel.getAliens()) {
            for (Entity a : viewModel.getAliens())
                a.render(g);
        }
        for (Entity o : viewModel.getObstacles()) {
            o.render(g);
        }
        synchronized (viewModel.getPlayerBullets()) {
            for (Entity b : viewModel.getPlayerBullets())
                b.render(g);
        }
        synchronized (viewModel.getAlienBullets()) {
            for (Entity b : viewModel.getAlienBullets())
                b.render(g);
        }
        synchronized (viewModel.getExplosions()) {
            for (Entity ex : viewModel.getExplosions())
                ex.render(g);
        }

        // 7. Gambar HUD (Heads-Up Display)
        drawHUD(g);
    }

    private void drawHUD(Graphics g) {
        /*
         * Method drawHUD (Updated for Heart Container Display)
         * Menggambar deretan hati berdasarkan HP
         */

        // --- TAMPILAN NYAWA (HEART CONTAINER) ---
        int hp = 0;
        int maxHp = 100; // Default fallback

        if (viewModel.getPlayer() instanceof xenoevade.model.Player) {
            xenoevade.model.Player player = (xenoevade.model.Player) viewModel.getPlayer();
            hp = player.getHp();
            maxHp = player.getMaxHp();
        }

        // Konfigurasi Tampilan Hati
        int heartsTotal = maxHp / 20; // Asumsi 1 hati = 20 HP (Total 5 hati untuk 100HP)
        int startX = 20; // Posisi X mulai menggambar
        int startY = 20; // Posisi Y mulai menggambar
        int heartSize = 30; // Ukuran gambar hati di layar
        int padding = 5; // Jarak antar hati

        // Loop untuk menggambar setiap slot hati
        for (int i = 0; i < heartsTotal; i++) {
            // Nilai HP di ambang batas hati ke-(i+1)
            // Misal i=0 (hati pertama), threshold = 20. i=1 (hati kedua), threshold = 40.
            int heartThreshold = (i + 1) * 20;

            // Tentukan gambar mana yang dipakai
            Image imgToDraw;

            if (hp >= heartThreshold) {
                // Jika HP di atas ambang batas, gambar hati penuh
                imgToDraw = heartFullImg;
            } else if (hp >= heartThreshold - 10) {
                // Jika HP di antara setengah dan penuh (misal HP 30, threshold hati ke-2 adalah
                // 40. 30 >= 40-10)
                imgToDraw = heartHalfImg;
            } else {
                // Sisanya kosong
                imgToDraw = heartEmptyImg;
            }

            // Hitung posisi X untuk hati saat ini
            int drawX = startX + (i * (heartSize + padding));

            // Gambar hati (dengan fallback kotak merah jika gambar gagal load)
            if (imgToDraw != null) {
                g.drawImage(imgToDraw, drawX, startY, heartSize, heartSize, null);
            } else {
                g.setColor(Color.RED);
                g.drawRect(drawX, startY, heartSize, heartSize); // Gambar kotak kosong sebagai penanda error
            }
        }
        // -------------------------------------------------------

        // --- TAMPILAN SKOR (Pojok Kanan Atas) ---
        String scoreText = "SCORE: " + viewModel.getScore();
        g.setFont(new Font("Monospaced", Font.BOLD, 18)); // Set font dulu untuk perhitungan width
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.setColor(Color.CYAN);
        g.drawString(scoreText, getWidth() - scoreWidth - 20, 40);

        // --- TAMPILAN AMMO (Pojok Kanan Bawah) ---
        String ammoText = "AMMO: " + viewModel.getAmmo();
        g.setColor(Color.GREEN);
        g.drawString(ammoText, getWidth() - 150, getHeight() - 20);
    }
}
