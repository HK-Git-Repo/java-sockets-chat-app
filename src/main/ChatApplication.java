package main;

import main.server.HostIP;
import main.server.SecureSocketServer;

public class ChatApplication {
    public static void main(String[] args) {
       new SecureSocketServer().start();
    }
}
