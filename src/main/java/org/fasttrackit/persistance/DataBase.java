package org.fasttrackit.persistance;

import org.fasttrackit.domain.Account;
import org.fasttrackit.domain.Card;

import java.sql.*;

import static java.lang.System.out;

public class DataBase {
    private static String url;

    public DataBase(String locationDB){
        url = "jdbc:sqlite:" + locationDB;
    }

    //conectarea la o baza de date
    private Connection connect() {
        // SQLite connection string

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    //crearea unei baze de date
    public void createNewDatabase() {

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn == null) {
                System.out.println("A new database NOT been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //crearea tabelului card
    public void createTableCard() {
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "    id INTEGER PRIMARY KEY ASC,\n"
                + "    iban TEXT,\n"
                + "    number TEXT,\n"
                + "    pin TEXT,\n"
                + "    balance DOUBLE DEFAULT 0,\n"
                + "    lock TEXT DEFAULT open\n"
                + ");";

        try (Statement stmt = connect().createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //verifica daca exista un IBAN sau number in baza de date
    public boolean verifyExist(String codeIban,String codeNumber){
        String sql = "SELECT iban, number, pin FROM card";

        try (Statement stmt  = connect().createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                if ( rs.getString("iban").compareTo(codeIban)==0||
                        rs.getString("number").compareTo(codeNumber)==0)
                    return false;
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return true;
    }

    //inserarea unui card
    public void insertNewCard(String cardIban, String cardNumber, String cardPin, double balance){
        String sql = "INSERT INTO card(iban,number,pin,balance) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardIban);
            pstmt.setString(2, cardNumber);
            pstmt.setString(3, cardPin);
            pstmt.setDouble(4, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Boolean verifyNumber(String codeNumber){
        String sql = "SELECT number FROM card";

        try (Statement stmt  = connect().createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                if (rs.getString("number").compareTo(codeNumber)==0)
                    return true;
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return false;
    }

    //verifica daca exista un card specific cu un pin specific
    public Boolean verifyLogin(String codeNumber, String codePin){
        String sql = "SELECT number, pin FROM card";

        try (Statement stmt  = connect().createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                if (rs.getString("number").compareTo(codeNumber)==0 &&
                        rs.getString("pin").compareTo(codePin)==0)
                    return true;
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return false;
    }

    // verifica daca cardul este blocat
    public boolean verifyLock(String codeNumber){
        String sql = "SELECT lock FROM card WHERE number = " + codeNumber;

        try (PreparedStatement pstmt  = connect().prepareStatement(sql)){

            ResultSet rs    = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                if(rs.getString("lock").equals("open")){
                    return true;
                }
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return false;
    }

    //selecteaza contul dupa ce cardul a fost detectat
    public Account selectAccount(String codeNumber){
        Card card = null;
        double balanced = 0;
        String iban = "";

        String sql = "SELECT iban,number,pin,balance FROM card WHERE number = ?";

        try (PreparedStatement pstmt  = connect().prepareStatement(sql)){

            pstmt.setString(1,codeNumber);

            ResultSet rs    = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                iban =  rs.getString("iban");
                card = new Card(rs.getString("number"),rs.getString("pin"));
                balanced = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return new Account(iban,balanced,card);
    }

    // seteaza blocarea cardului
    public void setLock(String codeNumber){
        String sql = "UPDATE card SET lock = 'closed' WHERE number = ?";

        try (PreparedStatement pstmt = connect().prepareStatement(sql)) {
            pstmt.setString(1, codeNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //selecteaza balanta contului
    public double selectBalance(Account account){
        double balanced = 0;

        String sql = "SELECT balance FROM card WHERE number = " + account.getCard().getNumber();

        try (PreparedStatement pstmt  = connect().prepareStatement(sql)){

            ResultSet rs    = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                balanced = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return balanced;
    }

    // seteaza o noua balanta cu suma introdusa
    public void insertIncome(Account account, double income){
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (PreparedStatement pstmt = connect().prepareStatement(sql)) {
            pstmt.setDouble(1, income);
            pstmt.setString(2, account.getCard().getNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //verifica daca exista un iban unde poate fi transferata o suma de bani
    public boolean verifyExistAccountForSendMoney(String iban){
        String sql = "SELECT iban FROM card";

        try (Statement stmt  = connect().createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
                if ((rs.getString("iban").compareTo(iban)==0))
                    return true;
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return false;
    }

    // seteaza noile balante pentru contul de transfer si cel care transfera
    public void transferMoney(String number, String ibanTo, double money){
        String sql1 = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String sql2 = "UPDATE card SET balance = balance + ? WHERE iban = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt1 = conn.prepareStatement(sql1);
             PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
            pstmt1.setDouble(1, money);
            pstmt1.setString(2, number);
            pstmt1.executeUpdate();
            pstmt2.setDouble(1, money);
            pstmt2.setString(2, ibanTo);
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //sterge un cont din baza de date
    public void deleteAccount(Account account){
        String sql = "DELETE FROM card WHERE number = ?";

        try (PreparedStatement pstmt = connect().prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, account.getCard().getNumber());
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //seteaza un nou cod pin
    public void changePin(Account account, String pin){
        String sql = "UPDATE card SET pin = ? WHERE number = ?";

        try (PreparedStatement pstmt = connect().prepareStatement(sql)) {
            pstmt.setString(1, pin);
            pstmt.setString(2, account.getCard().getNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //inchiderea bazei de date
    public void close() {
        try {
            connect().close();
        } catch (SQLException throwables) {
            System.out.println("Closing error!");
        }
    }

}
