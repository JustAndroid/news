package com.allNews.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.allNews.data.Event;
import com.allNews.data.News;
import com.allNews.managers.EWLoader;
import com.allNews.managers.ManagerNews;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.web.Statistic;

import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class NewsAdapter extends ArrayAdapter<News> {

    List<News> listNews;
    List<Event> listEvents;
    
    private final int TYPE_NEWS = 1;
    private final int TYPE_EVENT = 2;

    private LayoutInflater inflater;
    private int curTheme;
    private int textSize;
    private ArrayList<Integer> shownNewsIdList = new ArrayList<>();

    public NewsAdapter(Context context, List<News> news, List<Event> events) {
        super(context, 0, news);

        this.inflater = LayoutInflater.from(getContext());
this.listEvents = events;
        this.listNews = news;

        textSize = MyPreferenceManager.getTextSize(getContext());
        curTheme = MyPreferenceManager.getCurrentTheme(getContext());
    }

    @Override
    public int getItemViewType(int position) {

        if ((position % 20) == 0) {
            return TYPE_EVENT;
        }
        return TYPE_NEWS;
    }

    public static void openNews(final Context context, final News news,
                                final ArrayList<Integer> listIds, final int position) {
        ((AllNewsActivity) context).showProgressBar();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // if(news.isRead()==0)
                // ManagerNews.updateNews(getContext(), "isRead", news.getNewsID());
                // ManagerNews.setNewsToHistory(getContext(), news.getNewsID(), (news
                // .isTop() == 1 || news.isNewApp() == 1) ? true : false);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((AllNewsActivity) context).hideProgressBar();
                        Intent intent = new Intent(context,
                                NewsCollectionActivity.class);
                        intent.putExtra(NewsCollectionActivity.NEWS_ID_KEY,
                                news.getNewsID());
                        intent.putExtra(
                                NewsCollectionActivity.NEWS_LIST_IDS_KEY,
                                listIds);
                        intent.putExtra(
                                NewsCollectionActivity.NEWS_POSITION_KEY,
                                position);
                        // intent.putExtra(NewsCollectionActivity.NEWS_CATEGORY_KEY,
                        // categoryTag);
                        ((ActionBarActivity) context).startActivityForResult(
                                intent, 1);

                    }

                });

            }

        };
        new Thread(runnable).start();

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Log.e("position", ""+position);

       if(getItemViewType(position) == TYPE_NEWS) {
           Holder holder;
           if (convertView == null) {
               holder = new Holder();

               convertView = inflater.inflate(R.layout.list_item, null);
               holder.ivImage = (ImageView) convertView
                       .findViewById(R.id.listItemImg);
               holder.tvTitle = (TextView) convertView
                       .findViewById(R.id.listItemTitle);
               holder.tvDate = (TextView) convertView
                       .findViewById(R.id.listItemPubDate);
               holder.tvSource = (TextView) convertView
                       .findViewById(R.id.listItemSource);
               holder.tvQuantity = (TextView) convertView
                       .findViewById(R.id.listItemQuantity);

               holder.isMark = (RatingBar) convertView
                       .findViewById(R.id.listItemMark);
               holder.mainlinearLayout = (LinearLayout) convertView
                       .findViewById(R.id.mainlinearLayout);
               holder.fadelinearLayout = (LinearLayout) convertView
                       .findViewById(R.id.fade_layout);
               holder.likesLayout = (LinearLayout) convertView.findViewById(R.id.likesLayout);
               holder.likesLayout.setVisibility(View.GONE);
               holder.like = (TextView) convertView.findViewById(R.id.txtPlus);
               holder.dislike = (TextView) convertView.findViewById(R.id.txtMinus);
               holder.plus = (TextView) convertView.findViewById(R.id.plus);
               holder.minus = (TextView) convertView.findViewById(R.id.minus);

               convertView.setTag(holder);

           } else {
               holder = (Holder) convertView.getTag();
               holder.likesLayout.setVisibility(View.GONE);
           }
           final News news = this.getItem(position);

           if (getContext() != null && getCount() > position)
               try {
                   populate(holder, news);
               } catch (Exception e) {
                   return convertView;
               } catch (NoSuchMethodError nsme) {
                   return convertView;
               }

           convertView.setOnClickListener(new OnClickListener() {

               @Override
               public void onClick(View v) {

                   int newsID = news.getNewsID();
                   if (news.isNewApp() == 1)
                       Statistic.sendStatistic(getContext(),
                               Statistic.CATEGORY_NEW_APP, news.getTitle(), "", 0l);
                   else if (news.getIsB2() == 1) {
                       Statistic.sendStatistic(getContext(),
                               Statistic.CATEGORY_PRESS_RELEASE_B2B, "CLICK_FULL_SCREEN", news.getTitle(), 0l);
               /* }else if (ManagerSources.getExcludedSourcesId().contains(news.getsourceID())) {
                    Intent webIntent = new Intent(parent.getgetContext()() , WebViewActivity.class);
                    String url = news.getLink();
                    webIntent.putExtra(WebViewActivity.URL_LINK_KEY, url);
                    webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    parent.getgetContext()().startActivity(webIntent);
                     return;*/

                   } else
                       Statistic.sendStatistic(getContext(), Statistic.CATEGORY_NEWS,
                               "id " + newsID, news.getSource(), 0L);
                   openNews(getContext(), news, Utils.getNewsIds(listNews), position);
                   news.setRead(1);
                   notifyDataSetChanged();
               }
           });
       }
        else{

           Holder holder;
           if (convertView == null) {
               holder = new Holder();

               convertView = inflater.inflate(R.layout.list_item_event, null);
               holder.ivAvatar = (ImageView) convertView
                       .findViewById(R.id.ivAvatar);
               holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
               holder.tvAddress = (TextView) convertView
                       .findViewById(R.id.tvAddress);
               holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
               holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
               holder.isMark = (ImageView) convertView.findViewById(R.id.eventTop);
               convertView.setTag(holder);

           } else {
               holder = (Holder) convertView.getTag();
           }

           populate(holder, this.values.get(position));

       }
        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void populate(Holder holder, News news) {
        if (MyPreferenceManager.getViewMode(getContext()) == MyPreferenceManager.VIEW_MODE_1) {
            news.setImageSmallUrl(getContext());
            if (news.getImageSmallUrl() != null
                    && Utils.isUrlValid(news.getImageSmallUrl())) {
                EWLoader.loadImage(getContext(), news.getImageSmallUrl(),
                        holder.ivImage, R.drawable.ic_placeholder);
            } else
                EWLoader.loadImage(getContext(), "news.getImageUrl()",
                        holder.ivImage, R.drawable.ic_placeholder);
        } else if (MyPreferenceManager.getViewMode(getContext()) == MyPreferenceManager.VIEW_MODE_2) {
            holder.ivImage.setVisibility(View.GONE);
        }

        if (curTheme == AllNewsActivity.THEME_WHITE) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.newsListTitle));
            holder.tvDate.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.newsListTime));
            holder.tvSource.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.newsListTime));
            holder.mainlinearLayout.setBackgroundColor(getContext().getResources()
                    .getColor(R.color.white));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.newsListTitleNight));
            holder.tvDate.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.newsListTimeNight));
            holder.tvSource.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.newsListTimeNight));
            holder.mainlinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.newsBgNight));
        }

        if (news.isShown() == 1) {
            holder.fadelinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.transparent));
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                animHideOldApi(holder.fadelinearLayout);
            else
                animHide(holder.mainlinearLayout);
            if (news.getIsB2() == 1) {
                setShownAndSentStatistic(getContext(), news);

            } else {
                updateNews(news.getNewsID());
            }
        }

        if (news.isRead() == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                holder.tvTitle.setAlpha(0.6f);
                holder.tvDate.setAlpha(0.6f);
                holder.tvSource.setAlpha(0.6f);
                holder.ivImage.setAlpha(0.6f);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                holder.tvTitle.setAlpha(1f);
                holder.tvDate.setAlpha(1f);
                holder.tvSource.setAlpha(1f);
                holder.ivImage.setAlpha(1f);
            } // else

            // holder.mainlinearLayout.setBackgroundColor(getContext()
            // .getResources().getColor(R.color.white));
        }
        holder.likesLayout.setVisibility(View.GONE);
        if (news.getLikesCount() > 0 || news.getDislikesCount() > 0) {
            holder.likesLayout.setVisibility(View.VISIBLE);
            holder.like.setMaxLines(1);
            holder.like.setText("" + news.getLikesCount());
            if (news.getIsLike() == 1) {
                holder.like.setText("" + (news.getLikesCount() + 1));
            }

            holder.dislike.setMaxLines(1);
            holder.dislike.setText("" + news.getDislikesCount());
            if (news.getIsDislike() == 1) {
                holder.dislike.setText("" + (news.getDislikesCount() + 1));
            }

            try {
                holder.dislike.setAlpha(1f);
                holder.like.setAlpha(1f);
                holder.minus.setAlpha(1f);
                holder.plus.setAlpha(1f);
            } catch (NoSuchMethodError nsme) {
                Log.e(NewsAdapter.class.getSimpleName(), nsme.getMessage());
            }

            if (news.getLikesCount() > 0 && news.getDislikesCount() == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    holder.dislike.setAlpha(0.5f);
                    holder.minus.setAlpha(0.5f);
                }
            }
            if (news.getLikesCount() == 0 && news.getDislikesCount() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    holder.plus.setAlpha(0.5f);
                    holder.like.setAlpha(0.5f);
                }
            }
            if (news.getLikesCount() == 0 && news.getDislikesCount() == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    holder.dislike.setAlpha(0.5f);
                    holder.like.setAlpha(0.5f);
                    holder.minus.setAlpha(0.5f);
                    holder.plus.setAlpha(0.5f);
                }
            }
            notifyDataSetChanged();
        }
        // else
        // holder.mainlinearLayout.setBackgroundColor(getContext()
        // .getResources().getColor(R.color.reedNews));


        holder.tvTitle.setTextSize(textSize);
        holder.tvDate.setTextSize(textSize - 4);
        holder.tvSource.setTextSize(textSize - 4);

        holder.tvTitle.setText(Html.fromHtml(news.getTitle()));
        holder.tvSource.setText(news.getSource());
        String time = "";
        StringBuilder builder = new StringBuilder();
        if (/*news.getIsB2() == 0
                &&*/ news.isNewApp() == 0) {

            long days = Utils.getDaysAgo(news.getPubTime());

            if (days > 0)
                builder.append(days).append(getContext().getResources()
                        .getString(R.string.day) + " ");
            long hours = Utils.getHourAgo(news.getPubTime());

            if (hours > 0)
                builder.append(hours).append(getContext().getResources()
                        .getString(R.string.hour) + " ");
            time = builder.toString()
                    + Utils.getMinAgo(news.getPubTime())
                    + getContext().getResources()
                    .getString(R.string.minute);
        }


        holder.tvDate.setText(time);
        if (news.getMarked() == 1)
            holder.isMark.setVisibility(View.VISIBLE);
        else
            holder.isMark.setVisibility(View.GONE);

        // if (news.getQuantity() > 0) {
        // holder.tvQuantity.setVisibility(View.VISIBLE);
        // holder.tvQuantity.setText("" + news.getQuantity());
        // } else
        // holder.tvQuantity.setVisibility(View.GONE);
    }

    private synchronized void setShownAndSentStatistic(final Context context, final News news) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ManagerNews.setShownB2B(getContext(), news.getNewsID());
                Statistic.sendStatistic(getContext(), Statistic.CATEGORY_PRESS_RELEASE_B2B, "was shown in list new statistic", news.getTitle(), 0l);
            }
        }).start();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animHide(LinearLayout fadelinearLayout) {
        int colorFrom = ContextCompat.getColor(getContext(),
                R.color.showFirstTimeNews);
        int colorTo = Color.WHITE;
        if (curTheme == AllNewsActivity.THEME_DARK) {
            colorFrom = ContextCompat.getColor(getContext(),
                    R.color.showFirstTimeNewsNight);
            colorTo = ContextCompat.getColor(getContext(), R.color.newsBgNight);
        }
        ObjectAnimator anim = ObjectAnimator.ofInt(fadelinearLayout,
                "backgroundColor", colorFrom, colorTo);
        anim.setDuration(2000);
        anim.setEvaluator(new ArgbEvaluator());

        anim.start();

    }

    private void animHideOldApi(LinearLayout fadelinearLayout) {

        fadelinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(),
                R.color.fadeColor));
        Animation animation = AnimationUtils
                .loadAnimation(getContext(), R.anim.hide);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setStartOffset(1000);
        fadelinearLayout.startAnimation(animation);

    }

    private void updateNews(final int newsId) {

        shownNewsIdList.add(newsId);
        if (shownNewsIdList.size() >= 5) {
            ArrayList<Integer> shownNewsIdList2 = new ArrayList<>();
            shownNewsIdList2.addAll(shownNewsIdList);
            updateNews(shownNewsIdList2);
            shownNewsIdList.clear();
        }
    }

    private void updateNews(final ArrayList<Integer> shownNewsIdList) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ManagerNews.updateNewsSetShown(getContext(), shownNewsIdList);

            }
        }).start();

    }

    private final class Holder {
        private ImageView ivImage;
        private TextView tvTitle;
        private TextView tvUs;
        private TextView tvUs2;
        private TextView tvSource;
        private TextView tvDate;
        private TextView tvQuantity;
        private RatingBar isMark;
        private LinearLayout mainlinearLayout;
        private LinearLayout fadelinearLayout;
        private LinearLayout likesLayout;
        private TextView like;
        private TextView dislike;
        private TextView plus;
        private TextView minus;
        //event
        ImageView ivAvatar;
        TextView tvName;
        TextView tvAddress;
        TextView tvPrice;



    }
}
//public class NewsAdapter extends BaseAdapter {
//
//    private List<News> data;
//    private LayoutInflater inflater;
//    private int curTheme;
//    private int textSize;
//    private getContext() getContext();
//    private ArrayList<Integer> shownNewsIdList = new ArrayList<>();
//    // private String categoryTag;
//
//    private int newsPosition;
//
//    public NewsAdapter(getContext() getContext(), List<News> d) {
//        if (getContext() == null)
//            return;
//        this.inflater = LayoutInflater.from(getContext());
//        // this.categoryTag = categoryTag;
//
//        data = d;
//
//        textSize = MyPreferenceManager.getTextSize(getContext());
//        curTheme = MyPreferenceManager.getCurrentTheme(getContext());
//        this.getContext() = getContext();
//
//    }
//
//    public static void openNews(final getContext() getContext(), final News news,
//                                final ArrayList<Integer> listIds, final int position) {
//        ((AllNewsActivity) getContext()).showProgressBar();
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                // if(news.isRead()==0)
//                // ManagerNews.updateNews(getContext(), "isRead", news.getNewsID());
//                // ManagerNews.setNewsToHistory(getContext(), news.getNewsID(), (news
//                // .isTop() == 1 || news.isNewApp() == 1) ? true : false);
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((AllNewsActivity) getContext()).hideProgressBar();
//                        Intent intent = new Intent(getContext(),
//                                NewsCollectionActivity.class);
//                        intent.putExtra(NewsCollectionActivity.NEWS_ID_KEY,
//                                news.getNewsID());
//                        intent.putExtra(
//                                NewsCollectionActivity.NEWS_LIST_IDS_KEY,
//                                listIds);
//                        intent.putExtra(
//                                NewsCollectionActivity.NEWS_POSITION_KEY,
//                                position);
//                        // intent.putExtra(NewsCollectionActivity.NEWS_CATEGORY_KEY,
//                        // categoryTag);
//                        ((ActionBarActivity) getContext()).startActivityForResult(
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
//        return getCount();
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
//    public View getView(final int position, View convertView, final ViewGroup parent) {
//        // Log.e("position", ""+position);
//
//        Holder holder;
//        if (convertView == null) {
//            holder = new Holder();
//
//            convertView = inflater.inflate(R.layout.list_item, null);
//            holder.ivImage = (ImageView) convertView
//                    .findViewById(R.id.listItemImg);
//            holder.tvTitle = (TextView) convertView
//                    .findViewById(R.id.listItemTitle);
//            holder.tvDate = (TextView) convertView
//                    .findViewById(R.id.listItemPubDate);
//            holder.tvSource = (TextView) convertView
//                    .findViewById(R.id.listItemSource);
//            holder.tvQuantity = (TextView) convertView
//                    .findViewById(R.id.listItemQuantity);
//
//            holder.isMark = (RatingBar) convertView
//                    .findViewById(R.id.listItemMark);
//            holder.mainlinearLayout = (LinearLayout) convertView
//                    .findViewById(R.id.mainlinearLayout);
//            holder.fadelinearLayout = (LinearLayout) convertView
//                    .findViewById(R.id.fade_layout);
//            holder.likesLayout = (LinearLayout) convertView.findViewById(R.id.likesLayout);
//            holder.likesLayout.setVisibility(View.GONE);
//            holder.like = (TextView) convertView.findViewById(R.id.txtPlus);
//            holder.dislike = (TextView) convertView.findViewById(R.id.txtMinus);
//            holder.plus = (TextView) convertView.findViewById(R.id.plus);
//            holder.minus = (TextView) convertView.findViewById(R.id.minus);
//
//            convertView.setTag(holder);
//
//        } else {
//            holder = (Holder) convertView.getTag();
//            holder.likesLayout.setVisibility(View.GONE);
//        }
//        final News news = this.getItem(position);
//
//        if (getContext() != null && getCount() > position)
//            try {
//                populate(holder, news);
//            } catch (Exception e) {
//                return convertView;
//            } catch (NoSuchMethodError nsme){
//                return convertView;
//            }
//
//        convertView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                int newsID = news.getNewsID();
//                if (news.isNewApp() == 1)
//                    Statistic.sendStatistic(getContext(),
//                            Statistic.CATEGORY_NEW_APP, news.getTitle(), "", 0l);
//                else if (news.getIsB2() == 1){
//                    Statistic.sendStatistic(getContext(),
//                            Statistic.CATEGORY_PRESS_RELEASE_B2B, "CLICK_FULL_SCREEN", news.getTitle(), 0l);
//               /* }else if (ManagerSources.getExcludedSourcesId().contains(news.getsourceID())) {
//                    Intent webIntent = new Intent(parent.getgetContext()() , WebViewActivity.class);
//                    String url = news.getLink();
//                    webIntent.putExtra(WebViewActivity.URL_LINK_KEY, url);
//                    webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    parent.getgetContext()().startActivity(webIntent);
//                     return;*/
//
//                }else
//                    Statistic.sendStatistic(getContext(), Statistic.CATEGORY_NEWS,
//                            "id " + newsID, news.getSource(), 0L);
//                openNews(getContext(), news, Utils.getNewsIds(data), position);
//                news.setRead(1);
//                notifyDataSetChanged();
//            }
//        });
//        return convertView;
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void populate(Holder holder, News news) {
//            if (MyPreferenceManager.getViewMode(getContext()) == MyPreferenceManager.VIEW_MODE_1) {
//                news.setImageSmallUrl(getContext());
//                if (news.getImageSmallUrl() != null
//                        && Utils.isUrlValid(news.getImageSmallUrl())) {
//                    EWLoader.loadImage(getContext(), news.getImageSmallUrl(),
//                            holder.ivImage, R.drawable.ic_placeholder);
//                } else
//                    EWLoader.loadImage(getContext(), "news.getImageUrl()",
//                            holder.ivImage, R.drawable.ic_placeholder);
//            } else if (MyPreferenceManager.getViewMode(getContext()) == MyPreferenceManager.VIEW_MODE_2) {
//                holder.ivImage.setVisibility(View.GONE);
//            }
//
//            if (curTheme == AllNewsActivity.THEME_WHITE) {
//                holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(), 
//                        R.color.newsListTitle));
//                holder.tvDate.setTextColor(ContextCompat.getColor(getContext(), 
//                        R.color.newsListTime));
//                holder.tvSource.setTextColor(ContextCompat.getColor(getContext(), 
//                        R.color.newsListTime));
//                holder.mainlinearLayout.setBackgroundColor(getContext().getResources()
//                        .getColor(R.color.white));
//            } else {
//                holder.tvTitle.setTextColor(ContextCompat.getColor(getContext(), 
//                        R.color.newsListTitleNight));
//                holder.tvDate.setTextColor(ContextCompat.getColor(getContext(), 
//                        R.color.newsListTimeNight));
//                holder.tvSource.setTextColor(ContextCompat.getColor(getContext(), 
//                        R.color.newsListTimeNight));
//                holder.mainlinearLayout.setBackgroundColor(getContext().getResources()
//                        .getColor(R.color.newsBgNight));
//            }
//
//            if (news.isShown() == 1) {
//                holder.fadelinearLayout.setBackgroundColor(getContext().getResources()
//                        .getColor(R.color.transparent));
//            } else {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
//                    animHideOldApi(holder.fadelinearLayout);
//                else
//                    animHide(holder.mainlinearLayout);
//                if (news.getIsB2() == 1) {
//                    setShownAndSentStatistic(getContext(), news);
//
//                } else {
//                    updateNews(news.getNewsID());
//                }
//            }
//
//            if (news.isRead() == 1) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    holder.tvTitle.setAlpha(0.6f);
//                    holder.tvDate.setAlpha(0.6f);
//                    holder.tvSource.setAlpha(0.6f);
//                    holder.ivImage.setAlpha(0.6f);
//                }
//            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    holder.tvTitle.setAlpha(1f);
//                    holder.tvDate.setAlpha(1f);
//                    holder.tvSource.setAlpha(1f);
//                    holder.ivImage.setAlpha(1f);
//                } // else
//
//                // holder.mainlinearLayout.setBackgroundColor(getContext()
//                // .getResources().getColor(R.color.white));
//            }
//            holder.likesLayout.setVisibility(View.GONE);
//            if (news.getLikesCount() > 0 || news.getDislikesCount() > 0 ) {
//                holder.likesLayout.setVisibility(View.VISIBLE);
//                holder.like.setMaxLines(1);
//                holder.like.setText("" + news.getLikesCount());
//                if (news.getIsLike() == 1) {
//                    holder.like.setText("" + (news.getLikesCount() + 1));
//                }
//
//                holder.dislike.setMaxLines(1);
//                holder.dislike.setText("" + news.getDislikesCount());
//                if (news.getIsDislike() == 1) {
//                    holder.dislike.setText("" + (news.getDislikesCount() + 1));
//                }
//
//                try {
//                    holder.dislike.setAlpha(1f);
//                    holder.like.setAlpha(1f);
//                    holder.minus.setAlpha(1f);
//                    holder.plus.setAlpha(1f);
//                } catch (NoSuchMethodError nsme){
//                    Log.e(NewsAdapter.class.getSimpleName(), nsme.getMessage());
//                }
//
//                if (news.getLikesCount() > 0 && news.getDislikesCount() == 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                        holder.dislike.setAlpha(0.5f);
//                        holder.minus.setAlpha(0.5f);
//                    }
//                }
//                if (news.getLikesCount() == 0 && news.getDislikesCount() > 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                        holder.plus.setAlpha(0.5f);
//                        holder.like.setAlpha(0.5f);
//                    }
//                }
//                if (news.getLikesCount() == 0 && news.getDislikesCount() == 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                        holder.dislike.setAlpha(0.5f);
//                        holder.like.setAlpha(0.5f);
//                        holder.minus.setAlpha(0.5f);
//                        holder.plus.setAlpha(0.5f);
//                    }
//                }
//                notifyDataSetChanged();
//            }
//            // else
//            // holder.mainlinearLayout.setBackgroundColor(getContext()
//            // .getResources().getColor(R.color.reedNews));
//
//
//            holder.tvTitle.setTextSize(textSize);
//            holder.tvDate.setTextSize(textSize - 4);
//            holder.tvSource.setTextSize(textSize - 4);
//
//            holder.tvTitle.setText(Html.fromHtml(news.getTitle()));
//            holder.tvSource.setText(news.getSource());
//            String time = "";
//            StringBuilder builder = new StringBuilder();
//            if (/*news.getIsB2() == 0
//                &&*/ news.isNewApp() == 0) {
//
//                long days = Utils.getDaysAgo(news.getPubTime());
//
//                if (days > 0)
//                    builder.append(days).append(inflater.getgetContext()().getResources()
//                            .getString(R.string.day) + " ");
//                long hours = Utils.getHourAgo(news.getPubTime());
//
//                if (hours > 0)
//                    builder.append(hours).append(inflater.getgetContext()().getResources()
//                            .getString(R.string.hour) + " ");
//                time = builder.toString()
//                        + Utils.getMinAgo(news.getPubTime())
//                        + inflater.getgetContext()().getResources()
//                        .getString(R.string.minute);
//            }
//
//
//            holder.tvDate.setText(time);
//            if (news.getMarked() == 1)
//                holder.isMark.setVisibility(View.VISIBLE);
//            else
//                holder.isMark.setVisibility(View.GONE);
//
//            // if (news.getQuantity() > 0) {
//            // holder.tvQuantity.setVisibility(View.VISIBLE);
//            // holder.tvQuantity.setText("" + news.getQuantity());
//            // } else
//            // holder.tvQuantity.setVisibility(View.GONE);
//    }
//
//    private synchronized void setShownAndSentStatistic(final getContext() getContext(), final News news) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ManagerNews.setShownB2B(getContext(), news.getNewsID());
//                Statistic.sendStatistic(getContext(), Statistic.CATEGORY_PRESS_RELEASE_B2B, "was shown in list new statistic", news.getTitle(), 0l );
//            }
//        }).start();
//
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void animHide(LinearLayout fadelinearLayout) {
//        int colorFrom = ContextCompat.getColor(getContext(), 
//                R.color.showFirstTimeNews);
//        int colorTo = Color.WHITE;
//        if (curTheme == AllNewsActivity.THEME_DARK) {
//            colorFrom = ContextCompat.getColor(getContext(), 
//                    R.color.showFirstTimeNewsNight);
//            colorTo = ContextCompat.getColor(getContext(), R.color.newsBgNight);
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
//        fadelinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), 
//                R.color.fadeColor));
//        Animation animation = AnimationUtils
//                .loadAnimation(getContext(), R.anim.hide);
//        animation.setFillEnabled(true);
//        animation.setFillAfter(true);
//        animation.setStartOffset(1000);
//        fadelinearLayout.startAnimation(animation);
//
//    }
//
//    private void updateNews(final int newsId) {
//
//        shownNewsIdList.add(newsId);
//        if (shownNewsIdList.size() >= 5) {
//            ArrayList<Integer> shownNewsIdList2 = new ArrayList<>();
//            shownNewsIdList2.addAll(shownNewsIdList);
//            updateNews(shownNewsIdList2);
//            shownNewsIdList.clear();
//        }
//    }
//
//    private void updateNews(final ArrayList<Integer> shownNewsIdList) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                ManagerNews.updateNewsSetShown(getContext(), shownNewsIdList);
//
//            }
//        }).start();
//
//    }
//
//    class Holder {
//        ImageView ivImage;
//        TextView tvTitle;
//        TextView tvUs;
//        TextView tvUs2;
//        TextView tvSource;
//        TextView tvDate;
//        TextView tvQuantity;
//        RatingBar isMark;
//        LinearLayout mainlinearLayout;
//        LinearLayout fadelinearLayout;
//        LinearLayout likesLayout;
//        TextView like;
//        TextView dislike;
//        TextView plus;
//        TextView minus;
//    }
//}