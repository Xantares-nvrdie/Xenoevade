/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: MenuPanel.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Main Menu View with Dark Overlay for Visibility
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import xenoevade.model.DB;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Vector;
import javax.imageio.ImageIO;

public class MenuPanel extends JPanel {

    private MainFrame mainFrame;
    private Image backgroundImage;

    public MenuPanel(MainFrame mainFrame) {
        /*
         * Method MenuPanel
         * Menyusun tampilan menu dengan container transparan
         */

        this.mainFrame = mainFrame;
        // Menggunakan GridBagLayout agar kotak menu ada di tengah layar
        this.setLayout(new GridBagLayout());

        // Load Gambar Background
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/assets/background_menu.png"));
        } catch (IOException | NullPointerException e) {
            System.err.println("Gagal memuat gambar background: " + e.getMessage());
            this.setBackground(Color.DARK_GRAY);
        }

        // Setup Main Container (Kotak Hitam Transparan)
        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Membuat kotak hitam transparan dengan sudut melengkung
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 200)); // Hitam dengan opacity tinggi (agar teks terbaca)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Radius 30px
                g2.dispose();
            }
        };
        container.setOpaque(false);
        // Mengatur ukuran container agar tidak memenuhi layar (memberi jarak dari
        // pinggir)
        container.setPreferredSize(new Dimension(700, 500));
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Judul Menu
        JLabel lblTitle = new JLabel("XENO EVADE", JLabel.CENTER);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 36));
        lblTitle.setForeground(new Color(100, 255, 100)); // Warna hijau neon
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        container.add(lblTitle, BorderLayout.NORTH);

        // Setup Tabel
        JTable tabel = new JTable(getTableData());
        styleTable(tabel); // Panggil fungsi styling terpisah agar rapi

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        container.add(scrollPane, BorderLayout.CENTER);

        // Panel Input (Bawah)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        bottomPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JTextField txtUser = new JTextField(15);
        txtUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 255, 100), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        txtUser.setBackground(new Color(20, 20, 20));
        txtUser.setForeground(Color.WHITE);
        txtUser.setCaretColor(Color.WHITE);

        JButton btnPlay = new JButton("START MISSION");
        styleButton(btnPlay); // Styling tombol

        // Action Listener
        btnPlay.addActionListener(e -> {
            String username = txtUser.getText().trim();
            if (!username.isEmpty()) {
                mainFrame.startGame(username);
            } else {
                JOptionPane.showMessageDialog(this, "Masukkan Username terlebih dahulu!", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        bottomPanel.add(lblUser);
        bottomPanel.add(txtUser);
        bottomPanel.add(btnPlay);
        container.add(bottomPanel, BorderLayout.SOUTH);

        // Tambahkan container ke panel utama
        this.add(container);
    }

    private void styleTable(JTable tabel) {
        /* Helper untuk styling tabel agar terlihat profesional */
        tabel.setOpaque(false);
        ((DefaultTableCellRenderer) tabel.getDefaultRenderer(Object.class)).setOpaque(false);
        tabel.setForeground(Color.WHITE);
        tabel.setRowHeight(30);
        tabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(new Color(255, 255, 255, 30)); // Garis tipis transparan

        // Header Style
        JTableHeader header = tabel.getTableHeader();
        header.setBackground(new Color(0, 0, 0, 0)); // Header transparan (ikut container)
        header.setForeground(new Color(100, 255, 100)); // Teks header hijau neon
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 255, 100)));
    }

    private void styleButton(JButton btn) {
        /* Helper untuk styling tombol ala game */
        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setBackground(new Color(100, 255, 100)); // Hijau Neon
        btn.setForeground(Color.GREEN);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Menggambar background fullscreen
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    private DefaultTableModel getTableData() {
        /* Mengambil data dari database */
        Vector<String> columns = new Vector<>();
        columns.add("PLAYER");
        columns.add("SCORE");
        columns.add("MISSED");
        columns.add("AMMO");

        Vector<Vector<Object>> data = new Vector<>();

        try {
            DB db = new DB();
            String q = "SELECT * FROM tbenefit ORDER BY skor DESC LIMIT 10"; // Tambah LIMIT biar ga kepenuhan
            db.createQuery(q);
            ResultSet rs = db.getRS();

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

        return new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Agar tabel tidak bisa diedit user
            }
        };
    }
}
