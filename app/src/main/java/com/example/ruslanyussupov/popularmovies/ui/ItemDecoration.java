package com.example.ruslanyussupov.popularmovies.ui;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private int offsetLeft;
    private int offsetTop;
    private int offsetRight;
    private int offsetBottom;

    public ItemDecoration(int offsetLeft, int offsetTop, int offsetRight, int offsetBottom) {
        this.offsetLeft = offsetLeft;
        this.offsetTop = offsetTop;
        this.offsetRight = offsetRight;
        this.offsetBottom = offsetBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(offsetLeft, offsetTop, offsetRight, offsetBottom);
    }

}
