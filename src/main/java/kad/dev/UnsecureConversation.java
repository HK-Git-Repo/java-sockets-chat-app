package main.java.kad.dev;


import java.net.Socket;

public class UnsecureConversation extends Conversation {

    public UnsecureConversation(Socket socketClient, DatabaseConnection db) {
        super(socketClient, db);
    }

    @Override
    public void run() {
        this.handleConversation(this.socketClient);
    }
}
