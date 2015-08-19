package com.allNews.activity;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import gregory.network.rss.R;

public class WebViewActivity extends ActionBarActivity {
    private WebView webView;
    public static final String URL_LINK_KEY = "URL_LINK_KEY";
    private String urlLink = null;
    private TextView mTitleTextView;

    private ImageButton btnShare;
    private ImageButton btnClose;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_layout);
        initWebView();
        initActionBar();

        urlLink = getIntent().getStringExtra(URL_LINK_KEY);
        if (urlLink != null) {

            webView.loadUrl(urlLink);
        }else {
            Toast.makeText(getApplicationContext(), "Error download page " , Toast.LENGTH_LONG).show();
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, urlLink);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

    }

    private void initActionBar() {

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
            bar.setHomeButtonEnabled(false);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.white)));

            //bar.setElevation(1);
            LayoutInflater mInflater = LayoutInflater.from(this);
            View mCustomView = mInflater.inflate(R.layout.web_action_bar, null);
            mTitleTextView = (TextView) mCustomView.findViewById(R.id.web_title_txt);
            //      mTitleTextView.setText("all news");
            btnShare = (ImageButton) mCustomView
                    .findViewById(R.id.image_share);
            btnClose = (ImageButton) mCustomView.findViewById(R.id.image_close);
            bar.setCustomView(mCustomView);
            bar.setDisplayShowCustomEnabled(true);
        }



    }



    private void initWebView() {

        webView = (WebView) findViewById(R.id.webview);
        final ProgressBar viewContentProgress = (ProgressBar) findViewById(R.id.progressBar2);
        WebSettings webSettings = webView.getSettings();
       // webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        webSettings.setSupportZoom(true);




        webView.setWebChromeClient(new WebChromeClient() {

                                       @Override
                                       public void onProgressChanged(WebView view, int newProgress) {

                                           viewContentProgress.setProgress(newProgress);
                                           viewContentProgress.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
                                       }
                                   }

        );
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        webView.setWebViewClient(customWebViewClient);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }

        return super.onKeyDown(keyCode, event);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) { //this method is used for adding menu items to the Activity
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //this method is used for handling menu items' events
// Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:

                super.onBackPressed();


            case R.id.goBack:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                return true;

            case R.id.goForward:
                if (webView.canGoForward()) {
                    webView.goForward();

                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Handle the error
        }

       @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url!= null) {
                view.loadUrl(url);
                return false;
            }else {
                return true;
            }
        }



        @Override
        public void onPageFinished(WebView view, String url) {
            mTitleTextView.setText(view.getTitle());
        }
    }


}



