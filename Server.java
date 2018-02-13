import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Properties;

/**
 * Main purpose of this application is to have a server that handles the operations with the database.
 * The client will only talk to this server and therefore never have access to the database username and password.
 * Username and password will be needed in the future to interact with the database. For now I'm focusing on learing
 * to setup a server that a client can communicate with.
 */
public class Server {


    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Usage: java CyberMafiaServer <Port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                ) {
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

    private static String validateUser(String username, String password){
        UserHandling user = new UserHandling();
        if(user.validateUser(username, password)){
            return "true";
        }
        return "false";
    }
}
