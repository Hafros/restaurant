package com.restaurant.hafros.restaurant;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class DataViewModel extends Application {

    @Nullable String id = null;

    public boolean isLoading = false;
    public boolean hasNextPage = true;
    private int current_page = 1;



    public DataViewModel(@Nullable String id){
        if (id != null){
            this.id = id;
        }

    }

    public boolean canLoad(){

        if (hasNextPage && !isLoading) {
            return true;
        }

        return false;
    }

    public void fetchItems(@Nullable String page,final APIHandler handler){

        isLoading = true;

        if (this.id == null || this.id.isEmpty()){

            RequestManager.fetchList(""+current_page, new APIHandler() {
                @Override
                public void successHandler(ArrayList<DataModel> items, boolean hasNext, boolean hasPrevious) {
                    isLoading = false;
                    hasNextPage = hasNext;
                    current_page++;
                    handler.successHandler(items,hasNext,hasPrevious);
                }

                @Override
                public void failHandler(String error) {
                    isLoading = false;
                    handler.failHandler(error);
                }
            });


        }else{

            RequestManager.fetchByID(id, new APIHandler() {
                @Override
                public void successHandler(ArrayList<DataModel> items, boolean hasNext, boolean hasPrevious) {
                    isLoading = false;
                    handler.successHandler(items,hasNext,hasPrevious);
                }

                @Override
                public void failHandler(String error) {
                    isLoading = false;
                    handler.failHandler(error);
                }
            });

        }

    }

}
