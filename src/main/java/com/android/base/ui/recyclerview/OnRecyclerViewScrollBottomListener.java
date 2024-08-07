package com.android.base.ui.recyclerview;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * A listener when RecyclerView scroll to bottom.
 */
public abstract class OnRecyclerViewScrollBottomListener extends RecyclerView.OnScrollListener {

    private int[] mLastPositions;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        int lastVisibleItemPosition;

        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            if (mLastPositions == null) {
                mLastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
            }
            staggeredGridLayoutManager.findLastVisibleItemPositions(mLastPositions);
            lastVisibleItemPosition = findMax(mLastPositions);
        } else {
            throw new IllegalStateException("unsupported layoutManager: " + layoutManager.getClass().getName());
        }

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();

        if ((visibleItemCount > 0 && (lastVisibleItemPosition) >= totalItemCount - 1)) {
            onBottom();
        } else {
            onLeaveBottom();
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    protected abstract void onBottom();

    protected abstract void onLeaveBottom();

}