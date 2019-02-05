package com.kimcy929.iconpakagereader.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * Created by Kimcy929 on 07/11/2016.
 */
class SquareImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

        val width = measuredWidth
        val height = measuredHeight

        // Optimization so we don't measure twice unless we need to
        if (width != height) {
            setMeasuredDimension(width, width)
        }
    }
}
