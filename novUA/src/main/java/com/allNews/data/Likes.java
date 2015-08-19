package com.allNews.data;


import com.google.gson.annotations.Expose;

public class Likes {

    @Expose(serialize = true)
    private String newsID;
    @Expose(serialize = true)
    private String dislike;
    @Expose(serialize = true)
    private String like;

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getDislike() {
        return dislike;
    }

    public void setDislike(String dislike) {
        this.dislike = dislike;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return "Like [newsID = " + newsID + ", dislike = " + dislike + ", like = " + like + "]";
    }
}

