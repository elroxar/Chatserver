package server;

import libary.network.server.Server;

import java.security.Key;
import java.security.KeyPair;

import static util.Cryptography.*;

public class ChatServer extends Server {

    private Handler handler;

    public ChatServer(int port) {
        super(port);
    }

    @Override
    public void processNewConnection(String ip, int port) {
        User user = handler.addUser(ip, port);
        KeyPair kP = generateKeyPair();
        user.setEncryptionKey(kP.getPrivate());
        send(ip, port, "INIT_KEY;" + keyToString(kP.getPublic()));
    }

    @Override
    public void processMessage(String ip, int port, String message) {
        User user = handler.getUser(ip, port);
        if(!user.isSignedIn())
        {
            Key finalKey = keyAgreement(user.getEncryptionKey(), message);
            user.setEncryptionKey(finalKey);
           return;
        }
        String[] inst = decryptAES(user.getEncryptionKey(), message).split(";");
        if(inst.length > 0 && generateMac(user.getEncryptionKey(), message.substring(0, message.lastIndexOf(";"))).equals(inst[inst.length-1])) {

        } else {

        }
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        handler.removeUser(pClientIP, pClientPort);
    }
}
