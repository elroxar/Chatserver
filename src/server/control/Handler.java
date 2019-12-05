package server.control;

import libary.datastructure.linear.list.List;

/**
 * handles the connected clients
 *
 * @author Stefan Christian Kohlmeier
 * @version 05.12.2019
 */
public class Handler {

    private List<User> users;

    /**
     * constructor
     */
    public Handler() {
        users = new List<>();
    }

    /**
     * adds an user to the connected users
     *
     * @param ip   ip
     * @param port port
     * @return an user object
     */
    public User addUser(String ip, int port) {
        User user = new User(ip, port);
        users.append(user);
        return user;
    }

    /**
     * gets an user from the connected users
     *
     * @param name name
     * @return the user object
     */
    public User getUser(String name) {
        users.toFirst();
        while (users.hasAccess()) {
            User user = users.getContent();
            if (user.getName().equals(name))
                return user;
        }
        return null;
    }

    /**
     * gets an user from the connected users
     *
     * @param ip   ip
     * @param port port
     * @return an user object
     */
    public User getUser(String ip, int port) {
        users.toFirst();
        while (users.hasAccess()) {
            User user = users.getContent();
            if (user.getIp().equals(ip) && user.getPort() == port)
                return user;
            users.next();
        }
        return null;
    }

    /**
     * removes an user from the connected users
     *
     * @param ip   ip
     * @param port port
     * @return an object of the removed user
     */
    public User removeUser(String ip, int port) {
        users.toFirst();
        while (users.hasAccess()) {
            User user = users.getContent();
            if (user.getIp().equals(ip) && user.getPort() == port) {
                users.remove();
                return user;
            }
            users.next();
        }
        return null;
    }
}
