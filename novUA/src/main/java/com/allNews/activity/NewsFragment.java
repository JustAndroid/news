package com.allNews.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.allNews.application.App;
import com.allNews.data.News;
import com.allNews.data.Source;
import com.allNews.facebook.Constants.Extra;
import com.allNews.facebook.FacebookActivity;
import com.allNews.managers.DialogManager;
import com.allNews.managers.EWLoader;
import com.allNews.managers.ManagerNews;
import com.allNews.managers.ManagerSources;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.view.NewsContentView;
import com.allNews.view.ResizableImageView;
import com.allNews.web.Statistic;
import com.android.volley.RequestQueue;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

import gregory.network.rss.R;

public class NewsFragment extends Fragment {

    public static final String ARG_OBJECT = "newsId";
    public static int sourcesId = -1;
    public static int openOneSource = 0;
    private static boolean isAdsReadyToShow = false;
    public News selectedNewsItem;
    AdView adViewTop;
    AdView adViewBottom;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    MessageDialog messageDialog;
    private View rootView;
    private ProgressBar progressBarUpdate;
    private View textSizeLayout;
    private Activity mActivity;

    public static void setOpenFullImageListener(ImageView image,
                                                final String imageUrl) {
        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FullImage.class);
                intent.putExtra(FullImage.URL_KEY, imageUrl);
                v.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        //       initUnityAds();


