/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Project: XenoEvade
Filename: DB.java
Programmer: Bintang Fajar Putra Pamungkas
Email: bintangfajarputra@upi.edu
Description: Database connection class
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package xenoevade.model;

import java.sql.Connection; //untuk koneksi database
import java.sql.DriverManager; //untuk manajemen driver database
import java.sql.SQLException; //untuk penanganan error SQL
import java.sql.ResultSet; //untuk menampung hasil query
import java.sql.Statement; //untuk eksekusi query

public class DB {
    //atribut alamat database
    private String conAddress = "jdbc:mysql://localhost:3306/xenoevade?user=root&password=";
    //atribut statement untuk eksekusi query
    private Statement stmt = null;
    //atribut resultset untuk menampung hasil query
    private ResultSet rs = null;
    //atribut connection untuk koneksi database
    private Connection conn = null;


    public DB() throws Exception, SQLException {
        /* Method DB
        Konstruktor melakukan koneksi ke MySQL
        Menerima masukan berupa string alamat koneksi ke MySQL*/

        try{
            //membuat driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            //membuat koneksi ke database
            conn = DriverManager.getConnection(conAddress);
            conn.setTransactionIsolation(conn.TRANSACTION_READ_UNCOMMITTED);
        }catch(SQLException es){
            throw es; //melempar exception jika terjadi error
        }
    }

    public void createQuery(String Query) throws Exception, SQLException {
        /* Method createQuery
        Method untuk mengeksekusi query ke database
        Menerima masukan berupa string query*/

        try{
            stmt = conn.createStatement(); //membuat statement
            rs = stmt.executeQuery(Query); //mengeksekusi query
        }catch(SQLException es){
            throw es; //melempar exception jika terjadi error
        }
    }

    public void createUpdate(String Query) throws Exception, SQLException {
        /* Method createUpdate
        Method untuk mengeksekusi update ke database
        Menerima masukan berupa string query*/

        try{
            stmt = conn.createStatement(); //membuat statement
            stmt.executeUpdate(Query); //mengeksekusi update
        }catch(SQLException es){
            throw es; //melempar exception jika terjadi error
        }
    }

    public ResultSet getRS() throws Exception {
        /* Method getRS
        Method untuk mendapatkan resultset dari query
        Mengembalikan nilai berupa resultset*/

        ResultSet hasil = null;
        try{
            return rs; //mengembalikan nilai resultset
        }catch(Exception e){
            return hasil; //mengembalikan nilai null jika terjadi error
        }
    }

    public void closeResultSet() throws Exception, SQLException {
        /* Method closeResultSet
        Method untuk menutup resultset*/

        //menutup resultset
        if(rs != null){
            try{
                rs.close(); //menutup resultset
            }catch(SQLException es){
                rs = null;
                throw es; //melempar exception jika terjadi error
            }
        }
        //menutup statement juga
        if(stmt != null){
            try{
                stmt.close(); //menutup statement
            }catch(SQLException es){
                stmt = null;
                throw es; //melempar exception jika terjadi error
            }
        }

    }

    public void closeConnection() throws Exception, SQLException {
        /* Method closeConnection
        Method untuk menutup koneksi database*/

        //menutup koneksi
        if(conn != null){
            try{
                conn.close(); //menutup koneksi
            }catch(SQLException es){
                conn = null;
                throw es; //melempar exception jika terjadi error
            }
        }
    }
}
