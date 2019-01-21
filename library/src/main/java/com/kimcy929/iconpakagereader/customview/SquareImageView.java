package com.kimcy929.iconpakagereader.customview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Kimcy929 on 07/11/2016.
 */
public class SquareImageView extends androidx.appcompat.widget.AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // Optimization so we don't measure twice unless we need to
        if (width != height) {
            setMeasuredDimension(width, width);
        }
    }

}
