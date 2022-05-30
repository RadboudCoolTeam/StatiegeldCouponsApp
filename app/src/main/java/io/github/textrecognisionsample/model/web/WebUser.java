package io.github.textrecognisionsample.model.web;

import java.util.Arrays;

public class WebUser {

    public long id;

    public String name;

    public String email;

    public String password;

    public String passwordHash;

    public byte[] data;

    public WebUser(String email, String password, byte[] data) {
        this.id = 0;
        this.name = " ";
        this.email = email;
        this.password = password;
        this.passwordHash = "";
        if (data != null) {
            this.data = Arrays.copyOf(data, data.length);
        } else {
            this.data = null;
        }
    }

    public void updateUser(WebUser webUser) {
        this.id = webUser.id;
        this.name = webUser.name;
        this.email = webUser.email;
        this.password = webUser.password;
        if (webUser.data != null) {
            this.data = Arrays.copyOf(webUser.data, webUser.data.length);
        } else {
            this.data = null;
        };
    }

    public void reset() {
        this.id = 0;
        this.name = " ";
        this.email = "";
        this.password = "";
        this.passwordHash = "";
        this.data = null;
    }
}
