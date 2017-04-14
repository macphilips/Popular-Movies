package com.rmhub.popularmovies;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by MOROLANI on 4/12/2017
 * <p>
 * owm
 * .
 */

public class CustomImageView extends android.support.v7.widget.AppCompatImageView {

    private float radius;
    private Path clipPath;
    private RectF rect;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomImageView,
                0, 0
        );

        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
            //
            // The R.styleable.PieChart_* constants represent the index for
            // each custom attribute in the R.styleable.PieChart array.
            radius = a.getDimension(R.styleable.CustomImageView_corner_radius, 0f);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        init();
    }

    private void init() {
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //float radius = 36.0f;
        Path clipPath;
         RectF rect;
        clipPath = new Path();
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }

}
