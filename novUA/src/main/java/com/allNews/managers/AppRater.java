package com.allNews.managers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.allNews.application.App;
import com.allNews.web.Statistic;

import gregory.network.rss.R;

public class AppRater {

    private final static String APP_PNAME = App.getContext().getPackageName();
    private final static int DAYS_UNTIL_PROMPT = 14;
    private final static int LAUNCHES_UNTIL_PROMPT = 5;
    private static String APP_TITLE = "";

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        APP_TITLE = mContext.getResources().getString(R.string.app_name);
        // Increment launch counter


        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT && !prefs.getBoolean("later", false)) {
            showRateDialog(mContext, editor);
            if (System.currentTimeMillis() >= date_firstLaunch
                    + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    private static void showRateDialog(final Context mContext,
                                       final SharedPreferences.Editor editor) {

        final Dialog dialog = new Dialog(mContext);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.rate_dialog, null);

        layout.setMinimumWidth((int) (new Rect().width() * 0.8f));

        dialog.setContentView(layout);

        RatingBar ratingBar = (RatingBar) layout.findViewById(R.id.ratingBar1);
        //final Drawable stars =  ratingBar.getProgressDrawable();


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


            }
        });
        //ratingBar.setFocusable(false);
        // stars.setColorFilter(
        //             mContext.getResources().getColor(R.color.rateGreen), PorterDuff.Mode.SRC_IN
        //     );


        dialog.show();

        Button btnRate = (Button) dialog.findViewById(R.id.btn_rate);
        btnRate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }

                Statistic.sendStatistic(mContext, Statistic.CATEGORY_CLICK,
                        Statistic.ACTION_CLICK_BTN_RATE,
                        Statistic.LABEL_RATE_US, 0L);

                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("market://details?id=" + mContext.getPackageName())));
                dialog.dismiss();
            }
        });

        Button btnWish = (Button) dialog.findViewById(R.id.btn_wish);
        btnWish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri
                            .fromParts("mailto",
                                    "yaroslav.maxymovych@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, APP_TITLE);

                    Statistic.sendStatistic(mContext, Statistic.CATEGORY_CLICK,
                            Statistic.ACTION_CLICK_BTN_RATE,
                            Statistic.LABEL_RATE_WISH, 0L);

                    mContext.startActivity(Intent.createChooser(emailIntent,
                            "Send email..."));
                } catch (android.content.ActivityNotFoundException ignored) {
                }

                dialog.dismiss();
            }
        });

        TextView btnRemind = (TextView) dialog.findViewById(R.id.txtRateLater);
        btnRemind.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (editor != null) {
                    editor.putLong("launch_count", 0);
                    editor.putBoolean("later", true);
                    editor.commit();
                }
                Statistic.sendStatistic(mContext, Statistic.CATEGORY_CLICK,
                        Statistic.ACTION_CLICK_BTN_RATE,
                        Statistic.LABEL_RATE_LATER, 0L);

                dialog.dismiss();

            }
        });

        TextView btnCancel = (TextView) dialog.findViewById(R.id.txtRateNo);
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                Statistic.sendStatistic(mContext, Statistic.CATEGORY_CLICK,
                        Statistic.ACTION_CLICK_BTN_RATE,
                        Statistic.LABEL_RATE_NEVER, 0L);

                dialog.dismiss();
            }
        });

    }
}
