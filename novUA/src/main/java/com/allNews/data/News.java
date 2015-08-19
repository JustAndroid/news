package com.allNews.data;

import android.content.Context;

import com.allNews.db.DBOpenerHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import gregory.network.rss.R;

@DatabaseTable(tableName = "table_news")
public class News {
	@DatabaseField(generatedId = true)
	private int idIndex;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_ID)
	@DatabaseField(columnName = DBOpenerHelper.KEY_ID, unique = true)
	private int newsID;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_TITLE)
	@DatabaseField()
	private String title;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_LINK)
	@DatabaseField()
	private String link;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_DESCRIPTION)
	@DatabaseField()
	private String description;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_CONTENT)
	@DatabaseField()
	private String content;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_NEWS_SOURCE_ID)
	@DatabaseField()
	private int sourceID;

	@DatabaseField()
	private String sourceName;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_PUB_DATE)
	@DatabaseField()
	private String pubDate;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_UPDATE_DATE)
	@DatabaseField()
	private String updateDate;

	@Expose(serialize = false)
	@SerializedName(DBOpenerHelper.KEY_MARK)
	@DatabaseField()
	private int isMarked;

	@Expose(serialize = false)
	@SerializedName(DBOpenerHelper.KEY_UPDATE_TIME)
	@DatabaseField()
	private long updateTime;

	@Expose(serialize = false)
	@SerializedName(DBOpenerHelper.KEY_PUB_TIME)
	@DatabaseField()
	private long pubTime;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_CATEGORY_NEWS)
	private List<String> category;

	@DatabaseField(canBeNull = true)
	private int category1;

	@DatabaseField(canBeNull = true)
	private int category2;

	@DatabaseField(canBeNull = true)
	private int category3;

	@DatabaseField()
	private int isRead;

	@DatabaseField()
	private int isShown;

	@DatabaseField()
	private int isTop;

	@DatabaseField()
	private int isNewApp;

    @DatabaseField()
    private int isB2B;

	@Expose(serialize = true)
	@SerializedName("quantity")
	@DatabaseField()
	private int quantity;

	@Expose(serialize = true)
	@SerializedName(DBOpenerHelper.KEY_IMAGE)
	@DatabaseField()
	private String imageUrl;

	@Expose(serialize = true)
	@SerializedName("image180x150URL")
	@DatabaseField()
	private String imageSmallUrl;

	@DatabaseField
	private int likesCount;

	@DatabaseField
	private int dislikesCount;

	@DatabaseField
	private int isLike;

	@DatabaseField
	private int isDislike;

	@DatabaseField
	private int isArticle;

	public News() {
	}

	// private News(Parcel in) {
	// this();
	// readFromParcel(in);
	// }


	public int getIsArticle() {
		return isArticle;
	}

	public void setIsArticle(int isArticle) {
		this.isArticle = isArticle;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public int getIsLike() {
		return isLike;
	}

	public void setIsLike(int isLike) {
		this.isLike = isLike;
	}

	public int getIsDislike() {
		return isDislike;
	}

	public void setIsDislike(int isDislike) {
		this.isDislike = isDislike;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public int getDislikesCount() {
		return dislikesCount;
	}

	public void setDislikesCount(int dislikesCount) {
		this.dislikesCount = dislikesCount;
	}

	public int getIsB2() {
        return isB2B;
    }

    public void setIsB2B(int isB2BLogger) {
        this.isB2B = isB2BLogger;
    }

    public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageSmallUrl(Context context) {
		if (!imageSmallUrl.equals("")
				&& !imageSmallUrl.startsWith(context.getResources().getString(
						R.string.url_domain)))
			imageSmallUrl = context.getResources().getString(
					R.string.url_domain)
					+ context.getResources().getString(R.string.image_url_base)
					+ imageSmallUrl;

	}

	public String getImageSmallUrl() {

		return imageSmallUrl;
	}

	public void setUpdateTime(long time) {
		this.updateTime = time;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setPubTime(long time) {
		this.pubTime = time;
	}

	public long getPubTime() {
		return pubTime;
	}

	public void setNewsId(int newsID) {
		this.newsID = newsID;

	}

	public int getNewsID() {
		return newsID;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getcontent() {
		return content;
	}

	public void setcontent(String content) {
		this.content = content;
	}

	public int getsourceID() {
		return sourceID;
	}

	public void setsourceID(int sourceID) {
		this.sourceID = sourceID;
	}

	public String getSource() {
		return sourceName;
	}

	public void setSource(String sourceName) {
		this.sourceName = sourceName;
	}

	public int getMarked() {
		return isMarked;
	}

	public void setisMarked(int isMarked) {
		this.isMarked = isMarked;
	}

	public int isRead() {
		return isRead;
	}

	public void setRead(int isRead) {
		this.isRead = isRead;
	}

	public int isTop() {
		return isTop;
	}

	public void setIsTop(int isTop) {
		this.isTop = isTop;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public int getCategory1() {
		return category1;
	}

	public void setCategory1(int category1) {
		this.category1 = category1;
	}

	public int getCategory2() {
		return category2;
	}

	public void setCategory2(int category2) {
		this.category2 = category2;
	}

	public int getCategory3() {
		return category3;
	}

	public void setCategory3(int category3) {
		this.category3 = category3;
	}

	public int isNewApp() {
		return isNewApp;
	}

	public void setIsNewApp(int newApp) {
		this.isNewApp = newApp;

	}
	
	public int isShown() {
		return isShown;
	}

	public void setShown() {
		this.isShown = 1;

	}
	 
}
