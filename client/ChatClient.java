package client;

import libary.network.client.Client;

import java.security.Key;
import java.security.KeyPair;

import static util.Cryptography.*;

public class ChatClient extends Client {

    private Key encryptionKey;

    public ChatClient(String ip, int port) {
        super(ip, port);
    }

    @Override
    public void processMessage(String message) {
        if (encryptionKey == null) {
            String[] inst = message.split(";");
            if (inst.length == 2 && inst[0].equals("INIT_KEY"))
                initKey(inst[1]);
        } else {
            message = decryptAES(encryptionKey, message);
            String[] inst = message.split(";");
            message = message.substring(0, message.lastIndexOf(";"));
            if (generateMac(encryptionKey, message).equals(inst[inst.length - 1])) {
                // schreib hier einfach deinen code...
            } else {
                // wenn Angreifer Nachricht verändert hat, schließe die Verbindung, da unsicher
                System.err.println("connection insecure");
                close();
            }
        }
    }

    private void initKey(String key) {
        KeyPair kP = generateKeyPair();
        encryptionKey = keyAgreement(kP.getPrivate(), key);
        send("INIT_KEY;" + keyToString(kP.getPublic()));
    }

    @Override
    public void send(String message) {
        if (encryptionKey == null) {
            super.send(message);
        } else {
            String mac = generateMac(encryptionKey, message);
            message = message + ";" + mac;
            message = encryptAES(encryptionKey, message);
            super.send(message);
        }
    }
}
