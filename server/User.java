package server;

import java.security.Key;

public class User {

    private final String ip;
    private final int port;

    private int id;
    private Key encryptionKey;

    public User(String ip, int port) {
        this.ip = ip;
        this.port = port;
        id = -1;
    }

    public Key getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(Key encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isSignedIn() {
        return id != -1;
    }
}
