package com.allNews.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allNews.adapter.EventPagerAdapter;
import com.allNews.adapter.NewAppPagerAdapter;
import com.allNews.adapter.NewsTabsCollectionPagerAdapter;
import com.allNews.application.App;
import com.allNews.data.Categories;
import com.allNews.data.CategoriesNewApp;
import com.allNews.managers.AppRater;
import com.allNews.managers.DialogManager;
import com.allNews.managers.Loader;
import com.allNews.managers.ManagerApp;
import com.allNews.managers.ManagerCategories;
import com.allNews.managers.ManagerCategoriesNewApp;
import com.allNews.managers.ManagerEvents;
import com.allNews.managers.ManagerNews;
import com.allNews.managers.ManagerSources;
import com.allNews.managers.ManagerTaxonomyNewApp;
import com.allNews.managers.ManagerUpdateNewApp;
import com.allNews.managers.ManagerUpdates;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.GCMUtils;
import com.allNews.utils.ResourcesImpl;
import com.allNews.utils.Utils;
import com.allNews.view.ProgressView;
import com.allNews.weather.WeatherFragment;
import com.allNews.web.Requests;
import com.allNews.web.Statistic;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.rampo.updatechecker.UpdateChecker;

import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;
import io.presage.Presage;
import io.presage.utils.IADHandler;


public class AllNewsActivity extends ActionBarActivity implements TabListener, WeatherFragment.WeatherEventListener {

    final String LOG_TAG = "LogsRssActivity";
    public final static int THEME_WHITE = 1;
    public final static int THEME_DARK = 2;
    private int curTheme;
    private ProgressBar progressBarUpdate;

    private ViewPager mPager;
    private TextView txtMsg;
    private SharedPreferences sp;
    private Menu mmenu;
    private boolean newAppUpdated = false;
    private boolean searchNow = false;
    private boolean eventUpdated = false;
    private boolean needOneToast = true;
    private boolean onlyRefresh;
    private boolean isScreenLive;
    public boolean nowUpdated = false;
    private TabState currentTabState = TabState.NEWS;
    private ProgressView progressView;
    private DrawerLayout mDrawerLayout;
    private boolean openFav;
    private int searchViewID = 4756;
    RequestQueue requestQueue;
    BillingProcessor bp;
    private boolean openSelectedMedia ;

    private boolean doubleBackToExitPressedOnce;

    @Override
    public void requestCompleted() {

    }

    public enum TabState {

        NEWS, EVENTS, ADS
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyPreferenceManager.getRotatePref(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        Utils.checkSDCard();
        Utils.setLanguage(this);
        setContentView(R.layout.main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // ///////////////////////////////////////
        // Utils.viewHashCode(this, "gregory.network.rss");

        // //////////////////////////////////////////////


        curTheme = MyPreferenceManager.getCurrentTheme(this);

        MyPreferenceManager.setLaunchToday(this);
        MyPreferenceManager.setHintNextPosition(this);


        if (sp.getBoolean(Preferences.PREF_RUN, true)) {

            showProgressBar();
            showMsg(getResources().getString(R.string.msg_updating));

            ManagerApp.tryCopyDb(getApplicationContext());

//			SharedPreferences.Editor editor = sp.edit();
//			editor.putBoolean(Preferences.PREF_RUN, false);
//			editor.commit();
        }

        initView();
        initActionBar();
        //else

        createTabs();

        if (Utils.checkSDCard() && !Utils.isSDFree()) {
            DialogManager.showNoMemmoryDialog(this);
            return;
        } else
            tryStartUpdate(null);


        AppRater.app_launched(this);
        // AppRater.showRateDialog(this, null);
        GCMUtils.itinGCM(this);

        ManagerApp.startPeriodicUpdate(AllNewsActivity.this);
        tryLoadCategoriesAndSources();
        if (MyPreferenceManager.needShowChangeLog(getApplicationContext())) {
            showChangeLog(this);
        }
        initBilling();

        if(!sp.getBoolean(DonateActivity.CHEK_PURCHASE, false) || !sp.getBoolean(Preferences.PREF_PROMO, false) || !getResources().getBoolean(R.bool.sport_news) ) {
            initOguryAds();
        }
        if(getIntent().getAction() != null && getIntent().getAction().equals("OPEN_TAB_TOP")) {
            openTopTab();
        }
        if(getIntent().getAction() != null && getIntent().getAction().equals("openSelectedMedia")){
            openSelectedMedia();
        }
        // Bug in UpdateChecker - sometime app crashed

       /* UpdateChecker  uch  = new UpdateChecker(this);
        uch.setSuccessfulChecksRequired(50);
        uch.setNoticeIcon(R.drawable.ic_launcher);
        uch.start();
*/
    }

    private void initBilling() {
        bp = new BillingProcessor(this, DonateActivity.LICENSE_KEY,
                new BillingProcessor.IBillingHandler() {

                    @Override
                    public void onPurchaseHistoryRestored() {

                    }

                    @Override
                    public void onProductPurchased(String productId,
                                                   TransactionDetails arg1) {


                    }

                    @Override
                    public void onBillingInitialized() {
                        if (Utils.isOnline(App.getContext())) {

                            if (bp != null && bp.loadOwnedPurchasesFromGoogle()) {
                                List<String> subs = bp.listOwnedSubscriptions();
                                if (!subs.isEmpty()) {
                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean(DonateActivity.CHEK_PURCHASE, true);
                                    editor.apply();
                                } else {
                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean(DonateActivity.CHEK_PURCHASE, false);
                                    editor.apply();

                                }
                            }


                        }
                    }

                    @Override
                    public void onBillingError(int errorCode, Throwable arg1) {


                    }
                });



    }

    private void initOguryAds() {
        Presage.getInstance().setContext(this.getBaseContext());
        Presage.getInstance().start();
    }


    private void initView() {
        RelativeLayout main = (RelativeLayout) findViewById(R.id.newsMainLayout);
        progressView = new ProgressView(main);

        mPager = (ViewPager) findViewById(R.id.pager);
        progressBarUpdate = (ProgressBar) findViewById(R.id.progressBar);
        txtMsg = (TextView) findViewById(R.id.txtMsg);

        initDrawer();
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.END);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mDrawerLayout.setDrawerListener(new DrawerListener() {

            @Override
            public void onDrawerStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDrawerSlide(View arg0, float arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDrawerOpened(View arg0) {
                // getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            }

            @Override
            public void onDrawerClosed(View arg0) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setNavigationMode(
                            ActionBar.NAVIGATION_MODE_TABS);
                }

            }
        });

        LinearLayout drawerDivider1 = (LinearLayout) findViewById(R.id.drawerDivider1);
        LinearLayout drawerDivider2 = (LinearLayout) findViewById(R.id.drawerDivider2);
        LinearLayout drawerDivider3 = (LinearLayout) findViewById(R.id.drawerDivider3);
        LinearLayout drawerDivider4 = (LinearLayout) findViewById(R.id.drawerDivider4);
        LinearLayout drawerDivider5 = (LinearLayout) findViewById(R.id.drawerDivider5);
        LinearLayout drawerDivider6 = (LinearLayout) findViewById(R.id.drawerDivider6);
        LinearLayout drawerDivider7 = (LinearLayout) findViewById(R.id.drawerDivider7);
        LinearLayout drawerDivider8 = (LinearLayout) findViewById(R.id.drawerDivider8);
        LinearLayout drawerItemColorStroke = (LinearLayout) findViewById(R.id.drawerItemColorStroke);
        LinearLayout drawerItemViewStroke = (LinearLayout) findViewById(R.id.drawerItemViewModeStroke);
        LinearLayout drawerLayoutBottom = (LinearLayout) findViewById(R.id.drawerLayoutBottom);
        LinearLayout drawerLayoutTop = (LinearLayout) findViewById(R.id.drawerLayoutTop);
        TextView drawerItemDonate = (TextView) findViewById(R.id.drawerItemDonate);
        TextView drawerItemRefresh = (TextView) findViewById(R.id.drawerItemRefresh);
        TextView drawerItemBookmark = (TextView) findViewById(R.id.drawerItemBookmark);
        TextView drawerItemClean = (TextView) findViewById(R.id.drawerItemClean);
        TextView drawerItemSearch = (TextView) findViewById(R.id.drawerItemSearch);
        TextView drawerItemSettings = (TextView) findViewById(R.id.drawerItemSettings);
        TextView drawerItemShare = (TextView) findViewById(R.id.drawerItemShare);
        TextView drawerItemSources = (TextView) findViewById(R.id.drawerItemSources);
        TextView drawerItemColorTxt = (TextView) findViewById(R.id.drawerItemColorTxt);
        TextView drawerItemViewModeTxt = (TextView) findViewById(R.id.drawerItemViewModeTxt);

        final TextView drawerItemColorNight = (TextView) findViewById(R.id.drawerItemColorNight);
        final TextView drawerItemColorDay = (TextView) findViewById(R.id.drawerItemColorDay);
        drawerItemDonate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                // if (!readyToPurchase) {
                // Toast.makeText(getApplicationContext(),"Billing not initialized.",
                // Toast.LENGTH_LONG).show();
                // return;
                // }
                // Statistic.sendStatistic(AllNewsActivity.this,
                // Statistic.CATEGORY_CLICK, Statistic.ACTION_CLICK_BTN_DONATE,
                // "", 0L);
                // bp.subscribe(null, SUBSCRIPTION_ID);
                Intent intent = new Intent(getApplicationContext(),
                        DonateActivity.class);
                startActivity(intent);

            }
        });

        drawerItemRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                Statistic.sendStatistic(AllNewsActivity.this,
                        Statistic.CATEGORY_CLICK,
                        Statistic.ACTION_CLICK_BTN_UPDATE, "", 0L);
                onlyRefresh = false;
                tryStartUpdate(null);
            }
        });

        drawerItemBookmark.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openFav = true;
                mDrawerLayout.closeDrawers();
                // openFavTabs();

            }
        });

        drawerItemClean.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                DialogManager.showDeleteDbDialog(AllNewsActivity.this);
            }
        });

