package com.restaurant.hafros.restaurant;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class RequestManager {

    public static String getBASEURL(){

        return "http://178.128.71.171";

    }


    public static void makeGetRequest(final String url, final NetworkHandler handler){

        OkHttpClient httpclient = new OkHttpClient();

        Request request = new Request.Builder().url(url).get().build();

        httpclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                handler.failHandler(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                @Nullable Response responceTemp = response;
                String content = new String(responceTemp.body().bytes(), "UTF-8");

                if (responceTemp.body() != null) {
                    Log.d("NETWORK","URL = "+url + " RESPONSE = "+content);
                }



                if (responceTemp.body() != null) {
                    try {
                        handler.successHandler(content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    handler.failHandler("response.body() == null");
                }
            }
        });

    }

    private static boolean isJSONArray(String body){

        try {
            JSONArray jsonObject = new JSONArray(body);
        }
        catch (JSONException e){
            return false;
        }
        catch (NullPointerException e){
            return false;
        }
        catch (IllegalStateException e){
            return false;
        }

        return true;

    }

    private static boolean isJSONObject(String body){

        try {
            JSONObject jsonObject = new JSONObject(body);
        }
        catch (JSONException e){
            return false;
        }
        catch (NullPointerException e){
            return false;
        }
        catch (IllegalStateException e){
            return false;
        }

        return true;

    }

    public static void fetchByID(String id, final APIHandler handler){

        makeGetRequest(getBASEURL()+"/api/"+id, new NetworkHandler() {
            @Override
            public void successHandler(String body) throws JSONException {

                if (isJSONObject(body)){
                    JSONObject jsonObject = new JSONObject(body);

                    DataModel model = new DataModel(jsonObject);

                    if (model != null){

                        ArrayList<DataModel> arrayList = new ArrayList<>();
                        arrayList.add(model);

                        handler.successHandler(arrayList);

                    }

                }

                if (isJSONArray(body)){

                    ArrayList<DataModel> arrayList = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(body);

                    for (int i=0; i<jsonArray.length(); i++) {

                        JSONObject component = jsonArray.getJSONObject(i);

                        DataModel model = new DataModel(component);

                        if (model != null){
                            arrayList.add(model);
                        }

                    }

                    handler.successHandler(arrayList);

                }



            }

            @Override
            public void failHandler(String error) {
                handler.failHandler(error);
            }
        });

    }

    public static void fetchList(final APIHandler handler){

        makeGetRequest(getBASEURL()+"/api/list", new NetworkHandler() {
            @Override
            public void successHandler(String body) throws JSONException {

                if (isJSONObject(body)){
                    JSONObject jsonObject = new JSONObject(body);

                    DataModel model = new DataModel(jsonObject);

                    if (model != null){

                        ArrayList<DataModel> arrayList = new ArrayList<>();
                        arrayList.add(model);

                        handler.successHandler(arrayList);

                    }

                }

                if (isJSONArray(body)){

                    ArrayList<DataModel> arrayList = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(body);

                    for (int i=0; i<jsonArray.length(); i++) {

                        JSONObject component = jsonArray.getJSONObject(i);

                        DataModel model = new DataModel(component);

                        if (model != null){
                            arrayList.add(model);
                        }

                    }

                    handler.successHandler(arrayList);

                }



            }

            @Override
            public void failHandler(String error) {
                handler.failHandler(error);
            }
        });

    }

}
