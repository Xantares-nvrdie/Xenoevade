/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: MainFrame.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Main Window Container (JFrame)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import javax.swing.JFrame; //untuk frame utama
import javax.swing.SwingUtilities; //unruk thread safety ui

public class MainFrame extends JFrame {
    public MainFrame() {
        /* Method MainFrame
        Konstruktor untuk inisialisasi frame utama*/

        this.setTitle("XenoEvade"); //set judul frame
        this.setSize(800, 600); //set ukuran frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //set operasi tutup frame
        this.setLocationRelativeTo(null); //membuat frame muncul di tengah layar
        this.setResizable(false); //membuat frame tidak bisa diubah ukurannya

        showMenu(); //tampilan awal saat dibuka
        this.setVisible(true); //menampilkan frame
    }

    public void showMenu() {
        /* Method showMenu
        Method untuk menampilkan menu utama*/

        this.setContentPane(new MenuPanel(this)); //set panel menu utama
        this.revalidate(); //merefresh frame
        this.repaint(); //merepaint frame
    }

    public void startGame(String username){
        /* Method startGame
        Mengganti tampilan konten enjadi GamePanel dan memulai permainan */

        GamePanel gamePanel = new GamePanel(this, username); //membuat panel game baru
        this.setContentPane(gamePanel); //set panel game

        gamePanel.requestFocusInWindow(); //meminta fokus ke panel game
        
        this.revalidate(); //merefresh frame
        this.repaint(); //merepaint frame
    }

    
    
}
