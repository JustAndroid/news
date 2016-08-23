package com.allNews.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;

import com.allNews.adapter.NewAppCollectionPagerAdapter;
import com.allNews.adapter.NewsCollectionPagerAdapter;
import com.allNews.application.App;
import com.allNews.data.History;
import com.allNews.data.NewApp.NewApp;
import com.allNews.data.News;
import com.allNews.db.DatabaseHelper;
import com.allNews.managers.DialogManager;
import com.allNews.managers.ManagerNews;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.web.Requests;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class NewsCollectionActivity extends ActionBarActivity {
    public static final String NEWS_ID_KEY = "NEWS_ID_KEY";
    public static final String NEWS_LIST_IDS_KEY = "NEWS_LIST_IDS_KEY";
    public static final String NEWS_POSITION_KEY = "NEWS_POSITION_KEY";
    public static final String NEW_APP_POSITION_KEY = "NEW_APP_POSITION_KEY";
    public static final String NEWS_CATEGORY_KEY = "NEWS_CATEGORY_KEY";
    public static final String NEW_APP_NODE_ID = "NEW_APP_NODE_ID";
    public static final String NEW_APP_LIST_IDS_KEY = "NEW_APP_LIST_IDS_KEY";

    public boolean needRefresh = false;
    private boolean changeTheme = false;
    private News currentNews;
    private NewApp currentNewApp;
    private ActionBar actionBar;
    private ViewPager mViewPager;
    private int curTheme = 0;
    private Menu mmenu;
    private boolean isNewApp;
    private NewsFragment currentNewsFr;
    private NewAppFragment curentNewAppFr;
    private static RequestQueue requestQueue;


    public void onCreate(Bundle savedInstanceState) {

        curTheme = MyPreferenceManager.getCurrentTheme(this);

        super.onCreate(savedInstanceState);
        if (MyPreferenceManager.getRotatePref(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setContentView(R.layout.news_collections);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }


        mViewPager = (ViewPager) findViewById(R.id.pager);

        initView(NewsCollectionActivity.this);

    }

    public static void tryMakeSynch(final Context context, final int newsId, final boolean isLike, final boolean isDislike) {
        Utils.isOnline(context, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ManagerNews.setNewsToHistory(context,
                        newsId, false, false);
                if (isLike) {
                    ManagerNews.setNewsToHistory(context, newsId, true, false);
                }
                if (isDislike) {
                    ManagerNews.setNewsToHistory(context, newsId, false, true);
                }
                switch (msg.what) {
                    case 1:
                        makeSynch(context);
                        break;

                    default:

                        break;
                }

            }
        });

    }

    private static void makeSynch(final Context context) {

        final Handler handler = new Handler();

        new Thread(new Runnable() {

            @Override
            public void run() {
                final List<History> newToSynch = ManagerNews
                        .getNewsFromHistoryToSync(context);
                final List<History> likesToSynch = ManagerNews.getLikeFromHistoryToSync(context);
                final List<History> disLikesToSynch = ManagerNews.getDisLikeFromHistoryToSync(context);

                if (newToSynch != null && !newToSynch.isEmpty())
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            requestQueue = App.getRequestQueue(context);

                            requestQueue.add(Requests.getRequestForNewsSynch(
                                    context, newToSynch,
                                    new Listener<String>() {

                                        @Override
                                        public void onResponse(String response) {
                                            // Log.e("onResponse " , "response "
                                            // +response);
                                            if (likesToSynch != null && !likesToSynch.isEmpty()) {
                                                requestQueue.add(Requests.getRequestForLikesSynch(context, likesToSynch, new Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {

                                                            }
                                                        },
                                                        new ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Log.e("onErrorResponse ", "error "
                                                                        + error);
                                                            }
                                                        }));
                                            }
                                            if(disLikesToSynch != null && !disLikesToSynch.isEmpty()) {
                                                requestQueue.add(Requests.getRequestForDislikesSynch(context, disLikesToSynch, new Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {

                                                    }
                                                }, new ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.e("onErrorResponse ", "error "
                                                                + error);
                                                    }
                                                }));
                                            }
                                            ManagerNews
                                                    .deleteNewsFromHistory(context);

                                        }
                                    }, new ErrorListener() {

                                        @Override
                                        public void onErrorResponse(
                                                VolleyError error) {
                                            Log.e("onErrorResponse ", "error "
                                                    + error);

                                        }
                                    }));
                        }
                    });
            }
        }).start();

    }

    private void initView(Context context) {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(NEWS_ID_KEY)
                && bundle.containsKey(NEWS_LIST_IDS_KEY)
                && bundle.containsKey(NEWS_POSITION_KEY)) {
            isNewApp = false;
            int newsId = bundle.getInt(NEWS_ID_KEY);
            int position = bundle.getInt(NEWS_POSITION_KEY);
            ArrayList<Integer> newsIdsList = bundle
                    .getIntegerArrayList(NEWS_LIST_IDS_KEY);
            // String newsCategory = bundle.getString(NEWS_CATEGORY_KEY);
            initNewsCollectionPagerAdapter(context, newsIdsList, newsId, position);
        } else if (bundle != null && bundle.containsKey(NEWS_ID_KEY)) {
            isNewApp = false;
            int newsId = bundle.getInt(NEWS_ID_KEY);

            ArrayList<Integer> newsIdsList = new ArrayList<Integer>();
            newsIdsList.add(newsId);
            initNewsCollectionPagerAdapter(context, newsIdsList, newsId, 0);
        } else if (bundle != null && bundle.containsKey(NEW_APP_NODE_ID)
                && bundle.containsKey(NEW_APP_LIST_IDS_KEY)
                && bundle.containsKey(NEW_APP_POSITION_KEY)) {
            int newsId = bundle.getInt(NEW_APP_NODE_ID);
            int position = bundle.getInt(NEW_APP_POSITION_KEY);
            ArrayList<Integer> newsIdsList = bundle
                    .getIntegerArrayList(NEW_APP_LIST_IDS_KEY);
            isNewApp = true;
            initNewsCollectionPagerAdapter(context, newsIdsList, newsId, position);

        } else {
            finish();
        }
    }

    // private void getNewsIdsByCategoryInThread(int newsId, final String
    // newsCategory) {
    // final Handler handler = new Handler();
    // new Thread(new Runnable() {
    //
    // @Override
    // public void run() {
    // final ArrayList<Integer> newsIdsList =
    // ManagerNews.getNewsIdsByCategory(NewsCollectionActivity.this,
    // newsCategory);
    // handler.post(new Runnable() {
    //
    // @Override
    // public void run() {
    // initNewsCollectionPagerAdapter(newsIdsList);
    //
    // }
    // });
    // }
    // }).start();
    //
    //
    //
    //
    // }

    private void initNewsCollectionPagerAdapter(final Context context, ArrayList<Integer> newsIdsList,
                                                int newsId, int position) {
        final NewsCollectionPagerAdapter newsCollectionPagerAdapter = new NewsCollectionPagerAdapter(
                getSupportFragmentManager(), newsIdsList);
        final NewAppCollectionPagerAdapter newAppCollectionPagerAdapter = new NewAppCollectionPagerAdapter(getSupportFragmentManager(), newsIdsList);
        if (isNewApp) {
            mViewPager.setAdapter(newAppCollectionPagerAdapter);
        } else {
            mViewPager.setAdapter(newsCollectionPagerAdapter);
        }


        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                try {
                    if (isNewApp) {
                        curentNewAppFr = (NewAppFragment) newAppCollectionPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
                        currentNewApp = curentNewAppFr.selectedNewsItem;

                        updateMenu();

                    } else {
                        currentNewsFr = (NewsFragment) newsCollectionPagerAdapter
                                .getRegisteredFragment(mViewPager.getCurrentItem());

                        currentNews = currentNewsFr.selectedNewsItem;
                        updateMenu();
                        Log.e("onPageScrolled ", "currentNews " + currentNews);
                        if (currentNews.isRead() == 0) {
                            tryMakeSynch(context, currentNews.getNewsID(), false, false);
                        }

                        if (currentNews.isRead() == 0) {
                            setNewsIsRead();
                        }
                        updateMenu();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        int currentItem = position;
        if (newsIdsList.get(position) != newsId) {

            for (int i = 0; i < newsIdsList.size(); i++) {
                if (newsIdsList.get(i) == newsId) {
                    currentItem = i;
                    break;
                }
            }

        }
        mViewPager.setCurrentItem(currentItem);

    }

    protected void setNewsIsRead() {
        currentNews.setRead(1);
        new Thread(new Runnable() {

            @Override
            public void run() {
                ManagerNews.updateNews(getApplicationContext(), "isRead",
                        currentNews.getNewsID());

            }
        }).start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenu() {
        if (currentNews != null && mmenu != null
                && currentNews.getSource() != null) {
            String deviceBrand = android.os.Build.BRAND;
            // on LG device with android 4.1.2 crash happend when set
            // SpannableString to actionBar title
            if (deviceBrand.equalsIgnoreCase("lg")
                    && Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
                actionBar.setTitle(currentNews.getSource());
            } else {
                Spannable actionBarTitle = new SpannableString(
                        currentNews.getSource());

                actionBarTitle
                        .setSpan(
                                new ForegroundColorSpan(
                                        curTheme == AllNewsActivity.THEME_WHITE ? NewsCollectionActivity.this.getResources()
                                                .getColor(R.color.white)
                                                : NewsCollectionActivity.this.getResources().getColor(
                                                R.color.txtGrey)), 0,
                                actionBarTitle.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                actionBar.setTitle(actionBarTitle);
            }

            if (currentNews.isNewApp() == 0)

                mmenu.getItem(2).setIcon(getImportantDrawable());
        }
        if (isNewApp) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.mmenu = menu;

        MenuItem textSize = menu.add("Text Size");
        textSize.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (currentNewsFr != null) {
                    currentNewsFr.openTextSizeLayout();
                    // needRefresh = true;
                } else if (curentNewAppFr != null) {
                    curentNewAppFr.openTextSizeLayout();
                }
                return false;
            }
        });
        MenuItemCompat.setShowAsAction(textSize,
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItem m1 = menu.add(getResources()
                .getString(R.string.menu_day_night));
        m1.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                makeToast(NewsCollectionActivity.this.getResources().getString(curTheme == AllNewsActivity.THEME_WHITE ? R.string.theme_night_msg : R.string.theme_day_msg));
                changeTheme = true;
                MyPreferenceManager.setThemeToPref(NewsCollectionActivity.this,
                        curTheme);
                curTheme = MyPreferenceManager
                        .getCurrentTheme(NewsCollectionActivity.this);
                setTheme(menu);
                initView(NewsCollectionActivity.this);
                return false;
            }
        });
        MenuItemCompat
                .setShowAsAction(m1, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        if (currentNews != null && currentNews.isNewApp() == 0) {
            MenuItem m2 = menu
                    .add(getResources().getString(R.string.menu_mark));
            m2.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    markNews();
                    return false;
                }
            });
            MenuItemCompat.setShowAsAction(m2,
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }
        MenuItem m3 = menu.add(getResources().getString(R.string.menu_share));
        m3.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (currentNews != null)
                    DialogManager.openDialogShareNews(
                            NewsCollectionActivity.this, currentNews, false);
                // share(" nameApp", "String imagePath");
                if (currentNewApp != null)
                    DialogManager.openDialogShareNewApp(
                            NewsCollectionActivity.this, currentNewApp, false);
                return false;
            }
        });

        MenuItemCompat
                .setShowAsAction(m3, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        setTheme(menu);
        return true;
    }

    protected void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }

    private void setTheme(Menu mmenu) {

        updateMenu();
        final ActionBar bar = getSupportActionBar();

        if (curTheme == AllNewsActivity.THEME_DARK) {

            if (bar != null) {
                bar.setBackgroundDrawable(new ColorDrawable(NewsCollectionActivity.this.getResources()
                        .getColor(R.color.bgActionBarNight)));
            }

            mmenu.getItem(0).setIcon(R.drawable.font_size_dark);
            mmenu.getItem(1).setIcon(R.drawable.ic_daynight_dark);
            if (currentNews != null && currentNews.isNewApp() == 0) {
                mmenu.getItem(2).setIcon(getImportantDrawable());
                try {
                    mmenu.getItem(3).setIcon(R.drawable.ic_share_dark);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                mmenu.getItem(2).setIcon(R.drawable.ic_share_dark);

        } else {

            if (bar != null) {
                bar.setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.bgActionBarDay)));
            }
            if (isNewApp) {

                if (bar != null) {
                    bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.rateGreen)));
                }
            }
            mmenu.getItem(0).setIcon(R.drawable.font_size_white);
            mmenu.getItem(1).setIcon(R.drawable.ic_daynight);
            if (currentNews != null && currentNews.isNewApp() == 0) {
                mmenu.getItem(2).setIcon(getImportantDrawable());
                try {
                    mmenu.getItem(3).setIcon(R.drawable.ic_share);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                mmenu.getItem(2).setIcon(R.drawable.ic_share);
        }


        // mmenu.getItem(0).setVisible(false);
    }

    private int getImportantDrawable() {
        if (currentNews == null || curTheme == 0)
            return R.drawable.ic_star_unpressed;

        if (currentNews.getMarked() == 1)
            return curTheme == AllNewsActivity.THEME_WHITE ? R.drawable.ic_star_pressed
                    : R.drawable.ic_star_pressed_dark;
        else
            return curTheme == AllNewsActivity.THEME_WHITE ? R.drawable.ic_star_unpressed
                    : R.drawable.ic_star_unpressed_dark;

    }

    protected void markNews() {
        needRefresh = true;
        if (currentNews != null) {
            makeToast(getResources().getString(currentNews.getMarked() == 1 ? R.string.bookmark_msg_remove : R.string.bookmark_msg_add));
            currentNews.setisMarked(currentNews.getMarked() == 1 ? 0 : 1);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Dao<News, Integer> dao = OpenHelperManager.getHelper(
                                NewsCollectionActivity.this,
                                DatabaseHelper.class).getNewsDao();
                        dao.update(currentNews);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();

            updateMenu();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this,
                getResources().getString(R.string.fluri_id));
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
        EasyTracker.getInstance(this).activityStop(this);

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("key", needRefresh);
        intent.putExtra("changeTheme", changeTheme);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}