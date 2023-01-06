package com.android.base.ui.banner;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * @author Ztiany
 */
public abstract class BannerViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Uri> mEntities;
    private OnPageClickListener mClickListener;
    private boolean mIsLooper;
    private String mTransitionName;

    void setContext(Context context) {
        mContext = context;
    }

    void setEntities(List<Uri> entities, boolean isLooper) {
        mEntities = entities;
        mIsLooper = isLooper;
    }

    void setTransitionName(String transitionName) {
        mTransitionName = transitionName;
    }

    void setOnBannerClickListener(OnPageClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    protected final void callPagerClicked(int position, View view) {
        if (mClickListener != null) {
            mClickListener.onClick(view, getFixedPosition(position));
        }
    }

    private int getFixedPosition(int position) {
        if (mIsLooper) {
            if (position == 0) {
                position = mEntities.size() - 3;
            } else if (position == mEntities.size() - 1) {
                position = 0;
            } else {
                position--;
            }
        }
        return position;
    }

    public List<Uri> getEntities() {
        return mEntities;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return getEntities().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public String getTransitionName() {
        return mTransitionName;
    }

}