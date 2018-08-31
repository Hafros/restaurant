package com.restaurant.hafros.restaurant;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.nguyenhoanglam.progresslayout.ProgressWheel;

public class LoadMoreFooterView extends FrameLayout {

    private Status mStatus;

    private ProgressWheel mLoadingView;

    private View mErrorView;

    private View mTheEndView;

    private ProgressWheel progressBar;

    private OnRetryListener mOnRetryListener;

    public LoadMoreFooterView(Context context) {
        this(context, null);
        LayoutInflater.from(context).inflate(R.layout.layout_irecyclerview_load_more_footer_view, this, true);

        mLoadingView = findViewById(R.id.loadingView);
        mErrorView = findViewById(R.id.errorView);
        mTheEndView = findViewById(R.id.theEndView);

        progressBar = (ProgressWheel) mLoadingView;

//        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
//
//        int[][] states = new int[][] {
//                new int[] { android.R.attr.state_enabled}, // enabled
//                new int[] {-android.R.attr.state_enabled}, // disabled
//                new int[] {-android.R.attr.state_checked}, // unchecked
//                new int[] { android.R.attr.state_active},
//                new int[] { android.R.attr.state_focused}// pressed
//        };
//
//        int[] colors = new int[] {
//                Color.YELLOW,
//                Color.RED,
//                Color.GREEN,
//                Color.BLUE,
//                Color.MAGENTA
//        };
//
//        ColorStateList myList = new ColorStateList(states, colors);
//
//        progressBar.setIndeterminateTintList(myList);
//
//
//
//        progressBar.getIndeterminateDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.MULTIPLY);






        mErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRetryListener != null) {
                    mOnRetryListener.onRetry(LoadMoreFooterView.this);
                }
            }
        });

        // setStatus(Status.GONE);
    }


    public void stylishView(int RimColor, int BarColor){
        mLoadingView.setRimColor(RimColor);
        mLoadingView.setBarColor(BarColor);
        this.setBackgroundColor(BarColor);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_irecyclerview_load_more_footer_view, this, true);

        mLoadingView = findViewById(R.id.loadingView);
        mErrorView = findViewById(R.id.errorView);
        mTheEndView = findViewById(R.id.theEndView);


        mLoadingView.setLinearProgress(false);

        mLoadingView.setBarWidth(10);
        mLoadingView.setRimWidth(10);
        mLoadingView.setCircleRadius(App.dpToPx(36,getResources()));




        mLoadingView.spin();

        mLoadingView.requestLayout();



        mErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRetryListener != null) {
                    mOnRetryListener.onRetry(LoadMoreFooterView.this);
                }
            }
        });

        // setStatus(Status.GONE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);




    }

    public void setOnRetryListener(OnRetryListener listener) {
        this.mOnRetryListener = listener;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
        change();
    }

    public boolean canLoadMore() {
        return mStatus == Status.GONE || mStatus == Status.ERROR;
    }

    private void change() {
        switch (mStatus) {
            case GONE:
                mLoadingView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
                mTheEndView.setVisibility(GONE);
                break;

            case LOADING:
                mLoadingView.setVisibility(VISIBLE);
                mErrorView.setVisibility(GONE);
                mTheEndView.setVisibility(GONE);



                // progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));

                break;

            case ERROR:
                mLoadingView.setVisibility(GONE);
                mErrorView.setVisibility(VISIBLE);
                mTheEndView.setVisibility(GONE);
                break;

            case THE_END:
                mLoadingView.setVisibility(GONE);
                mErrorView.setVisibility(GONE);
                mTheEndView.setVisibility(VISIBLE);
                break;
        }
    }

    public enum Status {
        GONE, LOADING, ERROR, THE_END
    }

    public interface OnRetryListener {
        void onRetry(LoadMoreFooterView view);
    }

}
