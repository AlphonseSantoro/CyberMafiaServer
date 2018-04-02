package cybermafia;

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
             ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream())
        ) {
            String outputLine;
            outputLine = "Connected...";
            out.println(outputLine);
            UserHandling objectInput = (UserHandling) objIn.readObject();

            /**
             * Get the currents users profile and return the values as a UserHandling object
             */
            if(objectInput.getProfile()){
                String stmt = "SELECT p.username, p.playerip, c.ghz as CGhz, g.ghz as GGhz, h.size " +
                        "FROM player AS p JOIN pc_cpu AS c ON p.pc_cpu_id = c.id " +
                        "JOIN pc_gpu AS g ON p.pc_gpu_id = g.id " +
                        "JOIN pc_hdd AS h ON p.pc_hdd_id = h.id " +
                        "WHERE p.username = ?;";
                PreparedStatement preparedStatement = cybermafia.DBConnect.getConnection().prepareStatement(stmt);
                preparedStatement.setString(1, objectInput.getUsername());
                ResultSet rs = DBConnect.selectStatement(preparedStatement);
                UserHandling user = setValuesFromResultSet(rs);
                objOut.writeObject(user);
            }

            /**
             * Get a users profile and return the values as a UserHandling object
             */
            if(objectInput.getIPadress()){
                String stmt = "SELECT p.username, p.playerip, c.ghz as CGhz, g.ghz as GGhz, h.size " +
                        "FROM player AS p JOIN pc_cpu AS c ON p.pc_cpu_id = c.id " +
                        "JOIN pc_gpu AS g ON p.pc_gpu_id = g.id " +
                        "JOIN pc_hdd AS h ON p.pc_hdd_id = h.id " +
                        "WHERE p.playerip = ?;";
                PreparedStatement preparedStatement = cybermafia.DBConnect.getConnection().prepareStatement(stmt);
                preparedStatement.setString(1, objectInput.getIP());
                ResultSet rs = DBConnect.selectStatement(preparedStatement);
                UserHandling user = setValuesFromResultSet(rs);
                objOut.writeObject(user);
            }

            /**
             * Validate if username and password is correct, return a UserHanding object with boolean value set
             */
            if(objectInput.getValidate()){
                boolean answer = validateUser(objectInput.getUsername(), objectInput.getPassword());
                objectInput.setAnswer(answer);
                objOut.writeObject(objectInput);
            }
            /**
             * Insert new user into database
             */
            if(objectInput.getRegister()){
                insertNewUserIntoPlayerTable(objectInput.getUsername(), objectInput.getPassword(), objectInput.getEmail());
            }
        } catch (IOException|ClassNotFoundException|SQLException e) {
            e.printStackTrace();
        }
    }

    private UserHandling setValuesFromResultSet(ResultSet rs) throws SQLException {
        UserHandling user = new UserHandling();
        while(rs.next()){
            user.setUsername(rs.getString("username"));
            user.setIp(rs.getString("playerip"));
            user.setCpu(rs.getString("CGhz"));
            user.setGpu(rs.getString("GGhz"));
            user.setHdd(rs.getString("size"));
        }
        return user;
    }

    /**
     * Create a new user and insert default values into Player table in DB.
     */
    public void insertNewUserIntoPlayerTable(String username, String password, String email) throws SQLException {
        // Prepare statement for user table
        String salt = Security.bytesToHex(Security.getNextSalt());
        String sha256hex = Security.hashString(password, salt);
        String userInsertStatement = "INSERT INTO user (username, password, salt, email) VALUES (?, ?, ?, ?);";
        PreparedStatement userInsert = DBConnect.getConnection().prepareStatement(userInsertStatement);
        userInsert.setString(1, username);
        userInsert.setString(2, sha256hex);
        userInsert.setString(3, salt);
        userInsert.setString(4, email);
        DBConnect.executeStatement(userInsert);

        // Prepare statement for Player table
        String ip = new IPHandling().generateIPv7();
        String playerInsertStatement = "INSERT INTO player (username, playerip, pc_cpu_id, pc_gpu_id, pc_hdd_id)" +
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
    private boolean validateUser(String username, String password) throws SQLException {
        String userStmt = "SELECT password, salt FROM user WHERE username = ?;";
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
            return true;
        }
        return false;
    }
}
