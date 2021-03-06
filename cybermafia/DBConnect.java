package cybermafia;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBConnect {
    public static Connection con;
    private static ResultSet rs;

    public DBConnect(){

    }

    public Connection createConnection(){
        Connection connection = null;
        try {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword("CxqSR7sUk5DXNcrE");
            Properties prop = new EncryptableProperties(encryptor);
            prop.load(new FileInputStream("data/cybermafia.properties"));
            String url = prop.getProperty("dataSource.url");
            String uName = prop.getProperty("dataSource.username");
            String uPass = prop.getProperty("dataSource.password");
            connection = DriverManager.getConnection(url, uName, uPass);
        } catch (IOException | SQLException err) {
            System.out.println(err.getMessage());
        }
        return connection;
    }

    public static Connection getConnection(){
        if(con == null){
            DBConnect db = new DBConnect();
            con = db.createConnection();
        }
        return con;
    }

    public static ResultSet selectStatement(PreparedStatement statement){
        try {
            rs = statement.executeQuery();
        } catch (SQLException err){
            System.out.println(err.getMessage());
        }
        return rs;
    }

    public static void executeStatement(PreparedStatement statement){
        try {
            statement.executeUpdate();
        }  catch (SQLException err){
            System.out.println(err.getMessage());
        }
    }

}
