package com.photo.heartstory;

import de.hdodenhof.circleimageview.CircleImageView;

public class Commentitem {
    private String publisher;
    private String comment;
    private String commentid;


    public Commentitem(String publisher, String comment, String commentid) {
        this.publisher = publisher;
        this.comment = comment;
        this.commentid=commentid;
    }

    public Commentitem(){

    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }
}
