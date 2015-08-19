package com.allNews.data.NewApp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tags_new_app")
public class TagNewApp {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField()
    private String TagID;


    @DatabaseField(foreign = true)
    private NewApp newApp;

    @DatabaseField
    private int isCategory;

    @DatabaseField()
    String tagName;

    public TagNewApp() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagID() {
        return TagID;
    }

    public void setTagID(String tagID) {
        TagID = tagID;
    }

    public int getIsCategory() {
        return isCategory;
    }

    public void setIsCategory(int isCategory) {
        this.isCategory = isCategory;
    }

    public NewApp getNewApp() {
        return newApp;
    }

    public void setNewApp(NewApp newApp) {
        this.newApp = newApp;
    }

    @Override
    public String toString() {
        return "TagNewApp{" +
                "TagID='" + TagID + '\'' +
                                '}';
    }
}
