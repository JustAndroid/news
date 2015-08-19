package com.allNews.data.NewApp;

import com.allNews.db.DBOpenerHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;


@DatabaseTable(tableName = "table_new_app")
   public class NewApp {


    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_NODE_ID)
    @DatabaseField(unique = true, id = true)
    private int nodeID;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_TITLE)
    @DatabaseField()
    private String title;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_NODE_CREATED)
    @DatabaseField(columnName = DBOpenerHelper.KEY_NODE_CREATED)
    private long node_created;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_NODE_CHANGED)
    @DatabaseField(columnName = DBOpenerHelper.KEY_NODE_CHANGED)
    private long node_changed;

    @Expose(serialize = true)
    private Body body;

    @DatabaseField
    private String content;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_FIELD_IMAGE)
    private FieldImage fieldImage;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_FIELD_LINK)
    private FieldLink fieldLink;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_FIELD_TAGS)
    private FieldTags fieldTags;

    @DatabaseField
    private String refLink;

    @DatabaseField
    private String imgUrl;

    @DatabaseField()
    private int isShown;

    @DatabaseField()
    private String summary;

    @ForeignCollectionField()
    private Collection<TagNewApp> TagNewApps;


    public NewApp(){

    }



    public Collection<TagNewApp> getTagNewApps() {
        return TagNewApps;
    }

    public void setTagNewApps(Collection<TagNewApp> tagNewApps) {
        TagNewApps = tagNewApps;
    }

    public FieldTags getFieldTags() {
        return fieldTags;
    }

    public void setFieldTags(FieldTags fieldTags) {
        this.fieldTags = fieldTags;
    }

    public int getIsShown() {
        return isShown;
    }

    public void setIsShown() {
        this.isShown = 1;
    }

    public String getRefLink() {
        return refLink;
    }

    public void setRefLink(String refLink) {
        this.refLink = refLink;
    }

    public FieldLink getFieldLink() {
        return fieldLink;
    }

    public void setFieldLink(FieldLink fieldLink) {
        this.fieldLink = fieldLink;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public FieldImage getFieldImage() {
        return fieldImage;
    }

    public void setFieldImage(FieldImage fieldImage) {
        this.fieldImage = fieldImage;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getNode_created() {
        return node_created;
    }

    public void setNode_created(long node_created) {
        this.node_created = node_created;
    }

    public long getNode_changed() {
        return node_changed;
    }

    public void setNode_changed(long node_changed) {
        this.node_changed = node_changed;
    }

    public void setIsShown(int isShown) {
        this.isShown = isShown;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}