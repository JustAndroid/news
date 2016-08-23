package com.allNews.common;

/**
 * Created by Vladimir on 11.11.2015.
 */
public class ServerConstants {

    public static final String CLIENT_ID = "1";

    public static final String SERVER = "http://78.154.165.2:8888/api/";

    //GET
    /*******************************************************************************/
    //Response Code = 200; (SUCCESS)

    public static final String GET_CLIENT_INFO = "collector/client/" + CLIENT_ID;

    public static final String GET_ARTICLES = "collector/clientnews/" + CLIENT_ID;

    public static final String GET_ARTICLES_AFTER_ARTICLE_ID = "collector/clientnews/" + CLIENT_ID + "/"; // + ARTICLE_ID

    public static final String GET_SOURCES_INFO = "collector/clientfeeds/" + CLIENT_ID;

    public static final String GET_ARTICLES_BY_SOURCE_ID = "collector/feednews/" + CLIENT_ID + "/"; // + SOURCE_ID_1 + %2C + SOURCE_ID_2 + %2C + ...

    public static final String GET_ARTICLES_AFTER_ARTICLE_ID_BY_SOURCES_ID = "collector/feednews/" + CLIENT_ID + "/";  // + SOURCE_ID_1 + %2C + SOURCE_ID_2 + %2C + ... + "/" + ARTICLE_ID

    public static final String GET_EVENTS = "http://api2.withmyfriends.org/api/v3/newsapp/events/";

    //POST
    /*******************************************************************************/
    //Response Code = 201; (SUCCESS)

    public static final String CLIENT = "client"; // CLIENT_ID (String)
    public static final String OS = "os"; // Android - 1 , IOS - 2 (Integer)
    public static final String VERSION = "version"; // Application version (String)
    public static final String ARTICLE = "post"; // ARTICLE_ID (String)
    public static final String LIKE = "status"; //  True +1 , False -1 (Boolean)

    public static final String POST_FIRST_LAUNCH = "statistic/installs/"; // CLIENT, OS, VERSION

    public static final String POST_ARTICLE_LIKE = "statistic/likes/"; // CLIENT, ARTICLE, LIKE

    public static final String POST_ARTICLE_WAS_READ = "statistic/reads/"; // CLIENT, ARTICLE

}
