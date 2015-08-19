package com.allNews.db;

public class DBOpenerHelper //extends SQLiteOpenHelper
{
	public static final String TABLE_NAME = "news";
	public static final String TABLE_NAME_SOURCES = "sources";
	public static final String TABLE_NAME_HISTORY = "history";
	public static final String TABLE_NAME_CATEGORIES = "categories";
	public static final String TABLE_NAME_CATEGORIES_IN_NEWS = "categoriesInNEws";

	public static final String KEY_HISTORY_NEWS_ID = "newsId";
	public static final String KEY_HISTORY_ID = "id";

	public static final String KEY_CATEGORIES_IN_NEWS_CAT_ID = "categoriesId";
	public static final String KEY_CATEGORIES_IN_NEWS_ID = "newsId";

	public static final String KEY_CATEGORIES_ID = "id";
	public static final String KEY_CATEGORIES_RU = "RU";
	public static final String KEY_CATEGORIES_UA = "UK";
	public static final String KEY_CATEGORIES_EN = "EN";

	public static final String KEY_NEWS_ID = "news_id";
	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_LINK = "link";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_SOURCE = "source";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_NEWS_SOURCE_ID = "sourceID";
	public static final String KEY_GUI_ID = "guid";
	public static final String KEY_UPDATE_TIME = "updateTime";
	public static final String KEY_PUB_TIME = "pubTime";
	public static final String KEY_IMAGE = "imageURL";
	public static final String KEY_MARK = "mark";
	public static final String KEY_PUB_DATE = "pubDate";
	public static final String KEY_UPDATE_DATE = "updateDate";
	public static final String KEY_CATEGORY_NEWS = "categories";

	public static final String KEY_SOURCE_ID = "id";
	public static final String KEY_SOURCE_ENABLED = "enabled";
	public static final String KEY_SOURCE_NAME = "name";
	public static final String KEY_SOURCE_DEFAULT = "isDefaultSource";
	public static final String KEY_SOURCE_APP = "app_android";
	public static final String KEY_SOURCE_URL = "url";
	public static final String KEY_SOURCE_IMAGE_URL = "image_url";
	 public static final String KEY_SOURCE_LANGUAGE = "language";
	// public static final String KEY_SOURCE_DESCRIPTION = "description";
	public static final String KEY_SOURCE_IS_ACTIVE = "isActive";
    public static final long MAX_NEWS_IN_PAGE = 50;
/*    NewApp fields*/
    public static final String KEY_NODE_ID = "nid";
    public static final String KEY_NODE_CHANGED = "changed";
    public static final String KEY_NODE_CREATED = "created";
    public static final String FULL_NODE_LINK = "uri";
    public static final String KEY_CONTENT_NEW_APP = "safe_value";
    public static final String KEY_IMG_NEW_APP = "uri";
    public static final String KEY_URL_REF_LINK = "value";
    public static final String KEY_NODE_LINK = "uri";
    public static final String KEY_FIELD_IMAGE = "field_image";
    public static final String KEY_FIELD_LINK = "field_link";
    /* Taxonomy NewApp*/
    public static final String KEY_TAXONOMY_TAG_ID = "tid";
    public static final String KEY_FIELD_TAGS = "field_tags";


    //public static String DB_PATH = "";
	//public static String DB_NAME = "allnews.db";


