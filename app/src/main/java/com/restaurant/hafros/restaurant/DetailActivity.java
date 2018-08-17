package com.restaurant.hafros.restaurant;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.applinks.AppLinkData;
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

        String action = intent.getAction();
        Uri data = intent.getData();

        if (data != null){

            String idStr = data.getEncodedPath().substring(data.getEncodedPath().lastIndexOf('/') + 1);

            Log.d("DEEP","DATA = "+idStr);


            id = idStr;
        }
        else{
            id = intent.getExtras().getString("id");
        }



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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        multiStateLayout.setState(MultiStateLayout.State.EMPTY);
                    }
                });
            }
        });

    }

    private void updateContent(final DataModel model){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (model.photo != null){
                    App.loadImage(RequestManager.getBASEURL()+model.photo, imageView);
                }

                name.setText(model.name);

                textView.setText(model.text);


                multiStateLayout.setState(MultiStateLayout.State.CONTENT);
            }
        });



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
    public void onDestroy() {

        super.onDestroy();

        unregisterReceiver(networkStateReceiver);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.custom_menu_item, menu);
        return true;
    }

    private void goToHome(){

        Intent intent = new Intent( this, MainActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        this.startActivity( intent );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_name:
                goToHome();
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        super.finish();
    }
}
