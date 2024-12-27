package main.java.kad.dev;

import main.java.kad.dev.Conversation;
import main.java.kad.dev.DatabaseConnection;

import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class SecureConversation extends Conversation {

    public SecureConversation(SSLSocket socketClient, DatabaseConnection db) {
        super(socketClient, db);
    }

    @Override
    public void run() {
        SSLSocket socket = ((SSLSocket) this.socketClient);
        try {
            socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
            socket.setUseClientMode(false);
            socket.setNeedClientAuth(false);

            try {
                socket.startHandshake();
            } catch (IOException e) {
                System.err.println("SSL Handshake failed: " + e.getMessage());
                socket.close();
                return;
            }
            this.handleConversation(socket);
        } catch (IOException e) {
            System.err.println("Client logout");
        }
    }
}