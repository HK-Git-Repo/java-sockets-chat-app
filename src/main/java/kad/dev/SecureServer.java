package main.java.kad.dev;

import java.io.Console;
import java.util.Scanner;

public class SecureServer {
    /*private static String[] databaseCredentials() {
        String[] res = new String[2];
        Console console = System.console();
        if (console == null) {
            System.out.println("No console available");
            System.exit(0);
        }
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter PostgreSQL Username: ");
        String username = scanner.nextLine();
        res[0] = username;

        System.out.print("Enter PostgreSQL Password: ");
        char[] passwordArray = console.readPassword();
        String password = new String(passwordArray);
        res[1] = password;
        return res;
    }*/
    public static void main(String[] args) {

       // String[] credentials = databaseCredentials();
        DatabaseConnection dbInstance = DatabaseConnection.getInstance("postgres", "azerty");

        new SecureSocketServer(dbInstance).start();
    }
}