        super.onAttach(activity);


    }

    @Override
    public void onPause() {
        super.onPause();
        if (adViewBottom != null && adViewTop != null) {
            adViewBottom.pause();
            adViewTop.pause();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.news_item_displayer, container,
                false);
        initWidget();
        Long newsID = getArguments().getLong(ARG_OBJECT);
        selectedNewsItem = ManagerNews.getNewsByIdFromDb(getActivity(), newsID);
        if (selectedNewsItem != null) {
            Log.e("selectedNewsItem ", "" + selectedNewsItem.getNewsID());
            if (getActivity() != null & rootView != null)
                setNews();
        } else
            getNewsById(newsID);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // UnityAds.changeActivity(mActivity);
        if (adViewBottom != null && adViewTop != null) {
            adViewTop.resume();
            adViewBottom.resume();
        }


    }

    private void initWidget() {
        final String[] sizeValues = getResources().getStringArray(
                R.array.saveCharValues);
        int textSize = MyPreferenceManager.getTextSize(App.getContext());
        ScrollView fullNewsScrollView = (ScrollView) rootView
                .findViewById(R.id.fullNewsScrollView);
        fullNewsScrollView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Log.e("fullNewsScrollView", "fullNewsScrollView");
                if (textSizeLayout.getVisibility() == View.VISIBLE)
                    textSizeLayout.setVisibility(View.GONE);
                return false;
            }
        });
        progressBarUpdate = (ProgressBar) rootView
                .findViewById(R.id.progressBar);
        textSizeLayout = rootView.findViewById(R.id.textSizeLayout);
        SeekBar textSizeSeekBar = (SeekBar) rootView.findViewById(R.id.textSizeSeekBar);
        textSizeSeekBar.setMax(sizeValues.length - 1);
        for (int i = 0; i < sizeValues.length; i++) {

            if (sizeValues[i].equals("" + textSize)) {
                textSizeSeekBar.setProgress(i);
                break;
            }
        }

        textSizeSeekBar
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        if (getActivity() != null) {
                            ((NewsCollectionActivity) getActivity()).needRefresh = true;
                            MyPreferenceManager.setTextSize(getActivity(),
                                    sizeValues[progress]);
                            setNews();
                        }
                    }
                });
    }

    private void setNews() {

        LinearLayout mainLay = (LinearLayout) rootView
                .findViewById(R.id.fullNewsViewMain);

        adViewTop = (AdView) rootView.findViewById(R.id.adViewNewsTop);
        adViewBottom = (AdView) rootView.findViewById(R.id.adViewNewsBottom);

        TextView txtTitle = (TextView) rootView
                .findViewById(R.id.fullTitleView);
        TextView txtDescription = (TextView) rootView
                .findViewById(R.id.fullDescriptionView);
        TextView txtText = (TextView) rootView
                .findViewById(R.id.fullFullTextView);
        Button sourse = (Button) rootView
                .findViewById(R.id.fullItemSourse2);
        Button btnNewApp = (Button) rootView.findViewById(R.id.new_app_button);
        TextView txtCategory = (TextView) rootView
                .findViewById(R.id.fullItemCategory);
        TextView txtDate = (TextView) rootView
                .findViewById(R.id.fullItemPubDate);
        TextView txtSource = (TextView) rootView
                .findViewById(R.id.fullItemSource);
        LinearLayout shareNewsLay = (LinearLayout) rootView
                .findViewById(R.id.shareNewsLayout);
        LinearLayout shareNewsDivider1 = (LinearLayout) rootView
                .findViewById(R.id.shareNewsDivider1);
        LinearLayout shareNewsDivider2 = (LinearLayout) rootView
                .findViewById(R.id.shareNewsDivider2);
        Button btnshareApp = (Button) rootView.findViewById(R.id.btnShareApp);
        Button btnReadWeb = (Button) rootView.findViewById(R.id.btnReadInWeb);
        Button btnDownloadApp = (Button) rootView
                .findViewById(R.id.btnDownloadApp);
        Button btnshareToAll = (Button) rootView.findViewById(R.id.shareToAll);
        ImageButton shareGmail = (ImageButton) rootView
                .findViewById(R.id.shareGmail);
        ImageButton shareFacebook = (ImageButton) rootView
                .findViewById(R.id.shareFacebook);
        LinearLayout likesLayout = (LinearLayout) rootView.findViewById(R.id.likesInNewsLayout);
        final Button likeButton = (Button) rootView.findViewById(R.id.likeButton);
        final Button disLikeButton = (Button) rootView.findViewById(R.id.disLikeButton);
        TextView subTxt = (TextView) rootView.findViewById(R.id.subscribe_txt);
        Button subBtn = (Button) rootView.findViewById(R.id.btn_subscribe);

        int textSize = MyPreferenceManager.getTextSize(App.getContext());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());

        if (App.getContext().getResources().getBoolean(R.bool.news_ua)) {
            if (!"UA".equals(sp.getString(Preferences.COUNTRY_CODE, "UA"))) {
                if (!sp.getBoolean(DonateActivity.CHEK_PURCHASE, false) && !sp.getBoolean(Preferences.PREF_PROMO, false))
                    showAds();
            }
        } else {
            if (!sp.getBoolean(DonateActivity.CHEK_PURCHASE, false) && !sp.getBoolean(Preferences.PREF_PROMO, false))
                showAds();
        }


        int curTheme = MyPreferenceManager.getCurrentTheme(App.getContext());

        if (curTheme == AllNewsActivity.THEME_DARK) {
            btnReadWeb.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.btn_innews_night_bg));
            btnReadWeb.setTextColor(getResources().getColor(
                    R.color.newsTextNight));
            btnDownloadApp.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.btn_innews_night_bg));
            btnDownloadApp.setTextColor(getResources().getColor(
                    R.color.newsTextNight));
            btnshareApp.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.btn_innews_night_bg));
            btnshareApp.setTextColor(getResources().getColor(
                    R.color.newsTextNight));
            btnshareApp.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_drawer_share_dark, 0, 0, 0);
            btnshareToAll.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_drawer_share_dark, 0);
            btnshareToAll.setTextColor(getResources().getColor(
                    R.color.newsTextNight));
            shareNewsLay.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.bg_innews_share_btn_night_norm));
            txtTitle.setTextColor(getResources()
                    .getColor(R.color.newsTextNight));
            txtDescription.setTextColor(getResources().getColor(
                    R.color.newsTextNight));
            sourse.setTextColor(getResources().getColor(R.color.newsTextNight));
            txtCategory.setTextColor(getResources().getColor(
                    R.color.newsTextNight));
            txtDate.setTextColor(getResources().getColor(R.color.newsTextNight));
            txtSource.setTextColor(getResources().getColor(
                    R.color.newsTextNight));
            mainLay.setBackgroundColor(getResources().getColor(
                    R.color.newsBgNight));
            shareNewsDivider1.setBackgroundColor(getResources().getColor(
                    R.color.shareNewsDividerBgNight));
            shareNewsDivider2.setBackgroundColor(getResources().getColor(
                    R.color.shareNewsDividerBgNight));
            textSizeLayout.setBackgroundColor(getResources().getColor(
                    R.color.tabBgNight));
        }

        txtTitle.setText(Html.fromHtml(selectedNewsItem.getTitle()));
        txtTitle.setTextSize(textSize + 1);

        txtDescription
                .setText(Html.fromHtml(selectedNewsItem.getDescription()));
        txtDescription.setTextSize(textSize);
        String imageUrl = selectedNewsItem.getImageUrl();
        if (imageUrl != null && Utils.isUrlValid(imageUrl)) {
            final ResizableImageView image = (ResizableImageView) rootView
                    .findViewById(R.id.fullItemImg);
            EWLoader.loadImg(getActivity(), imageUrl, image);
            setOpenFullImageListener(image, imageUrl);
        }

        if (selectedNewsItem.getSource() != null) {
            sourse.setVisibility(View.VISIBLE);
            String tmpTxt = getResources().getString(R.string.read_all_from);
            sourse.setText(tmpTxt + selectedNewsItem.getSource());

            sourse.setTextSize(textSize - 4);
            sourse.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sourcesId = selectedNewsItem.getsourceID();
                    openOneSource = 1;
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), AllNewsActivity.class);
                        intent.putExtra("openSelectedMedia", true);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }

        NewsContentView fullTextLayout = (NewsContentView) rootView
                .findViewById(R.id.fullTextLayout);


        String content = selectedNewsItem.getcontent();
        if (!sp.getBoolean(DonateActivity.CHEK_PURCHASE, false) && !sp.getBoolean(Preferences.PREF_PROMO, false)) {
            String contentSubscribe = "";
            if (ManagerSources.getExcludedSourcesId().contains(selectedNewsItem.getsourceID())) {
                if (selectedNewsItem.isNewApp() == 0 && content.length() >= 30) {
                    int dotIndex = content.indexOf(". ", 30) + 1;
                    if (dotIndex < 30)
                        dotIndex = 30;

                    content = content.substring(0, dotIndex) + "...";
                    contentSubscribe = App.getContext().getResources().getString(R.string.excluded_sources_txt);
                    subTxt.setText(contentSubscribe);
                    subTxt.setTextSize(18);
                    subBtn.setVisibility(View.VISIBLE);
                    subBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity().getApplicationContext(),
                                    DonateActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
        if (selectedNewsItem.isNewApp() == 1) {
            fullTextLayout.setIsNewAppMark();
            fullTextLayout.setnewsName(selectedNewsItem.getTitle());
            txtTitle.setVisibility(View.GONE);
            txtDate.setVisibility(View.GONE);


            Document doc = Jsoup.parse(content);
            Element link = doc.select("a").first();
            if (link != null) {
                final String linkHref = link.attr("href");
                btnNewApp.setVisibility(View.VISIBLE);
                if (linkHref != null && Utils.isUrlValid(linkHref)) {
                    btnNewApp.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkHref));
                            startActivity(downloadIntent);
                        }
                    });
                } else {
                    btnNewApp.setVisibility(View.GONE);
                }
            }
        }


        fullTextLayout.setContent(content);


        String date = "";
        try {
            date = new SimpleDateFormat("HH:mm, dd MMMM").format(new Date(
                    selectedNewsItem.getPubTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtDate.setText(date);
        // txtDate.setText(selectedRssItem.getPubDate());
        txtDate.setTextSize(textSize - 4);
        if (selectedNewsItem.getSource() != null) {

            txtSource.setText(selectedNewsItem.getSource());
            txtSource.setTextSize(textSize - 4);

            btnReadWeb.setText(getResources().getString(R.string.readInWeb)
                    + selectedNewsItem.getSource().toUpperCase());
            btnDownloadApp.setText(getResources().getString(
                    R.string.download_app)
                    + "\n" + selectedNewsItem.getSource().toUpperCase());
        }
        if (selectedNewsItem.getLink() == null
                || !Utils.isUrlValid(selectedNewsItem.getLink())
                || selectedNewsItem.isNewApp() == 1)
            btnReadWeb.setVisibility(View.GONE);
        else {
            btnReadWeb.setVisibility(View.VISIBLE);
            /*btnReadWeb.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(selectedNewsItem.getLink()));
					try {
						if (selectedNewsItem.getSource() != null)
							Statistic.sendStatistic(getActivity(),
									Statistic.CATEGORY_CLICK,
									Statistic.ACTION_CLICK_BTN_WEB,
									selectedNewsItem.getSource(), 0L);

						startActivity(browserIntent);
					} catch (Exception ex) {
					}

				}
			});*/
            btnReadWeb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {


                    try {
                        if (selectedNewsItem.getSource() != null)
                            Statistic.sendStatistic(getActivity(),
                                    Statistic.CATEGORY_CLICK,
                                    Statistic.ACTION_CLICK_BTN_WEB,
                                    selectedNewsItem.getSource(), 0L);


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (isAdsReadyToShow) {
                        Toast.makeText(getActivity().getApplicationContext(), "При переходе на сайт, иногда показываеться эта прекрасная реклама", Toast.LENGTH_LONG).show();
                        //    showAds();

                        isAdsReadyToShow = false;

                    } else {
                        if (App.getContext().getResources().getBoolean(R.bool.sport_news)){
                            startBrowser();
                        }else{
                            startInAppBrowser();
                        }
                    }


                }
            });
        }
        final String app = getAppName();

        if (app != null && Utils.isUrlValid(app)) {
            btnDownloadApp.setVisibility(View.VISIBLE);
            btnDownloadApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
                            .parse(app));
                    try {
                        if (selectedNewsItem.getSource() != null)
                            Statistic.sendStatistic(getActivity(),
                                    Statistic.CATEGORY_CLICK,
                                    Statistic.ACTION_CLICK_BTN_DOWNLOAD_APP,
                                    selectedNewsItem.getSource(), 0L);

                        startActivity(browserIntent);
                    } catch (Exception ignored) {
                    }
                }
            });
        }

        if (sp.getBoolean(Preferences.PREF_ADMIN, false)) {
            Button pushBtn = (Button) rootView.findViewById(R.id.sendPush);
            pushBtn.setVisibility(View.VISIBLE);
            pushBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogManager.showSendPushDialog(getActivity(),
                            selectedNewsItem.getNewsID());

                }
            });
        }
        shareNewsLay.setVisibility(View.VISIBLE);
        if (selectedNewsItem.isNewApp() == 0)
            btnshareApp.setVisibility(View.VISIBLE);
        btnshareApp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // if (selectedNewsItem.isNewApp() == 0)
                DialogManager.openDialogShareUS(getActivity(),
                        Statistic.LABEL_SHARE_FROM_NEWS);
                // else
                // DialogManager.openDialogShareNews(getActivity(),
                // selectedNewsItem, false);
            }
        });
        btnshareToAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogManager.openDialogShareNews(getActivity(),
                        selectedNewsItem, false);

            }
        });
        shareGmail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogManager.openDialogShareNews(getActivity(),
                        selectedNewsItem, true);

            }
        });
        shareFacebook.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // startFacebookActivity(selectedNewsItem);
                FacebookSdk.sdkInitialize(getActivity());
                callbackManager = CallbackManager.Factory.create();
                shareDialog = new ShareDialog(getActivity());
                String trimFullText = String.valueOf(Html.fromHtml(selectedNewsItem.getcontent()));
                if (trimFullText.length() >= 100) {
                    int dotIndex = trimFullText.indexOf(". ", 100) + 1;
                    if (dotIndex < 100)
                        dotIndex = 100;

                    trimFullText = trimFullText.substring(0, dotIndex);
                }
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(selectedNewsItem.getTitle() + " " + getResources().getString(R.string.facebook_share_title))
                            .setContentDescription(
                                    trimFullText)
                            .setContentUrl(Uri.parse(selectedNewsItem.getLink()))
                            .build();

                    shareDialog.show(linkContent);
                }


            }
        });
        if (content != null) {
            likesLayout.setVisibility(View.VISIBLE);
            likeButton.setText(Integer.toString(selectedNewsItem.getLikesCount()));
            if (selectedNewsItem.getIsLike() == 1) {
                likeButton.setClickable(false);
                likeButton.setText("" + (selectedNewsItem.getLikesCount() + 1));
            } else {
                likeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeButton.setText("" + (selectedNewsItem.getLikesCount() + 1));
                        likeButton.setClickable(false);
                        Toast.makeText(App.getContext(), App.getContext().getResources().getString(R.string.like_toast_txt), Toast.LENGTH_SHORT).show();
                        NewsCollectionActivity.tryMakeSynch(selectedNewsItem.getNewsID(), true, false);
                        ManagerNews.setNewsLike(App.getContext(), selectedNewsItem.getNewsID(), true);
                    }
                });
            }
            disLikeButton.setText(Integer.toString(selectedNewsItem.getDislikesCount()));
            if (selectedNewsItem.getIsDislike() == 1) {
                disLikeButton.setClickable(false);
                disLikeButton.setText("" + (selectedNewsItem.getDislikesCount() + 1));
            } else {
                disLikeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        disLikeButton.setText("" + (selectedNewsItem.getDislikesCount() + 1));
                        disLikeButton.setClickable(false);
                        Toast.makeText(App.getContext(), App.getContext().getResources().getString(R.string.like_toast_txt), Toast.LENGTH_SHORT).show();
                        NewsCollectionActivity.tryMakeSynch(selectedNewsItem.getNewsID(), false, true);
                        ManagerNews.setNewsLike(App.getContext(), selectedNewsItem.getNewsID(), false);
                    }
                });
            }
        }



      /*  if (selectedNewsItem.getsourceID() == getResources().getInteger(
                R.integer.ad_source_id)) {
            btnshareApp.setVisibility(View.GONE);
            btnDownloadApp.setVisibility(View.GONE);
            btnReadWeb.setVisibility(View.GONE);
        }*/

    }

    private void showAds() {

        AdRequest adRequest = new AdRequest.Builder().build();

        adViewBottom.loadAd(adRequest);
        adViewBottom.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adViewBottom.setVisibility(View.VISIBLE);

            }
        });
        adViewTop.loadAd(adRequest);
        adViewTop.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adViewTop.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startInAppBrowser() {
        if (getActivity() != null) {
            Intent webIntent = new Intent(getActivity(), WebViewActivity.class);
            webIntent.putExtra(WebViewActivity.URL_LINK_KEY, selectedNewsItem.getLink());
            startActivity(webIntent);
        }
    }
    private void startBrowser(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedNewsItem.getLink()));
        startActivity(browserIntent);
    }

    private void startFacebookActivity(News news) {

        Intent intent = new Intent(getActivity(), FacebookActivity.class);
        intent.putExtra(Extra.POST_MESSAGE,
                DialogManager.getTextToShareNews(getActivity(), news));
        intent.putExtra(Extra.POST_LINK, news.getLink());
        intent.putExtra(Extra.POST_LINK_NAME, news.getTitle());
        // intent.putExtra(Extra.POST_LINK_DESCRIPTION, news.getTitle());
        String igmageUrl = getResources().getString(R.string.url_icon);
        if (news.getImageUrl() != null && Utils.isUrlValid(news.getImageUrl()))
            igmageUrl = news.getImageUrl();
        intent.putExtra(Extra.POST_PICTURE, igmageUrl);
        startActivity(intent);
    }

    private String getAppName() {

        try {

            Source source = ManagerSources.getSourceById(App.getContext(), selectedNewsItem.getsourceID());
            return source.getApp();
        } catch (Exception e) {
            return null;
        }

    }

    private void getNewsById(final long newsId) {
        RequestQueue requestQueue = App.getRequestQueue();
        progressBarUpdate.setVisibility(View.VISIBLE);
        String url = App.getContext().getResources().getString(R.string.url_base)
                + App.getContext().getResources().getString(
                R.string.url_get_news_by_id) + newsId;

        requestQueue.add(ManagerNews.getNewsRequest(url, App.getContext(),
                new Handler() {
                    public void handleMessage(Message msg) {

                        switch (msg.what) {

                            case 2:

                                selectedNewsItem = ManagerNews.getNewsByIdFromDb(
                                        App.getContext(), newsId);

                                if (selectedNewsItem != null
                                        && getActivity() != null & rootView != null)
                                    setNews();
                                progressBarUpdate.setVisibility(View.GONE);

                                break;
                            default:
                                break;
                        }
                    }

                }));
    }

    public void openTextSizeLayout() {
        if (textSizeLayout.getVisibility() == View.VISIBLE)
            textSizeLayout.setVisibility(View.GONE);
        else
            textSizeLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        if (adViewTop != null) {
            adViewTop.destroy();
        }
        if (adViewBottom != null) {
            adViewBottom.destroy();
        }

        super.onDestroyView();
    }
}
