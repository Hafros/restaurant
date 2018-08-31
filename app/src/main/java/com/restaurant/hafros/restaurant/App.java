package com.restaurant.hafros.restaurant;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class App extends Application {

    private static Context context;

    @Override public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }

    public static void loadImage(String url, ImageView imageView){

        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(imageView);

    }

    public static int dpToPx(float dp, Resources resources) {
        float px =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
}
