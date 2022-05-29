package io.github.textrecognisionsample.model.web;

public class WebUser {

    public long id;

    public String name;

    public String email;

    public String password;

    public String passwordHash;

    public WebUser(String email, String password) {
        this.id = 0;
        this.name = " ";
        this.email = email;
        this.password = password;
        this.passwordHash = "";
    }

    public void updateUser(WebUser webUser) {
        this.id = webUser.id;
        this.name = webUser.name;
        this.email = webUser.email;
        this.password = webUser.password;
    }

    public void reset() {
        this.id = 0;
        this.name = " ";
        this.email = "";
        this.password = "";
        this.passwordHash = "";
    }
}
