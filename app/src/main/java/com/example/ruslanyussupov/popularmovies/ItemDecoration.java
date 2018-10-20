package com.example.ruslanyussupov.popularmovies;


import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private final int offsetLeft;
    private final int offsetTop;
    private final int offsetRight;
    private final int offsetBottom;

    public ItemDecoration(int offsetLeft, int offsetTop, int offsetRight, int offsetBottom) {
        this.offsetLeft = offsetLeft;
        this.offsetTop = offsetTop;
        this.offsetRight = offsetRight;
        this.offsetBottom = offsetBottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(offsetLeft, offsetTop, offsetRight, offsetBottom);
    }

}
