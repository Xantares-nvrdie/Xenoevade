/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: MainFrame.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Main Window Container (JFrame)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

    public MainFrame() {
        /*
         * Method MainFrame
         * konstruktor untuk inisialisasi frame utama
         */

        this.setTitle("XenoEvade");
        this.setSize(800, 600);

        // set operasi tutup frame agar aplikasi berhenti total
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // membuat frame muncul di tengah layar
        this.setLocationRelativeTo(null);

        // membuat frame tidak bisa diubah ukurannya
        this.setResizable(false);

        showMenu();
        this.setVisible(true);
    }

    public void showMenu() {
        /*
         * Method showMenu
         * method untuk menampilkan menu utama
         */

        this.setContentPane(new MenuPanel(this));

        // refresh frame untuk menerapkan perubahan ui
        this.revalidate();
        this.repaint();
    }

    public void startGame(String username) {
        /*
         * Method startGame
         * mengganti tampilan konten menjadi gamepanel dan memulai permainan
         */

        GamePanel gamePanel = new GamePanel(this, username);
        this.setContentPane(gamePanel);

        // meminta fokus ke panel game agar input keyboard terbaca
        gamePanel.requestFocusInWindow();

        // refresh frame
        this.revalidate();
        this.repaint();
    }
}
