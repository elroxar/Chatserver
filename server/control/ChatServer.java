package server.control;

import libary.network.server.Server;
import server.database.DBConnector;

import java.security.Key;
import java.security.KeyPair;

import static util.Cryptography.*;

public class ChatServer extends Server {

    private Handler handler;
    private DBConnector dbConnector;
    private StringBuilder stringBuilder;

    public ChatServer(int port, String dbIp, int dbPort, String dbName, String dbUsername, String dbPassword) {
        super(port);
        dbConnector = new DBConnector(dbIp, dbPort, dbName, dbUsername, dbPassword);
        stringBuilder = new StringBuilder();
    }

    @Override
    public void processNewConnection(String ip, int port) {
        User user = handler.addUser(ip, port);
        KeyPair kP = generateKeyPair();
        user.setEncryptionKey(kP.getPrivate());
        send(ip, port, "+INIT_KEY;" + keyToString(kP.getPublic()));
    }

    private void signIn(String[] inst, User user, String ip, int port) {
        if (inst.length == 4) {
            if (handler.getUser(ip, port) != null) {
                send(user, "-;already signed in");
                return;
            }
            String[] pw = dbConnector.getPassword(inst[1]);
            if (pw == null) {
                String salt = generateSalt();
                if (dbConnector.addUser(inst[1], salt, getSHA(salt, inst[2])))
                user.setName(inst[1]);
                send(user, "+;created new user");
            } else if (getSHA(pw[1], inst[2]).equals(pw[2])) {
                user.setName(inst[1]);
                send(user, "+;signed in");
            } else
                send(user, "-;wrong username or password");
        } else
            send(user, "-");
    }

    private void signOut(String[] inst, User user, String ip, int port) {
        if (inst.length == 2)
            if (handler.removeUser(ip, port) == null)
                send(user, "-;not signed out");
            else
                send(user, "+");

        else
            send(user, "-");
    }

    private void getChats(String[] inst, User user) {
        if (inst.length == 2 && user.isSignedIn()) {
            send(user, "+;" + arrToStr(dbConnector.getChats(user.getName())));
        } else
            send(user, "-");
    }

    private void getGroups(String[] inst, User user) {
        if (inst.length == 2 && user.isSignedIn()) {
            send(user, "+;" + arrToStr(dbConnector.getGroupChats(user.getName())));
        } else
            send(user, "-");
    }

    private void getMessages(String[] inst, User user) {
        if (inst.length == 4 && user.isSignedIn()) {
            String[] messages = dbConnector.getMessages(user.getName(), Integer.parseInt(inst[1]), Integer.parseInt(inst[2]));
            if (messages == null)
                send(user, "-;chat not available");
            else
                send(user, "SEND_MESSAGES;" + arrToStr(messages));
        } else
            send(user, "-");
    }

    private void getGroupMessages(String[] inst, User user) {
        if (inst.length == 4 && user.isSignedIn()) {
            String[] messages = dbConnector.getGroupMessages(inst[1], Integer.parseInt(inst[2]), Integer.parseInt(inst[3]));
            if (messages == null)
                send(user, "-;chat not available");
            else
                send(user, "+;SEND_GROUP_MESSAGES;" + arrToStr(messages));
        } else
            send(user, "-");
    }

    private void createGroup(String[] inst, User user) {
        if (inst.length == 3 && user.isSignedIn())
            if (dbConnector.createGroup(user.getName(), inst[1]))
                send(user, "+");
            else
                send(user, "-;group already exists");
        else
            send(user, "-");
    }

    private void addToGroup(String[] inst, User user) {
        if (inst.length == 4 && user.isSignedIn())
            if (dbConnector.addGroupMember(user.getName(), inst[1], inst[2]))
                send(user, "+");
            else
                send(user, "-;failure");
        else
            send(user, "-");
    }

