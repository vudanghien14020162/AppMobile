package com.mobitv.ott.other;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import com.mobitv.ott.R;

/**
 * Created by sonth on 10/8/2015.
 */
public class GridItemChannelInsetDecoration extends RecyclerView.ItemDecoration {

    private int insetHorizontal;
    private int insetEdgeHorizontal;
    private int insetVertical;
    private int spanCount;

    public GridItemChannelInsetDecoration(Context context, int spanCount) {
        insetHorizontal = context.getResources()
                .getDimensionPixelSize(R.dimen.item_padding);
        insetVertical = context.getResources()
                .getDimensionPixelSize(R.dimen.item_padding);
        this.spanCount = spanCount;
        insetEdgeHorizontal = context.getResources().getDimensionPixelSize(R.dimen.side_margin_common);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        GridLayoutManager.LayoutParams layoutParams
                = (GridLayoutManager.LayoutParams) view.getLayoutParams();

        int position = layoutParams.getViewAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        // add edge margin only if item edge is not the grid edge
        int itemSpanIndex = layoutParams.getSpanIndex();
        // is left grid edge?
        if (itemSpanIndex == 0) {
            outRect.left = insetEdgeHorizontal;
            outRect.right = 0;
        } else {
            if(itemSpanIndex == spanCount - 1){
                outRect.left = 0;
                outRect.right = insetEdgeHorizontal;
            }else {
                outRect.left = insetEdgeHorizontal / 2;
                outRect.right = insetEdgeHorizontal / 2;
            }
        }
        outRect.top = 0;
        outRect.bottom = insetVertical;
    }
}
