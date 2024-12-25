package main.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Conversation extends Thread {
    private Socket socketClient;
    private String login;

    public Conversation(Socket socketClient) {
        this.socketClient = socketClient;
    }

    public String getLogin() {
        return this.login;
    }

    private void broadcast(String message, Socket socket)  {
        try{
            String sender = "";
            for( Conversation client : DB.getAll() ) {
                if(client.socketClient == socket) {
                    sender = client.getLogin();
                }
            }
            for( Conversation client : DB.getAll() ) {
                PrintWriter pw = new PrintWriter(client.socketClient.getOutputStream(), true);
                if ( client.socketClient != socket ) {
                    pw.println();
                    pw.println("("+sender+") "+message);
                }
                    pw.print("> ");
                    pw.flush();
            }
        } catch (IOException e) {}

    }

    private void unicast(String message, String clientLogin, Socket socket)  {
        try{
            String sender = "";
            for( Conversation client : DB.getAll() ) {
                if(client.socketClient == socket) {
                    sender = client.getLogin();
                }
            }
            Conversation conversation = DB.getAll().stream().filter(c -> c.getLogin().equals(clientLogin)).findFirst().get();
            PrintWriter pw = new PrintWriter(conversation.socketClient.getOutputStream(), true);
            pw.println();
            pw.println("("+sender+") "+message);
            pw.print("> ");
            pw.flush();
        } catch (IOException e) {}
    }

    @Override
    public void run() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(this.socketClient.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            PrintWriter pw = new PrintWriter(this.socketClient.getOutputStream(), true);

            pw.print("login> ");
            pw.flush();
            this.login = bufferedReader.readLine();

            DB.login(this);
            pw.println("\n\t** You are logged in successfully as: [" + this.login + "] **\n");

            pw.print("> ");
            pw.flush();

            while (true) {
                String msg = bufferedReader.readLine();
                if (msg == null) {
                    break;
                }
                if( msg.startsWith("@") ) {
                    String[] requestParams = msg.split(" ");
                    if( requestParams.length == 2 ) {
                        String receiver = requestParams[0].substring(1);
                        if( DB.getClients().contains(receiver) ) {
                            String content = requestParams[1];
                            this.unicast(content, receiver, this.socketClient);
                            pw.print("> ");
                            pw.flush();
                        }
                    }
                } else {
                    this.broadcast(msg, this.socketClient);
                }

            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }

}
