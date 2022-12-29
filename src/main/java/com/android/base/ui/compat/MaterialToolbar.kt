package com.android.base.ui.compat

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * This MaterialToolbar doesn't consume touch events.
 *
 *@author Ztiany
 */
class MaterialToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.style.Widget_MaterialComponents_Toolbar
) : com.google.android.material.appbar.MaterialToolbar(context, attrs, defStyleAttr) {

    var noHandleTouchEvent = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (noHandleTouchEvent) {
            return false
        }
        return super.onTouchEvent(ev)
    }

}