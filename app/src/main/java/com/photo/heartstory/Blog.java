package com.photo.heartstory;

public class Blog {
    private String username;
    private String name;
    private String id;
    private String profileimageview;
    private String email;
    private String namelower;

    public Blog(String username, String name, String id, String profileimageview, String email , String namelower) {
        this.username = username;
        this.name = name;
        this.id = id;
        this.profileimageview = profileimageview;
        this.email = email;
        this.namelower = namelower;
    }

    public Blog(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileimageview() {
        return profileimageview;
    }

    public void setProfileimageview(String profileimageview) {
        this.profileimageview = profileimageview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamelower() {
        return namelower;
    }

    public void setNamelower(String namelower) {
        this.namelower = namelower;
    }
}
