package server;

import libary.datastructure.linear.list.List;

public class Handler {

    private List<User> users;

    public Handler() {
        users = new List<>();
    }

    public User addUser(String ip, int port) {
        User user = new User(ip, port);
        users.append(user);
        return user;
    }

    public User getUser(String ip, int port) {
        users.toFirst();
        while(users.hasAccess()) {
            User user = users.getContent();
            if(user.getIp().equals(ip) && user.getPort() == port)
                return user;
            users.next();
        }
        return null;
    }

    public User removeUser(String ip, int port) {
        users.toFirst();
        while(users.hasAccess()) {
            User user = users.getContent();
            if(user.getIp().equals(ip) && user.getPort() == port) {
                users.remove();
                return user;
            }
            users.next();
        }
        return null;
    }
}
