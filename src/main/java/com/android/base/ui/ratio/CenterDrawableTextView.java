package com.android.base.ui.ratio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * Use case: For example: when the width of TextView is match, the leftDrawable is on the left, and its position cannot be modified through the Gravity property.
 *
 * @author Ztiany
 */
public class CenterDrawableTextView extends RatioTextView {

    private final Paint.FontMetrics metrics = new Paint.FontMetrics();

    public CenterDrawableTextView(Context context) {
        super(context);
    }

    public CenterDrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] compoundDrawables = getCompoundDrawables();
        setToCenterPadding(compoundDrawables, canvas);
        super.onDraw(canvas);
    }

    private void setToCenterPadding(Drawable[] compoundDrawables, Canvas canvas) {
        if (compoundDrawables == null) {
            return;
        }

        Drawable leftDrawable = compoundDrawables[0];
        Drawable topDrawable = compoundDrawables[1];
        Drawable rightDrawable = compoundDrawables[2];
        Drawable bottomDrawable = compoundDrawables[3];

        if (leftDrawable != null) {
            setLeftDrawableCenter(canvas, leftDrawable);
        } else if (topDrawable != null) {
            setTopDrawableCenter(canvas, topDrawable);
        } else if (rightDrawable != null) {
            setRightDrawableCenter(canvas, rightDrawable);
        } else if (bottomDrawable != null) {
            setBottomDrawableCenter(canvas, bottomDrawable);
        }
    }

    private void setBottomDrawableCenter(Canvas canvas, Drawable downDrawable) {
        setGravity(Gravity.BOTTOM | getGravity());

        int compoundDrawablePadding = getCompoundDrawablePadding();
        TextPaint paint = getPaint();
        paint.getFontMetrics(metrics);
        float contentHeight = (metrics.bottom - metrics.top + compoundDrawablePadding + downDrawable.getIntrinsicHeight());
        int measuredHeight = getMeasuredHeight();
        canvas.translate(0F, -(measuredHeight / 2.0F - contentHeight / 2));
    }

    private void setRightDrawableCenter(Canvas canvas, Drawable rightDrawable) {
        setGravity(Gravity.END | getGravity());

        int compoundDrawablePadding = getCompoundDrawablePadding();
        TextPaint paint = getPaint();
        float measureText = paint.measureText(getText().toString());
        float contentWidth = (measureText + compoundDrawablePadding + rightDrawable.getIntrinsicHeight());
        int measuredWidth = getMeasuredWidth();
        canvas.translate(-(measuredWidth / 2.0F - contentWidth / 2), 0F);
    }

    private void setLeftDrawableCenter(Canvas canvas, Drawable leftDrawable) {
        setGravity(Gravity.START | getGravity());

        int compoundDrawablePadding = getCompoundDrawablePadding();
        TextPaint paint = getPaint();
        float measureText = paint.measureText(getText().toString());
        float contentWidth = (measureText + compoundDrawablePadding + leftDrawable.getIntrinsicHeight());
        int measuredWidth = getMeasuredWidth();
        canvas.translate((measuredWidth / 2.0F - contentWidth / 2), 0F);
    }

    private void setTopDrawableCenter(Canvas canvas, Drawable topDrawable) {
        int compoundDrawablePadding = getCompoundDrawablePadding();
        TextPaint paint = getPaint();
        paint.getFontMetrics(metrics);
        float contentHeight = (metrics.bottom - metrics.top + compoundDrawablePadding + topDrawable.getIntrinsicHeight());
        int measuredHeight = getMeasuredHeight();
        canvas.translate(0F, (measuredHeight / 2.0F - contentHeight / 2));
    }

}