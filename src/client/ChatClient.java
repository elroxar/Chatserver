package client;

import client.gui.GUI;
import client.gui.chatviewer.Chat;
import client.gui.chatviewer.ChatViewer;
import libary.network.client.Client;

import javax.crypto.SecretKey;
import java.security.KeyPair;

import static util.Cryptography.*;

/**
 * chat client
 *
 * @author Stefan Christian Kohlmeier
 * @version 09.12.2019
 */
public class ChatClient extends Client implements AutoCloseable {

    private final static String ip = "localhost";
    private final static int port = 4000;

    private GUI gui;
    private SecretKey encryptionKey;

    /**
     * constructor
     *
     * @param gui gui
     */
    public ChatClient(GUI gui) {
        super(ip, port);
        this.gui = gui;
    }

    public static void main(String[] args) {
        new GUI();
    }

    /**
     * sends the the server the instruction to join a group
     *
     * @param group group
     */
    public void joinGroup(String group) {
        send("JOIN_GROUP;" + group);
    }

    /**
     * sends the server the instruction to create a group
     *
     * @param group group
     */
    public void createGroup(String group) {
        send("CREATE_GROUP;" + group);
    }

    /**
     * sends the server the instruction to leave a group
     *
     * @param group group
     */
    public void leaveGroup(String group) {
        send("LEAVE_GROUP;" + group);
    }

    /**
     * sends the server the instruction to leave a chat
     *
     * @param chat chat
     */
    public void leaveChat(String chat) {
        send("LEAVE_CHAT;" + chat);
    }

    /**
     * sends the server the instruction to sign in
     *
     * @param username username
     * @param password password
     */
    public void signIn(String username, String password) {
        send("SIGN_IN;" + username + ";" + password);
    }

    /**
     * sends the server the instruction to sign out
     */
    public void signOut() {
        send("SIGN_OUT");
    }

    /**
     * sends the server the instruction to send a group message
     *
     * @param group   group
     * @param message message
     */
    public void sendGroupMessage(String group, String message) {
        send("SEND_GROUP_MESSAGE;" + group + ";" + message);
    }

    /**
     * sends the server the instruction to send a chat message
     *
     * @param chat    chat
     * @param message message
     */
    public void sendChatMessage(String chat, String message) {
        send("SEND_CHAT_MESSAGE;" + chat + ";" + message);
    }

    @Override
    public void processMessage(String message) {
        // Diffie-Hellmann key agreement
        if (message.indexOf("INIT_KEY;") == 0) {
            KeyPair kp = generateKeyPair();
            // set AES security key
            encryptionKey = keyAgreement(kp.getPrivate(), message.substring(9));
            super.send("INIT_KEY;" + keyToString(kp.getPublic()));
            return;
        } else if (encryptionKey == null) {
            super.send("-;NOT_PROPERLY_CONNECTED");
            return;
        }
        message = decryptAES(encryptionKey, message);
        System.out.println(message);
        String[] splt = message.split(";");
        ChatViewer cv = gui.getChatViewer();
        message = message.substring(0, message.length() - splt[splt.length - 1].length() - 1);
        if (!macCheck(message, splt[splt.length - 1])) {
            System.err.println("Insecure connection!");
            super.send("-;WRONG MAC");
        } else if (splt[0].equals("-")) {
            System.err.println(message.substring(0, message.length() - splt[splt.length - 1].length() - 1));
        } else if (splt[0].equals("+")) {
            if (splt.length == 3 && (splt[1].equals("SIGN_IN") || splt[1].equals(("REGISTER")))) {
                gui.signIn(this);
                send("GET_GROUPS");
                send("GET_CHATS");
            } else if (splt.length == 4) {
                if (splt[1].equals("CREATE_GROUP")) {
                    cv.addChat(new Chat(this, cv, splt[2], true));
                } else if (splt[1].equals("JOIN_GROUP")) {
                    cv.addChat(new Chat(this, cv, splt[2], true));
                    send("GET_GROUP_MESSAGES;" + splt[2]);
                }
            } else if (splt[1].equals("SEND_CHATS")) {
                for (int i = 2; i < splt.length - 1; i++) {
                    cv.addChat(new Chat(this, cv, splt[i], false));
                    send("GET_CHAT_MESSAGES;" + splt[i]);
                }
            } else if (splt[1].equals("SEND_GROUPS")) {
                for (int i = 2; i < splt.length - 1; i++) {
                    cv.addChat(new Chat(this, cv, splt[i], true));
                    send("GET_GROUP_MESSAGES;" + splt[i]);
                }
            } else if (splt[1].equals("SEND_CHAT_MESSAGES")) {
                String chat = splt[2];
                Chat c = cv.getChat(chat);
                for (int i = 3; i < splt.length - 1; i++) {
                    c.addMessage(splt[i], chat, splt[++i]);
                }
            } else if (splt[1].equals("SEND_GROUP_MESSAGES")) {
                Chat c = cv.getGroup(splt[2]);
                for (int i = 3; i < splt.length - 1; i++) {
                    c.addMessage(splt[i], splt[++i], splt[++i]);
                }
            }
        } else if (splt.length == 5 && splt[0].equals("SEND_CHAT_MESSAGE")) {
            String chat = splt[2];
            Chat c = cv.getChat(chat);
            if (c == null)
                cv.addChat(c = new Chat(this, cv, chat, false));
            c.addMessage(splt[1], chat, splt[3]);
        } else if (splt.length == 6 && splt[0].equals("SEND_GROUP_MESSAGE")) {
            String group = splt[1];
            Chat c = cv.getGroup(group);
            if (c == null) cv.addChat(c = new Chat(this, cv, group, true));
            System.out.println(splt[4]);
            c.addMessage(splt[2], splt[3], splt[4]);
        } else {
            send("-;WRONG_INPUT");
        }
    }

    /**
     * MAC (Message Authentication Code) check
     *
     * @param message message
     * @param mac     mac code
     * @return <code>true</code>, if the message is correctly transmitted, otherwise <code>false</code>
     */
    private boolean macCheck(String message, String mac) {
        return generateMac(encryptionKey, message).equals(mac);
    }

    @Override
    public void send(String message) {
        if (encryptionKey == null)
            return;
        String mac = generateMac(encryptionKey, message);
        message = encryptAES(encryptionKey, message + ";" + mac);
        super.send(message);
    }

}
