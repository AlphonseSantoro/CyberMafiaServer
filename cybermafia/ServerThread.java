package cybermafia;

import cybermafia.IPHandling;
import cybermafia.Security;
import cybermafia.UserHandling;

import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerThread extends Thread {

    private Socket socket = null;

    public ServerThread(Socket socket){
        super("cybermafia.ServerThread");
        this.socket = socket;
    }

    public void run(){
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream())
        ) {
            String outputLine;
            outputLine = "Successfully established connection";
            out.println(outputLine);
            UserHandling objectInput = (UserHandling) objIn.readObject();

            /* This has no functionality yet. TODO: This will possibly be for retrieval of highscores or something.
            if(objectInput.getSelect()){
                ResultSet rs = cybermafia.DBConnect.selectStatement(objectInput.getSqlStatement1());
                objOut.writeObject(rs);
            }
            */
            if(objectInput.getValidate()){
                outputLine = validateUser(objectInput.getUsername(), objectInput.getPassword());
                objectInput.setAnswer(outputLine);
                objOut.writeObject(objectInput);
            }
            if(objectInput.getRegister()){
                insertNewUserIntoPlayerTable(objectInput.getUsername(), objectInput.getPassword(), objectInput.getEmail());
            }
        } catch (IOException|ClassNotFoundException|SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new user and insert default values into Player table in DB.
     */
    public void insertNewUserIntoPlayerTable(String username, String password, String email) throws SQLException {
        // Prepare statement for user table
        String salt = Security.bytesToHex(Security.getNextSalt());
        String sha256hex = Security.hashString(password, salt);
        String userInsertStatement = "INSERT INTO User (username, password, salt, email) VALUES (?, ?, ?, ?);";
        PreparedStatement userInsert = DBConnect.getConnection().prepareStatement(userInsertStatement);
        userInsert.setString(1, username);
        userInsert.setString(2, sha256hex);
        userInsert.setString(3, salt);
        userInsert.setString(4, email);
        DBConnect.executeStatement(userInsert);

        // Prepare statement for Player table
        String ip = new IPHandling().generateIPv7();
        String playerInsertStatement = "INSERT INTO Player (username, playerIP, pc_CPU_ID, pc_GPU_ID, pc_HDD_ID)" +
                "VALUES (?, ?, 1, 1, 1);";
        PreparedStatement playerInsert = DBConnect.getConnection().prepareStatement(playerInsertStatement);
        playerInsert.setString(1, username);
        playerInsert.setString(2, ip);
        DBConnect.executeStatement(playerInsert);
    }

    /**
     * Validate if username and password is correct
     * @param username
     * @param password
     * @return
     */
    private String validateUser(String username, String password) throws SQLException {
        String userStmt = "SELECT password, salt FROM User WHERE username = ?;";
        PreparedStatement selectUser = DBConnect.getConnection().prepareStatement(userStmt);
        selectUser.setString(1, username);
        ResultSet rs = DBConnect.selectStatement(selectUser);
        String dbPass = "";
        String salt = "";
        while (rs.next()) {
            dbPass = rs.getString("password");
            salt = rs.getString("salt");
        }
        String validatePass = Security.hashString(password, salt);
        if(validatePass.equals(dbPass)){
            String update = "UPDATE user SET lastlogin = current_timestamp() WHERE username = ?;";
            PreparedStatement loginStmt = DBConnect.getConnection().prepareStatement(update);
            loginStmt.setString(1, username);
            DBConnect.executeStatement(loginStmt);
            return "true";
        }
        return "false";
    }
}
