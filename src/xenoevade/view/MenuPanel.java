/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: MenuPanel.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Main Menu View with Dark Overlay for Visibility
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import xenoevade.viewmodel.MenuVM; //import viewmodel menu

import java.awt.BorderLayout; //untuk layout border
import java.awt.Color; //untuk warna
import java.awt.Cursor; //untuk kursor
import java.awt.Dimension; //untuk dimensi
import java.awt.FlowLayout; //untuk layout flow
import java.awt.Font; //untuk font
import java.awt.Graphics; //untuk menggambar
import java.awt.Graphics2D; //untuk grafis 2D
import java.awt.GridBagLayout; //untuk layout gridbag
import java.awt.GridLayout; //untuk layout grid
import java.awt.Image; //untuk gambar
import java.awt.RenderingHints; //untuk rendering grafis
import java.io.IOException; //untuk penanganan IO Exception
import java.net.URL; //untuk URL resource

import javax.imageio.ImageIO; //untuk membaca file gambar
import javax.swing.BorderFactory; //untuk border
import javax.swing.JButton; //untuk tombol
import javax.swing.JLabel; //untuk label 
import javax.swing.JOptionPane; //untuk dialog pesan
import javax.swing.JPanel; //untuk panel
import javax.swing.JScrollBar; //untuk scroll bar
import javax.swing.JScrollPane; //untuk scroll pane
import javax.swing.JTable; //untuk tabel
import javax.swing.JTextField; //untuk input teks
import javax.swing.table.DefaultTableCellRenderer; //untuk render sel tabel
import javax.swing.table.DefaultTableModel; //untuk model tabel
import javax.swing.table.JTableHeader; //untuk header tabel

public class MenuPanel extends JPanel {

    private MainFrame mainFrame; // referensi mainframe induk
    private Image backgroundImage; // gambar latar belakang

    private MenuVM menuVM = new MenuVM();

    // konstanta warna ui
    private final Color COLOR_NEON_GREEN = new Color(100, 255, 100);
    private final Color COLOR_NEON_RED = new Color(255, 80, 80);
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

        // panel tengah untuk tabel dan tombol scroll
        JPanel centerPanel = new JPanel(new BorderLayout(10, 0));
        centerPanel.setOpaque(false);

        // tambahkan tabel leaderboard
        JTable tabel = new JTable(getTableData());
        styleTable(tabel);

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // sembunyikan scrollbar bawaan tapi tetap bisa di-scroll mouse
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // panel tombol navigasi scroll
        JPanel scrollButtons = new JPanel(new GridLayout(2, 1, 0, 10));
        scrollButtons.setOpaque(false);

        JButton btnUp = new JButton("▲");
        JButton btnDown = new JButton("▼");

        styleButton(btnUp, COLOR_NEON_GREEN);
        styleButton(btnDown, COLOR_NEON_GREEN);

        // logika tombol scroll
        JScrollBar vsb = scrollPane.getVerticalScrollBar();

        btnUp.addActionListener(e -> {
            vsb.setValue(vsb.getValue() - 50);
        });

        btnDown.addActionListener(e -> {
            vsb.setValue(vsb.getValue() + 50);
        });

        scrollButtons.add(btnUp);
        scrollButtons.add(btnDown);

        centerPanel.add(scrollButtons, BorderLayout.EAST);
        container.add(centerPanel, BorderLayout.CENTER);

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
        container.setPreferredSize(new Dimension(750, 500));
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

        JTextField txtUser = new JTextField(12);
        txtUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtUser.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(COLOR_NEON_GREEN, 1),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        txtUser.setBackground(new Color(20, 20, 20));
        txtUser.setForeground(COLOR_TEXT_WHITE);
        txtUser.setCaretColor(COLOR_TEXT_WHITE);

        JButton btnPlay = new JButton("START");
        styleButton(btnPlay, COLOR_NEON_GREEN);

        JButton btnQuit = new JButton("QUIT");
        styleButton(btnQuit, COLOR_NEON_RED);

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

        // event listener tombol quit
        btnQuit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Keluar dari game?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        bottomPanel.add(lblUser);
        bottomPanel.add(txtUser);
        bottomPanel.add(btnPlay);
        bottomPanel.add(btnQuit);

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

    private void styleButton(JButton btn, Color c) {
        /*
         * Method styleButton
         * helper untuk styling tombol ala game
         */

        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setBackground(c);
        btn.setForeground(Color.BLACK);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
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
         * mengambil data top skor dari model tabel pengguna
         */

        return menuVM.getLeaderboardData();
    }
}
