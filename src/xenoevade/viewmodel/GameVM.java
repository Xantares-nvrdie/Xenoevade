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
import xenoevade.model.Explosion; //untuk efek ledakan
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
    private Player player; // atribut objek player
    private List<Entity> aliens; // atribut list objek alien
    private List<Entity> obstacles; // atribut list objek obstacle
    private List<Entity> playerBullets; // atribut list peluru pemain
    private List<Entity> alienBullets; // atribut list peluru alien
    private List<Entity> explosions; // atribut list ledakan

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
        obstacles = new ArrayList<>(); // obstacle statis jadi arraylist biasa cukup
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
         * Method untuk inisialisasi player dan obstacle (batu) secara acak
         */

        player = new Player(GAME_WIDTH / 2 - 32, GAME_HEIGHT - 100);

        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            // menambahkan 5 obstacle secara acak
            // spawn area disesuaikan agar tidak menimpa spawn point player
            int obsX = rand.nextInt(GAME_WIDTH - 50);
            int obsY = rand.nextInt(GAME_HEIGHT - 300) + 50;

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
         * Mengatur pergerakan alien, peluru, spawn musuh, dan fisika alien
         */

        player.update();

        // spawn alien baru
        if (rand.nextInt(100) < 2) {
            int alienX = rand.nextInt(GAME_WIDTH - 50);
            int alienY = -50;
            aliens.add(new Alien(alienX, alienY));
        }

        // logic alien bergerak, menembak, dan collision dinding
        synchronized (aliens) {
            for (int i = 0; i < aliens.size(); i++) {
                Entity a = aliens.get(i);

                // 1. Simpan posisi lama
                int oldX = a.x;
                int oldY = a.y;

                // 2. Gerakkan alien
                a.update();

                // 3. Cek Tabrakan Fisik dengan Obstacle (Alien Mentok)
                boolean isStuck = false;
                for (Entity obs : obstacles) {
                    if (a.getBounds().intersects(obs.getBounds())) {
                        isStuck = true;
                        break;
                    }
                }

                // 4. Jika nabrak, kembalikan posisi (Undo movement)
                if (isStuck) {
                    a.x = oldX;
                    a.y = oldY;
                }

                // Alien Menembak (Aiming Logic)
                if (rand.nextInt(100) < 1) { // probabilitas tembak
                    double startX = a.x + (a.width / 2.0);
                    double startY = a.y + a.height;
                    double targetX = player.x + (player.width / 2.0);
                    double targetY = player.y + (player.height / 2.0);

                    // hitung sudut tembak ke arah player
                    double angleToPlayer = Math.atan2(targetY - startY, targetX - startX);

                    // batasi sudut tembak (Cone of Fire)
                    double centerAngle = Math.PI / 2;
                    double maxSpread = Math.toRadians(45);
                    if (angleToPlayer < centerAngle - maxSpread) {
                        angleToPlayer = centerAngle - maxSpread;
                    } else if (angleToPlayer > centerAngle + maxSpread) {
                        angleToPlayer = centerAngle + maxSpread;
                    }

                    // konversi sudut ke velocity vector
                    double bulletSpeed = 5.0;
                    double velX = bulletSpeed * Math.cos(angleToPlayer);
                    double velY = bulletSpeed * Math.sin(angleToPlayer);

                    // tambah peluru alien
                    alienBullets.add(new Bullet((int) startX, (int) startY, velX, velY, false));
                }

                // hapus alien jika lewat batas layar
                if (a.y > GAME_HEIGHT)
                    aliens.remove(i--);
            }
        }

        // logic peluru pemain
        synchronized (playerBullets) {
            for (int i = 0; i < playerBullets.size(); i++) {
                Entity b = playerBullets.get(i);
                b.update();
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
                        ammo += 5; // bonus peluru jika musuh sering meleset
                    }
                    alienBullets.remove(i--);
                }
            }
        }

        // logic animasi ledakan
        synchronized (explosions) {
            for (int i = 0; i < explosions.size(); i++) {
                Explosion ex = (Explosion) explosions.get(i);
                ex.update();

                if (ex.isFinished()) {
                    explosions.remove(i--);
                }
            }
        }
    }

    private void respawnObstacle(Obstacle obs) {
        /*
         * Method respawnObstacle
         * Helper untuk memindahkan obstacle yang hancur ke posisi baru
         */

        Random rand = new Random();
        int newX = rand.nextInt(GAME_WIDTH - 50);
        int newY = rand.nextInt(GAME_HEIGHT - 300) + 50;

        obs.reset(newX, newY); // reset HP dan posisi
    }

    private void checkCollisions() {
        /*
         * Method checkCollisions
         * Memeriksa tabrakan peluru vs entitas, dan player vs obstacle
         * Termasuk logika pengurangan HP dan Respawn
         */

        // 1. Peluru Pemain vs (Alien & Obstacle)
        synchronized (playerBullets) {
            for (int i = 0; i < playerBullets.size(); i++) {
                Entity bEntity = playerBullets.get(i);
                Bullet b = (Bullet) bEntity;
                boolean bulletDestroyed = false;

                // Cek vs Alien
                synchronized (aliens) {
                    for (int j = 0; j < aliens.size(); j++) {
                        Entity a = aliens.get(j);
                        if (b.getBounds().intersects(a.getBounds())) {
                            explosions.add(new Explosion(a.x, a.y));
                            aliens.remove(j); // alien mati instant
                            score += 10;
                            bulletDestroyed = true;
                            break;
                        }
                    }
                }

                // Cek vs Obstacle
                if (!bulletDestroyed) {
                    for (Entity eObs : obstacles) {
                        Obstacle obs = (Obstacle) eObs;

                        if (b.getBounds().intersects(obs.getBounds())) {
                            boolean destroyed = obs.takeDamage(10); // kurangi HP batu

                            if (destroyed) {
                                explosions.add(new Explosion(obs.x, obs.y));
                                score += 5;
                                respawnObstacle(obs); // respawn batu
                            }
                            bulletDestroyed = true;
                            break;
                        }
                    }
                }

                if (bulletDestroyed) {
                    playerBullets.remove(i--);
                }
            }
        }

        // 2. Peluru Alien vs (Player & Obstacle)
        synchronized (alienBullets) {
            for (int i = 0; i < alienBullets.size(); i++) {
                Entity bEntity = alienBullets.get(i);
                Bullet b = (Bullet) bEntity;
                boolean bulletDestroyed = false;

                // Cek vs Player
                if (b.getBounds().intersects(player.getBounds())) {
                    player.takeDamage(10); // kurangi HP player

                    if (player.isDead()) {
                        stopGame(true);
                        support.firePropertyChange("gameOver", false, true);
                        return;
                    }
                    bulletDestroyed = true;
                }

                // Cek vs Obstacle (Alien juga bisa hancurkan batu)
                if (!bulletDestroyed) {
                    for (Entity eObs : obstacles) {
                        Obstacle obs = (Obstacle) eObs;
                        if (b.getBounds().intersects(obs.getBounds())) {
                            boolean destroyed = obs.takeDamage(10); // kurangi HP batu

                            if (destroyed) {
                                explosions.add(new Explosion(obs.x, obs.y));
                                respawnObstacle(obs); // respawn batu
                            }
                            bulletDestroyed = true;
                            break;
                        }
                    }
                }

                if (bulletDestroyed) {
                    alienBullets.remove(i--);
                }
            }
        }

        // 3. Player vs Obstacle (Tabrakan Fisik)
        for (Entity eObs : obstacles) {
            Obstacle obs = (Obstacle) eObs;

            if (player.getBounds().intersects(obs.getBounds())) {
                player.takeDamage(20); // player kena damage tabrakan
                boolean obsDestroyed = obs.takeDamage(50); // batu kena damage besar

                // cek status player
                if (player.isDead()) {
                    stopGame(true);
                    support.firePropertyChange("gameOver", false, true);
                    return;
                }

                // cek status batu
                if (obsDestroyed) {
                    explosions.add(new Explosion(obs.x, obs.y));
                    respawnObstacle(obs);
                } else {
                    // efek knockback sederhana agar tidak menempel
                    player.y += 20;
                }
            }
        }
    }

    public void updatePlayerInput(boolean up, boolean down, boolean left, boolean right) {
        /*
         * Method updatePlayerInput
         * Meneruskan input keyboard dari view ke model player
         */
        player.setDirection(up, down, left, right);
    }

    public void playerShoot() {
        /*
         * Method playerShoot
         * Method untuk menembakkan peluru dari pemain
         */

        if (ammo > 0) {
            // hitung posisi X biar di tengah
            int bulletX = player.x + (player.width / 2) - 5;
            int bulletY = player.y;

            // tambah peluru dengan velocity arah atas
            playerBullets.add(new Bullet(bulletX, bulletY, 0, -10.0, true));

            ammo--;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        /*
         * Method addPropertyChangeListener
         * Method untuk menambahkan listener ke property change support
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
