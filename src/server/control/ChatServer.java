package server.control;

import libary.network.server.Server;
import server.database.DBConnector;

import javax.crypto.SecretKey;
import java.security.KeyPair;

import static util.Cryptography.*;

/**
 * a chat server
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class ChatServer extends Server {

    private Handler handler;
    private DBConnector dbConnector;
    private StringBuilder stringBuilder;


    /**
     * constructor
     *
     * @param port       port
     * @param dbUsername db username
     * @param dbPassword db password
     */
    public ChatServer(int port, String dbUsername, String dbPassword) {
        super(port);
        handler = new Handler();
        dbConnector = new DBConnector(dbUsername, dbPassword);
        stringBuilder = new StringBuilder();
    }

    public static void main(String[] args) {
        //ChatServer cS = new ChatServer();
        ChatServer cS = null;

    }

    @Override
    public void processNewConnection(String ip, int port) {
        User user = handler.addUser(ip, port);
        KeyPair kP = generateKeyPair();
        user.setPrivateKey(kP.getPrivate());
        send(ip, port, "+INIT_KEY;" + keyToString(kP.getPublic()));
    }

    /**
     * signs in an user
     *
     * @param inst instructions from client <code>["SIGN_IN", username, password, MAC]</code>
     * @param user user
     * @param ip   ip
     * @param port port
     */
    private void signIn(String[] inst, User user, String ip, int port) {
        if (inst.length == 4) {
            if (handler.getUser(ip, port) != null) {
                send(user, "-;already signed in");
                return;
            }
            String[] pw = dbConnector.getPassword(inst[1]);
            if (pw == null) {
                String salt = generateSalt();
                if (dbConnector.addUser(inst[1], salt, getSHA(salt, inst[2])) == -1)
                    user.setName(inst[1]);
                send(user, "+;created new user");
            } else if (getSHA(pw[1], inst[2]).equals(pw[2])) {
                user.setName(inst[1]);
                // DBCOnnector sign in
                send(user, "+;signed in");
            } else
                send(user, "-;wrong username or password");
        } else
            send(user, "-");
    }

    /**
     * signs out an user
     *
     * @param inst instructions from client <code>["SIGN_OUT", MAC]</code>
     * @param user user
     * @param ip   ip
     * @param port port
     */
    private void signOut(String[] inst, User user, String ip, int port) {
        if (inst.length == 2)
            if (handler.removeUser(ip, port) == null)
                send(user, "-;not signed out");
            else
                send(user, "+");

        else
            send(user, "-");
    }

    /**
     * gets chats of an user
     *
     * @param inst instructions from client <code>["GET_CHATS"]</code>
     * @param user user
     */
    private void getChats(String[] inst, User user) {
        if (inst.length == 2 && user.isSignedIn())
            send(user, "+;" + arrToStr(dbConnector.getChats(user.getName())));
        else
            send(user, "-");
    }

    /**
     * gets the groups of an user
     *
     * @param inst instructions from client <code>["GET_GROUPS"]</code>
     * @param user user
     */
    private void getGroups(String[] inst, User user) {
        if (inst.length == 2 && user.isSignedIn()) {
            send(user, "+;" + arrToStr(dbConnector.getGroupChats(user.getName())));
        } else
            send(user, "-");
    }

    /**
     * gets the messages of an user
     *
     * @param inst instructions from client <code>["GET_CHAT_MESSAGES", name]</code>
     * @param user user
     */
    private void getChatMessages(String[] inst, User user) {
        if (inst.length == 3 && user.isSignedIn()) {
            String[] messages = dbConnector.getMessages(user.getName(), inst[1]);
            if (messages == null)
                send(user, "-;chat not available");
            else
                send(user, "SEND_MESSAGES;" + arrToStr(messages));
        } else
            send(user, "-");
    }

    /**
     * gets the group messages of a users group
     *
     * @param inst instructions from client <code>["GET_GROUP_MESSAGES", group]</code>
     * @param user user
     */
    private void getGroupMessages(String[] inst, User user) {
        if (inst.length == 3 && user.isSignedIn()) {
            String[] messages = dbConnector.getGroupMessages(user.getName(), inst[1]);
            if (messages == null)
                send(user, "-;chat not available");
            else
                send(user, "+;SEND_GROUP_MESSAGES;" + arrToStr(messages));
        } else
            send(user, "-");
    }

    /**
     * creates a group for an user
     *
     * @param inst instructions from client <code>["CREATE_GROUP", group]</code>
     * @param user user
     */
    private void createGroup(String[] inst, User user) {
        if (inst.length == 3 && user.isSignedIn())
            if (dbConnector.createGroup(user.getName(), inst[1]) == -1)
                send(user, "-;group already exists");
            else
                send(user, "+");
        else
            send(user, "-");
    }

    /**
     * lets a user join a group
     *
     * @param inst instructions from client <code>["JOIN_GROUP", group]</code>
     * @param user user
     */
    private void joinGroup(String[] inst, User user) {
        if (inst.length == 4 && user.isSignedIn())
            if (dbConnector.joinGroup(user.getName(), inst[1]))
                send(user, "+");
            else
                send(user, "-;failure");
        else
            send(user, "-");
    }

    /**
     * lets a user leave a chat
     *
     * @param inst instructions from client <code>["LEAVE_CHAT", name]</code>
     * @param user user
     */
    private void leaveChat(String[] inst, User user) {
        if (inst.length == 3 && user.isSignedIn())
            if (dbConnector.leaveChat(user.getName(), inst[1]))
                send(user, "+");
            else
                send(user, "-;failure");
        else
            send(user, "-");
    }

    /**
     * lets an user leave a group
     *
     * @param inst instructions from client <code>["LEAVE_GROUP", group</code>
     * @param user user
     */
    private void leaveGroup(String[] inst, User user) {
        if (inst.length == 3 && user.isSignedIn())
            if (dbConnector.leaveGroup(user.getName(), inst[1]))
                send(user, "+");
            else
                send(user, "-;failure");
        else
            send(user, "-");
    }

    /**
     * lets an user send a message to a chat partner
     *
     * @param inst instructions from client <code>["SEND_MESSAGE", name, message]</code>
     * @param user user
     */
    private void sendChatMessage(String[] inst, User user) {
        if (inst.length == 4 && user.isSignedIn())
            if (dbConnector.sendChatMessage(inst[1], inst[2])) {
                send(user, "+");
                User receiver = handler.getUser(inst[1]);
                if (receiver != null)
                    send(receiver, "SEND_MESSAGE;" + dbConnector.getMessages(user.getName(), inst[1])[0]);
            } else
                send(user, "-");
    }

    /**
     * lets an user send a message to a group
     *
     * @param inst instructions from client <code>["SEND_GROUP_MESSAGE", group, message]</code>
     * @param user user
     */
    private void sendGroupMessage(String[] inst, User user) {
        if (inst.length == 4 && user.isSignedIn())
            if (dbConnector.sendGroupMessage(inst[1], inst[2])) {
                String[] members = dbConnector.getGroupMembers(inst[1]);
                for (String str : members) {
                    User receiver = handler.getUser(str);
                    if (receiver != null)
                        send(receiver, "SEND_GROUP_MESSAGES;" + dbConnector.getGroupMessages(user.getName(), inst[1])[0]);
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
                SecretKey finalKey = keyAgreement(user.getPrivateKey(), inst[1]);
                user.setPrivateKey(null);
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
            else if (inst[0].equals("GET_CHAT_MESSAGES"))
                getChatMessages(inst, user);
            else if (inst[0].equals("GET_GROUP_MESSAGES"))
                getGroupMessages(inst, user);
            else if (inst[0].equals("CREATE_GROUP"))
                createGroup(inst, user);
            else if (inst[0].equals("JOIN_GROUP"))
                joinGroup(inst, user);
            else if (inst[0].equals("LEAVE_CHAT"))
                leaveChat(inst, user);
            else if (inst[0].equals("LEAVE_GROUP"))
                leaveGroup(inst, user);
            else if (inst[0].equals("SEND_CHAT_MESSAGE"))
                sendChatMessage(inst, user);
            else if (inst[0].equals("SEND_GROUP_MESSAGE"))
                sendGroupMessage(inst, user);
            else
                send(user, "-");
        } else {
            System.err.println("connection to " + ip + ":" + port + " insecure");
            closeConnection(ip, port);
        }
    }

    /**
     * converts a string array to a string
     *
     * @param strArr string array
     * @return string with the following pattern: <code>strArr[0]+","+strArr[1]+","+...</code>
     */
    private String arrToStr(String[] strArr) {
        stringBuilder.setLength(0);
        for (String str : strArr) {
            stringBuilder.append(str + ",");
        }
        return stringBuilder.toString();
    }

    /**
     * sends a message to a user using the aes encryption
     *
     * @param user    user
     * @param message message
     * @see #send(String, int, String)
     */
    private void send(User user, String message) {
        send(user.getIp(), user.getPort(), encryptAES(user.getEncryptionKey(),
                message + ";" + generateMac(user.getEncryptionKey(), message)));
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        handler.removeUser(pClientIP, pClientPort);
    }
}
