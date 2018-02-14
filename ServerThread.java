import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

    private Socket socket = null;

    public ServerThread(Socket socket){
        super("ServerThread");
        this.socket = socket;
    }


    public void run(){
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String outputLine;
            outputLine = "Successfully established connection";
            out.println(outputLine);
            String username = in.readLine();
            String password = in.readLine();
            outputLine = validateUser(username, password);
            out.println(outputLine);

        } catch (IOException e) {
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
