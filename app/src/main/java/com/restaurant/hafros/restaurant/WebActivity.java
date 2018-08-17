package com.restaurant.hafros.restaurant;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.nguyenhoanglam.progresslayout.ProgressWheel;

import cn.refactor.multistatelayout.MultiStateLayout;
import cn.refactor.multistatelayout.OnStateViewCreatedListener;

public class WebActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;
    private MultiStateLayout multiStateLayout;
    private String url;
    private WebView webView;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);

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



        Intent intent = getIntent();

        url = intent.getExtras().getString("url");

        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.d("SHOULD","LOAD");
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.d("SHOULD","LOADED");
                loaded = true;
                multiStateLayout.setState(MultiStateLayout.State.CONTENT);
            }
        };

        webView.setWebViewClient(webViewClient);



        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        load();


    }

    private void load(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                multiStateLayout.setState(MultiStateLayout.State.LOADING);
                webView.loadUrl(url);
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

    @Override
    public void networkAvailable() {
        if (!loaded){
            load();
        }
        else{

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    multiStateLayout.setState(MultiStateLayout.State.CONTENT);
                }
            });


        }
    }

    @Override
    public void onDestroy(){
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
}
