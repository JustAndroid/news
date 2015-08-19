package com.allNews.managers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Listener;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;

import gregory.network.rss.R;

import static android.content.Context.ACTIVITY_SERVICE;

public class EWLoader {
    private static final String LOG_TAG = EWLoader.class.getName();
    private static Picasso singleton;
    // private static int width;
    // private static DisplayMetrics metrics;

    private static LruCache cache;


    public static Picasso with(Context context) {
        if (singleton == null) {

            cache = getMemoryCache(context);

            singleton = new Picasso.Builder(context).listener(new Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception ex) {
                    Log.e(LOG_TAG, "uri : " + uri );

                }
            }).memoryCache(cache).build();
            singleton.setDebugging(false);

            // metrics = context.getResources().getDisplayMetrics();
            // width = (int) (metrics.widthPixels / 2.0f);
        }

        return singleton;
    }


    public static Bitmap getBitmap(String key) {
        if (cache != null) {
            return cache.get(key);
        }
        return null;
    }


    public static void clearCache() {
        if (cache != null) {
            cache.evictAll();
        }
    }

    public static void clearCache2() {
        if (cache != null) {
            cache.clear();
        }
    }

    public static void setBitmap(String key, Bitmap bitmap) {
        if (cache != null) {
            cache.set(key, bitmap);
        }
        // return null;
    }


    public static LruCache getMemoryCache(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        // Target 75% of the available RAM.
        int size = (int) (1024 * 1024 * memoryClass / 4);
        // 69905066,666666666666666666666667
        // 41943040
        return new LruCache(size);
    }


    public static void loadBigAvatar(Context context, String url, ImageView imageView) {
        int size = context.getResources().getDisplayMetrics().widthPixels / 3;

        with(context).load(url).resize(size, size).centerCrop().placeholder(R.drawable.stub).error(R.drawable.stub)
                .into(imageView);
    }


    public static void loadBigAvatarEvent(Context context, String url, ImageView imageView) {
        int size = context.getResources().getDisplayMetrics().widthPixels / 2;

        with(context).load(url).resize(size, size).centerCrop().placeholder(R.drawable.stub).error(R.drawable.stub)
                .into(imageView);
    }


    public static void loadAvatarEvent(Context context, String url, ImageView imageView) {
        int size = context.getResources().getDisplayMetrics().widthPixels;

        with(context).load(url).resize(size, size).centerCrop().placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView, int placeholder) {
        int size = context.getResources().getDisplayMetrics().widthPixels;

        with(context).load(url).resize(size, size).centerCrop().placeholder(placeholder).error(placeholder).into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        int size = context.getResources().getDisplayMetrics().widthPixels;

        with(context).load(url).resize(size, size).centerCrop().into(imageView);
    }

    public static void load(Context context, String url, ImageView imageView) {
        with(context).load(url).resize(150, 150).centerCrop().placeholder(R.drawable.stub).error(R.drawable.stub).into(imageView);
    }


    public static void load(Context context, int defaultImage, String url, ImageView imageView) {
        with(context).load(url).resize(150, 150).centerCrop().placeholder(defaultImage).error(defaultImage).into(imageView);
    }


    public static void loadImg(Context context, String url, ImageView imageView) {
        with(context).load(url).into(imageView);
    }

    public static void loadImg(Context context, String url, ImageView imageView, Callback callBack) {

        with(context).load(url).into(imageView, callBack);
    }

    public static void loadImg(Context context, String url, int placeholderResId, ImageView imageView) {
        with(context).load(url).placeholder(placeholderResId).into(imageView);
    }

    public static void loadImg23(Context context, String url, ImageView imageView) {
        int size = context.getResources().getDisplayMetrics().widthPixels * 2 / 3;
        with(context).load(url).resize(size, size).centerCrop().into(imageView);
    }

    public static void loadWithoutHolder(Context context, String url, ImageView imageView) {
        int size = context.getResources().getDisplayMetrics().widthPixels / 3;
        with(context).load(url).resize(size, size).centerCrop().into(imageView);
    }


    public static void loadWithErrorDrawable(Context context, String url, Target target, int theDrawable) {
        with(context).load(url).placeholder(R.drawable.stub).error(theDrawable).transform(new CropSquareTransformation()).into(target);
    }


    public static void cancel(Context context, ImageView imageView) {
        with(context).cancelRequest(imageView);
    }


    public static void loadTarget(Context context, String url, final ImageView mliv) {
        with(context).load(url).into(new Target() {

//          @Override
//          public void onSuccess(Bitmap arg0)
//          {
//              L.e("LOADED");
//              // mliv.setImageBitmap(arg0);
//          }
//
//
//          @Override
//          public void onError()
//          {
//
//          }


            @Override
            public void onBitmapFailed(Drawable arg0) {
                // TODO Auto-generated method stub

            }


            @Override
            public void onBitmapLoaded(Bitmap arg0, LoadedFrom arg1) {
                // TODO Auto-generated method stub
                mliv.setImageBitmap(arg0);
            }


            @Override
            public void onPrepareLoad(Drawable arg0) {
                // TODO Auto-generated method stub

            }
        });
    }


    public static void loadTarget(Context context, String url) {
        // L.d("stat load");
        with(context).load(url).into(new Target() {

            @Override
            public void onBitmapFailed(Drawable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onBitmapLoaded(Bitmap arg0, LoadedFrom arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPrepareLoad(Drawable arg0) {
                // TODO Auto-generated method stub

            }
//
//          @Override
//          public void onSuccess(Bitmap arg0)
//          {
//              L.e("LOADED =====================");
//              // mliv.setImageBitmap(arg0);
//          }
//
//
//          @Override
//          public void onError()
//          {
//              L.e("error =====================");
//          }
        });
    }

    public static Bitmap decodeBitmap(Bitmap theBitmap, int reqWidth, int reqHeight) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        theBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] theBitmapByte = stream.toByteArray();

        // First decode with inJustDecodeBounds=true to check dimensions
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeByteArray(theBitmapByte, 0, theBitmapByte.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(theBitmapByte, 0, theBitmapByte.length, options);
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            if (options.outWidth > options.outHeight) {
                inSampleSize = (int) Math.ceil((float) options.outHeight / (float) reqHeight);
            } else {
                inSampleSize = (int) Math.ceil((float) options.outWidth / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    private static class CropSquareTransformation
            implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
            Bitmap result = decodeBitmap(source, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }


        @Override
        public String key() {
            return "square()";
        }
    }

    // >>>>>>> fddefa033b07bdc56065b190c79b6a3630754d91
}
