package com.allNews.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.allNews.view.TouchImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import gregory.network.rss.R;

public final class ImageViewerActivity extends AppCompatActivity {

    public static final String IMAGE_URL = "IMAGE_URL";

    private TouchImageView tiv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        tiv_image = (TouchImageView) findViewById(R.id.tiv_image);
        tiv_image.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
            @Override public void onMove() {} });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(IMAGE_URL))
                Glide.with(ImageViewerActivity.this)
                        .load(extras.getString(IMAGE_URL))
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                tiv_image.setImageBitmap(resource);
                            }
                        });
        }
    }
}
