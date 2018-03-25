package com.example.ruslanyussupov.popularmovies.ui;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VideoItemDecoration extends RecyclerView.ItemDecoration {

    private int offset;

    public VideoItemDecoration(int offset) {
        this.offset = offset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, offset, 0);
    }
}
