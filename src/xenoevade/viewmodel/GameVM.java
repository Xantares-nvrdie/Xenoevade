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
    // atribut observer pattern
    private PropertyChangeSupport support;

    // atribut database
    private DB db;
    private String username;

    // atribut threading
    private boolean isRunning;
    private Thread gameThread;

    // atribut game state
    private int score = 0;
    private int missed = 0;
    private int ammo = 0;
    private int initialDbMissed = 0;

    // atribut entities
    private Player player;
    private List<Entity> aliens;
    private List<Entity> obstacles;
    private List<Entity> playerBullets;
    private List<Entity> alienBullets;
    private List<Entity> explosions;

    // konstanta area game
    private final int GAME_WIDTH = 800;
    private final int GAME_HEIGHT = 600;

    public GameVM(String username) {
        /*
         * Method GameVM
         * konstruktor untuk inisialisasi atribut game
         */

        this.username = username;
        this.support = new PropertyChangeSupport(this);

        // menggunakan synchronizedList agar aman diakses dari multiple thread
        aliens = Collections.synchronizedList(new ArrayList<>());
        playerBullets = Collections.synchronizedList(new ArrayList<>());
        alienBullets = Collections.synchronizedList(new ArrayList<>());
        explosions = Collections.synchronizedList(new ArrayList<>());

        // obstacle statis tidak perlu sinkronisasi karena tidak berubah jumlahnya saat
        // gameplay
        obstacles = new ArrayList<>();

        loadPlayerData();
        initEntities();
    }

    public void loadPlayerData() {
        /*
         * Method loadPlayerData
         * memuat data pemain dari database mysql
         */

        try {
            // buka koneksi database
            db = new DB();
            String query = "SELECT * FROM tbenefit WHERE username = '" + username + "';";
            db.createQuery(query);
            ResultSet rs = db.getRS();

            if (rs.next()) {
                // jika user ditemukan, ambil data sisa peluru terakhir
                this.ammo = rs.getInt("sisa_peluru");
                // simpan nilai missed awal untuk perhitungan akumulatif
                this.initialDbMissed = rs.getInt("peluru_meleset");
            } else {
                // jika user tidak ditemukan, buat record baru dengan nilai default
                db.closeResultSet();
                String insertQuery = "INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES ('"
                        + username + "', 0, 0, 0);";
                db.createUpdate(insertQuery);
                this.ammo = 0;
            }
            // tutup koneksi agar tidak memory leak
            db.closeResultSet();
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEntities() {
        /*
         * Method initEntities
         * inisialisasi posisi awal player dan obstacle lingkungan
         */

        // letakkan player di tengah bawah layar
        player = new Player(GAME_WIDTH / 2 - 32, GAME_HEIGHT - 100);

        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            // generate koordinat acak untuk obstacle
            // dibatasi agar tidak menimpa posisi spawn player
            int obsX = rand.nextInt(GAME_WIDTH - 50);
            int obsY = rand.nextInt(GAME_HEIGHT - 300) + 50;

            obstacles.add(new Obstacle(obsX, obsY));
        }
    }

    public void startGame() {
        /*
         * Method startGame
         * memulai thread utama untuk game loop
         */

        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start(); // memanggil method run()
    }

    public void stopGame(boolean save) {
        /*
         * Method stopGame
         * menghentikan game loop dan opsi menyimpan data
         */

        isRunning = false;
        if (save) {
            saveDataToDB();
        }
    }

    private void saveDataToDB() {
        /*
         * Method saveDataToDB
         * menyimpan progress skor, missed, dan ammo ke database
         */

        try {
            db = new DB();
            // query update akumulatif (skor lama + skor baru)
            String sql = "UPDATE tbenefit SET skor = skor + " + score +
                    ", peluru_meleset = peluru_meleset + " + missed +
                    ", sisa_peluru = " + ammo +
                    " WHERE username = '" + username + "'";

            db.createUpdate(sql);
            db.closeConnection();
        } catch (Exception e) {
            System.err.println("error saving data to db:");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        /*
         * Method run
         * inti dari game loop: update logic -> check collision -> render
         */

        Random rand = new Random();
        while (isRunning) {
            try {
                // 1. hitung pergerakan dan logika ai
                updateLogic(rand);

                // 2. cek tabrakan antar objek
                checkCollisions();

                // 3. kirim sinyal ke view untuk menggambar ulang layar
                support.firePropertyChange("render", null, null);

                // 4. jeda sebentar untuk menjaga frame rate (60 fps)
                Thread.sleep(16);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateLogic(Random rand) {
        /*
         * Method updateLogic
         * mengatur pergerakan entitas, spawn musuh, dan ai sederhana
         */

        // update posisi player berdasarkan input
        player.update();

        // spawn alien baru dengan probabilitas 2% per frame
        if (rand.nextInt(100) < 2) {
            int alienX = rand.nextInt(GAME_WIDTH - 50);
            int alienY = -50; // muncul dari atas layar
            aliens.add(new Alien(alienX, alienY));
        }

        // iterasi dan update setiap alien
        synchronized (aliens) {
            for (int i = 0; i < aliens.size(); i++) {
                Entity a = aliens.get(i);

                // simpan posisi lama sebelum bergerak
                int oldX = a.x;
                int oldY = a.y;

                // gerakkan alien ke bawah
                a.update();

                // cek apakah alien menabrak obstacle (agar tidak tembus)
                boolean isStuck = false;
                int k = 0;

                // gunakan while loop untuk cek tabrakan tanpa break
                while (k < obstacles.size() && !isStuck) {
                    Entity obs = obstacles.get(k);
                    if (a.getBounds().intersects(obs.getBounds())) {
                        isStuck = true; // set flag stuck
                    }
                    k++;
                }

                // jika stuck, kembalikan ke posisi sebelumnya (undo move)
                if (isStuck) {
                    a.x = oldX;
                    a.y = oldY;
                }

                // logika ai menembak (1% chance per frame)
                if (rand.nextInt(100) < 1) {
                    // hitung titik tengah alien dan player
                    double startX = a.x + (a.width / 2.0);
                    double startY = a.y + a.height;
                    double targetX = player.x + (player.width / 2.0);
                    double targetY = player.y + (player.height / 2.0);

                    // hitung sudut tembak ke arah player menggunakan arctan
                    double angleToPlayer = Math.atan2(targetY - startY, targetX - startX);

                    // batasi sudut tembak (cone of fire 45 derajat)
                    double centerAngle = Math.PI / 2;
                    double maxSpread = Math.toRadians(45);
                    if (angleToPlayer < centerAngle - maxSpread) {
                        angleToPlayer = centerAngle - maxSpread;
                    } else if (angleToPlayer > centerAngle + maxSpread) {
                        angleToPlayer = centerAngle + maxSpread;
                    }

                    // konversi sudut menjadi vector kecepatan peluru
                    double bulletSpeed = 5.0;
                    double velX = bulletSpeed * Math.cos(angleToPlayer);
                    double velY = bulletSpeed * Math.sin(angleToPlayer);

                    alienBullets.add(new Bullet((int) startX, (int) startY, velX, velY, false));
                }

                // hapus alien jika sudah melewati batas bawah layar
                if (a.y > GAME_HEIGHT)
                    aliens.remove(i--);
            }
        }

        // update posisi peluru player
        synchronized (playerBullets) {
            for (int i = 0; i < playerBullets.size(); i++) {
                Entity b = playerBullets.get(i);
                b.update();
                // hapus jika keluar batas atas
                if (b.y < 0)
                    playerBullets.remove(i--);
            }
        }

        // update posisi peluru alien
        synchronized (alienBullets) {
            for (int i = 0; i < alienBullets.size(); i++) {
                Entity b = alienBullets.get(i);
                b.update();

                // hapus jika keluar dari layar manapun
                if (b.y > GAME_HEIGHT || b.x < 0 || b.x > GAME_WIDTH) {
                    // hitung sebagai missed (bonus ammo untuk player)
                    missed++;
                    if (missed % 5 == 0) {
                        ammo += 5;
                    }
                    alienBullets.remove(i--);
                }
            }
        }

        // update animasi ledakan
        synchronized (explosions) {
            for (int i = 0; i < explosions.size(); i++) {
                Explosion ex = (Explosion) explosions.get(i);
                ex.update();
                // hapus ledakan jika animasinya selesai
                if (ex.isFinished()) {
                    explosions.remove(i--);
                }
            }
        }
    }

    private void respawnObstacle(Obstacle obs) {
        /*
         * Method respawnObstacle
         * memindahkan obstacle yang hancur ke posisi baru secara acak
         */

        Random rand = new Random();
        int newX = rand.nextInt(GAME_WIDTH - 50);
        int newY = rand.nextInt(GAME_HEIGHT - 300) + 50;

        obs.reset(newX, newY);
    }

    private void checkCollisions() {
        /*
         * Method checkCollisions
         * deteksi tabrakan menggunakan while loop (tanpa break)
         */

        // 1. cek interaksi peluru pemain terhadap alien dan obstacle
        synchronized (playerBullets) {
            for (int i = 0; i < playerBullets.size(); i++) {
                Entity bEntity = playerBullets.get(i);
                Bullet b = (Bullet) bEntity;
                boolean bulletDestroyed = false;

                // cek tabrakan dengan alien
                synchronized (aliens) {
                    int j = 0;
                    // looping alien, berhenti jika list habis atau peluru sudah meledak
                    while (j < aliens.size() && !bulletDestroyed) {
                        Entity a = aliens.get(j);
                        if (b.getBounds().intersects(a.getBounds())) {
                            // tambah efek ledakan dan hapus alien
                            explosions.add(new Explosion(a.x, a.y));
                            aliens.remove(j);
                            score += 10;
                            bulletDestroyed = true; // set flag agar loop berhenti
                        }
                        j++;
                    }
                }

                // cek tabrakan dengan obstacle (hanya jika peluru belum hancur)
                if (!bulletDestroyed) {
                    int k = 0;
                    // looping obstacle tanpa break
                    while (k < obstacles.size() && !bulletDestroyed) {
                        Entity eObs = obstacles.get(k);
                        Obstacle obs = (Obstacle) eObs;

                        if (b.getBounds().intersects(obs.getBounds())) {
                            // kurangi hp batu
                            boolean destroyed = obs.takeDamage(10);

                            if (destroyed) {
                                // jika hancur, beri skor dan respawn batu
                                explosions.add(new Explosion(obs.x, obs.y));
                                score += 5;
                                respawnObstacle(obs);
                            }
                            bulletDestroyed = true; // set flag agar loop berhenti
                        }
                        k++;
                    }
                }

                // hapus peluru dari list jika sudah menabrak sesuatu
                if (bulletDestroyed) {
                    playerBullets.remove(i--);
                }
            }
        }

        // 2. cek interaksi peluru alien terhadap player dan obstacle
        synchronized (alienBullets) {
            for (int i = 0; i < alienBullets.size(); i++) {
                Entity bEntity = alienBullets.get(i);
                Bullet b = (Bullet) bEntity;
                boolean bulletDestroyed = false;

                // cek tabrakan dengan player
                if (b.getBounds().intersects(player.getBounds())) {
                    player.takeDamage(10);

                    // cek kondisi game over
                    if (player.isDead()) {
                        stopGame(true);
                        support.firePropertyChange("gameOver", false, true);
                        return;
                    }
                    bulletDestroyed = true;
                }

                // cek tabrakan dengan obstacle (friendly fire alien ke batu)
                if (!bulletDestroyed) {
                    int k = 0;
                    while (k < obstacles.size() && !bulletDestroyed) {
                        Entity eObs = obstacles.get(k);
                        Obstacle obs = (Obstacle) eObs;

                        if (b.getBounds().intersects(obs.getBounds())) {
                            boolean destroyed = obs.takeDamage(10);

                            if (destroyed) {
                                explosions.add(new Explosion(obs.x, obs.y));
                                respawnObstacle(obs);
                            }

                            // logika missed: jika kena batu, dianggap meleset dari player
                            missed++;
                            if (missed % 5 == 0) {
                                ammo += 5;
                            }

                            bulletDestroyed = true; // loop berhenti
                        }
                        k++;
                    }
                }

                if (bulletDestroyed) {
                    alienBullets.remove(i--);
                }
            }
        }

        // 3. cek tabrakan fisik player dengan obstacle
        for (Entity eObs : obstacles) {
            Obstacle obs = (Obstacle) eObs;

            if (player.getBounds().intersects(obs.getBounds())) {
                player.takeDamage(20);
                // batu menerima damage besar jika ditabrak
                boolean obsDestroyed = obs.takeDamage(50);

                if (player.isDead()) {
                    stopGame(true);
                    support.firePropertyChange("gameOver", false, true);
                    return;
                }

                if (obsDestroyed) {
                    explosions.add(new Explosion(obs.x, obs.y));
                    respawnObstacle(obs);
                } else {
                    // efek mental (knockback) agar player tidak nyangkut
                    player.y += 20;
                }
            }
        }
    }

    public void updatePlayerInput(boolean up, boolean down, boolean left, boolean right) {
        /*
         * Method updatePlayerInput
         * meneruskan status tombol keyboard dari view ke model player
         */
        player.setDirection(up, down, left, right);
    }

    public void playerShoot() {
        /*
         * Method playerShoot
         * logika player menembak: spawn peluru dan kirim sinyal suara
         */

        if (ammo > 0) {
            // posisi peluru di tengah atas sprite player
            int bulletX = player.x + (player.width / 2) - 5;
            int bulletY = player.y;

            playerBullets.add(new Bullet(bulletX, bulletY, 0, -10.0, true));

            ammo--;
            // kirim sinyal ke gamepanel untuk memutar sfx
            support.firePropertyChange("sfx_shoot", null, null);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        /*
         * Method addPropertyChangeListener
         * mendaftarkan listener untuk komunikasi observer pattern
         */
        support.addPropertyChangeListener(pcl);
    }

    // kumpulan method getter untuk diakses oleh view saat render
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

    public int getAlienMissedBullets() {
        return initialDbMissed + missed;
    }
}
