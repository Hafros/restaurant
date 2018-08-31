package com.restaurant.hafros.restaurant;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.nguyenhoanglam.progresslayout.ProgressWheel;
import com.rubengees.easyheaderfooteradapter.EasyHeaderFooterAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.multistatelayout.MultiStateLayout;
import cn.refactor.multistatelayout.OnStateViewCreatedListener;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private IRecyclerView recyclerView;
    private MultiStateLayout multiStateLayout;
    private ArrayList<DataModel> mData = new ArrayList<>();
    private DataAdapter adapter;
    private NetworkStateReceiver networkStateReceiver;
    private DataViewModel viewModel;
    private LoadMoreFooterView loadMoreFooterView;
    private EasyHeaderFooterAdapter easyHeaderFooterAdapter;


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

        loadMoreFooterView = new LoadMoreFooterView(this);

        loadMoreFooterView.stylishView(Color.RED,Color.WHITE);



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




        recyclerView = (IRecyclerView) findViewById(R.id.mainList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adapter = new DataAdapter(mData);

        easyHeaderFooterAdapter =
                new EasyHeaderFooterAdapter(adapter);

        easyHeaderFooterAdapter.setFooter(loadMoreFooterView);

        ViewGroup.LayoutParams params2 = easyHeaderFooterAdapter.getFooter().getLayoutParams();

        params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, App.dpToPx(48,getResources()));

        easyHeaderFooterAdapter.getFooter().setLayoutParams(params2);

        easyHeaderFooterAdapter.getFooter().setBackgroundColor(Color.WHITE);

        recyclerView.setAdapter(easyHeaderFooterAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setLoadMoreEnabled(true);
        recyclerView.setLoadMoreFooterView(loadMoreFooterView);

        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (mData.size() <= 0){
                    return;
                }

                if (viewModel.canLoad()) {
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                    loadData();

                }
                else{

                    if (!viewModel.hasNextPage){
                        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                        easyHeaderFooterAdapter.setFooter(null);
                        recyclerView.setOnLoadMoreListener(null);
                    }


                }

                Log.d("LOAD MORE","OK");
            }
        });

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

        if (mData.size() == 0) {
            multiStateLayout.setState(MultiStateLayout.State.LOADING);
        }

        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);

        viewModel.fetchItems(null,new APIHandler() {
            @Override
            public void successHandler(ArrayList<DataModel> items, boolean hasNext, boolean hasPrevious) {
                if (items.isEmpty()){
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    easyHeaderFooterAdapter.setFooter(null);
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

                //mData.clear();
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

            if (viewModel.canLoad()){
                loadData();
            }


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
