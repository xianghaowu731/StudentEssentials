package com.app.studentessentials.Models;

public class UserModel {
    public String userkey;
    public String email;
    public String userName;
    public String password;
    public String remember;

    public UserModel(String key, String mail, String name, String pass, String rem){
        this.userkey = key;
        this.email = mail;
        this.userName = name;
        this.password = pass;
        this.remember = rem;
    }
}
