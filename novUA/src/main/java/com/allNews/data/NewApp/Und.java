package com.allNews.data.NewApp;

import android.content.Context;

import com.allNews.application.App;
import com.allNews.db.DBOpenerHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gregory.network.rss.R;


public class Und {

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_CONTENT_NEW_APP)
    private  String content;

    @Expose(serialize = true)
    private String summary;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_IMG_NEW_APP)
    private String imgURL;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_URL_REF_LINK)
    private String refUrl;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_TAXONOMY_TAG_ID)
    private String  tagID;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }

    public String getImgURL(Context context) {
        try {
            String decodeUrl = URLEncoder.encode(imgURL.substring(21), "utf-8");
            return context.getResources().getString(R.string.url_img_new_app) + decodeUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }
}
