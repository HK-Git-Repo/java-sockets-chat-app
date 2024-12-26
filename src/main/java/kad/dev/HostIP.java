package main.java.kad.dev;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostIP {
    public static String IP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}
