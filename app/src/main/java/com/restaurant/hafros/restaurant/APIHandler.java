package com.restaurant.hafros.restaurant;

import org.json.JSONArray;

import java.util.ArrayList;

public interface APIHandler {
    void successHandler(ArrayList<DataModel> items);
    void failHandler(String error);
}
