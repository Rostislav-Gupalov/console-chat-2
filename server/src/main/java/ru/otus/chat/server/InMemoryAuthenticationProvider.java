package ru.otus.chat.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticatedProvider {

    private Server server;
    private UsersJdbc usersJdbc;
    private List<User> users;
    int id;

    public InMemoryAuthenticationProvider(Server server) throws SQLException {
        this.server = server;
        this.usersJdbc = new UsersJdbc();
        this.users = new ArrayList<>();
        users = usersJdbc.getAll();
        this.id = users.toArray().length + 1;
    }

    @Override
    public void initialize() {
        System.out.println("Сервис аутентификации запущен: In memory режим");
    }

    private String getUsernameByLoginAndPassword(String login, String password) {
        for (User user : usersJdbc.getAll()) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user.getUsername();
            }
        }
        return null;
    }

    private String getRoleByLoginAndPassword(String login, String password) {
        for (User user : usersJdbc.getAll()) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user.getRole();
            }
        }
        return null;
    }


    @Override
    public synchronized boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String authName = getUsernameByLoginAndPassword(login, password);
        String authRole = getRoleByLoginAndPassword(login, password);
        if (authName == null) {
            clientHandler.sendMessage("Некорректный логин/пароль");
            return false;
        }
        if (server.isUsernameBusy(authName)) {
            clientHandler.sendMessage("Учетная запись уже занята");
            return false;
        }
        clientHandler.setUsername(authName);
        clientHandler.setRole(authRole);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/authok " + authName);
        return true;
    }

    private boolean isLoginAlreadyExist(String login) {
        for (User user : usersJdbc.getAll()) {
            if (user.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUsernameAlreadyExist(String username) {
        for (User user : usersJdbc.getAll()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String username, String role) {
        if (login.trim().length() < 3 || password.trim().length() < 6
                || username.trim().length() < 2) {
            clientHandler.sendMessage("Требования логин 3+ символа, пароль 6+ символа," +
                    "имя пользователя 2+ символа не выполнены");
            return false;
        }
        if (isLoginAlreadyExist(login)) {
            clientHandler.sendMessage("Указанный логин уже занят");
            return false;
        }
        if (isUsernameAlreadyExist(username)) {
            clientHandler.sendMessage("Указанное имя пользователя уже занято");
            return false;
        }
        users.add(new User(id, login, password, username, role));
        id++;
        clientHandler.setUsername(username);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/regok " + username);

        return true;
    }
}
