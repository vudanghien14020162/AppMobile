package com.mobitv.ott.other;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import com.mobitv.ott.R;

/**
 * Created by sonth on 11/1/2019.
 */
public class GridItemVodInsetDecoration extends RecyclerView.ItemDecoration {

    private int insetHorizontal;
    private int insetVertical;
    private int spanCount;

    public GridItemVodInsetDecoration(Context context, int spanCount) {
        insetHorizontal = context.getResources()
                .getDimensionPixelSize(R.dimen.side_margin_common);
        insetVertical = context.getResources()
                .getDimensionPixelSize(R.dimen.side_margin_common);
        this.spanCount = spanCount;
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
            outRect.left = insetHorizontal;
        } else {
            outRect.left = insetHorizontal / 2;
        }
        outRect.top = 0;
        if(itemSpanIndex == spanCount - 1){
            outRect.right = insetHorizontal;
        }else {
            outRect.right = insetHorizontal / 2;
        }
        outRect.bottom = insetVertical;
    }
}
