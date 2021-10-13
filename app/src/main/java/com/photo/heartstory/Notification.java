package com.photo.heartstory;

public class Notification {

    private String userid;
    private String text;
    private String postid;
    private boolean iscomment;

    public Notification(String userid, String text, String postid, boolean iscomment) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.iscomment = iscomment;
    }

    public Notification() {

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIscomment() {
        return iscomment;
    }

    public void setIscomment(boolean iscomment) {
        this.iscomment = iscomment;
    }
}
