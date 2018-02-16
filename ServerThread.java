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

            if(objectInput.getSelect()){
                ResultSet rs = DBConnect.selectStatement(objectInput.getSqlStatement1());
                objOut.writeObject(rs);
            }
            if(objectInput.getValidate()){
                outputLine = validateUser(objectInput.getUsername(), objectInput.getPassword());
                objectInput.setAnswer(outputLine);
                objOut.writeObject(objectInput);
            }
            if(objectInput.getRegister()){
                System.out.println(objectInput.getUsername() +  " " + objectInput.getPassword() + " " + objectInput.getEmail());
                objectInput.insertNewUserIntoPlayerTable();
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

    /**
     * Create a new user and insert default values into Player table in DB.
     */
    public void insertNewUserIntoPlayerTable() throws SQLException {
        String salt = bytesToHex(getNextSalt());
        String sha256hex = hashString(this.password, salt);
        String userInsertStatement = "INSERT INTO User (username, password, salt, email) " +
                "VALUES ('" + this.username + "', '" + sha256hex + "', '" + salt + "', '" + this.email + "');";
        sqlStatement1 = userInsertStatement;
        String playerInsertStatement = "INSERT INTO Player (username, playerIP, pc_CPU_ID, pc_GPU_ID, pc_HDD_ID)" +
                "VALUES ('" + this.username + "', '" + new IPHandling().generateIPv7() + "', 1, 1, 1);";
        sqlStatement2 = playerInsertStatement;
    }
}
