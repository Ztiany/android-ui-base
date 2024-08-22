package com.android.base.ui.text;

import android.widget.EditText;

import androidx.annotation.NonNull;

public interface OnClearTextListener {

    void onTextCleared(@NonNull EditText editText);

}