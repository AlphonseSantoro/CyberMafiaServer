import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;

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
                ResultSet rs = DBConnect.selectStatement(objectInput.getSqlStatement());

            }

            String username = in.readLine();
            String password = in.readLine();
            outputLine = validateUser(username, password);
            out.println(outputLine);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String validateUser(String username, String password){
        UserHandling user = new UserHandling();
        if(user.validateUser(username, password)){
            return "true";
        }
        return "false";
    }
}
