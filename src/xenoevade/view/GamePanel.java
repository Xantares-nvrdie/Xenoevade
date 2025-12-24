/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: GamePanel.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Game Canvas View (Painting & Input) with Audio
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import xenoevade.audio.AudioPlayer; //untuk pemutar audio
import xenoevade.model.Entity; //untuk referensi entitas game
import xenoevade.viewmodel.GameVM; //untuk viewmodel game

import javax.imageio.ImageIO; //untuk membaca file gambar
import javax.swing.JOptionPane; //untuk dialog pesan
import javax.swing.JPanel; //untuk panel swing
import javax.swing.SwingUtilities; //untuk utilitas swing

import java.awt.Color; //untuk warna latar
import java.awt.Font; //untuk font teks
import java.awt.Graphics; //untuk menggambar
import java.awt.Image; //untuk variabel penampung gambar
import java.awt.event.KeyAdapter; //untuk input keyboard
import java.awt.event.KeyEvent; //untuk event keyboard
import java.awt.image.BufferedImage; //untuk manipulasi gambar
import java.beans.PropertyChangeEvent; //untuk event properti
import java.beans.PropertyChangeListener; //untuk listener properti
import java.net.URL; //untuk URL resource

public class GamePanel extends JPanel implements PropertyChangeListener {
    // referensi viewmodel dan mainframe
    private GameVM viewModel;
    private MainFrame mainFrame;
    private Image backgroundImage;

    // aset hud
    private Image heartFullImg;
    private Image heartHalfImg;
    private Image heartEmptyImg;

    // atribut audio player
    private AudioPlayer bgmPlayer;
    private AudioPlayer gameOverPlayer;
    private AudioPlayer shootPlayer;

    // input flags
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;

    public GamePanel(MainFrame mainFrame, String username) {
        /*
         * Method GamePanel
         * konstruktor utama panel game
         */

        this.mainFrame = mainFrame;

        // inisialisasi viewmodel dan daftarkan listener
        this.viewModel = new GameVM(username);
        this.viewModel.addPropertyChangeListener(this);

        // setting tampilan panel dasar
        this.setBackground(new Color(20, 2, 40));
        this.setFocusable(true);

        // memanggil method persiapan
        loadAssets();
        setupAudio();
        setupInput();

        // memulai game loop di viewmodel
        this.viewModel.startGame();
    }

    private void setupAudio() {
        /*
         * Method setupAudio
         * inisialisasi musik latar, sfx tembak, dan game over
         */

        // inisialisasi musik gameplay
        bgmPlayer = new AudioPlayer("bgm2.wav");
        bgmPlayer.setVolume(0.0f); // set volume normal
        bgmPlayer.loop(); // putar secara looping

        // inisialisasi musik game over
        gameOverPlayer = new AudioPlayer("die.wav");
        gameOverPlayer.setVolume(0.0f);

        //w inisialisasi sfx tembakan
        shootPlayer = new AudioPlayer("shoot.wav");
        shootPlayer.setVolume(0.0f);
    }

    private void loadAssets() {
        /*
         * Method loadAssets
         * memuat gambar background dan elemen hud (hearts)
         */
        try {
            // memuat background image
            URL bgUrl = getClass().getResource("/assets/background.png");
            if (bgUrl != null) {
                this.backgroundImage = ImageIO.read(bgUrl);
            }

            // memuat sprite sheet untuk icon hati
            URL heartSheetUrl = getClass().getResource("/assets/hearts.png");
            if (heartSheetUrl != null) {
                BufferedImage sheet = ImageIO.read(heartSheetUrl);

                // menghitung ukuran per cell (asumsi 3 gambar sejajar)
                int cellWidth = sheet.getWidth() / 3;
                int cellHeight = sheet.getHeight();

                // potong sprite sheet menjadi bagian terpisah
                heartFullImg = sheet.getSubimage(0, 0, cellWidth, cellHeight);
                heartHalfImg = sheet.getSubimage(cellWidth, 0, cellWidth, cellHeight);
                heartEmptyImg = sheet.getSubimage(cellWidth * 2, 0, cellWidth, cellHeight);
            }
        } catch (Exception e) {
            // cetak error jika aset gagal dimuat
            System.err.println("gagal memuat aset visual: " + e.getMessage());
        }
    }

