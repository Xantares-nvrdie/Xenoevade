/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: MenuPanel.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Main Menu View with Dark Overlay for Visibility
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import xenoevade.model.DB; //untuk koneksi database

import java.awt.BorderLayout; //untuk layout border
import java.awt.Color; //untuk warna
import java.awt.Cursor; //untuk kursor
import java.awt.Dimension; //untuk dimensi
import java.awt.FlowLayout; //untuk layout flow
import java.awt.Font; //untuk font
import java.awt.Graphics; //untuk menggambar
import java.awt.Graphics2D; //untuk grafis 2D
import java.awt.GridBagLayout; //untuk layout gridbag
import java.awt.Image; //untuk gambar
import java.awt.RenderingHints; //untuk rendering grafis
import java.io.IOException; //untuk penanganan IO Exception
import java.net.URL; //untuk URL resource
import java.sql.ResultSet; //untuk menampung hasil query
import java.util.Vector; //untuk menampung data tabel

import javax.imageio.ImageIO; //untuk membaca file gambar
import javax.swing.BorderFactory; //untuk border
import javax.swing.JButton; //untuk tombol
import javax.swing.JLabel; //untuk label 
import javax.swing.JOptionPane; //untuk dialog pesan
import javax.swing.JPanel; //untuk panel
import javax.swing.JScrollPane; //untuk scroll pane
import javax.swing.JTable; //untuk tabel
import javax.swing.JTextField; //untuk input teks
import javax.swing.table.DefaultTableCellRenderer; //untuk render sel tabel
import javax.swing.table.DefaultTableModel; //untuk model tabel
import javax.swing.table.JTableHeader; //untuk header tabel

public class MenuPanel extends JPanel {

    private MainFrame mainFrame; // referensi mainframe induk
    private Image backgroundImage; // gambar latar belakang

    // konstanta warna ui
    private final Color COLOR_NEON_GREEN = new Color(100, 255, 100);
    private final Color COLOR_TRANSPARENT_BLACK = new Color(0, 0, 0, 200);
    private final Color COLOR_TEXT_WHITE = Color.WHITE;

    public MenuPanel(MainFrame mainFrame) {
        /*
         * Method MenuPanel
         * konstruktor utama untuk menyusun tampilan menu
         */

        this.mainFrame = mainFrame;

        // menggunakan gridbaglayout agar konten berada di tengah
        this.setLayout(new GridBagLayout());

        loadBackground();
        initUI();
    }

    private void loadBackground() {
        /*
         * Method loadBackground
         * memuat aset gambar latar belakang
         */
        try {
            URL url = getClass().getResource("/assets/background_menu.png");
            if (url != null) {
                backgroundImage = ImageIO.read(url);
            }
        } catch (IOException e) {
            System.err.println("gagal memuat gambar background: " + e.getMessage());
            this.setBackground(Color.DARK_GRAY);
        }
    }

    private void initUI() {
        /*
         * Method initUI
         * menyusun komponen-komponen antarmuka
         */

        // buat container transparan
        JPanel container = createContainer();

        // tambahkan judul
        JLabel lblTitle = new JLabel("XENO EVADE", JLabel.CENTER);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 36));
        lblTitle.setForeground(COLOR_NEON_GREEN);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        container.add(lblTitle, BorderLayout.NORTH);

        // tambahkan tabel leaderboard
        JTable tabel = new JTable(getTableData());
        styleTable(tabel);

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        container.add(scrollPane, BorderLayout.CENTER);

        // tambahkan panel input user di bawah
        container.add(createInputPanel(), BorderLayout.SOUTH);

        // masukkan container ke panel utama
        this.add(container);
    }

    private JPanel createContainer() {
        /*
         * Method createContainer
         * membuat panel kotak hitam transparan dengan sudut melengkung
         */

        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // gambar kotak rounded transparan
                g2.setColor(COLOR_TRANSPARENT_BLACK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };

        container.setOpaque(false);
        container.setPreferredSize(new Dimension(700, 500));
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        return container;
    }

    private JPanel createInputPanel() {
        /*
         * Method createInputPanel
         * membuat area input username dan tombol start
         */

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        bottomPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(COLOR_TEXT_WHITE);
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JTextField txtUser = new JTextField(15);
        txtUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_NEON_GREEN, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        txtUser.setBackground(new Color(20, 20, 20));
        txtUser.setForeground(COLOR_TEXT_WHITE);
        txtUser.setCaretColor(COLOR_TEXT_WHITE);

        JButton btnPlay = new JButton("START MISSION");
        styleButton(btnPlay);

        // event listener tombol play
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

        return bottomPanel;
    }

    private void styleTable(JTable tabel) {
        /*
         * Method styleTable
         * helper untuk styling tabel agar terlihat profesional
         */

        tabel.setOpaque(false);
        ((DefaultTableCellRenderer) tabel.getDefaultRenderer(Object.class)).setOpaque(false);
        tabel.setForeground(COLOR_TEXT_WHITE);
        tabel.setRowHeight(30);
        tabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(new Color(255, 255, 255, 30));

        // styling header tabel
        JTableHeader header = tabel.getTableHeader();
        header.setBackground(new Color(0, 0, 0, 0));
        header.setForeground(COLOR_NEON_GREEN);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_NEON_GREEN));
    }

    private void styleButton(JButton btn) {
        /*
         * Method styleButton
         * helper untuk styling tombol ala game
         */

        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setBackground(COLOR_NEON_GREEN);
        btn.setForeground(Color.GREEN); // text warna hijau tua/default tombol swing kadang menimpa
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        /*
         * Method paintComponent
         * menggambar background image fullscreen
         */
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    private DefaultTableModel getTableData() {
        /*
         * Method getTableData
         * mengambil data top 10 skor dari database
         */

        // membuat header tabel
        Vector<String> columns = new Vector<>();
        columns.add("PLAYER");
        columns.add("SCORE");
        columns.add("MISSED");
        columns.add("AMMO");

        Vector<Vector<Object>> data = new Vector<>();

        try {
            DB db = new DB();
            String q = "SELECT * FROM tbenefit ORDER BY skor DESC LIMIT 10";
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
