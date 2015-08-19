
package com.allNews.data;

import com.allNews.db.DBOpenerHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_taxonomy_newapp")
public class TaxonomyNewApp {

    @Expose(serialize = true)
    @DatabaseField
    private String name;

    @Expose(serialize = true)
    @SerializedName(DBOpenerHelper.KEY_TAXONOMY_TAG_ID)
    @DatabaseField(unique = true, id = true)
    private int tagID;




    public TaxonomyNewApp() {
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

}
