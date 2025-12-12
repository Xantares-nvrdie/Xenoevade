/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: MenuPanel.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Main Menu View (Table & Input)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.view;

import xenoevade.model.DB;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable; //untuk tabel
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel; //untuk model tabel

import java.awt.BorderLayout;
import java.sql.ResultSet; //untuk menampung hasil query
import java.util.Vector; //untuk menampung data tabel


public class MenuPanel extends JPanel{
    
    private MainFrame mainFrame; //referensi ke frame utama
    
    public MenuPanel(MainFrame mainFrame) {
        /* Method MenuPanel
        menyusun tampilan menu*/

        this.mainFrame = mainFrame;
        this.setLayout(new BorderLayout());
        
        //judul menu
        JLabel lblTitle = new JLabel("XenoEvade Main Menu", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(24.0f));
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 0));
        this.add(lblTitle, BorderLayout.NORTH);


        //tabel skor
        JTable tabel = new JTable(getTableData());
        JScrollPane scrollPane = new JScrollPane(tabel); //menambahkan scroll pane ke tabel
        this.add(scrollPane, BorderLayout.CENTER);

        //input
        JPanel bottomPanel = new JPanel();

        JLabel lblUser = new JLabel("Username: ");
        JTextField txtUser = new JTextField(20);
        JButton btnPlay = new JButton("Play Game");

        //action ketika ditekan tombol play
        btnPlay.addActionListener(e -> {
            String username = txtUser.getText().trim();
            if(!username.isEmpty()){
                mainFrame.startGame(username); //memulai game dengan username
            } else {
                JOptionPane.showMessageDialog(this, "Masukkan Username terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottomPanel.add(lblUser);
        bottomPanel.add(txtUser);
        bottomPanel.add(btnPlay);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private DefaultTableModel getTableData() {
        /* Method getTableData
        Method untuk mendapatkan data tabel skor dari database
        Mengembalikan model tabel berisi data skor*/

        Vector<String> columns = new Vector<>();
        columns.add("Username");
        columns.add("Skor");
        columns.add("Peluru Meleset");
        columns.add("Sisa Peluru");

        Vector<Vector<Object>> data = new Vector<>();

        try {
            DB db = new DB();
            String q = "SELECT * FROM tbenefit";
            db.createQuery(q);
            ResultSet rs = db.getRS();

            while(rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("username"));
                row.add(rs.getInt("skor"));
                row.add(rs.getInt("peluru_meleset"));
                row.add(rs.getInt("sisa_peluru"));
                data.add(row);
            }

            db.closeResultSet();
            db.closeConnection();
        }catch(Exception e){
            e.printStackTrace();
        }

        return new DefaultTableModel(data, columns);
    }
}
