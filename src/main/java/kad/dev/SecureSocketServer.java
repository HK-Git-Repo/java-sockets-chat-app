package main.java.kad.dev;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SecureSocketServer extends Thread {
    private final int PORT = 1000;

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(this.PORT)) {
            System.out.println("Server Socket Running with [NON SECURE] connexion on "+ HostIP.IP()+":"+this.PORT);
            while (true) {
                System.out.println("Listening ...");
                Socket socket = serverSocket.accept();
                System.out.println("\t** Client with Address "+ socket.getRemoteSocketAddress().toString()+" is connected **");
                new Conversation(socket).start();
            }
        } catch (IOException e) {

        }
    }
}
