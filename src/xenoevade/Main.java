/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: Main.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Application Entry Point
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
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

        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
