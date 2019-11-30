package server.control;

import java.security.Key;

public class User {

    private final String ip;
    private final int port;

    private String name;
    private Key encryptionKey;

    public User(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.name = "";
    }

    public Key getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(Key encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isSignedIn() {
        return name != null;
    }
}
