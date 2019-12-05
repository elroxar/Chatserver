package server.control;

import javax.crypto.SecretKey;
import java.security.PrivateKey;

/**
 * a signed in chat user
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class User {

    private final String ip;
    private final int port;

    private String name;
    // just using the key interface would save memory but so it's saver
    private SecretKey encryptionKey;
    private PrivateKey privateKey;

    /**
     * constructor
     *
     * @param ip   ip
     * @param port port
     */
    public User(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.name = "";
    }

    /**
     * gets the private key for key agreement
     *
     * @return key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * sets the private key for key agreement
     *
     * @param privateKey key
     */
    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * gets encryption key
     *
     * @return key for aes encryption
     */
    public SecretKey getEncryptionKey() {
        return encryptionKey;
    }

    /**
     * sets encryption key
     *
     * @param encryptionKey key for aes encryption
     */
    public void setEncryptionKey(SecretKey encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * gets name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * sets name
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets ip
     *
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * gets port
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * tests, if the user is signed in
     *
     * @return <code>true</code>, if the user is signed in, otherwise <code>false</code>
     */
    public boolean isSignedIn() {
        return name != null;
    }
}
