package com.allNews.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.allNews.activity.AllNewsActivity;
import com.allNews.activity.NewsCollectionActivity;
import com.allNews.data.NewApp.NewApp;
import com.allNews.managers.EWLoader;
import com.allNews.managers.ManagerNewAppNewApi;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.web.Statistic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;


public class NewAppListAdapter extends ArrayAdapter<NewApp> {

    private LayoutInflater inflater;
    private int curTheme;
    private int textSize;

    public NewAppListAdapter(Context context, List<NewApp> objects) {

        super(context, 0, objects);

        inflater = LayoutInflater.from(context);
        textSize = MyPreferenceManager.getTextSize(context);
        curTheme = MyPreferenceManager.getCurrentTheme(context);
    }

    public static void openNewApp(final Context context, final NewApp newApp,
                                  final ArrayList<Integer> listIds, final int position) {
        ((AllNewsActivity) context).showProgressBar();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((AllNewsActivity) context).hideProgressBar();
                        Intent intent = new Intent(context,
                                NewsCollectionActivity.class);
                        intent.putExtra(NewsCollectionActivity.NEW_APP_NODE_ID,
                                newApp.getNodeID());
                        intent.putExtra(
                                NewsCollectionActivity.NEW_APP_LIST_IDS_KEY,
                                listIds);
                        intent.putExtra(
                                NewsCollectionActivity.NEW_APP_POSITION_KEY,
                                position);
                        ((ActionBarActivity) context).startActivityForResult(
                                intent, 1);

                    }

                });

            }

        };
        new Thread(runnable).start();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();

            convertView = inflater.inflate(R.layout.list_item, null);
            holder.ivImage = (ImageView) convertView
                    .findViewById(R.id.listItemImg);
            holder.tvTitle = (TextView) convertView
                    .findViewById(R.id.listItemTitle);
            holder.tvQuantity = (TextView) convertView
                    .findViewById(R.id.listItemQuantity);
            holder.tvSummary = (TextView) convertView.findViewById(R.id.listItemSource);
            holder.isMark = (RatingBar) convertView
                    .findViewById(R.id.listItemMark);
            holder.mainlinearLayout = (LinearLayout) convertView
                    .findViewById(R.id.mainlinearLayout);
            holder.fadelinearLayout = (LinearLayout) convertView
                    .findViewById(R.id.fade_layout);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final NewApp newApp = getItem(position);

        if (getCount() >= position)
            try {

                populate(holder, newApp);

            } catch (Exception e) {
                e.printStackTrace();
                return convertView;
            }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Statistic.sendStatistic(getContext(),
                        Statistic.CATEGORY_NEW_APP, newApp.getTitle(), "", 0l);
                openNewApp(getContext(), newApp, getNewAppNodeID(), position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private ArrayList<Integer> getNewAppNodeID(){
        ArrayList<Integer> list = new ArrayList<>(getCount());
        for (int i = 0; i < getCount(); i++){
            list.add(getItem(i).getNodeID());
        }
        return list;
    }

    private void populate(Holder holder, NewApp newApp)  {

        if (MyPreferenceManager.getViewMode(getContext()) == MyPreferenceManager.VIEW_MODE_1) {

            if (newApp.getImgUrl() != null
                    && Utils.isUrlValid(newApp.getImgUrl())) {
                EWLoader.loadImage(getContext(), newApp.getImgUrl(),
                        holder.ivImage, R.drawable.ic_placeholder);
            }
        } else if (MyPreferenceManager.getViewMode(getContext()) == MyPreferenceManager.VIEW_MODE_2) {
            holder.ivImage.setVisibility(View.GONE);
        }
        holder.tvTitle.setTextSize(textSize + 2);

        holder.tvTitle.setText(newApp.getTitle());
        holder.tvSummary.setTextSize(textSize - 4);

        holder.tvSummary.setText(newApp.getSummary());
        holder.isMark.setVisibility(View.GONE);




        if (curTheme == AllNewsActivity.THEME_WHITE) {
            holder.tvTitle.setTextColor(getContext().getResources().getColor(
                    R.color.newsListTitle));


            holder.mainlinearLayout.setBackgroundColor(getContext().getResources()
                    .getColor(R.color.white));

            return;
        } else {
            holder.tvTitle.setTextColor(getContext().getResources().getColor(
                    R.color.newsListTitleNight));

            holder.mainlinearLayout.setBackgroundColor(getContext().getResources()
                    .getColor(R.color.newsBgNight));

        }

        if (newApp.getIsShown() == 1) {
            holder.fadelinearLayout.setBackgroundColor(getContext().getResources()
                    .getColor(R.color.transparent));
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                animHideOldApi(holder.fadelinearLayout);

            else
                animHide(holder.mainlinearLayout);
            newApp.setIsShown();

            updateNewApps();

        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animHide(LinearLayout fadelinearLayout) {
        int colorFrom = getContext().getResources().getColor(
                R.color.showFirstTimeNews);
        int colorTo = Color.WHITE;
        if (curTheme == AllNewsActivity.THEME_DARK) {
            colorFrom = getContext().getResources().getColor(
                    R.color.showFirstTimeNewsNight);
            colorTo = getContext().getResources().getColor(R.color.newsBgNight);
        }
        ObjectAnimator anim = ObjectAnimator.ofInt(fadelinearLayout,
                "backgroundColor", colorFrom, colorTo);
        anim.setDuration(2000);
        anim.setEvaluator(new ArgbEvaluator());

        anim.start();

    }

    private void animHideOldApi(LinearLayout fadelinearLayout) {

        fadelinearLayout.setBackgroundColor(getContext().getResources().getColor(
                R.color.fadeColor));
        Animation animation = AnimationUtils
                .loadAnimation(getContext(), R.anim.hide);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setStartOffset(1000);
        fadelinearLayout.startAnimation(animation);

    }

    private void updateNewApps() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ManagerNewAppNewApi.removeOrUpdateOldNewApp(getContext());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
    class Holder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvSummary;
        TextView tvDate;
        TextView tvQuantity;
        RatingBar isMark;
        LinearLayout mainlinearLayout;
        LinearLayout fadelinearLayout;
    }

}


//public class NewAppListAdapter extends BaseAdapter {
//
//    private List<NewApp> data;
//    private LayoutInflater inflater;
//    private int curTheme;
//    private int textSize;
//    private Context context;
//    private ArrayList<Integer> shownNewsIdList = new ArrayList<>();
//
//    public NewAppListAdapter(Context context, List<NewApp> d) {
//        if (context == null)
//            return;
//        this.inflater = LayoutInflater.from(context);
//        // this.categoryTag = categoryTag;
//
//        data = d;
//
//        textSize = MyPreferenceManager.getTextSize(context);
//        curTheme = MyPreferenceManager.getCurrentTheme(context);
//        this.context = context;
//
//    }
//
//    public static void openNewApp(final Context context, final NewApp newApp,
//                                  final ArrayList<Integer> listIds, final int position) {
//        ((AllNewsActivity) context).showProgressBar();
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((AllNewsActivity) context).hideProgressBar();
//                        Intent intent = new Intent(context,
//                                NewsCollectionActivity.class);
//                        intent.putExtra(NewsCollectionActivity.NEW_APP_NODE_ID,
//                                newApp.getNodeID());
//                        intent.putExtra(
//                                NewsCollectionActivity.NEW_APP_LIST_IDS_KEY,
//                                listIds);
//                        intent.putExtra(
//                                NewsCollectionActivity.NEW_APP_POSITION_KEY,
//                                position);
//                        ((ActionBarActivity) context).startActivityForResult(
//                                intent, 1);
//
//                    }
//
//                });
//
//            }
//
//        };
//        new Thread(runnable).start();
//
//    }
//
//    @Override
//    public int getCount() {
//        return data.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        Holder holder;
//        if (convertView == null) {
//            holder = new Holder();
//
//            convertView = inflater.inflate(R.layout.list_item, null);
//            holder.ivImage = (ImageView) convertView
//                    .findViewById(R.id.listItemImg);
//            holder.tvTitle = (TextView) convertView
//                    .findViewById(R.id.listItemTitle);
//            holder.tvQuantity = (TextView) convertView
//                    .findViewById(R.id.listItemQuantity);
//            holder.tvSummary = (TextView) convertView.findViewById(R.id.listItemSource);
//            holder.isMark = (RatingBar) convertView
//                    .findViewById(R.id.listItemMark);
//            holder.mainlinearLayout = (LinearLayout) convertView
//                    .findViewById(R.id.mainlinearLayout);
//            holder.fadelinearLayout = (LinearLayout) convertView
//                    .findViewById(R.id.fade_layout);
//
//            convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }
//        final NewApp newApp = this.data.get(position);
//
//        if (context != null && data.size() > position)
//            try {
//
//                populate(holder, newApp);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return convertView;
//            }
//        convertView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                Statistic.sendStatistic(context,
//                        Statistic.CATEGORY_NEW_APP, newApp.getTitle(), "", 0l);
//                openNewApp(context, newApp, Utils.getNewAppNodeID(data), position);
//                notifyDataSetChanged();
//            }
//        });
//        return convertView;
//    }
//
//    private void populate(Holder holder, NewApp newApp)  {
//
//            if (MyPreferenceManager.getViewMode(context) == MyPreferenceManager.VIEW_MODE_1) {
//
//                if (newApp.getImgUrl() != null
//                        && Utils.isUrlValid(newApp.getImgUrl())) {
//                         EWLoader.loadImage(context, newApp.getImgUrl(),
//                                holder.ivImage, R.drawable.ic_placeholder);
//                }
//            } else if (MyPreferenceManager.getViewMode(context) == MyPreferenceManager.VIEW_MODE_2) {
//                holder.ivImage.setVisibility(View.GONE);
//            }
//        holder.tvTitle.setTextSize(textSize + 2);
//
//        holder.tvTitle.setText(newApp.getTitle());
//        holder.tvSummary.setTextSize(textSize - 4);
//
//        holder.tvSummary.setText(newApp.getSummary());
//        holder.isMark.setVisibility(View.GONE);
//
//
//
//
//            if (curTheme == AllNewsActivity.THEME_WHITE) {
//                holder.tvTitle.setTextColor(context.getResources().getColor(
//                        R.color.newsListTitle));
//
//
//                holder.mainlinearLayout.setBackgroundColor(context.getResources()
//                        .getColor(R.color.white));
//
//                return;
//            } else {
//                holder.tvTitle.setTextColor(context.getResources().getColor(
//                        R.color.newsListTitleNight));
//
//                holder.mainlinearLayout.setBackgroundColor(context.getResources()
//                        .getColor(R.color.newsBgNight));
//
//            }
//
//            if (newApp.getIsShown() == 1) {
//                holder.fadelinearLayout.setBackgroundColor(context.getResources()
//                        .getColor(R.color.transparent));
//            } else {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
//                    animHideOldApi(holder.fadelinearLayout);
//
//                else
//                    animHide(holder.mainlinearLayout);
//                newApp.setIsShown();
//
//                updateNewApps();
//
//            }
//        }
//
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void animHide(LinearLayout fadelinearLayout) {
//        int colorFrom = context.getResources().getColor(
//                R.color.showFirstTimeNews);
//        int colorTo = Color.WHITE;
//        if (curTheme == AllNewsActivity.THEME_DARK) {
//            colorFrom = context.getResources().getColor(
//                    R.color.showFirstTimeNewsNight);
//            colorTo = context.getResources().getColor(R.color.newsBgNight);
//        }
//        ObjectAnimator anim = ObjectAnimator.ofInt(fadelinearLayout,
//                "backgroundColor", colorFrom, colorTo);
//        anim.setDuration(2000);
//        anim.setEvaluator(new ArgbEvaluator());
//
//        anim.start();
//
//    }
//
//    private void animHideOldApi(LinearLayout fadelinearLayout) {
//
//        fadelinearLayout.setBackgroundColor(context.getResources().getColor(
//                R.color.fadeColor));
//        Animation animation = AnimationUtils
//                .loadAnimation(context, R.anim.hide);
//        animation.setFillEnabled(true);
//        animation.setFillAfter(true);
//        animation.setStartOffset(1000);
//        fadelinearLayout.startAnimation(animation);
//
//    }
//
//    private void updateNewApps() {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    ManagerNewAppNewApi.removeOrUpdateOldNewApp(context);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
//
//    }
//    class Holder {
//        ImageView ivImage;
//        TextView tvTitle;
//        TextView tvSummary;
//        TextView tvDate;
//        TextView tvQuantity;
//        RatingBar isMark;
//        LinearLayout mainlinearLayout;
//        LinearLayout fadelinearLayout;
//    }
//
//}