    //	private SQLiteDatabase myDataBase;
//	private final Context myContext;
//	private static DBOpenerHelper instance;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
//	public DBOpenerHelper(Context context) {
//
//		super(context, DB_NAME, null, 3);
//		DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
//				+ "/allNews/";
//		new File(DB_PATH).mkdirs();
//		// DB_PATH = context.getFilesDir().getParent() + "/databases/";
//		this.myContext = context;
//	}
//
//	public static synchronized DBOpenerHelper getHelper(Context context) {
//		if (instance == null)
//			instance = new DBOpenerHelper(context);
//
//		return instance;
//	}

//	/**
//	 * Creates a empty database on the system and rewrites it with your own
//	 * database.
//	 */
//	public void createDataBase() throws IOException {
//
//		boolean dbExist = checkDataBase();
//
//		if (dbExist) {
//			// do nothing - database already exist
//		} else {
//			try {
//
//				copyDataBase();
//				this.getWritableDatabase();
//			} catch (IOException e) {
//
//				throw new Error("Error copying database");
//
//			}
//		}
//
//	}
//
//	/**
//	 * Check if the database already exist to avoid re-copying the file each
//	 * time you open the application.
//	 * 
//	 * @return true if it exists, false if it doesn't
//	 */
//	private boolean checkDataBase() {
//
//		SQLiteDatabase checkDB = null;
//		String myPath = DB_PATH + DB_NAME;
//		try {
//
//			checkDB = SQLiteDatabase.openDatabase(myPath, null,
//					SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
//
//		} catch (SQLiteException e) {
//
//			// database does't exist yet.
//
//		}
//
//		if (checkDB != null) {
//
//			checkDB.close();
//			if (updateDb()) {
//				new File(myPath).delete();
//				checkDB = null;
//			}
//		} else {
//			SharedPreferences sp = PreferenceManager
//					.getDefaultSharedPreferences(myContext);
//			SharedPreferences.Editor editor = sp.edit();
//			editor.putBoolean("up", false);
//
//			editor.commit();
//		}
//
//		return checkDB != null ? true : false;
//	}
//
//	private boolean updateDb() {
//		SharedPreferences sp = PreferenceManager
//				.getDefaultSharedPreferences(myContext);
//
//		boolean needUpdateDb = sp.getBoolean("up", true);
//		//needUpdateDb = true;
//		if (needUpdateDb) {
//
//			SharedPreferences.Editor editor = sp.edit();
//
//			ArrayList<Source> sourceList = getSources();
//			for (int i = 0; i < sourceList.size(); i++) {
//				editor.remove("" + sourceList.get(i).getSourceID());
//			}
//			editor.putBoolean("up", false);
//			editor.putBoolean(Preferences.PREF_RUN, true);
//			editor.commit();
//
//		}
//		return needUpdateDb;
//	}
//
//	/**
//	 * Copies your database from your local assets-folder to the just created
//	 * empty database in the system folder, from where it can be accessed and
//	 * handled. This is done by transfering bytestream.
//	 */
//	private void copyDataBase() throws IOException {
//
//		// Open your local db as the input stream
//		InputStream myInput = myContext.getAssets().open(DB_NAME);
//
//		// Path to the just created empty db
//		String outFileName = DB_PATH + DB_NAME;
//
//		// Open the empty db as the output stream
//		OutputStream myOutput = new FileOutputStream(outFileName);
//
//		// transfer bytes from the inputfile to the outputfile
//		byte[] buffer = new byte[1024];
//		int length;
//		while ((length = myInput.read(buffer)) > 0) {
//			myOutput.write(buffer, 0, length);
//		}
//
//		// Close the streams
//		myOutput.flush();
//		myOutput.close();
//		myInput.close();
//
//	}
//
//	public void openDataBase() throws SQLException {
//
//		// Open the database
//		String myPath = DB_PATH + DB_NAME;
//		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
//				SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
//
//	}
//
//	@Override
//	public synchronized void close() {
//
//		if (myDataBase != null)
//
//			super.close();
//
//	}
//
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//	}
//
//	public Cursor getCursor(String table, String where, String orderBy) {
//		myDataBase = getWritableDatabase();
//		return myDataBase.query(table, null, where, null, null, null, orderBy);
//	}
//
//	public long insertItem(String table, ContentValues initalValues) {
//		long rowid = 0;
//
//		try {
//			myDataBase = getWritableDatabase();
//			rowid = myDataBase.insertOrThrow(table, null, initalValues);
//
//			return rowid;
//
//		} catch (SQLException e) {
//			Log.e(e.toString(), e.toString());
//		} finally {
//
//		}
//
//		return rowid;
//	}
//
//	public int updateTableItem(ContentValues updatevalues, String table,
//			String pimaryID, String ID) {
//		int rowid = 0;
//
//		try {
//			myDataBase = getWritableDatabase();
//			rowid = myDataBase.update(table, updatevalues, pimaryID + "=?",
//					new String[] { ID });
//
//		} catch (SQLException e) {
//			Log.e(e.toString(), e.toString());
//
//		} finally {
//
//		}
//
//		// Log.e("update "+updatevalues.toString(), "s "+rowid);
//		return rowid;
//	}
//
//	public ArrayList<News> getAllNews(boolean onlyImportant, long lastDate) {
//		ArrayList<News> newss = new ArrayList<News>();
//		News currentNews;
//		myDataBase = getWritableDatabase();
//
//		Cursor cursor = getAllEntries(onlyImportant, lastDate);
//
//		if (cursor.moveToFirst()) {
//			do {
//				currentNews = setNewsFromDb(cursor);
//				if (currentNews != null)
//					newss.add(currentNews);
//
//				currentNews = null;
//			} while (cursor.moveToNext());
//
//		} else
//			System.out.println("");
//		cursor.close();
//
//		return newss;
//	}
//
//	public News getNewsById(long newsID) {
//
//		myDataBase = getWritableDatabase();
//
//		String where = KEY_NEWS_ID + " = " + newsID;
//		Cursor cursor = getCursor(TABLE_NAME, where, null);
//		cursor.moveToFirst();
//		News currentNews = setNewsFromDb(cursor);
//		// currentNews = null ;
//		cursor.close();
//
//		return currentNews;
//	}
//
//	private News setNewsFromDb(Cursor cursor) {
//		if (cursor != null && cursor.getCount() > 0) {
//			int newsid = cursor.getInt(cursor.getColumnIndex(KEY_NEWS_ID));
//			String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
//			String link = cursor.getString(cursor.getColumnIndex(KEY_LINK));
//			String categories = cursor.getString(cursor
//					.getColumnIndex(KEY_CATEGORY_NEWS));
//			String description = cursor.getString(cursor
//					.getColumnIndex(KEY_DESCRIPTION));
//			String imageUrl = cursor
//					.getString(cursor.getColumnIndex(KEY_IMAGE));
//
//			int sourceId = cursor.getInt(cursor
//					.getColumnIndex(KEY_NEWS_SOURCE_ID));
//			String pubDate = cursor.getString(cursor
//					.getColumnIndex(KEY_PUB_DATE));
//			String updateDate = cursor.getString(cursor
//					.getColumnIndex(KEY_UPDATE_DATE));
//			String content = cursor.getString(cursor
//					.getColumnIndex(KEY_CONTENT));
//			long time = cursor.getLong(cursor.getColumnIndex(KEY_UPDATE_TIME));
//			long pubtime = cursor.getLong(cursor.getColumnIndex(KEY_PUB_TIME));
//
//			// boolean mark = false;
//			// if
//			// (cursor.getString(cursor.getColumnIndex(KEY_MARK)).equals("1"))
//			// mark = true;
//			int mark = cursor.getString(cursor.getColumnIndex(KEY_MARK))
//					.equals(1) ? 1 : 0;
//			// Log.e(cursor.getString(cursor.getColumnIndex(KEY_MARK)),
//			// ""+mark);
//			// cursor.close();
//			News currentNews = new News();
//			currentNews.setNewsId(newsid);
//			currentNews.setTitle(title);
//		//	currentNews.setCategory(categories);
//			currentNews.setLink(link);
//			currentNews.setDescription(description);
//			currentNews.setcontent(content);
//
//			currentNews.setsourceID(sourceId);
//			 currentNews.setSource(getSourceById(sourceId).getName());
//			currentNews.setisMarked(mark);
//			currentNews.setPubDate(pubDate);
//			currentNews.setUpdateDate(updateDate);
//			currentNews.setUpdateTime(time);
//			currentNews.setPubTime(pubtime);
//			currentNews.setImageUrl(imageUrl);
//			return currentNews;
//		} else
//			return null;
//	}
//
//	public ArrayList<Long> getLatestNewsIDs() {
//		myDataBase = getWritableDatabase();
//		ArrayList<Long> historyNews = new ArrayList<Long>();
//		// String order = KEY_HISTORY_ID + "  DESC" ;
//		Cursor cursor = getCursor(TABLE_NAME_HISTORY, null, null);
//		cursor.moveToLast();
//		int count = cursor.getCount() > 10 ? 10 : cursor.getCount();
//		// Log.e("history count", ""+count);
//		for (int i = 0; i < count; i++) {
//			historyNews.add(cursor.getLong(cursor
//					.getColumnIndex(KEY_HISTORY_NEWS_ID)));
//			cursor.moveToPrevious();
//		}
//
//		cursor.close();
//
//		Collections.reverse(historyNews);
//		return historyNews;
//	}
//
//	public Source getSourceById(int sourceId) {
//		ArrayList<Source> sourceList = getSources();
//
//		for (int i = 0; i < sourceList.size(); i++) {
//			if (sourceList.get(i).getSourceID() == sourceId)
//				return sourceList.get(i);
//		}
//		return null;
//	}
//
//	public String getLatestNewsDate() {
//		myDataBase = getWritableDatabase();
//		Cursor cursor = myDataBase.query(TABLE_NAME, new String[] {
//				KEY_UPDATE_DATE, "MAX(" + KEY_UPDATE_TIME + ")" }, null, null,
//				null, null, null, null);
//
//		if (cursor.moveToFirst()) {
//
//			String LatestNewsDate = cursor.getString(cursor
//					.getColumnIndex(KEY_UPDATE_DATE));
//			cursor.close();
//
//			return LatestNewsDate;
//		}
//		cursor.close();
//
//		return null;
//	}
//
//	public long getLatestNewsTime() {
//		myDataBase = getWritableDatabase();
//		Cursor cursor = myDataBase.query(TABLE_NAME, new String[] {
//				KEY_UPDATE_TIME, "MAX(" + KEY_UPDATE_TIME + ")" }, null, null,
//				null, null, null, null);
//
//		if (cursor.getCount() != 0) {
//			cursor.moveToFirst();
//			long LatestNewsTime = cursor.getLong(cursor
//					.getColumnIndex(KEY_UPDATE_TIME));
//			cursor.close();
//
//			return LatestNewsTime;
//		}
//		cursor.close();
//
//		return 0;
//	}
//
//	public Cursor getAllEntries(boolean onlyImportant, long lastDate) {
//		SharedPreferences sp = PreferenceManager
//				.getDefaultSharedPreferences(myContext);
//
//		String order = KEY_PUB_TIME + " DESC";
//		String selection = "";
//		// String[] selectionArgs = null;
//		if (onlyImportant) {
//			selection = KEY_MARK + " = 1 ";
//
//		} else {
//			selection = selection + KEY_NEWS_SOURCE_ID + " IN (";
//			ArrayList<Source> sourceList = getSources();
//
//			for (int i = 0; i < sourceList.size(); i++) {
//
//				if (sp.getBoolean("" + sourceList.get(i).getSourceID(),
//						sourceList.get(i).getIsActive() == 1 ? true : false))
//					selection = selection + sourceList.get(i).getSourceID()
//							+ ",";
//			}
//			if (selection.contains(","))
//				selection = selection.substring(0, selection.length() - 1);
//			selection = selection + ")";
//		}
//
//		if (!onlyImportant && lastDate > 0) {
//			selection = selection + " AND " + KEY_PUB_TIME + " < " + lastDate;
//		}
//
//		String maxNews = "" + MAX_NEWS_IN_PAGE;
//	 
//
//		Log.e("selection", selection);
//		Cursor cursor = myDataBase.query(TABLE_NAME, null, selection, null,
//				null, null, order, maxNews);
//		//
//		return cursor;
//	}
//
//	public Cursor getAllEntriesByCategory(boolean onlyImportant,
//			String newsIds, long lastDate) {
//		SharedPreferences sp = PreferenceManager
//				.getDefaultSharedPreferences(myContext);
//		String order = KEY_PUB_TIME + " DESC";
//		String selection = "";
//
//		if (onlyImportant) {
//			selection = KEY_MARK + " = 1 AND ";
//
//		}
//		selection = selection + KEY_NEWS_ID + " IN (" + newsIds + ")";
//		if (lastDate > 0) {
//			selection = selection + " AND " + KEY_PUB_TIME + " < " + lastDate;
//		}
//
//		String maxNews = "" + MAX_NEWS_IN_PAGE;
//	 
//		Cursor cursor = myDataBase.query(TABLE_NAME, null, selection, null,
//				null, null, order, maxNews);
//
//		return cursor;
//	}
//
//	public int clearDb() {
//		myDataBase = getWritableDatabase();
//		try {
//			myDataBase.delete(TABLE_NAME, KEY_MARK + " = ?",
//					new String[] { "0" });
//			myDataBase.delete(TABLE_NAME_CATEGORIES_IN_NEWS, null, null);
//			myDataBase.execSQL("vacuum");
//		} catch (Exception e) {
//			Log.e("Exception", "" + e);
//			return 0;
//		}
//		return 1;
//
//	}
//
//	public void deleteOldNews() {
//		SharedPreferences sp = PreferenceManager
//				.getDefaultSharedPreferences(myContext);
//
//		long timeToSavePref = Integer.parseInt(sp.getString(
//				Preferences.PREF_SAVE, "48")) * 60 * 60 * 1000;
//		long timeToDelNews = System.currentTimeMillis() - timeToSavePref;
//		myDataBase = getWritableDatabase();
//		myDataBase.delete(TABLE_NAME, KEY_UPDATE_TIME + " < ? AND " + KEY_MARK
//				+ " = ?", new String[] { "" + timeToDelNews, "0" });
//
//	}
//
//	public void delete(String table, String key, String id) {
//
//		myDataBase = getWritableDatabase();
//		myDataBase.delete(table, key + " = ?", new String[] { id });
//
//	}
//
//	
//	
//
//	public boolean isExistRow(String table, String key, String id) {
//		// myDataBase.close();
//		myDataBase = getWritableDatabase();
//		Cursor cursor = myDataBase.query(table, null, key + " = ?",
//				new String[] { id }, null, null, null);
//		boolean exist = true;
//		if (cursor.getCount() == 0)
//			exist = false;
//		cursor.close();
//
//		return exist;
//	}
//
//	public void delOldRow(String table, String key, String idToNotDel) {
//		myDataBase = getWritableDatabase();
//		myDataBase.delete(table, key + " NOT IN (" + idToNotDel + ")", null);
//		// Log.e("delOldSources ", "" + row); // delete table.* from table where
//		// not in
//		// (1,2,3)
//
//	}
//
//	public ArrayList<Source> getSources() {
//		myDataBase = getWritableDatabase();
//		Cursor cursor = myDataBase.query(TABLE_NAME_SOURCES, null, null, null,
//				null, null, null);
//		// Log.e("getSources ", "" + cursor.getCount());
//		ArrayList<Source> sourceList = new ArrayList<Source>();
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			for (int i = 0; i < cursor.getCount(); i++) {
//
//				Source source = new Source();
//				source.setSourceID(cursor.getInt(cursor
//						.getColumnIndex(KEY_SOURCE_ID)));
//				source.setName(cursor.getString(cursor
//						.getColumnIndex(KEY_SOURCE_NAME)));
//				source.setApp(cursor.getString(cursor
//						.getColumnIndex(KEY_SOURCE_APP)));
//				source.setDefault(cursor.getInt(cursor
//						.getColumnIndex(KEY_SOURCE_DEFAULT)));
//				source.setEnabled(cursor.getInt(cursor
//						.getColumnIndex(KEY_SOURCE_ENABLED)));
//				source.setIsActive(cursor.getInt(cursor
//						.getColumnIndex(KEY_SOURCE_IS_ACTIVE)));
//				sourceList.add(source);
//				cursor.moveToNext();
//			}
//		}
//		cursor.close();
//		//
//		return sourceList;
//	}
//
//	public ArrayList<Categories> getCategories() {
//		ArrayList<Categories> catList = new ArrayList<Categories>();
//		myDataBase = getWritableDatabase();
//		Cursor cursor = myDataBase.query(TABLE_NAME_CATEGORIES, null, null,
//				null, null, null, null);
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			for (int i = 0; i < cursor.getCount(); i++) {
//				if (!isCategoryEmpty(cursor.getInt(cursor
//						.getColumnIndex(KEY_CATEGORIES_ID)))) {
//					Categories category = new Categories();
//					category.setCategoryID(cursor.getInt(cursor
//							.getColumnIndex(KEY_CATEGORIES_ID)));
//					category.setEn(cursor.getString(cursor
//							.getColumnIndex(KEY_CATEGORIES_EN)));
//					category.setRu(cursor.getString(cursor
//							.getColumnIndex(KEY_CATEGORIES_RU)));
//					category.setUa(cursor.getString(cursor
//							.getColumnIndex(KEY_CATEGORIES_UA)));
//
//					catList.add(category);
//				}
//				cursor.moveToNext();
//			}
//		}
//		cursor.close();
//
//		return catList;
//	}
//
//	private boolean isCategoryEmpty(int categoriesID) {
//		Cursor cursor = myDataBase.query(TABLE_NAME_CATEGORIES_IN_NEWS, null,
//				KEY_CATEGORIES_IN_NEWS_CAT_ID + " = ?", new String[] { ""
//						+ categoriesID }, null, null, null);
//		boolean isCategoryEmpty = cursor.getCount() > 0 ? false : true;
//		cursor.close();
//		return isCategoryEmpty;
//	}
//
//	public ArrayList<News> getNewsByCategory(int categoriesID, boolean marked,
//			long lastDate) {
//		ArrayList<News> news = new ArrayList<News>();
//		myDataBase = getWritableDatabase();
//		Cursor cursor = myDataBase.query(TABLE_NAME_CATEGORIES_IN_NEWS, null,
//				KEY_CATEGORIES_IN_NEWS_CAT_ID + " = ?", new String[] { ""
//						+ categoriesID }, null, null, null);
//		String newsIdSelection = "";
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			for (int i = 0; i < cursor.getCount(); i++) {
//				newsIdSelection = newsIdSelection
//						+ cursor.getInt(cursor
//								.getColumnIndex(KEY_CATEGORIES_IN_NEWS_ID))
//						+ ",";
//
//				cursor.moveToNext();
//			}
//			newsIdSelection = newsIdSelection.substring(0,
//					newsIdSelection.length() - 1);
//		}
//		cursor.close();
//
//		cursor = getAllEntriesByCategory(marked, newsIdSelection, lastDate);
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			for (int i = 0; i < cursor.getCount(); i++) {
//				News curnews = setNewsFromDb(cursor);
//				if (curnews != null)
//					news.add(curnews);
//
//				cursor.moveToNext();
//			}
//
//		}
//		cursor.close();
//
//		// Log.e("newsIdSelection ", "" + newsIdSelection);
//		return news;
//	}
//
//	public SQLiteDatabase getWritableDatabase() {
//		if (myDataBase == null)
//			openDataBase();
//		return myDataBase;
//	}
//
//	public void deleteNewNews() {
//
//		long time = (6 * 60 + 30) * 60 * 1000;
//		long timeToDelNews = System.currentTimeMillis() - time;
//		myDataBase = getWritableDatabase();
//		myDataBase.delete(TABLE_NAME, KEY_UPDATE_TIME + " > ?",
//				new String[] { "" + timeToDelNews });
//
//	}
}
