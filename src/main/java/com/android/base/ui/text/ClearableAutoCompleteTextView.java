package com.android.base.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.base.ui.R;
import com.android.base.ui.Sizes;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.jetbrains.annotations.NotNull;

public class ClearableAutoCompleteTextView extends MaterialAutoCompleteTextView {

    public ClearableAutoCompleteTextView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ClearableAutoCompleteTextView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public ClearableAutoCompleteTextView(@NonNull Context context, @Nullable AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context, attributeSet);
    }

    private Bitmap mClearBitmap;

    private boolean mContentClearableEnable;

    private Paint mBitmapPaint;

    /**
     * the edge offset of first bitmap
     */
    private int mBitmapRightEdgeOffset;

    private int mInitPaddingRight;

    private static final int DOWN_POSITION_NONE = 1;
    private static final int DOWN_POSITION_CLEAR = 2;
    private static final int DOWN_POSITION_PASSWORD = 3;
    private int mDownPosition = DOWN_POSITION_NONE;

    private int extendDrawableTouchingSize = 0;

    private PasswordTransformationMethod mVisibleTransformation;

    private ClearableEditText.OnShowPasswordListener mOnShowPasswordListener;

    private void init(Context context, AttributeSet attrs) {
        parseAttributes(context, attrs);
        mInitPaddingRight = getPaddingRight();
        mBitmapRightEdgeOffset = Sizes.dpToPx(getContext(), 5);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        adjustPadding();
        addTextChangedListener(newWatcher());
        extendDrawableTouchingSize = Sizes.dpToPx(getContext(), 10);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        try (TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearableAutoCompleteTextView)) {
            BitmapDrawable clearDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.ClearableAutoCompleteTextView_cet_clear_drawable);
            if (clearDrawable != null) {
                mClearBitmap = clearDrawable.getBitmap();
            }
            if (mClearBitmap == null) {
                mClearBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.base_ui_icon_clear);
            }
            mContentClearableEnable = typedArray.getBoolean(R.styleable.ClearableAutoCompleteTextView_cet_enable_content_clearable, true);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (processTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        doDraw(canvas);
    }

    private void doDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getMeasuredWidth() - mInitPaddingRight + getScrollX(), 0);

        boolean hasClearBitmap = mContentClearableEnable && !TextUtils.isEmpty(getTextValue());

        if (hasClearBitmap) {
            canvas.translate(-(mClearBitmap.getWidth() + mBitmapRightEdgeOffset), 0);
            canvas.drawBitmap(mClearBitmap, 0, (int) (0.5F + (getMeasuredHeight() - mClearBitmap.getHeight()) / 2.0F), mBitmapPaint);
        }

        canvas.restore();
    }

    public void setOnShowPasswordListener(ClearableEditText.OnShowPasswordListener onShowPasswordListener) {
        mOnShowPasswordListener = onShowPasswordListener;
    }

    private PasswordTransformationMethod getInvisibleTransformation() {
        if (mVisibleTransformation == null) {
            mVisibleTransformation = new PasswordTransformationMethod();
        }
        return mVisibleTransformation;
    }

    private void adjustPadding() {
        boolean hasClearBitmap = mContentClearableEnable && !TextUtils.isEmpty(getTextValue());

        int rightPadding;
        if (hasClearBitmap) {
            rightPadding = mInitPaddingRight + mClearBitmap.getWidth() + mBitmapRightEdgeOffset;
        } else {
            rightPadding = mInitPaddingRight;
        }

        invalidate();
        setPadding(getPaddingLeft(), getPaddingTop(), rightPadding, getPaddingBottom());
    }

    @NotNull
    private TextWatcher newWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adjustPadding();
            }
        };
    }

    private boolean processTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            mDownPosition = detectTouchPosition(event);
        } else if (action == MotionEvent.ACTION_UP) {
            int upPosition = detectTouchPosition(event);
            if (upPosition == mDownPosition) {
                if ((upPosition == DOWN_POSITION_CLEAR)) {
                    setText("");
                } else if (upPosition == DOWN_POSITION_PASSWORD) {
                    if (isPasswordInvisible()) {
                        if (mOnShowPasswordListener != null) {
                            mOnShowPasswordListener.onShowPassword(this);
                        }
                        setTransformationMethod(null);
                    } else {
                        setTransformationMethod(getInvisibleTransformation());
                    }
                    setSelection(getTextValue().length());
                }
            }
        }

        return mDownPosition != DOWN_POSITION_NONE;
    }

    private int detectTouchPosition(MotionEvent event) {
        float eventX = event.getX();
        if (mContentClearableEnable && !TextUtils.isEmpty(getTextValue())) {
            int clearRight = getMeasuredWidth() - mInitPaddingRight - mBitmapRightEdgeOffset;
            int clearLeft = clearRight - mClearBitmap.getWidth();
            if (eventX >= clearLeft - extendDrawableTouchingSize && eventX <= clearRight + extendDrawableTouchingSize) {
                return DOWN_POSITION_CLEAR;
            }
        }
        return DOWN_POSITION_NONE;
    }

    private String getTextValue() {
        Editable text = getText();
        return (text == null) ? "" : text.toString();
    }

    private boolean isInputTypePassword() {
        int inputType = getInputType();
        final int variation = inputType & (EditorInfo.TYPE_MASK_CLASS | EditorInfo.TYPE_MASK_VARIATION);
        final boolean passwordInputType = variation == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        final boolean webPasswordInputType = variation == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD);
        final boolean numberPasswordInputType = variation == (EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
        return passwordInputType || webPasswordInputType || numberPasswordInputType;
    }

    public boolean isPasswordInvisible() {
        return getTransformationMethod() instanceof PasswordTransformationMethod;
    }

    public void setInitRightPadding(int initRightPadding) {
        mInitPaddingRight = initRightPadding;
        adjustPadding();
    }

    public void setContentClearableEnable(boolean contentClearableEnable) {
        mContentClearableEnable = contentClearableEnable;
        invalidate();
    }

    public void hidePassword() {
        setTransformationMethod(getInvisibleTransformation());
        invalidate();
    }

}