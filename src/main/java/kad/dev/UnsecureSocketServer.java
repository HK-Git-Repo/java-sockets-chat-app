package main.java.kad.dev;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UnsecureSocketServer extends Thread {
    private final DatabaseConnection db;

    public UnsecureSocketServer(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(1000)) {
            System.out.println("Server Socket Running with [UNSECURE] connexion on "+ HostIP.IP()+":1000");
            while (true) {
                System.out.println("Listening ...");
                Socket socket = serverSocket.accept();
                System.out.println("\t** Client with Address "+ socket.getRemoteSocketAddress().toString()+" is connected **");
                new UnsecureConversation(socket, this.db).start();
            }
        } catch (IOException e) {

        }
    }
}