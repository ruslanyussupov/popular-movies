package com.example.ruslanyussupov.popularmovies


import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class ItemDecoration(private val offsetLeft: Int,
                     private val offsetTop: Int,
                     private val offsetRight: Int,
                     private val offsetBottom: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(offsetLeft, offsetTop, offsetRight, offsetBottom)
    }

}
