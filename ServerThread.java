import java.io.*;
import java.net.Socket;
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

    private String validateUser(String username, String password){
        ResultSet rs = DBConnect.selectStatement("SELECT password, salt FROM User WHERE username = '" + username + "';");
        UserHandling user = new UserHandling();
        if(user.validateUser(username, password, rs)){
            return "true";
        }
        return "false";
    }
}
