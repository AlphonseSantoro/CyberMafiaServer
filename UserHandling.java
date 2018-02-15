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
    private boolean select, execute, validate, register;
    private String sqlStatement1, sqlStatement2, username, password, email, ip, answer;
    private ResultSet resultSet;

    /**
     * Construct a connection to DB
     */
    public UserHandling(){
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

    /**
     * Set SQL statement string
     * @param sqlStatement1
     */
    public void setSqlStatement1(String sqlStatement1){
        this.sqlStatement1 = sqlStatement1;
    }

    /**
     * Returns an SQL statement
     * @return
     */
    public String getSqlStatement1(){
        return this.sqlStatement1;
    }

    /**
     * Set SQL statement string
     * @param sqlStatement2
     */
    public void setSqlStatement2(String sqlStatement2){
        this.sqlStatement2 = sqlStatement2;
    }

    /**
     * Returns an SQL statement
     * @return
     */
    public String getSqlStatement2(){
        return this.sqlStatement2;
    }

    /**
     * Set string username
     * @param username
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Returns string username
     * @return Username of type String
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Set String password
     * @param password
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Returns String password
     * @return Password of type String
     */
    public String getPassword(){
        return this.password;
    }

    /**
     * Set String email
     * @param email
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * Get String email
     * @return
     */
    public String getEmail(){
        return this.email;
    }

    /**
     * Set String IP
     * @param ip
     */
    public void setIp(String ip){
        this.ip = ip;
    }

    /**
     * Get String IP
     * @return
     */
    public String getIP(){
        return this.ip;
    }

    /**
     * Set String answer
     * @param answer
     * @return
     */
    public void setAnswer(String answer){
        this.answer = answer;
    }

    /**
     * Get String answer
     * @return
     */
    public String getAnswer(){
        return this.answer;
    }

    /**
     * Set boolean register
     * @param register
     */
    public void setRegister(boolean register){
        this.register = register;
    }

    /**
     * Get boolean register
     * @return
     */
    public boolean getRegister(){
        return this.register;
    }

    /**
     * Set Select boolean
     * @param select
     */
    public void setSelect(boolean select){
        this.select = select;
    }

    /**
     * Set boolean Validate
     * @param validate
     */
    public void setValidate(boolean validate){
        this.validate = validate;
    }

    /**
     * Get boolean Validate
     * @return
     */
    public boolean getValidate(){
        return this.validate;
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
     * Get resultset
     * @return
     */
    public ResultSet getResultSet(){
        return this.resultSet;
    }
}
