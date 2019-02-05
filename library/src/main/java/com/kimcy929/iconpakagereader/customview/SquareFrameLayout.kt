package com.kimcy929.iconpakagereader.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created by Kimcy929 on 07/11/2016.
 */

class SquareFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }
}
