package main.java.kad.dev;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

public class SecureSocketServer extends Thread {
    private final String KEYSTORE_PASSWORD = System.getenv("CHAT_APP_JAVA_SSLKEY");
    private final DatabaseConnection db;

    public SecureSocketServer(DatabaseConnection db) {
        this.db = db;
    }

    private SSLContext createSSLContext() {
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            String KEYSTORE_PATH = "server.jks";
            FileInputStream keystoreFile = new FileInputStream(new File(KEYSTORE_PATH).getAbsolutePath());
            keystore.load(keystoreFile, KEYSTORE_PASSWORD.toCharArray());
            keystoreFile.close();

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, KEYSTORE_PASSWORD.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keystore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(
                    keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    null
            );

            return sslContext;
        } catch (Exception  e) {
            System.err.println("Error creating SSL context: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            //System.setProperty("javax.net.debug", "ssl,handshake");

            SSLContext sslContext = createSSLContext();

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            int PORT = 1000;
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);

            serverSocket.setEnabledProtocols(new String[] {"TLSv1.2", "TLSv1.3"});

            System.out.println("Server Socket Running with [SECURE SSL/TLS] connection on "
                    + HostIP.IP() + ":" + PORT);

            while (true) {
                System.out.println("Listening ...");
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                System.out.println("\t** Client with Address "
                        + socket.getInetAddress().toString() + " is connected **");
                new SecureConversation(socket, this.db).start();
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}