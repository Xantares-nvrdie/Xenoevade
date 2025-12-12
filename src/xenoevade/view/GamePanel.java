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

public class GamePanel extends JPanel implements PropertyChangeListener {
    private GameVM viewModel;
    private MainFrame mainFrame;

    public GamePanel(MainFrame mainFrame, String username) {
        /* Method GamePanel
        Konstruktor untuk inisialisasi panel game*/

        this.mainFrame = mainFrame;
        this.viewModel = new GameVM(username);

        //mvvm binding untuk perubahan data
        this.viewModel.addPropertyChangeListener(this);

        this.setBackground(new Color(20, 02, 40));
        this.setFocusable(true);

        setupInput();

        this.viewModel.startGame(); //memulai game

    }

    private void setupInput() {
        /* Method setupInput
        Method untuk mengatur input keyboard*/

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();
                
                //Kontrol Gerak
                if(k == KeyEvent.VK_LEFT) viewModel.movePlayer(-15, 0);
                if(k == KeyEvent.VK_RIGHT) viewModel.movePlayer(15, 0);
                if(k == KeyEvent.VK_UP) viewModel.movePlayer(0, -15);
                if(k == KeyEvent.VK_DOWN) viewModel.movePlayer(0, 15);
                
                //Kontrol Tembak (Z)
                if(k == KeyEvent.VK_Z) viewModel.playerShoot();;
                
                //Kontrol Keluar/Pause (Space)
                if(k == KeyEvent.VK_SPACE) {
                    viewModel.stopGame(true); //simpan data
                    mainFrame.showMenu(); //kembali ke menu
                }
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
        } else if ("gameover".equals(evt.getPropertyName())) {
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

        // 1. Gambar Player (Cyan)
        g.setColor(Color.CYAN);
        Entity p = viewModel.getPlayer();
        g.fillRect(p.x, p.y, p.width, p.height);

        // 2. Gambar Aliens (Merah)
        g.setColor(Color.RED);
        for (Entity a : viewModel.getAliens()) {
            g.fillOval(a.x, a.y, a.width, a.height);
        }

        // 3. Gambar Batu/Obstacle (Abu-abu)
        g.setColor(Color.GRAY);
        for (Entity o : viewModel.getObstacles()) {
            g.fillRect(o.x, o.y, o.width, o.height);
        }

        // 4. Gambar Peluru Player (Kuning)
        g.setColor(Color.YELLOW);
        for (Entity b : viewModel.getPlayerBullets()) {
            g.fillOval(b.x, b.y, b.width, b.height);
        }

        // 5. Gambar Peluru Alien (Hijau)
        g.setColor(Color.GREEN);
        for (Entity b : viewModel.getAlienBullets()) {
            g.fillOval(b.x, b.y, b.width, b.height);
        }

        // 6. Gambar HUD (Skor & Status)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Score: " + viewModel.getScore(), 20, 30);
        g.drawString("Ammo: " + viewModel.getAmmo(), 20, 50);
        g.drawString("Missed: " + viewModel.getMissed(), 20, 70);
        g.drawString("[Space] to Quit", 650, 30);
    }
}
