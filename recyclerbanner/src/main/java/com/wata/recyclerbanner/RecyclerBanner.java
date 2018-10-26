package com.wata.recyclerbanner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 参考来自：
 * 1. https://github.com/renjianan/RecyclerBanner
 * 2. https://github.com/zjloong/RecyclerBanner
 * 3. https://mp.weixin.qq.com/s/znjGPRO7cM5gAKJY2IZ9rA
 */
public class RecyclerBanner extends FrameLayout {

    //-------------------------------- 不为空
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearLayout;
    private GradientDrawable mDefaultDrawable, mSelectedDrawable;
    private boolean mIsPlaying;
    private long mIntervalTime = 3500;
    private Handler mHandler = new Handler();
    private String TAG = getClass().getSimpleName();
    //-------------------------------- 可能为空
    private RecyclerBannerAdapter bannerAdapter;
    private int startX, startY, currentIndex, dp6 = dp2px(6);

    public RecyclerBanner(Context context) {
        this(context, null);
    }

    public RecyclerBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //指示点
        mDefaultDrawable = new GradientDrawable();
        mDefaultDrawable.setSize(dp6, dp6);
        mDefaultDrawable.setCornerRadius(dp6);
        mDefaultDrawable.setColor(Color.WHITE);
        mSelectedDrawable = new GradientDrawable();
        mSelectedDrawable.setShape(GradientDrawable.RECTANGLE);
        mSelectedDrawable.setCornerRadius(dp2px(8));
        mSelectedDrawable.setSize(dp2px(15), dp6);
        mSelectedDrawable.setColor(Color.parseColor("#F44336"));
        //复用列表
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        new RecyclerBannerSnapHelper().attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                long first = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                long last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                long center = (first + last) / 2;
                if (currentIndex != center) {
                    currentIndex = (int) center;
                    changePoint();
                }
            }
        });
        addView(mRecyclerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //指示点容器
        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams linearLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLinearLayout.setGravity(Gravity.CENTER);
        mLinearLayout.setPadding(dp6 * 2, dp6 * 2, dp6 * 2, dp6 * 2);
        linearLayoutParams.gravity = Gravity.BOTTOM;
        addView(mLinearLayout, linearLayoutParams);
    }

    //==============================================================================================

    public void setAdapter(RecyclerBannerAdapter adapter) {
        if (adapter == null) {
            throw new RuntimeException("adapter can not null");
        }
        bannerAdapter = adapter;
        bannerAdapter.setListener(new RecyclerBannerAdapter.OnListener() {
            @Override
            public void onDataChanged() {
                notifyUIChanged();
            }
        });
        mRecyclerView.setAdapter(bannerAdapter);
    }

    private void notifyUIChanged() {
        if (bannerAdapter != null) {
            setPlaying(false);
            mLinearLayout.removeAllViews();
            if (bannerAdapter.getData().size() > 0) {
                currentIndex = Integer.MAX_VALUE / 2;
                Log.d(TAG, "notifyUIChanged, currentIndex = " + currentIndex);
                mRecyclerView.scrollToPosition(currentIndex);
                createPoint();
                setPlaying(true);
            } else {
                currentIndex = 0;
            }
        }
    }

    public synchronized void setPlaying(boolean playing) {
        if (bannerAdapter != null && bannerAdapter.getItemCount() > 0 && !mIsPlaying && playing) {
            mHandler.postDelayed(playTask, mIntervalTime);
            mIsPlaying = true;
        } else if (mIsPlaying && !playing) {
            mHandler.removeCallbacksAndMessages(null);
            mIsPlaying = false;
        }
    }

    private void createPoint() {
        if (bannerAdapter != null) {
            for (int i = 0; i < bannerAdapter.getData().size(); i++) {
                ImageView img = new ImageView(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                img.setImageDrawable(i == 0 ? mSelectedDrawable : mDefaultDrawable);
                lp.leftMargin = dp6 / 2;
                lp.rightMargin = dp6 / 2;
                mLinearLayout.addView(img, lp);
            }
        }
    }

    private void changePoint() {
        if (mLinearLayout.getChildCount() > 0 && bannerAdapter != null && bannerAdapter.getData().size() > 0) {
            for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
                ImageView imageView = (ImageView) mLinearLayout.getChildAt(i);
                boolean selectImage = i == currentIndex % bannerAdapter.getData().size();
                imageView.setImageDrawable(selectImage ? mSelectedDrawable : mDefaultDrawable);
            }
        }
    }

    private int dp2px(int dp) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (dp * d + 0.5);
    }

    //==============================================================================================

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                setPlaying(false);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                int disX = moveX - startX;
                int disY = moveY - startY;
                getParent().requestDisallowInterceptTouchEvent(2 * Math.abs(disX) > Math.abs(disY));
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == View.GONE) {
            setPlaying(false);
        } else if (visibility == View.VISIBLE) {
            setPlaying(true);
        }
        super.onWindowVisibilityChanged(visibility);
    }

    private Runnable playTask = new Runnable() {
        @Override
        public void run() {
            currentIndex++;
            mRecyclerView.smoothScrollToPosition(currentIndex);
            changePoint();
            mHandler.postDelayed(this, mIntervalTime);
        }
    };

}