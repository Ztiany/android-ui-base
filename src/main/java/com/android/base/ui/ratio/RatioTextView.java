package com.android.base.ui.ratio;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class RatioTextView extends AppCompatTextView {

    private final RatioHelper mRatioHelper;

    public RatioTextView(Context context) {
        this(context, null);
    }

    public RatioTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRatioHelper = new RatioHelper(this);
        mRatioHelper.resolveAttr(attrs);
    }

    public void setRatio(float ratio) {
        mRatioHelper.setRatio(ratio);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measure = mRatioHelper.measure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(measure[0], measure[1]);
    }

}
