package com.example.julio.photogo;

import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

public class User extends AppCompatActivity implements Serializable {
    private int userId;
    private String  user;
    private String Name;
    private String Lname;
    private String Email;
    private String photo;

    public User(){}
    public User(String user){
    this.user=user;
    }
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo=photo;
    }


}
