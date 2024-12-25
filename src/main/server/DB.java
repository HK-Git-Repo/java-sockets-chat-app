package main.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DB {
    private static final List<Conversation> clients = new ArrayList<>();

    public static void login( Conversation client ) {
        clients.add(client);
    }

    public static List<Conversation> getAll() {
        return clients;
    }

    public static List<String> getClients() {
        return clients.stream().map(Conversation::getLogin).collect(Collectors.toList());
    }

    public static void logout( String clientLogin ) {
        clients.removeIf(client -> Objects.equals(client.getLogin(), clientLogin));
    }

}
