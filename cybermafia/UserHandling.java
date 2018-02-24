package cybermafia; /**
 * This class is meant for storing data and sending it to the server,
 * the server will retrieve data from this object and create the necessary queries.
 * When the client needs data from the server it is stored in resultSet.
 */

import java.io.Serializable;
import java.sql.ResultSet;

public class UserHandling implements Serializable{
    private static final long serialVersionUID = 1L; // Needed to de-Serialize object, class on server-side and client-side have same UID.
    private boolean select, execute, validate, register;
    private String username, password, email, ip, answer;
    private ResultSet resultSet; // Contains table from a query


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
