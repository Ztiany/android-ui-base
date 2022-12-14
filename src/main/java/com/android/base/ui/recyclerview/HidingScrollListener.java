package com.android.base.ui.recyclerview;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView 滑动时隐藏 MaterialToolbar。
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        int firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        //show views if first item is first visible position and views are hidden
        if (firstVisibleItem == 0) {
            if (!controlsVisible) {
                onShow();
                controlsVisible = true;
            }
        } else {
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }
        }
        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {    // 显示 向上滑动 || 没有显示 向下滑动
            scrolledDistance += dy;
        }
    }

    public abstract void onHide();

    public abstract void onShow();

}