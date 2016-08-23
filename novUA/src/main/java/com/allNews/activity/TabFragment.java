package com.allNews.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.allNews.adapter.HelpAdapter;
import com.allNews.adapter.NewsAdapter;
import com.allNews.adapter.NewAppListAdapter;
import com.allNews.data.NewApp.NewApp;
import com.allNews.data.News;
import com.allNews.db.DBOpenerHelper;
import com.allNews.managers.EWLoader;
import com.allNews.managers.ManagerCategories;
import com.allNews.managers.ManagerNewAppNewApi;
import com.allNews.managers.ManagerNews;
import com.allNews.managers.ManagerSources;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.web.Statistic;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.List;

import gregory.network.rss.R;

import static gregory.network.rss.R.id.frag_container;

public class TabFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, OnScrollListener {
    public static final String TAB = "TAB";
    public static final String TAB_TOP = "TAB_TOP";
    public static final String TAB_ALL = "TAB_ALL";
    public static final String TAB_FAV = "TAB_FAV";
    public static final String TAB_SELECTED_MEDIA = "TAB_SELECTED_MEDIA";
    public static final String TAB_READ_NEWS = "TAB_READ_NEWS";
    public static final String NEW_APP = "NEW_APP";
    public static final String TAB_UNREAD = "TAB_UNREAD";
    public static final String TAB_ARTICLES = "TAB_ARTICLES";

    private String SEARCH_REQUEST = "";

    private ListView newsFeedListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long screenCount = 1;
    private int preLast;
    private TextView txtMsg;
    private int mLastFirstVisibleItem = 0;
    private String tag;
    private boolean isHeaderOn = false;


    private Handler handler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Log.e("onCreateView", getArguments().getString(TAB));
        View rootView = inflater
                .inflate(R.layout.news_tab_content, container, false);
        newsFeedListView = (ListView) rootView.findViewById(R.id.newsListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        txtMsg = (TextView) rootView.findViewById(R.id.txtMsg);


        //TODO
        newsFeedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView listView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    Glide.with(getActivity()).pauseRequests();
                } else {
                    Glide.with(getActivity()).resumeRequests();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        return rootView;
    }


    public String getCustomTag() {
        if (tag != null)
            return tag;
        return getArguments().getString(TAB);
    }

    public void setCustomTag(String tag) {
        this.tag = tag;
    }

    public void refreshTab() {
        updateTab(null);
    }

    @Override
    public void onResume() {

        super.onResume();

        if (newsFeedListView != null) {
            if (newsFeedListView.getAdapter() != null) {

                updateTab(null);
            }
        }
    }

    public void updateTab(String str) {

        int count = 0;
        if (str == null)
            count = newsFeedListView.getFirstVisiblePosition();

        setAdapterInThread(str, count);

    }

    public void goUpToTab() {
        screenCount = 1;
        setAdapterInThread(null, 0);

    }

    public void sendStatistics() {
        String tag = getCustomTag();

        try {
            if (!tag.equals(TAB_ALL)
                    && !tag.equals(TAB_FAV)
                    && !tag.equals(TAB_TOP)
                    && !tag.equals(TAB_SELECTED_MEDIA)
                    && !tag.equals(TAB_READ_NEWS)
                    && !tag.equals(TAB_UNREAD)
                    && !tag.equals(TAB_ARTICLES))
                tag = ManagerCategories.getCategoryById(getActivity(),
                        Integer.parseInt(tag));
        } catch (Exception ignored) {
        }
        Log.e("sendStatistics", tag);
        Statistic.sendStatistic(getActivity(), Statistic.CATEGORY_CATEGORY,
                tag, "", 0l);
    }

