/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: GameVM.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Game View Model (Business Logic & Threading)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.viewmodel;

import xenoevade.model.DB; //untuk koneksi database
import xenoevade.model.Entity; //untuk representasi objek game parent
import xenoevade.model.Explosion;
import xenoevade.model.Player; //Import class Player
import xenoevade.model.Alien; //Import class Alien
import xenoevade.model.Bullet; //Import class Bullet
import xenoevade.model.Obstacle; //Import class Obstacle

import java.beans.PropertyChangeListener; //untuk observer pattern
import java.beans.PropertyChangeSupport; //untuk mengirim notifikasi ke view
import java.sql.ResultSet; //untuk menampung hasil query
import java.util.ArrayList; //untuk thread list
import java.util.Collections;
import java.util.List; //interface list
import java.util.Random; //untuk random posisi alien dan obstacle

public class GameVM implements Runnable {
    // atribut untuk observer pattern
    private PropertyChangeSupport support; // atribut untuk mengirim sinyal ke view

    // atribut database
    private DB db; // atribut koneksi database
    private String username;

    // atribut thread game
    private boolean isRunning; // atribut status game berjalan
    private Thread gameThread; // atribut thread game

    // atribut skor dan status game
    private int score = 0; // atribut skor pemain
    private int missed = 0; // atribut jumlah peluru alien yang meleset
    private int ammo = 0; // atribut jumlah peluru pemain

    // atribut objek game
    private Player player; // UBAH ke tipe Player agar spesifik
    private List<Entity> aliens; // atribut list objek alien
    private List<Entity> obstacles; // atribut list objek obstacle
    private List<Entity> playerBullets; // atribut list peluru pemain
    private List<Entity> alienBullets; // atribut list peluru alien
    private List<Entity> explosions;

    // atribut ukuran area game
    private final int GAME_WIDTH = 800; // lebar area game
    private final int GAME_HEIGHT = 600; // tinggi area game

    public GameVM(String username) {
        /*
         * Method GameVM
         * Konstruktor untuk inisialisasi atribut game
         * Menerima masukan berupa username pemain
         */

        this.username = username;
        this.support = new PropertyChangeSupport(this);

        // inisialisasi list dengan sinkronisasi agar thread-safe
        aliens = Collections.synchronizedList(new ArrayList<>());
        playerBullets = Collections.synchronizedList(new ArrayList<>());
        alienBullets = Collections.synchronizedList(new ArrayList<>());
        obstacles = new ArrayList<>();
        explosions = Collections.synchronizedList(new ArrayList<>());

        // load data player dari database
        loadPlayerData();

        initEntities(); // inisialisasi entitas game
    }

