/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Main.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Application Entry Point
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

/*Saya Bintang Fajar Putra Pamungkas dengan NIM 2405073 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.*/

package xenoevade;

import xenoevade.view.MainFrame; //import frame utama
import javax.swing.SwingUtilities; //thread safety

public class Main {
    public static void main(String[] args) {
        /*
         * Method main
         * Menjalankan aplikasi di dalam Event Dispatch Thread (EDT)
         * agar aman untuk Swing GUI
         */

        // menjalankan pembuatan frame utama di EDT
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
