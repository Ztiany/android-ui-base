package com.android.base.ui.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.viewpager.widget.ViewPager;

import com.android.base.ui.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 支持无限轮播的 ViewPager。
 *
 * @author Ztiany
 */
public class BannerViewPager extends FrameLayout {

    private final ViewPager mViewPager;

    private IPagerNumberView mPageNumberView;
    private final int mPagerNumberViewId;

    private final List<Uri> mImageUrlList = new ArrayList<>();

    private OnBannerPositionChangedListener mOnBannerPositionChangedListener;

    private final String mTransitionName;

    private OnPageClickListener mOnPageClickListener;

    private boolean mEnableLopper;
    private final boolean mEnableAutoScroll;
    private final int mAutoScrollInterval;

    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerViewPager);

        /*用于支持 5.0 的 transition 动画*/
        mTransitionName = typedArray.getString(R.styleable.BannerViewPager_zvp_item_transition_name);
        mPagerNumberViewId = typedArray.getResourceId(R.styleable.BannerViewPager_zvp_pager_number_id, -1);
        mEnableLopper = typedArray.getBoolean(R.styleable.BannerViewPager_zvp_enable_looper, false);
        mAutoScrollInterval = typedArray.getInt(R.styleable.BannerViewPager_zvp_auto_scroll_interval, 3000);
        mEnableAutoScroll = typedArray.getBoolean(R.styleable.BannerViewPager_zvp_enable_auto_scroll, false) && mAutoScrollInterval > 0;

        typedArray.recycle();

        inflate(context, R.layout.base_ui_widget_banner_view, this);

        mViewPager = getRootView().findViewById(R.id.base_widget_banner_vp);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mPagerNumberViewId != -1) {
            View pageNumber = findViewById(mPagerNumberViewId);
            if (pageNumber instanceof IPagerNumberView) {
                mPageNumberView = (IPagerNumberView) pageNumber;
                mPageNumberView.setBannerView(this);
            }
        }
    }

    public void setOnBannerPositionChangedListener(OnBannerPositionChangedListener onBannerPositionChangedListener) {
        mOnBannerPositionChangedListener = onBannerPositionChangedListener;
    }

    /**
     * 在 {@link #setImages(List, BannerViewPagerAdapter)} 之前调用。
     */
    public void setPageNumberView(IPagerNumberView pageNumberView) {
        mPageNumberView = pageNumberView;
        if (mPageNumberView != null) {
            mPageNumberView.setBannerView(this);
        }
    }

    /**
     * 在 {@link #setImages(List, BannerViewPagerAdapter)} 之前调用。
     */
    public void setEnableLooper(boolean enableLooper) {
        mEnableLopper = enableLooper;
    }

    public void setImages(List<Uri> entities, BannerViewPagerAdapter adapter) {
        if (entities == null || entities.isEmpty()) {
            mImageUrlList.clear();
            mViewPager.setAdapter(null);
            setPageSize(0);
            setUpAutoScrollIfNeeded();
            return;
        }

        mImageUrlList.clear();
        setPageSize(entities.size());

        if (entities.size() > 1 && mEnableLopper) {
            addExtraPage(entities);
            showBanner(adapter);
        } else {
            mImageUrlList.addAll(entities);
            showBanner(adapter);
        }
        setViewPagerListener();

        notifyPageSelected(mViewPager.getCurrentItem());
        notifyOnBannerPositionChanged(mViewPager.getCurrentItem());

        setUpAutoScrollIfNeeded();
    }

    public boolean isEnableLopper() {
        return mEnableLopper && mImageUrlList.size() > 1;
    }

    public int getFixedCurrentPosition() {
        int currentItem = mViewPager.getCurrentItem();
        return getFixedPosition(currentItem);
    }

    public void setCurrentPosition(int position) {
        if (position < 0) {
            return;
        }

        if (!mEnableLopper) {
            mViewPager.setCurrentItem(position);
            return;
        }

        if (mImageUrlList.size() > 1) {
            int realSize = mImageUrlList.size() - 2;
            if (position >= realSize) {
                position = realSize - 1;
            }
            position++;
        }

        mViewPager.setCurrentItem(position);
    }

    private void setViewPagerListener() {
        mViewPager.clearOnPageChangeListeners();
        if (isEnableLopper()) {
            mViewPager.setCurrentItem(1, false);
        }
        mViewPager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        notifyPageScrolled(position, positionOffset);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if (!isEnableLopper()) {
                            return;
                        }
                        //positionOffset 为 0 的时候，并不一定是切换完成，所以动画还在执行，强制再次切换，就会闪屏
                        switch (state) {
                            case ViewPager.SCROLL_STATE_IDLE:// 空闲状态，没有任何滚动正在进行（表明完成滚动）
                                int position = mViewPager.getCurrentItem();
                                if (position == mImageUrlList.size() - 1) {
                                    mViewPager.setCurrentItem(1, false);
                                } else if (position == 0) {
                                    mViewPager.setCurrentItem(mImageUrlList.size() - 2, false);
                                }
                                break;
                            case ViewPager.SCROLL_STATE_DRAGGING:// 正在拖动page状态
                                break;
                            case ViewPager.SCROLL_STATE_SETTLING:// 手指已离开屏幕，自动完成剩余的动画效果
                                break;
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {
                        notifyPageSelected(position);
                        notifyOnBannerPositionChanged(position);
                    }
                });
    }


    private void addExtraPage(List<Uri> entities) {
        mImageUrlList.add(entities.get(entities.size() - 1));
        mImageUrlList.addAll(entities);
        mImageUrlList.add(entities.get(0));
    }

    private void showBanner(BannerViewPagerAdapter adapter) {
        adapter.setContext(getContext());
        adapter.setTransitionName(mTransitionName);
        adapter.setEntities(mImageUrlList, isEnableLopper());
        adapter.setOnBannerClickListener(mOnPageClickListener);
        mViewPager.setAdapter(adapter);
    }

    public void setOnPageClickListener(OnPageClickListener onPageClickListener) {
        mOnPageClickListener = onPageClickListener;
    }

    public interface OnBannerPositionChangedListener {
        void onPagePositionChanged(int position);
    }

    ///////////////////////////////////////////////////////////////////////////
    // OnBannerPositionChangedListener
    ///////////////////////////////////////////////////////////////////////////
    private void notifyOnBannerPositionChanged(int position) {
        if (mOnBannerPositionChangedListener != null) {
            mOnBannerPositionChangedListener.onPagePositionChanged(getFixedPosition(position));
        }
    }

    private int getFixedPosition(int position) {
        if (isEnableLopper()) {
            if (position == 0) {
                position = mImageUrlList.size() - 3;
            } else if (position == mImageUrlList.size() - 1) {
                position = 0;
            } else {
                position--;
            }
        }
        return position;
    }

    ///////////////////////////////////////////////////////////////////////////
    // AutoScroll
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setUpAutoScrollIfNeeded();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getHandler().removeCallbacks(mAutoScrollTask);
    }

    private void setUpAutoScrollIfNeeded() {
        getHandler().removeCallbacks(mAutoScrollTask);
        if (!mEnableAutoScroll || mImageUrlList.size() <= 1) {
            return;
        }
        Timber.d("setUpAutoScrollIfNeeded mAutoScrollInterval = %d", mAutoScrollInterval);
        getHandler().postDelayed(mAutoScrollTask, mAutoScrollInterval);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            setUpAutoScrollIfNeeded();
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            getHandler().removeCallbacks(mAutoScrollTask);
        }
        return super.dispatchTouchEvent(ev);
    }

    private final Runnable mAutoScrollTask = new Runnable() {

        @Override
        public void run() {
            int fixedCurrentPosition = getFixedCurrentPosition();
            int nextPosition = fixedCurrentPosition + 1;
            if (isEnableLopper()) {
                if (nextPosition >= mImageUrlList.size() - 2) {
                    mViewPager.setCurrentItem(nextPosition + 1);
                } else {
                    setCurrentPosition(nextPosition);
                }
            } else {
                if (nextPosition >= mImageUrlList.size()) {
                    nextPosition = 0;
                }
                setCurrentPosition(nextPosition);
            }
            getHandler().postDelayed(this, mAutoScrollInterval);
        }

    };

    ///////////////////////////////////////////////////////////////////////////
    // Pager Number View
    ///////////////////////////////////////////////////////////////////////////
    private void notifyPageScrolled(int position, float positionOffset) {
        if (mPageNumberView != null) {
            mPageNumberView.onPageScrolled(getFixedPosition(position), positionOffset);
        }
    }

    private void setPageSize(int pageSize) {
        if (mPageNumberView != null) {
            mPageNumberView.setPageSize(pageSize);
        }
    }

    private void notifyPageSelected(int position) {
        if (mPageNumberView != null) {
            mPageNumberView.onPageSelected(getFixedPosition(position));
        }
    }

}