/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: TBenefit.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Data Access Object (DAO) for tbenefit table
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.sql.ResultSet;

public class TBenefit {
    private DB db;
    
    public TBenefit() {
        // Konstruktor kosong
    }

    public void getOrCreateUser(String username, GameDataCallback callback) {
        /*
         * Method getOrCreateUser
         * Mengambil data user, atau membuat baru jika belum ada
         * Menggunakan callback object (atau return object custom) untuk data
         */
        try {
            db = new DB();
            String query = "SELECT * FROM tbenefit WHERE username = '" + username + "'";
            db.createQuery(query);
            ResultSet rs = db.getRS();

            if (rs.next()) {
                // User ditemukan, kembalikan data
                int ammo = rs.getInt("sisa_peluru");
                int missed = rs.getInt("peluru_meleset");
                callback.onDataLoaded(ammo, missed);
            } else {
                // User baru, buat record default
                db.closeResultSet();
                String insert = "INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) VALUES ('"
                        + username + "', 0, 0, 0)";
                db.createUpdate(insert);
                callback.onDataLoaded(0, 0); // Default values
            }

            db.closeResultSet();
            db.closeConnection();

        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    public void updateGameData(String username, int score, int missed, int ammo) {
        /*
         * Method updateGameData
         * Menyimpan progress akumulatif ke database
         */
        try {
            db = new DB();
            String sql = String.format(
                    "UPDATE tbenefit SET skor = skor + %d, peluru_meleset = peluru_meleset + %d, sisa_peluru = %d WHERE username = '%s'",
                    score, missed, ammo, username);

            db.createUpdate(sql);
            db.closeConnection();
        } catch (Exception e) {
            System.err.println("Error saving game data: " + e.getMessage());
        }
    }

    // Interface sederhana untuk mengembalikan data (Inner interface)
    public interface GameDataCallback {
        void onDataLoaded(int ammo, int initialMissed);
    }
}
