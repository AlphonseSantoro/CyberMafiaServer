import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerThread extends Thread {

    private Socket socket = null;

    public ServerThread(Socket socket){
        super("ServerThread");
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
                ResultSet rs = DBConnect.selectStatement(objectInput.getSqlStatement1());
                objOut.writeObject(rs);
            }
            */
            if(objectInput.getValidate()){
                outputLine = validateUser(objectInput.getUsername(), objectInput.getPassword());
                objectInput.setAnswer(outputLine);
                objOut.writeObject(objectInput);
            }
            if(objectInput.getRegister()){
                insertNewUserIntoPlayerTable();
                DBConnect.executeStatement(objectInput.getSqlStatement1());
                DBConnect.executeStatement(objectInput.getSqlStatement2());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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

        // Prepare statement for Player table
        String ip = new IPHandling().generateIPv7();
        String playerInsertStatement = "INSERT INTO Player (username, playerIP, pc_CPU_ID, pc_GPU_ID, pc_HDD_ID)" +
                "VALUES (?, ?, 1, 1, 1);";
        PreparedStatement playerInsert = DBConnect.getConnection().prepareStatement(playerInsertStatement);
        playerInsert.setString(1, username);
        playerInsert.setString(2, ip);
    }

    private String validateUser(String username, String password) throws SQLException {
        String userStmt = "SELECT password, salt FROM User WHERE username = ?;";
        PreparedStatement selectUser = DBConnect.getConnection().prepareStatement(userStmt);
        selectUser.setString(1, username);
        ResultSet rs = DBConnect.selectStatement(selectUser);
        UserHandling user = new UserHandling();
        if(user.validateUser(username, password, rs)){
            String update = "UPDATE user SET lastlogin = current_timestamp() WHERE username = ?;";
            PreparedStatement loginStmt = DBConnect.getConnection().prepareStatement(update);
            loginStmt.setString(1, username);
            DBConnect.executeStatement(loginStmt);
            return "true";
        }
        return "false";
    }

    //TODO: FIND A WAY TO MERGE THIS AND THE ABOVE METHOD

    /**
     * Validate if username and password is correct
     * @param userName
     * @param passWord
     * @return
     */
    public boolean validateUser(String userName, String passWord, ResultSet rs){
        String dbPass = "";
        String salt = "";
        try {
            while (rs.next()) {
                dbPass = rs.getString("password");
                salt = rs.getString("salt");
            }
        } catch (SQLException err){
            System.out.println(err.getMessage());
        }
        String validatePass = hashString(passWord, salt);
        if(!validatePass.equals(dbPass)){
            System.out.println("Access Denied: Wrong password");
            return false;
        }
        System.out.println("Access Granted");
        return true;
    }
}
