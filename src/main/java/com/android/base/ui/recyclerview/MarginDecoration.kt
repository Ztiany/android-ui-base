package com.android.base.ui.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State

class MarginDecoration : ItemDecoration {

    private var top: Int
    private var left: Int
    private var right: Int
    private var bottom: Int

    constructor(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        this.top = top
        this.bottom = bottom
        this.right = right
        this.left = left
    }

    constructor(vertical: Int = 0, horizontal: Int = 0) {
        this.top = vertical
        this.bottom = vertical
        this.right = horizontal
        this.left = horizontal
    }

    constructor(margin: Int) : this(margin, margin, margin, margin)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = top
        outRect.bottom = bottom
        outRect.left = left
        outRect.right = right
    }

}