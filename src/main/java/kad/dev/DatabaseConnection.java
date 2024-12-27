package main.java.kad.dev;

import java.net.Socket;
import java.sql.*;

import javax.net.ssl.SSLSocket;
import java.util.*;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private final Map<String, Socket> session = new HashMap<>();

    private DatabaseConnection(String username, String password) {
        try {
            String url = "jdbc:postgresql://localhost/chatapp";
            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("ssl", "false");

            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, props);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Failed to connect to the database! caused by : "+e.getMessage());
        }
    }

    public static DatabaseConnection getInstance(String username, String password) {
        if (instance == null) {
            instance = new DatabaseConnection(username, password);
        }
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean login(String login, Socket socket) {
        try {
            String checkQuery = """
            SELECT COUNT(*) AS userCount FROM users WHERE login = ?
            """;
            try (PreparedStatement checkStmt = this.connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, login);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt("userCount") == 0) {
                    String insertQuery = """
                    INSERT INTO users (login, isConnected) VALUES (?, TRUE)
                    """;
                    try (PreparedStatement insertStmt = this.connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, login);
                        int rowsInserted = insertStmt.executeUpdate();
                        if (rowsInserted > 0) {
                            this.session.put(login, socket);
                            return true;
                        }
                    }
                } else {
                    String updateQuery = """
                    UPDATE users SET isConnected = TRUE WHERE login = ?
                    """;
                    try (PreparedStatement updateStmt = this.connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, login);
                        int rowsUpdated = updateStmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            this.session.put(login, socket);
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return false;
    }

    public Socket getSessionSocket(String login) {
        return this.session.get(login);
    }

    public List<String> getAllConnectedUsers() {
        List<String> connectedUsers = new ArrayList<>();
        String query = """
        SELECT login FROM users WHERE isConnected = TRUE
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                connectedUsers.add(rs.getString("login"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return connectedUsers;
    }

    public boolean logout(String login) {
        try {
            String updateQuery = """
            UPDATE users SET isConnected = FALSE WHERE login = ?
            """;
            try (PreparedStatement stmt = this.connection.prepareStatement(updateQuery)) {
                stmt.setString(1, login);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    this.session.remove(login);
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return false;
    }
}
