//package com.allNews.adapter;
//
//import android.animation.ArgbEvaluator;
//import android.animation.ObjectAnimator;
//import android.content.Context;
//import android.graphics.Color;
//import android.support.v4.content.ContextCompat;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//import com.allNews.activity.AllNewsActivity;
//import com.allNews.managers.EWLoader;
//import com.allNews.managers.MyPreferenceManager;
//import com.allNews.model.Article;
//
//import java.util.List;
//
//import gregory.network.rss.R;
//
///**
// * Created by Vladimir on 12.11.2015.
// */
//public class ArticleAdapter extends ArrayAdapter<Article> {
//
//    private int theme;
//    private int textSize;
//
//    public ArticleAdapter(Context context, int resource, List<Article> objects) {
//        super(context, resource, objects);
//
//        resetThemeAndTextSize();
//    }
//
//    public void resetThemeAndTextSize()
//    {
//        theme = MyPreferenceManager.getCurrentTheme(getContext());
//        textSize = MyPreferenceManager.getTextSize(getContext());
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        Article article = getItem(position);
//
//        ViewHolder holder;
//
//        if (!article.isEvent()) {
//            if (convertView == null) {
//                holder = new ViewHolder();
//
//
//                LayoutInflater inflater = LayoutInflater.from(getContext());
//                convertView = inflater.inflate(R.layout.list_item, null);
//
//                holder.iv_image = (ImageView) convertView
//                        .findViewById(R.id.listItemImg);
//                holder.tv_title = (TextView) convertView
//                        .findViewById(R.id.listItemTitle);
//                holder.tvQuantity = (TextView) convertView
//                        .findViewById(R.id.listItemQuantity);
//                holder.tv_source = (TextView) convertView.findViewById(R.id.listItemSource);
//                holder.isMark = (RatingBar) convertView
//                        .findViewById(R.id.listItemMark);
//                holder.mainlinearLayout = (LinearLayout) convertView
//                        .findViewById(R.id.mainlinearLayout);
//                holder.fadelinearLayout = (LinearLayout) convertView
//                        .findViewById(R.id.fade_layout);
//
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            EWLoader.loadImage(getContext(), article.getImageURL(),
//                    holder.iv_image, R.drawable.ic_placeholder);
//
//            holder.tv_title.setTextSize(textSize + 2);
//
//            holder.tv_title.setText(article.getTitle());
//            holder.tv_source.setTextSize(textSize - 4);
//
//            holder.tv_source.setText(AllNewsActivity.getSource(getContext(), article.getSourceID()).getName());
//            holder.isMark.setVisibility(View.GONE);
//
//            if (theme == AllNewsActivity.THEME_WHITE) {
//                holder.tv_title.setTextColor(ContextCompat.getColor(getContext(), R.color.newsListTitle));
//
//                holder.mainlinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
//            } else {
//                holder.tv_title.setTextColor(ContextCompat.getColor(getContext(), R.color.newsListTitleNight));
//
//                holder.mainlinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.newsBgNight));
//            }
//
////            if (article.isShown()) {
////                holder.fadelinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
////            } else {
////                animHide(holder.mainlinearLayout);
////                article.setShown(true);
////
//////                updateNewApps();
////            }
//        }
//
//        return convertView;
//    }
//    private void animHide(LinearLayout fadelinearLayout) {
//        int colorFrom = ContextCompat.getColor(getContext(), R.color.showFirstTimeNews);
//        int colorTo = Color.WHITE;
//        if (theme == AllNewsActivity.THEME_DARK) {
//            colorFrom = ContextCompat.getColor(getContext(), R.color.showFirstTimeNewsNight);
//            colorTo = ContextCompat.getColor(getContext(), R.color.newsBgNight);
//        }
//        ObjectAnimator anim = ObjectAnimator.ofInt(fadelinearLayout,
//                "backgroundColor", colorFrom, colorTo);
//        anim.setDuration(2000);
//        anim.setEvaluator(new ArgbEvaluator());
//
//        anim.start();
//    }
//
//    private final static class ViewHolder {
//        private ImageView iv_image;
//        private TextView tv_title;
//        private TextView tv_source;
//        private TextView tv_date;
//        private TextView tvQuantity;
//        private RatingBar isMark;
//        private LinearLayout mainlinearLayout;
//        private LinearLayout fadelinearLayout;
//    }
//}
