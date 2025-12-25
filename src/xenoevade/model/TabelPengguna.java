/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: TabelPengguna.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Model Data Object for Leaderboard Table
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.sql.ResultSet; //untuk menampung hasil query
import java.util.Vector; //untuk menampung data tabel
import javax.swing.table.DefaultTableModel; //untuk model tabel swing

public class TabelPengguna {

    public TabelPengguna() {
        /*
         * Method TabelPengguna
         * konstruktor default
         */
    }

    public DefaultTableModel getLeaderboardData() {
        /*
         * Method getLeaderboardData
         * mengambil data top skor dari database dan mengembalikan model tabel
         */

        // menyusun header kolom
        Vector<String> columns = new Vector<>();
        columns.add("PLAYER");
        columns.add("SCORE");
        columns.add("MISSED");
        columns.add("AMMO");

        Vector<Vector<Object>> data = new Vector<>();

        try {
            DB db = new DB();
            // limit diperbanyak agar fitur scroll berguna
            String q = "SELECT * FROM tbenefit ORDER BY skor DESC";
            db.createQuery(q);
            ResultSet rs = db.getRS();

            // ambil data dari resultset
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("username"));
                row.add(rs.getInt("skor"));
                row.add(rs.getInt("peluru_meleset"));
                row.add(rs.getInt("sisa_peluru"));
                data.add(row);
            }
            db.closeResultSet();
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // mengembalikan model tabel yang tidak bisa diedit cell-nya
        return new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
