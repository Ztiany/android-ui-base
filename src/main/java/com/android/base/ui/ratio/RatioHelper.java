package com.android.base.ui.ratio;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.android.base.ui.R;

class RatioHelper {

    private float mRatio;

    private final View mView;

    private final int[] mMeasuredDimension = new int[2];

    RatioHelper(View view) {
        mView = view;
    }

    void resolveAttr(AttributeSet attrs) {
        try (TypedArray typedArray = mView.getContext().obtainStyledAttributes(attrs, R.styleable.RatioLayout)) {
            mRatio = typedArray.getFloat(0, 0F);
        }
    }

    void setRatio(float ratio) {
        if (mRatio == ratio) {
            return;
        }
        mRatio = ratio;
        mView.requestLayout();
    }

    int[] measure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mRatio != 0) {
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

            int width = View.MeasureSpec.getSize(widthMeasureSpec) - mView.getPaddingLeft() - mView.getPaddingRight();
            int height = View.MeasureSpec.getSize(heightMeasureSpec) - mView.getPaddingTop() - mView.getPaddingBottom();

            if (widthMode == View.MeasureSpec.EXACTLY && heightMode != View.MeasureSpec.EXACTLY) {
                height = (int) (width / mRatio + 0.5f);
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height + mView.getPaddingTop() + mView.getPaddingBottom(), View.MeasureSpec.EXACTLY);
            } else if (widthMode != View.MeasureSpec.EXACTLY && heightMode == View.MeasureSpec.EXACTLY) {
                width = (int) (height * mRatio + 0.5f);
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width + mView.getPaddingLeft() + mView.getPaddingRight(), View.MeasureSpec.EXACTLY);
            }
        }

        mMeasuredDimension[0] = widthMeasureSpec;
        mMeasuredDimension[1] = heightMeasureSpec;
        return mMeasuredDimension;
    }

}