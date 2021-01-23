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

    //Connect to database
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

    //Create new database
    public void createNewDatabase() {

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn == null) {
                System.out.println("A new database NOT been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Create table Card
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

    //Verify if exist number/pin in database and return "false" if exist
    public boolean verifyExist(String codeIban,String codeNumber, String codePin){
        String sql = "SELECT iban, number, pin FROM card";

        try (Statement stmt  = connect().createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                if ( rs.getString("iban").compareTo(codeIban)==0||
                        rs.getString("number").compareTo(codeNumber)==0 ||
                        rs.getString("pin").compareTo(codePin)==0)
                    return false;
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return true;
    }

    //Insert a new card
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

    //Verify if exist card
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

    //select the account after login
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

    public void setLock(String codeNumber){
        String sql = "UPDATE card SET lock = 'closed' WHERE number = ?";

        try (PreparedStatement pstmt = connect().prepareStatement(sql)) {
            pstmt.setString(1, codeNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //select the account balance
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

    public void transferMoney(String number, String numberTo, double money){
        String sql1 = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String sql2 = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt1 = conn.prepareStatement(sql1);
             PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
            pstmt1.setDouble(1, money);
            pstmt1.setString(2, number);
            pstmt1.executeUpdate();
            pstmt2.setDouble(1, money);
            pstmt2.setString(2, numberTo);
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //delete the account from the database
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

    public boolean existPin(String codePin){
        String sql = "SELECT pin FROM card";

        try (Statement stmt  = connect().createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                if (rs.getString("pin").compareTo(codePin)==0)
                    return false;
            }
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return true;
    }

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

    //close database
    public void close() {
        try {
            connect().close();
        } catch (SQLException throwables) {
            System.out.println("Closing error!");
        }
    }

}
