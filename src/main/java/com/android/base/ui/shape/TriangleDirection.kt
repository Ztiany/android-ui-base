package com.android.base.ui.shape

import androidx.annotation.IntDef

@IntDef(TriangleDirection.TOP, TriangleDirection.BOTTOM, TriangleDirection.LEFT, TriangleDirection.RIGHT)
@Retention(AnnotationRetention.SOURCE)
annotation class TriangleDirection {
    companion object {
        const val TOP = 1
        const val BOTTOM = 2
        const val LEFT = 3
        const val RIGHT = 4
    }
}