//		drawerItemSearch.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !getResources().getBoolean(R.bool.sport_news)) {

            drawerItemSearch.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mDrawerLayout.closeDrawers();
                    showSearchView();

                }
            });
        } else
            drawerItemSearch.setVisibility(View.GONE);

        drawerItemSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                startActivityForResult(new Intent(AllNewsActivity.this,
                        Preferences.class), 1);
            }
        });

        drawerItemShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                DialogManager.openDialogShareUS(AllNewsActivity.this,
                        Statistic.LABEL_SHARE_FROM_SETTINGS);
            }
        });

        String sourcesCount = ManagerSources
                .getFormattingCheckedSourcesCount(AllNewsActivity.this);

        drawerItemSources.setText(getResources().getString(
                R.string.menu_sources)
                + sourcesCount);
        drawerItemSources.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                startActivityForResult(new Intent(AllNewsActivity.this,
                        Preferences.class).putExtra("sources", true), 1);

            }
        });

        drawerItemColorNight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (curTheme == THEME_DARK)
                    return;
                mDrawerLayout.closeDrawers();

                MyPreferenceManager.setThemeToPref(AllNewsActivity.this,
                        curTheme);
                changeTheme();

            }
        });

        drawerItemColorDay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (curTheme == THEME_WHITE)
                    return;
                mDrawerLayout.closeDrawers();
                MyPreferenceManager.setThemeToPref(AllNewsActivity.this,
                        curTheme);
                changeTheme();

            }
        });

        if (curTheme == THEME_WHITE) {
            drawerDivider1.setBackgroundColor(getResources().getColor(
                    R.color.drawerDivider));
            drawerDivider2.setBackgroundColor(getResources().getColor(
                    R.color.drawerDivider));
            drawerDivider3.setBackgroundColor(getResources().getColor(
                    R.color.drawerDivider));
            drawerDivider4.setBackgroundColor(getResources().getColor(
                    R.color.drawerDivider));
            drawerDivider5.setBackgroundColor(getResources().getColor(
                    R.color.drawerDivider));
            drawerDivider6.setBackgroundColor(getResources().getColor(
                    R.color.drawerDivider));
            drawerDivider7.setBackgroundColor(getResources().getColor(
                    R.color.drawerDivider));
            drawerDivider8.setBackgroundColor(getResources().getColor(
                    R.color.drawerDivider));

            drawerItemColorStroke.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.bg_innews_share_btn_norm));
            drawerItemViewStroke.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.bg_innews_share_btn_norm));
            drawerLayoutBottom.setBackgroundColor(getResources().getColor(
                    R.color.drawerBottomColorDay));
            drawerLayoutTop.setBackgroundColor(getResources().getColor(
                    R.color.drawerTopColorDay));
            drawerItemDonate.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemDonate.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_donate, 0, 0, 0);

            drawerItemRefresh.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemRefresh.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_refresh, 0, 0, 0);

            drawerItemBookmark.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemBookmark.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_bookmark, 0, 0, 0);

            drawerItemClean.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemClean.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_clean, 0, 0, 0);

            drawerItemSearch.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemSearch.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_search, 0, 0, 0);

            drawerItemSettings.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemSettings.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_settings, 0, 0, 0);

            drawerItemShare.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemShare.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_share, 0, 0, 0);

            drawerItemSources.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemSources.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_sources, 0, 0, 0);

            drawerItemColorTxt.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemViewModeTxt.setTextColor(getResources().getColor(
                    R.color.drawerItem));
            drawerItemColorNight.setBackgroundColor(getResources().getColor(
                    R.color.bgActionBarDay));
            drawerItemColorNight.setTextColor(getResources().getColor(
                    R.color.white));

            drawerItemColorDay.setBackgroundColor(getResources().getColor(
                    R.color.white));
            drawerItemColorDay.setTextColor(getResources().getColor(
                    R.color.bgActionBarDay));
        } else {
            drawerDivider1.setBackgroundColor(getResources().getColor(
                    R.color.black));
            drawerDivider2.setBackgroundColor(getResources().getColor(
                    R.color.black));
            drawerDivider3.setBackgroundColor(getResources().getColor(
                    R.color.black));
            drawerDivider4.setBackgroundColor(getResources().getColor(
                    R.color.black));
            drawerDivider5.setBackgroundColor(getResources().getColor(
                    R.color.black));
            drawerDivider6.setBackgroundColor(getResources().getColor(
                    R.color.black));
            drawerDivider7.setBackgroundColor(getResources().getColor(
                    R.color.black));
            drawerDivider8.setBackgroundColor(getResources().getColor(
                    R.color.black));

            drawerItemColorStroke.setBackgroundColor(getResources().getColor(
                    R.color.transparent));
            drawerItemViewStroke.setBackgroundColor(getResources().getColor(
                    R.color.transparent));

            drawerLayoutBottom.setBackgroundColor(getResources().getColor(
                    R.color.drawerBottomColorNight));
            drawerLayoutTop.setBackgroundColor(getResources().getColor(
                    R.color.drawerTopColorNight));

            drawerItemRefresh.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));
            drawerItemRefresh.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_refresh_dark, 0, 0, 0);

            drawerItemBookmark.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));
            drawerItemBookmark.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_bookmark_dark, 0, 0, 0);

            drawerItemClean.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));
            drawerItemClean.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_clean_dark, 0, 0, 0);

            drawerItemSearch.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));
            drawerItemSearch.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_search_dark, 0, 0, 0);

            drawerItemSettings.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));
            drawerItemSettings.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_settings_dark, 0, 0, 0);

            drawerItemShare.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));
            drawerItemShare.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_share_dark, 0, 0, 0);

            drawerItemSources.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));
            drawerItemSources.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_sources_dark, 0, 0, 0);
            drawerItemViewModeTxt.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));
            drawerItemColorTxt.setTextColor(getResources().getColor(
                    R.color.drawerItemNight));

            drawerItemColorNight.setBackgroundColor(getResources().getColor(
                    R.color.drawerColorNightBgNight));
            drawerItemColorNight.setTextColor(getResources().getColor(
                    R.color.drawerColorNightTextColorNight));

            drawerItemColorDay.setBackgroundColor(getResources().getColor(
                    R.color.drawerColorNightBgDay));
            drawerItemColorDay.setTextColor(getResources().getColor(
                    R.color.drawerColorNightTextColorDay));
        }
        initDrawerViewMode();
    }

    private void initDrawerViewMode() {
        ImageView drawerItemViewMode1 = (ImageView) findViewById(R.id.drawerItemViewMode1);
        ImageView drawerItemViewMode2 = (ImageView) findViewById(R.id.drawerItemViewMode2);
        setDrawerViewMode();

        drawerItemViewMode1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MyPreferenceManager.getViewMode(AllNewsActivity.this) == MyPreferenceManager.VIEW_MODE_1)
                    return;
                mDrawerLayout.closeDrawers();
                MyPreferenceManager.setViewMode(AllNewsActivity.this,
                        MyPreferenceManager.VIEW_MODE_1);
                setDrawerViewMode();
                updateNewsTab(null);
            }
        });

        drawerItemViewMode2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MyPreferenceManager.getViewMode(AllNewsActivity.this) == MyPreferenceManager.VIEW_MODE_2)
                    return;
                mDrawerLayout.closeDrawers();
                MyPreferenceManager.setViewMode(AllNewsActivity.this,
                        MyPreferenceManager.VIEW_MODE_2);
                setDrawerViewMode();
                updateNewsTab(null);
            }
        });
    }

    private void setDrawerViewMode() {
        ImageView drawerItemViewMode1 = (ImageView) findViewById(R.id.drawerItemViewMode1);
        ImageView drawerItemViewMode2 = (ImageView) findViewById(R.id.drawerItemViewMode2);
        switch (MyPreferenceManager.getViewMode(this)) {
            case MyPreferenceManager.VIEW_MODE_1:
                if (curTheme == THEME_WHITE) {
                    drawerItemViewMode1.setBackgroundColor(getResources().getColor(
                            R.color.white));

                    drawerItemViewMode2.setBackgroundColor(getResources().getColor(
                            R.color.bgActionBarDay));
                    drawerItemViewMode1.setImageDrawable(getResources()
                            .getDrawable(R.drawable.small_image_view_icon_blue));
                    drawerItemViewMode2.setImageDrawable(getResources()
                            .getDrawable(R.drawable.list_view_white));
                } else {
                    drawerItemViewMode1.setBackgroundColor(getResources().getColor(
                            R.color.drawerColorNightBgNight));
                    drawerItemViewMode2.setBackgroundColor(getResources().getColor(
                            R.color.drawerColorNightBgDay));

                    drawerItemViewMode1.setImageDrawable(getResources()
                            .getDrawable(
                                    R.drawable.small_image_view_icon_night_active));
                    drawerItemViewMode2.setImageDrawable(getResources()
                            .getDrawable(R.drawable.list_view_icon_night_gray));
                }

                break;
            case MyPreferenceManager.VIEW_MODE_2:
                if (curTheme == THEME_WHITE) {
                    drawerItemViewMode1.setBackgroundColor(getResources().getColor(
                            R.color.bgActionBarDay));
                    drawerItemViewMode2.setBackgroundColor(getResources().getColor(
                            R.color.white));
                    drawerItemViewMode1.setImageDrawable(getResources()
                            .getDrawable(R.drawable.small_image_view_icon_white));
                    drawerItemViewMode2.setImageDrawable(getResources()
                            .getDrawable(R.drawable.list_view_blue));

                } else {
                    drawerItemViewMode1.setBackgroundColor(getResources().getColor(
                            R.color.drawerColorNightBgDay));
                    drawerItemViewMode2.setBackgroundColor(getResources().getColor(
                            R.color.drawerColorNightBgNight));
                    drawerItemViewMode1.setImageDrawable(getResources()
                            .getDrawable(
                                    R.drawable.small_image_view_icon_night_gray));
                    drawerItemViewMode2.setImageDrawable(getResources()
                            .getDrawable(R.drawable.list_view_icon_night_active));
                }
                break;
            case MyPreferenceManager.VIEW_MODE_3:

                break;
            default:
                break;
        }
    }

    private void initActionBar() {
        // initMenuDotButton();
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                    curTheme == THEME_DARK ? R.color.bgActionBarNight
                            : R.color.bgActionBarDay)));


            if (!getResources().getBoolean(R.bool.need_event)) {
                Spannable actionBarTitle = new SpannableString(getResources()
                        .getString(R.string.app_full_name));

                actionBarTitle
                        .setSpan(
                                new ForegroundColorSpan(
                                        curTheme == AllNewsActivity.THEME_WHITE ? getResources()
                                                .getColor(R.color.black)
                                                : getResources().getColor(
                                                R.color.txtGrey)), 0,
                                actionBarTitle.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                bar.setTitle(actionBarTitle);
                bar.setDisplayUseLogoEnabled(true);
                return;
            }

            bar.setDisplayShowTitleEnabled(false);

            bar.setIcon(new ColorDrawable(getResources().getColor(
                    android.R.color.transparent)));
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayShowCustomEnabled(true);


            LayoutInflater inflator = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = inflator.inflate(R.layout.action_bar, null);
            bar.setCustomView(v);


	/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            View homeIcon = findViewById(android.R.id.home);
			((View) homeIcon.getParent()).setVisibility(View.GONE);
		}*/

            LinearLayout newsLayout = (LinearLayout) v
                    .findViewById(R.id.barNewsLayout);
            LinearLayout eventsLayout = (LinearLayout) v
                    .findViewById(R.id.barEventsLayout);
            LinearLayout newAppLayout = (LinearLayout) v
                    .findViewById(R.id.barNewAppLayout);
            setTabView();

            eventsLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    openEventTab();

                }
            });

            newsLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    openNewsTab();

                }
            });
            newAppLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    openNewAppTab();

                }
            });
            if (getResources().getBoolean(R.bool.sport_news)){
                newAppLayout.setVisibility(View.GONE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !getResources().getBoolean(R.bool.sport_news))
                addSearchNewsMenu(v);
        }
    }

    public void openNewsTab() {
        currentTabState = TabState.NEWS;
        mDrawerLayout.closeDrawers();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
        hideMsg();
        setTabView();
        createTabs();
        onPrepareOptionsMenu(mmenu);
        initNewsPager();
    }

    public void openEventTab() {
        showActionBar();
        mDrawerLayout.closeDrawers();
        ActionBar bar = getSupportActionBar();

        hideMsg();

        progressView.hideProgress();

        currentTabState = TabState.EVENTS;
        if (bar != null) {
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }

        Statistic.sendStatistic(AllNewsActivity.this,
                Statistic.CATEGORY_EVENTS, Statistic.ACTION_CLICK_ALL_EVENT,
                "", 0l);

        setTabView();
        onPrepareOptionsMenu(mmenu);

        if (!eventUpdated) {

            eventUpdated = true;
            getEvents();
        }
        initEventPager();
    }

    public void openNewAppTab() {
        mDrawerLayout.closeDrawers();
        hideMsg();
        showActionBar();

        // showMsg(getResources().getString(R.string.msg_updating));
        // showProgressBar();

        progressView.hideProgress();
        currentTabState = TabState.ADS;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        Statistic.sendStatistic(AllNewsActivity.this,
                Statistic.CATEGORY_NEW_APP, Statistic.ACTION_CLICK_ALL_NEW_APP,
                "", 0l);

        setTabView();
        if (MyPreferenceManager.isNewAppHasBest(getApplicationContext())) {
            MyPreferenceManager.setNewAppHasBest(this, false);
            showNewAppBuble(false);
        }
        onPrepareOptionsMenu(mmenu);
        // if (!newAppUpdated) {
        // newAppUpdated = true;
        // updateNewApp();
        // }

        createNewAppTabs();
    }

    // private void initMenuDotButton() {
    // final String overflowDesc = getString(R.string.accessibility_overflow);
    // final ViewGroup decor = (ViewGroup) getWindow().getDecorView();
    // decor.postDelayed(new Runnable() {
    //
    // @Override
    // public void run() {
    // final ArrayList<View> outViews = new ArrayList<View>();
    // Utils.findViewsWithText(outViews, decor, overflowDesc);
    //
    // if (outViews.isEmpty()) {
    // return;
    // }
    // final ImageButton overflow = (ImageButton) outViews.get(0);
    // overflow.setImageResource(R.drawable.ic_options_dark);
    //
    // }
    //
    // }, 2000);
    //
    // }

    private void createTabs() {
        initTabs();
        initNewsPager();

    }

    private void createNewAppTabs() {
        initNewAppTabs();
        initNewAppPager();

    }

    private void initNewAppTabs() {
        ActionBar bar = getSupportActionBar();

        if (bar != null) {
            bar.removeAllTabs();
            addTab(bar, NewAppListFragment.TAB_ALL, true);

            List<CategoriesNewApp> catList = ManagerCategoriesNewApp
                    .getAllCategories(this);
            for (CategoriesNewApp category : catList) {
                addTab(bar, category.getName(), false);
            }
        }


    }

    private void initTabs() {
        ActionBar bar = getSupportActionBar();

        if (bar != null) {
            bar.removeAllTabs();
            addTab(bar, getString(R.string.tab_name_top), false);
           // addTab(bar, getString(R.string.tab_name_unread), false);


            addTab(bar, getString(R.string.tab_name_all), true);
            if (!getResources().getBoolean(R.bool.need_event)){
                addTab(bar, getString(R.string.tab_name_articles), false);
            }

        }
        if (NewsFragment.sourcesId != -1) {
            addTab(bar, getString(R.string.tab_name_selected_media), true);
            refreshNewsTab();
         } else {
            // addTab(bar, getString(R.string.tab_name_selected_media), false);
        }


        addTab(bar, getString(R.string.tab_name_read_news), false);
        addTab(bar, getString(R.string.tab_name_fav), false);

        /*  if (!App.getContext().getResources().getBoolean(R.bool.sport_news)) {*/
        List<Categories> catList = ManagerCategories.getAllCategories(this,
                true);

        for (Categories category : catList) {
            addTab(bar, category.getCategoryName(this), false);
        }
        /* }*/
    }

    private void addTab(ActionBar bar, String tabName, boolean setChecked) {
        Tab tab = bar.newTab();

        tab.setTabListener(this);
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(
                R.layout.custom_tab, null);
        TextView t = (TextView) view.findViewById(R.id.custom_tab_text);
        t.setText(tabName);
        if (curTheme == THEME_DARK) {
            t.setTextColor(App.getContext().getResources().getColor(R.color.tabNameNight));
            t.setBackgroundColor(App.getContext().getResources().getColor(R.color.tabBgNight));
        } else {
            // t.setBackgroundColor(getResources().getColor(R.color.white));
        }

        tab.setCustomView(view);

        bar.addTab(tab, setChecked);
    }

    private void initNewAppPager() {

        NewAppPagerAdapter collectionPagerAdapter = new NewAppPagerAdapter(
                getSupportFragmentManager(),
                ManagerCategoriesNewApp.getAllTabTag(this));
        mPager.setAdapter(collectionPagerAdapter);
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ActionBar bar = getSupportActionBar();

                if (bar != null) {
                    if (bar.getTabCount() > position)
                        try {
                            // sendStatistics();
                            // refreshTabView();
                            bar.setSelectedNavigationItem(position);
                            refreshNewApp();
                        } catch (Exception e) {

                        }
                }

            }

        });
    }

    private void initNewsPager() {
        NewsTabsCollectionPagerAdapter collectionPagerAdapter = new NewsTabsCollectionPagerAdapter(
                getSupportFragmentManager(), getAllTabTag());
        mPager.setAdapter(collectionPagerAdapter);
        if(NewsFragment.sourcesId == -1) {
            mPager.setCurrentItem(1);
        }else {
            mPager.setCurrentItem(2);
        }
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ActionBar bar = getSupportActionBar();
                // MyPreferenceManager.setNavigationItemToPrefence(AllNewsActivity.this,
                // position);
                if (bar != null) {
                    if (!bar.isShowing()) {
                        bar.show();
                    }


                    if (bar.getTabCount() > position)
                        try {
                            sendStatistics();
                            bar.setSelectedNavigationItem(position);
                            refreshNewsTab();
                        } catch (Exception e) {
                            // bar.setSelectedNavigationItem(0);
                        }

                }
            }

        });

        if (sp.getBoolean(Preferences.PREF_RUN, true)) {
//			SharedPreferences.Editor editor = sp.edit();
//			editor.putBoolean(Preferences.PREF_RUN, false);
//			editor.commit();
        } else
            showFirstTab();

    }


    private void initEventPager() {

        EventPagerAdapter collectionPagerAdapter = new EventPagerAdapter(
                getSupportFragmentManager());

        mPager.setAdapter(collectionPagerAdapter);

    }

    private void showFirstTab() {
        mPager.post(new Runnable() {

            @Override
            public void run() {
                sendStatistics();
                updateNewsTab(null);

            }
        });

    }

    protected ArrayList<String> getAllTabTag() {
        List<Categories> catList = ManagerCategories.getAllCategories(this,
      		true);
        ArrayList<String> tabTag = new ArrayList<>();
        tabTag.add(TabFragment.TAB_TOP);
        tabTag.add(TabFragment.TAB_ALL);
       // tabTag.add(TabFragment.TAB_UNREAD);

        if (!App.getContext().getResources().getBoolean(R.bool.need_event)){
            tabTag.add(TabFragment.TAB_ARTICLES);
        }
        if (NewsFragment.sourcesId != -1) {
            tabTag.add(TabFragment.TAB_SELECTED_MEDIA);
        }

//        if (App.getContext().getResources().getBoolean(R.bool.sport_news))
//            for (Categories category : catList) {
//            	tabTag.add("" + category.getCategoryID());
//           	}


        tabTag.add(TabFragment.TAB_READ_NEWS);
        tabTag.add(TabFragment.TAB_FAV);

        if (!catList.isEmpty())
            for (Categories category : catList) {
                tabTag.add("" + category.getCategoryID());
            }

        return tabTag;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (currentTabState == TabState.EVENTS)
            initEventsActionBar();
        else if (currentTabState == TabState.ADS) {
            initNewAppActionBar();
        } else
            initNewsActionBar();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.mmenu = menu;
        return true;
    }

    private void initNewAppActionBar() {
        // TODO Auto-generated method stub

    }

    private void initNewsActionBar() {

        MenuItem mDrawer = mmenu.add("Menu");

        mDrawer.setIcon(App.getContext().getResources().getDrawable(R.drawable.ic_drawer));
        mDrawer.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openDrawer();

                return false;
            }
        });
        MenuItemCompat.setShowAsAction(mDrawer,
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        setBg();

    }

    protected void openDrawer() {
        ActionBar bar = getSupportActionBar();
        if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            if (bar != null) {
                bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        } else {
            if (bar != null) {
                bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            }
            mDrawerLayout.closeDrawers();
        }

    }

    private void addSearchNewsMenu(View v) {
        LinearLayout parentLayout = (LinearLayout) v
                .findViewById(R.id.barParentLayout);

        final SearchView searchView = new SearchView(this);

        searchView.setVisibility(View.GONE);

        searchView.setId(searchViewID);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(App.getContext().getResources().getString(
                R.string.action_search_title));

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {

                if (!queryTextFocused) {
                    hideSearchView();
                    searchView.setQuery("", false);
                    updateNewsTab(null);
                }
            }
        });
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {

                Utils.hideVirtualKeyBoard(AllNewsActivity.this);
                updateNewsTab(arg0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {

                return false;
            }
        });

        parentLayout.addView(searchView);

		/*
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, mmenu);

		MenuItem searchItem = mmenu.findItem(R.id.action_search);

		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		searchView.setIconifiedByDefault(false);
	//	searchView.setIconified(false);
		searchView.setQueryHint(getResources().getString(
				R.string.action_search_title));

		searchView.setSubmitButtonEnabled(true);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {

				Utils.hideVirtualKeyBoard(AllNewsActivity.this);
				updateNewsTab(arg0);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {

				return false;
			}
		});
		searchItem.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				Log.e("onMenuItemActionExpand", "onMenuItemActionExpand");
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				updateNewsTab(null);
				return true;
			}
		});

		searchItem.expandActionView();
	//	searchView.performClick();
	//	 ActionBar bar = getSupportActionBar();
	//	 MenuItemCompat.setShowAsAction(searchItem,	 MenuItemCompat.SHOW_AS_ACTION_NEVER);

	 */
    }

    private void showSearchView() {
        ActionBar bar = getSupportActionBar();
        View v = null;
        if (bar != null) {
            v = bar.getCustomView();
        }
        if (v != null) {

            v.findViewById(R.id.barNewsLayout).setVisibility(View.GONE);
            v.findViewById(R.id.barEventsLayout).setVisibility(View.GONE);
            v.findViewById(R.id.barNewAppLayout).setVisibility(View.GONE);
            v.findViewById(R.id.barDividerLine1).setVisibility(View.GONE);
            v.findViewById(R.id.barDividerLine2).setVisibility(View.GONE);

            SearchView searchView = (SearchView) v.findViewById(searchViewID);
            searchView.setVisibility(View.VISIBLE);
            searchView.requestFocusFromTouch();
            Utils.showVirtualKeyBoard(searchView.findFocus(), this);
        }
    }

    private void hideSearchView() {
        ActionBar bar = getSupportActionBar();
        View v = null;
        if (bar != null) {
            v = bar.getCustomView();
        }
        if (v != null) {

            v.findViewById(R.id.barNewsLayout).setVisibility(View.VISIBLE);
            v.findViewById(R.id.barEventsLayout).setVisibility(View.VISIBLE);
            v.findViewById(R.id.barNewAppLayout).setVisibility(View.VISIBLE);
            v.findViewById(R.id.barDividerLine1).setVisibility(View.VISIBLE);
            v.findViewById(R.id.barDividerLine2).setVisibility(View.VISIBLE);
            v.findViewById(searchViewID).setVisibility(View.GONE);
        }
    }

    private void initEventsActionBar() {
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			addSearchEventMenu();*/

        MenuItem m1 = mmenu
                .add(App.getContext().getResources().getString(R.string.event_create));
        m1.setIcon(curTheme == AllNewsActivity.THEME_WHITE ? R.drawable.ic_add_to_calendar
                : R.drawable.ic_add_to_calendar_dark);
        m1.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Statistic.sendStatistic(AllNewsActivity.this,
                        Statistic.CATEGORY_EVENTS,
                        Statistic.ACTION_CLICK_EVENT_CREATE, "", 0L);
                DialogManager.eventCreateDialog(AllNewsActivity.this);
                return false;
            }

        });

        MenuItem m2 = mmenu.add(App.getContext().getResources().getString(R.string.event_help));
        m2.setIcon(R.drawable.ic_help);
        m2.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Statistic.sendStatistic(AllNewsActivity.this,
                        Statistic.CATEGORY_EVENTS,
                        Statistic.ACTION_CLICK_EVENT_HELP, "", 0L);
                DialogManager.eventAboutDialog(AllNewsActivity.this);
                return false;
            }
        });
		/*
		 * MenuItem m3 = mmenu.add(getResources().getString(
		 * R.string.menu_day_night));
		 *
		 * m3.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		 *
		 * @Override public boolean onMenuItemClick(MenuItem item) { preLast =
		 * 0; setThemeToPref(); onPrepareOptionsMenu(mmenu); updateView();
		 * return false; }
		 *
		 * });
		 */

        MenuItem m4 = mmenu.add(App.getContext().getResources().getString(R.string.filter));

        m4.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(AllNewsActivity.this,
                        EventFilterActivity.class);

                startActivityForResult(intent, 2);
                return false;
            }
        });

        MenuItemCompat.setShowAsAction(m1, MenuItemCompat.SHOW_AS_ACTION_NEVER);
        MenuItemCompat.setShowAsAction(m2, MenuItemCompat.SHOW_AS_ACTION_NEVER);
        // MenuItemCompat.setShowAsAction(m3,
        // MenuItemCompat.SHOW_AS_ACTION_NEVER);
        MenuItemCompat.setShowAsAction(m4, MenuItemCompat.SHOW_AS_ACTION_NEVER);
        setBg();
    }


    @SuppressLint("NewApi")
    private void addSearchEventMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, mmenu);
            MenuItem searchItem = mmenu.findItem(R.id.action_search);

            SearchView searchView = (SearchView) MenuItemCompat
                    .getActionView(searchItem);
            searchView.setIconifiedByDefault(true);
            searchView.setSubmitButtonEnabled(true);
            searchView.setQueryHint(App.getContext().getResources().getString(
                    R.string.action_search_title));
            searchView.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String arg0) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String arg0) {

                    searchNow = true;
                    if (arg0.length() > 2)
                        refreshEvents(arg0);
                    else
                        refreshEvents(null);
                    return false;
                }
            });

            searchItem.setOnActionExpandListener(new OnActionExpandListener() {

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {

                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    searchNow = false;
                    return true;
                }
            });

            MenuItemCompat.setShowAsAction(searchItem,
                    MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
    }

    private void setTabView() {
        ActionBar bar = getSupportActionBar();
        View v = null;
        if (bar != null) {
            v = bar.getCustomView();
        }

        TextView txtNews = null;
        if (v != null) {
            txtNews = (TextView) v.findViewById(R.id.barNewsText);

            TextView txtEvents = (TextView) v.findViewById(R.id.barEventsText);
            TextView txtNewApp = (TextView) v.findViewById(R.id.barNewAppText);
            LinearLayout newsMark = (LinearLayout) v
                    .findViewById(R.id.barNewsUnderLine);
            LinearLayout eventsMark = (LinearLayout) v
                    .findViewById(R.id.barEventsUnderLine);
            LinearLayout newAppMark = (LinearLayout) v
                    .findViewById(R.id.barNewAppUnderLine);
            LinearLayout divider1 = (LinearLayout) v
                    .findViewById(R.id.barDividerLine1);
            LinearLayout divider2 = (LinearLayout) v
                    .findViewById(R.id.barDividerLine2);
            if (curTheme == THEME_DARK) {
                divider1.setBackgroundColor(App.getContext().getResources().getColor(
                        R.color.actionBarDividerNight));
                divider2.setBackgroundColor(App.getContext().getResources().getColor(
                        R.color.actionBarDividerNight));
            } else {
            }


            if (MyPreferenceManager.isNewAppHasBest(getApplicationContext())) {

                showNewAppBuble(true);
            }

            int colorWhite = getResources().getColor(R.color.white);
            int colorTransparent = getResources().getColor(R.color.transparent);

            switch (currentTabState) {
                case NEWS:

                    newsMark.setBackgroundColor(colorWhite);
                    eventsMark.setBackgroundColor(colorTransparent);
                    newAppMark.setBackgroundColor(colorTransparent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        txtNews.setAlpha(1f);
                        txtEvents.setAlpha(0.8f);
                        txtNewApp.setAlpha(0.8f);
                    }

                    break;
                case EVENTS:

                    newsMark.setBackgroundColor(colorTransparent);
                    eventsMark.setBackgroundColor(colorWhite);
                    newAppMark.setBackgroundColor(colorTransparent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        txtNews.setAlpha(0.8f);
                        txtEvents.setAlpha(1f);
                        txtNewApp.setAlpha(0.8f);
                    }

                    break;
                case ADS:

                    newsMark.setBackgroundColor(colorTransparent);
                    eventsMark.setBackgroundColor(colorTransparent);
                    newAppMark.setBackgroundColor(colorWhite);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        txtNews.setAlpha(0.8f);
                        txtEvents.setAlpha(0.8f);
                        txtNewApp.setAlpha(1f);
                    }

                    break;
                default:
                    break;
            }

        }
    }

    private void setBg() {
        if (curTheme == THEME_DARK) {
            mPager.setBackgroundColor(getResources().getColor(
                    R.color.newsBgNight));
        } else
            mPager.setBackgroundColor(getResources().getColor(
                    R.color.transparent));
    }

    private void changeTheme() {
        curTheme = MyPreferenceManager.getCurrentTheme(this);
        initDrawer();
        initActionBar();
        initTabs();
        setBg();
        updateNewsTab(null);
    }

    public void tryStartUpdate(final String sourceList) {
        if (onlyRefresh) {
            refreshNewsTab();
            onlyRefresh = false;
            return;
        }
        if (nowUpdated)
            return;
        Utils.isOnline(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case 1:
                        startUpdate(sourceList);
                        break;

                    default:
                        if (isScreenLive)
                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(
                                            R.string.internet_error),
                                    Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });

    }

    public void startUpdate(final String sourceList) {

        showProgressBar();
        showMsg(getResources().getString(R.string.msg_updating));
        final Handler handler = new Handler();

        new Thread(new Runnable() {

            @Override
            public void run() {
                if (mPager != null)
                    MyPreferenceManager.saveCurrentPage(App.getContext(),
                            mPager.getCurrentItem());

                nowUpdated = true;
                ManagerUpdates.getListOfUrl(App.getContext(), sourceList);
                final String urlForQuantity = ManagerUpdates
                        .getUrlForQuantity(App.getContext());
                // Log.e("urlForQuantity", urlForQuantity);
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        getDownloadNewsCount(urlForQuantity);

                    }

                });
            }
        }).start();

    }

    private Handler handlerUpdate = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case Loader.UPDATE_PROGRESS:
                    if (currentTabState == TabState.NEWS) {
                        // if (!getResources().getBoolean(R.bool.auto_refresh))
                        // showRefreshLayout();
                        hideMsg();
                        progressView.addProgress(msg.arg1);
                        if (msg.obj != null && msg.obj.equals(TabFragment.TAB_TOP)) {
                            updateTopTab();

                        } else if (needOneToast && isScreenLive) {
                            needOneToast = false;
                            if (sp.getBoolean(Preferences.PREF_RUN, true)) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean(Preferences.PREF_RUN, false);
                                editor.apply();
                                showFirstTab();
                            }

                            Toast.makeText(App.getContext(),
                                    getResources().getString(R.string.refresh_msg),
                                    Toast.LENGTH_LONG).show();

                        }
                        onlyRefresh = true;
                    }

                    break;

                case Loader.UPDATE_STOP:
                    hideProgressBar();
                    progressView.hideProgress();
                    ManagerNews.deleteOldNews(App.getContext());

                    if (App.getContext().getResources().getBoolean(R.bool.need_event) && !ManagerEvents.isEventExist(App.getContext())) {

                        eventUpdated = true;
                        getEvents();
                    }
                    nowUpdated = false;

                    break;
                default:
                    break;
            }

        }


    };

    private void getDownloadNewsCount(String urlForQuantity) {

        requestQueue = App.getRequestQueue();
        Listener<String> listener = new Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    int count = Integer.parseInt(response);
                    progressView.setMaxProgress(count);
                    if (currentTabState == TabState.NEWS)
                        progressView.showProgress();
                } catch (Exception e) {

                }
                hideMsg();
                new Loader(AllNewsActivity.this, handlerUpdate).Start();

            }
        };
        ErrorListener errorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideMsg();
                new Loader(AllNewsActivity.this, handlerUpdate).Start();

            }
        };
        requestQueue.add(Requests.getStringRequest(urlForQuantity, listener,
                errorListener));

    }

    @Override
    protected void onPause() {
        super.onPause();
        isScreenLive = false;
        if (mPager != null) {
            MyPreferenceManager.saveCurrentPage(this, mPager.getCurrentItem());
        }
        App.activityPaused();

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
        FlurryAgent.onStartSession(this,
                getResources().getString(R.string.fluri_id));

    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ManagerApp.removeNotification(this.getApplicationContext());
        isScreenLive = true;
        GCMUtils.checkPlayServices(this);
        if(!sp.getBoolean(DonateActivity.CHEK_PURCHASE, false) && !sp.getBoolean(Preferences.PREF_PROMO, false)) {
            Presage.getInstance().adToServe("interstitial", new IADHandler() {

                @Override
                public void onAdNotFound() {
                    Log.i("PRESAGE", "ad not found");
                }

                @Override
                public void onAdFound() {
                    Log.i("PRESAGE", "ad found");
                }

                @Override
                public void onAdClosed() {
                    Log.i("PRESAGE", "ad closed");
                }
            });
        }
        App.activityResumed();


    }

    private void showActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null && !bar.isShowing())
            bar.show();

    }

    protected void updateTopTab() {
        TabFragment topTab = getNewsTab(TabFragment.TAB_TOP);
        if (topTab != null)
            topTab.updateTab(null);

    }

    protected TabFragment getNewsTab(String tabTag) {
        try {

            NewsTabsCollectionPagerAdapter adapter = ((NewsTabsCollectionPagerAdapter) mPager
                    .getAdapter());
            for (int i = 0; i < adapter.getCount(); i++) {
                TabFragment tab = (TabFragment) adapter
                        .getRegisteredFragment(i);
                if (tab != null && tab.getCustomTag().equals(tabTag))
                    return tab;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void openAllNewsTab() {
        mPager.setCurrentItem(2);

    }
    private void openTopTab(){
        mPager.setCurrentItem(0);
    }
    private void openSelectedMedia (){
        mPager.setCurrentItem(3);
    }

    protected int getFavTabsIndex() {
        return getAllTabTag().size() - 1;

    }

    protected TabFragment getCurrentNewsTab() {
        try {
            int index = mPager.getCurrentItem();
            NewsTabsCollectionPagerAdapter adapter = ((NewsTabsCollectionPagerAdapter) mPager
                    .getAdapter());
            return (TabFragment) adapter.getRegisteredFragment(index);
        } catch (Exception e) {
            return null;
        }
    }

    protected EventFragment getEventFragment() {
        try {
            int index = mPager.getCurrentItem();
            EventPagerAdapter adapter = ((EventPagerAdapter) mPager
                    .getAdapter());
            return (EventFragment) adapter.getRegisteredFragment(index);
        } catch (Exception e) {
            return null;
        }
    }

    protected NewAppListFragment getNewAppListFragment() {
        try {
            int index = mPager.getCurrentItem();
            NewAppPagerAdapter adapter = ((NewAppPagerAdapter) mPager
                    .getAdapter());
            return (NewAppListFragment) adapter.getRegisteredFragment(index);
        } catch (Exception e) {
            return null;
        }
    }

    protected void updateNewsTab(String str) {

        TabFragment currentTab = getCurrentNewsTab();
        if (currentTab != null)
            currentTab.updateTab(str);
    }

    private void refreshNewsTab() {
        TabFragment currentTab = getCurrentNewsTab();
        if (currentTab != null)
            currentTab.refreshTab();

    }

    protected void refreshNewApp() {
        NewAppListFragment newAppListFragment = getNewAppListFragment();
        if (newAppListFragment != null)
            newAppListFragment.refresh();
    }

    protected void refreshEvents(String str) {
        EventFragment eventFragment = getEventFragment();
        if (eventFragment != null)
            eventFragment.refreshEvents(str);
    }

    protected void refreshEvents(String filterFrom, String filterTo) {
        EventFragment eventFragment = getEventFragment();
        if (eventFragment != null)
            eventFragment.refreshEvents(filterFrom, filterTo);
    }

    public void hideProgressBar() {
        if (progressBarUpdate != null)
            progressBarUpdate.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        if (progressBarUpdate != null)
            progressBarUpdate.setVisibility(View.VISIBLE);

    }

    public void hideMsg() {
        if (txtMsg != null)
            txtMsg.setVisibility(View.GONE);
    }

    public void showMsg(String msg) {
        if (txtMsg != null) {
            txtMsg.setVisibility(View.VISIBLE);
            if (msg != null)
                txtMsg.setText(msg);
        }
    }

    protected void showNewAppBuble(boolean show) {
        ActionBar bar = getSupportActionBar();
        View v = null;
        if (bar != null) {
            v = bar.getCustomView();
            ImageView buble = (ImageView) v.findViewById(R.id.bubleNewApp);
            if (show)
                buble.setVisibility(View.VISIBLE);
            else
                buble.setVisibility(View.GONE);
        }


    }

    private void sendStatistics() {
        TabFragment currentTab = getCurrentNewsTab();
        if (currentTab != null) {
            currentTab.sendStatistics();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1:
                boolean needRefresh = data.getBooleanExtra("key", false);
                boolean needRestart =  data.getBooleanExtra("key2", false);
                boolean needUpdate = data.getBooleanExtra("key3", false);
                boolean needChangeAutoUpdateTime = data.getBooleanExtra("key4",
                        false);
                boolean needChangetab = data.getBooleanExtra("key6", false);
                boolean changeTheme = data.getBooleanExtra("changeTheme", false);
                if (changeTheme && mmenu != null) {
                    changeTheme();
                    // curTheme = sp.getInt(Preferences.PREF_THEME, THEME_WHITE);
                    // onPrepareOptionsMenu(mmenu);
                    // setTheme(); needChangetab
                }
                if (needChangetab) {
                    createTabs();
                }
                if (needChangeAutoUpdateTime)
                    ManagerApp.startPeriodicUpdate(this);
                if (needUpdate) {
                    String sourcesToUpdate = data.getStringExtra("key5");
                    if (!sourcesToUpdate.equals(""))
                        sourcesToUpdate = sourcesToUpdate.substring(0,
                                sourcesToUpdate.length() - 1);
                    onlyRefresh = false;
                    tryStartUpdate(sourcesToUpdate);
                    return;
                }
                if (needRestart) {
                    startActivity(new Intent(this, this.getClass()));
                    finish();
                    return;
                }
                if (needRefresh)
                    updateNewsTab(null);
                break;
            case 2:
                String filterFrom = data.getStringExtra("filterFrom");
                String filterTo = data.getStringExtra("filterTo");

                refreshEvents(filterFrom, filterTo);

                break;
            default:
                break;
        }

    }

    public void tryLoadCategoriesAndSources() {

        Utils.isOnline(getApplicationContext(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        loadCategoriesAndSources();
                        break;

                    default:
                        if (isScreenLive) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(
                                            R.string.internet_error),
                                    Toast.LENGTH_LONG).show();
                            hideMsg();
                            hideProgressBar();
                        }
                        break;
                }

            }
        });

    }

    private void loadCategoriesAndSources() {

        showProgressBar();
        requestQueue = App.getRequestQueue();

        requestQueue.add(ManagerApp.getCategoriesAndSourcesRequest(this,
                new Handler() {
                    @Override
                    public void handleMessage(android.os.Message msg) {

	/*					if (isScreenLive) {
							//				hideProgressBar();
							//				hideMsg();
//							if (sp.getBoolean(Preferences.PREF_RUN, true)) {
//								createTabs();
//								startActivityForResult(
//										new Intent(
//												AllNewsActivity.this,
//												com.allNews.activity.Preferences.class),
//										1);
//							}

						}*/
                        if (!getApplicationContext().getResources().getBoolean(R.bool.sport_news))
                            updateNewApp();
                    }
                }));

        // requestQueue.add(ManagerNewApp.getNewAppRequestTest(this,
        // new Handler() {
        // @Override
        // public void handleMessage(android.os.Message msg) {
        //
        //
        // }
        // }));

    }

    // protected void updateNewApp() {
    // if (ManagerCategoriesNewApp.getAllCategories(this).isEmpty()) {
    // getNewApp();
    // }
    //
    // }

    private void getEvents() {
        showProgressBar();
        requestQueue = App.getRequestQueue();
        requestQueue.add(ManagerEvents.getSourcesRequest(this, new Handler() {
            public void handleMessage(Message msg) {
                if (isScreenLive)
                    switch (msg.what) {
                        case 1:
                            if (!searchNow && currentTabState == TabState.EVENTS) {
                                refreshEvents(null);
                            }
                            hideProgressBar();
                            break;

                        default:
                            break;
                    }
            }

            ;
        }));
    }

    private void updateNewApp() {
        showProgressBar();
        requestQueue = App.getRequestQueue();
        final JsonArrayRequest reqNewApp = ManagerUpdateNewApp
                .getUpdateNewAppRequest(this, new Handler() {
                    public void handleMessage(Message msg) {
                        if (isScreenLive) {
                            if (MyPreferenceManager
                                    .isNewAppHasBest(getApplicationContext())) {
                                Log.e("handleMessage", "handleMessage");
                                showNewAppBuble(true);
                            }
                            if (currentTabState == TabState.ADS) {
                                refreshNewApp();
                            }
                            hideProgressBar();
                        }
                    }


                });

        JsonArrayRequest taxonomyNewAppRequest = ManagerTaxonomyNewApp.getTaxonomyNewAppRequest
                (this, new Handler());

        requestQueue.add(taxonomyNewAppRequest);
        requestQueue.add(reqNewApp);

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction arg1) {
        // mPager.setCurrentItem(tab.getPosition());
        // Log.e("onTabReselected","onTabReselected");
        TabFragment currentTab = getCurrentNewsTab();
        if (currentTab != null)
            currentTab.goUpToTab();
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction arg1) {
        int tabPosition = tab.getPosition();

        if (openFav) {
            tabPosition = getFavTabsIndex();
            // setTabView(tab, false);
            openFav = false;

        } else
            setTabView(tab, true);


        mPager.setCurrentItem(tabPosition);

    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction arg1) {

        setTabView(tab, false);
    }

    private void setTabView(Tab tab, boolean isSelect) {
        View view = tab.getCustomView();
        TextView t = (TextView) view.findViewById(R.id.custom_tab_text);
        ImageView i = (ImageView) view.findViewById(R.id.custom_tab_triangle);
        if (isSelect) {
            if (curTheme == THEME_DARK) {
                i.setImageDrawable(getResources().getDrawable(
                        R.drawable.tab_select_triangle_dark));
                t.setTextColor(getResources().getColor(
                        R.color.tabNameSelectedNight));
            } else
                t.setTextColor(getResources().getColor(R.color.black));
            t.setTypeface(null, Typeface.BOLD);
            i.setVisibility(View.VISIBLE);
        } else {
            if (curTheme == THEME_DARK) {
                t.setTextColor(getResources().getColor(R.color.tabNameNight));
            } else
                t.setTextColor(getResources().getColor(R.color.tabName));
            t.setTypeface(null, Typeface.NORMAL);
            i.setVisibility(View.INVISIBLE);
        }
    }

    ResourcesImpl mResourcesImpl;

    @Override
    public Resources getResources() {

        if (mResourcesImpl == null) {
            mResourcesImpl = new ResourcesImpl(this, super.getResources());
        }
        return mResourcesImpl;
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MENU:
                    if (currentTabState == TabState.NEWS) {
                        openDrawer();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    // Log.e("onKeyDown", "KEYCODE_VOLUME_UP");
                    if (currentTabState == TabState.NEWS) {
                        TabFragment fragment = getCurrentNewsTab();
                        if (fragment != null)
                            fragment.scrollToPrevious();
                    } else if (currentTabState == TabState.EVENTS) {
                        EventFragment fragment = getEventFragment();
                        if (fragment != null)
                            fragment.scrollToPrevious();
                    } else if (currentTabState == TabState.ADS) {
                        NewAppListFragment fragment = getNewAppListFragment();
                        if (fragment != null)
                            fragment.scrollToPrevious();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    // Log.e("onKeyDown", "KEYCODE_VOLUME_DOWN");
                    Fragment fragment = null;

                    if (currentTabState == TabState.NEWS) {
                        fragment = getCurrentNewsTab();
                        if (fragment != null)
                            ((TabFragment) fragment).scrollToNext();
                    } else if (currentTabState == TabState.EVENTS) {
                        fragment = getEventFragment();
                        if (fragment != null)
                            ((EventFragment) fragment).scrollToNext();
                    } else if (currentTabState == TabState.ADS) {
                        fragment = getNewAppListFragment();
                        if (fragment != null)
                            ((NewAppListFragment) fragment).scrollToNext();
                    }

                    return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP
                && (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN || event
                .getKeyCode() == KeyEvent.KEYCODE_MENU)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, this.getResources().getString(R.string.exit_toast), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    @Override
    protected void onDestroy() {

        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    public static void showChangeLog(ActionBarActivity activity) {

        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("changelog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new DialogStandardFragment().show(ft, "changeLog_about");
    }
}