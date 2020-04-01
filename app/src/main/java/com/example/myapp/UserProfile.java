package com.example.myapp;

public class UserProfile {

    public String userAge;
    public String userName;
    public String userEmail;

    public UserProfile()
    {

    }

    public UserProfile(String userAge, String userName, String userEmail) {
        this.userAge = userAge;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
