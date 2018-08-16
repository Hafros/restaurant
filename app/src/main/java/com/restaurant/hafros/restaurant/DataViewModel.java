package com.restaurant.hafros.restaurant;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class DataViewModel extends Application {

    @Nullable String id = null;



    public DataViewModel(@Nullable String id){
        if (id != null){
            this.id = id;
        }

    }

    public void fetchItems(final APIHandler handler){

        if (this.id == null || this.id.isEmpty()){

            RequestManager.fetchList(new APIHandler() {
                @Override
                public void successHandler(ArrayList<DataModel> items) {

                    handler.successHandler(items);

                }

                @Override
                public void failHandler(String error) {
                    handler.failHandler(error);
                }
            });

        }else{

            RequestManager.fetchByID(id, new APIHandler() {
                @Override
                public void successHandler(ArrayList<DataModel> items) {
                    handler.successHandler(items);
                }

                @Override
                public void failHandler(String error) {
                    handler.failHandler(error);
                }
            });

        }

    }

}
