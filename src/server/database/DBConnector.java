package server.database;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("SqlResolve")
public class DBConnector {

    private final static String database = "localhost";
    private final static int port = 3306;
    private MariaDbDataSource db;
    private Connection conn;
    private java.util.Date time = new java.util.Date();
    private Logger logger;

    public DBConnector(String pUser, String pPassword) {
        db = new MariaDbDataSource();
        logger = Logger.getLogger("databse");
        try {
            db.setServerName(database);
            db.setUser(pUser);
            db.setPort(port);
            db.setPassword(pPassword);
            logger.log(Level.INFO, String.format("Connecting to resource jdbc://%s:%s@%s:%s", database, port, pUser, pPassword));
            conn = db.getConnection();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            logger.log(Level.INFO, "Connected to resource.");
        }
    }

    private Date getTimestamp() {
        Date timestamp = new Date(time.getTime());
        return timestamp;
    }

    private int getUserID(String username) {
        int ID = -1;
        try {
            PreparedStatement getUserID = conn.prepareStatement("SELECT ID FROM Chatserver.users WHERE Chatserver.users.usersname = ?");
            getUserID.setString(1, username);
            ResultSet rs = getUserID.executeQuery();
            rs.next();
            ID = rs.getInt("ID");
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return ID;
    }

    private int getGroupID(String groupname) {
        int ID = -1;
        try {
            PreparedStatement getUserID = conn.prepareStatement("SELECT ID FROM Chatserver.groups WHERE Chatserver.groups.groupname = ?");
            getUserID.setString(1, groupname);
            ResultSet rs = getUserID.executeQuery();
            rs.next();
            ID = rs.getInt("ID");
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return ID;
    }

    public int addUser(String name, String password, String salt) {
        int id = -1;
        try {
            PreparedStatement newUser = conn.prepareStatement("INSERT INTO Chatserver.users (username, joined, password, salt) VALUES (?,?,?,?)");
            newUser.setString(1, name);
            newUser.setDate(2, this.getTimestamp());
            newUser.setString(3, password);
            newUser.setString(4, salt);
            newUser.executeQuery();
            id = this.getUserID(name);
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return id;
    }

    public String[] getPassword(String name) {
        String[] ret = new String[2];
        try {
            PreparedStatement getHash = conn.prepareStatement("SELECT users.password, users.salt FROM Chatserver.users WHERE username = ?");
            getHash.setString(1, name);
            ResultSet rs = getHash.executeQuery();
            rs.next();
            ret[0] = rs.getString("password");
            ret[1] = rs.getString("salt");
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return ret;
    }

    public int createGroup(String username, String groupname) {
        int id = -1;
        try {
            PreparedStatement createGroup = conn.prepareStatement("INSERT INTO Chatserver.groups (groupname, timestamp, createdBy) VALUES (?,?,?)");
            createGroup.setString(1, groupname);
            createGroup.setDate(2, this.getTimestamp());
            createGroup.setInt(3, this.getUserID(username));
            createGroup.executeQuery();
            id = this.getGroupID(groupname);
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return id;
    }

    public String[] getChats(String user) {
        int length;
        String[] ret = new String[0];
        try {
            PreparedStatement getChats = conn.prepareStatement("SELECT DISTINCT username FROM Chatserver.sentToUser, Chatserver.users WHERE senderID = (SELECT id FROM users WHERE username = ?) AND recieverID = users.id");
            getChats.setString(1, user);
            ResultSet rs = getChats.executeQuery();
            rs.last();
            length = rs.getRow();
            ret = new String[length];
            rs.first();
            int i = 0;
            while (rs.next()) {
                ret[i] = rs.getString("username");
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return ret;
    }

    public String[] getGroupChats(String user) {
        int length;
        String[] ret = new String[0];
        try {
            PreparedStatement getGroupChats = conn.prepareStatement("SELECT DISTINCT groupname FROM Chatserver.sentToGroup, Chatserver.groups, Chatserver.users WHERE senderID = (SELECT id FROM users WHERE username = ?) AND groupID = `groups`.ID");
            getGroupChats.setString(1, user);
            ResultSet rs = getGroupChats.executeQuery();
            rs.last();
            length = rs.getRow();
            ret = new String[length];
            rs.first();
            int i = 0;
            while (rs.next()) {
                ret[i] = rs.getString("groupname");
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return ret;
    }

    public String[] getMessages(String user, String chat) {
        // format of one entry: <timestamp>:<message>
        int length;
        String[] ret = new String[0];
        try {
            PreparedStatement getMessages = conn.prepareStatement("SELECT message, stamp FROM Chatserver.messages, Chatserver.sentToUser, Chatserver.users WHERE senderID = (SELECT id FROM users WHERE username = ?) AND recieverID = (SELECT id FROM users WHERE username = ?) AND messageID = messages.id");
            getMessages.setString(1, user);
            getMessages.setString(2, chat);
            ResultSet rs = getMessages.executeQuery();
            rs.last();
            length = rs.getRow();
            ret = new String[length];
            rs.first();
            int i = 0;
            while (rs.next()) {
                ret[i] = rs.getDate("stamp").toString() + ":" + rs.getString("message");
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return ret;
    }

    public String[] getGroupMessages(String user, String group) {
        // format of one entry <timestamp>:<message>
        int length;
        String[] ret = new String[0];
        try {
            PreparedStatement getGroupMessages = conn.prepareStatement("SELECT message, stamp FROM Chatserver.messages, Chatserver.sentToGroup, Chatserver.users, Chatserver.groups WHERE senderID = (SELECT id FROM users WHERE username = ?) AND groupID = (SELECT id FROM `groups` WHERE groupname = ?) AND messageID = messages.id");
            getGroupMessages.setString(1, user);
            getGroupMessages.setString(2, group);
            ResultSet rs = getGroupMessages.executeQuery();
            rs.last();
            length = rs.getRow();
            ret = new String[length];
            rs.first();
            int i = 0;
            while (rs.next()) {
                ret[i] = rs.getDate("stamp").toString() + ":" + rs.getString("message");
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return ret;
    }

    public boolean joinGroup(String username, String groupname) {
        try {
            PreparedStatement joinGroup = conn.prepareStatement("INSERT INTO Chatserver.isMemberOf (userID, groupID, timestamp) VALUES (?,?,?)");
            joinGroup.setInt(1, this.getUserID(username));
            joinGroup.setInt(2, this.getGroupID(groupname));
            joinGroup.setDate(3, this.getTimestamp());
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public void inviteToGroup(String sendername, String recievername, String groupname) {
        try {
            PreparedStatement inviteToGroup = conn.prepareStatement("INSERT INTO Chatserver.sentInviteTo (senderID, recieverID, groupID, timestamp) VALUES (?,?,?,?)");
            inviteToGroup.setInt(1, this.getUserID(sendername));
            inviteToGroup.setInt(2, this.getUserID(recievername));
            inviteToGroup.setInt(3, this.getGroupID(groupname));
            inviteToGroup.setDate(4, this.getTimestamp());
            inviteToGroup.executeQuery();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void login(String username, String ip) {
        try {
            PreparedStatement login = conn.prepareStatement("INSERT INTO Chatserver.logins (stamp, userID, userIP) VALUES (?,?,?)");
            login.setDate(1, this.getTimestamp());
            login.setString(2, username);
            login.setString(3, ip);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