    private void leaveChat(String[] inst, User user) {
        if (inst.length == 3 && user.isSignedIn())
            if (dbConnector.leaveChat(user.getName(), inst[1]))
                send(user, "+");
            else
                send(user, "-;failure");
        else
            send(user, "-");
    }

    private void leaveGroup(String[] inst, User user) {
        if (inst.length == 3 && user.isSignedIn())
            if (dbConnector.leaveGroup(user.getName(), inst[1]))
                send(user, "+");
            else
                send(user, "-;failure");
        else
            send(user, "-");
    }

    private void sendMessage(String[] inst, User user) {
        if (inst.length == 4 && user.isSignedIn())
            if (dbConnector.sendMessage(inst[1], inst[2])) {
                send(user, "+");
                User receiver = handler.getUser(inst[1]);
                if (receiver != null)
                    send(receiver, "SEND_MESSAGE;" + dbConnector.getMessages(inst[1], 0, 0)[0]);
            } else
                send(user, "-");
    }

    private void sendGroupMessage(String[] inst, User user) {
        if (inst.length == 4 && user.isSignedIn())
            if (dbConnector.sendGroupMessage(inst[1], inst[2])) {
                String[] members = dbConnector.getGroupMembers(inst[1]);
                for (String str : members) {
                    User receiver = handler.getUser(str);
                    if (receiver != null)
                        send(receiver, "SEND_GROUP_MESSAGES;" + dbConnector.getGroupMessages(inst[1], 0, 0)[0]);
                }
                send(user, "+");
            } else
                send(user, "-;failure");
        else
            send(user, "-");
    }

    @Override
    public void processMessage(String ip, int port, String message) {
        User user = handler.getUser(ip, port);
        // user is always registered in handler
        if (!user.isSignedIn()) {
            String[] inst = message.split(";");
            if (inst[0].equals(("INIT_KEY"))) {
                Key finalKey = keyAgreement(user.getEncryptionKey(), inst[1]);
                user.setEncryptionKey(finalKey);
            } else {
                send(ip, port, "-");
            }
            return;
        }
        String[] inst = decryptAES(user.getEncryptionKey(), message).split(";");
        if (generateMac(user.getEncryptionKey(), message.substring(0, message.lastIndexOf(";"))).equals(inst[inst.length - 1])) {
            if (inst[0].equals("SIGN_IN"))
                signIn(inst, user, ip, port);
            else if (inst[0].equals("SIGN_OUT"))
                signOut(inst, user, ip, port);
            else if (inst[0].equals("GET_CHATS"))
                getChats(inst, user);
            else if (inst[0].equals("GET_GROUPS"))
                getGroups(inst, user);
            else if (inst[0].equals("GET_MESSAGES"))
                getMessages(inst, user);
            else if (inst[0].equals("GET_GROUP_MESSAGES"))
                getGroupMessages(inst, user);
            else if (inst[0].equals("CREATE_GROUP"))
                createGroup(inst, user);
            else if (inst[0].equals("ADD_TO_GROUP"))
                addToGroup(inst, user);
            else if (inst[0].equals("LEAVE_CHAT"))
                leaveChat(inst, user);
            else if (inst[0].equals("LEAVE_GROUP"))
                leaveGroup(inst, user);
            else if (inst[0].equals("SEND_MESSAGE"))
                sendMessage(inst, user);
            else if (inst[0].equals("SEND_GROUP_MESSAGE"))
                sendGroupMessage(inst, user);
            else
                send(user, "-");
        } else {
            System.err.println("connection to " + ip + ":" + port + " insecure");
            closeConnection(ip, port);
        }
    }

    private String arrToStr(String[] strArr) {
        stringBuilder.setLength(0);
        for (String str : strArr) {
            stringBuilder.append(str + ",");
        }
        return stringBuilder.toString();
    }

    private void send(User user, String message) {
        send(user.getIp(), user.getPort(), encryptAES(user.getEncryptionKey(),
                message + ";" + generateMac(user.getEncryptionKey(), message)));
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        handler.removeUser(pClientIP, pClientPort);
    }
}
