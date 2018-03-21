package org.app.mydukan.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

public class SpacesItemDecoration1 extends RecyclerView.ItemDecoration {
    private int mOrientaion;
    private int space;

    public SpacesItemDecoration1(int space, int orientation) {
        this.space = space;
        setOrientation(orientation);
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid mOrientaion. It should be either HORIZONTAL or VERTICAL");
        }
        this.mOrientaion = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (mOrientaion == VERTICAL) {
            outRect.left = space;
            outRect.right = space;
            if (parent.getChildAdapterPosition(view) == parent.getChildCount() - 1)
                outRect.bottom = space;
        } else {
//            outRect.top = space;
//            outRect.bottom = space;
//            if (parent.getChildAdapterPosition(view) == parent.getChildCount() - 1) {
            outRect.right = space;
            outRect.left = space;
//            }
        }
    }
}