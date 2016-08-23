package com.allNews.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allNews.activity.AboutScreen;
import com.allNews.activity.AllNewsActivity;
import com.allNews.activity.DonateActivity;
import com.allNews.activity.EventActivity;
import com.allNews.activity.Preferences;
import com.allNews.application.App;
import com.allNews.data.Event;
import com.allNews.managers.DialogManager;
import com.allNews.managers.EWLoader;
import com.allNews.managers.ManagerEvents;
import com.allNews.managers.ManagerSources;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.web.Statistic;
import com.appintop.adbanner.BannerAdContainer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gregory.network.rss.R;

public class HelpAdapter extends BaseAdapter {
    private final Activity activity;
    private final BaseAdapter delegate;
    private boolean isNeed = true;
    private final int k = 100;
    private String[] hints;
    private Context context;
    private View firstPosition;
    private View secondPosition;
    private View thirdPosition;
    private View fourPosition;


    public HelpAdapter(Activity activity, BaseAdapter delegate, boolean isNeed) {

        this.activity = activity;
        this.delegate = delegate;
        this.isNeed = isNeed;

        if (activity == null)
            return;
        this.hints = activity.getResources().getStringArray(R.array.hints);
        delegate.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                notifyDataSetInvalidated();
            }
        });
    }

    public int getCount() {

        return delegate.getCount();
    }

    public Object getItem(int i) {
        return delegate.getItem(i - 1);
    }

    public long getItemId(int i) {
        return delegate.getItemId(i - 1);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int theType = getItemViewType(position);
        //Log.e("theType " + theType, "" + position);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        switch (theType) {
            case 1:
                final BannerAdContainer ad = new BannerAdContainer(activity);
                ad.setOrientation(BannerAdContainer.VERTICAL);
                ad.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

//                final AdView adView = new AdView(activity);
//                adView.setAdUnitId(activity.getResources().getString(R.string.banner_ad_in_list_news_25_positional));
//
//                adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
//                adView.setVisibility(View.GONE);
//                AdRequest request = new AdRequest.Builder()
//                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                        .build();
//                adView.loadAd(request);

                if (isNeed && !MyPreferenceManager.isTodayAppLaunch(activity)) {
                    if (convertView != null) {
                        return convertView;
                    } else {
                        LayoutInflater inflater = LayoutInflater.from(activity);
                        convertView = inflater.inflate(R.layout.us_layout, null);
                        convertView.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Utils.openUsWindow(activity);

                            }
                        });
                        return convertView;

                    }
                } else
                    return delegate.getView(position, convertView, parent);
            case 2:

                convertView = getHintView(position);

                return convertView;
            case 3:
                if (activity.getResources().getBoolean(R.bool.news_ua))
                {
                    if (Utils.isOnline(activity) && !"UA".equals(sp.getString(Preferences.COUNTRY_CODE, "UA"))
                            && !sp.getBoolean(DonateActivity.CHEK_PURCHASE, false) && !sp.getBoolean(Preferences.PREF_PROMO, false))
                    {
                        if (convertView instanceof BannerAdContainer) {
                            return convertView;
                        } else {
                            return getAdView();
                        }
                    }
                    else {
                        return delegate.getView(position, convertView, parent);
                    }
                } else {

                    if (Utils.isOnline(activity) && !sp.getBoolean(DonateActivity.CHEK_PURCHASE, false) && !sp.getBoolean(Preferences.PREF_PROMO, false))
                    {
                        if (convertView instanceof BannerAdContainer) {
                            return convertView;

                        } else {
                            return getAdView();
                        }
                    }
                    else {
                        return delegate.getView(position, convertView, parent);
                    }
                }
            case 4:
                if (getEventView(activity.getApplicationContext(), position) != null) {
                  return getEventView(activity.getApplicationContext(), position);
                } else {
                   return delegate.getView(position, convertView, parent);
                }



            default:
                break;
        }
        return delegate.getView(position, convertView, parent);
    }

    private View getEventView(Context context, int position) {
        this.context = context;
        Event event = null;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String countryCode = sp.getString(Preferences.COUNTRY_CODE, "UA");
        String country = "Украина";
        if (countryCode.equalsIgnoreCase("RU")){
            country = "Россия";
        }

        try {
            event = ManagerEvents.getEventForNewsList(context, position, country);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Holder holder;
        LayoutInflater inflater = LayoutInflater.from(activity);
        View convertView = inflater.inflate(R.layout.list_item_event, null);
        holder = new Holder();
        holder.ivAvatar = (ImageView) convertView
                .findViewById(R.id.ivAvatar);
        holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
        holder.tvAddress = (TextView) convertView
                .findViewById(R.id.tvAddress);
        holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
        holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        holder.isMark = (ImageView) convertView
                .findViewById(R.id.eventTop);
        convertView.setTag(holder);
        final Event finalEvent = event;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EventActivity.class);
                intent.putExtra("event_id", finalEvent.getEventId());
                activity.startActivity(intent);
            }
        });
        if (event != null) {
            populate(holder, event, activity);
            return convertView;
        } else {
            return null;
        }


    }

    private void populate(Holder holder, Event event, Context context) {
        final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm dd MMM");
        LayoutInflater inflater = LayoutInflater.from(activity);
        holder.ivAvatar.setImageResource(R.drawable.ic_placeholder);
        if (event.getLogoSmallUrl() != null
                && Utils.isUrlValid(event.getLogoSmallUrl())) {
            EWLoader.loadAvatarEvent(inflater.getContext(),
                    event.getLogoSmallUrl(), holder.ivAvatar);
        }

        holder.tvName.setText(event.getName(inflater.getContext()));

        if (!event.getAddress().equals("")) {
            holder.tvAddress.setVisibility(View.VISIBLE);
            holder.tvAddress.setText(event.getCity());
        } else
            holder.tvAddress.setVisibility(View.INVISIBLE);

        holder.tvPrice.setVisibility(View.VISIBLE);
        if (event.isFree())
            holder.tvPrice.setText("" + event.getPrice());
        else if (event.getPrice() > 0 && event.getCurrency() != null)
            holder.tvPrice
                    .setText(event.getPrice() + " " + event.getCurrency());
        else
            holder.tvPrice.setVisibility(View.INVISIBLE);


        holder.tvDate.setText(formatDate.format(new Date(
                event.getStartDate() * 1000)));
        if (event.isTop())
            holder.isMark.setVisibility(View.VISIBLE);
        else
            holder.isMark.setVisibility(View.GONE);

        if (MyPreferenceManager.getCurrentTheme(context) == AllNewsActivity.THEME_DARK) {
            holder.tvAddress.setTextColor(inflater.getContext().getResources()
                    .getColor(R.color.white));
            holder.tvName.setTextColor(inflater.getContext().getResources()
                    .getColor(R.color.white));
            holder.tvDate.setTextColor(inflater.getContext().getResources()
                    .getColor(R.color.white));

            holder.tvAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_locationr_dark, 0, 0, 0);
            holder.tvDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_calendar_dark, 0, 0, 0);
            holder.tvPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_ticket_dark, 0, 0, 0);
        }
    }


    private View getHintView(int position) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View convertView = inflater.inflate(R.layout.hint_layout, null);
        TextView hintTextView = (TextView) convertView
                .findViewById(R.id.textViewHint);
        int hintPos = position / k - 1
                + MyPreferenceManager.getHintPosition(activity);
        if (hintPos > hints.length - 1)
            hintPos = hintPos - hints.length;

        if (hintPos == 2) {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(activity);
            String updateTime = ""
                    + (Integer.parseInt(sp.getString(Preferences.PREF_UPDATE,
                    "1800")) / 60);
            hintTextView.setText(activity.getResources().getString(
                    R.string.hint_3, updateTime));
        } else if (hintPos == 5) {
            String sourcesCount = ManagerSources.getAllSourcesCount(activity);
            String checkedSourcesCount = ManagerSources
                    .getCheckedSourcesCount(activity);
            hintTextView.setText(activity.getResources().getString(
                    R.string.hint_6, sourcesCount, checkedSourcesCount));
        } else if (hintPos == 7) {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(activity);
            String maxHoursToSaveNews = sp.getString(Preferences.PREF_SAVE,
                    "12");
            hintTextView.setText(activity.getResources().getString(
                    R.string.hint_8, maxHoursToSaveNews));

        } else if (hintPos < hints.length)
            hintTextView.setText(Html.fromHtml(hints[hintPos]));

        final int hintToOpen = hintPos;
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openFromHints(hintToOpen);

            }
        });
        return convertView;

    }

    protected void openFromHints(int hintToOpen) {

        switch (hintToOpen) {
            case 39:
            case 0:
                DialogManager.openDialogShareUS(activity,
                        Statistic.LABEL_SHARE_FROM_HINT);
                break;
            case 2:
            case 5:
            case 7:
            case 23:
            case 24:
            case 25:
            case 26:
            case 29:
            case 31:
            case 36:
            case 37:
                Utils.openPrefWindow(activity);
                break;
            case 3:
                DialogManager.openDialogFeedBack(activity);
                break;
            case 8:
            case 38:
                ((AllNewsActivity) activity).openEventTab();
                break;
            case 12:

                activity.startActivity(new Intent(activity, AboutScreen.class));
                break;
            case 13:
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(activity.getResources().getString(R.string.app_url))));
                break;
            case 14:
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(activity.getResources().getString(
                                R.string.app_android_6))));
                break;
            case 15:
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(activity.getResources().getString(
                                R.string.app_android_7))));
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(activity.getResources().getString(
                                R.string.app_android_3))));
                break;

            default:
                Utils.openHintsWindow(activity);
                break;
        }

    }

    public BannerAdContainer getAdView() {
        Log.e("AAA", "getAdView");
//        final AdView adView = new AdView(activity);
//        adView.setAdUnitId(activity.getResources().getString(R.string.banner_ad_in_list_news_25_positional));
//
//        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
//        adView.setVisibility(View.GONE);
//        AdRequest request = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build();
//        adView.loadAd(request);
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                adView.setVisibility(View.VISIBLE);
//            }
//
//        });
//        return adView;
        final BannerAdContainer ad = new BannerAdContainer(activity);
        ad.setOrientation(BannerAdContainer.VERTICAL);
        ad.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        return ad;
    }


    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 11)
            return 1;
        else if (position > 0 && (position % k) == 0
                && position < (hints.length * k + 1))
            return 2;
        else if (position % 10 == 0)
            return 3;
        else if (position > 0 && (position % 20) == 0)
            return 4;
        else
            return 0;
        // return position == 2 ? 1 : 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0 && delegate.isEnabled(position - 1);
    }

    class Holder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvAddress;
        TextView tvDate;
        TextView tvPrice;
        ImageView isMark;
    }
}
