package main.java.kad.dev;

import java.io.Console;
import java.util.Scanner;

public class SecureServer {
    private static String[] credentials = new String[2];
    private static void databaseCredentials() {

        Console console = System.console();
        if (console == null) {
            System.out.println("No console available");
            System.exit(0);
        }
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter PostgreSQL Username: ");
        String username = scanner.nextLine();
        credentials[0] = username;

        System.out.print("Enter PostgreSQL Password: ");
        char[] passwordArray = console.readPassword();
        String password = new String(passwordArray);
        credentials[1] = password;
    }
    public static void main(String[] args) {

        databaseCredentials();
        DatabaseConnection dbInstance = DatabaseConnection.getInstance(credentials[0], credentials[1]);
        System.setProperty("javax.net.debug", "");
        new SecureSocketServer(dbInstance).start();
    }
}