    public void loadPlayerData() {
        /*
         * Method loadPlayerData
         * Method untuk memuat data pemain dari database
         */

        try {
            db = new DB(); // membuat koneksi database
            String query = "SELECT * FROM tbenefit WHERE username = '" + username + "';";
            db.createQuery(query); // mengeksekusi query
            ResultSet rs = db.getRS(); // mengambil hasil query

            if (rs.next()) {
                // jika pemain sudah ada di database, muat data
                this.ammo = rs.getInt("sisa_peluru");
            } else {
                // jika pemain baru, masukkan data default ke database
                db.closeResultSet(); // tutup resultset sebelumnya
                String insertQuery = "INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES ('"
                        + username + "', 0, 0, 0);";
                db.createUpdate(insertQuery); // mengeksekusi insert
                this.ammo = 0; // inisialisasi peluru pemain
            }
            db.closeResultSet(); // tutup resultset
            db.closeConnection(); // tutup koneksi database
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEntities() {
        /*
         * Method initEntities
         * Method untuk inisialisasi obstacle (batu) secara acak di game
         */

        // REVISI: Menggunakan class Player (bukan new Entity)
        // Ukuran tidak perlu dimasukkan karena sudah diatur di dalam class Player
        player = new Player(GAME_WIDTH / 2 - 32, GAME_HEIGHT - 100);

        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            // menambahkan 5 obstacle secara acak
            int obsX = rand.nextInt(GAME_WIDTH - 50);
            int obsY = rand.nextInt(GAME_HEIGHT - 300); // Agar tidak spawn di area player

            // REVISI: Menggunakan class Obstacle
            obstacles.add(new Obstacle(obsX, obsY));
        }
    }

    public void startGame() {
        /*
         * Method startGame
         * Method untuk memulai thread game
         */

        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGame(boolean save) {
        /*
         * Method stopGame
         * Method untuk menghentikan thread game
         */

        isRunning = false;
        if (save) {
            saveDataToDB();
        }
    }

    private void saveDataToDB() {
        /*
         * Method saveDataToDB
         * Method untuk menyimpan data pemain ke database
         */

        try {
            db = new DB(); // membuat koneksi database
            String sql = "UPDATE tbenefit SET skor = skor + " + score +
                    ", peluru_meleset = peluru_meleset + " + missed +
                    ", sisa_peluru = " + ammo +
                    " WHERE username = '" + username + "'";

            System.out.println("SQL: " + sql);
            db.createUpdate(sql); // mengeksekusi update
            db.closeConnection(); // tutup koneksi database
        } catch (Exception e) {
            System.err.println("Error saving data to DB:");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        /*
         * Method run (override runnable)
         * Inti dari game loop. mengupdate logic dan mengirim sinyal render terus
         * menerus
         */

        Random rand = new Random();
        while (isRunning) {
            try {
                updateLogic(rand); // mengupdate logika game
                checkCollisions(); // memeriksa tabrakan antar objek
                support.firePropertyChange("render", null, null); // mengirim sinyal render ke view
                Thread.sleep(16); // delay sekitar 60 FPS
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateLogic(Random rand) {
        /*
         * Method updateLogic
         * Mengatur pergerakan alien, peluru, dan spawn musuh
         */
        player.update();
        if (rand.nextInt(100) < 2) {
            int alienX = rand.nextInt(GAME_WIDTH - 50);
            int alienY = -50;
            aliens.add(new Alien(alienX, alienY));
        }

        // logic alien bergerak dan menembak
        synchronized (aliens) {
            for (int i = 0; i < aliens.size(); i++) {
                Entity a = aliens.get(i);
                a.update();

                // Alien menembak secara acak
                if (rand.nextInt(100) < 1) { // Probabilitas tembakan 1% per frame

                    // 1. Tentukan titik tengah alien dan player
                    double startX = a.x + (a.width / 2.0);
                    double startY = a.y + a.height;
                    double targetX = player.x + (player.width / 2.0);
                    double targetY = player.y + (player.height / 2.0);

                    // 2. Hitung sudut ke arah player (dalam radian)
                    double angleToPlayer = Math.atan2(targetY - startY, targetX - startX);

                    // 3. Konfigurasi batasan sudut (Cone of Fire)
                    // Sudut 90 derajat (PI/2) adalah lurus ke bawah
                    double centerAngle = Math.PI / 2;
                    double maxSpread = Math.toRadians(45); // Batas kemiringan maksimal 45 derajat kiri/kanan

                    // 4. Clamp (batasi) sudut agar tidak terlalu miring
                    // Jika sudut < 45 derajat (terlalu kanan) atau > 135 derajat (terlalu kiri)
                    if (angleToPlayer < centerAngle - maxSpread) {
                        angleToPlayer = centerAngle - maxSpread;
                    } else if (angleToPlayer > centerAngle + maxSpread) {
                        angleToPlayer = centerAngle + maxSpread;
                    }

                    // 5. Hitung velocity vector berdasarkan sudut yang sudah dibatasi
                    double bulletSpeed = 5.0; // Kecepatan peluru
                    double velX = bulletSpeed * Math.cos(angleToPlayer);
                    double velY = bulletSpeed * Math.sin(angleToPlayer);

                    // Menambahkan peluru dengan vektor kecepatan khusus
                    // Pastikan class Bullet memiliki konstruktor yang menerima (x, y, velX, velY,
                    // isPlayer)
                    alienBullets.add(new Bullet((int) startX, (int) startY, velX, velY, false));
                }

                if (a.y > GAME_HEIGHT)
                    aliens.remove(i--);
            }
        }

        // logic peluru pemain
        synchronized (playerBullets) {
            for (int i = 0; i < playerBullets.size(); i++) {
                Entity b = playerBullets.get(i);
                b.update(); // Gunakan update() dari class Bullet
                if (b.y < 0)
                    playerBullets.remove(i--);
            }
        }

        // logic peluru alien
        synchronized (alienBullets) {
            for (int i = 0; i < alienBullets.size(); i++) {
                Entity b = alienBullets.get(i);
                b.update();

                if (b.y > GAME_HEIGHT || b.x < 0 || b.x > GAME_WIDTH) {
                    missed++;
                    if (missed % 5 == 0) {
                        ammo += 5;
                    }
                    alienBullets.remove(i--);
                }
            }
        }

        synchronized (explosions) {
            for (int i = 0; i < explosions.size(); i++) {
                Explosion ex = (Explosion) explosions.get(i);
                ex.update(); // Jalankan animasi frame berikutnya
                
                // Jika animasi sudah frame terakhir (selesai)
                if (ex.isFinished()) {
                    explosions.remove(i--); // Hapus dari memori
                }
            }
        }
    }

    private void checkCollisions() {
        /*
         * Method checkCollisions
         * Method untuk memeriksa tabrakan antar objek game
         */

        // memeriksa tabrakan peluru pemain dengan alien
        synchronized (playerBullets) {
            synchronized (aliens) {
                for (int i = 0; i < playerBullets.size(); i++) {
                    Entity b = playerBullets.get(i);
                    boolean hit = false;

                    synchronized (aliens) {
                        for (int j = 0; j < aliens.size(); j++) {
                            Entity a = aliens.get(j);
                            if (b.getBounds().intersects(a.getBounds())) {
                                // kena alien
                                explosions.add(new Explosion(a.x, a.y));
                                aliens.remove(j); // hapus alien
                                score += 10; // tambah skor
                                
                                // aliens.remove(j); // HAPUS DUPLIKAT REMOVE
                                hit = true;
                                break;
                            }
                        }
                    }

                    if (hit) {
                        playerBullets.remove(i--); // hapus peluru jika kena
                    } else {
                        // periksa tabrakan dengan obstacle
                        for (Entity obs : obstacles) {
                            if (b.getBounds().intersects(obs.getBounds())) {
                                playerBullets.remove(i--); // hapus peluru jika kena obstacle
                                break;
                            }
                        }
                    }
                }
            }
        }

        // memeriksa tabrakan peluru alien dengan pemain
        synchronized (alienBullets) {
            for (Entity b : alienBullets) {
                if (b.getBounds().intersects(player.getBounds())) {
                    // kena player maka game over
                    stopGame(true); // save data dan hentikan game
                    support.firePropertyChange("gameOver", false, true); // kirim sinyal game over ke view
                    return;
                }
                for (Entity obs : obstacles) {
                    if (b.getBounds().intersects(obs.getBounds())) {
                        b.y = GAME_HEIGHT + 100; // hilangkan peluru jika kena obstacle (lempar keluar layar)
                    }
                }
            }
        }
    }

    // Ganti method movePlayer yang lama dengan ini:
    public void updatePlayerInput(boolean up, boolean down, boolean left, boolean right) {
        player.setDirection(up, down, left, right);
    }

    public void playerShoot() {
        /*
         * Method playerShoot
         * Method untuk menembakkan peluru dari pemain
         */

        if (ammo > 0) {
            // Hitung posisi X biar di tengah
            int bulletX = player.x + (player.width / 2) - 5;
            int bulletY = player.y;

            // Velocity Player: X=0 (lurus), Y=-10 (naik)
            // Pastikan parameter velocity dikirim sebagai double (-10.0)
            playerBullets.add(new Bullet(bulletX, bulletY, 0, -10.0, true));

            ammo--;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        /*
         * Method addPropertyChangeListener
         * Method untuk menambahkan listener ke property change support
         * Menerima masukan berupa objek listener
         */

        support.addPropertyChangeListener(pcl);
    }

    // getter untuk atribut game
    public Entity getPlayer() {
        return player;
    }

    public List<Entity> getAliens() {
        return aliens;
    }

    public List<Entity> getObstacles() {
        return obstacles;
    }

    public List<Entity> getPlayerBullets() {
        return playerBullets;
    }

    public List<Entity> getAlienBullets() {
        return alienBullets;
    }

    public int getScore() {
        return score;
    }

    public int getAmmo() {
        return ammo;
    }

    public int getMissed() {
        return missed;
    }
    
    public List<Entity> getExplosions() {
        synchronized (explosions) {
            return new ArrayList<>(explosions);
        }
    }
}
