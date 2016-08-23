package com.allNews.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.allNews.application.App;
import com.allNews.data.NewApp.NewApp;
import com.allNews.managers.EWLoader;
import com.allNews.managers.ManagerNewAppNewApi;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;
import com.allNews.view.NewsContentView;
import com.allNews.view.ResizableImageView;
import com.allNews.web.Statistic;


import gregory.network.rss.R;

public class NewAppFragment extends Fragment {

    public static final String ARG_OBJECT = "newsId";
    public static final String ARG_IMAGE_RES = "image_source";
    public static final String ARG_ACTION_BG_RES = "image_action_bs_res";
    public NewApp selectedNewsItem;
    private ProgressBar progressBarUpdate;
    private View textSizeLayout;
    private View rootView;

    private Bundle mArguments;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.new_app_item_displayer, container,
                false);
        initWidget();

        Long newsID = getArguments().getLong(ARG_OBJECT);
        selectedNewsItem = ManagerNewAppNewApi.getNewAppByIdFromDb(getActivity(), newsID);

        if (selectedNewsItem != null) {
            Log.e("selectedNewsItem ", "" + selectedNewsItem.getNodeID());
            if (getActivity() != null & rootView != null)
                setNewApp();
        }

        try {
            if (((AllNewsActivity) getActivity()) != null) {
                ActionBar bar = ((AllNewsActivity) getActivity()).getSupportActionBar();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return rootView;
    }


    private void setNewApp() {

        LinearLayout mainLay = (LinearLayout) rootView
                .findViewById(R.id.fullNewsViewMain);
        TextView txtTitle = (TextView) rootView
                .findViewById(R.id.fullTitleView);
        TextView txtText = (TextView) rootView
                .findViewById(R.id.fullFullTextView);
        Button btnNewApp = (Button) rootView.findViewById(R.id.new_app_button);
        LinearLayout shareNewsLay = (LinearLayout) rootView
                .findViewById(R.id.shareNewsLayout);
        LinearLayout shareNewsDivider1 = (LinearLayout) rootView
                .findViewById(R.id.shareNewsDivider1);
        LinearLayout shareNewsDivider2 = (LinearLayout) rootView
                .findViewById(R.id.shareNewsDivider2);
        Button btnshareApp = (Button) rootView.findViewById(R.id.btnShareApp);
        Button btnDownloadApp = (Button) rootView
                .findViewById(R.id.btnDownloadApp);
        Button btnshareToAll = (Button) rootView.findViewById(R.id.shareToAll);
        ImageButton shareGmail = (ImageButton) rootView
                .findViewById(R.id.shareGmail);
        ImageButton shareFacebook = (ImageButton) rootView
                .findViewById(R.id.shareFacebook);
        Button button2 = (Button) rootView.findViewById(R.id.button);
        int textSize = MyPreferenceManager.getTextSize(getActivity());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());


        int curTheme = MyPreferenceManager.getCurrentTheme(getActivity());

        if (curTheme == AllNewsActivity.THEME_DARK) {


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

            mainLay.setBackgroundColor(getResources().getColor(
                    R.color.newsBgNight));
            shareNewsDivider1.setBackgroundColor(getResources().getColor(
                    R.color.shareNewsDividerBgNight));
            shareNewsDivider2.setBackgroundColor(getResources().getColor(
                    R.color.shareNewsDividerBgNight));
            textSizeLayout.setBackgroundColor(getResources().getColor(
                    R.color.tabBgNight));
        }

        txtTitle.setText(selectedNewsItem.getTitle());
        txtTitle.setTextSize(textSize + 1);
        txtTitle.setVisibility(View.VISIBLE);

        String imageUrl = selectedNewsItem.getImgUrl();
        if (imageUrl != null && Utils.isUrlValid(imageUrl)) {
            final ResizableImageView image = (ResizableImageView) rootView
                    .findViewById(R.id.fullItemImg);
            EWLoader.loadImg(getActivity(), imageUrl, image);
            setOpenFullImageListener(image, imageUrl);
        }


        NewsContentView fullTextLayout = (NewsContentView) rootView
                .findViewById(R.id.fullTextLayout);


        String content = selectedNewsItem.getContent();
        fullTextLayout.setnewsName(selectedNewsItem.getTitle());
        // txtTitle.setVisibility(View.GONE);
        final String linkHref = selectedNewsItem.getRefLink();
        btnNewApp.setVisibility(View.VISIBLE);
        if (linkHref != null && Utils.isUrlValid(linkHref)) {
            btnNewApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Statistic.sendStatistic(getActivity().getApplicationContext(), Statistic.CATEGORY_NEW_APP, Statistic.ACTION_CLICK_LINK_NEW_APP,
                     selectedNewsItem.getTitle() + ",  New App ID = " + selectedNewsItem.getNodeID(), 0L);
                    Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkHref));
                    startActivity(downloadIntent);
                }
            });
        } else {
            btnNewApp.setVisibility(View.GONE);
        }
        button2.setVisibility(View.VISIBLE);
        if (linkHref != null && Utils.isUrlValid(linkHref)) {
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Statistic.sendStatistic(getActivity().getApplicationContext(), Statistic.CATEGORY_NEW_APP, Statistic.ACTION_CLICK_LINK_NEW_APP,
                            selectedNewsItem.getTitle() + ",  New App ID = " + selectedNewsItem.getNodeID(), 0L);
                    Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkHref));
                    startActivity(downloadIntent);
                }
            });
        } else {
           button2.setVisibility(View.GONE);
        }
        fullTextLayout.setIsNewAppMark();

        fullTextLayout.setContentNewApp(content);

        // txtDate.setText(selectedRssItem.getPubDate());


        shareNewsLay.setVisibility(View.VISIBLE);
    }


    private void setOpenFullImageListener(ResizableImageView image, final String imageUrl) {
        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                intent.putExtra(ImageViewerActivity.IMAGE_URL, imageUrl);
                v.getContext().startActivity(intent);

            }
        });
    }


    private void initWidget() {
        final String[] sizeValues = getResources().getStringArray(R.array.saveCharValues);
        int textSize = MyPreferenceManager.getTextSize(getActivity());
        ScrollView fullNewsScrollView = (ScrollView) rootView
                .findViewById(R.id.fullNewsScrollView);
        fullNewsScrollView.setOnTouchListener(new View.OnTouchListener() {

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
        textSizeLayout = (View) rootView.findViewById(R.id.textSizeLayout);
        SeekBar textSizeSeekBar = (SeekBar) rootView.findViewById(R.id.textSizeSeekBar);
        textSizeSeekBar.setMax(sizeValues.length - 1);
        for (int i = 0; i < sizeValues.length; i++) {

            if (sizeValues[i].equals("" + textSize)) {
                textSizeSeekBar.setProgress(i);
                break;
            }
        }
        textSizeSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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

                        ((NewsCollectionActivity) getActivity()).needRefresh = true;
                        MyPreferenceManager.setTextSize(getActivity(),
                                sizeValues[progress]);
                        setNewApp();
                    }
                });
    }
    public void openTextSizeLayout() {
        if (textSizeLayout.getVisibility() == View.VISIBLE)
            textSizeLayout.setVisibility(View.GONE);
        else
            textSizeLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }
}
