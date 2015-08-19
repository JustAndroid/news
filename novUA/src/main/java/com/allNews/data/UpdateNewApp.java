package com.allNews.data;


import com.allNews.db.DBOpenerHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_update_new_app")
public class UpdateNewApp {

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_NODE_ID)
    @DatabaseField(unique = true)
    private int nodeID;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_NODE_CREATED)
    @DatabaseField()
    private long node_created;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_NODE_CHANGED)
    @DatabaseField()
    private long node_changed;

    @Expose(serialize = true)
    @DatabaseField
    @SerializedName(DBOpenerHelper.KEY_NODE_LINK)
    private String nodeLink;

    public UpdateNewApp() {
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public long getNode_changed() {
        return node_changed;
    }

    public void setNode_changed(long node_changed) {
        this.node_changed = node_changed;
    }

    public long getNode_created() {
        return node_created;
    }

    public void setNode_created(long node_created) {
        this.node_created = node_created;
    }

    public String getNodeLink() {
        return nodeLink = nodeLink + ".json";
    }

    public void setNodeLink(String nodeLink) {
        this.nodeLink = nodeLink;
    }
}
