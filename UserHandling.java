import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class UserHandling implements Serializable{
    private static final Random random = new SecureRandom();
    private static final long serialVersionUID = 1L;
    private Connection connect;
    private boolean select, execute = false;
    private String sqlStatement;
    private ResultSet resultSet;

    /**
     * Constructor to create a new user.
     * @param userName A unique username for identification
     * @param password The new users password
     * @param email The new users email
     * @param ip The users in-game IP
     */
    public UserHandling(String userName, String password, String email, String ip){
        connect = DBConnect.getConnection();
        insertNewUserIntoPlayerTable(userName, password, email, ip);
    }

    /**
     * Construct a connection to DB
     */
    public UserHandling(){
        connect = DBConnect.getConnection();
    }


    /**
     * Get a random 16 byte salt.
     * @return
     */
    private static byte[] getNextSalt(){
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Convert bytes to hex
     * @param hash
     * @return Hex in type string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Validate if username and password is correct
     * @param userName
     * @param passWord
     * @return
     */
    public boolean validateUser(String userName, String passWord){
        ResultSet rs = DBConnect.selectStatement("SELECT password, salt FROM User WHERE username = '" + userName + "';");

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
     * Hash password with a salt that is generated for each user.
     * @param password
     * @param salt
     * @return
     */
    public String hashString(String password, String salt){
        String saltPlussPass = salt + password;
        String sha256hex = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    saltPlussPass.getBytes(StandardCharsets.UTF_8)
            );
            sha256hex = bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException err) {
            err.getStackTrace();
        }
        return sha256hex;
    }

    /**
     * Create a new user and insert default values into Player table in DB.
     * @param newUserName
     * @param password
     * @param email
     * @param ip
     */
    public void insertNewUserIntoPlayerTable(String newUserName, String password, String email, String ip){
        String salt = bytesToHex(getNextSalt());
        String sha256hex = hashString(password, salt);
        String userInsertStatement = "INSERT INTO User (username, password, salt, email) " +
                                     "VALUES ('" + newUserName + "', '" + sha256hex + "', '" + salt + "', '" + email + "');";
        DBConnect.executeStatement(userInsertStatement);
        String playerInsertStatement = "INSERT INTO Player (username, playerIP, pc_CPU_ID, pc_GPU_ID, pc_HDD_ID)" +
                             "VALUES ('" + newUserName + "', '" + ip + "', 1, 1, 1);";
        DBConnect.executeStatement(playerInsertStatement);
    }

    /**
     * Set SQL statement string
     * @param sqlStatement
     */
    public void setSqlStatement(String sqlStatement){
        this.sqlStatement = sqlStatement;
    }

    /**
     * Returns an SQL statement
     * @return
     */
    public String getSqlStatement(){
        return this.sqlStatement;
    }

    /**
     * Set Select boolean
     * @param select
     */
    public void setSelect(boolean select){
        this.select = select;
    }

    /**
     * Get Select boolean
     * @return
     */
    public boolean getSelect(){
        return this.select;
    }

    /**
     * Set Execute boolean
     * @param execute
     */
    public void setExecute(boolean execute){
        this.execute = execute;
    }

    /**
     * Get Execute boolean
     * @return
     */
    public boolean getExecute(){
        return this.execute;
    }

    /**
     * Set resultset
     * @return
     */
    public void setResultSet(ResultSet resultSet){
        this.resultSet = resultSet;
    }

    /**
     * Returns a resultset
     * @return
     */
    public ResultSet getResultSet(){
        return this.resultSet;
    }
}
