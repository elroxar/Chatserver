package server.database;

public class DBConnector {

    public DBConnector(String pIP, int pPort, String pDatabase, String pUsername, String pPassword) {
    }

    public boolean addUser(String user, String salt, String passwordHash) {
        // Hash and salt are saved in Base64
        return true;
    }

    public String[] getPassword(String user) {
        // 1st return: salt (hexadecimal number to calculate hash)
        // 2nd return: hash
        return null;
    }

    public String[] getChats(String user) {
        return null;
    }

    public String[] getGroupChats(String user) {
        return null;
    }

    public String[] getMessages(String user, int from, int to) {
        // format of one entry: <message>:<timestamp>
        // from: last message available for client, to: x messages before
        return null;
    }

    public String[] getGroupMessages(String group, int from, int to) {
        // format of one entry: <message>:<timestamp>
        return null;
    }

    public boolean addGroupMember(String user, String group, String add) {
        return true;
    }

    public String[] getGroupMembers(String group) {
        return null;
    }

    public boolean sendGroupMessage(String group, String message) {
        return true;
    }

    public boolean sendMessage(String user, String message) {
        return true;
    }

    public boolean leaveGroup(String user, String group) {
        return true;
    }

    public boolean leaveChat(String user, String chatPartner) {
        return true;
    }

    public boolean createGroup(String user, String group) {
        return true;
    }

}
