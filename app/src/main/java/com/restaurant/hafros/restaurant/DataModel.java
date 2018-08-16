package com.restaurant.hafros.restaurant;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class DataModel {

    @NonNull Integer id;
    @Nullable String name;
    @Nullable String photo;
    @Nullable String url;
    @Nullable String text;

    public DataModel(@NonNull JSONObject jsonObject) throws JSONException {

        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.photo = jsonObject.getString("photo");
        this.url = jsonObject.getString("url");
        if (jsonObject.has("text") && !jsonObject.getString("text").isEmpty()){
            this.text = jsonObject.getString("text");
        }

    }

}
