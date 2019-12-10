package server.control;

import libary.network.server.Server;
import server.database.DBConnector;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static util.Cryptography.*;

/**
 * @author Stefan Christian Kohlmeier
 * @version 07.12.2019
 */
public class ChatServer extends Server implements AutoCloseable {

    private final static int port = 4000;

    private DBConnector db;
    private List<User> users;
    private StringBuilder sb;

    @Override
    public void close() {
        try {
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * constructor
     */
    public ChatServer() {
        super(port);
        db = new DBConnector();
        users = new ArrayList<>();
        sb = new StringBuilder();
    }

    public static void main(String[] args) {
        new ChatServer();
    }

    /**
     * gets the user
     *
     * @param ip   ip
     * @param port port
     * @return the user
     */
    private User getUser(String ip, int port) {
        for (User u : users)
            if (u.getIp().equals(ip) && u.getPort() == port)
                return u;
        return null;
    }

    /**
     * gets the user
     *
     * @param name name
     * @return the user
     */
    private User getUser(String name) {
        for (User u : users) {
            String uName = u.getName();
            if (uName.equals(name)) return u;
        }
        return null;
    }

    @Override
    public void processClosingConnection(String ip, int port) {
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            User u = it.next();
            if (u.getIp().equals(ip) && u.getPort() == port) {
                it.remove();
                return;
            }
        }
    }

    @Override
    public void processNewConnection(String ip, int port) {
        User u = new User(ip, port);
        users.add(u);
        KeyPair kp = generateKeyPair();
        u.setPrivateKey(kp.getPrivate());
        send(ip, port, "INIT_KEY;" + keyToString(kp.getPublic()));
    }

    /**
     * signs an user in
     *
     * @param user     user
     * @param username username
     * @param password password
     */
    private void signIn(User user, String username, String password) {
        byte[][] pw;
        if ((pw = db.getPassword(username)) == null) {
            byte[] salt = generateSalt();
            byte[] genSHA = getSHA(salt, password);
            if (db.registerUser(username, salt, genSHA)) {
                user.setName(username);
                send(user, "+;REGISTER");
            } else
                send(user, "-;REGISTER");
        } else {
            byte[] pwSHA = getSHA(pw[0], password);
            if (Arrays.equals(pw[1], pwSHA)) {
                user.setName(username);
                send(user, "+;SIGN_IN");
            } else
                send(user, "-;SIGN_IN");
        }
    }

    /**
     * sends a chat message
     *
     * @param user    user
     * @param partner partner
     * @param message message
     */
    private void sendChatMessage(User user, String partner, String message) {
        Timestamp ts = db.sendChatMessage(user.getName(), partner, message);
        if (ts == null)
            send(user, "-;SEND_CHAT_MESSAGE");
        else {
            User u = getUser(partner);
            if (u != null)
                send(u, "SEND_CHAT_MESSAGE;" + ts + ";" + user.getName() + ";" + message);
        }
    }

    /**
     * sends a group message
     *
     * @param user    user
     * @param group   group
     * @param message message
     */
    private void sendGroupMessage(User user, String group, String message) {
        Timestamp ts = db.sendGroupMessage(user.getName(), group, message);
        if (ts == null) {
            send(user, "-;SEND_GROUP_MESSAGE");
        } else {
            String[] arr = db.getGroupMembers(group);
            if (arr == null) return;
            for (String s : arr) {
                User u = getUser(s);
                if (u != null)
                    send(u, "SEND_GROUP_MESSAGE;" + group + ";" + ts + ";"
                            + user.getName() + ";" + message);
            }
        }
    }

    @Override
    public void processMessage(String ip, int port, String message) {
        User u = getUser(ip, port);
        if (message.indexOf("INIT_KEY;") == 0) {
            String key = message.substring(9);
            SecretKey ec = keyAgreement(u.getPrivateKey(), key);
            u.setPrivateKey(null);
            u.setEncryptionKey(ec);
            return;
        } else if (u.getEncryptionKey() == null) {
            send(ip, port, "-");
            return;
        }
        message = decryptAES(u.getEncryptionKey(), message);
        System.out.println(message);
        // matches ; not preceded and followed by ;
        String[] splt = message.split(";");
        message = message.substring(0, message.length() - splt[splt.length - 1].length() - 1);
        if (!macCheck(u, message, splt[splt.length - 1])) {
            System.err.println("Insecure connection!");
            return;
        }
        if (u.isSignedIn()) {
            if (splt.length == 2) {
                if (splt[0].equals("GET_CHATS"))
                    getChats(u);
                else if (splt[0].equals("GET_GROUPS"))
                    getGroups(u);
                else if (splt[0].equals("SIGN_OUT"))
                    signOut(u);
            } else if (splt.length == 3) {
                if (splt[0].equals("CREATE_GROUP"))
                    createGroup(u, splt[1]);
                else if (splt[0].equals("GET_CHAT_MESSAGES"))
                    getChatMessages(u, splt[1]);
                else if (splt[0].equals("GET_GROUP_MESSAGES"))
                    getGroupMessages(u, splt[1]);
                else if (splt[0].equals("LEAVE_CHAT"))
                    leaveChat(u, splt[1]);
                else if (splt[0].equals("JOIN_GROUP"))
                    joinGroup(u, splt[1]);
                else if (splt[0].equals("LEAVE_GROUP"))
                    leaveGroup(u, splt[1]);
            } else if (splt.length == 4) {
                if (splt[0].equals("SEND_CHAT_MESSAGE"))
                    sendChatMessage(u, splt[1], splt[2]);
                else if (splt[0].equals("SEND_GROUP_MESSAGE"))
                    sendGroupMessage(u, splt[1], splt[2]);
            }
        } else if (splt.length == 4 && splt[0].equals("SIGN_IN"))
            signIn(u, splt[1], splt[2]);
        else
            send(u, "-;WRONG_INPUT");
    }

    /**
     * joins a group
     *
     * @param user  user
     * @param group group
     */
    private void joinGroup(User user, String group) {
        if (db.joinGroup(user.getName(), group))
            send(user, "+;JOIN_GROUP;" + group);
        else
            send(user, "-;JOIN_GROUP;" + group);
    }

    /**
     * signs out
     *
     * @param user user
     */
    private void signOut(User user) {
        if (user.isSignedIn()) {
            send(user, "+;SIGN_OUT");
            user.setName(null);
        } else
            send(user, "-;SIGN_OUT");
    }

    /**
     * leaves a group
     *
     * @param user  user
     * @param group group
     */
    private void leaveGroup(User user, String group) {
        if (db.leaveGroup(user.getName(), group))
            send(user, "+;LEAVE_GROUP;" + group);
        else
            send(user, "-;LEAVE_GROUP;" + group);
    }

    /**
     * leaves a chat
     *
     * @param user    user
     * @param partner partner
     */
    private void leaveChat(User user, String partner) {
        if (db.leaveChat(user.getName(), partner))
            send(user, "+;LEAVE_CHAT");
        else
            send(user, "-;LEAVE_CHAT");
    }

    private void createGroup(User user, String group) {
        if (db.createGroup(user.getName(), group))
            send(user, "+;CREATE_GROUP;" + group);
        else
            send(user, "-;CREATE_GROUP;" + group);
    }

    private void getGroupMessages(User user, String group) {
        String[][] arr = db.getGroupMessages(group);
        if (arr == null) {
            send(user, "-;SEND_GROUP_MESSAGES;" + group);
        } else {
            sb.setLength(0);
            sb.append("+;SEND_GROUP_MESSAGES;" + group);
            sendChatGroupMessages(user, arr);
        }
    }

    private void getChatMessages(User user, String chat) {
        String[][] arr = db.getChatMessages(user.getName(), chat);
        if (arr == null) {
            sb.setLength(0);
            sb.append("+;SEND_CHAT_MESSAGES;" + chat);
            sendChatGroupMessages(user, arr);
        } else {
            send(user, "-;SEND_GROUP_MESSAGES;" + chat);
        }
    }

    private void sendChatGroupMessages(User user, String[][] arr) {
        for (String[] s : arr) {
            sb.append(";" + s[0]);
            sb.append(";" + s[1]);
            sb.append(";" + s[2]);
        }
        send(user, sb.toString());
    }

    private boolean macCheck(User user, String message, String mac) {
        return generateMac(user.getEncryptionKey(), message).equals(mac);
    }

    private String getChatsGroups(String[] arr) {
        if (arr != null) {
            for (String s : arr) {
                sb.append(";" + s);
            }
        }
        return sb.toString();
    }

    private void getGroups(User user) {
        sb.setLength(0);
        sb.append("+;SEND_GROUPS");
        String[] arr = db.getGroups(user.getName());
        String msg = getChatsGroups(arr);
        send(user, msg);
    }

    private void getChats(User user) {
        sb.setLength(0);
        sb.append("+;SEND_CHATS");
        String[] arr = db.getChats(user.getName());
        String msg = getChatsGroups(arr);
        send(user, msg);
    }

    public void send(User user, String message) {
        SecretKey k = user.getEncryptionKey();
        String mac = generateMac(k, message);
        message = encryptAES(k, message + ";" + mac);
        send(user.getIp(), user.getPort(), message);
    }
}
