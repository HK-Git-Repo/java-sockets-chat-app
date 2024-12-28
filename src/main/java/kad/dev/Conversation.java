package main.java.kad.dev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Conversation extends Thread {
    protected final Socket socketClient;
    protected final DatabaseConnection db;

    protected Conversation(Socket socketClient, DatabaseConnection db) {
        this.socketClient = socketClient;
        this.db = db;
    }

    protected void broadcast(String message) {

        try {
            String sender = this.db.getAllConnectedUsers().stream()
                    .filter(client -> this.db.getSessionSocket(client).equals(this.socketClient))
                    .findFirst()
                    .orElse("Unknown");

            for (String client : this.db.getAllConnectedUsers()) {
                try {
                    PrintWriter pw = new PrintWriter(this.db.getSessionSocket(client).getOutputStream(), false);
                    if (!sender.equals(client)) {
                        pw.println("\n(" + sender + ") " + message);
                    }
                    pw.print("> ");
                    pw.flush();
                } catch (IOException e) {
                    System.err.println("Error sending message to client: " + client);
                }
            }
        } catch (Exception e) {
            System.err.println("Broadcast error: " + e.getMessage());
        }
    }

    protected void unicast(String message, String clientLogin) {

        try {
            String sender = this.db.getAllConnectedUsers().stream()
                    .filter(client -> this.db.getSessionSocket(client).equals(this.socketClient))
                    .findFirst()
                    .orElse("Unknown");

            String recipient = this.db.getAllConnectedUsers().stream()
                    .filter(client -> client.equals(clientLogin))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            try {
                PrintWriter pw = new PrintWriter(this.db.getSessionSocket(recipient).getOutputStream(), true);
                pw.println("\n(" + sender + ") " + message);
                pw.print("> ");
                pw.flush();
            } catch (IOException e) {
                System.err.println("Error sending message to " + recipient + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Unicast error: " + e.getMessage());
        }
    }

    protected void handleConversation(Socket socket) {
        String login = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            pw.print("login> ");
            pw.flush();
            login = bufferedReader.readLine().toLowerCase();

            this.db.login(login, socket);
            pw.println("\n\t** You are logged in successfully as: [" + login + "] **\n");

            pw.print("> ");
            pw.flush();

            while (true) {
                String msg = bufferedReader.readLine();
                if (msg == null) {
                    break;
                }
                if (msg.startsWith("@")) {
                    int firstSpaceIndex = msg.indexOf(" ");
                    if (firstSpaceIndex > 0) {
                        String receiver = msg.substring(1, firstSpaceIndex);
                        String content = msg.substring(firstSpaceIndex + 1);

                        if (this.db.getAllConnectedUsers().contains(receiver)) {
                            this.unicast(content, receiver);
                            pw.print("> ");
                            pw.flush();
                        }
                    }
                }
                else {
                    this.broadcast(msg);
                }
            }
        } catch (IOException e) {

        } finally {
            try {
                this.db.logout(login);
                socket.close();
                System.err.println("[Client logged by: "+login+"] is Disconnected! ");
            } catch (IOException e) {}
        }
    }

    @Override
    public abstract void run();
}