    private void setupInput() {
        /*
         * Method setupInput
         * menangani input keyboard dari user
         */

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();

                // set flag arah menjadi true saat tombol ditekan
                if (k == KeyEvent.VK_A)
                    isLeft = true;
                if (k == KeyEvent.VK_D)
                    isRight = true;
                if (k == KeyEvent.VK_W)
                    isUp = true;
                if (k == KeyEvent.VK_S)
                    isDown = true;

                // kirim update input ke viewmodel
                viewModel.updatePlayerInput(isUp, isDown, isLeft, isRight);

                // aksi menembak
                if (k == KeyEvent.VK_SPACE)
                    viewModel.playerShoot();

                // aksi keluar game
                if (k == KeyEvent.VK_ENTER) {
                    stopMusicAndExit();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int k = e.getKeyCode();

                // set flag arah menjadi false saat tombol dilepas
                if (k == KeyEvent.VK_A)
                    isLeft = false;
                if (k == KeyEvent.VK_D)
                    isRight = false;
                if (k == KeyEvent.VK_W)
                    isUp = false;
                if (k == KeyEvent.VK_S)
                    isDown = false;

                // update viewmodel agar player berhenti
                viewModel.updatePlayerInput(isUp, isDown, isLeft, isRight);
            }
        });
    }

    private void stopMusicAndExit() {
        /*
         * Method stopMusicAndExit
         * menghentikan audio dan kembali ke menu utama
         */

        // hentikan semua musik yang sedang berjalan
        if (bgmPlayer != null)
            bgmPlayer.stop();
        if (gameOverPlayer != null)
            gameOverPlayer.stop();

        // hentikan thread game dan simpan data
        viewModel.stopGame(true);

        // pindah layar ke menu
        mainFrame.showMenu();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // event render: minta swing untuk menggambar ulang layar
        if ("render".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(this::repaint);

        } else if ("gameOver".equals(evt.getPropertyName())) {
            // logika saat game over terjadi

            // matikan bgm dan putar efek kalah
            if (bgmPlayer != null)
                bgmPlayer.stop();
            if (gameOverPlayer != null)
                gameOverPlayer.play();

            // tampilkan dialog pesan di thread gui
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "GAME OVER! Misi Gagal.");

                // setelah dialog ditutup, matikan musik dan keluar
                if (gameOverPlayer != null)
                    gameOverPlayer.stop();
                mainFrame.showMenu();
            });

        } else if ("sfx_shoot".equals(evt.getPropertyName())) {
            // mainkan efek suara tembakan jika ada sinyal dari vm
            if (shootPlayer != null) {
                shootPlayer.play();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        /*
         * Method paintComponent
         * render loop utama untuk menggambar seluruh objek game
         */

        super.paintComponent(g);

        // gambar background jika ada, jika tidak pakai warna solid
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(20, 2, 40));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // ambil objek player dan gambar
        Entity p = viewModel.getPlayer();
        if (p != null)
            p.render(g);

        // render list entitas dengan sinkronisasi thread
        synchronized (viewModel.getAliens()) {
            for (Entity a : viewModel.getAliens())
                a.render(g);
        }

        // render obstacle (tidak perlu synchronized karena list statis)
        for (Entity o : viewModel.getObstacles()) {
            o.render(g);
        }

        // render peluru player
        synchronized (viewModel.getPlayerBullets()) {
            for (Entity b : viewModel.getPlayerBullets())
                b.render(g);
        }

        // render peluru alien
        synchronized (viewModel.getAlienBullets()) {
            for (Entity b : viewModel.getAlienBullets())
                b.render(g);
        }

        // render efek ledakan
        synchronized (viewModel.getExplosions()) {
            for (Entity ex : viewModel.getExplosions())
                ex.render(g);
        }

        // gambar hud (head-up display) di lapisan paling atas
        drawHUD(g);
    }

    private void drawHUD(Graphics g) {
        /*
         * Method drawHUD
         * menggambar ui di atas layer game (score, ammo, health)
         */

        int hp = 0;
        int maxHp = 100;

        // ambil data hp dari player
        if (viewModel.getPlayer() instanceof xenoevade.model.Player) {
            xenoevade.model.Player player = (xenoevade.model.Player) viewModel.getPlayer();
            hp = player.getHp();
            maxHp = player.getMaxHp();
        }

        // konfigurasi posisi awal icon hati
        int heartsTotal = maxHp / 20;
        int startX = 20;
        int startY = 20;
        int heartSize = 30;
        int padding = 5;

        // looping untuk menggambar barisan hati
        for (int i = 0; i < heartsTotal; i++) {
            int heartThreshold = (i + 1) * 20;
            Image imgToDraw;

            // tentukan gambar hati (penuh, setengah, atau kosong)
            if (hp >= heartThreshold) {
                imgToDraw = heartFullImg;
            } else if (hp >= heartThreshold - 10) {
                imgToDraw = heartHalfImg;
            } else {
                imgToDraw = heartEmptyImg;
            }

            // gambar icon hati di posisi yang dihitung
            int drawX = startX + (i * (heartSize + padding));
            if (imgToDraw != null) {
                g.drawImage(imgToDraw, drawX, startY, heartSize, heartSize, null);
            } else {
                // fallback kotak merah jika gambar gagal load
                g.setColor(Color.RED);
                g.drawRect(drawX, startY, heartSize, heartSize);
            }
        }

        // tampilkan teks score di kanan atas
        String scoreText = "SCORE: " + viewModel.getScore();
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.setColor(Color.CYAN);
        g.drawString(scoreText, getWidth() - scoreWidth - 20, 40);

        // tampilkan teks ammo di kanan bawah
        String ammoText = "AMMO: " + viewModel.getAmmo();
        g.setColor(Color.GREEN);
        g.drawString(ammoText, getWidth() - 150, getHeight() - 20);

        // tampilkan teks peluru meleset alien di bawah score
        String missedText = "ALIEN MISS: " + viewModel.getAlienMissedBullets();
        int missedWidth = g.getFontMetrics().stringWidth(missedText);
        g.setColor(Color.ORANGE); // Gunakan warna oranye agar kontras
        g.drawString(missedText, getWidth() - missedWidth - 20, 65);
    }
}
