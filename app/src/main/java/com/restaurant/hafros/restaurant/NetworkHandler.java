package com.restaurant.hafros.restaurant;

import org.json.JSONException;

public interface NetworkHandler {
    void successHandler(String body) throws JSONException;
    void failHandler(String error);
}
