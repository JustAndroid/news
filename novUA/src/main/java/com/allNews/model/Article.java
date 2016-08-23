package com.allNews.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vladimir on 11.11.2015.
 */
public class Article {

//    "id": 921,
//    "feed": 4,
//    "title": "Савченко будет на свободе в новом году - адвокат",
//    "description": "<img align=\"left\" hspace=\"10\" src=\"http://kor.ill.in.ua/m/190x120/1704076.jpg\" vspace=\"5\" />Почему это должно произойти Марк Фейгин не рассказал.",
//    "content": "<div id=\"insertNewsBlock\">\r\n\tНадежда Савченко будет на свободе в 2016 году, таков смысл сообщения в Twitter ее адвоката Марка Фейгина.</div>\r\n<p>\r\n\t\"Вышел из тюрьмы в Новочеркасске. Всем привет от Надежды. Свобода ближе, чем кажется. В новом году Савченко уедет домой\", – написал Фейгин.</p>\r\n<p>\r\n\tВ чем причина такой уверенности, Фейгин не уточнил. На просьбу подписчиков меньше говорить, чтобы не спугнуть удачу, адвокат ответил: \"Мы не суеверны\".</p>\r\n<blockquote class=\"twitter-tweet\" lang=\"ru\">\r\n\t<p dir=\"ltr\" lang=\"ru\">\r\n\t\tВышел из тюрьмы в Новочеркасске. Всем привет от Надежды. Свобода ближе, чем кажется. В новом году Савченко уедет домой <a href=\"https://twitter.com/hashtag/FreeSavchenko?src=hash\">#FreeSavchenko</a></p>\r\n\t— Mark Feygin (@mark_feygin) <a href=\"https://twitter.com/mark_feygin/status/659001911178301440\">27 октября 2015</a></blockquote>\r\n<p>\r\n\t<strong><a href=\"http://korrespondent.net/ukraine/3579491-sud-po-delu-savchenko-otkazalsia-povtorno-doprashyvat-luhanskykh-vrachei\">Суд по делу Савченко отказался повторно допрашивать луганских врачей</a></strong></p>\r\n<p>\r\n\tНапомним, Украинская летчица и народный депутат <a href=\"http://korrespondent.net/ukraine/politics/3579300-savchenko-sostavyla-zaveschanye-y-hotova-nachat-holodovku\" target=\"_self\">Надежда Савченко составила завещание</a> и намерена снова начать голодовку.</p>",
//    "image": "http://78.154.165.2:8888/media/images/2015/10/28/921.jpg",
//    "read": 0,
//    "like": 0,
//    "dislike": 0,
//    "create_date": "2015-10-28T02:00:58.803755Z"
//    **************************************************
//    id (string),
//    feed (string),
//    title (string),
//    description (string),
//    content (string),
//    image (string),
//    read (integer),
//    like (integer),
//    dislike (integer),
//    create_date (string)

    private String articleID;
    private String sourceID;
    private String title;
    private String description;
    private String content;
    private String imageURL;
    private int read;
    private int like;
    private int dislike;
    private String date;

    private boolean isShown;

    private boolean isEvent;

    public Article(boolean isEvent)
    {
        this.isEvent = isEvent;
    }

    public Article(String articleID, String sourceID, String title, String description,
                   String content, String imageURL, int read, int like, int dislike, String date)
    {
        this.articleID = articleID;
        this.sourceID = sourceID;
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageURL = imageURL;
        this.read = read;
        this.like = like;
        this.dislike = dislike;
        this.date = date;
    }

    public Article(JSONObject object) throws JSONException
    {
        this.articleID = object.getString("id");
        this.sourceID = object.getString("feed");
        this.title = object.getString("title");
        this.description = object.getString("description");
        this.content = object.getString("content");
        this.imageURL = object.getString("image");
        this.read = object.getInt("read");
        this.like = object.getInt("like");
        this.dislike = object.getInt("dislike");
        this.date = object.getString("create_date");
    }

    public void setShown(boolean isShown){
        this.isShown = isShown;
    }

    public String getArticleID(){
        return articleID;
    }
    public String getSourceID(){
        return sourceID;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getContent(){
        return content;
    }
    public String getImageURL(){
        return imageURL;
    }
    public int getRead(){
        return read;
    }
    public int getLike(){
        return like;
    }
    public int getDislike(){
        return dislike;
    }
    public String getDate(){
        return date;
    }
    public boolean isEvent(){
        return isEvent;
    }
    public boolean isShown(){
        return isShown;
    }
}
