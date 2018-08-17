package com.restaurant.hafros.restaurant;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.nguyenhoanglam.progresslayout.ProgressWheel;

import java.util.ArrayList;

import cn.refactor.multistatelayout.MultiStateLayout;
import cn.refactor.multistatelayout.OnStateViewCreatedListener;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private RecyclerView recyclerView;
    private MultiStateLayout multiStateLayout;
    private ArrayList<DataModel> mData = new ArrayList<>();
    private DataAdapter adapter;
    private NetworkStateReceiver networkStateReceiver;
    private DataViewModel viewModel;


    private boolean isIDNull(){
        Intent intent = getIntent();

        try {
            intent.getExtras().getString("id");
        }
        catch (NullPointerException e){
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        AppLinkData.fetchDeferredAppLinkData(this, new AppLinkData.CompletionHandler() {
            @Override
            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {

                if (appLinkData != null){
                    Log.d("DEEP",""+appLinkData.toString());
                }


            }
        });

        AppLinkData.fetchDeferredAppLinkData(this, getResources().getString(R.string.facebook_app_id), new AppLinkData.CompletionHandler() {
            @Override
            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                if (appLinkData != null){
                    Log.d("DEEP2",""+appLinkData.toString());
                }
            }
        });





            if (isIDNull()){
                viewModel = new DataViewModel(null);
            }
            else {
                Intent intent = getIntent();
                String string = intent.getExtras().getString("id");
                viewModel = new DataViewModel(string);




            }







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




        recyclerView = (RecyclerView) findViewById(R.id.mainList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adapter = new DataAdapter(mData);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                if ((mData.get(position).url != null && !mData.get(position).url.isEmpty())){

                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
                    intent.putExtra("url", ""+mData.get(position).url);
                    startActivity(intent);

                }
                else{
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                    intent.putExtra("id", ""+mData.get(position).id);
                    startActivity(intent);
                }

            }
        });


        loadData();


    }

    private void loadData(){

        multiStateLayout.setState(MultiStateLayout.State.LOADING);

        viewModel.fetchItems(new APIHandler() {
            @Override
            public void successHandler(ArrayList<DataModel> items) {
                if (items.isEmpty()){
                    showEmpty();
                    return;
                }

                updateAdapter(items);
                Log.d("MODELS","FETCHED "+items);
            }

            @Override
            public void failHandler(String error) {
                Log.d("MODELS","FAIL "+error);
            }
        });

    }

    private void showEmpty(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                multiStateLayout.setState(MultiStateLayout.State.EMPTY);
            }
        });


    }



    private void updateAdapter(final ArrayList<DataModel> items){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mData.clear();
                mData.addAll(items);

                adapter.notifyDataSetChanged();

                multiStateLayout.setState(MultiStateLayout.State.CONTENT);

            }
        });

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        unregisterReceiver(networkStateReceiver);
    }


    @Override
    public void networkAvailable() {

        if (mData.size() == 0){
            loadData();
            return;
        }

        multiStateLayout.setState(MultiStateLayout.State.CONTENT);

    }

    @Override
    public void networkUnavailable() {

        multiStateLayout.setState(MultiStateLayout.State.NETWORK_ERROR);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