    private void setAdapterInThread(final String str, final int count) {

        if(str != null){
            SEARCH_REQUEST = str;
        }

        showProgress();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final List<News> newsList = getNewsList(SEARCH_REQUEST);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            setAdapter(count, newsList);
                            hideProgress();
                        }
                    }
                });
            }

        };
        new Thread(runnable).start();
    }

    private List<News> getNewsList(String str) {

        if (str == null)
            return getNewsList(screenCount);

        if (getCustomTag().equals(TAB_ALL))
            return ManagerNews.getNews(getActivity(), str, false, false, false, false, false, false);

        else if (getCustomTag().equals(TAB_FAV))
            return ManagerNews.getNews(getActivity(), str, true, false, false, false, false, false);

        else if (getCustomTag().equals(TAB_TOP))
            return ManagerNews.getNews(getActivity(), str, false, true, false, false, false, false);
        else if (getCustomTag().equals(TAB_SELECTED_MEDIA))
            return ManagerNews.getNews(getActivity(), str, false, false, false, true, false, false);
        else if (getCustomTag().equals(TAB_READ_NEWS))
            return ManagerNews.getNews(getActivity(), str, false, false, true, false, false, false);
        else if (getCustomTag().equals(TAB_UNREAD))
            return ManagerNews.getNews(getActivity(), str, false, false, false, false, true, false);
        else if (getCustomTag().equals(TAB_ARTICLES))
            return ManagerNews.getNews(getActivity(), str, false, false, false, false, false, true);
        else
            return ManagerNews.getNewsByCategory(getActivity(), str, getCustomTag());
    }

    private List<News> getNewsList(long screenCount) {
        //For debug purpose
//        if(getCustomTag().equals((TAB_UNREAD))){
//            Log.e(TAB_UNREAD, "getCustomTag().equals((TAB_UNREAD)) == true !!!");
//        }

        if (getCustomTag().equals(TAB_TOP))
            return ManagerNews.getTopNews(getActivity());

        else if (getCustomTag().equals(TAB_ALL))
            return ManagerNews.getAllNews(getActivity(), false, false, false, false, false, screenCount);

        else if (getCustomTag().equals(TAB_READ_NEWS))
            return ManagerNews.getAllNews(getActivity(), false, true, false, false, false, screenCount);

        else if (getCustomTag().equals(TAB_FAV))
            return ManagerNews.getAllNews(getActivity(), true, false, false, false, false, screenCount);

        else if (getCustomTag().equals((TAB_SELECTED_MEDIA)))
            return ManagerNews.getAllNews(getActivity(), false, false, true, false, false, screenCount);

        else if (getCustomTag().equals((TAB_UNREAD)))
            return ManagerNews.getAllNews(getActivity(), false, false, false, true, false, screenCount);

        else if (getCustomTag().equals(TAB_ARTICLES))
            return ManagerNews.getAllNews(getActivity(), false, false, false, false, true, screenCount);

        else
            return ManagerNews.getAllNewsByCategory(getActivity(), screenCount, getCustomTag());

    }

    private void setAdapter(final int count, List<News> allNews) {

        txtMsg.setVisibility(View.GONE);

        if (allNews == null || allNews.size() == 0) {

            newsFeedListView.setAdapter(null);
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (Utils.isOnline(getActivity()) && sp.getBoolean(Preferences.PREF_WEATHER, true)) {
            addWeatherHeader();
        }


        if(getCustomTag().equals(TAB_ALL)){
            try {
                //  addHeaderToList(TAB_ALL);
            }catch (NullPointerException npe){
                Log.e(TabFragment.class.getSimpleName(), "addHeaderToList!");
            }
        }

        if(getCustomTag().equals(TAB_UNREAD)){
            //    addHeaderToList(TAB_UNREAD);
        }

        //	txtMsg.setVisibility(View.GONE);
        NewsAdapter newsAdapter = new NewsAdapter(
                getActivity(), allNews);


        if (getActivity().getResources().getBoolean(R.bool.need_hints)) {
            HelpAdapter hintAdapter = new HelpAdapter(getActivity(), newsAdapter,
                    !getCustomTag().equals(TAB_TOP));

            addFooterToTOP();

            newsFeedListView.setAdapter(hintAdapter);
            newsFeedListView.setAdapter(hintAdapter);

            hintAdapter.notifyDataSetChanged();

        } else {

            newsFeedListView.setAdapter(newsAdapter);

            newsAdapter.notifyDataSetChanged();
        }
        newsFeedListView.setSelection(count);
        newsFeedListView.setOnScrollListener(this);
    }

    private void addHeaderToList(String category) {
        String categoryNewsCount = String.valueOf(ManagerNews.getNewsCount(getActivity(), category));
        int headerCount = 100;
        if(category.equals(TAB_ALL)){
            headerCount = 1;
        }else if(category.equals(TAB_UNREAD)){
            headerCount = 0;
        }
        StringBuilder builder = new StringBuilder();
        if(newsFeedListView.getHeaderViewsCount() == headerCount) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = inflater.inflate(R.layout.list_header, null);
            TextView newsCount = (TextView) v.findViewById(R.id.newsCount);
            if(MyPreferenceManager.getCurrentTheme(getActivity()) == 1){
                newsCount.setTextColor(getActivity().getResources().getColor(R.color.black));
            }
            try {
                builder.append(getActivity().getResources().getString(R.string.this_category_news));
                builder.append(" ");
                builder.append(categoryNewsCount);
                newsCount.setText(builder.toString());
                builder.append(" ");
                builder.append(getActivity().getResources().getString(R.string.news));
                newsCount.setText(builder.toString());
                newsFeedListView.addHeaderView(v);
            } catch (NullPointerException e) {
                Log.e(TabFragment.class.getSimpleName(), "addHeaderToList!");
            }
        }else {
            try {
                TextView newsCount = (TextView) getActivity().findViewById(R.id.newsCount);
                if (MyPreferenceManager.getCurrentTheme(getActivity()) == 1) {
                    newsCount.setTextColor(getActivity().getResources().getColor(R.color.black));
                }
                builder.append(getActivity().getResources().getString(R.string.this_category_news));
                builder.append(" ");
                builder.append(categoryNewsCount);
                newsCount.setText(builder.toString());
                builder.append(" ");
                builder.append(getActivity().getResources().getString(R.string.news));
                newsCount.setText(builder.toString());
            }catch (NullPointerException e){
                Log.e(TabFragment.class.getSimpleName(), "addHeaderToList!");
            }
        }
    }

    private void setAdapter() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final List<News> allNews = getNewsList(screenCount);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null && allNews != null) {
                            int count = newsFeedListView
                                    .getFirstVisiblePosition();
                            NewsAdapter newsAdapter = new NewsAdapter(getActivity(), allNews);
                            if (getActivity().getResources().getBoolean(R.bool.need_hints)) {
                                HelpAdapter hintAdapter = new HelpAdapter(getActivity(),
                                        newsAdapter, getCustomTag().equals(
                                        TAB_TOP) ? false : true);

                                newsFeedListView.setAdapter(hintAdapter);

                            } else {
                                if (Utils.isOnline(getActivity())) {

                                    addWeatherHeader();
                                }

                                newsFeedListView.setAdapter(newsAdapter);

                            }

                            newsFeedListView.setSelection(count);
                            if (!getCustomTag().equals(TAB_TOP)
                                    && !getCustomTag().equals(TAB_FAV)
                                    && !getCustomTag().equals(TAB_SELECTED_MEDIA)
                                    && !getCustomTag().equals(TAB_READ_NEWS)) {

//                                long allNewsCount = ManagerNews.getNewsCount(
//                                getActivity(), getCustomTag());
//                                if (allNews.size() == allNewsCount && allNews.size() > 0)
//                                  addFooterToList(allNewsCount);
                            }
                        }
                    }

                });
            }

        };
        new Thread(runnable).start();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        SEARCH_REQUEST = null;
        Statistic.sendStatistic(getActivity(), Statistic.CATEGORY_CLICK,
                Statistic.ACTION_CLICK_BTN_UPDATE, "", 0L);
        ((AllNewsActivity) getActivity()).tryStartUpdate(null);
    }

    private void addFooterToTOP() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (getCustomTag().equals(TAB_TOP)
                        && newsFeedListView.getFooterViewsCount() == 0) {

                    final NewApp nextNewApp = ManagerNewAppNewApi
                            .getNewAppForTOP20Footer(getActivity());

                    if (nextNewApp != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    LayoutInflater inflater = (LayoutInflater) getActivity()
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View v = inflater.inflate(R.layout.top_footer, null);
                                    populate(v, nextNewApp);

                                    if (newsFeedListView.getFooterViewsCount() == 0) {
                                        newsFeedListView.addFooterView(v);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    private void addWeatherHeader() {
        if (getCustomTag().equals(TAB_ALL) && newsFeedListView.getHeaderViewsCount() == 0 &&
                newsFeedListView.getAdapter() == null && getActivity() != null) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.weather_header, null);
            FragmentTransaction ft;
            ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransitionStyle(R.style.CustomTheme);
            WeakReference<WeatherHeaderFragment> weekFrag = new WeakReference<WeatherHeaderFragment>(WeatherHeaderFragment.newInstance());
            WeatherHeaderFragment fragment = weekFrag.get();
            ft.add(frag_container, fragment, "WeatherHeader");
            ft.commitAllowingStateLoss();
            newsFeedListView.addHeaderView(v);
//            v.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity().getApplicationContext(), WeatherActivity.class);
//                    startActivity(intent);
//                }
//            });
        }
    }


    private void populate(final View convertView, final NewApp news) {

        // String allNewsCount = String.valueOf(ManagerNews.getNewsCount(getActivity(), TAB_ALL));

        View newsView = (View) convertView.findViewById(R.id.newApp_item_top20);
        newsView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Statistic.sendStatistic(getActivity(),
                        Statistic.CATEGORY_CLICK,
                        Statistic.ACTION_CLICK_NEWAPP_FROM_TOP20,
                        news.getTitle(), 0L);
                NewAppListAdapter.openNewApp(getActivity(), news, Utils
                        .getNewAppNodeID(ManagerNewAppNewApi.getNewApp(getActivity())), 0);

            }
        });

        TextView appNewsMoreView = (TextView) convertView
                .findViewById(R.id.newApp_item_top20_more);
        appNewsMoreView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Statistic.sendStatistic(getActivity(),
                        Statistic.CATEGORY_CLICK,
                        Statistic.ACTION_CLICK_MORE_NEWAPP_FROM_TOP20, "", 0L);
                ((AllNewsActivity) getActivity()).openNewAppTab();

            }
        });

        TextView newsMoreView = (TextView) convertView
                .findViewById(R.id.top20_more);

        //    newsMoreView.setText(getActivity().getResources().getString(R.string.open_all_news,
        //             allNewsCount));
        newsMoreView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    ((AllNewsActivity) getActivity()).openAllNewsTab();

            }
        });

        int textSize = MyPreferenceManager.getTextSize(getActivity());
        int curTheme = MyPreferenceManager.getCurrentTheme(getActivity());

        ImageView ivImage = (ImageView) convertView
                .findViewById(R.id.listItemImg);
        TextView tvTitle = (TextView) convertView
                .findViewById(R.id.listItemTitle);
        TextView tvDate = (TextView) convertView
                .findViewById(R.id.listItemPubDate);

        TextView tvSummary = (TextView) convertView.findViewById(R.id.listItemSource);

        RatingBar isMark = (RatingBar) convertView
                .findViewById(R.id.listItemMark);
        LinearLayout mainlinearLayout = (LinearLayout) convertView
                .findViewById(R.id.top20_footer_layout);

        LinearLayout fadelinearLayout = (LinearLayout) convertView
                .findViewById(R.id.fade_layout);

        fadelinearLayout.setBackgroundColor(getActivity().getResources().getColor(
                R.color.transparent));


        if (news.getImgUrl() != null
                && Utils.isUrlValid(news.getImgUrl())) {
            EWLoader.loadImage(getActivity(), news.getImgUrl(), ivImage,
                    R.drawable.ic_placeholder);
        } else
            EWLoader.loadImage(getActivity(), "news.getImageUrl()", ivImage,
                    R.drawable.ic_placeholder);
        tvTitle.setTextColor(getActivity().getResources().getColor(
                R.color.txtGrey));
        if (curTheme == AllNewsActivity.THEME_WHITE) {
            // tvTitle.setTextColor(getActivity().getResources().getColor(
            // R.color.newsListTitle));

            newsView.setBackgroundColor(getActivity().getResources().getColor(
                    R.color.transparent));
            mainlinearLayout.setBackgroundColor(getActivity().getResources()
                    .getColor(R.color.transparent));
        } else {
            // tvTitle.setTextColor(getActivity().getResources().getColor(
            // R.color.newsListTitleNight));

            newsView.setBackgroundColor(getActivity().getResources().getColor(
                    R.color.bgActionBarNight));
            mainlinearLayout.setBackgroundColor(getActivity().getResources()
                    .getColor(R.color.bgActionBarNight));
        }

        tvTitle.setTextSize(textSize);
        tvSummary.setTextSize(textSize - 4);

        tvTitle.setText(Html.fromHtml(news.getTitle()));
        tvSummary.setText(news.getSummary());
        // tvSource.setText("news.getSource()");

        tvDate.setText("");
        isMark.setVisibility(View.GONE);

    }


    private void addFooterToList(long count) {
        if (newsFeedListView.getFooterViewsCount() == 0) {
            LayoutInflater inflator = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.list_footer, null);
            TextView txtMsg1 = (TextView) v.findViewById(R.id.footer_txt1);
            TextView txtMsg2 = (TextView) v.findViewById(R.id.footer_txt2);
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            String maxHoursToSaveNews = sp.getString(Preferences.PREF_SAVE,
                    "12");
            String sourcesCount = ManagerSources
                    .getAllSourcesCount(getActivity());
            String checkedSourcesCount = ManagerSources
                    .getCheckedSourcesCount(getActivity());

            txtMsg1.setText(getActivity().getResources().getString(R.string.list_footer_msg1,
                    "" + count, maxHoursToSaveNews, checkedSourcesCount,
                    sourcesCount));
            txtMsg2.setText(getActivity().getResources().getString(R.string.list_footer_msg2));
            txtMsg1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getActivity(),
                            Preferences.class), 1);

                }
            });
            txtMsg2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showTabs();
                    if (getActivity() != null)
                        ((AllNewsActivity) getActivity()).openNewAppTab();

                }
            });
            newsFeedListView.addFooterView(v);
        }
    }

    private void hideTabs() {
        if (getActivity() != null) {
            ActionBar bar = ((AllNewsActivity) getActivity()).getSupportActionBar();

            if (bar != null && bar.isShowing())
                bar.hide();
        }
    }

    private void showTabs() {
        if (getActivity() != null) {
            ActionBar bar = ((AllNewsActivity) getActivity()).getSupportActionBar();
            if (bar != null && !bar.isShowing())
                bar.show();
        }
    }

    private void hideProgress() {
        if (getActivity() != null)
            ((AllNewsActivity) getActivity()).hideProgressBar();
    }

    private void showProgress() {
        if (getActivity() != null)
            ((AllNewsActivity) getActivity()).showProgressBar();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == 0) {
            // Log.e("a", "scrolling stopped...");
            // showTabs();
        }
        if (view.getId() == newsFeedListView.getId()) {
            final int currentFirstVisibleItem = newsFeedListView
                    .getFirstVisiblePosition();
            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                // mIsScrollingUp = false;
                // Log.e("a", "scrolling down...");
                hideTabs();
            } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                // mIsScrollingUp = true;
                // Log.e("a", "scrolling up...");
                showTabs();
            }

            mLastFirstVisibleItem = currentFirstVisibleItem;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;

        if (lastItem == totalItemCount
                && totalItemCount >= DBOpenerHelper.MAX_NEWS_IN_PAGE) {

            if (preLast != lastItem) {
                preLast = lastItem;
                screenCount++;
                setAdapter();

            }
        }

    }

    public void scrollToNext() {
        int currentPosition = newsFeedListView.getFirstVisiblePosition();
        if (currentPosition == newsFeedListView.getCount() - 1)
            return;
        newsFeedListView.setSelection(currentPosition + 1);
        newsFeedListView.clearFocus();
    }

    public void scrollToPrevious() {
        int currentPosition = newsFeedListView.getFirstVisiblePosition();
        if (currentPosition == 0)
            return;
        newsFeedListView.setSelection(currentPosition - 1);
        newsFeedListView.clearFocus();
    }

    @Override
    public void onDestroyView() {
        newsFeedListView.setAdapter(null);
        super.onDestroyView();
    }
}