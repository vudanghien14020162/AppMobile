package com.mobitv.ott.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.mobitv.ott.R;

public class RoundedCornerFrameLayout extends FrameLayout {
    private int cornerRadius;

    public RoundedCornerFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public RoundedCornerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedCornerFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        cornerRadius = (int) context.getResources().getDimension(R.dimen.common_border_radius);
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        int count = canvas.save();

        final Path path = new Path();
        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), (float) cornerRadius, (float) cornerRadius, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.INTERSECT);

        canvas.clipPath(path);
        super.draw(canvas);
        canvas.restoreToCount(count);
    }
}