package server.database;

import java.sql.*;

/**
 * the db connector for the chat application
 *
 * @author Stefan Christian Kohlmeier
 * @version 09.12.2019
 */
public class DBConnector implements AutoCloseable {

    private final String ip = "localhost";
    private final int port = 3306;
    private final String user = "root";
    private final String password = "";

    private Connection connection;

    /**
     * constructor
     */
    public DBConnector() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://" + ip + ":" + port, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        initTables();
    }

    /**
     * returns all chats of a user
     *
     * @param user user
     * @return all chats if it worked, otherwise <code>null</code>
     */
    public String[] getChats(String user) {
        String sql = "SELECT name FROM chats, users WHERE partner = users.id AND chats.user = ?";
        return getChatsGroups(user, sql);
    }

    /**
     * returns the all messages of a chat or a group
     *
     * @param user user
     * @param sql  sql statement
     * @return all messages of a chat or a group if it worked, otherwise <code>null</code>
     */
    private String[] getChatsGroups(String user, String sql) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int userId = getUserId(user);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return getStringArrayColumn(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * returns the members of a group
     *
     * @param group group
     * @return the group members if it worked, otherwise <code>null</code>
     */
    public String[] getGroupMembers(String group) {
        String sql = "SELECT users.name FROM " +
                     "group_members, users WHERE " +
                     "group_id = ? AND member_id = users.id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int groupId = getGroupId(group);
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            return getStringArrayColumn(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets the data array of a result set
     *
     * @param rs result set
     * @return data array
     * @throws SQLException
     */
    private String[][] getDataArray(ResultSet rs) throws SQLException {
        rs.last();
        String[][] arr = new String[rs.getRow()][rs.getMetaData().getColumnCount()];
        rs.first();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < rs.getMetaData().getColumnCount(); j++)
                arr[i][j] = rs.getString(j + 1);
            rs.next();
        }
        return arr;
    }

    /**
     * gets the first column of a result set
     *
     * @param rs result set
     * @return array of the first column
     * @throws SQLException
     */
    private String[] getStringArrayColumn(ResultSet rs) throws SQLException {
        rs.last();
        String[] arr = new String[rs.getRow()];
        rs.first();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rs.getString(1);
            rs.next();
        }
        return arr;
    }

    /**
     * gets the groups a user is member of
     *
     * @param user user
     * @return all groups, if it worked, otherwise <code>null</code>
     */
    public String[] getGroups(String user) {
        String sql = "SELECT groups.name FROM " +
                     "groups, group_members WHERE " +
                     "groups.id = group_id AND member_id = ? ";
        return getChatsGroups(user, sql);
    }

    /**
     * lets a user leave a group
     *
     * @param user  user
     * @param group group
     * @return <code>true</code>, if it worked, otherwise <code>false</code>
     */
    public boolean leaveGroup(String user, String group) {
        try {
            int groupId = getGroupId(group);
            int userId = getUserId(user);
            String sql = "DELETE FROM group_members WHERE group_id = ? AND member_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, groupId);
                stmt.setInt(2, userId);
                stmt.execute();
            }
            sql = "SELECT COUNT(*) FROM group_members WHERE group_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                rs.first();
                if (rs.getInt("COUNT(*)") == 0) {
                    sql = "DELETE FROM group_messages WHERE group_id = ?";
                    try (PreparedStatement stmt2 = connection.prepareStatement(sql)) {
                        stmt2.setInt(1, groupId);
                        stmt2.execute();
                    }
                    sql = "DELETE FROM groups WHERE id = ?";
                    try (PreparedStatement stmt2 = connection.prepareStatement(sql)) {
                        stmt2.setInt(1, groupId);
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * returns the SHA salt and password of an user
     *
     * @param user user
     * @return byte array of salt[0] and password[1]
     */
    public byte[][] getPassword(String user) {
        // 1: salt 2: password
        String sql = "SELECT salt, password FROM users WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            rs.first();
            return new byte[][]{
                    rs.getBytes("salt"),
                    rs.getBytes("password")
            };
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets the messages of a group
     *
     * @param group group
     * @return all group messages if it worked, otherwise <code>null</code>
     * format: [0]: timestamp, [1]: sender name, [2]: message
     */
    public String[][] getGroupMessages(String group) {
        String sql = "SELECT send_timestamp, users.name, message FROM users, group_messages WHERE " +
                     "group_id = ? AND users.id = sender ORDER BY send_timestamp";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int groupId = getGroupId(group);
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            return getDataArray(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets all messages of a chat
     *
     * @param user    user
     * @param partner partner
     * @return all messages of a group if it worked, otherwise <code>null</code>
     * format: [0]: timestamp, [1]: sender name, [2]: message
     */
    public String[][] getChatMessages(String user, String partner) {
        String sql = "SELECT send_timestamp, sender, chat_messages.message FROM " +
                     "chats, chats_chat_messages, chat_messages WHERE " +
                     "chats.id = chat AND chat_messages.id = chats_chat_messages.message AND " +
                     "user = ? AND partner = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int userId = getUserId(user);
            stmt.setInt(1, userId);
            int partnerId = getUserId(partner);
            stmt.setInt(2, partnerId);
            ResultSet rs = stmt.executeQuery();
            return getDataArray(rs);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * gets the id of a group
     *
     * @param group group
     * @return its id if the group exists, otherwise <code>0</code>
     */
    private int getGroupId(String group) {
        String sql = "SELECT id FROM groups WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, group);
            ResultSet rs = pstmt.executeQuery();
            rs.first();
            return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * lets a user join a group
     *
     * @param user  user
     * @param group group
     * @return <code>if it worked</code>, otherwise <code>false</code>
     */
    public boolean joinGroup(String user, String group) {
        String sql = "INSERT INTO group_members VALUES(?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int groupId = getGroupId(group);
            stmt.setInt(1, groupId);
            int userId = getUserId(user);
            stmt.setInt(2, userId);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * lets a user send a group message
     *
     * @param user    user
     * @param group   group
     * @param message message
     * @return if the message is sent its timestamp, otherwise <code>null</code>
     */
    public Timestamp sendGroupMessage(String user, String group, String message) {
        String sql = "INSERT INTO group_messages (group_id, sender, message) VALUES (" +
                     "(SELECT id FROM groups WHERE name = ?)," +
                     "(SELECT id FROM users WHERE name = ?)," +
                     "?)";
        try {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, group);
                stmt.setString(2, user);
                stmt.setString(3, message);
                stmt.execute();
                sql = "SELECT MAX(send_timestamp) FROM " +
                      "users, groups, group_messages WHERE " +
                      "users.id = sender AND groups.id = group_id AND users.name = ? AND groups.name = ?";
            }
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, user);
                stmt.setString(2, group);
                ResultSet rs = stmt.executeQuery();
                rs.first();
                return rs.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * lets an user create a group
     *
     * @param user  user
     * @param group group
     * @return <code>true</code>, if it worked, otherwise for example if the group already exists <code>false</code>
     */
    public boolean createGroup(String user, String group) {
        String sql = "INSERT INTO groups (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, group);
            stmt.execute();
            joinGroup(user, group);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * gets the user id of an user
     *
     * @param user user
     * @return id, otherwise, if the user does not exists <code>0</code>
     */
    private int getUserId(String user) {
        String sql = "SELECT id FROM users WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            rs.first();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * gets the chat id of a conversation
     *
     * @param user    user
     * @param partner partner
     * @return chat id, if the chat does not exist <code>0</code>
     */
    private int getChatId(int user, int partner) {
        String sql = "SELECT id FROM chats WHERE user = ? AND partner = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user);
            pstmt.setInt(2, partner);
            ResultSet rs = pstmt.executeQuery();
            rs.first();
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * lets a user leave a chat
     *
     * @param user    user
     * @param partner partner
     * @return <code>true</code>, if it worked, otherwise <code>false</code>
     */
    public boolean leaveChat(String user, String partner) {
        int userId = getUserId(user);
        int partnerId = getUserId(partner);
        int chatId = getChatId(userId, partnerId);
        String sql = "SELECT message FROM chats_chat_messages WHERE chat = ?";
        try {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                sql = "DELETE FROM chats_chat_messages WHERE chat = ?";
                try (PreparedStatement stmt2 = connection.prepareStatement(sql)) {
                    stmt2.setInt(1, chatId);
                    stmt2.execute();
                }
                if (getChatId(partnerId, userId) == 0) {
                    sql = "DELETE FROM chat_messages WHERE id = ?";
                    try (PreparedStatement stmt2 = connection.prepareStatement(sql)) {
                        while (rs.next()) {
                            stmt2.setInt(1, rs.getInt(1));
                            stmt2.execute();
                        }
                    }
                }
            }
            sql = "DELETE FROM chats WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, chatId);
                stmt.execute();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * returns the last insert id
     *
     * @return id
     */
    private int getLastInsertId() {
        try (Statement stmt = connection.createStatement()) {
            String sql = "SELECT LAST_INSERT_ID()";
            ResultSet rs = stmt.executeQuery(sql);
            rs.first();
            return rs.getInt("LAST_INSERT_ID()");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * lets a user send a chat message to another user
     *
     * @param user    user
     * @param partner the chat partner of the user
     * @param message message
     * @return if it worked the timestamp of the message, otherwise if the user is his own partner or
     * it did not worked, <code>null</code>
     */
    public Timestamp sendChatMessage(String user, String partner, String message) {
        if (!user.equals(partner)) {
            int chatId1 = 0;
            int chatId2 = 0;
            String sql = "INSERT INTO chats (user, partner) VALUES (?, ?)";
            try {
                int senderId = getUserId(user);
                int receiverId = getUserId(partner);
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, senderId);
                    stmt.setInt(2, receiverId);
                    try {
                        stmt.execute();
                        chatId1 = getLastInsertId();
                    } catch (SQLException e) {
                        // do nothing
                    }
                    stmt.setInt(1, receiverId);
                    stmt.setInt(2, senderId);
                    try {
                        stmt.execute();
                        chatId2 = getLastInsertId();
                    } catch (SQLException e) {
                        // do nothing
                    }
                }
                sql = "INSERT INTO chat_messages (sender, receiver, message) VALUES (?, ?, ?) ";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, senderId);
                    stmt.setInt(2, receiverId);
                    stmt.setString(3, message);
                    stmt.execute();
                }
                int msgId = getLastInsertId();
                sql = "SELECT id FROM chats WHERE user = ? AND partner = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    if (chatId1 == 0) {
                        pstmt.setInt(1, senderId);
                        pstmt.setInt(2, receiverId);
                        ResultSet rs = pstmt.executeQuery();
                        rs.first();
                        chatId1 = rs.getInt(1);
                    }
                    if (chatId2 == 0) {
                        pstmt.setInt(1, receiverId);
                        pstmt.setInt(2, senderId);
                        ResultSet rs = pstmt.executeQuery();
                        rs.first();
                        chatId2 = rs.getInt(1);
                    }
                }
                sql = "INSERT INTO chats_chat_messages VALUES(?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, chatId1);
                    stmt.setInt(2, msgId);
                    try {
                        stmt.execute();
                    } catch (SQLException e) {
                        // do nothing
                    }
                    ResultSet rs = stmt.executeQuery("SELECT send_timestamp FROM chat_messages WHERE id = LAST_INSERT_ID()");
                    stmt.setInt(1, chatId2);
                    try {
                        stmt.execute();
                    } catch (SQLException e) {
                        // do nothing
                    }
                    rs.first();
                    return rs.getTimestamp("send_timestamp");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * registers an user
     *
     * @param name     name
     * @param salt     salt
     * @param password password
     * @return <code>true</code>, if the user has been registered, otherwise <code>false</code>
     */
    public boolean registerUser(String name, byte[] salt, byte[] password) {
        String sql = "INSERT INTO users (name, salt, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setBytes(2, salt);
            pstmt.setBytes(3, password);
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * inits the needed sql tables, if they do not exist
     */
    private void initTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS chat_server");
            stmt.execute("USE chat_server");
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY," +
                         "name VARCHAR(255) NOT NULL UNIQUE," +
                         "salt BINARY(16) NOT NULL," +
                         "password BINARY(64) NOT NULL" +
                         ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS chats (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY," +
                         "user INT," +
                         "partner INT," +
                         "UNIQUE (user, partner)," +
                         "FOREIGN KEY (user) REFERENCES users(id)," +
                         "FOREIGN KEY(partner) REFERENCES users(id)" +
                         ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS chat_messages (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY," +
                         "sender INT," +
                         "receiver INT," +
                         "send_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                         "message TEXT," +
                         "UNIQUE (sender, receiver, send_timestamp)," +
                         "FOREIGN KEY (sender) REFERENCES users(id)," +
                         "FOREIGN KEY (receiver) REFERENCES users(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS chats_chat_messages (" +
                         "chat INT," +
                         "message INT," +
                         "PRIMARY KEY(chat, message)," +
                         "FOREIGN KEY (chat) REFERENCES chats(id)," +
                         "FOREIGN KEY(message) REFERENCES chat_messages(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS groups (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY ," +
                         "name VARCHAR(255) NOT NULL UNIQUE" +
                         ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS group_members (" +
                         "group_id INT," +
                         "member_id INT," +
                         "PRIMARY KEY (group_id, member_id)," +
                         "FOREIGN KEY (group_id) REFERENCES groups(id)," +
                         "FOREIGN KEY (member_id) REFERENCES users(id)" +
                         ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS group_messages (" +
                         "group_id INT," +
                         "sender INT," +
                         "send_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                         "message TEXT NOT NULL DEFAULT ''," +
                         "PRIMARY KEY (group_id, sender, send_timestamp)," +
                         "FOREIGN KEY (group_id) REFERENCES groups(id)," +
                         "FOREIGN KEY (sender) REFERENCES users(id)" +
                         ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
