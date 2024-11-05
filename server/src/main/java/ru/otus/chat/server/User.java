package ru.otus.chat.server;


public class User {
    private int id;
    private String login;
    private String password;
    private String username;
    private String role;

    public User(int id, String login, String password, String username, String role) {
        this.id = id;
        this.password = password;
        this.login = login;
        this.username = username;
        this.role = role;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}