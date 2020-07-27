package com.mobitv.ott.other;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by sonth on 8/23/2015.
 */
public class ScaleAnimation extends Animation {
    private final int startHeight;
    private final int targetHeight;
    private final View view;


    public ScaleAnimation(View view, int startHeight, int targetHeight) {
        this.view = view;
        this.startHeight = startHeight;
        this.targetHeight = targetHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        newHeight = (int) ((targetHeight - startHeight) * interpolatedTime) + startHeight;
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }


    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
