package com.application;

public class User {
    int id;
    String username;
    String password;
    int age = -1;

    public User(int id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password, int age){
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
    }
    public boolean isUserNull(){
        if (this.username.equals("") && this.password.equals("")){
            return true;
        }else{
            return false;
        }
    }
}
