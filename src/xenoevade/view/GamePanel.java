/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: GamePanel.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Game Canvas View (Painting & Input) with Audio
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import xenoevade.audio.AudioPlayer; // Untuk pemutar audio
import xenoevade.model.Entity; // Untuk entitas game
import xenoevade.viewmodel.GameVM; // Untuk ViewModel game

import javax.swing.JPanel; // Untuk panel GUI
import javax.swing.JOptionPane; // Untuk dialog pesan
import javax.swing.SwingUtilities; // Untuk threading GUI

import java.awt.Color; // Untuk warna latar
import java.awt.Font; // Untuk font teks
import java.awt.Graphics; // Untuk menggambar grafik
import java.awt.Image; // Untuk menggambar gambar
import java.awt.event.KeyAdapter; // Untuk input keyboard
import java.awt.event.KeyEvent; // Untuk event keyboard
import java.awt.image.BufferedImage; // Untuk manipulasi gambar

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.imageio.ImageIO;


public class GamePanel extends JPanel implements PropertyChangeListener {
    private GameVM viewModel;
    private MainFrame mainFrame;
    private Image backgroundImage;

    // Aset HUD
    private Image heartFullImg;
    private Image heartHalfImg;
    private Image heartEmptyImg;

    // Atribut Audio Player
    private AudioPlayer bgmPlayer; // Musik In-Game
    private AudioPlayer gameOverPlayer; // Musik Game Over
    private AudioPlayer shootPlayer;

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

        loadAssets();
        setupAudio();
        setupInput();

        this.viewModel.startGame();
    }

    private void setupAudio() {
        /*
         * Method setupAudio
         * Inisialisasi musik latar dan musik game over
         */

        // 1. Musik Gameplay
        bgmPlayer = new AudioPlayer("bgm2.wav");
        bgmPlayer.setVolume(0.0f); // Volume sedikit dikecilkan
        bgmPlayer.loop(); // Mainkan terus menerus

        // 2. Musik Game Over
        gameOverPlayer = new AudioPlayer("die.wav");
        gameOverPlayer.setVolume(0.0f);

        shootPlayer = new AudioPlayer("shoot.wav");
        shootPlayer.setVolume(0.0f); // Volume biasanya lebih kecil biar ga berisik
    }

    private void loadAssets() {
        /* Method loadAssets */
        try {
            java.net.URL bgUrl = getClass().getResource("/assets/background.png");
            if (bgUrl != null)
                this.backgroundImage = ImageIO.read(bgUrl);

            java.net.URL heartSheetUrl = getClass().getResource("/assets/hearts.png");
            if (heartSheetUrl != null) {
                BufferedImage sheet = ImageIO.read(heartSheetUrl);
                int cellWidth = sheet.getWidth() / 3;
                int cellHeight = sheet.getHeight();

                heartFullImg = sheet.getSubimage(0, 0, cellWidth, cellHeight);
                heartHalfImg = sheet.getSubimage(cellWidth, 0, cellWidth, cellHeight);
                heartEmptyImg = sheet.getSubimage(cellWidth * 2, 0, cellWidth, cellHeight);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat aset visual: " + e.getMessage());
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
                    stopMusicAndExit();
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

    private void stopMusicAndExit() {
        /* Helper untuk stop semua musik sebelum ganti layar */
        if (bgmPlayer != null)
            bgmPlayer.stop();
        if (gameOverPlayer != null)
            gameOverPlayer.stop();

        viewModel.stopGame(true);
        mainFrame.showMenu();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("render".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(this::repaint);

        } else if ("gameOver".equals(evt.getPropertyName())) {

            // --- LOGIKA MUSIK GAME OVER ---

            // 1. Matikan musik gameplay
            if (bgmPlayer != null)
                bgmPlayer.stop();

            // 2. Mainkan musik game over
            if (gameOverPlayer != null) {
                gameOverPlayer.play();
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "GAME OVER! Misi Gagal.");

                // Matikan musik game over setelah user menekan OK di dialog
                if (gameOverPlayer != null)
                    gameOverPlayer.stop();

                mainFrame.showMenu();
            });
        } else if ("sfx_shoot".equals(evt.getPropertyName())) { // [BARU]
            // Mainkan suara tembak
            if (shootPlayer != null) {
                shootPlayer.play();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        /* Method paintComponent */
        super.paintComponent(g);

        // Render Background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(20, 2, 40));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Render Entities
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

        drawHUD(g);
    }

    private void drawHUD(Graphics g) {
        /* Method drawHUD */

        int hp = 0;
        int maxHp = 100;
        if (viewModel.getPlayer() instanceof xenoevade.model.Player) {
            xenoevade.model.Player player = (xenoevade.model.Player) viewModel.getPlayer();
            hp = player.getHp();
            maxHp = player.getMaxHp();
        }

        int heartsTotal = maxHp / 20;
        int startX = 20;
        int startY = 20;
        int heartSize = 30;
        int padding = 5;

        for (int i = 0; i < heartsTotal; i++) {
            int heartThreshold = (i + 1) * 20;
            Image imgToDraw;

            if (hp >= heartThreshold) {
                imgToDraw = heartFullImg;
            } else if (hp >= heartThreshold - 10) {
                imgToDraw = heartHalfImg;
            } else {
                imgToDraw = heartEmptyImg;
            }

            int drawX = startX + (i * (heartSize + padding));
            if (imgToDraw != null) {
                g.drawImage(imgToDraw, drawX, startY, heartSize, heartSize, null);
            } else {
                g.setColor(Color.RED);
                g.drawRect(drawX, startY, heartSize, heartSize);
            }
        }

        String scoreText = "SCORE: " + viewModel.getScore();
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.setColor(Color.CYAN);
        g.drawString(scoreText, getWidth() - scoreWidth - 20, 40);

        String ammoText = "AMMO: " + viewModel.getAmmo();
        g.setColor(Color.GREEN);
        g.drawString(ammoText, getWidth() - 150, getHeight() - 20);
    }
}
