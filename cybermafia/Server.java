package cybermafia;

import java.io.*;
import java.net.ServerSocket;

/**
* Main purpose of this application is to have a server that handles the operations with the database.
* The client will only talk to this server and therefore never have access to the database username and password.
* Username and password will be needed in the future to interact with the database. For now I'm focusing on learing
* to setup a server that a client can communicate with.
*/

public class Server {

    private static boolean listening = true;

        public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Usage: java -jar CyberMafiaServer.jar <Port number>");
            System.exit(1);
            }
                int portNumber = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening){
                new ServerThread(serverSocket.accept()).start();
                }
            } catch (IOException e) {
            e.printStackTrace();
            }
        }
}