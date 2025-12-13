/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: GamePanel.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Game Canvas View (Painting & Input)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import xenoevade.model.Entity; //untuk ambil data posisi
import xenoevade.viewmodel.GameVM; //otak permainan
import javax.swing.*; //gui components
import java.awt.*; //graphics painting
import java.awt.event.KeyAdapter; //input keyboard
import java.awt.event.KeyEvent; //kode tombol
import java.beans.PropertyChangeEvent; //listener
import java.beans.PropertyChangeListener; //listener interface
import java.io.File;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements PropertyChangeListener {
    private GameVM viewModel;
    private MainFrame mainFrame;
    private Image backgroundImage; // Tambahan: Background Image


    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;

    public GamePanel(MainFrame mainFrame, String username) {
        /*
         * Method GamePanel
         * Konstruktor untuk inisialisasi panel game
         */

        this.mainFrame = mainFrame;
        this.viewModel = new GameVM(username);

        // mvvm binding untuk perubahan data
        this.viewModel.addPropertyChangeListener(this);

        this.setBackground(new Color(20, 2, 40)); // Warna background cadangan
        this.setFocusable(true);

        loadBackground(); // Load background image
        setupInput();

        this.viewModel.startGame(); // memulai game
    }

    private void loadBackground() {
        try {
            // Opsional: Jika punya background.png di assets
            java.net.URL url = getClass().getResource("/assets/background.png");
            if (url != null) {
                this.backgroundImage = ImageIO.read(url);
            }
        } catch (Exception e) {
            // Ignore error if no background
        }
    }

    private void setupInput() {
        /*
         * Method setupInput
         * Menggunakan logika boolean agar gerakan smooth tanpa delay OS
         */

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();

                // Saat tombol DITEKAN, set flag jadi true
                if (k == KeyEvent.VK_LEFT)
                    isLeft = true;
                if (k == KeyEvent.VK_RIGHT)
                    isRight = true;
                if (k == KeyEvent.VK_UP)
                    isUp = true;
                if (k == KeyEvent.VK_DOWN)
                    isDown = true;

                // Update ke ViewModel
                viewModel.updatePlayerInput(isUp, isDown, isLeft, isRight);

                // Aksi sekali tekan (seperti menembak) tetap di sini
                if (k == KeyEvent.VK_Z)
                    viewModel.playerShoot();

                if (k == KeyEvent.VK_SPACE) {
                    viewModel.stopGame(true);
                    mainFrame.showMenu();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int k = e.getKeyCode();

                // Saat tombol DILEPAS, set flag jadi false
                if (k == KeyEvent.VK_LEFT)
                    isLeft = false;
                if (k == KeyEvent.VK_RIGHT)
                    isRight = false;
                if (k == KeyEvent.VK_UP)
                    isUp = false;
                if (k == KeyEvent.VK_DOWN)
                    isDown = false;

                // Update ke ViewModel agar player berhenti
                viewModel.updatePlayerInput(isUp, isDown, isLeft, isRight);
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        /*
         * Method propertyChange (Observer)
         * Dipanggil otomatis oleh GameVM saat ada update logika/render
         */

        if ("render".equals(evt.getPropertyName())) {
            // Gambar ulang layar (Thread Safe)
            SwingUtilities.invokeLater(this::repaint);
        } else if ("gameOver".equals(evt.getPropertyName())) { // Perbaikan: case sensitive "gameOver"
            // Tampilkan pesan game over
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "GAME OVER! Kamu tertembak.");
                mainFrame.showMenu();
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        /*
         * Method paintComponent
         * Menggambar semua objek berdasarkan data dari ViewModel
         */

        super.paintComponent(g); // bersihkan layar

        // 0. Gambar Background (Jika ada)
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // ============================================================
        // PERBAIKAN DISINI: Panggil method .render(g) milik Entity!
        // Jangan menggambar manual pakai fillRect/fillOval lagi.
        // ============================================================

        // 1. Gambar Player
        // Tidak perlu g.setColor, karena gambar PNG punya warna sendiri
        Entity p = viewModel.getPlayer();
        p.render(g); // <--- INI KUNCINYA

        // 2. Gambar Aliens
        for (Entity a : viewModel.getAliens()) {
            a.render(g); // Biarkan Alien menggambar dirinya sendiri
        }

        // 3. Gambar Batu/Obstacle
        for (Entity o : viewModel.getObstacles()) {
            o.render(g);
        }

        // 4. Gambar Peluru Player
        for (Entity b : viewModel.getPlayerBullets()) {
            b.render(g);
        }

        // 5. Gambar Peluru Alien
        for (Entity b : viewModel.getAlienBullets()) {
            b.render(g);
        }

        // 6. Gambar HUD (Skor & Status)
        g.setColor(Color.WHITE); // Kembalikan warna putih untuk teks
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Score: " + viewModel.getScore(), 20, 30);
        g.drawString("Ammo: " + viewModel.getAmmo(), 20, 50);
        g.drawString("Missed: " + viewModel.getMissed(), 20, 70);
        g.drawString("[Space] to Quit", 650, 30);
    }
}
