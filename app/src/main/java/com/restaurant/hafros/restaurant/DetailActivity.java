package com.restaurant.hafros.restaurant;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nguyenhoanglam.progresslayout.ProgressWheel;

import java.util.ArrayList;

import cn.refactor.multistatelayout.MultiStateLayout;
import cn.refactor.multistatelayout.OnStateViewCreatedListener;

public class DetailActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;
    private MultiStateLayout multiStateLayout;
    private boolean loaded = false;
    private ImageView imageView;
    private TextView name;
    private TextView textView;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();

        id = intent.getExtras().getString("id");

        imageView = (ImageView) findViewById(R.id.imageView);
        name = (TextView) findViewById(R.id.textView);
        textView = (TextView) findViewById(R.id.text);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        multiStateLayout = (MultiStateLayout) findViewById(R.id.multi_state_layout);

        multiStateLayout.setOnStateViewCreatedListener(new OnStateViewCreatedListener() {
            @Override
            public void onViewCreated(View view, int i) {

                if (i == MultiStateLayout.State.EMPTY){

                    RelativeLayout mainView = (RelativeLayout) view.findViewById(R.id.emptyView);
                    mainView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));

                }

                if (i == MultiStateLayout.State.NETWORK_ERROR){

                    RelativeLayout mainView = (RelativeLayout) view.findViewById(R.id.emptyView);
                    mainView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));

                }

                if (i == MultiStateLayout.State.LOADING){

                    ProgressWheel wheel = (ProgressWheel) view.findViewById(R.id.progress);

                    wheel.setLinearProgress(false);
                    wheel.setRimColor(getResources().getColor(R.color.preloader));
                    wheel.setBarColor(getResources().getColor(R.color.backgroundColor));
                    wheel.setBarWidth(10);
                    wheel.setRimWidth(10);

                    wheel.spin();

                    wheel.requestLayout();

                    RelativeLayout mainView = (RelativeLayout) view.findViewById(R.id.loadingView);

                    mainView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));

                }


            }
        });
    }

    private void loadContent(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                multiStateLayout.setState(MultiStateLayout.State.LOADING);
            }
        });

        RequestManager.fetchByID(id, new APIHandler() {
            @Override
            public void successHandler(ArrayList<DataModel> items) {

                if (items.size() == 0){

                    multiStateLayout.setState(MultiStateLayout.State.EMPTY);

                    return;
                }

                updateContent(items.get(0));

            }

            @Override
            public void failHandler(String error) {

            }
        });

    }

    private void updateContent(DataModel model){

        if (model.photo != null){
            App.loadImage(model.photo, imageView);
        }

        name.setText(model.name);

        textView.setText(model.text);

    }

    @Override
    public void networkAvailable() {
        if (loaded){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    multiStateLayout.setState(MultiStateLayout.State.CONTENT);
                }
            });


        }
        else{
            loadContent();
        }
    }

    @Override
    public void networkUnavailable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                multiStateLayout.setState(MultiStateLayout.State.NETWORK_ERROR);
            }
        });
    }
}
