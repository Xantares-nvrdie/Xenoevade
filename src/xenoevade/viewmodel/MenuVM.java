/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: MenuVM.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: ViewModel for Menu & Leaderboard
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.viewmodel;

import javax.swing.table.DefaultTableModel;
import xenoevade.model.TabelPengguna;

public class MenuVM {

    private TabelPengguna tabelPengguna;

    public MenuVM() {
        /*
         * Konstruktor MenuVM
         * Inisialisasi model data
         */
        tabelPengguna = new TabelPengguna();
    }

    public DefaultTableModel getLeaderboardData() {
        /*
         * Method getLeaderboardData
         * Menyediakan data leaderboard untuk View
         */
        return tabelPengguna.getLeaderboardData();
    }
}
